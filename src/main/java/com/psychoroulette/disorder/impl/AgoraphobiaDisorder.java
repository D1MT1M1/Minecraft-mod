package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class AgoraphobiaDisorder extends Disorder {
    public AgoraphobiaDisorder() {
        super("agoraphobia", 0xFF33AAFF);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 20 != 0) return;

        ServerLevel level = (ServerLevel) player.level();
        boolean seesSky = level.canSeeSky(player.blockPosition().above());

        if (seesSky) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 2, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 45, 0, true, false));
            state.setExtraBanner("☁ ОТКРЫТОЕ НЕБО! НАЙДИ КРЫШУ!");
            if (activeTicks % 40 == 0) {
                level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 0.8F, 1.2F);
            }
        } else {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, true, false));
            state.setExtraBanner("🏠 Под крышей спокойно");
        }
    }
}
