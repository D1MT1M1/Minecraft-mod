package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PyromaniaDisorder extends Disorder {
    public PyromaniaDisorder() {
        super("pyromania", 0xFFFF5500);
    }

    @Override
    public void onStart(ServerPlayer player, PlayerPsychoState state) {
        state.setCounter("pyro_dry", 0);
        // Give a free flint and steel if player doesn't have one so the disorder is always playable
        if (!player.getInventory().contains(new ItemStack(Items.FLINT_AND_STEEL))) {
            player.getInventory().add(new ItemStack(Items.FLINT_AND_STEEL));
        }
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 20 != 0) return;

        ServerLevel level = (ServerLevel) player.level();
        boolean nearFire = player.isOnFire()
                || player.getMainHandItem().is(Items.FLINT_AND_STEEL)
                || player.getMainHandItem().is(Items.FIRE_CHARGE)
                || hasFireNearby(level, player.blockPosition(), 6);

        if (nearFire) {
            state.setCounter("pyro_dry", 0);
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 40, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, true, false));
            state.setExtraBanner("🔥 КАЙФ ОТ ОГНЯ (Бафф активен)");
        } else {
            int drySeconds = state.incrementCounter("pyro_dry");
            if (drySeconds >= 25) {
                state.setExtraBanner("🔥 ЛОМКА! СРОЧНО ПОДЖИГАЙ ЧТО-НИБУДЬ!");
                player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 60, 0, true, false));
                if (drySeconds % 3 == 0) {
                    player.hurtServer(level, level.damageSources().magic(), 1.5F);
                }
                if (drySeconds == 25) {
                    player.sendSystemMessage(Component.literal("🔥 Руки трясутся... Нужно срочно что-нибудь сжечь!")
                            .withStyle(ChatFormatting.RED));
                }
            } else {
                state.setExtraBanner("🔥 Без огня: " + drySeconds + "/25 сек");
            }
        }
    }

    private boolean hasFireNearby(ServerLevel level, BlockPos center, int radius) {
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -2, -radius), center.offset(radius, 3, radius))) {
            BlockState bs = level.getBlockState(pos);
            if (bs.is(Blocks.FIRE) || bs.is(Blocks.SOUL_FIRE) || bs.is(Blocks.LAVA) || bs.is(Blocks.CAMPFIRE) || bs.is(Blocks.SOUL_CAMPFIRE)) {
                return true;
            }
        }
        return false;
    }
}
