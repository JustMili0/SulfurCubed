package net.justmili.sulfurcubed.config;

import net.justmili.libs.v1.config.MConfigBuilder;
import net.justmili.libs.v1.config.entry.ConfigEntry;
import net.justmili.libs.v1.utils.ClientUtil;
import net.justmili.sulfurcubed.SulfurCubed;
import net.minecraft.world.entity.player.Player;

public class Config {
    public static MConfigBuilder builder = new MConfigBuilder(SulfurCubed.MODID, null, false);

    public static ConfigEntry<Boolean> disableOffhand, disableArmor;
    public static ConfigEntry<Double> modifyAttributeIntensity;
    public static ConfigEntry<String> sulfurCubePlayer;

    public static void register() {

        sulfurCubePlayer = builder.comment("Should the mod and when turn the player visually into a Sulfur Cube?\n" +
                "Allowed values: ALWAYS, NEVER, SNEAKING")
            .define("sulfurCubePlayer", "SNEAKING");

        disableOffhand = builder.comment("Should offhand be disabled for the challenge?")
            .define("disableOffhand", false);
        disableArmor = builder.comment("Should armor be disabled for the challenge?")
            .define("disableArmor", false);

        modifyAttributeIntensity = builder.comment("Additional multiplier of each applied attribute modifier value")
            .define("modifyAttributeIntensity", 0.0, 0.0, 255.0);

        builder.build();
    }

    public static boolean getDisableOffhand() {
        return disableOffhand.get();
    }
    public static boolean getDisableArmor() {
        return disableArmor.get();
    }
    public static double getModMultiplier() {
        return modifyAttributeIntensity.get();
    }
    public static boolean shouldTransform() {
        Player player = ClientUtil.getPlayer();
        if (player == null) return false;

        return switch (Config.sulfurCubePlayer.get()) {
            case "ALWAYS" -> true;
            case "SNEAKING" -> player.isShiftKeyDown();
            default -> false; // "NEVER" and anything unrecognized
        };
    }
}