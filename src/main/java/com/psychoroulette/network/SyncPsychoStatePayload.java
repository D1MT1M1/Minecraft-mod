package com.psychoroulette.network;

import com.psychoroulette.PsychoRouletteMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncPsychoStatePayload(
        String primaryId,
        String secondaryId,
        int primaryColor,
        int secondaryColor,
        int ticksRemaining,
        int maxTicks,
        boolean hidden,
        boolean calm,
        boolean hideHealthHud,
        String extraBanner
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncPsychoStatePayload> TYPE =
            new CustomPacketPayload.Type<>(PsychoRouletteMod.id("sync_psycho_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPsychoStatePayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                ByteBufCodecs.STRING_UTF8.encode(buf, payload.primaryId);
                ByteBufCodecs.STRING_UTF8.encode(buf, payload.secondaryId);
                buf.writeInt(payload.primaryColor);
                buf.writeInt(payload.secondaryColor);
                buf.writeInt(payload.ticksRemaining);
                buf.writeInt(payload.maxTicks);
                buf.writeBoolean(payload.hidden);
                buf.writeBoolean(payload.calm);
                buf.writeBoolean(payload.hideHealthHud);
                ByteBufCodecs.STRING_UTF8.encode(buf, payload.extraBanner);
            },
            buf -> new SyncPsychoStatePayload(
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    ByteBufCodecs.STRING_UTF8.decode(buf)
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
