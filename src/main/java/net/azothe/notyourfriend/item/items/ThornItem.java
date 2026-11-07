package net.azothe.notyourfriend.item.items;

import net.azothe.notyourfriend.data.PlayerData;
import net.azothe.notyourfriend.data.payloads.CreateGuiParticlesPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class ThornItem extends Item {
    public ThornItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.FAIL;
        }
        return stab(level, player, player, false);
    }

    public static InteractionResult stab(Level level, Player player, Player user, boolean mouseUse) {
        if (!level.isClientSide() && player.gameMode().isSurvival() && player.getData(PlayerData.PAIN) == 0) {
            boolean success = true;
            switch (player.getData(PlayerData.STAB_TYPE)) {
                case 0 -> {
                    player.setData(PlayerData.STAB_TYPE, 2);
                    player.setData(PlayerData.INFECTION_RATE, player.getData(PlayerData.INFECTION_RATE) * 2f + 0.1f);
                    player.setData(PlayerData.PAIN, player.getData(PlayerData.PAIN) + 20);
                    PacketDistributor.sendToPlayer((ServerPlayer)player, new CreateGuiParticlesPayload(3, 4));
                    level.playSound(null, player, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 0.15f, 1.2f);
                    player.hurtServer((ServerLevel)level, player.damageSources().magic(), Math.min(1f, player.getHealth() - 3f));
                }
                case 2 -> {
                    player.setData(PlayerData.STAB_TYPE, 3);
                    player.setData(PlayerData.INFECTION_RATE, player.getData(PlayerData.INFECTION_RATE) * 2f + 0.1f);
                    player.setData(PlayerData.PAIN, player.getData(PlayerData.PAIN) + 40);
                    PacketDistributor.sendToPlayer((ServerPlayer)player, new CreateGuiParticlesPayload(3, 7));
                    level.playSound(null, player, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 0.35f, 1f);
                    player.hurtServer((ServerLevel)level, player.damageSources().magic(), Math.min(1f, player.getHealth() - 2f));
                }
                case 3 -> {
                    player.setData(PlayerData.STAB_TYPE, 4);
                    player.setData(PlayerData.INFECTION_RATE, player.getData(PlayerData.INFECTION_RATE) * 2f + 0.1f);
                    player.setData(PlayerData.PAIN, player.getData(PlayerData.PAIN) + 80);
                    PacketDistributor.sendToPlayer((ServerPlayer)player, new CreateGuiParticlesPayload(3, 15));
                    level.playSound(null, player, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 0.5f, 0.8f);
                    player.hurtServer((ServerLevel)level, player.damageSources().magic(), Math.min(1f, player.getHealth() - 1f));
                }
                default -> success = false;
            }
            if (success) {
                if (mouseUse) {
                    user.containerMenu.getCarried().consume(1, user);
                } else {
                    user.getMainHandItem().consume(1, user);
                }
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.FAIL;
    }
}
