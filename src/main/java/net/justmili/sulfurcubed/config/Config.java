package net.justmili.sulfurcubed.config;

import net.justmili.libs.v1.config.ConfigType;
import net.justmili.libs.v1.config.FileType;
import net.justmili.libs.v1.config.MConfigBuilder;
import net.justmili.libs.v1.config.entry.ConfigEntry;
import net.justmili.sulfurcubed.SulfurCubed;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class Config {
    private static MConfigBuilder newConfig(ConfigType configType) {
        return new MConfigBuilder(SulfurCubed.MODID, configType, FileType.PROPERTIES, true);
    }
    public static MConfigBuilder server = newConfig(ConfigType.SERVER), common = newConfig(ConfigType.COMMON_SERVER_PRIORITY), client = newConfig(ConfigType.CLIENT);

    public static ConfigEntry<Boolean> disableOffhand, disableArmor, sulfurBall;
    public static ConfigEntry<Double> modifyAttributeIntensity;
    public static ConfigEntry<String> sulfurCubePlayer, hideHand;

    public static void registerServer() {

        disableOffhand = server.comment("Should offhand be disabled for the challenge?")
            .define("disableOffhand", false);
        disableArmor = server.comment("Should armor be disabled for the challenge?")
            .define("disableArmor", false);

        modifyAttributeIntensity = server.comment("Additional multiplier of each applied attribute modifier value")
            .define("modifyAttributeIntensity", 0.0, 0.0, 255.0);

        server.build();
    }

    public static void registerCommon() {

        sulfurCubePlayer = common.comment("""
                Should the mod and when turn the player visually into a Sulfur Cube?
                Applies to player rendering and player hitbox.
                Allowed values: ALWAYS, NEVER, SNEAKING""")
            .define("sulfurCubePlayer", "SNEAKING");

        common.build();
    }

    public static void registerClient() {
        hideHand = client.comment("""
                When should the mod hide player hand in first-person?
                Only applies when the player is rendered as a Sulfur Cube (sulfurCubePlayer).
                Allowed values: ALWAYS, NEVER, HAND_EMPTY""")
            .define("hideHand", "HAND_EMPTY");

        client.build();
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
    public static boolean shouldTransform(Player player) {
        if (player == null) return false;

        return switch (sulfurCubePlayer.get()) {
            case "ALWAYS" -> true;
            case "SNEAKING" -> player.isShiftKeyDown();
            default -> false; // "NEVER" and anything unrecognized
        };
    }
    public static boolean shouldHideHand(Player player) {
        if (player == null) return false;
        if (!shouldTransform(player)) return false;

        return switch (hideHand.get()) {
            case "ALWAYS" -> true;
            case "HAND_EMPTY" -> player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
            default -> false; // "NEVER" and anything unrecognized
        };
    }
}