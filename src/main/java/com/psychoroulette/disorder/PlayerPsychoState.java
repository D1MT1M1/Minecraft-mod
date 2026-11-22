package com.psychoroulette.disorder;

import com.psychoroulette.config.PsychoConfig;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerPsychoState {
    private final List<Disorder> activeDisorders = new ArrayList<>();
    private int ticksRemaining = PsychoConfig.cycleDurationTicks;
    private int maxCycleTicks = PsychoConfig.cycleDurationTicks;
    private int activeTicks = 0;

    private int calmTicksRemaining = 0;
    private int maxCalmTicks = PsychoConfig.calmDurationTicks;

    private boolean hiddenDiagnosis = false;
    private boolean diedThisCycle = false;
    private boolean hideHealthHud = false;
    private String extraBanner = "";

    // Generic per-disorder scratchpad data
    private final Map<String, Integer> counters = new HashMap<>();
    private Vec3 lastPosition = Vec3.ZERO;
    private float lastYaw = 0.0F;
    private int lastInventoryItemCount = -1;

    public List<Disorder> getActiveDisorders() {
        return activeDisorders;
    }

    public Disorder getPrimaryDisorder() {
        return activeDisorders.isEmpty() ? null : activeDisorders.get(0);
    }

    public Disorder getSecondaryDisorder() {
        return activeDisorders.size() > 1 ? activeDisorders.get(1) : null;
    }

    public int getTicksRemaining() {
        return ticksRemaining;
    }

    public void setTicksRemaining(int ticksRemaining) {
        this.ticksRemaining = ticksRemaining;
    }

    public int getMaxCycleTicks() {
        return maxCycleTicks;
    }

    public void setMaxCycleTicks(int maxCycleTicks) {
        this.maxCycleTicks = maxCycleTicks;
    }

    public int getActiveTicks() {
        return activeTicks;
    }

    public void incrementActiveTicks() {
        this.activeTicks++;
    }

    public void resetActiveTicks() {
        this.activeTicks = 0;
    }

    public boolean isCalm() {
        return calmTicksRemaining > 0;
    }

    public int getCalmTicksRemaining() {
        return calmTicksRemaining;
    }

    public void setCalmTicksRemaining(int calmTicksRemaining) {
        this.calmTicksRemaining = calmTicksRemaining;
        this.maxCalmTicks = Math.max(1, calmTicksRemaining);
    }

    public int getMaxCalmTicks() {
        return maxCalmTicks;
    }

    public boolean isHiddenDiagnosis() {
        return hiddenDiagnosis;
    }

    public void setHiddenDiagnosis(boolean hiddenDiagnosis) {
        this.hiddenDiagnosis = hiddenDiagnosis;
    }

    public boolean hasDiedThisCycle() {
        return diedThisCycle;
    }

    public void setDiedThisCycle(boolean diedThisCycle) {
        this.diedThisCycle = diedThisCycle;
    }

    public boolean isHideHealthHud() {
        return hideHealthHud;
    }

    public void setHideHealthHud(boolean hideHealthHud) {
        this.hideHealthHud = hideHealthHud;
    }

    public String getExtraBanner() {
        return extraBanner;
    }

    public void setExtraBanner(String extraBanner) {
        this.extraBanner = extraBanner == null ? "" : extraBanner;
    }

    public int getCounter(String key) {
        return counters.getOrDefault(key, 0);
    }

    public void setCounter(String key, int value) {
        counters.put(key, value);
    }

    public int incrementCounter(String key) {
        int next = getCounter(key) + 1;
        counters.put(key, next);
        return next;
    }

    public void clearScratchpad() {
        counters.clear();
        hideHealthHud = false;
        extraBanner = "";
        lastInventoryItemCount = -1;
    }

    public Vec3 getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Vec3 lastPosition) {
        this.lastPosition = lastPosition;
    }

    public float getLastYaw() {
        return lastYaw;
    }

    public void setLastYaw(float lastYaw) {
        this.lastYaw = lastYaw;
    }

    public int getLastInventoryItemCount() {
        return lastInventoryItemCount;
    }

    public void setLastInventoryItemCount(int count) {
        this.lastInventoryItemCount = count;
    }
}
