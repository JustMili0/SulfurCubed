package net.justmili.sulfurcubed.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.justmili.sulfurcubed.config.Config;
import net.justmili.sulfurcubed.content.mechanics.logic.CopyCubeAnimations;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.SulfurCubeRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public class SwapRenderers {

    @ModifyReturnValue(method = "extractEntity", at = @At("RETURN"))
    private EntityRenderState swapExtractedState(EntityRenderState originalState, Entity entity, float partialTicks) {
        if (!(entity instanceof AbstractClientPlayer player) || !(originalState instanceof AvatarRenderState)) return originalState;
        if (!Config.shouldTransform(player)) return originalState;

        var cubeState = new SulfurCubeRenderState();

        cubeState.size = 2;
        cubeState.bodyRot = player.getViewYRot(partialTicks);
        cubeState.x = originalState.x;
        cubeState.y = originalState.y;
        cubeState.z = originalState.z;

        cubeState.boundingBoxWidth = originalState.boundingBoxWidth;
        cubeState.boundingBoxHeight = originalState.boundingBoxHeight;
        cubeState.eyeHeight = originalState.eyeHeight;

        cubeState.squish = CopyCubeAnimations.getSquish(player, partialTicks);
        cubeState.hasRedOverlay = ((AvatarRenderState) originalState).hasRedOverlay;
        cubeState.deathTime = ((AvatarRenderState) originalState).deathTime;

        cubeState.displayFireAnimation = originalState.displayFireAnimation;
        cubeState.isInvisible = originalState.isInvisible;
        cubeState.isDiscrete = originalState.isDiscrete;
        cubeState.distanceToCameraSq = originalState.distanceToCameraSq;
        cubeState.lightCoords = originalState.lightCoords;
        cubeState.outlineColor = originalState.outlineColor;

        cubeState.entityType = EntityTypes.SULFUR_CUBE;
        cubeState.isBaby = false;
        cubeState.shadowRadius = 0.25f;

        // Render held blocks
        // Non-block/non-blockitem items get rendered with SCHeldItem
        EntityRenderDispatcher dispatcher = (EntityRenderDispatcher) (Object) this;
        ItemStack mainHand = player.getMainHandItem();

        if (mainHand.getItem() instanceof BlockItem) {
            BlockItemStateProperties blockItemState = mainHand.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
            BlockState blockState = blockItemState.apply(Block.byItem(mainHand.getItem()).defaultBlockState());
            dispatcher.blockModelResolver.update(cubeState.containedBlock, blockState, SulfurCubeRenderer.BLOCK_DISPLAY_CONTEXT);
        } else {
            dispatcher.itemModelResolver.updateForLiving(cubeState.headItem, mainHand, ItemDisplayContext.FIXED, player);
        }

        return cubeState;
    }
}