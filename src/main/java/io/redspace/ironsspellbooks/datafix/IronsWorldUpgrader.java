package io.redspace.ironsspellbooks.datafix;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.util.ByteHelper;
import io.redspace.ironsspellbooks.util.CodeTimer;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import net.minecraft.util.Util;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ChunkLoader;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.SaveFormat;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IronsWorldUpgrader {
    public static int IRONS_WORLD_DATA_VERSION = 1;
    final int REPORT_PROGRESS_MS = 5000;
    final byte[] INHABITED_TIME_MARKER = new byte[]{0x49, 0x6E, 0x68, 0x61, 0x62, 0x69, 0x74, 0x65, 0x64, 0x54, 0x69, 0x6D, 0x65};
    public static final String REGION_FOLDER = "region";
    public static final String ENTITY_FOLDER = "entities";
    private final SaveFormat.LevelSave levelStorage;
    private final DataFixer dataFixer;
    private int converted;
    private int skipped;
    private int fixes;
    private boolean running;
    private final Object2FloatMap<RegistryKey<World>> progressMap = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap<>(Util.identityStrategy()));
    private static final Pattern REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    private final DimensionSavedDataManager overworldDataStorage;
    private final IronsSpellBooksWorldData ironsSpellBooksWorldData;
    private Set<RegistryKey<World>> levels = null;

    public IronsWorldUpgrader(SaveFormat.LevelSave pLevelStorage, DimensionGeneratorSettings pWorldGenSettings) {
        this.levels = pWorldGenSettings.levels();
        this.levelStorage = pLevelStorage;
        this.dataFixer = new DataFixerBuilder(1).buildUnoptimized();

        var file = this.levelStorage.getDimensionPath(World.OVERWORLD).resolve("data").toFile();

        try {
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }

        this.overworldDataStorage = new DimensionSavedDataManager(file, dataFixer);
        this.ironsSpellBooksWorldData = overworldDataStorage.computeIfAbsent(
                IronsSpellBooksWorldData::load,
                IronsSpellBooksWorldData::new,
                IronsSpellbooks.MODID);
    }

    public boolean worldNeedsUpgrading() {
        return ironsSpellBooksWorldData.getDataVersion() < IRONS_WORLD_DATA_VERSION;
    }

    public void runUpgrade() {
        if (worldNeedsUpgrading()) {
            IronsSpellbooks.LOGGER.info("IronsWorldUpgrader starting upgrade");

//            try {
//                IronsSpellbooks.LOGGER.info("IronsWorldUpgrader Attempting minecraft world backup (this can take long on large worlds)");
//                levelStorage.makeWorldBackup();
//                IronsSpellbooks.LOGGER.info("IronsWorldUpgrader Minecraft world backup complete.");
//            } catch (Exception exception) {
//                IronsSpellbooks.LOGGER.error("IronsWorldUpgrader Level Backup failed: {}", exception.getMessage());
//            }

            IronsSpellbooks.LOGGER.info("IronsWorldUpgrader starting REGION_FOLDER");
            long millis = Util.getMillis();
            doWork(REGION_FOLDER, "block_entities", true);
            millis = Util.getMillis() - millis;
            IronsSpellbooks.LOGGER.info("IronsWorldUpgrader finished REGION_FOLDER after {} ms.  chunks updated:{} chunks skipped:{} tags fixed:{}", millis, this.converted, this.skipped, this.fixes);

            IronsSpellbooks.LOGGER.info("IronsWorldUpgrader starting ENTITY_FOLDER");
            millis = Util.getMillis();
            doWork(ENTITY_FOLDER, null, false);
            millis = Util.getMillis() - millis;
            IronsSpellbooks.LOGGER.info("IronsWorldUpgrader finished ENTITY_FOLDER after {} ms.  chunks updated:{} chunks skipped:{} tags fixed:{}", millis, this.converted, this.skipped, this.fixes);

            IronsSpellbooks.LOGGER.info("IronsWorldUpgrader starting fixDimensionStorage");
            millis = Util.getMillis();
            fixDimensionStorage();
            millis = Util.getMillis() - millis;
            IronsSpellbooks.LOGGER.info("IronsWorldUpgrader finished fixDimensionStorage after {} ms. tags fixed:{} ", millis, this.fixes);

            int previousVersion = ironsSpellBooksWorldData.getDataVersion();
            ironsSpellBooksWorldData.setDataVersion(IRONS_WORLD_DATA_VERSION);
            overworldDataStorage.save();
            IronsSpellbooks.LOGGER.info("IronsWorldUpgrader V{} -> V{} completed", previousVersion, IRONS_WORLD_DATA_VERSION);
        }
    }

    private void fixDimensionStorage() {
        running = true;
        converted = 0;
        skipped = 0;
        fixes = 0;

        levels.stream().map(resourceKey -> {
            return this.levelStorage.getDimensionPath(resourceKey).resolve("data").toFile();
        }).forEach(dir -> {
            var files = dir.listFiles();
            if (files != null) {
                Arrays.stream(files).toList().forEach(file -> {
                    try {
                        var compoundTag = CompressedStreamTools.readCompressed(file);
                        var ironsTraverser = new IronsTagTraverser();
                        ironsTraverser.visit(compoundTag);

                        if (ironsTraverser.changesMade()) {
                            CompressedStreamTools.writeCompressed(compoundTag, file);
                        }

                        fixes += ironsTraverser.totalChanges();
                    } catch (Exception exception) {
                        IronsSpellbooks.LOGGER.error("IronsWorldUpgrader FixDimensionStorage error: {}", exception.getMessage());
                    }
                });
            }
        });
    }

    private boolean preScanChunkUpdateNeeded(ChunkLoader chunkStorage, ChunkPos chunkPos) throws Exception {
        var regionFile = chunkStorage.worker.storage.getRegionFile(chunkPos);
        var dataInputStream = regionFile.getChunkDataInputStream(chunkPos);

        try (dataInputStream) {
            if (dataInputStream == null) {
                return false;
            }

            int markerPos = ByteHelper.indexOf(dataInputStream, INHABITED_TIME_MARKER);

            if (markerPos == -1) {
                return true;
            }

            var inhabitedTime = dataInputStream.readLong();
            return inhabitedTime != 0;

        } catch (Exception ignored) {
        }

        return true;
    }

    private void doWork(String regionFolder, String filterTag, boolean preScan) {
        running = true;
        converted = 0;
        skipped = 0;
        fixes = 0;
        long nextProgressReportMS = System.currentTimeMillis() + REPORT_PROGRESS_MS;
        int totalChunks = 0;

        ImmutableMap.Builder<RegistryKey<World>, ListIterator<ChunkPos>> builder = ImmutableMap.builder();

        for (RegistryKey<World> resourcekey : levels) {
            List<ChunkPos> list = this.getAllChunkPos(resourcekey, regionFolder);
            builder.put(resourcekey, list.listIterator());
            totalChunks += list.size();
        }

        if (totalChunks > 0) {
            ImmutableMap<RegistryKey<World>, ListIterator<ChunkPos>> immutablemap = builder.build();
            ImmutableMap.Builder<RegistryKey<World>, ChunkLoader> builder1 = ImmutableMap.builder();

            for (RegistryKey<World> resourcekey1 : levels) {
                Path path = this.levelStorage.getDimensionPath(resourcekey1);
                builder1.put(resourcekey1, new ChunkLoader(path.resolve(regionFolder), this.dataFixer, true));
            }

            ImmutableMap<RegistryKey<World>, ChunkLoader> immutablemap1 = builder1.build();
            while (this.running) {
                boolean processedItem = false;

                for (RegistryKey<World> resourcekey2 : levels) {
                    ListIterator<ChunkPos> listiterator = immutablemap.get(resourcekey2);
                    ChunkLoader chunkstorage = immutablemap1.get(resourcekey2);
                    if (listiterator.hasNext()) {
                        ChunkPos chunkpos = listiterator.next();
                        boolean updated = false;

                        try {
                            if (!preScan || preScanChunkUpdateNeeded(chunkstorage, chunkpos)) {
                                CompoundNBT chunkDataTag = chunkstorage.read(chunkpos).join().orElse(null);

                                if (chunkDataTag != null && chunkDataTag.getInt("InhabitedTime") != 0) {
                                    ListNBT blockEntitiesTag;

                                    if (filterTag != null) {
                                        blockEntitiesTag = (ListNBT) chunkDataTag.get(filterTag);
                                    } else {
                                        blockEntitiesTag = new ListNBT();
                                        blockEntitiesTag.add(chunkDataTag);
                                    }

                                    var ironsTagTraverser = new IronsTagTraverser();
                                    ironsTagTraverser.visit(blockEntitiesTag);

                                    if (ironsTagTraverser.changesMade()) {
                                        chunkstorage.write(chunkpos, chunkDataTag);
                                        this.fixes = ironsTagTraverser.totalChanges();
                                        updated = true;
                                    }
                                }
                            }
                        } catch (Exception exception) {
                            IronsSpellbooks.LOGGER.error("IronsWorldUpgrader: Error upgrading chunk {}, {}", chunkpos, exception.getMessage());
                        }

                        if (updated) {
                            ++this.converted;
                        } else {
                            ++this.skipped;
                        }

                        if (System.currentTimeMillis() > nextProgressReportMS) {
                            nextProgressReportMS = System.currentTimeMillis() + REPORT_PROGRESS_MS;
                            int chunksProcessed = this.converted + this.skipped;
                            IronsSpellbooks.LOGGER.info("IronsWorldUpgrader {} PROGRESS: {} of {} chunks complete ({}%)", regionFolder, chunksProcessed, totalChunks, String.format("%.2f", (chunksProcessed / (float) totalChunks) * 100));
                        }

                        processedItem = true;
                    }
                }

                if (!processedItem) {
                    this.running = false;
                }
            }

            for (ChunkLoader chunkstorage1 : immutablemap1.values()) {
                try {
                    chunkstorage1.close();
                } catch (IOException ioexception) {
                    IronsSpellbooks.LOGGER.error("IronsWorldUpgrader: Error closing chunk storage: {}", ioexception.getMessage());
                }
            }
        }
    }

    private List<ChunkPos> getAllChunkPos(RegistryKey<World> p_18831_, String folder) {
        File file1 = this.levelStorage.getDimensionPath(p_18831_).toFile();
        File file2 = new File(file1, folder);
        File[] afile = file2.listFiles((p_18822_, p_18823_) -> {
            return p_18823_.endsWith(".mca");
        });
        if (afile == null) {
            return ImmutableList.of();
        } else {
            List<ChunkPos> list = Lists.newArrayList();

            for (File file3 : afile) {
                Matcher matcher = REGEX.matcher(file3.getName());
                if (matcher.matches()) {
                    int i = Integer.parseInt(matcher.group(1)) << 5;
                    int j = Integer.parseInt(matcher.group(2)) << 5;

                    try {
                        RegionFile regionfile = new RegionFile(file3.toPath(), file2.toPath(), true);

                        try {
                            for (int k = 0; k < 32; ++k) {
                                for (int l = 0; l < 32; ++l) {
                                    ChunkPos chunkpos = new ChunkPos(k + i, l + j);
                                    if (regionfile.doesChunkExist(chunkpos)) {
                                        list.add(chunkpos);
                                    }
                                }
                            }
                        } catch (Throwable throwable1) {
                            try {
                                regionfile.close();
                            } catch (Throwable throwable) {
                                throwable1.addSuppressed(throwable);
                            }

                            throw throwable1;
                        }

                        regionfile.close();
                    } catch (Throwable throwable2) {
                    }
                }
            }

            return list;
        }
    }
}