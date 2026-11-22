package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BerserkPsychosisDisorder extends Disorder {
    public BerserkPsychosisDisorder() {
        super("berserk_psychosis", 0xFFFF1133);
    }

    @Override
    public void onStart(ServerPlayer player, PlayerPsychoState state) {
        state.setCounter("peace_sec", 0);
    }

    @Override
    public void onAttack(ServerPlayer player, Entity target, PlayerPsychoState state) {
        if (target instanceof LivingEntity) {
            state.setCounter("peace_sec", 0);
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 160, 2, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 160, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1, true, false));
            state.setExtraBanner("⚔ КРОВАВЫЙ РАЖ! (Сила III)");
        }
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 20 != 0) return;

        int peace = state.incrementCounter("peace_sec");
        if (peace >= 20) {
            ServerLevel level = (ServerLevel) player.level();
            state.setExtraBanner("⚔ СКУЧНО! УДАРЬ КОГО-НИБУДЬ!");
            if (peace == 20) {
                player.sendSystemMessage(Component.literal("⚔ Слишком тихо... Нужна драка, иначе станет плохо!")
                        .withStyle(ChatFormatting.DARK_RED));
            }
            if (peace % 3 == 0) {
                player.hurtServer(level, level.damageSources().magic(), 1.5F);
            }
        } else if (peace > 8) {
            state.setExtraBanner("⚔ Без драки: " + peace + "/20 сек");
        }
    }
}
