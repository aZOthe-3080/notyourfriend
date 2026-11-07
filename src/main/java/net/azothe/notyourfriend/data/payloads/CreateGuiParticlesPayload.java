package net.azothe.notyourfriend.data.payloads;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.azothe.notyourfriend.NotYourFriend;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record CreateGuiParticlesPayload(int id, int count ) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CreateGuiParticlesPayload> TYPE = new CustomPacketPayload.Type<>(NotYourFriend.id("create_gui_particle"));

    public static final Codec<CreateGuiParticlesPayload> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("id").forGetter(CreateGuiParticlesPayload::id),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(CreateGuiParticlesPayload::count)
            ).apply(instance, CreateGuiParticlesPayload::new)
    );

    public static final StreamCodec<ByteBuf, CreateGuiParticlesPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, CreateGuiParticlesPayload::id,
                    ByteBufCodecs.INT, CreateGuiParticlesPayload::count,
                    CreateGuiParticlesPayload::new
            );

    @Override
    public CustomPacketPayload.@NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
