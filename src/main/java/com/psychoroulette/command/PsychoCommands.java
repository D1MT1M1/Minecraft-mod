package com.psychoroulette.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.psychoroulette.config.PsychoConfig;
import com.psychoroulette.disorder.DisorderRegistry;
import com.psychoroulette.disorder.PsychoManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PsychoCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("psychoroulette")
                .then(Commands.literal("roll")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            PsychoManager.rollNewDiagnosis(player, false);
                            return 1;
                        }))
                .then(Commands.literal("combo")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            PsychoManager.rollNewDiagnosis(player, true);
                            return 1;
                        }))
                .then(Commands.literal("calm")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            PsychoManager.activateCalmMode(player);
                            return 1;
                        }))
                .then(Commands.literal("list")
                        .executes(ctx -> {
                            String all = String.join(", ", DisorderRegistry.getIds());
                            ctx.getSource().sendSuccess(() -> Component.literal("📋 Доступные диагнозы (" + DisorderRegistry.getAll().size() + "): " + all)
                                    .withStyle(ChatFormatting.AQUA), false);
                            return 1;
                        }))
                .then(Commands.literal("set")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (String id : DisorderRegistry.getIds()) {
                                        builder.suggest(id);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    String id = StringArgumentType.getString(ctx, "id");
                                    if (!PsychoManager.forceSetDisorder(player, id)) {
                                        ctx.getSource().sendFailure(Component.literal("Неизвестный диагноз: " + id));
                                        return 0;
                                    }
                                    return 1;
                                })))
                .then(Commands.literal("interval")
                        .then(Commands.argument("seconds", IntegerArgumentType.integer(10, 3600))
                                .executes(ctx -> {
                                    int seconds = IntegerArgumentType.getInteger(ctx, "seconds");
                                    PsychoConfig.cycleDurationTicks = seconds * 20;
                                    ctx.getSource().sendSuccess(() -> Component.literal("⏱ Интервал рулетки установлен на " + seconds + " сек.")
                                            .withStyle(ChatFormatting.GREEN), true);
                                    return 1;
                                })))
        );
    }
}
