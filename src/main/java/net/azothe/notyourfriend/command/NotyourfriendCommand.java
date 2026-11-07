package net.azothe.notyourfriend.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.azothe.notyourfriend.data.PlayerData;
import net.azothe.notyourfriend.data.payloads.CreateGuiParticlesPayload;
import net.azothe.notyourfriend.keymapping.ModKeyMappings;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicInteger;

import static net.azothe.notyourfriend.NotYourFriend.RANDOM;

public class NotyourfriendCommand {
    private static final SimpleCommandExceptionType ERROR_NOT_ENOUGH_PLAYERS = new SimpleCommandExceptionType(Component.translatable("commands.notyourfriend.start.not_enough_players"));
    private static final SimpleCommandExceptionType ERROR_GAME_ALREADY_STARTED = new SimpleCommandExceptionType(Component.translatable("commands.notyourfriend.start.game_already_started"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("notyourfriend")
                .then(Commands.literal("debug").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.literal("data")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.literal("infection_rate")
                                                        .then(Commands.argument("value", FloatArgumentType.floatArg())
                                                                .executes(context -> {
                                                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
                                                                        player.setData(PlayerData.INFECTION_RATE, FloatArgumentType.getFloat(context, "value"));
                                                                    }
                                                                    return 0;
                                                                })))
                                                .then(Commands.literal("infection_progress")
                                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f))
                                                                .executes(context -> {
                                                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
                                                                        player.setData(PlayerData.INFECTION_PROGRESS, FloatArgumentType.getFloat(context, "value"));
                                                                    }
                                                                    return 0;
                                                                })))
                                                .then(Commands.literal("stab_type")
                                                        .then(Commands.argument("value", IntegerArgumentType.integer(0, 4))
                                                                .executes(context -> {
                                                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
                                                                        player.setData(PlayerData.STAB_TYPE, IntegerArgumentType.getInteger(context, "value"));
                                                                    }
                                                                    return 0;
                                                                })))
                                                .then(Commands.literal("pain")
                                                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                                                .executes(context -> {
                                                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
                                                                        player.setData(PlayerData.PAIN, IntegerArgumentType.getInteger(context, "value"));
                                                                    }
                                                                    return 0;
                                                                })))
                                                .then(Commands.literal("evil")
                                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                                .executes(context -> {
                                                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
                                                                        player.setData(PlayerData.EVIL, BoolArgumentType.getBool(context, "value"));
                                                                    }
                                                                    return 0;
                                                                })))
                                                .then(Commands.literal("max_soul_energy")
                                                        .then(Commands.argument("value", IntegerArgumentType.integer(1))
                                                                .executes(context -> {
                                                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
                                                                        player.setData(PlayerData.MAX_SOUL_ENERGY, IntegerArgumentType.getInteger(context, "value"));
                                                                    }
                                                                    return 0;
                                                                })))
                                                .then(Commands.literal("soul_energy")
                                                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                                                .executes(context -> {
                                                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
                                                                        player.setData(PlayerData.SOUL_ENERGY, IntegerArgumentType.getInteger(context, "value"));
                                                                    }
                                                                    return 0;
                                                                })))
                                                .then(Commands.literal("max_health_modification")
                                                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                                                .executes(context -> {
                                                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
                                                                        player.setData(PlayerData.MAX_HEALTH_MODIFICATION, IntegerArgumentType.getInteger(context, "value"));
                                                                    }
                                                                    return 0;
                                                                })))))
                                .then(Commands.literal("query")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.literal("infection_rate")
                                                        .executes(context -> {
                                                            context.getSource().sendSystemMessage(Component.literal(String.valueOf(EntityArgument.getPlayer(context, "player").getData(PlayerData.INFECTION_RATE))));
                                                            return 0;
                                                        }))
                                                .then(Commands.literal("infection_progress")
                                                        .executes(context -> {
                                                            context.getSource().sendSystemMessage(Component.literal(String.valueOf(EntityArgument.getPlayer(context, "player").getData(PlayerData.INFECTION_PROGRESS))));
                                                            return 0;
                                                        }))
                                                .then(Commands.literal("stab_type")
                                                        .executes(context -> {
                                                            context.getSource().sendSystemMessage(Component.literal(String.valueOf(EntityArgument.getPlayer(context, "player").getData(PlayerData.STAB_TYPE))));
                                                            return 0;
                                                        }))
                                                .then(Commands.literal("pain")
                                                        .executes(context -> {
                                                            context.getSource().sendSystemMessage(Component.literal(String.valueOf(EntityArgument.getPlayer(context, "player").getData(PlayerData.PAIN))));
                                                            return 0;
                                                        }))
                                                .then(Commands.literal("max_health_modification")
                                                        .executes(context -> {
                                                            context.getSource().sendSystemMessage(Component.literal(String.valueOf(EntityArgument.getPlayer(context, "player").getData(PlayerData.MAX_HEALTH_MODIFICATION))));
                                                            return 0;
                                                        }))
                                                .then(Commands.literal("max_soul_energy")
                                                        .executes(context -> {
                                                            context.getSource().sendSystemMessage(Component.literal(String.valueOf(EntityArgument.getPlayer(context, "player").getData(PlayerData.MAX_SOUL_ENERGY))));
                                                            return 0;
                                                        }))
                                                .then(Commands.literal("soul_energy")
                                                        .executes(context -> {
                                                            context.getSource().sendSystemMessage(Component.literal(String.valueOf(EntityArgument.getPlayer(context, "player").getData(PlayerData.SOUL_ENERGY))));
                                                            return 0;
                                                        }))
                                                .then(Commands.literal("evil")
                                                        .executes(context -> {
                                                            context.getSource().sendSystemMessage(Component.literal(String.valueOf(EntityArgument.getPlayer(context, "player").getData(PlayerData.EVIL))));
                                                            return 0;
                                                        })))))
                        .then(Commands.literal("create_gui_particles")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("id", IntegerArgumentType.integer(1, 3))
                                                .then(Commands.argument("count", IntegerArgumentType.integer(1, 10000))
                                                        .executes((context -> {
                                                            for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
                                                                PacketDistributor.sendToPlayer(player, new CreateGuiParticlesPayload(IntegerArgumentType.getInteger(context, "id"), IntegerArgumentType.getInteger(context, "count")));
                                                            }
                                                            return 0;
                                                        })))))))
                .then(Commands.literal("start").requires(Commands.hasPermission(Commands.LEVEL_ALL))
                        .executes(context -> {
                            AtomicInteger playerCount = new AtomicInteger();
                            context.getSource().getServer().getPlayerList().getPlayers().forEach(player -> {
                                if (player.gameMode().isSurvival()) {
                                    playerCount.getAndIncrement();
                                }
                            });
                            if (playerCount.get() < 3) {
                                throw ERROR_NOT_ENOUGH_PLAYERS.create();
                            }
                            int evilIndex = RANDOM.nextInt(playerCount.get());
                            int currentIndex = 0;
                            for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
                                if (player.gameMode().isSurvival()) {
                                    if (player.getData(PlayerData.EVIL)) {
                                        throw ERROR_GAME_ALREADY_STARTED.create();
                                    }
                                    if (currentIndex == evilIndex) {
                                        player.setData(PlayerData.EVIL, true);
                                        player.sendSystemMessage(Component.translatable("notyourfriend.role_reveal", Component.translatable("notyourfriend.role_evil")));
                                        player.sendSystemMessage(Component.translatable("notyourfriend.role_evil_description", ModKeyMappings.ABILITY_MENU.get().getTranslatedKeyMessage()));
                                    } else {
                                        player.sendSystemMessage(Component.translatable("notyourfriend.role_reveal", Component.translatable("notyourfriend.role_chill")));
                                        player.sendSystemMessage(Component.translatable("notyourfriend.role_chill_description"));
                                    }
                                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
                                    player.teleportTo(context.getSource().getPosition().x(), context.getSource().getPosition().y(), context.getSource().getPosition().z());
                                }
                            }
                            return 0;
                        })));
    }
}
