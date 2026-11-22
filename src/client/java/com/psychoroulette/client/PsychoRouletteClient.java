package com.psychoroulette.client;

import com.psychoroulette.PsychoRouletteMod;
import com.psychoroulette.network.SyncPsychoStatePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

public class PsychoRouletteClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SyncPsychoStatePayload.TYPE, (payload, context) -> {
            context.client().execute(() -> ClientPsychoState.update(payload));
        });

        HudElementRegistry.addLast(PsychoRouletteMod.id("psycho_hud"), PsychoHudOverlay::render);
    }
}
