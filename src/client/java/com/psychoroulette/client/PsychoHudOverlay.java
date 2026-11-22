package com.psychoroulette.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class PsychoHudOverlay {

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) {
            return;
        }
        if (ClientPsychoState.primaryId.isEmpty() && !ClientPsychoState.calm) {
            return;
        }

        Font font = mc.font;
        int screenW = mc.getWindow().getGuiScaledWidth();
        int boxW = 175;
        boolean hasCombo = !ClientPsychoState.secondaryId.isEmpty() && !ClientPsychoState.calm;
        boolean hasBanner = !ClientPsychoState.extraBanner.isEmpty() && !ClientPsychoState.calm && !ClientPsychoState.hidden;
        int boxH = 44 + (hasCombo ? 12 : 0) + (hasBanner ? 12 : 0);

        int x = screenW - boxW - 8;
        int y = 8;

        int accent = ClientPsychoState.calm ? 0xFF33FFCC : ClientPsychoState.primaryColor;

        // Dark brutalist translucent card
        graphics.fill(x, y, x + boxW, y + boxH, 0xCC100C18);
        // Left neon accent bar
        graphics.fill(x, y, x + 3, y + boxH, accent);
        // Top & bottom subtle borders
        graphics.fill(x, y, x + boxW, y + 1, 0x55FFFFFF);
        graphics.fill(x, y + boxH - 1, x + boxW, y + boxH, 0x55FFFFFF);

        // Header + Timer
        int secTotal = Math.max(0, ClientPsychoState.ticksRemaining / 20);
        int mins = secTotal / 60;
        int secs = secTotal % 60;
        String timeStr = String.format("%02d:%02d", mins, secs);

        Component header = hasCombo
                ? Component.translatable("psychoroulette.hud.combo")
                : Component.translatable("psychoroulette.hud.title");

        graphics.drawString(font, header, x + 8, y + 6, 0xFFBBBBCC, false);
        int timeW = font.width(timeStr);
        int timeColor = secTotal <= 10 ? 0xFFFF4455 : 0xFFFFFFFF;
        graphics.drawString(font, timeStr, x + boxW - timeW - 7, y + 6, timeColor, true);

        // Diagnosis title(s)
        int lineY = y + 18;
        if (ClientPsychoState.calm) {
            graphics.drawString(font, Component.translatable("psychoroulette.hud.calm"), x + 8, lineY, 0xFF33FFCC, true);
        } else if (ClientPsychoState.hidden) {
            graphics.drawString(font, Component.translatable("psychoroulette.hud.hidden"), x + 8, lineY, 0xFFDD66FF, true);
        } else {
            Component name1 = Component.translatable("disorder.psychoroulette." + ClientPsychoState.primaryId + ".name");
            graphics.drawString(font, name1, x + 8, lineY, accent, true);
            if (hasCombo) {
                lineY += 12;
                Component name2 = Component.translatable("disorder.psychoroulette." + ClientPsychoState.secondaryId + ".name");
                graphics.drawString(font, Component.literal("+ ").append(name2), x + 8, lineY, ClientPsychoState.secondaryColor, true);
            }
        }

        // Dynamic symptom banner
        if (hasBanner) {
            lineY += 12;
            String trimmed = font.plainSubstrByWidth(ClientPsychoState.extraBanner, boxW - 16);
            graphics.drawString(font, trimmed, x + 8, lineY, 0xFFEEDDFF, false);
        }

        // Progress bar at bottom
        int barX = x + 8;
        int barY = y + boxH - 8;
        int barW = boxW - 16;
        float ratio = Math.max(0.0F, Math.min(1.0F, (float) ClientPsychoState.ticksRemaining / (float) ClientPsychoState.maxTicks));
        graphics.fill(barX, barY, barX + barW, barY + 3, 0x55333344);
        graphics.fill(barX, barY, barX + (int) (barW * ratio), barY + 3, accent);

        // During Bipolar Mania: visually cover the player's vanilla health bar!
        if (ClientPsychoState.hideHealthHud && !mc.player.isCreative()) {
            int screenH = mc.getWindow().getGuiScaledHeight();
            int hx = screenW / 2 - 92;
            int hy = screenH - 41;
            graphics.fill(hx, hy, hx + 84, hy + 11, 0xFF140D20);
            graphics.fill(hx, hy, hx + 84, hy + 1, 0xFFFFCC00);
            graphics.fill(hx, hy + 10, hx + 84, hy + 11, 0xFFFFCC00);
            graphics.drawString(font, "∞ БЕССМЕРТЕН", hx + 7, hy + 2, 0xFFFFDD33, true);
        }
    }
}
