package com.psychoroulette.disorder.impl;

import com.psychoroulette.disorder.Disorder;
import com.psychoroulette.disorder.PlayerPsychoState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class HallucinationsDisorder extends Disorder {
    private static final Random RANDOM = new Random();
    private static final String[] FAKE_CHATS = {
            "Herobrine присоединился к игре",
            "Ваш спавнпоинт был разрушен.",
            "<Неизвестный> Не оборачивайся.",
            "Кто-то открыл ваш сундук неподалёку...",
            "<Крипер> Шшшшшшш..."
    };

    public HallucinationsDisorder() {
        super("hallucinations", 0xFFCC00FF);
    }

    @Override
    public void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks) {
        if (activeTicks % 85 != 0) return;

        ServerLevel level = (ServerLevel) player.level();
        Vec3 behind = player.position().subtract(player.getLookAngle().scale(2.5));
        int glitch = RANDOM.nextInt(5);

        switch (glitch) {
            case 0 -> {
                // Fake creeper fuse behind the player!
                level.playSound(null, behind.x, behind.y, behind.z, SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE, 1.2F, 1.0F);
            }
            case 1 -> {
                // Fake explosion flash & particles with zero damage
                level.sendParticles(player, ParticleTypes.EXPLOSION_EMITTER, true, false, behind.x, behind.y + 1, behind.z, 1, 0, 0, 0, 0);
                level.playSound(null, behind.x, behind.y, behind.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 0.7F, 1.1F);
            }
            case 2 -> {
                // Fake chest opening right behind you
                level.playSound(null, behind.x, behind.y, behind.z, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            case 3 -> {
                // Fake Enderman stare sound
                level.playSound(null, behind.x, behind.y, behind.z, SoundEvents.ENDERMAN_STARE, SoundSource.HOSTILE, 0.9F, 0.9F);
            }
            default -> {
                // Cursed chat gaslighting
                String msg = FAKE_CHATS[RANDOM.nextInt(FAKE_CHATS.length)];
                player.sendSystemMessage(Component.literal(msg).withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
