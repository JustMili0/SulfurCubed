package net.justmili.sulfurcubed.config;

import net.justmili.libs.v1.config.MConfigBuilder;
import net.justmili.libs.v1.config.entry.ConfigEntry;
import net.justmili.sulfurcubed.SulfurCubed;

public class Config {
    public static MConfigBuilder builder = new MConfigBuilder(SulfurCubed.MODID, null, false);

    public static ConfigEntry<Boolean> disableOffhand, disableArmor;
    public static ConfigEntry<Double> addModifierMultiplier;

    public static void register() {

        disableOffhand = builder.comment("Should offhand be disabled for the challenge?")
            .define("disableOffhand", false);
        disableArmor = builder.comment("Should armor be disabled for the challenge?")
            .define("disableArmor", false);

        addModifierMultiplier = builder.comment("Additional multiplier of each applied attribute modifier value")
            .define("addModifierMultiplier", 1.0, 0.1, Double.MAX_VALUE);

        builder.build();
    }
}