package net.justmili.sulfurcubed.content.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SulfurCubeArchetype;
import net.minecraft.world.entity.SulfurCubeArchetypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class PlayerCubeUtil {
    public static final EntityDimensions HITBOX = EntityTypes.SULFUR_CUBE.getDimensions();
    public static final float HITBOX_WIDTH = HITBOX.width() * 2;
    public static final float HITBOX_HEIGHT = HITBOX.height() *2;

    public static List<SulfurCubeArchetype.AttributeEntry> attributeModifiers(Player player) {
        return lookupForHeld(player).attributeModifiers();
    }
    public static SulfurCubeArchetype.KnockbackModifiers knockbackModifiers(Player player) {
        return lookupForHeld(player).knockbackModifiers();
    }
    public static double getAttributeValue(Player player, Holder<Attribute> attribute, double elseReturn) {
        for (var entry : attributeModifiers(player)) {
            if (entry.attribute().equals(attribute)) {
                return entry.modifier().amount();
            }
        }
        return elseReturn;
    }
    public static double getSpeed(Player player) {
        return getAttributeValue(player, Attributes.KNOCKBACK_RESISTANCE, 0);
    }
    public static double getBounce(Player player) {
        return getAttributeValue(player, Attributes.BOUNCINESS, 0);
    }
    public static double getFriction(Player player) {
        return getAttributeValue(player, Attributes.FRICTION_MODIFIER, 1);
    }
    public static double getAirDrag(Player player) {
        return getAttributeValue(player, Attributes.AIR_DRAG_MODIFIER, 1);
    }

    public static SulfurCubeArchetype.SoundSettings soundSettings(Player player) {
        return lookupForHeld(player).soundSettings();
    }

    public static SulfurCubeArchetype lookupForHeld(Player player) {
        var registry = player.level().registryAccess();
        return registry.lookupOrThrow(Registries.SULFUR_CUBE_ARCHETYPE).stream()
            .filter(arch -> player.getMainHandItem().is(arch.items()))
            .findFirst().orElse(registry.getOrThrow(SulfurCubeArchetypes.REGULAR).value());
    }
    public static boolean hasHandItem(Player player) {
        return !player.getInventory().getItem(4).isEmpty();
    }
}
