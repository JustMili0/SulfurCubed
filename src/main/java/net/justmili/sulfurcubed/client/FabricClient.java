package net.justmili.sulfurcubed.client;

import net.fabricmc.api.ClientModInitializer;
import net.justmili.sulfurcubed.registries.EventRegistry;
import net.minecraft.world.entity.player.Player;

import static net.justmili.libs.v1.utils.ClientUtil.minecraft;

public class FabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EventRegistry.registerClient();
    }

    public static boolean nonSurvivalGamemode() {
        if (minecraft.gameMode == null) return false;
        return !(minecraft.gameMode.canHurtPlayer() && minecraft.getCameraEntity() instanceof Player);
    }
}
