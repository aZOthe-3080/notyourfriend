package net.azothe.notyourfriend.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.azothe.notyourfriend.data.MixinData.heartClick;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Inject(method = "mouseReleased",
            at = @At("HEAD"))
    public void mouseReleasedHead(MouseButtonEvent event, CallbackInfoReturnable<Boolean> callback) {

    }

    @ModifyVariable(method = "mouseReleased",
            at = @At("LOAD"),
            name = "clickedOutside")
    public boolean mouseReleasedModify_clickedOutside(boolean clickedOutside) {
        return clickedOutside && !(heartClick && (Object)this instanceof InventoryScreen);
    }
}
