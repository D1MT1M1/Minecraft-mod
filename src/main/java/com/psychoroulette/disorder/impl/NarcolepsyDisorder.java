package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.Random;

public class NarcolepsyDisorder extends Disorder {
    private static final Random RANDOM = new Random();

    public NarcolepsyDisorder() {
        super("narcolepsy", 0xFF6666FF);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        int nextSleep = state.getCounter("narco_next");
        if (nextSleep == 0) {
            state.setCounter("narco_next", activeTicks + 260 + RANDOM.nextInt(260));
            return;
        }
        if (activeTicks < nextSleep) return;

        state.setCounter("narco_next", activeTicks + 300 + RANDOM.nextInt(280));
        ServerLevel level = (ServerLevel) player.level();
        int duration = 65; // ~3.25 seconds

        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, 1, true, false));
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, duration, 1, true, false));
        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, duration, 6, true, false));
        player.setSprinting(false);

        level.playSound(null, player.blockPosition(), SoundEvents.PANDA_SNEEZE, SoundSource.PLAYERS, 0.7F, 0.5F);
        player.sendSystemMessage(Component.literal("💤 Zzz... Вы внезапно отрубились прямо на ходу!")
                .withStyle(ChatFormatting.BLUE));
    }
}
