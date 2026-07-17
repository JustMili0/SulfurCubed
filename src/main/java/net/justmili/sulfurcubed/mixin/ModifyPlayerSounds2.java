package net.justmili.sulfurcubed.mixin;

import net.justmili.sulfurcubed.config.Config;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class ModifyPlayerSounds2 {

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
}
