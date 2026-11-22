package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.Random;

public class KleptomaniaDisorder extends Disorder {
    private static final Random RANDOM = new Random();

    public KleptomaniaDisorder() {
        super("kleptomania", 0xFF00CC88);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 60 != 0) return;

        ServerLevel level = (ServerLevel) player.level();
        BlockPos center = player.blockPosition();

        // Try stealing from nearby chests/barrels/containers
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-3, -2, -3), center.offset(3, 2, 3))) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof Container container) {
                for (int i = 0; i < container.getContainerSize(); i++) {
                    ItemStack stack = container.getItem(i);
                    if (!stack.isEmpty()) {
                        ItemStack stolen = stack.split(1);
                        container.setChanged();
                        if (!player.getInventory().add(stolen)) {
                            player.drop(stolen, false);
                        }
                        level.playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.8F, 1.4F);
                        player.sendSystemMessage(Component.literal("🧤 Ваши руки сами стырили: ")
                                .withStyle(ChatFormatting.GREEN)
                                .append(stolen.getHoverName().copy().withStyle(ChatFormatting.YELLOW)));
                        return;
                    }
                }
            }
        }

        // Or pickpocket a nearby villager
        List<LivingEntity> villagers = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(4.0),
                e -> e.getType() == EntityType.VILLAGER && e.isAlive());
        if (!villagers.isEmpty() && RANDOM.nextFloat() < 0.45F) {
            ItemStack loot = RANDOM.nextBoolean() ? new ItemStack(Items.EMERALD) : new ItemStack(Items.BREAD);
            player.getInventory().add(loot);
            level.playSound(null, player.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 0.9F, 1.1F);
            player.sendSystemMessage(Component.literal("🧤 Вы ловко обчистили карман жителя!")
                    .withStyle(ChatFormatting.GREEN));
        }
    }
}
