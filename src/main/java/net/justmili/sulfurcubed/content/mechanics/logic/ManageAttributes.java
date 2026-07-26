package net.justmili.sulfurcubed.content.mechanics.logic;

import net.justmili.sulfurcubed.SulfurCubed;
import net.justmili.sulfurcubed.config.Config;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
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

import static net.justmili.libs.v1.utils.AttributeUtil.*;

public class ManageAttributes {
    public static void onPlayerTick(ServerPlayer player) {
        ItemStack held = player.getMainHandItem();

        double speed, bounce, friction, drag;
        var registry = player.level().registryAccess();
        Optional<SulfurCubeArchetype> archetype = registry.lookupOrThrow(Registries.SULFUR_CUBE_ARCHETYPE).stream()
            .filter(arch -> held.is(arch.items())).findFirst();
        SulfurCubeArchetype regular = registry.getOrThrow(SulfurCubeArchetypes.REGULAR).value();

        speed = getAttributeValue(archetype.orElse(regular), Attributes.KNOCKBACK_RESISTANCE, 0);
        bounce = getAttributeValue(archetype.orElse(regular), Attributes.BOUNCINESS, 0);
        friction = getAttributeValue(archetype.orElse(regular), Attributes.FRICTION_MODIFIER, 1);
        drag = getAttributeValue(archetype.orElse(regular), Attributes.AIR_DRAG_MODIFIER, 1);

        applyModifiers(player, speed, bounce, friction, drag);
    }

    // Get attribute value, add itself multiplied by config value
    private static double getAttributeValue(SulfurCubeArchetype archetype, Holder<Attribute> attribute, int elseReturn ) {
        for (var entry : archetype.attributeModifiers()) {
            if (entry.attribute().equals(attribute)) {
                double value = entry.modifier().amount();
                return value + (value * Config.getModMultiplier());
            }
        }
        return elseReturn;
    }

    private static void applyModifiers(ServerPlayer player, double speed, double bounce, double friction, double drag) {
        var ADD_VALUE = AttributeModifier.Operation.ADD_VALUE;
        var ADD_MULTI_TOTAL = AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
        AttributeModifier
            modSpeed = newModifier(id("speed"), speed, ADD_VALUE),
            modBounce = newModifier(id("bounciness"), bounce, ADD_VALUE),
            modFriction = newModifier(id("friction"), friction, ADD_MULTI_TOTAL),
            modAirDrag = newModifier(id("air_drag"), drag, ADD_MULTI_TOTAL),
            modScale = newModifier(id("scale"), -0.4556, ADD_MULTI_TOTAL),
            modCamera = newModifier(id("camera_distance"), 4, ADD_VALUE);
        AttributeInstance
            instSpeed = getAttribute(player, Attributes.KNOCKBACK_RESISTANCE),
            instBounce = getAttribute(player, Attributes.BOUNCINESS),
            instFriction = getAttribute(player, Attributes.FRICTION_MODIFIER),
            instAirDrag = getAttribute(player, Attributes.AIR_DRAG_MODIFIER),
            instScale = getAttribute(player, Attributes.SCALE),
            instCamera = getAttribute(player, Attributes.CAMERA_DISTANCE);

        addOrUpdate(instSpeed, modSpeed);
        addOrUpdate(instBounce, modBounce);
        addOrUpdate(instFriction, modFriction);
        addOrUpdate(instAirDrag, modAirDrag);
        if (!Config.shouldTransform(player)) addOrUpdate(instScale, modScale);
        addOrUpdate(instCamera, modCamera);

        if (player.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() || player.isCreative() || player.isSpectator()) {
            instSpeed.removeModifier(modSpeed);
            instBounce.removeModifier(modBounce);
            instFriction.removeModifier(modFriction);
            instAirDrag.removeModifier(modAirDrag);
            // Don't remove scale and camera
        }
    }

    private static Identifier id(String id) {
        return SulfurCubed.asResource(id);
    }
}