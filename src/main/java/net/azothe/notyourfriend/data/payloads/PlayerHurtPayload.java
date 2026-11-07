package net.azothe.notyourfriend.data.payloads;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import io.netty.buffer.ByteBuf;
import net.azothe.notyourfriend.NotYourFriend;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record PlayerHurtPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerHurtPayload> TYPE = new CustomPacketPayload.Type<>(NotYourFriend.id("player_hurt"));

    public static final Codec<PlayerHurtPayload> CODEC = Codec.of(Encoder.empty(), Decoder.unit(new PlayerHurtPayload())).codec();

    public static final StreamCodec<ByteBuf, PlayerHurtPayload> STREAM_CODEC = StreamCodec.unit(new PlayerHurtPayload());

    @Override
    public CustomPacketPayload.@NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
