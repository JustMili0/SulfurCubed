package net.justmili.sulfurcubed.mixin;

import net.justmili.sulfurcubed.content.mechanics.logic.LockSlots;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PreventBarrierDrop {
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;",
        at = @At("HEAD"), cancellable = true)
    private void preventLockedDrop(ItemStack stack, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir) {
        if (LockSlots.isSlotLocked(stack, false)) {
            cir.setReturnValue(null);
        }
    }
}
