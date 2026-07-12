package net.justmili.sulfurcubed.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.justmili.sulfurcubed.config.Config;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public class SwapRenderers {

    @ModifyReturnValue(method = "extractEntity", at = @At("RETURN"))
    private EntityRenderState swapExtractedState(EntityRenderState original, Entity entity, float partialTicks) {
        if (!(entity instanceof AbstractClientPlayer player) || !(original instanceof AvatarRenderState)) return original;
        if (!Config.getShouldTransform()) return original;

        SulfurCubeRenderState cubeState = new SulfurCubeRenderState();

        cubeState.x = original.x;
        cubeState.y = original.y;
        cubeState.z = original.z;
        cubeState.size = 2;
        cubeState.bodyRot = player.getViewYRot(partialTicks);
        cubeState.ageInTicks = original.ageInTicks;
        cubeState.boundingBoxWidth = original.boundingBoxWidth;
        cubeState.boundingBoxHeight = original.boundingBoxHeight;
        cubeState.eyeHeight = original.eyeHeight;
        cubeState.distanceToCameraSq = original.distanceToCameraSq;
        cubeState.isInvisible = original.isInvisible;
        cubeState.isDiscrete = original.isDiscrete;
        cubeState.displayFireAnimation = original.displayFireAnimation;
        cubeState.lightCoords = original.lightCoords;
        cubeState.outlineColor = original.outlineColor;

        cubeState.entityType = EntityTypes.SULFUR_CUBE;
        cubeState.isBaby = false;
        cubeState.fuseRemainingTicks = 0.0f;
        cubeState.shadowRadius = 0.25f;

        EntityRenderDispatcher dispatcher = (EntityRenderDispatcher) (Object) this;
        dispatcher.itemModelResolver.updateForLiving(cubeState.headItem, player.getMainHandItem(), ItemDisplayContext.FIXED, player);

        return cubeState;
    }
}