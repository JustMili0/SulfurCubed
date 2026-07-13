package net.justmili.sulfurcubed.content.mechanics.logic;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityTypes;

public class CopyCubeConstants {
    public static final EntityDimensions HITBOX = EntityTypes.SULFUR_CUBE.getDimensions();
    public static final float HITBOX_WIDTH = HITBOX.width() * 2;
    public static final float HITBOX_HEIGHT = HITBOX.height() * 2;
}
