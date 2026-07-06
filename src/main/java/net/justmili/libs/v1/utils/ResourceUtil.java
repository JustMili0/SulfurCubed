package net.justmili.libs.v1.utils;


import net.minecraft.resources.Identifier;

public class ResourceUtil {
    public static Identifier parse(String modId, String path) {
        return Identifier.fromNamespaceAndPath(modId, path);
    }
    public static Identifier asMinecraft(String path) {
        return parse("minecraft", path);
    }
}