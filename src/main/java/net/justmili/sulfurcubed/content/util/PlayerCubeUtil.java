package net.justmili.sulfurcubed.content.util;

import net.justmili.libs.v1.utils.FdaUtil;
import net.justmili.sulfurcubed.config.Config;
import net.justmili.sulfurcubed.content.variables.SCAttachments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SulfurCubeArchetype;
import net.minecraft.world.entity.SulfurCubeArchetypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PlayerCubeUtil {
    public static final EntityDimensions HITBOX = EntityTypes.SULFUR_CUBE.getDimensions();
    public static final float HITBOX_WIDTH = HITBOX.width() * 2;
    public static final float HITBOX_HEIGHT = HITBOX.height() * 2;
    public static boolean floatsInLiquids = false;

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
        var currentItem = player.getMainHandItem();

        if (ItemStack.isSameItem(
            FdaUtil.get(player, SCAttachments.LAST_KNOWN_STACK, ItemStack.EMPTY), currentItem)
            && FdaUtil.has(player, SCAttachments.LAST_ARCHETYPE)
        ) return FdaUtil.get(player, SCAttachments.LAST_ARCHETYPE);

        var registry = player.level().registryAccess();
        var archetype = registry.lookupOrThrow(Registries.SULFUR_CUBE_ARCHETYPE).stream()
            .filter(arch -> currentItem.is(arch.items()))
            .findFirst().orElse(registry.getOrThrow(SulfurCubeArchetypes.REGULAR).value());

        FdaUtil.set(player, SCAttachments.LAST_KNOWN_STACK, currentItem);
        FdaUtil.set(player, SCAttachments.LAST_ARCHETYPE, archetype);

        return archetype;
    }
    public static boolean hasHandItem(Player player) {
        return !player.getInventory().getItem(4).isEmpty();
    }

    public static void makeSound(Player player, SoundEvent sound) {
        if (player.level().isClientSide()) return;

        player.level().playSound(null, player, sound, player.getSoundSource(), 1f, 1f);
    }
    public static boolean isImmuneToSource(DamageSource source, Player player) {
        return source.is(DamageTypeTags.SULFUR_CUBE_WITH_BLOCK_IMMUNE_TO)
            || (Config.shouldTransform(player) && source.is(DamageTypes.IN_WALL));
        // Prevent suffocation if Sulfur Cube Player hitbox is in a block to prevent accidental deaths
    }
    public static boolean isBuoyant(Player player) {
        if (lookupForHeld(player).buoyant()) {
            floatsInLiquids = true;
            return true;
        }
        return false;
    }
}
