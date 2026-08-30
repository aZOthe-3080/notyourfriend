package net.azothe.notyourfriend.mixin;

import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.data.PlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.azothe.notyourfriend.NotYourFriend.RANDOM;

@Mixin(Hud.class)
public class HudMixin {
    @Unique private static final Identifier BAR_SPRITESHEET = NotYourFriend.id("textures/gui/brittle_heart.png");
    @Unique private static final Identifier STAB_SPRITESHEET = NotYourFriend.id("textures/gui/stab.png");

    @Inject(method = "extractHeart",
            at = @At("HEAD"),
            cancellable = true)
    public void extractHeart(GuiGraphicsExtractor graphics, Hud.HeartType type, int xo, int yo, boolean isHardcore, boolean blinks, boolean half, CallbackInfo callback) {
        callback.cancel();
        assert Minecraft.getInstance().player != null;
        Player player = Minecraft.getInstance().player;

        int heartPos = ((xo - (graphics.guiWidth() / 2 - 91)) / 8);
        int yShift = (int)(player.getData(PlayerData.PAIN) * Math.sin((player.tickCount + heartPos / 2.0)) / (heartPos + 1) / (heartPos + 1));
        yo -= Math.clamp(yShift, -10, 30);
        xo += yShift > 30 || yShift < -10 ? RANDOM.nextInt(-(int)Math.sqrt(yShift / 10.0), (int)Math.sqrt(yShift / 10.0) + 1) : 0;
        float infectionProgress = player.getData(PlayerData.INFECTION_PROGRESS) - (float) heartPos;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, type.getSprite(isHardcore, half, blinks), xo, yo, 9, 9);

        int u;

        if (infectionProgress >= 1f) {
            u = 27;
        } else if (infectionProgress >= 0.75f) {
            u = 18;
        } else if (infectionProgress >= 0.5f) {
            u = 9;
        } else if (infectionProgress > 0.25f) {
            u = 0;
        } else {
            u = -99;
        }

        if (isHardcore) {
            u += 36;
        }

        int v;

        if (type == Hud.HeartType.CONTAINER) {
            v = 0;
        } else if (half) {
            v = 9;
        } else {
            v = 18;
        }

        if (u >= 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, BAR_SPRITESHEET, xo, yo, u, v, 9, 9, 72, 54);
        }

        if (((xo - (graphics.guiWidth() / 2 - 91)) / 8) == 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, STAB_SPRITESHEET, xo, yo, Minecraft.getInstance().player.getData(PlayerData.STAB_TYPE) * 9, 0, 9, 9, 45, 9);
        }
    }
}
