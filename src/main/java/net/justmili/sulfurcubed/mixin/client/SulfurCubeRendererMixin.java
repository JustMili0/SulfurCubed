package net.justmili.sulfurcubed.mixin.client;

import net.justmili.sulfurcubed.client.render.SCHeldItem;
import net.minecraft.client.model.monster.slime.SulfurCubeModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.SulfurCubeRenderer;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SulfurCubeRenderer.class)
public abstract class SulfurCubeRendererMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addHeldItemLayer(EntityRendererProvider.Context context, CallbackInfo ci) {
        ((SulfurCubeRenderer) (Object) this).addLayer(new SCHeldItem((RenderLayerParent<SulfurCubeRenderState, SulfurCubeModel>) this));
    }
}