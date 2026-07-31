package net.justmili.sulfurcubed.content.mechanics.logic;

import net.justmili.sulfurcubed.content.util.PlayerCubeUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class ManageVelocity {
    public static boolean knockback(Player player, final double power, double offsetX, double offsetZ, final DamageSource source, final float damage, final boolean comesFromEffect) {
        if (source.getEntity() == null || !PlayerCubeUtil.hasHandItem(player)) return false;
        
        var knockbackModifier = PlayerCubeUtil.knockbackModifiers(player);
        var hitSound = PlayerCubeUtil.soundSettings(player).hitSound();
        
        float horizontalHitAngleScale = 1.6F;
        float verticalHitAngleScale = 0.5F;
        float verticalPositionAngleScale = 0.8F;
        float horizontalPower = knockbackModifier.horizontalPower();
        float verticalPower = knockbackModifier.verticalPower();
        float originalHorizontalPower = horizontalPower;
        float originalVerticalPower = verticalPower;

        Vec2 originalAngle = new Vec2((float)offsetX, (float)offsetZ);
        Vec2 newAngle = applyHorizontalHitAngleScale(
            horizontalHitAngleScale, originalAngle,
            source.getEntity().getEyePosition(), source.getEntity().getLookAngle().normalize(),
            player.getBoundingBox().getCenter());

        Vec2 newPower = applyVerticalHitAnglePowerTransfer(
            verticalHitAngleScale, horizontalPower, verticalPower,
            source.getEntity().getEyePosition(),
            source.getEntity().getLookAngle().normalize(),
            player.getBoundingBox().getCenter(), player.getBbHeight());
        horizontalPower = newPower.x;
        verticalPower = newPower.y;
        newPower = applyVerticalPositionAnglePowerRotation(
            verticalPositionAngleScale, horizontalPower, verticalPower,
            originalHorizontalPower, originalVerticalPower,
            source.getEntity().position(), player.position());
        horizontalPower = newPower.x;
        verticalPower = newPower.y;

        offsetX = newAngle.x;
        offsetZ = newAngle.y;

        float powerMultiplier = Mth.sqrt(damage) * (comesFromEffect ? (float)power * 0.25F : 1.0F);
        horizontalPower *= powerMultiplier;
        verticalPower *= powerMultiplier;

        double knockBackResistance = PlayerCubeUtil.getSpeed(player);
        horizontalPower *= (float)((double)1.0F - knockBackResistance);
        verticalPower *= (float)((double)1.0F - knockBackResistance);

        player.needsSync = true;
        player.syncPosition = true; // needed so players actually know their velocity.

        Vec3 deltaMovement = player.getDeltaMovement();
        horizontalPower *= 0.4F;
        horizontalPower = Mth.clamp(horizontalPower, -128.0F, 128.0F);
        verticalPower = Mth.clamp(verticalPower, -128.0F, 128.0F);

        Vec3 horizontalKnockback = (new Vec3(offsetX, 0.0F, offsetZ)).normalize().scale(horizontalPower);
        player.setDeltaMovement(deltaMovement.x - horizontalKnockback.x,
            deltaMovement.y + (double)verticalPower * 1.2,
            deltaMovement.z - horizontalKnockback.z);

        PlayerCubeUtil.makeSound(player, hitSound.value());

        return true;
    }

    private static Vec2 applyHorizontalHitAngleScale(final float horizontalAngleScale, final Vec2 originalAngle,
                                                     final Vec3 attackerPosition, final Vec3 attackerAimDirection,
                                                     final Vec3 targetCenter) {
        Vec3 attackerToTarget = targetCenter.subtract(attackerPosition).normalize();
        float angleDiff = (float) Mth.atan2(
            attackerAimDirection.x * attackerToTarget.z - attackerAimDirection.z * attackerToTarget.x,
            attackerAimDirection.x * attackerToTarget.x + attackerAimDirection.z * attackerToTarget.z);

        return originalAngle.rotate(angleDiff * horizontalAngleScale);
    }

    private static Vec2 applyVerticalHitAnglePowerTransfer(final float verticalHitAngleScale, final float horizontalPower, final float verticalPower,
                                                           final Vec3 attackerPosition, final Vec3 attackerAimDirection, final Vec3 targetCenteredPosition,
                                                           final float targetHeight) {
        float targetHalfHeight = 0.5F * targetHeight;
        Vec3 targetTopPos = targetCenteredPosition.add(0.0F, targetHalfHeight, 0.0F);
        Vec3 tagetBottomPos = targetCenteredPosition.add(0.0F, -targetHalfHeight, 0.0F);
        Vec3 attackerToTargetTop = targetTopPos.subtract(attackerPosition).normalize();
        Vec3 attackerToTargetBottom = tagetBottomPos.subtract(attackerPosition).normalize();
        float verticalHitAngleFactor = (float) Mth.clampedMap(attackerAimDirection.y, attackerToTargetTop.y, attackerToTargetBottom.y, -1.0F, 1.0F);
        float transferredPowerRatio = Mth.abs(verticalHitAngleFactor * verticalHitAngleScale);

        if (verticalHitAngleFactor < 0.0F) transferredPowerRatio = -transferredPowerRatio;

        float px = horizontalPower * (1.0F - transferredPowerRatio);
        float py = verticalPower * (1.0F + transferredPowerRatio);
        return new Vec2(px, py);
    }

    private static Vec2 applyVerticalPositionAnglePowerRotation(final float verticalPositionAngleScale, final float horizontalPower, final float verticalPower,
                                                                final float originalHorizontalPower, final float originalVerticalPower,
                                                                final Vec3 attackerFeetPosition, final Vec3 targetFeetPosition) {
        Vec3 attackerFeetToTargetFeet = targetFeetPosition.subtract(attackerFeetPosition);
        float verticalPositionAngle = (float)Math.atan2(-attackerFeetToTargetFeet.y, attackerFeetToTargetFeet.horizontalDistance());
        Vec2 powerBeforeRotation = new Vec2(horizontalPower, verticalPower);
        Vec2 rotatedPower = powerBeforeRotation.rotate(-verticalPositionAngle * verticalPositionAngleScale);

        float horizontalRatio = originalHorizontalPower > 0.0F ? Mth.abs(rotatedPower.x) / originalHorizontalPower : 0.0F;
        float verticalRatio = originalVerticalPower > 0.0F ? Mth.abs(rotatedPower.y) / originalVerticalPower : 0.0F;
        float maxRatio = Math.max(horizontalRatio, verticalRatio);

        if (maxRatio > 1.0F) rotatedPower = rotatedPower.scale(1.0F / maxRatio);

        return rotatedPower;
    }
}
