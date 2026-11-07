package net.azothe.notyourfriend.guiparticle.guiparticleclass;

import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.guiparticle.GuiParticle;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import static net.azothe.notyourfriend.NotYourFriend.RANDOM;

public class BloodGuiParticle extends GuiParticle {
    private double x;
    private double y;
    private double dx;
    private double dy;
    private final int type;
    private static final Identifier SPRITESHEET = NotYourFriend.id("gui_particle/blood");

    public BloodGuiParticle(GuiGraphicsExtractor graphics, float green_chance, int pos, int force) {
        super();
        this.x = graphics.guiWidth() / 2.0 - 89 + 8 * pos;
        this.y = graphics.guiHeight() - 38;
        this.dx = RANDOM.nextDouble(-2, 2) * Math.sqrt(force / 2.0 + 0.5);
        this.dy = -RANDOM.nextDouble(4, 6) * Math.sqrt(force + 0.25);
        this.type = RANDOM.nextInt(0, 5) + (RANDOM.nextFloat() <= green_chance ? 5 : 0);
    }

    @Override
    public void tick() {
        this.x += this.dx;
        this.y += this.dy;
        this.dx *= 0.98;
        this.dy += 0.5;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics) {
        if (y > graphics.guiHeight() * 1.1 || x > graphics.guiWidth() * 1.1 || x < -graphics.guiWidth() * 0.1) {
            this.remove();
        }
        graphics.enableScissor((int)this.x, (int)this.y, (int)this.x + 5, (int)this.y + 5);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITESHEET, (int)this.x - this.type * 5, (int)this.y, 50, 5);
        graphics.disableScissor();
    }
}
