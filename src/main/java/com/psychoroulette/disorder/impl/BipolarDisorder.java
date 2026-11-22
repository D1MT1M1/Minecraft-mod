package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class BipolarDisorder extends Disorder {
    public BipolarDisorder() {
        super("bipolar", 0xFFFFAA00);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        // Flip every 30 seconds (600 ticks)
        boolean mania = ((activeTicks / 600) % 2) == 0;
        int prevPhase = state.getCounter("bipolar_phase");
        int curPhase = mania ? 1 : 2;

        if (prevPhase != curPhase) {
            state.setCounter("bipolar_phase", curPhase);
            if (mania) {
                player.sendSystemMessage(Component.literal("⚡ [МАНИЯ] Я БЕССМЕРТНЫЙ БОГ! МЕНЯ НЕ ОСТАНОВИТЬ!")
                        .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
            } else {
                player.sendSystemMessage(Component.literal("🌧 [АПАТИЯ] Зачем вообще копать эти кубы... Всё тлен...")
                        .withStyle(ChatFormatting.GRAY));
            }
        }

        state.setHideHealthHud(mania);

        if (activeTicks % 20 != 0) return;

        if (mania) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 45, 2, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.HASTE, 45, 2, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 45, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 45, 1, true, false));
            state.setExtraBanner("⚡ ФАЗА: МАНИЯ (ХП: ∞ БЕССМЕРТЕН)");
        } else {
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 45, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 45, 2, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 45, 1, true, false));
            player.setSprinting(false);
            state.setExtraBanner("🌧 ФАЗА: АПАТИЯ (нет сил)");
        }
    }

    @Override
    public void onEnd(ServerPlayer player, PlayerPsychoState state) {
        state.setHideHealthHud(false);
    }
}
