package net.justmili.sulfurcubed.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.monster.slime.SulfurCubeModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;

public class SCHeldItem extends RenderLayer<SulfurCubeRenderState, SulfurCubeModel> {

    public SCHeldItem(RenderLayerParent<SulfurCubeRenderState, SulfurCubeModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, SulfurCubeRenderState state, float yRot, float xRot) {
        if (state.headItem.isEmpty()) return;

        int overlayCoords = LivingEntityRenderer.getOverlayCoords(state, 0.0f);

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));
        poseStack.translate(0.0f, 0.0f, 0.0f);
        poseStack.scale(1.95f, 1.95f, 1.95f);
        state.headItem.submit(poseStack, submitNodeCollector, state.lightCoords, overlayCoords, state.outlineColor);
        poseStack.popPose();
    }
}