package net.justmili.sulfurcubed.content.mechanics.logic;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ManageHealth {
    public static void onPlayerTick(ServerPlayer player) {
        player.getFoodData().setFoodLevel(16);

        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getBaseValue() != 8.0) {
            maxHealth.setBaseValue(8.0);
        }
    }
}
