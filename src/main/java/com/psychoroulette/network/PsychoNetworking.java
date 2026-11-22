package com.psychoroulette.network;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class PsychoNetworking {

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(SyncPsychoStatePayload.TYPE, SyncPsychoStatePayload.CODEC);
    }

    public static void syncToPlayer(ServerPlayer player, PlayerPsychoState state) {
        if (!ServerPlayNetworking.canSend(player, SyncPsychoStatePayload.TYPE)) {
            return;
        }

        Disorder d1 = state.getPrimaryDisorder();
        Disorder d2 = state.getSecondaryDisorder();

        SyncPsychoStatePayload payload = new SyncPsychoStatePayload(
                d1 != null ? d1.getId() : "",
                d2 != null ? d2.getId() : "",
                d1 != null ? d1.getColor() : 0xFF33FFCC,
                d2 != null ? d2.getColor() : 0xFFFF3366,
                state.isCalm() ? state.getCalmTicksRemaining() : state.getTicksRemaining(),
                state.isCalm() ? state.getMaxCalmTicks() : state.getMaxCycleTicks(),
                state.isHiddenDiagnosis(),
                state.isCalm(),
                state.isHideHealthHud(),
                state.getExtraBanner()
        );

        ServerPlayNetworking.send(player, payload);
    }
}
