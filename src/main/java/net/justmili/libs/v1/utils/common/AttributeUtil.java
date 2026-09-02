package net.justmili.libs.v1.utils.common;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeUtil {
    public static AttributeModifier newModifier(Identifier id, double value, AttributeModifier.Operation operation) {
        return new AttributeModifier(id, value, operation);
    }
    public static AttributeInstance getAttribute(LivingEntity entity, Holder<Attribute> attribute) {
        return entity.getAttribute(attribute);
    }
    public static double getAttributeValue(LivingEntity entity, Holder<Attribute> attribute) {
        return getAttribute(entity, attribute).getValue();
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
