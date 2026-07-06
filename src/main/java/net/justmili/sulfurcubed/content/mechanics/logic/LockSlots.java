package net.justmili.sulfurcubed.content.mechanics.logic;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

public class LockSlots {
    private static final String LOCK_TAG = "pbs_slotlock";
    static ItemStack slotLock() {
        ItemStack stack = new ItemStack(Items.BARRIER);
        CompoundTag tag = new CompoundTag();

        stack.set(DataComponents.CUSTOM_NAME, Component.literal(""));
        tag.putBoolean(LOCK_TAG, true);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        return stack;
    }
    public static boolean isSlotLocked(ItemStack stack) {
        if (stack.is(Items.BARRIER) && stack.getCount() != 1) {
            stack.setCount(1);
            return true;
        }
        if (stack.isEmpty() || !stack.is(Items.BARRIER)) return false;
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        return data != null && data.copyTag().getBoolean(LOCK_TAG).orElse(false);
    }
}
