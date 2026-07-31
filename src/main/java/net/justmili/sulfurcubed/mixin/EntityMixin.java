package net.justmili.sulfurcubed.mixin;

import net.justmili.sulfurcubed.config.Config;
import net.justmili.sulfurcubed.content.util.PlayerCubeUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    // Overrides direction restrictions etc.
    @Inject(method = "omnidirectionalAirMover", at = @At("HEAD"), cancellable = true)
    private void omnidirectionalAirMover(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity)(Object)this;
        if (!(self instanceof Player player)) return;

        if (Config.shouldTransform(player) && PlayerCubeUtil.hasHandItem(player)) cir.setReturnValue(true);
    }
}
