package net.azothe.notyourfriend.guiparticle.guiparticleclass;

import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.guiparticle.GuiParticle;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import static net.azothe.notyourfriend.NotYourFriend.RANDOM;

public class BloodGuiParticle extends GuiParticle {
    private double x;
    private double y;
    private double dx;
    private double dy;
    private final int type;
    private static final Identifier SPRITESHEET = NotYourFriend.id("textures/gui_particle/blood.png");

    public BloodGuiParticle(GuiGraphicsExtractor graphics, float green_chance, int pos, int force) {
        super();
        this.x = graphics.guiWidth() / 2.0 - 89 + 8 * pos;
        this.y = graphics.guiHeight() - 38;
        this.dx = RANDOM.nextDouble(-2, 2) * Math.sqrt(force / 2.0 + 0.5);
        this.dy = -RANDOM.nextDouble(4, 6) * Math.sqrt(force + 0.25);
        this.type = RANDOM.nextInt(0, 5) + (RANDOM.nextFloat() <= green_chance ? 5 : 0);
    }

    @Override
    public void tick(GuiGraphicsExtractor graphics, DeltaTracker partialTick) {
        this.x += this.dx * partialTick.getRealtimeDeltaTicks();
        this.y += this.dy * partialTick.getRealtimeDeltaTicks();
        this.dx -= Mth.sign(this.dx) * 0.1 * partialTick.getRealtimeDeltaTicks();
        this.dy += 0.5 * partialTick.getRealtimeDeltaTicks();
        if (y > graphics.guiHeight() * 1.1 || x > graphics.guiWidth() * 1.1 || x < -graphics.guiWidth() * 0.1) {
            this.remove();
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, SPRITESHEET, (int)this.x, (int)this.y, 5 * this.type, 0, 5, 5, 50, 5);
    }
}
