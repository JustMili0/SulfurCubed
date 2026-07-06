package net.justmili.sulfurcubed.content.mechanics.logic;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;

public class PreventDamage {

    public static boolean onDamage(LivingEntity entity, DamageSource source, float amount) {
        if (!(entity instanceof ServerPlayer player)) return true;

        if (player.getInventory().getItem(4).isEmpty()) return true;

        return !(source.is(DamageTypes.FALL)
            || source.is(DamageTypes.FALLING_BLOCK)
            || source.is(DamageTypes.FALLING_ANVIL)
            || source.is(DamageTypes.FALLING_STALACTITE)
            || source.is(DamageTypes.ARROW)
            || source.is(DamageTypes.MOB_ATTACK)
            || source.is(DamageTypes.EXPLOSION)
            || source.is(DamageTypes.PLAYER_ATTACK)
            || source.is(DamageTypes.PLAYER_EXPLOSION)
            || source.is(DamageTypes.CACTUS)
            || source.is(DamageTypes.FREEZE));
    }
}
