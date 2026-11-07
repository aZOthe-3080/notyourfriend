package net.azothe.notyourfriend.data.payloads;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.azothe.notyourfriend.NotYourFriend;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record MouseStabPayload(int id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MouseStabPayload> TYPE = new CustomPacketPayload.Type<>(NotYourFriend.id("mouse_stab"));

    public static final Codec<MouseStabPayload> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("id").forGetter(MouseStabPayload::id)
            ).apply(instance, MouseStabPayload::new)
    );

    public static final StreamCodec<ByteBuf, MouseStabPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, MouseStabPayload::id,
                    MouseStabPayload::new
            );

    @Override
    public CustomPacketPayload.@NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
