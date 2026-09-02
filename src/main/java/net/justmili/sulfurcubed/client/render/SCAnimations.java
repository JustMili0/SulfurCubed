package net.justmili.sulfurcubed.client.render;

import net.justmili.sulfurcubed.config.Config;
import net.justmili.sulfurcubed.util.PlayerCubeUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class SCAnimations {
    private static final Map<AbstractClientPlayer, SCAnimations> TRACKERS = Collections.synchronizedMap(new WeakHashMap<>());
    public float targetSquish;
    public float squish;
    public float oSquish;
    private boolean wasOnGround;

    public static SCAnimations get(AbstractClientPlayer player) {
        return TRACKERS.computeIfAbsent(player, _ -> new SCAnimations());
    }

    public static float getSquish(AbstractClientPlayer player, float partialTicks) {
        var squish = get(player);
        return Mth.lerp(partialTicks, squish.oSquish, squish.squish);
    }

    public void tick(AbstractClientPlayer player) {
        if (!Config.shouldTransform(player)) {
            this.targetSquish = 0.0f;
            this.squish = 0.0f;
            this.oSquish = 0.0f;
            this.wasOnGround = player.onGround();
            return;
        }

        this.oSquish = this.squish;
        this.squish += (this.targetSquish - this.squish) * 0.5f;

        boolean onGround = player.onGround();
        if (onGround && !this.wasOnGround) {

            // Spawn particles
            float size = PlayerCubeUtil.HITBOX_WIDTH * 2.0f,
                radius = size / 2.0f;
            var random = player.getRandom();

            for (int i = 0; (float) i < size * 16.0f; i++) {
                float direction = random.nextFloat() * ((float) Math.PI * 2f),
                    distance = random.nextFloat() * 0.5f + 0.5f,
                    offsetX = Mth.sin(direction) * radius * distance,
                    offsetZ = Mth.cos(direction) * radius * distance;

                player.level().addParticle(
                    ParticleTypes.SULFUR_CUBE_GOO,
                    player.getX() + (double) offsetX,
                    player.getY(),
                    player.getZ() + (double) offsetZ,
                    0.0, 0.0, 0.0
                );
            }

            this.targetSquish = -0.5f;
        } else if (!onGround && this.wasOnGround) {
            this.targetSquish = 1.0f;
        }

        this.wasOnGround = onGround;
        this.targetSquish *= 0.6f;
    }
}