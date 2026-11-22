package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

public class DromomaniaDisorder extends Disorder {
    public DromomaniaDisorder() {
        super("dromomania", 0xFF33FF66);
    }

    @Override
    public void onStart(ServerPlayer player, PlayerPsychoState state) {
        state.setLastPosition(player.position());
        state.setCounter("still_ticks", 0);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 10 != 0) return;

        Vec3 current = player.position();
        double dist = current.distanceTo(state.getLastPosition());
        state.setLastPosition(current);

        if (dist > 0.35) {
            state.setCounter("still_ticks", 0);
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 35, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 35, 0, true, false));
            state.setExtraBanner("🏃 В ДВИЖЕНИИ! (+Скорость II)");
        } else {
            int still = state.incrementCounter("still_ticks");
            if (still >= 4) { // 2 seconds standing still
                ServerLevel level = (ServerLevel) player.level();
                player.hurtServer(level, level.damageSources().magic(), 1.5F);
                player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 40, 0, true, false));
                state.setExtraBanner("🏃 НЕ СТОЙ НА МЕСТЕ! БЕГИ!");
            }
        }
    }
}
