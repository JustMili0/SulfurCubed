package net.justmili.sulfurcubed.registries;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.justmili.sulfurcubed.content.mechanics.logic.LockSlots;
import net.justmili.sulfurcubed.content.mechanics.logic.ManageAttributes;
import net.justmili.sulfurcubed.content.mechanics.logic.ManageHealth;
import net.justmili.sulfurcubed.content.mechanics.logic.ManageInventory;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;

public class EventRegistry {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var player : server.getPlayerList().getPlayers()) {
                ManageInventory.onPlayerTick(player);
                ManageHealth.onPlayerTick(player);
                ManageAttributes.onPlayerTick(player);
            }
        });
        ServerEntityEvents.ENTITY_LOAD.register((entity, _) -> {
            if (!(entity instanceof ItemEntity item)) return;

            if (LockSlots.isSlotLocked(item.getItem(), true)) item.discard();
        });
        // To prevent losing momentum from fall damage
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((_, source, _) -> !source.is(DamageTypes.FALL));
    }
}
