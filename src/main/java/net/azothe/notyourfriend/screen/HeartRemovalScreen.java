package net.azothe.notyourfriend.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.data.PlayerData;
import net.azothe.notyourfriend.data.payloads.PlayerHurtPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class HeartRemovalScreen extends Screen {
    private static final int HALF_HEART_SIZE = 63;

    private static final Identifier CONTAINER = Identifier.withDefaultNamespace("hud/heart/container");
    private static final Identifier CONTAINER_HARDCORE = Identifier.withDefaultNamespace("hud/heart/container_harcore");
    private static final Identifier HALF_HEART = Identifier.withDefaultNamespace("hud/heart/half");
    private static final Identifier HALF_HEART_HARDCORE = Identifier.withDefaultNamespace("hud/heart/hardcore_half");
    private static final Identifier FULL_HEART = Identifier.withDefaultNamespace("hud/heart/full");
    private static final Identifier FULL_HEART_HARDCORE = Identifier.withDefaultNamespace("hud/heart/hardcore_full");
    private static final Identifier INFECTION = NotYourFriend.id("hud/brittle_heart");
    private static final Identifier KNIFE = NotYourFriend.id("minigames/knife");
    private static final int HEART_SIZE = HALF_HEART_SIZE * 2;

    private final boolean isHardcore;

    private int health = 0;
    private int infection = 0;
    private double knifePos = 0;
    private double cutProgress = 0;
    private double shake = 0;
    private double shakeX = 0;
    private double shakeY = 0;
    private int mouseX = 0;
    private int mouseY = 0;
    private int oldMouseX = 0;
    private int oldMouseY = 0;
    private int guiWidth = 0;
    private int guiHeight = 0;

    public HeartRemovalScreen() {
        super(Component.empty());
        Player player = Minecraft.getInstance().player;
        this.isHardcore = player.level().getLevelData().isHardcore();
    }

    @Override
    public void tick() {
        super.tick();

        Player player = Minecraft.getInstance().player;
        float maxHealth = player.getMaxHealth();

        if (maxHealth <= 2f) {
            this.onClose();
        }

        float ignoredHealth = maxHealth - 4f;
        this.health = Math.max((int)Math.ceil(player.getHealth() - ignoredHealth), 0);
        this.infection = Math.max((int)Math.floor((player.getData(PlayerData.INFECTION_PROGRESS) * 2f - ignoredHealth) * 2f), 0);

        int halfWidth = this.guiWidth / 2;

        int mouseDx = this.mouseX - this.oldMouseX;
        int mouseDy = this.mouseY - this.oldMouseY;

        this.shake = Math.sqrt(Math.max(Math.abs(mouseX - halfWidth) - 20.0, 0.0) / 10.0);

        if (this.shake > 0.0) {
            this.shakeX = NotYourFriend.RANDOM.nextDouble(-this.shake, this.shake);
            this.shakeY = NotYourFriend.RANDOM.nextDouble(-this.shake, this.shake);
            if ((int)(NotYourFriend.RANDOM.nextFloat(0, 100) / this.shake / (Math.abs(mouseDx) + 1)) == 0) {
                ClientPacketDistributor.sendToServer(new PlayerHurtPayload());
                this.cutProgress = Math.max(this.cutProgress / 2 - 5, 0);
            }
        } else {
            this.shakeX = 0.0;
            this.shakeY = 0.0;
        }

        double cutRate = this.knifePos;

        this.knifePos = Math.clamp(this.knifePos - mouseDy / 15.0, 0.0, 9.0);

        cutRate = Math.abs(cutRate - this.knifePos);

        this.cutProgress += cutRate / 10.0;

        if (this.cutProgress >= 100.0) {
            this.onClose();
        }

        this.oldMouseX = this.mouseX;
        this.oldMouseY = this.mouseY;
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

        int v;
        int u;

        // vvv right heart rendering vvv

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.isHardcore ? CONTAINER_HARDCORE : CONTAINER,
                halfWidth - 1, halfHeight - HALF_HEART_SIZE,
                HEART_SIZE, HEART_SIZE);


        if (this.health == 4) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.isHardcore ? FULL_HEART_HARDCORE : FULL_HEART,
                    halfWidth - 1, halfHeight - HALF_HEART_SIZE,
                    HEART_SIZE, HEART_SIZE);
            v = 2;
        } else if (this.health == 3) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.isHardcore ? HALF_HEART_HARDCORE : HALF_HEART,
                    halfWidth - 1, halfHeight - HALF_HEART_SIZE,
                    HEART_SIZE, HEART_SIZE);
            v = 1;
        } else {
            v = 0;
        }


        if (this.infection == 8) {
            u = 3;
        } else if (this.infection == 7) {
            u = 2;
        } else if (this.infection == 6) {
            u = 1;
        } else if (this.infection == 5) {
            u = 0;
        } else {
            u = -99; // prevents rendering the infected heart if the infection progress is too low
        }

        if (this.isHardcore) {
            u += 4;
        }

        graphics.enableScissor(halfWidth - 1, halfHeight - HALF_HEART_SIZE,
                halfWidth - 1 + HEART_SIZE, halfHeight + HALF_HEART_SIZE);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, INFECTION,
                halfWidth - 1 - HEART_SIZE * u, halfHeight - HALF_HEART_SIZE - HEART_SIZE * v,
                HEART_SIZE * 8, HEART_SIZE * 3);
        graphics.disableScissor();

        // ^^^ right heart rendering ^^^

        // vvv knife rendering vvv

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, KNIFE,
                (int)(halfWidth - HEART_SIZE * 0.1 - this.knifePos * HEART_SIZE / 24.0 + shakeX), (int)(halfHeight - HEART_SIZE * 0.4 - this.knifePos * HEART_SIZE / 20.0 + this.cutProgress * HEART_SIZE / 300.0 + shakeY),
                HEART_SIZE, HEART_SIZE);

        // ^^^ knife rendering ^^^

        // vvv left heart rendering vvv

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.isHardcore ? CONTAINER_HARDCORE : CONTAINER,
                halfWidth - HEART_SIZE + 1, halfHeight - HALF_HEART_SIZE,
                HEART_SIZE, HEART_SIZE);

        if (this.health >= 2) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.isHardcore ? FULL_HEART_HARDCORE : FULL_HEART,
                    halfWidth - HEART_SIZE + 1, halfHeight - HALF_HEART_SIZE,
                    HEART_SIZE, HEART_SIZE);
            v = 2;
        } else if (this.health == 1) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.isHardcore ? HALF_HEART_HARDCORE : HALF_HEART,
                    halfWidth - HEART_SIZE + 1, halfHeight - HALF_HEART_SIZE,
                    HEART_SIZE, HEART_SIZE);
            v = 1;
        } else {
            v = 0;
        }

        if (this.infection >= 4) {
            u = 3;
        } else if (this.infection == 3) {
            u = 2;
        } else if (this.infection == 2) {
            u = 1;
        } else if (this.infection == 1) {
            u = 0;
        } else {
            u = -99; // prevents rendering the infected heart if the infection progress is too low
        }

        if (this.isHardcore) {
            u += 4;
        }

        graphics.enableScissor(halfWidth - HEART_SIZE + 1, halfHeight - HALF_HEART_SIZE,
                halfWidth + 1, halfHeight + HALF_HEART_SIZE);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, INFECTION,
                halfWidth + 1 - HEART_SIZE * (u + 1), halfHeight - HALF_HEART_SIZE - HEART_SIZE * v,
                HEART_SIZE * 8, HEART_SIZE * 3);
        graphics.disableScissor();

        // ^^^ left heart rendering ^^^
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
}
