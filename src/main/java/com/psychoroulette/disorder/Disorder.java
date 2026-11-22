package com.psychoroulette.disorder;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public abstract class Disorder {
    private final String id;
    private final int color;

    protected Disorder(String id, int color) {
        this.id = id;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public Component getDisplayName() {
        return Component.translatable("disorder.psychoroulette." + id + ".name");
    }

    public Component getDescription() {
        return Component.translatable("disorder.psychoroulette." + id + ".desc");
    }

    /** Called once when the disorder is assigned to the player. */
    public void onStart(ServerPlayer player, PlayerPsychoState state) {}

    /** Called every server tick (20 times/sec) while active. */
    public abstract void onTick(ServerPlayer player, PlayerPsychoState state, int activeTicks);

    /** Called once when the disorder ends or is replaced. */
    public void onEnd(ServerPlayer player, PlayerPsychoState state) {}

    /** Called when the player attacks an entity. */
    public void onAttack(ServerPlayer player, Entity target, PlayerPsychoState state) {}
}
