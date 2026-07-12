package net.justmili.sulfurcubed.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.SulfurCubeInnerLayer;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SulfurCubeInnerLayer.class)
public class SulfurCubeInnerLayerMixin {

    @Inject(method = "submit*", at = @At("HEAD"), cancellable = true)
    private void hideWhenHoldingItem(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords,
                                                 SulfurCubeRenderState state, float yRot, float xRot, CallbackInfo ci) {
        if (!state.headItem.isEmpty()) ci.cancel();
    }
}