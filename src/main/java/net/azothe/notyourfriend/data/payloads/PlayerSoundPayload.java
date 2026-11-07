package net.azothe.notyourfriend.data.payloads;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.azothe.notyourfriend.NotYourFriend;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record PlayerSoundPayload(int id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerSoundPayload> TYPE = new CustomPacketPayload.Type<>(NotYourFriend.id("player_sound"));

    public static final Codec<PlayerSoundPayload> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("id").forGetter(PlayerSoundPayload::id)
            ).apply(instance, PlayerSoundPayload::new)
    );

    public static final StreamCodec<ByteBuf, PlayerSoundPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, PlayerSoundPayload::id,
                    PlayerSoundPayload::new
            );

    @Override
    public CustomPacketPayload.@NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
