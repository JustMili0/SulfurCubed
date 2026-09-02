package net.justmili.libs.v1.utils.client;

import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Environment(EnvType.CLIENT)
public class ClientUtil {
    public static Minecraft client = Minecraft.getInstance();

    public static Window getWindow() {
        return client.getWindow();
    }

    public static boolean isDebugScreenOn() {
        return client.getDebugOverlay().showDebugScreen();
    }

    public static int getWidth() {
        return getWindow().getGuiScaledWidth();
    }
    public static int getHeight() {
        return getWindow().getGuiScaledHeight();
    }

    public static Player getPlayer() {
        return client.player;
    }
    public static Level getLevel() {
        return client.level;
    }

    public static ResourceKey<Level> getDimension() {
        return getLevel().dimension();
    }

    public static boolean inDimension(ResourceKey<Level> dimension) {
        return getDimension() == dimension;
    }

    public static void playSound(SoundEvent sound, float volume, float pitch) {
        if (getPlayer() == null) return;
        getPlayer().playSound(sound, volume, pitch);
    }

    public static boolean isPackLoaded(String pack) {
        return client.getResourcePackRepository().isAvailable(pack);
    }
    public static boolean arePackLoaded(String... packs) {
        for (String pack : packs) {
            if (isPackLoaded(pack)) return true;
        }
        return false;
    }
    public static boolean addPackAndTell(String pack) {
        // Add resource pack and tell if it was loaded or not
        return client.getResourcePackRepository().addPack(pack);
    }
    public static void removePack(String pack) {
        client.getResourcePackRepository().removePack(pack);
        reloadPacks();
    }
    public static void reloadPacks() {
        client.reloadResourcePacks();
    }
}