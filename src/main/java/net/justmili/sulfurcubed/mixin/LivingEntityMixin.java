package net.justmili.sulfurcubed.mixin;

import net.justmili.sulfurcubed.config.Config;
import net.justmili.sulfurcubed.content.mechanics.logic.ManageVelocity;
import net.justmili.sulfurcubed.util.PlayerCubeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    // Play pickup and drop sounds
    @Inject(method = "onItemPickup", at = @At("HEAD"))
    private void playCubeAbsorb(ItemEntity entity, CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!(self instanceof Player player) || !Config.shouldTransform(player)) return;

        PlayerCubeUtil.makeSound(player, SoundEvents.SULFUR_CUBE_ABSORB);
    }
    @Inject(method = "drop", at = @At("HEAD"))
    private void playCubeEject(ItemStack itemStack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!(self instanceof Player player) || !thrownFromHand || !Config.shouldTransform(player)) return;

        PlayerCubeUtil.makeSound(player, SoundEvents.SULFUR_CUBE_EJECT);
    }

    // Play jump and land sounds
    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    private void playCubeJump(CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!(self instanceof Player player) || !Config.shouldTransform(player)) return;

        self.makeSound(SoundEvents.SULFUR_CUBE_JUMP);
    }
    @Inject(method = "checkFallDamage", at = @At("HEAD"))
    private void playCubeLand(double ya, boolean onGround, BlockState onState, BlockPos pos, CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!(self instanceof Player player) || !Config.shouldTransform(player)) return;
        if (!onGround || !(self.fallDistance > 0.0)) return;

        self.makeSound(PlayerCubeUtil.hasHandItem(player)? SoundEvents.SULFUR_CUBE_BOUNCE : SoundEvents.SULFUR_CUBE_SQUISH);
    }

    // Handle knockback velocity stuff
    @Inject(method = "knockback(DDDLnet/minecraft/world/damagesource/DamageSource;FZ)V", at = @At("HEAD"), cancellable = true)
    private void handleSulfurCubeKnockback(double power, double xd, double zd, DamageSource source, float damage, boolean comesFromEffect, CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!(self instanceof Player player)) return;
        
        if (ManageVelocity.knockback(player, power, xd, zd, source, damage, comesFromEffect)) ci.cancel();
    }

    // Make player float in water
    @Inject(method = "travelInFluid", at = @At(value = "TAIL"))
    private void makeBuoyant(Vec3 input, CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!(self instanceof Player player)) return;

        if (PlayerCubeUtil.hasHandItem(player) && PlayerCubeUtil.isBuoyant(player)) {
            float vibeAmount = 0.2F * Mth.sin(player.tickCount * 0.4F);
            double immersion = player.getFluidHeight(player.isInWater() ? FluidTags.WATER : FluidTags.LAVA) - (player.getBbHeight() * 0.2) + vibeAmount;
            if (immersion > 0.0) {
                player.setDeltaMovement(player.getDeltaMovement().add(0.0, Math.min(1.0, immersion) * 0.04F, 0.0));
            }
        }
    }
}
