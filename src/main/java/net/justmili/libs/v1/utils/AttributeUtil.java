package net.justmili.libs.v1.utils;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public class AttributeUtil {
    public static AttributeModifier newModifier(Identifier id, double value, AttributeModifier.Operation operation) {
        return new AttributeModifier(id, value, operation);
    }
    public static AttributeInstance getAttribute(Player player, Holder<Attribute> attribute) {
        return player.getAttribute(attribute);
    }
    public static void addOrUpdate(AttributeInstance instance, AttributeModifier modifier) {
        if (instance == null) return;
        instance.addOrUpdateTransientModifier(modifier);
    }
    public static void addOrReplace(AttributeInstance instance, AttributeModifier modifier) {
        if (instance == null) return;
        instance.addOrReplacePermanentModifier(modifier);
    }
}
