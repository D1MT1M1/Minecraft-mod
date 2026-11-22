package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

public class AmnesiaDisorder extends Disorder {
    private static final Random RANDOM = new Random();

    public AmnesiaDisorder() {
        super("amnesia", 0xFF88CCFF);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 220 != 0 || activeTicks == 0) return; // Every 11 seconds

        if (RANDOM.nextBoolean()) {
            // Shuffle two random hotbar slots
            int a = RANDOM.nextInt(9);
            int b = RANDOM.nextInt(9);
            if (a != b) {
                ItemStack stackA = player.getInventory().getItem(a).copy();
                ItemStack stackB = player.getInventory().getItem(b).copy();
                player.getInventory().setItem(a, stackB);
                player.getInventory().setItem(b, stackA);
                player.sendSystemMessage(Component.literal("🧠 Куда я положил свой меч?..")
                        .withStyle(ChatFormatting.AQUA));
            }
        } else {
            // Sudden 180-degree turn
            ServerLevel level = (ServerLevel) player.level();
            float flippedYaw = player.getYRot() + 180.0F;
            player.teleportTo(level, player.getX(), player.getY(), player.getZ(), java.util.Set.of(), flippedYaw, player.getXRot(), false);
            player.sendSystemMessage(Component.literal("🧠 Стоп... А зачем я вообще сюда пришёл?")
                    .withStyle(ChatFormatting.AQUA));
        }
    }
}
