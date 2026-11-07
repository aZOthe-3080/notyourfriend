package net.azothe.notyourfriend.event;

import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.command.NotyourfriendCommand;
import net.azothe.notyourfriend.data.PayloadHandler;
import net.azothe.notyourfriend.data.PlayerData;
import net.azothe.notyourfriend.data.payloads.CreateGuiParticlesPayload;
import net.azothe.notyourfriend.data.datagen.ModModelProvider;
import net.azothe.notyourfriend.data.payloads.MouseStabPayload;
import net.azothe.notyourfriend.data.payloads.PlayerHurtPayload;
import net.azothe.notyourfriend.data.payloads.PlayerSoundPayload;
import net.azothe.notyourfriend.data.payloads.StabRemovedPayload;
import net.azothe.notyourfriend.guiparticle.GuiParticle;
import net.azothe.notyourfriend.guiparticle.guiparticleclass.BloodGuiParticle;
import net.azothe.notyourfriend.item.ModItems;
import net.azothe.notyourfriend.item.items.ShardItem;
import net.azothe.notyourfriend.item.items.ThornItem;
import net.azothe.notyourfriend.keymapping.ModKeyMappingCategories;
import net.azothe.notyourfriend.keymapping.ModKeyMappings;
import net.azothe.notyourfriend.screen.AbilityScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
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
    private static final Identifier HEART_AMOUNT_MODIFIER = NotYourFriend.id("heart_amount_modifier");
    private static final Identifier SOUL_ENERGY_BAR_BG = NotYourFriend.id("hud/soul_energy_bar_background");
    private static final Identifier SOUL_ENERGY_BAR = NotYourFriend.id("hud/soul_energy_bar");
    private static final Identifier SOUL_ICON = Identifier.withDefaultNamespace("textures/particle/soul_9.png");

    @SubscribeEvent
    public static void onServerTickPost(ServerTickEvent.Post event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (player.getData(PlayerData.INFECTION_RATE) > 0f &&
                    (int)(RANDOM.nextFloat(2500f) / player.getData(PlayerData.INFECTION_RATE)) == 0 &&
                    player.getAbsorptionAmount() <= 0) {
                player.setData(PlayerData.INFECTION_PROGRESS, player.getData(PlayerData.INFECTION_PROGRESS) + RANDOM.nextFloat(0.1f, 0.35f)); // increase infection
            }
            if (player.getData(PlayerData.STAB_TYPE) == 0 &&
                    player.getData(PlayerData.INFECTION_RATE) <= 0 &&
                    (int)(RANDOM.nextFloat(2500f) / -(player.getData(PlayerData.INFECTION_RATE) - 1f)) == 0) {
                player.setData(PlayerData.INFECTION_PROGRESS, player.getData(PlayerData.INFECTION_PROGRESS) - RANDOM.nextFloat(0.2f, 0.5f)); // reduce infection when not stabbed
            }
            if (player.getData(PlayerData.INFECTION_PROGRESS) > 0f && player.getAbsorptionAmount() > 0f) { // use absorption to reduce infection
                float oldInfection = player.getData(PlayerData.INFECTION_PROGRESS);
                player.setData(PlayerData.INFECTION_PROGRESS, Math.max(oldInfection - player.getAbsorptionAmount(), 0));
                player.setAbsorptionAmount(player.getAbsorptionAmount() - (oldInfection - player.getData(PlayerData.INFECTION_PROGRESS)));
                player.level().playSound(null, player, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 0.25f, 1.5f);
            }
            if (player.getData(PlayerData.INFECTION_PROGRESS) > (player.getMaxHealth()) / 2f) { // infection upper cap
                player.setData(PlayerData.INFECTION_PROGRESS, (player.getMaxHealth()) / 2f);
            } else if (player.getData(PlayerData.INFECTION_PROGRESS) < 0f) { // infection lower cap
                player.setData(PlayerData.INFECTION_PROGRESS, 0f);
            }
            if (player.getData(PlayerData.SOUL_ENERGY) > player.getData(PlayerData.MAX_SOUL_ENERGY)) { // soul power upper cap
                player.setData(PlayerData.SOUL_ENERGY, player.getData(PlayerData.MAX_SOUL_ENERGY));
            } else if (player.getData(PlayerData.SOUL_ENERGY) < 0) { // soul power lower cap
                player.setData(PlayerData.SOUL_ENERGY, 0);
            }
            if (player.getData(PlayerData.PAIN) > 0) { // passively decrease pain
                player.setData(PlayerData.PAIN, (int)Math.floor(player.getData(PlayerData.PAIN) * 0.95f));
            }
            if (player.gameMode.isSurvival()) { // applying max health reduction attribute modifier
                player.getAttributes().getInstance(Attributes.MAX_HEALTH).addOrReplacePermanentModifier(new AttributeModifier(HEART_AMOUNT_MODIFIER, 2f * (float)player.getData(PlayerData.MAX_HEALTH_MODIFICATION), AttributeModifier.Operation.ADD_VALUE));
            }
            if (player.getData(PlayerData.STAB_TYPE) == 2 && RANDOM.nextInt(6000) == 0) { // 1x thorn bleeding
                PacketDistributor.sendToPlayer(player, new CreateGuiParticlesPayload(3, RANDOM.nextInt(1, 4)));
                player.setData(PlayerData.PAIN, player.getData(PlayerData.PAIN) + 40);
                if (player.getHealth() > player.getMaxHealth() * 0.75 && player.getHealth() - 1f > 2f * Math.floor(player.getData(PlayerData.INFECTION_PROGRESS))) {
                    player.hurtServer(player.level(), player.damageSources().magic(), 1f);
                }
            } else if (player.getData(PlayerData.STAB_TYPE) == 3 && RANDOM.nextInt(3600) == 0) { // 2x thorn bleeding
                PacketDistributor.sendToPlayer(player, new CreateGuiParticlesPayload(3, RANDOM.nextInt(1, 4)));
                player.setData(PlayerData.PAIN, player.getData(PlayerData.PAIN) + 40);
                if (player.getHealth() > player.getMaxHealth() * 0.5 && player.getHealth() - 1f > 2f * Math.floor(player.getData(PlayerData.INFECTION_PROGRESS))) {
                    player.hurtServer(player.level(), player.damageSources().magic(), 1f);
                }
            } else if (player.getData(PlayerData.STAB_TYPE) == 4 && RANDOM.nextInt(2400) == 0) { // 3x thorn bleeding
                PacketDistributor.sendToPlayer(player, new CreateGuiParticlesPayload(3, RANDOM.nextInt(1, 4)));
                player.setData(PlayerData.PAIN, player.getData(PlayerData.PAIN) + 40);
                if (player.getHealth() > player.getMaxHealth() * 0.33 && player.getHealth() - 1f > 2f * Math.floor(player.getData(PlayerData.INFECTION_PROGRESS))) {
                    player.hurtServer(player.level(), player.damageSources().magic(), 1f);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide()) {
            if (event.getSource().getDirectEntity() instanceof Player source) { // hitting another player stabs them
                if (event.getSource().getDirectEntity().getWeaponItem().getItem() instanceof ShardItem) {
                    ShardItem.stab(player.level(), player, source, false);
                } else if (event.getSource().getDirectEntity().getWeaponItem().getItem() instanceof ThornItem) {
                    ThornItem.stab(player.level(), player, source, false);
                }
            }
            if (player.getData(PlayerData.INFECTION_PROGRESS) >= 1f && // if the player has an infected heart
                    player.getHealth() - event.getAmount() < 2f * player.getData(PlayerData.INFECTION_PROGRESS) && // if the damage will reach the brittle heart
                    event.getSource() != player.damageSources().genericKill()) { // ignore /kill
                if (player.getInvulnerableTime() <= 10) { // if the player can be damaged
                    player.setData(PlayerData.INFECTION_PROGRESS, player.getData(PlayerData.INFECTION_PROGRESS) - 1); // reduce infection by 1
                    PacketDistributor.sendToPlayer((ServerPlayer)player, new CreateGuiParticlesPayload(2, 15)); // create particles
                    player.level().playSound(null, player, SoundEvents.SLIME_HURT, SoundSource.PLAYERS, 1, 1); // heart pop sound
                    player.setData(PlayerData.MAX_HEALTH_MODIFICATION, player.getData(PlayerData.MAX_HEALTH_MODIFICATION) - 1); // remove 1 heart
                }
                player.setHealth(Math.min(player.getHealth(), Mth.floor(player.getData(PlayerData.INFECTION_PROGRESS)) * 2f) + 1f);
                event.setAmount(1f);
            }
            if (player.getHealth() - event.getAmount() <= 0f) { // drop stab item on death
                ItemEntity item = null;
                switch (player.getData(PlayerData.STAB_TYPE)) {
                    case 1 -> item = player.createItemStackToDrop(ModItems.SHARD.toStack(), true, false);
                    case 2 -> item = player.createItemStackToDrop(ModItems.THORN.toStack(), true, false);
                    case 3 -> item = player.createItemStackToDrop(ModItems.THORN.toStack(2), true, false);
                    case 4 -> item = player.createItemStackToDrop(ModItems.THORN.toStack(3), true, false);
                }
                if (item != null) {
                    player.level().addFreshEntity(item);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        if (player.getData(PlayerData.PARTICLE_CREATION).id() != 0) {
            for (int i = 0; i < player.getData(PlayerData.PARTICLE_CREATION).count(); i++) {
                switch (player.getData(PlayerData.PARTICLE_CREATION).id()) {
                    case 1 -> new BloodGuiParticle(event.getGuiGraphics(), Math.min(1f, player.getData(PlayerData.INFECTION_PROGRESS)), 0, 1);
                    case 2 -> new BloodGuiParticle(event.getGuiGraphics(), 1f, (int)Math.floor(player.getData(PlayerData.INFECTION_PROGRESS)), 2);
                    case 3 -> new BloodGuiParticle(event.getGuiGraphics(), Math.min(1f, player.getData(PlayerData.INFECTION_PROGRESS)), 0, 0);
                    default -> {}
                }
            }
            player.setData(PlayerData.PARTICLE_CREATION, new CreateGuiParticlesPayload(0, 0));
        }
        GuiParticle.extractParticles(graphics);
        if (player.getData(PlayerData.EVIL)) {
            int actualSoulEnergy = Math.min(player.getData(PlayerData.SOUL_ENERGY), player.getData(PlayerData.MAX_SOUL_ENERGY));
            int bgHeight = (int)Math.sqrt(player.getData(PlayerData.MAX_SOUL_ENERGY)) + 4;
            int bgY = graphics.guiHeight() - 17 - (int)Math.sqrt(player.getData(PlayerData.MAX_SOUL_ENERGY));
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SOUL_ENERGY_BAR_BG, 3, bgY - 10,
                    16, bgHeight);
            int barHeight = (int)((Math.sqrt(player.getData(PlayerData.MAX_SOUL_ENERGY)) + 1) * actualSoulEnergy / player.getData(PlayerData.MAX_SOUL_ENERGY));
            int barY = graphics.guiHeight() - 15 - barHeight;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SOUL_ENERGY_BAR, 5, barY - 10,
                    12, barHeight);
            graphics.blit(RenderPipelines.GUI_TEXTURED, SOUL_ICON, 2, graphics.guiHeight() - 20, 5, 0, 5, 6, 16, 16);
            graphics.text(Minecraft.getInstance().font, actualSoulEnergy + "/" + player.getData(PlayerData.MAX_SOUL_ENERGY), 8, graphics.guiHeight() - 21, 0xFFFFFFFF);
        }
    }

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(NotYourFriend.MOD_ID);
        registrar.playToClient(
                CreateGuiParticlesPayload.TYPE,
                CreateGuiParticlesPayload.STREAM_CODEC,
                PayloadHandler.Client::onCreateGuiParticles
        );
        registrar.playToServer(
                MouseStabPayload.TYPE,
                MouseStabPayload.STREAM_CODEC,
                PayloadHandler.Server::onMouseStab
        );
        registrar.playToServer(
                StabRemovedPayload.TYPE,
                StabRemovedPayload.STREAM_CODEC,
                PayloadHandler.Server::onStabRemoved
        );
        registrar.playToServer(
                PlayerSoundPayload.TYPE,
                PlayerSoundPayload.STREAM_CODEC,
                PayloadHandler.Server::onPlayerSound
        );
        registrar.playToServer(
                PlayerHurtPayload.TYPE,
                PlayerHurtPayload.STREAM_CODEC,
                PayloadHandler.Server::onPlayerHurt
        );
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (player.getAttributes().getValue(Attributes.MAX_HEALTH) <= 1f && !player.level().isClientSide()) {
            ((ServerPlayer)player).setGameMode(GameType.SPECTATOR); // spectator the player on losing all max health
            player.setData(PlayerData.MAX_HEALTH_MODIFICATION, 0); // reset health modification
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        NotyourfriendCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onGatherDataClient(GatherDataEvent.Client event) {
        event.createProvider(ModModelProvider::new);
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(ModKeyMappingCategories.NOTYOURFRIEND_CATEGORY);

        event.register(ModKeyMappings.ABILITY_MENU.get());
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        while (ModKeyMappings.ABILITY_MENU.get().consumeClick()) {
            if (player.getData(PlayerData.EVIL)) {
                Minecraft.getInstance().setScreenAndShow(new AbilityScreen(Component.translatable("notyourfriend.ability_menu.title")));
            }
        }
        GuiParticle.tickParticles();
    }
}
