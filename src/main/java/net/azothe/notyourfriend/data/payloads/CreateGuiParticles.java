package net.azothe.notyourfriend.data.payloads;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.azothe.notyourfriend.NotYourFriend;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record CreateGuiParticles(int id, int count) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CreateGuiParticles> TYPE = new CustomPacketPayload.Type<>(NotYourFriend.id("create_gui_particle"));

    public static final Codec<CreateGuiParticles> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("id").forGetter(CreateGuiParticles::id),
                    Codec.INT.optionalFieldOf("count", 0).forGetter(CreateGuiParticles::count)
            ).apply(instance, CreateGuiParticles::new)
    );

    public static final StreamCodec<ByteBuf, CreateGuiParticles> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, CreateGuiParticles::id,
                    ByteBufCodecs.INT, CreateGuiParticles::count,
                    CreateGuiParticles::new
            );

    @Override
    public CustomPacketPayload.@NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
