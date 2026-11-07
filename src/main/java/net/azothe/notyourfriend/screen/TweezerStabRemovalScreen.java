package net.azothe.notyourfriend.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.Util;
import net.azothe.notyourfriend.data.PlayerData;
import net.azothe.notyourfriend.data.payloads.PlayerSoundPayload;
import net.azothe.notyourfriend.data.payloads.StabRemovedPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.Map;

public class TweezerStabRemovalScreen extends Screen {
    private static final int HALF_HEART_SIZE = 63;

    private static final Identifier CONTAINER = Identifier.withDefaultNamespace("hud/heart/container");
    private static final Identifier CONTAINER_HARDCORE = Identifier.withDefaultNamespace("hud/heart/container_harcore");
    private static final Identifier HALF_HEART = Identifier.withDefaultNamespace("hud/heart/half");
    private static final Identifier HALF_HEART_HARDCORE = Identifier.withDefaultNamespace("hud/heart/half_hardcore");
    private static final Identifier INFECTION = NotYourFriend.id("hud/brittle_heart");
    private static final Identifier TWEEZERS_TOP = NotYourFriend.id("minigames/tweezers_top");
    private static final Identifier TWEEZERS_TOP_USING = NotYourFriend.id("minigames/tweezers_top_using");
    private static final Identifier TWEEZERS_BOTTOM = NotYourFriend.id("minigames/tweezers_bottom");
    private static final Identifier TWEEZERS_BOTTOM_USING = NotYourFriend.id("minigames/tweezers_bottom_using");
    private static final Identifier STAB = NotYourFriend.id("minigames/stab");
    private static final int HEART_SIZE = HALF_HEART_SIZE * 2;
    private static final Map<Integer, Integer> STAB_COLOR_MAP = Map.ofEntries(
            Map.entry(1, 0xFF3F3C53),
            Map.entry(2, 0xFF7C5022),
            Map.entry(3, 0xFF7C5022),
            Map.entry(4, 0xFF7C5022)
    );
    private static final Map<Difficulty, Double> MAX_SPEED_MAP = Map.ofEntries(
            Map.entry(Difficulty.PEACEFUL, 9999.0),
            Map.entry(Difficulty.EASY, 400.0 / 20.0),
            Map.entry(Difficulty.NORMAL, 200.0 / 20.0),
            Map.entry(Difficulty.HARD, 100.0 / 20.0)
    );
    private static final Map<Difficulty, Double> X_SPEED_REDUCTION_MAP = Map.ofEntries(
            Map.entry(Difficulty.PEACEFUL, 3.0),
            Map.entry(Difficulty.EASY, 10.0),
            Map.entry(Difficulty.NORMAL, 20.0),
            Map.entry(Difficulty.HARD, 30.0)
    );
    private static final Map<Difficulty, Double> Y_SPEED_INCREASE_MAP = Map.ofEntries(
            Map.entry(Difficulty.PEACEFUL, 1.0),
            Map.entry(Difficulty.EASY, 2.0),
            Map.entry(Difficulty.NORMAL, 4.0),
            Map.entry(Difficulty.HARD, 6.0)
    );
    private static final Map<Integer, Double> STAB_DIFFICULTY_MAP = Map.ofEntries(
            Map.entry(1, 1.0),
            Map.entry(2, 0.2),
            Map.entry(3, 0.5),
            Map.entry(4, 0.7)
    );

    private final int originalStab;
    private final double xMaxSpeed;
    private final double xSpeedReduction;
    private final double ySpeedIncrease;
    private final double stabMultiplier;
    private final boolean isHardcore;

    private boolean mouseDown = false;
    private boolean mouseWasDown = false;
    private boolean grabbed = false;
    private double stabPos = 0;
    private int mouseX = 0;
    private int mouseY = 0;
    private int oldMouseX = 0;
    private int oldMouseY = 0;
    private double tweezersX = 0;
    private double tweezersY = 0;
    private int guiWidth = 0;
    private int guiHeight = 0;

    public TweezerStabRemovalScreen() {
        super(Component.empty());
        Minecraft instance = Minecraft.getInstance();
        Player player = instance.player;
        Difficulty difficulty = instance.level.getDifficulty();
        this.originalStab = player.getData(PlayerData.STAB_TYPE);
        this.xMaxSpeed = MAX_SPEED_MAP.get(difficulty);
        this.xSpeedReduction = X_SPEED_REDUCTION_MAP.get(difficulty);
        this.ySpeedIncrease = Y_SPEED_INCREASE_MAP.get(difficulty);
        this.stabMultiplier = STAB_DIFFICULTY_MAP.get(player.getData(PlayerData.STAB_TYPE));
        this.isHardcore = player.level().getLevelData().isHardcore();
    }

