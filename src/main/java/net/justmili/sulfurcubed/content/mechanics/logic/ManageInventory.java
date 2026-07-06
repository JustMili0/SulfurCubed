package net.justmili.sulfurcubed.content.mechanics.logic;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.justmili.sulfurcubed.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class ManageInventory {

    // Lock to just middle hotbar slot
    @Environment(EnvType.CLIENT)
    public static void onClientTick(Minecraft minecraft) {
        if (minecraft.player == null) return;
        Inventory inv = minecraft.player.getInventory();

        if (inv.getSelectedSlot() != 4) {
            inv.setSelectedSlot(4);

            // Send update packet to server
            minecraft.getConnection().send(new ServerboundSetCarriedItemPacket(4));
        }
    }

    // Lock all slots (except hotbar middle, offhand and armor are configurable)
    public static void onPlayerTick(ServerPlayer player) {
        Inventory inv = player.getInventory();
        int size = inv.getNonEquipmentItems().size(); // hotbar + rest of inventory

        for (int slot = 0; slot < size; slot++) {
            if (slot == 4) continue; // keep the selected middle slot free

            ItemStack current = inv.getItem(slot);
            if (LockSlots.isSlotLocked(current)) continue;

            if (!current.isEmpty()) {
                player.drop(current.copy(), true);
                inv.setItem(slot, ItemStack.EMPTY);
                inv.setChanged();
            }

            inv.setItem(slot, LockSlots.slotLock());
            inv.setChanged();
        }

        if (Config.disableOffhand.get()) {
            ItemStack offhand = player.getItemBySlot(EquipmentSlot.OFFHAND);

            if (!LockSlots.isSlotLocked(offhand)) {
                if (!offhand.isEmpty()) player.drop(offhand.copy(), true);
                inv.setChanged();
            }
        }

        if (Config.disableArmor.get()) {
            for (EquipmentSlot armorSlot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
            }) {
                ItemStack armor = player.getItemBySlot(armorSlot);

                if (!LockSlots.isSlotLocked(armor) && !armor.isEmpty()) {
                    player.drop(armor.copy(), true);
                    inv.setChanged();
                }
            }
        }
    }
}