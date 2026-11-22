package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class ClaustrophobiaDisorder extends Disorder {
    public ClaustrophobiaDisorder() {
        super("claustrophobia", 0xFFAA5533);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 20 != 0) return;

        ServerLevel level = (ServerLevel) player.level();
        BlockPos pos = player.blockPosition();

        boolean lowCeiling = !level.isEmptyBlock(pos.above(2)) || !level.isEmptyBlock(pos.above(3));
        int walls = 0;
        if (!level.isEmptyBlock(pos.north().above())) walls++;
        if (!level.isEmptyBlock(pos.south().above())) walls++;
        if (!level.isEmptyBlock(pos.east().above())) walls++;
        if (!level.isEmptyBlock(pos.west().above())) walls++;

        if (lowCeiling && walls >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 60, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1, true, false));
            state.setExtraBanner("🧱 СТЕНЫ СЖИМАЮТСЯ! ВЫЙДИ НА ПРОСТОР!");
            if (activeTicks % 60 == 0) {
                player.hurtServer(level, level.damageSources().inWall(), 1.0F);
            }
        } else {
            state.setExtraBanner("🌿 Простор — дышится легко");
        }
    }
}
