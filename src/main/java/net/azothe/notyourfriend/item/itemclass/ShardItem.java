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

public class ShardItem extends Item {
    public ShardItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return onItemUse(level, player, hand);
    }

    public static InteractionResult onItemUse(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player.getData(PlayerData.STAB_TYPE) == 0 && hand == InteractionHand.MAIN_HAND && player.gameMode().isSurvival()) {
            player.setData(PlayerData.STAB_TYPE, 1);
            player.setData(PlayerData.INFECTION_RATE, 1f);
            player.setData(PlayerData.PAIN, player.getData(PlayerData.PAIN) + 100);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new CreateGuiParticles(1, 7));
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS);
            player.hurtServer((ServerLevel) level, player.damageSources().magic(), Math.min(3f, player.getHealth() - 2f));
            player.getMainHandItem().consume(1, player);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }
}
