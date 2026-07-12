package net.justmili.sulfurcubed.content.mechanics.logic;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityTypes;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class CopyCubeBehavior {
    private static final Map<AbstractClientPlayer, CopyCubeBehavior> TRACKERS = Collections.synchronizedMap(new WeakHashMap<>());
    public static final EntityDimensions CUBE_HITBOX = EntityTypes.SULFUR_CUBE.getDimensions();
    public static final float CUBE_HITBOX_WIDTH = CUBE_HITBOX.width() * 2;
    public static final float CUBE_HITBOX_HEIGHT = CUBE_HITBOX.height() * 2;
    public float targetSquish;
    public float squish;
    public float oSquish;
    private boolean wasOnGround;

    public static CopyCubeBehavior get(AbstractClientPlayer player) {
        return TRACKERS.computeIfAbsent(player, _ -> new CopyCubeBehavior());
    }

    public void tick(AbstractClientPlayer player) {
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
        float size = CUBE_HITBOX_WIDTH * 2.0f;
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