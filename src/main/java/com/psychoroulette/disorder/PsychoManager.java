package com.psychoroulette.disorder;

import com.psychoroulette.config.PsychoConfig;
import com.psychoroulette.item.ModItems;
import com.psychoroulette.network.PsychoNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PsychoManager {
    private static final Map<UUID, PlayerPsychoState> STATES = new ConcurrentHashMap<>();
    private static final Random RANDOM = new Random();

    public static PlayerPsychoState getState(ServerPlayer player) {
        return STATES.computeIfAbsent(player.getUUID(), id -> new PlayerPsychoState());
    }

    public static void onPlayerJoin(ServerPlayer player) {
        PlayerPsychoState state = getState(player);
        state.setLastPosition(player.position());
        state.setLastYaw(player.getYRot());
        if (state.getActiveDisorders().isEmpty() && !state.isCalm()) {
            rollNewDiagnosis(player, false);
        } else {
            PsychoNetworking.syncToPlayer(player, state);
        }
    }

    public static void onPlayerLeave(ServerPlayer player) {
        // Keep state in memory while server runs so relogging doesn't cheese the roulette
    }

    public static void onPlayerDeath(ServerPlayer player) {
        PlayerPsychoState state = getState(player);
        state.setDiedThisCycle(true);
    }

    public static void onPlayerAttack(ServerPlayer player, Entity target) {
        PlayerPsychoState state = getState(player);
        if (state.isCalm() || player.isCreative() || player.isSpectator()) {
            return;
        }
        for (Disorder disorder : new ArrayList<>(state.getActiveDisorders())) {
            disorder.onAttack(player, target, state);
        }
    }

    public static void onServerTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            tickPlayer(player);
        }
    }

    private static void tickPlayer(ServerPlayer player) {
        if (player.isSpectator()) {
            return;
        }
        PlayerPsychoState state = getState(player);

        // Calm mode (Miracle Pill active)
        if (state.isCalm()) {
            state.setCalmTicksRemaining(state.getCalmTicksRemaining() - 1);
            if (state.getCalmTicksRemaining() <= 0) {
                player.sendSystemMessage(Component.literal("⚠ Действие таблетки закончилось! Рулетка возвращается...")
                        .withStyle(ChatFormatting.YELLOW));
                rollNewDiagnosis(player, false);
            } else if (state.getCalmTicksRemaining() % 10 == 0) {
                PsychoNetworking.syncToPlayer(player, state);
            }
            return;
        }

        // Ensure player has a diagnosis
        if (state.getActiveDisorders().isEmpty()) {
            rollNewDiagnosis(player, false);
            return;
        }

        // Countdown timer
        int remaining = state.getTicksRemaining() - 1;
        state.setTicksRemaining(remaining);
        state.incrementActiveTicks();

        // 10-second warning ticks
        if (remaining > 0 && remaining <= 200 && remaining % 20 == 0) {
            int sec = remaining / 20;
            ServerLevel level = (ServerLevel) player.level();
            level.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.5F, 1.5F + (10 - sec) * 0.05F);
            if (sec == 10 || sec <= 3) {
                player.sendSystemMessage(Component.literal("⏱ Смена диагноза через " + sec + " сек...")
                        .withStyle(ChatFormatting.GOLD));
            }
        }

        // Trigger new diagnosis cycle
        if (remaining <= 0) {
            if (PsychoConfig.giveSurvivalRewards && !state.hasDiedThisCycle() && !player.isCreative()) {
                giveSurvivalReward(player);
            }
            rollNewDiagnosis(player, false);
            return;
        }

        // Run active disorders (skip effects in creative so testing/building is comfortable)
        if (!player.isCreative()) {
            for (Disorder disorder : new ArrayList<>(state.getActiveDisorders())) {
                disorder.onTick(player, state, state.getActiveTicks());
            }
        }

        // Sync HUD every 10 ticks (0.5 sec)
        if (state.getActiveTicks() % 10 == 0) {
            PsychoNetworking.syncToPlayer(player, state);
        }
    }

    private static void giveSurvivalReward(ServerPlayer player) {
        ItemStack reward = RANDOM.nextBoolean()
                ? new ItemStack(ModItems.MIRACLE_PILL, 1)
                : new ItemStack(ModItems.REROLL_TOKEN, 1);
        if (!player.getInventory().add(reward)) {
            player.drop(reward, false);
        }
        player.sendSystemMessage(Component.literal("🎁 Вы пережили цикл! Награда: ")
                .withStyle(ChatFormatting.GREEN)
                .append(reward.getHoverName().copy().withStyle(ChatFormatting.AQUA)));
    }

    public static void activateCalmMode(ServerPlayer player) {
        PlayerPsychoState state = getState(player);
        clearActiveDisorders(player, state);
        state.setCalmTicksRemaining(PsychoConfig.calmDurationTicks);
        PsychoNetworking.syncToPlayer(player, state);
    }

    public static void clearActiveDisorders(ServerPlayer player, PlayerPsychoState state) {
        for (Disorder d : new ArrayList<>(state.getActiveDisorders())) {
            d.onEnd(player, state);
        }
        state.getActiveDisorders().clear();
        state.clearScratchpad();
        state.resetActiveTicks();
        state.setHiddenDiagnosis(false);
    }

    public static void rollNewDiagnosis(ServerPlayer player, boolean forceCombo) {
        PlayerPsychoState state = getState(player);
        List<Disorder> previous = new ArrayList<>(state.getActiveDisorders());
        clearActiveDisorders(player, state);

        state.setCalmTicksRemaining(0);
        state.setDiedThisCycle(false);
        state.setMaxCycleTicks(PsychoConfig.cycleDurationTicks);
        state.setTicksRemaining(PsychoConfig.cycleDurationTicks);
        state.setLastPosition(player.position());
        state.setLastYaw(player.getYRot());

        ServerLevel level = (ServerLevel) player.level();
        long dayTime = level.getDayTime();
        boolean isNight = (dayTime % 24000L) >= 13000L;
        boolean isFullMoon = ((dayTime / 24000L) % 8L) == 0L;
        boolean isStormOrFullMoon = level.isThundering() || (isFullMoon && isNight);
        boolean isCombo = forceCombo || (PsychoConfig.enableOverdriveCombo && isStormOrFullMoon);

        Disorder first = DisorderRegistry.getRandom(RANDOM, previous);
        state.getActiveDisorders().add(first);
        first.onStart(player, state);

        Disorder second = null;
        if (isCombo) {
            List<Disorder> excl = new ArrayList<>(previous);
            excl.add(first);
            second = DisorderRegistry.getRandom(RANDOM, excl);
            state.getActiveDisorders().add(second);
            second.onStart(player, state);
        }

        boolean hidden = !forceCombo && RANDOM.nextDouble() < PsychoConfig.hiddenDiagnosisChance;
        state.setHiddenDiagnosis(hidden);

        level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.8F, isCombo ? 0.6F : 1.2F);

        if (hidden) {
            player.sendSystemMessage(Component.literal("🎰 [ПСИХО-РУЛЕТКА] Новый диагноз: ??? (Угадай сам по симптомам!)")
                    .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));
        } else if (second != null) {
            player.sendSystemMessage(Component.literal("⚡ [ОБОСТРЕНИЕ x2] Комбо-диагноз: ")
                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
                    .append(first.getDisplayName().copy().withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal(" + ").withStyle(ChatFormatting.RED))
                    .append(second.getDisplayName().copy().withStyle(ChatFormatting.GOLD)));
        } else {
            player.sendSystemMessage(Component.literal("🎰 [ПСИХО-РУЛЕТКА] Ваш новый диагноз: ")
                    .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD)
                    .append(first.getDisplayName().copy().withStyle(ChatFormatting.YELLOW)));
            player.sendSystemMessage(Component.literal("   ↳ ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(first.getDescription().copy().withStyle(ChatFormatting.GRAY)));
        }

        PsychoNetworking.syncToPlayer(player, state);
    }

    public static boolean forceSetDisorder(ServerPlayer player, String disorderId) {
        Disorder target = DisorderRegistry.get(disorderId);
        if (target == null) {
            return false;
        }
        PlayerPsychoState state = getState(player);
        clearActiveDisorders(player, state);
        state.setCalmTicksRemaining(0);
        state.setDiedThisCycle(false);
        state.setMaxCycleTicks(PsychoConfig.cycleDurationTicks);
        state.setTicksRemaining(PsychoConfig.cycleDurationTicks);
        state.getActiveDisorders().add(target);
        target.onStart(player, state);

        player.sendSystemMessage(Component.literal("🧪 Установлен диагноз: ")
                .withStyle(ChatFormatting.AQUA)
                .append(target.getDisplayName().copy().withStyle(ChatFormatting.YELLOW)));
        PsychoNetworking.syncToPlayer(player, state);
        return true;
    }
}
