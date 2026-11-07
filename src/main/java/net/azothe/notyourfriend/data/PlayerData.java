package net.azothe.notyourfriend.data;

import com.mojang.serialization.Codec;
import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.data.payloads.CreateGuiParticlesPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class PlayerData {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, NotYourFriend.MOD_ID);

    public static void register(IEventBus eventBus) {
        ATTACHMENTS.register(eventBus);
    }

    public static final Supplier<AttachmentType<Float>> INFECTION_RATE = ATTACHMENTS.register(
            "infection_rate", () -> AttachmentType.builder(() -> 0f)
                .serialize(Codec.FLOAT.fieldOf("value"))
                .sync((holder, target) -> holder == target, ByteBufCodecs.FLOAT)
                .build());

    public static final Supplier<AttachmentType<Float>> INFECTION_PROGRESS = ATTACHMENTS.register(
            "infection_progress", () -> AttachmentType.builder(() -> 0f)
                .serialize(Codec.FLOAT.fieldOf("value"))
                .sync((holder, target) -> holder == target, ByteBufCodecs.FLOAT)
                .build());

    /*
    * stab_type ids
    * 0 - none
    * 1 - shard
    * 2 - 1x thorn
    * 3 - 2x thorn
    * 4 - 3x thorn
    */
    public static final Supplier<AttachmentType<Integer>> STAB_TYPE = ATTACHMENTS.register(
            "stab_type", () -> AttachmentType.builder(() -> 0)
                .serialize(Codec.INT.fieldOf("value"))
                .sync((holder, target) -> holder == target, ByteBufCodecs.INT)
                .build());

    public static final Supplier<AttachmentType<Integer>> PAIN = ATTACHMENTS.register(
            "pain", () -> AttachmentType.builder(() -> 0)
                    .serialize(Codec.INT.fieldOf("value"))
                    .sync((holder, target) -> holder == target, ByteBufCodecs.INT)
                    .build());

    public static final Supplier<AttachmentType<CreateGuiParticlesPayload>> PARTICLE_CREATION = ATTACHMENTS.register(
            "particle_creation", () -> AttachmentType.builder(() -> new CreateGuiParticlesPayload(0, 0))
                    .serialize(CreateGuiParticlesPayload.CODEC.fieldOf("value"))
                    .sync((holder, target) -> holder == target, CreateGuiParticlesPayload.STREAM_CODEC)
                    .build());

    public static final Supplier<AttachmentType<Integer>> MAX_HEALTH_MODIFICATION = ATTACHMENTS.register(
            "max_health_modification", () -> AttachmentType.builder(() -> 0)
                    .serialize(Codec.INT.fieldOf("value"))
                    .copyOnDeath()
                    .sync((holder, target) -> holder == target, ByteBufCodecs.INT)
                    .build());

    public static final Supplier<AttachmentType<Boolean>> EVIL = ATTACHMENTS.register(
            "evil", () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL.fieldOf("value"))
                    .copyOnDeath()
                    .sync((holder, target) -> holder == target, ByteBufCodecs.BOOL)
                    .build());

    public static final Supplier<AttachmentType<Integer>> SOUL_ENERGY = ATTACHMENTS.register(
            "soul_energy", () -> AttachmentType.builder(() -> 0)
                    .serialize(Codec.INT.fieldOf("value"))
                    .copyOnDeath()
                    .sync((holder, target) -> holder == target, ByteBufCodecs.INT)
                    .build());

    public static final Supplier<AttachmentType<Integer>> MAX_SOUL_ENERGY = ATTACHMENTS.register(
            "max_soul_energy", () -> AttachmentType.builder(() -> 100)
                    .serialize(Codec.INT.fieldOf("value"))
                    .copyOnDeath()
                    .sync((holder, target) -> holder == target, ByteBufCodecs.INT)
                    .build());
}
