package net.azothe.notyourfriend.data.payloads;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.azothe.notyourfriend.NotYourFriend;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record StabRemovedPayload(int id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StabRemovedPayload> TYPE = new CustomPacketPayload.Type<>(NotYourFriend.id("stab_removed"));

    public static final Codec<StabRemovedPayload> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("id").forGetter(StabRemovedPayload::id)
            ).apply(instance, StabRemovedPayload::new)
    );

    public static final StreamCodec<ByteBuf, StabRemovedPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, StabRemovedPayload::id,
                    StabRemovedPayload::new
            );

    @Override
    public CustomPacketPayload.@NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
