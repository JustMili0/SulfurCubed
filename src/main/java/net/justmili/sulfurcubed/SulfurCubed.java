package net.justmili.sulfurcubed;

import net.fabricmc.api.ModInitializer;
import net.justmili.libs.v1.config.sync.fabric.SyncConfigCSPNetworking;
import net.justmili.libs.v1.utils.ResourceUtil;
import net.justmili.sulfurcubed.config.Config;
import net.justmili.sulfurcubed.registries.EventRegistry;
import net.minecraft.resources.Identifier;

public class SulfurCubed implements ModInitializer {
    public static final String MODID = "sulfurcubed";
    public static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SulfurCubed.class);

    @Override
    public void onInitialize() {
        SyncConfigCSPNetworking.registerCommon();
        SyncConfigCSPNetworking.registerServer();

        Config.registerServer();
        Config.registerCommon();
        EventRegistry.register();
    }

    public static Identifier asResource(String path) {
        return ResourceUtil.parse(MODID, path);
    }
}
