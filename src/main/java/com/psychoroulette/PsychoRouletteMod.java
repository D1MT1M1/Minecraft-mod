package com.psychoroulette;

import com.psychoroulette.command.PsychoCommands;
import com.psychoroulette.disorder.DisorderRegistry;
import com.psychoroulette.disorder.PsychoManager;
import com.psychoroulette.item.ModItems;
import com.psychoroulette.network.PsychoNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsychoRouletteMod implements ModInitializer {
    public static final String MOD_ID = "psychoroulette";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Psycho-Roulette with 20 chaotic disorders...");

        ModItems.register();
        DisorderRegistry.registerAll();
        PsychoNetworking.registerPayloads();

        // Main server tick loop for every player
        ServerTickEvents.END_SERVER_TICK.register(PsychoManager::onServerTick);

        // Player join / disconnect lifecycle
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PsychoManager.onPlayerJoin(handler.getPlayer());
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PsychoManager.onPlayerLeave(handler.getPlayer());
        });

        // Death handling: marks cycle failed (no survival reward)
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayer player) {
                PsychoManager.onPlayerDeath(player);
            }
        });

        // Combat hook (used by Berserk Psychosis, Megalomania, etc.)
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                PsychoManager.onPlayerAttack(serverPlayer, entity);
            }
            return InteractionResult.PASS;
        });

        // Admin & debug commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            PsychoCommands.register(dispatcher);
        });
    }
}
