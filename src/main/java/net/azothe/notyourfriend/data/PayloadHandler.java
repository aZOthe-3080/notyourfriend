package net.azothe.notyourfriend.data;

import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.data.payloads.CreateGuiParticlesPayload;
import net.azothe.notyourfriend.data.payloads.MouseStabPayload;
import net.azothe.notyourfriend.data.payloads.PlayerSoundPayload;
import net.azothe.notyourfriend.data.payloads.PlayerHurtPayload;
import net.azothe.notyourfriend.data.payloads.StabRemovedPayload;
import net.azothe.notyourfriend.item.ModItems;
import net.azothe.notyourfriend.item.items.ShardItem;
import net.azothe.notyourfriend.item.items.ThornItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PayloadHandler {
    public static class Client {
        public static void onCreateGuiParticles(final CreateGuiParticlesPayload data, final IPayloadContext context) {
            context.player().setData(PlayerData.PARTICLE_CREATION, new CreateGuiParticlesPayload(data.id(), data.count()));
        }
    }

    public static class Server {
        public static void onMouseStab(final MouseStabPayload data, final IPayloadContext context) {
            Player player = context.player();
            switch (data.id()) {
                case 0 -> ShardItem.stab(player.level(), player, player, true);
                case 1 -> ThornItem.stab(player.level(), player, player, true);
            }
        }

        public static void onStabRemoved(final StabRemovedPayload data, final IPayloadContext context) {
            Player player = context.player();

            player.level().playSound(null, player, SoundEvents.SLIME_HURT, SoundSource.PLAYERS, 1, 1);
            switch (data.id()) {
                case 1 -> {
                    player.setData(PlayerData.STAB_TYPE, 0);
                    player.addItem(ModItems.SHARD.toStack());
                    player.setData(PlayerData.INFECTION_RATE, player.getData(PlayerData.INFECTION_RATE) / 2);
                }
                case 2 -> {
                    player.setData(PlayerData.STAB_TYPE, 0);
                    player.addItem(ModItems.THORN.toStack());
                    player.setData(PlayerData.INFECTION_RATE, player.getData(PlayerData.INFECTION_RATE) / 2);
                }
                case 3 -> {
                    player.setData(PlayerData.STAB_TYPE, 2);
                    player.addItem(ModItems.THORN.toStack());
                    player.setData(PlayerData.INFECTION_RATE, 0.2f);
                }
                case 4 -> {
                    player.setData(PlayerData.STAB_TYPE, 3);
                    player.addItem(ModItems.THORN.toStack());
                    player.setData(PlayerData.INFECTION_RATE, 0.4f);
                }
            }
        }

        public static void onPlayerSound(final PlayerSoundPayload data, final IPayloadContext context) {
            Player player = context.player();
            Level level = player.level();
            switch (data.id()) {
                case 0 -> level.playSound(null, player, SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1, NotYourFriend.RANDOM.nextFloat(0.9f, 1.1f));
            }
        }

        public static void onPlayerHurt(final PlayerHurtPayload data, final IPayloadContext context) {
            Player player = context.player();
            player.hurtServer((ServerLevel)player.level(), player.damageSources().generic(), 1f);
        }
    }
}
