package net.justmili.sulfurcubed.mixin.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Hud.class)
public class HudModifier {

    // Remove hunger bar. Sulfur Cubes don't starve
    @Inject(method = "extractFood", at = @At("HEAD"), cancellable = true)
    private void removeHungerBar(GuiGraphicsExtractor graphics, Player player, int yLineBase, int xRight, CallbackInfo ci) {
        ci.cancel();
    }

    // Center health bar. 4 hearts look prettier when centered
    @ModifyArgs(method = "extractPlayerHealth", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Hud;extractHearts(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V"))
    private void centerHearts(Args args) {
        GuiGraphicsExtractor graphics = args.get(0);
        float maxHealth = args.get(6);
        int absorption = args.get(9);

        int healthContainers = Mth.ceil(maxHealth / 2.0f),
            absorptionContainers = Mth.ceil((float) absorption / 2.0f),
            totalContainers = Math.min(healthContainers + absorptionContainers, 10);

        int rowWidth = totalContainers > 0 ? (totalContainers - 1) * 8 + 9 : 9,
            centeredXLeft = (graphics.guiWidth() - rowWidth) / 2;

        args.set(2, centeredXLeft);
    }

    // Make air level rise up and down with health height similar to armor bar
    @ModifyArgs(method = "extractPlayerHealth", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/Hud;extractAirBubbles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;III)V"))
    private void raiseBubblesForExtraHealth(Args args) {
        Player player = args.get(1);
        int yLineAir = args.get(3);

        float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), player.getHealth());
        int totalAbsorption = Mth.ceil(player.getAbsorptionAmount()),
            numHealthRows = Mth.ceil((maxHealth + (float) totalAbsorption) / 2.0f / 10.0f),
            healthRowHeight = Math.max(10 - (numHealthRows - 2), 3);

        args.set(3, yLineAir - (numHealthRows - 1) * healthRowHeight);
    }
}