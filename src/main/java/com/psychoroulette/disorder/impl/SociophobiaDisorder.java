package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SociophobiaDisorder extends Disorder {
    public SociophobiaDisorder() {
        super("sociophobia", 0xFFFF88AA);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 20 != 0) return;

        ServerLevel level = (ServerLevel) player.level();
        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(9.0),
                e -> e != player && e.isAlive());

        if (!nearby.isEmpty()) {
            LivingEntity closest = nearby.get(0);
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 2, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 45, 0, true, false));
            state.setExtraBanner("😰 КТО-ТО РЯДОМ (" + nearby.size() + ")! ОТОЙДИ!");

            // Slight recoil away from the closest living creature
            Vec3 away = player.position().subtract(closest.position()).normalize().scale(0.32);
            player.setDeltaMovement(player.getDeltaMovement().add(away.x, 0.05, away.z));
            player.hurtMarked = true;

            if (activeTicks % 40 == 0) {
                level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 0.7F, 1.4F);
            }
        } else {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 0, true, false));
            state.setExtraBanner("😌 Никого вокруг — благодать");
        }
    }
}
