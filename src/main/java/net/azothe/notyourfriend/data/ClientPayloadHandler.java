package net.azothe.notyourfriend.data;

import net.azothe.notyourfriend.data.payloads.CreateGuiParticles;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    public static void onParticleCreation(final CreateGuiParticles data, final IPayloadContext context) {
        context.player().setData(PlayerData.PARTICLE_CREATION, new CreateGuiParticles(data.id(), data.count()));
    }
}
