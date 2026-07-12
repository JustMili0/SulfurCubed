package net.justmili.sulfurcubed.registries;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.justmili.libs.v1.utils.ClientUtil;
import net.justmili.sulfurcubed.content.mechanics.logic.CopyCubeBehavior;
import net.justmili.sulfurcubed.content.mechanics.logic.ManageAttributes;
import net.justmili.sulfurcubed.content.mechanics.logic.ManageHealth;
import net.justmili.sulfurcubed.content.mechanics.logic.ManageInventory;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.level.Level;

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
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ManageInventory.onClientTick(client);

            Level level = ClientUtil.getLevel();
            if (level == null) return;
            for (var player : level.players()) {
                if (player instanceof AbstractClientPlayer clientPlayer) {
                    CopyCubeBehavior.get(clientPlayer).tick(clientPlayer);
                }
            }
        });
    }
}
