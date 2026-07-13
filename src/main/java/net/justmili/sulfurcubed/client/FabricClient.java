package net.justmili.sulfurcubed.client;

import net.fabricmc.api.ClientModInitializer;
import net.justmili.libs.v1.config.sync.fabric.SyncConfigCSPNetworking;
import net.justmili.sulfurcubed.registries.ClientEventRegistry;

public class FabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SyncConfigCSPNetworking.registerClient();

        ClientEventRegistry.register();
    }
}
