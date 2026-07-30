package net.justmili.sulfurcubed.content.mechanics.logic;

import net.justmili.sulfurcubed.SulfurCubed;
import net.justmili.sulfurcubed.config.Config;
import net.justmili.sulfurcubed.content.util.PlayerCubeUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import static net.justmili.libs.v1.utils.AttributeUtil.*;

public class ManageAttributes {
    public static void onPlayerTick(ServerPlayer player) {
        applyModifiers(player,
            getValueMultiplied(PlayerCubeUtil.getSpeed(player)),
            getValueMultiplied(PlayerCubeUtil.getBounce(player)),
            getValueMultiplied(PlayerCubeUtil.getFriction(player)),
            getValueMultiplied(PlayerCubeUtil.getAirDrag(player))
        );
    }
    private static double getValueMultiplied(double value) {
        return value + (value * Config.getModMultiplier());
    }

    private static void applyModifiers(ServerPlayer player, double speed, double bounce, double friction, double drag) {
        var ADD_VALUE = AttributeModifier.Operation.ADD_VALUE;
        var ADD_MULTI_TOTAL = AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;

        var modSpeed = newModifier(id("speed"), speed, ADD_VALUE);
        var modBounce = newModifier(id("bounciness"), bounce, ADD_VALUE);
        var modFriction = newModifier(id("friction"), friction, ADD_MULTI_TOTAL);
        var modAirDrag = newModifier(id("air_drag"), drag, ADD_MULTI_TOTAL);
        var modScale = newModifier(id("scale"), -0.4556, ADD_MULTI_TOTAL);
        var modCamera = newModifier(id("camera_distance"), 3, ADD_VALUE);

        var instSpeed = getAttribute(player, Attributes.KNOCKBACK_RESISTANCE); // Yes, knockback resistance for speed, not speed. That's correct
        var instBounce = getAttribute(player, Attributes.BOUNCINESS);
        var instFriction = getAttribute(player, Attributes.FRICTION_MODIFIER);
        var instAirDrag = getAttribute(player, Attributes.AIR_DRAG_MODIFIER);
        var instScale = getAttribute(player, Attributes.SCALE);
        var instCamera = getAttribute(player, Attributes.CAMERA_DISTANCE);

        addOrUpdate(instSpeed, modSpeed);
        addOrUpdate(instBounce, modBounce);
        addOrUpdate(instFriction, modFriction);
        addOrUpdate(instAirDrag, modAirDrag);
        if (!Config.shouldTransform(player)) addOrUpdate(instScale, modScale);
        addOrUpdate(instCamera, modCamera);

        if (player.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() || !player.gameMode().isSurvival()) {
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