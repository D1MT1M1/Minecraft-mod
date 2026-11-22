package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class HoardingDisorder extends Disorder {
    public HoardingDisorder() {
        super("hoarding", 0xFFCCAA44);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 20 != 0) return;

        int filledSlots = 0;
        int totalItems = 0;
        for (int i = 0; i < 36; i++) {
            var stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                filledSlots++;
                totalItems += stack.getCount();
            }
        }

        int prevItems = state.getLastInventoryItemCount();
        if (prevItems > 0 && totalItems < prevItems - 2 && !player.isUsingItem()) {
            // Lost items -> emotional damage!
            ServerLevel level = (ServerLevel) player.level();
            player.hurtServer(level, level.damageSources().magic(), 1.5F);
            player.sendSystemMessage(Component.literal("🎒 Больно расставаться со своими сокровищами!")
                    .withStyle(ChatFormatting.GOLD));
        }
        state.setLastInventoryItemCount(totalItems);

        if (filledSlots >= 28) {
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 45, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 45, 0, true, false));
            state.setExtraBanner("🎒 ПОЛНЫЕ КАРМАНЫ (" + filledSlots + "/36) — СИЛА!");
        } else if (filledSlots < 15) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 45, 0, true, false));
            state.setExtraBanner("🎒 МАЛО ХЛАМА (" + filledSlots + "/36)! СОБИРАЙ ВСЁ!");
        } else {
            state.setExtraBanner("🎒 Инвентарь: " + filledSlots + "/36 слотов");
        }
    }
}
