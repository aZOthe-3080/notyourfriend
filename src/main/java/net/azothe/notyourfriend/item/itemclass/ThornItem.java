package net.azothe.notyourfriend.item.itemclass;

import net.azothe.notyourfriend.data.PlayerData;
import net.azothe.notyourfriend.data.payloads.CreateGuiParticles;
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

public class ThornItem extends Item{
    public ThornItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return onItemUse(level, player, hand);
    }

    public static InteractionResult onItemUse(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && hand == InteractionHand.MAIN_HAND && player.gameMode().isSurvival()) {
            boolean success = true;
            switch (player.getData(PlayerData.STAB_TYPE)) {
                case 0 -> {
                    player.setData(PlayerData.STAB_TYPE, 2);
                    player.setData(PlayerData.INFECTION_RATE, 0.125f);
                    player.setData(PlayerData.PAIN, player.getData(PlayerData.PAIN) + 20);
                    PacketDistributor.sendToPlayer((ServerPlayer)player, new CreateGuiParticles(3, 4));
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 0.15f, 1.2f);
                    player.hurtServer((ServerLevel)level, player.damageSources().magic(), Math.min(1f, player.getHealth() - 1f));
                    player.getMainHandItem().consume(1, player);
                    return InteractionResult.CONSUME;
                }
                case 2 -> {
                    player.setData(PlayerData.STAB_TYPE, 3);
                    player.setData(PlayerData.INFECTION_RATE, 0.25f);
                    player.setData(PlayerData.PAIN, player.getData(PlayerData.PAIN) + 40);
                    PacketDistributor.sendToPlayer((ServerPlayer)player, new CreateGuiParticles(3, 7));
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 0.35f, 1f);
                    player.hurtServer((ServerLevel)level, player.damageSources().magic(), Math.min(1f, player.getHealth() - 2f));
                    player.getMainHandItem().consume(1, player);
                    return InteractionResult.CONSUME;
                }
                case 3 -> {
                    player.setData(PlayerData.STAB_TYPE, 4);
                    player.setData(PlayerData.INFECTION_RATE, 0.5f);
                    player.setData(PlayerData.PAIN, player.getData(PlayerData.PAIN) + 80);
                    PacketDistributor.sendToPlayer((ServerPlayer)player, new CreateGuiParticles(3, 15));
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 0.5f, 0.8f);
                    player.hurtServer((ServerLevel)level, player.damageSources().magic(), Math.min(1f, player.getHealth() - 3f));
                    player.getMainHandItem().consume(1, player);
                }
                default -> success = false;
            }
            if (success) {
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.FAIL;
    }
}
