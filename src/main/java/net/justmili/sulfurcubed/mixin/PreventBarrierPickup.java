package net.justmili.sulfurcubed.mixin;

import net.justmili.sulfurcubed.content.mechanics.logic.LockSlots;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class PreventBarrierPickup {
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void preventLockedPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = ((Slot)(Object)this).getItem();
        if (LockSlots.isSlotLocked(stack)) {
            cir.setReturnValue(false);
        }
    }
}
