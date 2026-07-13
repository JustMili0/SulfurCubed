package net.justmili.libs;

import net.justmili.libs.v1.utils.ResourceUtil;
import net.minecraft.resources.Identifier;

public class CoreLibs {
    public static final String MODID = "corelibs";
    public static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(CoreLibs.class);

    /*
     * This is a very stripped-down version of currently (As of July 6th 2026) WIP mod "Millie's Core Libraries" mod.
     * Millie's Core Libraries will bring a proper config UI for the mod when released.
     */

    public static Identifier asResource(String path) {
        return ResourceUtil.parse(MODID, path);
    }
}
