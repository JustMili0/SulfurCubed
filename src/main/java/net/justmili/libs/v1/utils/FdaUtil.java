package net.justmili.libs.v1.utils;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

@SuppressWarnings({"NullableProblems"})
public class FdaUtil {
    //True/false and text
    public static boolean getBool(AttachmentTarget target, AttachmentType<Boolean> variable) {
        return get(target, variable, false);
    }

    public static String getString(AttachmentTarget target, AttachmentType<String> variable) {
        return get(target, variable, "ValueReturnedNull");
    }

    //Numbers yay
    public static int getInt(AttachmentTarget target, AttachmentType<Integer> variable) {
        return get(target, variable, -1);
    }

    public static double getDouble(AttachmentTarget target, AttachmentType<Double> variable) {
        return get(target, variable, -1.0);
    }

    public static float getFloat(AttachmentTarget target, AttachmentType<Float> variable) {
        return get(target, variable, -1.0f);
    }

    public static long getLong(AttachmentTarget target, AttachmentType<Long> variable) {
        return get(target, variable, -1L);
    }

    // Generic value getter and setter
    public static <T> T get(AttachmentTarget target, AttachmentType<T> variable, T defaultValue) {
        if (variable == null) return defaultValue;
        return target.getAttachedOrElse(variable, defaultValue);
    }
    public static <T> T get(AttachmentTarget target, AttachmentType<T> variable) {
        return target.getAttachedOrThrow(variable);
    }
    public static <T> void set(AttachmentTarget target, AttachmentType<T> variable, T value) {
        target.setAttached(variable, value);
    }
    public static <T> boolean has(AttachmentTarget target, AttachmentType<T> variable) {
        return target.hasAttached(variable);
    }

    //Creates values that will clear after a restart
    public static <T> AttachmentType<T> create(Identifier id, T defaultValue) {
        return AttachmentRegistry.create(id, builder -> builder.initializer(() -> defaultValue).copyOnDeath());
    }

    //Creates values that will NOT clear after a restart
    public static <T> AttachmentType<T> createPersistent(Identifier id, T defaultValue, Codec<T> codec) {
        return AttachmentRegistry.create(id, builder -> builder.initializer(() -> defaultValue).copyOnDeath().persistent(codec));
    }
}
