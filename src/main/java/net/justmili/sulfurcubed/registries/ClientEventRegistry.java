package net.justmili.sulfurcubed.registries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.justmili.libs.v1.utils.ClientUtil;
import net.justmili.sulfurcubed.client.render.SCHeldItem;
import net.justmili.sulfurcubed.content.mechanics.logic.CopyCubeAnimations;
import net.justmili.sulfurcubed.content.mechanics.logic.ManageInventory;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.SulfurCubeRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import net.minecraft.world.level.Level;

@Environment(EnvType.CLIENT)
public class ClientEventRegistry {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ManageInventory.onClientTick(client);

            Level level = ClientUtil.getLevel();
            if (level == null) return;
            for (var player : level.players()) {
                if (player instanceof AbstractClientPlayer clientPlayer) {
                    CopyCubeAnimations.get(clientPlayer).tick(clientPlayer);
                }
            }
        });

        LivingEntityRenderLayerRegistrationCallback.EVENT.register((_, renderer, helper, _) -> {
            if (renderer instanceof SulfurCubeRenderer cubeRenderer) {
                //noinspection RedundantCast,unchecked
                helper.register((RenderLayer<SulfurCubeRenderState, ? extends EntityModel<SulfurCubeRenderState>>) (Object) new SCHeldItem(cubeRenderer));
            }
        });
    }
}
