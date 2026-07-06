package net.justmili.sulfurcubed.content.mechanics.logic;

import net.justmili.sulfurcubed.SulfurCubed;
import net.justmili.sulfurcubed.config.Config;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SulfurCubeArchetype;
import net.minecraft.world.entity.SulfurCubeArchetypes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public class ManageAttributes {
    public static void onPlayerTick(ServerPlayer player) {
        ItemStack held = player.getMainHandItem();

        double speed, bounce, friction, drag;
        SulfurCubeArchetype archetype;

        ResourceKey<SulfurCubeArchetype> archetypeKey = switch (held) {
            case ItemStack _ when held.is(ItemTags.SULFUR_CUBE_ARCHETYPE_REGULAR) -> SulfurCubeArchetypes.REGULAR;
            case ItemStack _ when held.is(ItemTags.SULFUR_CUBE_ARCHETYPE_BOUNCY) -> SulfurCubeArchetypes.BOUNCY;
            case ItemStack _ when held.is(ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_BOUNCY) -> SulfurCubeArchetypes.SLOW_BOUNCY;
            case ItemStack _ when held.is(ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_FLAT) -> SulfurCubeArchetypes.SLOW_FLAT;
            case ItemStack _ when held.is(ItemTags.SULFUR_CUBE_ARCHETYPE_FAST_FLAT) -> SulfurCubeArchetypes.FAST_FLAT;
            case ItemStack _ when held.is(ItemTags.SULFUR_CUBE_ARCHETYPE_LIGHT) -> SulfurCubeArchetypes.LIGHT;
            case ItemStack _ when held.is(ItemTags.SULFUR_CUBE_ARCHETYPE_FAST_SLIDING) -> SulfurCubeArchetypes.FAST_SLIDING;
            case ItemStack _ when held.is(ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_SLIDING) -> SulfurCubeArchetypes.SLOW_SLIDING;
            default -> SulfurCubeArchetypes.REGULAR; // Default for anything else
        };

        try {
            archetype = player.level().registryAccess().lookupOrThrow(Registries.SULFUR_CUBE_ARCHETYPE).getOrThrow(archetypeKey).value();
        } catch (Exception e) {
            SulfurCubed.LOGGER.error("Could not lookup Sulfur Cube Archetype.", e);
            return;
        }

        speed = getSpeed(archetype);
        bounce = getBounce(archetype);
        friction = getFriction(archetype);
        drag = getDrag(archetype);

        applyModifiers(player, speed, bounce, friction, drag);
    }

    private static void applyModifiers(ServerPlayer player, double speed, double bounce, double friction, double drag) {
        AttributeModifier
            modSpeed = createModifier("speed", speed, AttributeModifier.Operation.ADD_VALUE),
            modBounce = createModifier("bounciness", bounce, AttributeModifier.Operation.ADD_VALUE),
            modFriction = createModifier("friction", friction, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
            modAirDrag = createModifier("air_drag", drag, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
            modScale = createModifier("scale", -0.4556, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
            modCamera = createModifier("camera_distance", 2, AttributeModifier.Operation.ADD_VALUE);

        AttributeInstance
            instSpeed = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE),
            instBounce = player.getAttribute(Attributes.BOUNCINESS),
            instFriction = player.getAttribute(Attributes.FRICTION_MODIFIER),
            instAirDrag = player.getAttribute(Attributes.AIR_DRAG_MODIFIER),
            instScale = player.getAttribute(Attributes.SCALE),
            instCamera = player.getAttribute(Attributes.CAMERA_DISTANCE);
        if (instSpeed == null || instBounce == null
            || instFriction == null || instAirDrag == null
            || instScale == null || instCamera == null) return;

        instSpeed.addOrUpdateTransientModifier(modSpeed);
        instBounce.addOrUpdateTransientModifier(modBounce);
        instFriction.addOrUpdateTransientModifier(modFriction);
        instAirDrag.addOrUpdateTransientModifier(modAirDrag);
        instScale.addOrUpdateTransientModifier(modScale);
        instCamera.addOrUpdateTransientModifier(modCamera);

        if (player.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() || player.isCreative() || player.isSpectator()) {
            instSpeed.removeModifier(modSpeed);
            instBounce.removeModifier(modBounce);
            instFriction.removeModifier(modFriction);
            instAirDrag.removeModifier(modAirDrag);
            // Don't remove scale and camera
        }
    }
    private static AttributeModifier createModifier(String id, double value, AttributeModifier.Operation operation) {
        return new AttributeModifier(SulfurCubed.asResource(id), value, operation);
    }

    private static double getSpeed(SulfurCubeArchetype archetype) {
        for (var entry : archetype.attributeModifiers()) {
            if (entry.attribute().equals(Attributes.KNOCKBACK_RESISTANCE))
                return entry.modifier().amount() * Config.addModifierMultiplier.get();
        }
        return 0;
    }
    private static double getBounce(SulfurCubeArchetype archetype) {
        for (var entry : archetype.attributeModifiers()) {
            if (entry.attribute().equals(Attributes.BOUNCINESS))
                return entry.modifier().amount() * Config.addModifierMultiplier.get();
        }
        return 0;
    }
    private static double getFriction(SulfurCubeArchetype archetype) {
        for (var entry : archetype.attributeModifiers()) {
            if (entry.attribute().equals(Attributes.FRICTION_MODIFIER))
                return entry.modifier().amount() * Config.addModifierMultiplier.get();
        }
        return 1;
    }
    private static double getDrag(SulfurCubeArchetype archetype) {
        for (var entry : archetype.attributeModifiers()) {
            if (entry.attribute().equals(Attributes.AIR_DRAG_MODIFIER))
                return entry.modifier().amount() * Config.addModifierMultiplier.get();
        }
        return 1;
    }
}