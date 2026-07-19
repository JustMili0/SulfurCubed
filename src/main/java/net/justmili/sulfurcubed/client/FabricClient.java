package net.justmili.sulfurcubed.client;

import net.fabricmc.api.ClientModInitializer;
import net.justmili.libs.v1.config.sync.fabric.SyncConfigCSPNetworking;
import net.justmili.sulfurcubed.config.Config;
import net.justmili.sulfurcubed.registries.ClientEventRegistry;

public class FabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Integration for Millie's Core Libraries included in the mod as M'sCL itself is unfinished
        SyncConfigCSPNetworking.registerClient();

        Config.registerClient();
        ClientEventRegistry.register();
    }
}
