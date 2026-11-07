package net.azothe.notyourfriend.guiparticle;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;

public abstract class GuiParticle {
    public boolean remove = false;
    public static ArrayList<GuiParticle> particles = new ArrayList<>();

    public GuiParticle() {
        particles.add(this);
    }

    public abstract void tick();

    public abstract void extractRenderState(GuiGraphicsExtractor graphics);

    public void remove() {
        this.remove = true;
    }

    public static void tickParticles() {
        ArrayList<GuiParticle> iterParticles = new ArrayList<>(particles);
        iterParticles.forEach(particle -> {
            if (particle.remove) {
                particles.remove(particle);
            } else {
                particle.tick();
            }
        });
    }

    public static void extractParticles(GuiGraphicsExtractor graphics) {
        particles.forEach(particle -> {
            particle.extractRenderState(graphics);
        });
    }
}
