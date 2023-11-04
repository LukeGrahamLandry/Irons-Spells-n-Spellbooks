package io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob;

public class DefaultBipedBoneIdents {
    // From newer geckolib.
    // Has conventional names for more bones, which the models use, than the 1.16.5 version.
    // https://github.com/bernie-g/geckolib/blob/b57e6d4d30cb706644cfef850bcd9d28fd41cc82/Forge/src/main/java/software/bernie/example/client/DefaultBipedBoneIdents.java
    public static final String LEFT_HAND_BONE_IDENT = "bipedHandLeft";
    public static final String RIGHT_HAND_BONE_IDENT = "bipedHandRight";
    public static final String LEFT_FOOT_ARMOR_BONE_IDENT = "armorBipedLeftFoot";
    public static final String RIGHT_FOOT_ARMOR_BONE_IDENT = "armorBipedRightFoot";
    public static final String LEFT_FOOT_ARMOR_BONE_2_IDENT = "armorBipedLeftFoot2";
    public static final String RIGHT_FOOT_ARMOR_BONE_2_IDENT = "armorBipedRightFoot2";
    public static final String LEFT_LEG_ARMOR_BONE_IDENT = "armorBipedLeftLeg";
    public static final String RIGHT_LEG_ARMOR_BONE_IDENT = "armorBipedRightLeg";
    public static final String LEFT_LEG_ARMOR_BONE_2_IDENT = "armorBipedLeftLeg2";
    public static final String RIGHT_LEG_ARMOR_BONE_2_IDENT = "armorBipedRightLeg2";
    public static final String BODY_ARMOR_BONE_IDENT = "armorBipedBody";
    public static final String RIGHT_ARM_ARMOR_BONE_IDENT = "armorBipedRightArm";
    public static final String LEFT_ARM_ARMOR_BONE_IDENT = "armorBipedLeftArm";
    public static final String HEAD_ARMOR_BONE_IDENT = "armorBipedHead";

    
    // From vanilla's net.minecraft.client.model.geom.PartNames
    public static class PartNames {
        public static final String HEAD = "head";
        public static final String BODY = "body";
        public static final String LEFT_LEG = "left_leg";
        public static final String RIGHT_LEG = "right_leg";
        public static final String LEFT_ARM = "left_arm";
        public static final String RIGHT_ARM = "right_arm";
    }
}
