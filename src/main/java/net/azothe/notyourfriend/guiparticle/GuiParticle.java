package net.azothe.notyourfriend.guiparticle;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;

public abstract class GuiParticle {
    public boolean remove = false;
    public static ArrayList<GuiParticle> particles = new ArrayList<>();

    public GuiParticle() {
        particles.add(this);
    }

    public abstract void tick(GuiGraphicsExtractor graphics, DeltaTracker partialTick);

    public void remove() {
        this.remove = true;
    }


    public static void tickParticles(GuiGraphicsExtractor graphics, DeltaTracker partialTick) {
        ArrayList<GuiParticle> iterParticles = (ArrayList<GuiParticle>)particles.clone();
        iterParticles.forEach(particle -> {
            if (particle.remove) {
                particles.remove(particle);
            } else {
                particle.tick(graphics, partialTick);
            }
        });
    }
}
