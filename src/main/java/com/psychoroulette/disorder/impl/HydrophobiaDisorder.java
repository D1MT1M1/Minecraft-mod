package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

public class HydrophobiaDisorder extends Disorder {
    public HydrophobiaDisorder() {
        super("hydrophobia", 0xFF0077FF);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 20 != 0) return;

        ServerLevel level = (ServerLevel) player.level();
        if (player.isInWaterOrRain()) {
            player.hurtServer(level, level.damageSources().drown(), 1.5F);
            player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 50, 0, true, false));
            level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_HURT, SoundSource.PLAYERS, 0.8F, 1.3F);
            if (player.isInWater()) {
                Vec3 v = player.getDeltaMovement();
                player.setDeltaMovement(v.x, 0.45, v.z);
                player.hurtMarked = true;
            }
            state.setExtraBanner("💧 ВОДА ОБЖИГАЕТ! БЕГИ НА СУШУ!");
        } else {
            state.setExtraBanner("🏜 Сухо и безопасно");
        }
    }
}
