package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class NyctophobiaDisorder extends Disorder {
    public NyctophobiaDisorder() {
        super("nyctophobia", 0xFF442266);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 20 != 0) return;

        ServerLevel level = (ServerLevel) player.level();
        int light = level.getMaxLocalRawBrightness(player.blockPosition());

        if (light < 7) {
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1, true, false));
            state.setExtraBanner("🌑 СЛИШКОМ ТЕМНО! ПОСТАВЬ ФАКЕЛ!");
            if (activeTicks % 40 == 0) {
                player.hurtServer(level, level.damageSources().magic(), 1.0F);
                level.playSound(null, player.blockPosition(), SoundEvents.AMBIENT_CAVE.value(), SoundSource.AMBIENT, 0.8F, 0.9F);
            }
        } else {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 0, true, false));
            state.setExtraBanner("☀ В свете всё хорошо");
        }
    }
}
