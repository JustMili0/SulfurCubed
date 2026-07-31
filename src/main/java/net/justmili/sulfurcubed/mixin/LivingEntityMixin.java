package net.justmili.sulfurcubed.mixin;

import net.justmili.sulfurcubed.config.Config;
import net.justmili.sulfurcubed.content.util.PlayerCubeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    // Players override LivingEntity#playSound to play the sound to everyone but themselves, so the client doesn't end up hearing
    // the same sound twice. onItemPickup and drop only run on the server, so we need to make sure we can hear it too.
    @Unique
    private void sulfurcubed$makeSound(SoundEvent event) {
        // just to be safe.
        if (this.level().isClientSide())
            return;

        this.level().playSound(null, this, event, this.getSoundSource(), 1f, 1f);
    }

    @Inject(method = "onItemPickup", at = @At("HEAD"))
    private void playCubeAbsorb(ItemEntity entity, CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!(self instanceof Player player) || !Config.shouldTransform(player)) return;

        this.sulfurcubed$makeSound(SoundEvents.SULFUR_CUBE_ABSORB);
    }
    @Inject(method = "drop", at = @At("HEAD"))
    private void playCubeEject(ItemStack itemStack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!(self instanceof Player player) || !thrownFromHand || !Config.shouldTransform(player)) return;

        this.sulfurcubed$makeSound(SoundEvents.SULFUR_CUBE_EJECT);
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

        self.makeSound(PlayerCubeUtil.hasHandItem(player)? SoundEvents.SULFUR_CUBE_BOUNCE : SoundEvents.SULFUR_CUBE_SQUISH);
    }
}
