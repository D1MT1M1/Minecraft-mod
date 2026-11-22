package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MegalomaniaDisorder extends Disorder {
    public MegalomaniaDisorder() {
        super("megalomania", 0xFFFFCC00);
    }

    @Override
    public void onStart(ServerPlayer player, PlayerPsychoState state) {
        player.sendSystemMessage(Component.literal("👑 Вы — абсолютное божество этого мира. Щиты и бегство — для слабаков!")
                .withStyle(ChatFormatting.GOLD));
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 10 != 0) return;

        ServerLevel level = (ServerLevel) player.level();
        // God-like power
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 40, 1, true, false));
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 40, 0, true, false));

        // Refuse shields!
        if (player.getOffhandItem().is(Items.SHIELD)) {
            ItemStack shield = player.getOffhandItem().copy();
            player.getOffhandItem().setCount(0);
            player.drop(shield, false);
            player.sendSystemMessage(Component.literal("👑 «Щиты для трусов!» — вы швырнули щит на землю.")
                    .withStyle(ChatFormatting.GOLD));
        }
        if (player.getMainHandItem().is(Items.SHIELD)) {
            player.drop(false);
            player.sendSystemMessage(Component.literal("👑 «Богу не нужен щит!»")
                    .withStyle(ChatFormatting.GOLD));
        }

        // Refuse peasant food while eating
        if (player.isUsingItem()) {
            ItemStack using = player.getUseItem();
            if (using.is(Items.BREAD) || using.is(Items.POTATO) || using.is(Items.BAKED_POTATO)
                    || using.is(Items.CARROT) || using.is(Items.ROTTEN_FLESH) || using.is(Items.BEETROOT)) {
                player.stopUsingItem();
                player.sendSystemMessage(Component.literal("👑 Фу, крестьянская еда! Божество такое не ест!")
                        .withStyle(ChatFormatting.YELLOW));
            }
        }

        // Punish turning your back on nearby monsters while sprinting away
        if (activeTicks % 40 == 0 && player.isSprinting()) {
            AABB box = player.getBoundingBox().inflate(10.0);
            List<Monster> monsters = level.getEntitiesOfClass(Monster.class, box);
            if (!monsters.isEmpty()) {
                Monster closest = monsters.get(0);
                Vec3 toMonster = closest.position().subtract(player.position()).normalize();
                Vec3 look = player.getLookAngle().normalize();
                if (look.dot(toMonster) < -0.4) {
                    player.hurtServer(level, level.damageSources().magic(), 2.0F);
                    player.sendSystemMessage(Component.literal("👑 Позор! Бог не бежит спиной от врага!")
                            .withStyle(ChatFormatting.RED));
                }
            }
        }
    }
}
