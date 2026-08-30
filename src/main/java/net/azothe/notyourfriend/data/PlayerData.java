package net.azothe.notyourfriend.data;

import com.mojang.serialization.Codec;
import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.data.payloads.CreateGuiParticles;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class PlayerData {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, NotYourFriend.MOD_ID);

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }

    public static final Supplier<AttachmentType<Float>> INFECTION_RATE = ATTACHMENT_TYPES.register(
            "infection_rate", () -> AttachmentType.builder(() -> 0f)
                .serialize(Codec.FLOAT.fieldOf("value"))
                .copyOnDeath()
                .sync((holder, target) -> holder == target, ByteBufCodecs.FLOAT)
                .build());

    public static final Supplier<AttachmentType<Float>> INFECTION_PROGRESS = ATTACHMENT_TYPES.register(
            "infection_progress", () -> AttachmentType.builder(() -> 0f)
                .serialize(Codec.FLOAT.fieldOf("value"))
                .copyOnDeath()
                .sync((holder, target) -> holder == target, ByteBufCodecs.FLOAT)
                .build());

    /*
    * stab_type ids
    * 0 - none
    * 1 - shard
    */
    public static final Supplier<AttachmentType<Integer>> STAB_TYPE = ATTACHMENT_TYPES.register(
            "stab_type", () -> AttachmentType.builder(() -> 0)
                .serialize(Codec.INT.fieldOf("value"))
                .copyOnDeath()
                .sync((holder, target) -> holder == target, ByteBufCodecs.INT)
                .build());

    public static final Supplier<AttachmentType<Integer>> PAIN = ATTACHMENT_TYPES.register(
            "pain", () -> AttachmentType.builder(() -> 0)
                    .serialize(Codec.INT.fieldOf("value"))
                    .sync((holder, target) -> holder == target, ByteBufCodecs.INT)
                    .build());

    public static final Supplier<AttachmentType<CreateGuiParticles>> PARTICLE_CREATION = ATTACHMENT_TYPES.register(
            "particle_creation", () -> AttachmentType.builder(() -> new CreateGuiParticles(0, 0))
                    .serialize(CreateGuiParticles.CODEC.fieldOf("value"))
                    .sync((holder, target) -> holder == target, CreateGuiParticles.STREAM_CODEC)
                    .build());

    public static final Supplier<AttachmentType<Integer>> MAX_HEALTH_REDUCTION = ATTACHMENT_TYPES.register(
            "max_health_reduction", () -> AttachmentType.builder(() -> 0)
                    .serialize(Codec.INT.fieldOf("value"))
                    .copyOnDeath()
                    .sync((holder, target) -> holder == target, ByteBufCodecs.INT)
                    .build());
}
