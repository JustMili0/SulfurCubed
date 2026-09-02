package net.justmili.sulfurcubed.mixin;

import net.justmili.sulfurcubed.config.Config;
import net.justmili.sulfurcubed.util.PlayerCubeUtil;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Avatar.class)
public class AvatarMixin {
    @Inject(method = "getDefaultDimensions", at = @At("RETURN"), cancellable = true)
    private void modifyHitbox(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        Avatar self = (Avatar) (Object) this;
        if (!(self instanceof Player player)) return;

        if (Config.shouldTransform(player)) cir.setReturnValue(EntityDimensions.fixed( // fixed so scale attribute doesn't change the hitbox
            PlayerCubeUtil.HITBOX_WIDTH, PlayerCubeUtil.HITBOX_HEIGHT));
    }
}