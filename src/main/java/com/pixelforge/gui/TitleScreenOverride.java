package com.pixelforge.gui;

import com.pixelforge.util.ColorUtil;
import com.pixelforge.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TitleScreenOverride extends Screen {

    private final List<Star> stars = new ArrayList<>();
    private final Random random = new Random();
    private int selectedTab = 0; // 0 Home, 1 Mods, 2 Crosshair, 3 Accounts

    public TitleScreenOverride() {
        super(Text.literal("PixelForge"));
        for (int i = 0; i < 120; i++) {
            stars.add(new Star(random.nextInt(800), random.nextInt(500), random.nextFloat() * 1.5f + 0.5f));
        }
    }

    @Override
    protected void init() {
        // Left side buttons
        int btnWidth = 140;
        int btnHeight = 22;
        int leftX = 40;
        int startY = height / 2 - 70;

        addDrawableChild(ButtonWidget.builder(Text.literal("Singleplayer"), b ->
                client.setScreen(new SelectWorldScreen(this)))
                .dimensions(leftX, startY, btnWidth, btnHeight).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Multiplayer"), b ->
                client.setScreen(new MultiplayerScreen(this)))
                .dimensions(leftX, startY + 28, btnWidth, btnHeight).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Options"), b ->
                client.setScreen(new OptionsScreen(this, client.options)))
                .dimensions(leftX, startY + 56, btnWidth, btnHeight).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Quit"), b ->
                client.scheduleStop())
                .dimensions(leftX, startY + 84, btnWidth, btnHeight).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Background
        RenderUtil.fill(context, 0, 0, width, height, 0xFF0A0A1A);

        // Animated stars
        for (Star star : stars) {
            star.y += star.speed * 0.3f;
            if (star.y > height) {
                star.y = 0;
                star.x = random.nextInt(Math.max(1, width));
            }
            int alpha = (int) (180 + 75 * Math.sin(System.currentTimeMillis() / 400.0 + star.x));
            RenderUtil.fill(context, (int) star.x, (int) star.y, (int) star.x + 1, (int) star.y + 1,
                    ColorUtil.rgba(180, 190, 255, Math.min(255, alpha)));
        }

        // Top nav bar
        RenderUtil.fill(context, 0, 0, width, 28, 0xEE111133);
        String[] tabs = {"Home", "Mods", "Crosshair", "Accounts"};
        int tabX = 20;
        for (int i = 0; i < tabs.length; i++) {
            int color = (i == selectedTab) ? 0xFF6688FF : 0xFFAAAAAA;
            RenderUtil.drawText(context, textRenderer, tabs[i], tabX, 10, color, true);
            tabX += textRenderer.getWidth(tabs[i]) + 24;
        }

        // Title
        RenderUtil.drawCenteredText(context, textRenderer, "PixelForge", width / 2, 50, 0xFF88AAFF, true);
        RenderUtil.drawCenteredText(context, textRenderer, "1.21.11 • Clean Utility Client", width / 2, 64, 0xFF6677AA, false);

        // Right side - Accounts panel placeholder
        int panelX = width - 200;
        int panelY = height / 2 - 80;
        RenderUtil.drawRect(context, panelX, panelY, 180, 160, 0xCC111122, 0xFF3344AA);
        RenderUtil.drawText(context, textRenderer, "Accounts", panelX + 12, panelY + 10, 0xFFAABBFF, true);
        RenderUtil.drawText(context, textRenderer, "Offline / Microsoft", panelX + 12, panelY + 30, 0xFF888888, false);
        RenderUtil.drawText(context, textRenderer, "Active: Current", panelX + 12, panelY + 50, 0xFF55FF55, false);

        // Bottom bar
        RenderUtil.fill(context, 0, height - 22, width, height, 0xEE111133);
        RenderUtil.drawText(context, textRenderer, "PixelForge 1.0.0 • by ViperXC132", 8, height - 15, 0xFF6677AA, false);
        RenderUtil.drawText(context, textRenderer, "github.com/ViperXC132/pixel-forge-client", width - 220, height - 15, 0xFF445588, false);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Simple tab switching
        if (mouseY < 28) {
            int tabX = 20;
            String[] tabs = {"Home", "Mods", "Crosshair", "Accounts"};
            for (int i = 0; i < tabs.length; i++) {
                int w = textRenderer.getWidth(tabs[i]);
                if (mouseX >= tabX && mouseX <= tabX + w) {
                    selectedTab = i;
                    return true;
                }
                tabX += w + 24;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static class Star {
        float x, y, speed;
        Star(float x, float y, float speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
        }
    }
}
