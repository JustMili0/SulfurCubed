package net.justmili.sulfurcubed.mixin;

import net.justmili.sulfurcubed.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class ModifyPlayerSounds1 {

    // TODO: Fix drop and pickup sounds, idk I can't figure it out
    @Inject(method = "onItemPickup", at = @At("HEAD"))
    private void playCubeAbsorb(ItemEntity entity, CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!(self instanceof Player player) || !Config.shouldTransform(player)) return;

        self.makeSound(SoundEvents.SULFUR_CUBE_ABSORB);
    }
    @Inject(method = "drop", at = @At("HEAD"))
    private void playCubeEject(ItemStack itemStack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!(self instanceof Player player) || !thrownFromHand || !Config.shouldTransform(player)) return;

        self.makeSound(SoundEvents.SULFUR_CUBE_EJECT);
    }

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

        boolean hasHandItem = !player.getMainHandItem().isEmpty();
        self.makeSound(hasHandItem? SoundEvents.SULFUR_CUBE_BOUNCE : SoundEvents.SULFUR_CUBE_SQUISH);
    }
}
