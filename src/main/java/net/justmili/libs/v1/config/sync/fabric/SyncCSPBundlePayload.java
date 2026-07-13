package net.justmili.libs.v1.config.sync.fabric;

import net.justmili.libs.CoreLibs;
import net.justmili.libs.v1.config.sync.SyncConfigCSP.SyncCSPPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SyncCSPBundlePayload(List<SyncCSPPayload> payloads) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncCSPBundlePayload> TYPE =
        new CustomPacketPayload.Type<>(CoreLibs.asResource("sync_common_sp_config"));

    private static final StreamCodec<RegistryFriendlyByteBuf, Map<String, String>> MAP_CODEC =
        ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8.cast(), ByteBufCodecs.STRING_UTF8.cast());

    private static final StreamCodec<RegistryFriendlyByteBuf, SyncCSPPayload> ENTRY_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8.cast(), SyncCSPPayload::modId,
        MAP_CODEC, SyncCSPPayload::entries,
        MAP_CODEC, SyncCSPPayload::lists,
        SyncCSPPayload::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCSPBundlePayload> STREAM_CODEC = StreamCodec.composite(
        ENTRY_CODEC.apply(ByteBufCodecs.list()), SyncCSPBundlePayload::payloads,
        SyncCSPBundlePayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}