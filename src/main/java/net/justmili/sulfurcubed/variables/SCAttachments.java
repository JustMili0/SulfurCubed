package net.justmili.sulfurcubed.variables;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.justmili.libs.v1.utils.common.FdaUtil;
import net.justmili.sulfurcubed.SulfurCubed;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.SulfurCubeArchetype;
import net.minecraft.world.item.ItemStack;

public class SCAttachments {
    public static final AttachmentType<ItemStack> LAST_KNOWN_STACK = FdaUtil.create(id("last_known_stack"), ItemStack.EMPTY);
    public static final AttachmentType<SulfurCubeArchetype> LAST_ARCHETYPE = FdaUtil.create(id("last_archetype"), null);

    private static Identifier id(String path) {
        return SulfurCubed.asResource(path);
    }
}
