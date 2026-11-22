package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class TouretteDisorder extends Disorder {
    private static final Random RANDOM = new Random();
    private static final String[] SHOUTS = {
            "АААААА!", "КРИПЕР!!", "БЛЯТЬ!", "КУБИКИ!", "НЕ ТРОГАЙ МОЙ СУНДУК!", "Я БАТЯ!"
    };

    public TouretteDisorder() {
        super("tourette", 0xFFFF66CC);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        int nextTic = state.getCounter("tourette_next");
        if (nextTic == 0) {
            state.setCounter("tourette_next", activeTicks + 120 + RANDOM.nextInt(140));
            return;
        }
        if (activeTicks < nextTic) return;

        state.setCounter("tourette_next", activeTicks + 120 + RANDOM.nextInt(140));
        ServerLevel level = (ServerLevel) player.level();
        int action = RANDOM.nextInt(5);

        switch (action) {
            case 0 -> {
                // Drop held item
                if (!player.getMainHandItem().isEmpty()) {
                    player.drop(false);
                    player.sendSystemMessage(Component.literal("⚡ *тик* — предмет случайно вылетел из рук!")
                            .withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            }
            case 1 -> {
                // Random jump
                if (player.onGround()) {
                    Vec3 v = player.getDeltaMovement();
                    player.setDeltaMovement(v.x, 0.52, v.z);
                    player.hurtMarked = true;
                }
            }
            case 2 -> {
                // Swing arm wildly
                player.swing(InteractionHand.MAIN_HAND, true);
                level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.4F);
            }
            case 3 -> {
                // Loud scream that aggros mobs in 32 blocks
                String shout = SHOUTS[RANDOM.nextInt(SHOUTS.length)];
                player.sendSystemMessage(Component.literal("📢 Вы непроизвольно заорали: «" + shout + "»")
                        .withStyle(ChatFormatting.YELLOW));
                level.playSound(null, player.blockPosition(), SoundEvents.GHAST_SCREAM, SoundSource.PLAYERS, 1.4F, 1.2F);
                List<Monster> mobs = level.getEntitiesOfClass(Monster.class, player.getBoundingBox().inflate(32.0));
                for (Monster mob : mobs) {
                    mob.setTarget(player);
                }
            }
            default -> {
                // Sudden head jerk
                float newYaw = player.getYRot() + (RANDOM.nextBoolean() ? 65F : -65F);
                player.teleportTo(level, player.getX(), player.getY(), player.getZ(), java.util.Set.of(), newYaw, player.getXRot(), false);
            }
        }
    }
}
