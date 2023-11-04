package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.event.ClickEvent;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class GenerateModList {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.irons_spellbooks.generate_mod_list.failed"));

    public static void register(CommandDispatcher<CommandSource> pDispatcher) {
        if (pDispatcher.getRoot().getChild("modlist") == null) {
            pDispatcher.register(Commands.literal("modlist").requires((p_138819_) -> {
                return p_138819_.hasPermission(2);
            }).executes((commandContext) -> {
                return generateModList(commandContext.getSource());
            }));
        }else{
            IronsSpellbooks.LOGGER.debug("modlist already loaded.. skipping");
        }
    }

    private static int generateModList(CommandSource source) throws CommandSyntaxException {
        StringBuilder sb = new StringBuilder();

        sb.append("mod_id");
        sb.append(",");
        sb.append("mod_name");
        sb.append(",");
        sb.append("mod_version");
        sb.append(",");
        sb.append("mod_file");
        sb.append(",");
        sb.append("mod_url");
        sb.append(",");
        sb.append("display_url");
        sb.append(",");
        sb.append("issue_tracker_url");
        sb.append("\n");

        ModList.get().getMods().forEach(iModInfo -> {
            sb.append(iModInfo.getModId());
            sb.append(",");
            sb.append(iModInfo.getDisplayName());
            sb.append(",");
            sb.append(iModInfo.getVersion());
            sb.append(",");
            sb.append(iModInfo.getOwningFile().getFile().getFileName());
            sb.append(",");
            iModInfo.getModURL().ifPresent(sb::append);
            sb.append(",");
            iModInfo.getConfig().getConfigElement("displayURL").ifPresent(sb::append);
            sb.append(",");
            iModInfo.getOwningFile().getConfig().getConfigElement("issueTrackerURL").ifPresent(sb::append);
            sb.append("\n");
        });

        try {
            File file = new File("modlist.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(sb.toString());
            writer.close();

            ITextComponent component = new StringTextComponent(file.getName()).withStyle(TextFormatting.UNDERLINE).withStyle((style) -> {
                return style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath()));
            });

            source.sendSuccess(new TranslationTextComponent("commands.irons_spellbooks.generate_mod_list.success", component), true);

        } catch (Exception e) {
            IronsSpellbooks.LOGGER.info(e.getMessage());
            throw ERROR_FAILED.create();
        }
        return 1;
    }
}
