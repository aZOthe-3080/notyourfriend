package net.azothe.notyourfriend.event;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.data.ClientPayloadHandler;
import net.azothe.notyourfriend.data.PlayerData;
import net.azothe.notyourfriend.data.payloads.CreateGuiParticles;
import net.azothe.notyourfriend.guiparticle.GuiParticle;
import net.azothe.notyourfriend.guiparticle.guiparticleclass.BloodGuiParticle;
import net.azothe.notyourfriend.item.ModItems;
import net.azothe.notyourfriend.item.itemclass.ShardItem;
import net.azothe.notyourfriend.item.itemclass.ThornItem;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static net.azothe.notyourfriend.NotYourFriend.RANDOM;

@EventBusSubscriber(modid = NotYourFriend.MOD_ID)
public class EventHandler {
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        event.getServer().getPlayerList().getPlayers().forEach(player -> {
            if (player.getData(PlayerData.INFECTION_RATE) > 0f && (int)(RANDOM.nextFloat(2500f) / player.getData(PlayerData.INFECTION_RATE)) == 0) {
                player.setData(PlayerData.INFECTION_PROGRESS, player.getData(PlayerData.INFECTION_PROGRESS) + RANDOM.nextFloat(0.1f, 0.35f));
            }
            if (player.getData(PlayerData.STAB_TYPE) == 0 && player.getData(PlayerData.INFECTION_RATE) <= 0 && RANDOM.nextInt(2500) == 0) {
                player.setData(PlayerData.INFECTION_PROGRESS, player.getData(PlayerData.INFECTION_PROGRESS) - RANDOM.nextFloat(0.2f, 0.5f));
            }
            if (player.getData(PlayerData.INFECTION_PROGRESS) > Math.min((player.getMaxHealth() - player.getAbsorptionAmount()) / 2f, 20f)) {
                player.setData(PlayerData.INFECTION_PROGRESS, Math.min((player.getMaxHealth() - player.getAbsorptionAmount()) / 2f, 20f));
            } else if (player.getData(PlayerData.INFECTION_PROGRESS) < 0f) {
                player.setData(PlayerData.INFECTION_PROGRESS, 0f);
            }
            if (player.getData(PlayerData.PAIN) > 0) {
                player.setData(PlayerData.PAIN, (int)Math.floor(player.getData(PlayerData.PAIN) * 0.95));
            }
            if (player.gameMode.isSurvival()) {
                player.getAttributes().getInstance(Attributes.MAX_HEALTH).addOrReplacePermanentModifier(new AttributeModifier(NotYourFriend.id("health_breaking"), -2f * (float)player.getData(PlayerData.MAX_HEALTH_REDUCTION), AttributeModifier.Operation.ADD_VALUE));
            }
            if (player.getData(PlayerData.STAB_TYPE) == 2 && RANDOM.nextInt(6000) == 0) {
                PacketDistributor.sendToPlayer(player, new CreateGuiParticles(3, RANDOM.nextInt(1, 4)));
                player.setData(PlayerData.PAIN, player.getData(PlayerData.PAIN) + 40);
                if (player.getHealth() > player.getMaxHealth() * 0.77 && player.getHealth() - 1f > 2f * Math.floor(player.getData(PlayerData.INFECTION_PROGRESS))) {
                    player.hurtServer(player.level(), player.damageSources().magic(), 1f);
                }
            } else if (player.getData(PlayerData.STAB_TYPE) == 3 && RANDOM.nextInt(3600) == 0) {
                PacketDistributor.sendToPlayer(player, new CreateGuiParticles(3, RANDOM.nextInt(1, 4)));
                player.setData(PlayerData.PAIN, player.getData(PlayerData.PAIN) + 40);
                if (player.getHealth() > player.getMaxHealth() * 0.55 && player.getHealth() - 1f > 2f * Math.floor(player.getData(PlayerData.INFECTION_PROGRESS))) {
                    player.hurtServer(player.level(), player.damageSources().magic(), 1f);
                }
            } else if (player.getData(PlayerData.STAB_TYPE) == 4 && RANDOM.nextInt(2400) == 0) {
                PacketDistributor.sendToPlayer(player, new CreateGuiParticles(3, RANDOM.nextInt(1, 4)));
                player.setData(PlayerData.PAIN, player.getData(PlayerData.PAIN) + 40);
                if (player.getHealth() > player.getMaxHealth() * 0.33 && player.getHealth() - 1f > 2f * Math.floor(player.getData(PlayerData.INFECTION_PROGRESS))) {
                    player.hurtServer(player.level(), player.damageSources().magic(), 1f);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide()) {
            if (player.getData(PlayerData.INFECTION_PROGRESS) >= 1f && player.getHealth() - event.getAmount() < 2f * player.getData(PlayerData.INFECTION_PROGRESS) && event.getAmount() < 9999) {
                if (player.invulnerableTime <= 10) {
                    player.setData(PlayerData.INFECTION_PROGRESS, player.getData(PlayerData.INFECTION_PROGRESS) - 1f);
                    PacketDistributor.sendToPlayer((ServerPlayer)player, new CreateGuiParticles(2, 15));
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SLIME_HURT, SoundSource.PLAYERS);
                    player.setData(PlayerData.MAX_HEALTH_REDUCTION, player.getData(PlayerData.MAX_HEALTH_REDUCTION) + 1);
                }
                player.setHealth(Math.min(player.getHealth(), Mth.floor(player.getData(PlayerData.INFECTION_PROGRESS)) * 2f) + 1f);
                event.setAmount(1f);
            }
            if (player.getHealth() - event.getAmount() <= 0f && player.getData(PlayerData.STAB_TYPE) != 0) {
                switch (player.getData(PlayerData.STAB_TYPE)) {
                    case 1 -> player.drop(ModItems.SHARD.toStack(), true, false);
                    case 2 -> player.drop(ModItems.THORN.toStack(), true, false);
                    case 3 -> player.drop(new ItemStack(ModItems.THORN.get(), 2), true, false);
                    case 4 -> player.drop(new ItemStack(ModItems.THORN.get(), 3), true, false);
                }
                player.setData(PlayerData.STAB_TYPE, 0);
                player.setData(PlayerData.INFECTION_RATE, 0f);
                player.setData(PlayerData.INFECTION_PROGRESS, 0f);
                player.setHealth(1f);
                event.setAmount(1f);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (player.getData(PlayerData.PARTICLE_CREATION).id() != 0) {
            for (int i = 0; i < player.getData(PlayerData.PARTICLE_CREATION).count(); i++) {
                switch (player.getData(PlayerData.PARTICLE_CREATION).id()) {
                    case 1 -> new BloodGuiParticle(event.getGuiGraphics(), Math.min(1f, player.getData(PlayerData.INFECTION_PROGRESS)), 0, 1);
                    case 2 -> new BloodGuiParticle(event.getGuiGraphics(), 1f, (int)Math.floor(player.getData(PlayerData.INFECTION_PROGRESS)), 2);
                    case 3 -> new BloodGuiParticle(event.getGuiGraphics(), Math.min(1f, player.getData(PlayerData.INFECTION_PROGRESS)), 0, 0);
                    default -> {}
                }
            }
            player.setData(PlayerData.PARTICLE_CREATION, new CreateGuiParticles(0, 0));
        }
        GuiParticle.tickParticles(event.getGuiGraphics(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                CreateGuiParticles.TYPE,
                CreateGuiParticles.STREAM_CODEC,
                ClientPayloadHandler::onParticleCreation
        );
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (2f * (float)player.getData(PlayerData.MAX_HEALTH_REDUCTION) >= player.getMaxHealth() && !player.level().isClientSide()) {
            ((ServerPlayer)player).setGameMode(GameType.SPECTATOR);
            player.setData(PlayerData.MAX_HEALTH_REDUCTION, 0);
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("notyourfriend").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.literal("data")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.literal("infection_rate")
                                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f))
                                                                .executes(context -> {
                                                                    EntityArgument.getPlayers(context, "targets").forEach(player -> player.setData(PlayerData.INFECTION_RATE, FloatArgumentType.getFloat(context, "value")));
                                                                    return 0;
                                                                })))
                                                .then(Commands.literal("infection_progress")
                                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f))
                                                                .executes(context -> {
                                                                    EntityArgument.getPlayers(context, "targets").forEach(player -> player.setData(PlayerData.INFECTION_PROGRESS, FloatArgumentType.getFloat(context, "value")));
                                                                    return 0;
                                                                })))
                                                .then(Commands.literal("stab_type")
                                                        .then(Commands.argument("value", IntegerArgumentType.integer(0, 4))
                                                                .executes(context -> {
                                                                    EntityArgument.getPlayers(context, "targets").forEach(player -> player.setData(PlayerData.STAB_TYPE, IntegerArgumentType.getInteger(context, "value")));
                                                                    return 0;
                                                                })))
                                                .then(Commands.literal("pain")
                                                        .then(Commands.argument("value", IntegerArgumentType.integer(1))
                                                                .executes(context -> {
                                                                    EntityArgument.getPlayers(context, "targets").forEach(player -> player.setData(PlayerData.PAIN, IntegerArgumentType.getInteger(context, "value")));
                                                                    return 0;
                                                                })))
                                                .then(Commands.literal("max_health_reduction")
                                                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                                                .executes(context -> {
                                                                    EntityArgument.getPlayers(context, "targets").forEach(player -> player.setData(PlayerData.MAX_HEALTH_REDUCTION, IntegerArgumentType.getInteger(context, "value")));
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
                                                .then(Commands.literal("max_health_reduction")
                                                        .executes(context -> {
                                                            context.getSource().sendSystemMessage(Component.literal(String.valueOf(EntityArgument.getPlayer(context, "player").getData(PlayerData.MAX_HEALTH_REDUCTION))));
                                                            return 0;
                                                        })))))
                        .then(Commands.literal("create_gui_particles")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("id", IntegerArgumentType.integer(1, 3))
                                                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                                        .executes((context -> {
                                                            EntityArgument.getPlayers(context, "targets").forEach(player -> PacketDistributor.sendToPlayer(player, new CreateGuiParticles(IntegerArgumentType.getInteger(context, "id"), IntegerArgumentType.getInteger(context, "count"))));
                                                            return 0;
                                                        }))))))
                        .then(Commands.literal("simulate_stab")
                                .then(Commands.argument("players", EntityArgument.players())
                                        .then(Commands.literal("shard")
                                                .executes(context -> {
                                                    EntityArgument.getPlayers(context, "players").forEach(player -> {
                                                        ShardItem.onItemUse(player.level(), player, InteractionHand.MAIN_HAND);
                                                    });
                                                    return 0;
                                                }))
                                        .then(Commands.literal("thorn")
                                                .executes(context -> {
                                                    EntityArgument.getPlayers(context, "players").forEach(player -> {
                                                        ThornItem.onItemUse(player.level(), player, InteractionHand.MAIN_HAND);
                                                    });
                                                    return 0;
                                                })))));
    }
}
