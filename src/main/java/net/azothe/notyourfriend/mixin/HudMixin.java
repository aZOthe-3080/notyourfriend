package net.azothe.notyourfriend.mixin;

import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.data.PlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.azothe.notyourfriend.NotYourFriend.RANDOM;

@Mixin(Hud.class)
public class HudMixin {
    @Unique private static final Identifier BAR_SPRITESHEET = NotYourFriend.id("hud/brittle_heart");
    @Unique private static final Identifier STAB_SPRITESHEET = NotYourFriend.id("hud/stab");

    @Unique private static int notyourfriend$heartPos;

    @Inject(method = "extractHearts",
            at = @At("HEAD"))
    public void extractHeartsHead(GuiGraphicsExtractor graphics, Player player, int xLeft, int yLineBase, int healthRowHeight, int heartOffsetIndex, float maxHealth, int currentHealth, int oldHealth, int absorption, boolean blink, CallbackInfo ci) {
        notyourfriend$heartPos = Mth.ceil(maxHealth / 2.0) + Mth.ceil(absorption / 2.0); // get number of hearts, copied from the for loop in Hud#extractHearts
    }

    @Inject(method = "extractHeart",
            at = @At("HEAD"))
    public void extractHeartHead(GuiGraphicsExtractor graphics, Hud.HeartType type, int xo, int yo, boolean isHardcore, boolean blinks, boolean half, CallbackInfo ci) {
        if (type == Hud.HeartType.CONTAINER) { // only change the heart pos once per heart since every heart has 1 container
            notyourfriend$heartPos--;
        }
    }

    @ModifyVariable(method = "extractHeart",
            at = @At("HEAD"),
            argsOnly = true,
            name = "xo")
    public int extractHeartModify_xo(int xo) {
        Player player = Minecraft.getInstance().player;

        int yShift = (int)(player.getData(PlayerData.PAIN) * Math.sin((player.tickCount + notyourfriend$heartPos / 2.0)) / (notyourfriend$heartPos + 1) / (notyourfriend$heartPos + 1));
        xo += yShift > 30 || yShift < -10
                ? RANDOM.nextInt(-(int)Math.sqrt(yShift / 10.0), (int)Math.sqrt(yShift / 10.0) + 1) // shake the hearts horizontally if the y shake is too strong
                : 0;
        return xo;
    }

    @ModifyVariable(method = "extractHeart",
            at = @At("HEAD"),
            argsOnly = true,
            name = "yo")
    public int extractHeartModify_yo(int yo) {
        Player player = Minecraft.getInstance().player;
        int yShift = (int)(player.getData(PlayerData.PAIN) * Math.sin((player.tickCount + notyourfriend$heartPos / 2.0)) / (notyourfriend$heartPos + 1) / (notyourfriend$heartPos + 1));
        yo -= Math.clamp(yShift, -10, 30); // limit the shake amount
        return yo;
    }

    @Inject(method = "extractHeart",
            at = @At("TAIL"))
    public void extractHeartTail(GuiGraphicsExtractor graphics, Hud.HeartType type, int xo, int yo, boolean isHardcore, boolean blinks, boolean half, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;

        float infectionProgress = player.getData(PlayerData.INFECTION_PROGRESS) - (float)notyourfriend$heartPos;

        int u;

        if (infectionProgress >= 1f) {
            u = 27;
        } else if (infectionProgress >= 0.75f) {
            u = 18;
        } else if (infectionProgress >= 0.5f) {
            u = 9;
        } else if (infectionProgress >= 0.25f) {
            u = 0;
        } else {
            u = -99; // prevents rendering the infected heart if the infection progress is too low
        }

        if (isHardcore) {
            u += 36;
        }

        int v;

        if (type == Hud.HeartType.CONTAINER) { // empty heart
            v = 0;
        } else if (half) { // half heart
            v = 9;
        } else { // full heart
            v = 18;
        }

        graphics.enableScissor(xo, yo, xo + 9, yo + 9);

        if (u >= 0) { // V renders the infected heart
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BAR_SPRITESHEET, xo - u, yo - v, 72, 27);
        }

        if (notyourfriend$heartPos == 0) { // V renders the stab if this is the first heart
            int stabType = Minecraft.getInstance().player.getData(PlayerData.STAB_TYPE) * 9;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, STAB_SPRITESHEET, xo - stabType, yo, 45, 9);
        }

        graphics.disableScissor();
    }
}
