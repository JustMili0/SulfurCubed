package net.justmili.sulfurcubed.mixin;

import net.justmili.libs.v1.utils.ClientUtil;
import net.justmili.sulfurcubed.config.Config;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Avatar.class)
public abstract class ModifyPlayerHitbox {
    @Inject(method = "getDefaultDimensions", at = @At("RETURN"), cancellable = true)
    private void modifyHitbox(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        Player player = ClientUtil.getPlayer();
        if (player == null) return;

        if (Config.getShouldTransform()) {
            EntityDimensions dimensions = EntityTypes.SULFUR_CUBE.getDimensions();
            float width = dimensions.width() * 2,  height = dimensions.height() * 2;

            cir.setReturnValue(EntityDimensions.scalable(width, height));
        }
    }
}