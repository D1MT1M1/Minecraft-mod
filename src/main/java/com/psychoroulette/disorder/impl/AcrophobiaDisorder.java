package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class AcrophobiaDisorder extends Disorder {
    public AcrophobiaDisorder() {
        super("acrophobia", 0xFF55DDFF);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 20 != 0) return;

        ServerLevel level = (ServerLevel) player.level();
        int dropDepth = measureDropNearby(level, player.blockPosition());

        if (dropDepth >= 5) {
            player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 60, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, true, false));
            state.setExtraBanner("🏔 ВЫСОКО! КРУЖИТСЯ ГОЛОВА!");
        } else {
            state.setExtraBanner("🌍 Надёжная земля под ногами");
        }
    }

    private int measureDropNearby(ServerLevel level, BlockPos pos) {
        int maxDrop = 0;
        BlockPos[] checks = {pos, pos.north(), pos.south(), pos.east(), pos.west()};
        for (BlockPos check : checks) {
            int depth = 0;
            for (int y = 1; y <= 7; y++) {
                if (level.isEmptyBlock(check.below(y))) {
                    depth++;
                } else {
                    break;
                }
            }
            maxDrop = Math.max(maxDrop, depth);
        }
        return maxDrop;
    }
}
