package net.azothe.notyourfriend.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.Util;
import net.azothe.notyourfriend.data.PlayerData;
import net.azothe.notyourfriend.data.payloads.MouseStabPayload;
import net.azothe.notyourfriend.item.ModItems;
import net.azothe.notyourfriend.screen.HeartRemovalScreen;
import net.azothe.notyourfriend.screen.TweezerStabRemovalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.azothe.notyourfriend.data.MixinData.heartClick;
import static net.azothe.notyourfriend.data.MixinData.heartHighlight;
import static net.azothe.notyourfriend.data.MixinData.heartX;
import static net.azothe.notyourfriend.data.MixinData.heartXo;
import static net.azothe.notyourfriend.data.MixinData.heartY;
import static net.azothe.notyourfriend.data.MixinData.heartYo;


@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V",
            at = @At(value = "HEAD"))
    public void extractRenderStateModify_graphics(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo callback) {
        Player player = Minecraft.getInstance().player;
        if (player.containerMenu.getCarried().is(TagKey.create(
                Registries.ITEM,
                NotYourFriend.id("highlights_hearts")
        ))) {
            int hearts = (int)(Math.ceil(player.getMaxHealth() / 2f) + Math.ceil(player.getAbsorptionAmount() / 2f));
            int rows = (int)Math.ceil(hearts / 10.0);
            heartX = graphics.guiWidth() / 2 - 91;
            heartXo = rows == 1 ? 9 + 8 * (hearts - 1) : 81;
            heartYo = 9 + Math.max(12 - rows, 3) * (rows - 1);
            heartY = graphics.guiHeight() - 30 - heartYo;
            heartX -= 4;   //
            heartXo += 8;  // increase clickable area
            heartY -= 4;   //
            heartYo += 8;  //
            heartHighlight = true;
            graphics.outline(heartX - 1, heartY - 1, heartXo + 2, heartYo + 2, 0xFFFFFFFF);
        } else {
            heartX = 0;
            heartXo = 0;
            heartY = 0;
            heartYo = 0;
            heartHighlight = false;
        }
    }

    @Inject(method = "mouseReleased",
            at = @At(value = "HEAD"))
    public void mouseReleasedHead(MouseButtonEvent event, CallbackInfoReturnable<Boolean> callback) {
        heartClick = Util.pointInRegion(event.x(), event.y(),
                heartX, heartY,
                heartX + heartXo, heartY + heartYo); // checks if the click point is in the heart region
        if (heartClick && heartHighlight) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            ItemStack mouseItem = player.containerMenu.getCarried();
            if (mouseItem.is(ModItems.SHARD)) {
                ClientPacketDistributor.sendToServer(new MouseStabPayload(0));
            } else if (mouseItem.is(ModItems.THORN)) {
                ClientPacketDistributor.sendToServer(new MouseStabPayload(1));
            } else if (mouseItem.is(ModItems.TWEEZERS) && (player.getHealth() <= 1f || player.level().getDifficulty() == Difficulty.PEACEFUL) && player.getData(PlayerData.STAB_TYPE) != 0) {
                minecraft.setScreenAndShow(new TweezerStabRemovalScreen());
            } else if (mouseItem.is(ModItems.KNIFE) && player.getMaxHealth() > 2f) {
                minecraft.setScreenAndShow(new HeartRemovalScreen());
            }
        }
    }

    @WrapOperation(method = "extractBackground",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractRecipeBookScreen;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"))
    public void extractBackgroundWrap_extractBackground(InventoryScreen instance, GuiGraphicsExtractor graphicsExtractor, int mouseX, int mouseY, float a, Operation<Void> original) {
        if (!heartHighlight) {
            original.call(instance, graphicsExtractor, mouseX, mouseY, a);
        }
    }
}
