package net.justmili.sulfurcubed.content.mechanics.logic;

import net.justmili.sulfurcubed.config.Config;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class CopyCubeAnimations {
    private static final Map<AbstractClientPlayer, CopyCubeAnimations> TRACKERS = Collections.synchronizedMap(new WeakHashMap<>());
    public float targetSquish;
    public float squish;
    public float oSquish;
    private boolean wasOnGround;

    public static CopyCubeAnimations get(AbstractClientPlayer player) {
        return TRACKERS.computeIfAbsent(player, _ -> new CopyCubeAnimations());
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
            spawnLandingParticles(player);
            this.targetSquish = -0.5f;
        } else if (!onGround && this.wasOnGround) {
            this.targetSquish = 1.0f;
        }

        this.wasOnGround = onGround;
        this.targetSquish *= 0.6f;
    }

    private void spawnLandingParticles(AbstractClientPlayer player) {
        float size = CopyCubeConstants.HITBOX_WIDTH * 2.0f;
        float radius = size / 2.0f;
        var random = player.getRandom();

        for (int i = 0; (float) i < size * 16.0f; i++) {
            float direction = random.nextFloat() * ((float) Math.PI * 2f);
            float d = random.nextFloat() * 0.5f + 0.5f;
            float xd = Mth.sin(direction) * radius * d;
            float zd = Mth.cos(direction) * radius * d;

            player.level().addParticle(
                ParticleTypes.SULFUR_CUBE_GOO,
                player.getX() + (double) xd,
                player.getY(),
                player.getZ() + (double) zd,
                0.0, 0.0, 0.0
            );
        }
    }
}