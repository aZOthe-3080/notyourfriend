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

public class ShardItem extends Item {
    public ShardItem(Properties properties) {
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
        if (!level.isClientSide() && player.getData(PlayerData.STAB_TYPE) == 0 && player.gameMode().isSurvival() && player.getData(PlayerData.PAIN) == 0) {
            player.setData(PlayerData.STAB_TYPE, 1);
            player.setData(PlayerData.INFECTION_RATE, player.getData(PlayerData.INFECTION_RATE) * 2f + 1.5f);
            player.setData(PlayerData.PAIN, player.getData(PlayerData.PAIN) + 100);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new CreateGuiParticlesPayload(1, 7));
            level.playSound(null, player, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 1, 1);
            player.hurtServer((ServerLevel) level, player.damageSources().magic(), Math.min(3f, player.getHealth() - 2f));
            if (mouseUse) {
                user.containerMenu.getCarried().consume(1, user);
            } else {
                user.getMainHandItem().consume(1, user);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }
}
