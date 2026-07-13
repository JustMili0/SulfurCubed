package net.justmili.libs.v1.config.sync.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.justmili.libs.v1.config.sync.SyncConfigCSP;

import java.util.List;

public class SyncConfigCSPNetworking {
    public static void registerCommon() {
        PayloadTypeRegistry.clientboundPlay().register(SyncCSPBundlePayload.TYPE, SyncCSPBundlePayload.STREAM_CODEC);
    }

    public static void registerServer() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            List<SyncConfigCSP.SyncCSPPayload> payloads = SyncConfigCSP.buildAllPayloads();
            if (payloads.isEmpty()) return;

            ServerPlayNetworking.send(handler.player, new SyncCSPBundlePayload(payloads));
        });
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(SyncCSPBundlePayload.TYPE, (payload, context) ->
            context.client().execute(() -> SyncConfigCSP.applyPayloads(payload.payloads())));
    }
}