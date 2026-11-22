package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

public class OcdDisorder extends Disorder {
    public OcdDisorder() {
        super("ocd", 0xFFDDDDFF);
    }

    @Override
    public void onStart(ServerPlayer player, PlayerPsychoState state) {
        state.setCounter("ocd_need_crouches", 3);
        state.setCounter("ocd_was_crouching", 0);
        state.setCounter("ocd_timer", 20 * 20);
        player.sendSystemMessage(Component.literal("📐 [ОКР] Навязчивый ритуал: ПРИСЯДЬ 3 РАЗА прямо сейчас, иначе накроет тревога!")
                .withStyle(ChatFormatting.WHITE));
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        // Track crouch ritual
        int needed = state.getCounter("ocd_need_crouches");
        boolean crouching = player.isShiftKeyDown();
        boolean wasCrouching = state.getCounter("ocd_was_crouching") == 1;

        if (needed > 0) {
            if (crouching && !wasCrouching) {
                needed--;
                state.setCounter("ocd_need_crouches", needed);
                player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 1.6F);
                if (needed == 0) {
                    player.sendSystemMessage(Component.literal("📐 [ОКР] Ритуал выполнен! Идеальная симметрия (+Спешка I).")
                            .withStyle(ChatFormatting.GREEN));
                    player.addEffect(new MobEffectInstance(MobEffects.HASTE, 200, 0, true, false));
                    state.setCounter("ocd_timer", 20 * 25);
                }
            }
            state.setExtraBanner("📐 РИТУАЛ: присядь ещё " + needed + " раз(а)!");
        } else {
            int nextTimer = state.getCounter("ocd_timer") - 1;
            state.setCounter("ocd_timer", nextTimer);
            if (nextTimer <= 0) {
                state.setCounter("ocd_need_crouches", 3);
                player.sendSystemMessage(Component.literal("📐 [ОКР] Что-то лежит неровно... ПРИСЯДЬ 3 РАЗА!")
                        .withStyle(ChatFormatting.YELLOW));
            }
        }
        state.setCounter("ocd_was_crouching", crouching ? 1 : 0);

        // Odd stack size in hand triggers discomfort
        if (activeTicks % 20 == 0) {
            ItemStack main = player.getMainHandItem();
            if (!main.isEmpty() && main.getMaxStackSize() > 1 && main.getCount() % 2 != 0) {
                player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 40, 0, true, false));
                if (needed == 0) {
                    state.setExtraBanner("📐 НЕЧЁТНОЕ ЧИСЛО В РУКЕ (" + main.getCount() + ")! БЕСИТ!");
                }
            } else if (needed == 0) {
                state.setExtraBanner("📐 Всё ровно и чётно");
            }
        }
    }
}
