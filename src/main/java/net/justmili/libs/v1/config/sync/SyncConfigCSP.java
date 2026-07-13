package net.justmili.libs.v1.config.sync;

import net.justmili.libs.v1.config.ConfigLoader;
import net.justmili.libs.v1.config.entry.ConfigEntry;
import net.justmili.libs.v1.config.entry.ListConfigEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncConfigCSP {
    public record SyncCSPPayload(String modId, Map<String, String> entries, Map<String, String> lists) { }
    private static final Map<String, ConfigLoader> SYNCABLE = new HashMap<>();

    private SyncConfigCSP() {}

    public static void register(ConfigLoader loader) {
        SYNCABLE.put(loader.modId, loader);
    }

    public static List<SyncCSPPayload> buildAllPayloads() {
        List<SyncCSPPayload> payloads = new ArrayList<>(SYNCABLE.size());
        for (ConfigLoader loader : SYNCABLE.values()) {
            payloads.add(buildPayload(loader));
        }
        return payloads;
    }

    public static void applyPayloads(List<SyncCSPPayload> payloads) {
        for (SyncCSPPayload payload : payloads) {
            applyPayload(payload);
        }
    }

    private static SyncCSPPayload buildPayload(ConfigLoader loader) {
        Map<String, String> entries = new HashMap<>(capacityFor(loader.entries.size()));
        for (Map.Entry<String, ConfigEntry<?>> mapEntry : loader.entries.entrySet()) {
            entries.put(mapEntry.getKey(), mapEntry.getValue().serialize());
        }

        Map<String, String> lists = new HashMap<>(capacityFor(loader.listEntries.size()));
        for (Map.Entry<String, ListConfigEntry> mapEntry : loader.listEntries.entrySet()) {
            lists.put(mapEntry.getKey(), mapEntry.getValue().serialize());
        }

        return new SyncCSPPayload(loader.modId, entries, lists);
    }

    private static void applyPayload(SyncCSPPayload payload) {
        ConfigLoader loader = SYNCABLE.get(payload.modId());
        if (loader == null) return;

        for (Map.Entry<String, String> mapEntry : payload.entries().entrySet()) {
            ConfigEntry<?> entry = loader.entries.get(mapEntry.getKey());
            if (entry != null) entry.load(mapEntry.getValue());
        }
        for (Map.Entry<String, String> mapEntry : payload.lists().entrySet()) {
            ListConfigEntry<?> listEntry = loader.listEntries.get(mapEntry.getKey());
            if (listEntry != null) listEntry.load(mapEntry.getValue());
        }

        loader.save();
    }

    private static int capacityFor(int expectedSize) {
        return (int) (expectedSize / 0.75f) + 1;
    }
}