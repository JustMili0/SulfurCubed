package net.justmili.sulfurcubed.mixin;

import net.justmili.sulfurcubed.config.Config;
import net.justmili.sulfurcubed.content.mechanics.logic.LockSlots;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    // Replace sounds
    @Inject(method = "getHurtSound", at = @At("RETURN"), cancellable = true)
    private void playCubeHurt(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        Player self = (Player)(Object)this;
        if (!Config.shouldTransform(self)) return;

        cir.setReturnValue(SoundEvents.SULFUR_CUBE_HURT);
    }
    @Inject(method = "getDeathSound", at = @At("RETURN"), cancellable = true)
    private void playCubeDeath(CallbackInfoReturnable<SoundEvent> cir) {
        Player self = (Player)(Object)this;
        if (!Config.shouldTransform(self)) return;

        cir.setReturnValue(SoundEvents.SULFUR_CUBE_DEATH);
    }

    // Prevent dropping the barrier lock
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;",
        at = @At("HEAD"), cancellable = true)
    private void preventLockedDrop(ItemStack stack, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir) {
        if (LockSlots.isSlotLocked(stack, false)) cir.setReturnValue(null);
    }

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
