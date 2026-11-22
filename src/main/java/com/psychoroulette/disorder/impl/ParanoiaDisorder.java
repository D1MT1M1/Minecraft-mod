package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class ParanoiaDisorder extends Disorder {
    public ParanoiaDisorder() {
        super("paranoia", 0xFF9933FF);
    }

    @Override
    public void onStart(ServerPlayer player, PlayerPsychoState state) {
        state.setLastYaw(player.getYRot());
        state.setCounter("para_ticks", 0);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        float diff = Math.abs(wrapAngle(player.getYRot() - state.getLastYaw()));
        if (diff >= 110.0F) {
            state.setLastYaw(player.getYRot());
            state.setCounter("para_ticks", 0);
            state.setExtraBanner("👁 Сзади вроде никого... пока");
            return;
        }

        int ticks = state.incrementCounter("para_ticks");
        if (ticks > 120) { // 6 seconds without checking behind you
            state.setExtraBanner("👁 КТО-ТО ЗА СПИНОЙ! ОГЛЯНИСЬ!");
            if (ticks % 20 == 0) {
                ServerLevel level = (ServerLevel) player.level();
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 45, 0, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1, true, false));
                level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 0.9F, 1.3F);
            }
        }
    }

    private float wrapAngle(float angle) {
        float a = angle % 360.0F;
        if (a >= 180.0F) a -= 360.0F;
        if (a < -180.0F) a += 360.0F;
        return a;
    }
}