    @Override
    public void tick() {
        super.tick();

        Minecraft instance = Minecraft.getInstance();
        Player player = instance.player;

        int stabType = instance.player.getData(PlayerData.STAB_TYPE);

        if (player.getHealth() > 1f || stabType != player.getData(PlayerData.STAB_TYPE) || stabType != this.originalStab) {
            this.onClose();
        }

        int mouseDx = this.mouseX - this.oldMouseX;
        int mouseDy = this.mouseY - this.oldMouseY;

        if (this.grabbed && mouseDx >= 0) {
            if (mouseDx > this.xMaxSpeed) {
                grabbed = false;
                ClientPacketDistributor.sendToServer(new PlayerSoundPayload(0));
            } else {
                this.tweezersX += mouseDx / this.xSpeedReduction / stabMultiplier;
                this.stabPos += mouseDx / this.xSpeedReduction / stabMultiplier;
                this.tweezersY += mouseDy * this.ySpeedIncrease * stabMultiplier;
                if (Math.abs(this.tweezersY - this.guiHeight / 2.0) > 10.0) {
                    grabbed = false;
                    ClientPacketDistributor.sendToServer(new PlayerSoundPayload(0));
                }
            }
        } else {
            this.tweezersX += ((mouseX - 54) - this.tweezersX) / 2.0;
            this.tweezersY += ((mouseY + 54) - this.tweezersY) / 2.0;
        }

        this.grabbed = this.mouseDown &&
                (!this.mouseWasDown || this.grabbed) &&
                Util.pointInRegion(tweezersX, tweezersY, this.guiWidth / 2.0 + 5, this.guiHeight / 2.0 - 10, this.guiWidth / 2.0 + this.stabPos + 17, this.guiHeight / 2.0 + 10);

        this.mouseWasDown = this.mouseDown;

        this.oldMouseX = this.mouseX;
        this.oldMouseY = this.mouseY;

        if (stabPos >= 65) {
            ClientPacketDistributor.sendToServer(new StabRemovedPayload(stabType));
            this.onClose();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        Player player = Minecraft.getInstance().player;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.guiWidth = graphics.guiWidth();
        this.guiHeight = graphics.guiHeight();
        int halfWidth = this.guiWidth / 2;
        int halfHeight = this.guiHeight / 2;


        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.isHardcore ? CONTAINER_HARDCORE : CONTAINER,
                halfWidth - HALF_HEART_SIZE, halfHeight - HALF_HEART_SIZE,
                HEART_SIZE, HEART_SIZE);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.isHardcore ? HALF_HEART_HARDCORE : HALF_HEART,
                halfWidth - HALF_HEART_SIZE, halfHeight - HALF_HEART_SIZE,
                HEART_SIZE, HEART_SIZE);

        float infectionProgress = player.getData(PlayerData.INFECTION_PROGRESS);

        int u;

        if (infectionProgress >= 1f) {
            u = 3;
        } else if (infectionProgress >= 0.75f) {
            u = 2;
        } else if (infectionProgress >= 0.5f) {
            u = 1;
        } else if (infectionProgress >= 0.25f) {
            u = 0;
        } else {
            u = -99; // prevents rendering the infected heart if the infection progress is too low
        }

        if (this.isHardcore) {
            u += 4;
        }

        if (u >= 0) {
            graphics.enableScissor(halfWidth - HALF_HEART_SIZE, halfHeight - HALF_HEART_SIZE,
                    halfWidth + HALF_HEART_SIZE, halfHeight + HALF_HEART_SIZE);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, INFECTION,
                    halfWidth - HALF_HEART_SIZE - HEART_SIZE * u, halfHeight - HALF_HEART_SIZE - HEART_SIZE,
                    HEART_SIZE * 8, HEART_SIZE * 3);
            graphics.disableScissor();
        }

        graphics.fill(halfWidth - 2, halfHeight - 12, halfWidth + 2, halfHeight + 12, 0x7F000000);

        if (this.mouseDown) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TWEEZERS_BOTTOM_USING, (int)this.tweezersX - 30, (int)this.tweezersY - 66, 96, 96);
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TWEEZERS_BOTTOM, (int)this.tweezersX - 30, (int)this.tweezersY - 66, 96, 96);
        }

        graphics.enableScissor(halfWidth, 0, this.guiWidth, height);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, STAB,
                halfWidth - HALF_HEART_SIZE + (int)this.stabPos, halfHeight - 10,
                80, 20, STAB_COLOR_MAP.get(player.getData(PlayerData.STAB_TYPE)));
        graphics.disableScissor();

        if (this.mouseDown) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TWEEZERS_TOP_USING, (int)this.tweezersX - 30, (int)this.tweezersY - 66, 96, 96);
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TWEEZERS_TOP, (int)this.tweezersX - 30, (int)this.tweezersY - 66, 96, 96);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.extractTransparentBackground(graphics);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (Minecraft.getInstance().options.keyInventory.isActiveAndMatches(InputConstants.getKey(event))) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft instance = Minecraft.getInstance();
        instance.setScreenAndShow(new InventoryScreen(instance.player));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            this.mouseDown = true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            this.mouseDown = false;
        }
        return super.mouseReleased(event);
    }
}
