package net.justmili.sulfurcubed.registries;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.justmili.sulfurcubed.content.mechanics.logic.ManageAttributes;
import net.justmili.sulfurcubed.content.mechanics.logic.ManageHealth;
import net.justmili.sulfurcubed.content.mechanics.logic.ManageInventory;

public class EventRegistry {
    public static void registerServer() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var player : server.getPlayerList().getPlayers()) {
                ManageInventory.onPlayerTick(player);
                ManageHealth.onPlayerTick(player);
                ManageAttributes.onPlayerTick(player);
            }
        });
    }
    public static void registerClient() {
        ClientTickEvents.END_CLIENT_TICK.register(ManageInventory::onClientTick);
    }
}
