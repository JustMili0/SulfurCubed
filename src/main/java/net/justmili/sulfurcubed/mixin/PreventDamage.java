package net.justmili.sulfurcubed.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PreventDamage {
    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void cancelDamageOnly(ServerLevel level, DamageSource source, float dmg, CallbackInfo ci) {
        Player self = (Player)(Object)this;
        if (!(self instanceof ServerPlayer player)) return;
        if (player.getInventory().getItem(4).isEmpty()) return;

        if (isImmuneSource(source)) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean isImmuneSource(DamageSource source) {
        return source.is(DamageTypes.FALL)
            || source.is(DamageTypes.FALLING_BLOCK)
            || source.is(DamageTypes.FALLING_ANVIL)
            || source.is(DamageTypes.FALLING_STALACTITE)
            || source.is(DamageTypes.ARROW)
            || source.is(DamageTypes.MOB_ATTACK)
            || source.is(DamageTypes.EXPLOSION)
            || source.is(DamageTypes.PLAYER_ATTACK)
            || source.is(DamageTypes.PLAYER_EXPLOSION)
            || source.is(DamageTypes.CACTUS)
            || source.is(DamageTypes.FREEZE);
    }
}