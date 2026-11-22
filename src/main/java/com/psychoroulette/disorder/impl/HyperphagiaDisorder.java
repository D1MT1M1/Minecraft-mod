package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class HyperphagiaDisorder extends Disorder {
    public HyperphagiaDisorder() {
        super("hyperphagia", 0xFFFF9933);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 20 != 0) return;

        // Rapid hunger drain
        player.causeFoodExhaustion(2.2F);

        int food = player.getFoodData().getFoodLevel();
        if (food >= 18) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 40, 0, true, false));
            state.setExtraBanner("🍖 СЫТЫЙ И МОЩНЫЙ!");
        } else if (food <= 12) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0, true, false));
            state.setExtraBanner("🍖 ХОЧУ ЖРАТЬ! ЕШЬ СРОЧНО!");
        } else {
            state.setExtraBanner("🍖 Скоро проголодаешься...");
        }
    }
}
