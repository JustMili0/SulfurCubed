package net.justmili.sulfurcubed.mixin;

import net.justmili.sulfurcubed.config.Config;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PreventDamage {

    // Prevent damage but allow getting knockback
    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void cancelDamageOnly(ServerLevel level, DamageSource source, float dmg, CallbackInfo ci) {
        Player player = (Player)(Object)this;
        if (player.getInventory().getItem(4).isEmpty()) return;

        if (isImmuneSource(source, player)) ci.cancel();
    }

    @Unique
    private static boolean isImmuneSource(DamageSource source, Player player) {
        return source.is(DamageTypeTags.SULFUR_CUBE_WITH_BLOCK_IMMUNE_TO)
            || (Config.shouldTransform(player) && source.is(DamageTypes.IN_WALL));
        // Prevent suffocation if Sulfur Cube Player hitbox is in a block to prevent accidental deaths
    }
}