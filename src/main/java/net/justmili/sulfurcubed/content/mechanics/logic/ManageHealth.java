package net.justmili.sulfurcubed.content.mechanics.logic;

import net.justmili.libs.v1.utils.AttributeUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ManageHealth {
    public static void onPlayerTick(ServerPlayer player) {
        var food = player.getFoodData();
        if (food.getFoodLevel() < 16) food.setFoodLevel(16);

        var maxHealth = AttributeUtil.getAttribute(player, Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getBaseValue() != 8.0) {
            maxHealth.setBaseValue(8.0);
        }
    }
}
