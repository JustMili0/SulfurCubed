package net.justmili.sulfurcubed.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.justmili.libs.v1.utils.client.RenderStateUtil;
import net.justmili.sulfurcubed.client.render.SCAnimations;
import net.justmili.sulfurcubed.config.Config;
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
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @ModifyReturnValue(method = "extractEntity", at = @At("RETURN"))
    private EntityRenderState swapExtractedState(EntityRenderState originalState, Entity entity, float partialTicks) {
        if (!(entity instanceof AbstractClientPlayer player) || !(originalState instanceof AvatarRenderState)) return originalState;
        if (!Config.shouldTransform(player)) return originalState;

        var mainHand = player.getMainHandItem();
        var cubeState = new SulfurCubeRenderState();

        RenderStateUtil.copyTo(originalState, cubeState);
        cubeState.size = 2;
        cubeState.bodyRot = player.getViewYRot(partialTicks);
        cubeState.squish = SCAnimations.getSquish(player, partialTicks);
        cubeState.hasRedOverlay = mainHand.isEmpty() && ((AvatarRenderState) originalState).hasRedOverlay;
        cubeState.entityType = EntityTypes.SULFUR_CUBE;

        // Render held blocks
        // Non-block/non-blockitem items get rendered with SCHeldItem
        var dispatcher = (EntityRenderDispatcher) (Object) this;

        if (mainHand.getItem() instanceof BlockItem) {
            var blockItemState = mainHand.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
            var blockState = blockItemState.apply(Block.byItem(mainHand.getItem()).defaultBlockState());
            if (blockState.is(Blocks.BARRIER)) return cubeState; // Don't try to render Barrier blocks
            dispatcher.blockModelResolver.update(cubeState.containedBlock, blockState, SulfurCubeRenderer.BLOCK_DISPLAY_CONTEXT);
        } else {
            dispatcher.itemModelResolver.updateForLiving(cubeState.headItem, mainHand, ItemDisplayContext.FIXED, player);
        }

        return cubeState;
    }
}