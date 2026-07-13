package net.justmili.sulfurcubed.content.mechanics.logic;

import net.justmili.sulfurcubed.SulfurCubed;
import net.justmili.sulfurcubed.config.Config;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SulfurCubeArchetype;
import net.minecraft.world.entity.SulfurCubeArchetypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ManageAttributes {
    public static void onPlayerTick(ServerPlayer player) {
        ItemStack held = player.getMainHandItem();

        double speed, bounce, friction, drag;
        var registry = player.level().registryAccess();
        Optional<SulfurCubeArchetype> archetype = registry.lookupOrThrow(Registries.SULFUR_CUBE_ARCHETYPE).stream()
            .filter(arch -> held.is(arch.items())).findFirst();
        SulfurCubeArchetype regular = registry.getOrThrow(SulfurCubeArchetypes.REGULAR).value();

        speed = getSpeed(archetype.orElse(regular));
        bounce = getBounce(archetype.orElse(regular));
        friction = getFriction(archetype.orElse(regular));
        drag = getDrag(archetype.orElse(regular));

        applyModifiers(player, speed, bounce, friction, drag);
    }

    private static void applyModifiers(ServerPlayer player, double speed, double bounce, double friction, double drag) {
        var ADD_VALUE = AttributeModifier.Operation.ADD_VALUE;
        var ADD_MULTI_TOTAL = AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
        AttributeModifier
            modSpeed = createModifier("speed", speed, ADD_VALUE),
            modBounce = createModifier("bounciness", bounce, ADD_VALUE),
            modFriction = createModifier("friction", friction, ADD_MULTI_TOTAL),
            modAirDrag = createModifier("air_drag", drag, ADD_MULTI_TOTAL),
            modScale = createModifier("scale", -0.4556, ADD_MULTI_TOTAL),
            modCamera = createModifier("camera_distance", 4, ADD_VALUE);
        AttributeInstance
            instSpeed = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE),
            instBounce = player.getAttribute(Attributes.BOUNCINESS),
            instFriction = player.getAttribute(Attributes.FRICTION_MODIFIER),
            instAirDrag = player.getAttribute(Attributes.AIR_DRAG_MODIFIER),
            instScale = player.getAttribute(Attributes.SCALE),
            instCamera = player.getAttribute(Attributes.CAMERA_DISTANCE);

        if (instSpeed == null || instBounce == null || instFriction == null || instAirDrag == null || instScale == null || instCamera == null) return;

        applyOrUpdate(instSpeed, modSpeed);
        applyOrUpdate(instBounce, modBounce);
        applyOrUpdate(instFriction, modFriction);
        applyOrUpdate(instAirDrag, modAirDrag);
        if (!Config.shouldTransform(player)) applyOrUpdate(instScale, modScale);
        applyOrUpdate(instCamera, modCamera);

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
    private static void applyOrUpdate(AttributeInstance instance, AttributeModifier modifier) {
        instance.addOrUpdateTransientModifier(modifier);
    }

    private static double getSpeed(SulfurCubeArchetype archetype) {
        return getAttribute(archetype, Attributes.KNOCKBACK_RESISTANCE, 0);
    }
    private static double getBounce(SulfurCubeArchetype archetype) {
        return getAttribute(archetype, Attributes.BOUNCINESS, 0);
    }
    private static double getFriction(SulfurCubeArchetype archetype) {
        return getAttribute(archetype, Attributes.FRICTION_MODIFIER, 1);
    }
    private static double getDrag(SulfurCubeArchetype archetype) {
        return getAttribute(archetype, Attributes.AIR_DRAG_MODIFIER, 1);
    }

    private static double getAttribute(SulfurCubeArchetype archetype, Holder<Attribute> attribute, int elseReturn ) {
        for (var entry : archetype.attributeModifiers()) {
            if (entry.attribute().equals(attribute)) {
                double value = entry.modifier().amount();
                return value + (value * Config.getModMultiplier());
            }
        }
        return elseReturn;
    }
}