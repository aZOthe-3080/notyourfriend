package net.azothe.notyourfriend.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.azothe.notyourfriend.data.PlayerData;
import net.azothe.notyourfriend.keymapping.ModKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class AbilityScreen extends Screen {
    private static final Button button = Button.builder(Component.literal("WIP"), _ -> {})
            .bounds(10, 10, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT)
            .build();

    public AbilityScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        this.addWidget(button);
    }

    @Override
    public void tick() {
        super.tick();
        Player player = Minecraft.getInstance().player;
        if (!player.getData(PlayerData.EVIL)) {
            this.onClose();
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.extractTransparentBackground(graphics);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        button.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (ModKeyMappings.ABILITY_MENU.get().isActiveAndMatches(InputConstants.getKey(event)) ||
                Minecraft.getInstance().options.keyInventory.isActiveAndMatches(InputConstants.getKey(event))) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
