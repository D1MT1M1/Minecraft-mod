package com.psychoroulette.client;

import com.psychoroulette.network.SyncPsychoStatePayload;

public class ClientPsychoState {
    public static String primaryId = "";
    public static String secondaryId = "";
    public static int primaryColor = 0xFF33FFCC;
    public static int secondaryColor = 0xFFFF3366;
    public static int ticksRemaining = 0;
    public static int maxTicks = 6000;
    public static boolean hidden = false;
    public static boolean calm = false;
    public static boolean hideHealthHud = false;
    public static String extraBanner = "";

    public static void update(SyncPsychoStatePayload payload) {
        primaryId = payload.primaryId();
        secondaryId = payload.secondaryId();
        primaryColor = payload.primaryColor();
        secondaryColor = payload.secondaryColor();
        ticksRemaining = payload.ticksRemaining();
        maxTicks = Math.max(1, payload.maxTicks());
        hidden = payload.hidden();
        calm = payload.calm();
        hideHealthHud = payload.hideHealthHud();
        extraBanner = payload.extraBanner();
    }
}
