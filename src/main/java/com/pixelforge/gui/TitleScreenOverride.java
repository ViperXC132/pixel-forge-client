package com.pixelforge.gui;

import com.pixelforge.gui.screens.AccountsScreen;
import com.pixelforge.gui.screens.CrosshairScreen;
import com.pixelforge.gui.screens.ModsScreen;
import com.pixelforge.util.ColorUtil;
import com.pixelforge.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Lunar-style transparent main menu.
 * Dark indigo theme, star field, left menu, right accounts + quick servers.
 * NO center crosshair. VulkanMod safe (DrawContext only).
 */
public class TitleScreenOverride extends Screen {

    private final List<Star> stars = new ArrayList<>();
    private final Random random = new Random();
    private int selectedTab = 0; // 0 Home

    // Colors matching the mockup
    private static final int BG           = 0xFF0E1117;
    private static final int NAV_BG       = 0xCC0A0C14;
    private static final int ACCENT       = 0xFF3B5BDB;
    private static final int TEXT         = 0xFFC8D0E0;
    private static final int TEXT_DIM     = 0xFF8892A8;
    private static final int TEXT_MUTED   = 0xFF3D4A6A;
    private static final int PANEL_BG     = 0xE00A0D18;
    private static final int PANEL_BORDER = 0xFF1E2540;
    private static final int BTN_BG       = 0x0DFFFFFF;
    private static final int BTN_PRIMARY  = 0x403B5BDB;

    public TitleScreenOverride() {
        super(Text.literal("PixelForge"));
        for (int i = 0; i < 80; i++) {
            stars.add(new Star(
                    random.nextFloat() * 900,
                    random.nextFloat() * 500,
                    random.nextFloat() * 0.6f + 0.2f,
                    random.nextFloat() * 0.5f + 0.15f
            ));
        }
    }

    @Override
    protected void init() {
        int leftX = 28;
        int startY = height / 2 - 90;
        int bw = 150;
        int bh = 22;

        addDrawableChild(btn("Singleplayer", leftX, startY, bw, bh, true, b ->
                client.setScreen(new SelectWorldScreen(this))));

        addDrawableChild(btn("Multiplayer", leftX, startY + 28, bw, bh, false, b ->
                client.setScreen(new MultiplayerScreen(this))));

        addDrawableChild(btn("Mod Menu", leftX, startY + 64, bw, bh, false, b ->
                client.setScreen(new ModsScreen(this))));

        addDrawableChild(btn("Options", leftX, startY + 92, bw, bh, false, b ->
                client.setScreen(new OptionsScreen(this, client.options))));

        addDrawableChild(btn("Quit", leftX, startY + 120, bw, bh, false, b ->
                client.scheduleStop()));
    }

    private ButtonWidget btn(String label, int x, int y, int w, int h, boolean primary, ButtonWidget.PressAction action) {
        return ButtonWidget.builder(Text.literal(label), action)
                .dimensions(x, y, w, h)
                .build();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Base background
        RenderUtil.fill(context, 0, 0, width, height, BG);

        // Subtle grid
        for (int gx = 0; gx < width; gx += 40) {
            RenderUtil.fill(context, gx, 0, gx + 1, height, 0x04FFFFFF);
        }
        for (int gy = 0; gy < height; gy += 40) {
            RenderUtil.fill(context, 0, gy, width, gy + 1, 0x04FFFFFF);
        }

        // Stars
        for (Star s : stars) {
            s.y += s.speed * 0.25f;
            if (s.y > height) {
                s.y = 0;
                s.x = random.nextFloat() * width;
            }
            int a = (int) (s.alpha * 255);
            RenderUtil.fill(context, (int) s.x, (int) s.y, (int) s.x + 1, (int) s.y + 1,
                    ColorUtil.rgba(200, 210, 255, Math.min(255, a)));
        }

        // Nav bar
        RenderUtil.fill(context, 0, 0, width, 30, NAV_BG);
        RenderUtil.fill(context, 0, 29, width, 30, 0x0FFFFFFF);

        // Logo square
        RenderUtil.fill(context, 14, 6, 30, 22, ACCENT);
        RenderUtil.fill(context, 18, 10, 26, 18, 0xFFFFFFFF);

        RenderUtil.drawText(context, textRenderer, "PixelForge", 36, 10, TEXT, false);
        RenderUtil.drawText(context, textRenderer, "1.21.11", 100, 11, ACCENT, false);

        // Nav tabs
        drawNavTab(context, "Home", 200, 0, selectedTab == 0, mouseX, mouseY);
        drawNavTab(context, "Mods", 250, 1, selectedTab == 1, mouseX, mouseY);
        drawNavTab(context, "Crosshair", 300, 2, selectedTab == 2, mouseX, mouseY);
        drawNavTab(context, "Accounts", 370, 3, selectedTab == 3, mouseX, mouseY);

        // Center splash only (no crosshair)
        RenderUtil.drawCenteredText(context, textRenderer, "No hacks. Just vibes.",
                width / 2, height / 2 - 10, TEXT_MUTED, false);

        // Right column — Accounts panel
        int px = width - 220;
        int py = 48;
        drawPanel(context, px, py, 200, 130);
        RenderUtil.drawText(context, textRenderer, "ACCOUNTS", px + 10, py + 8, ACCENT, false);
        RenderUtil.drawText(context, textRenderer, "manage", px + 150, py + 8, TEXT_MUTED, false);

        // Fake account rows (real data comes from AccountManager later)
        drawAccountRow(context, px + 6, py + 28, "ViperXC132", "Microsoft · Premium", true);
        drawAccountRow(context, px + 6, py + 58, "Offline", "Offline account", false);
        RenderUtil.drawText(context, textRenderer, "+ Add account", px + 14, py + 100, TEXT_MUTED, false);

        // Quick connect panel
        int qy = py + 142;
        drawPanel(context, px, qy, 200, 110);
        RenderUtil.drawText(context, textRenderer, "QUICK CONNECT", px + 10, qy + 8, ACCENT, false);
        drawServerRow(context, px + 6, qy + 28, "Hypixel", "mc.hypixel.net", "34ms", true);
        drawServerRow(context, px + 6, qy + 54, "CubeCraft", "play.cubecraft.net", "72ms", true);
        drawServerRow(context, px + 6, qy + 80, "My SMP", "play.mysmp.net", "off", false);

        // Bottom bar
        RenderUtil.fill(context, 0, height - 20, width, height, 0xD9080A12);
        RenderUtil.drawText(context, textRenderer, "PixelForge v1.0.0 · Fabric 1.21.11 · Java 21",
                12, height - 14, TEXT_MUTED, false);
        RenderUtil.drawText(context, textRenderer, "Discord  ·  GitHub  ·  Report bug",
                width - 160, height - 14, TEXT_MUTED, false);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawNavTab(DrawContext context, String label, int x, int id, boolean active, int mx, int my) {
        int color = active ? 0xFF748FFF : TEXT_DIM;
        RenderUtil.drawText(context, textRenderer, label, x, 10, color, false);
    }

    private void drawPanel(DrawContext context, int x, int y, int w, int h) {
        RenderUtil.fill(context, x, y, x + w, y + h, PANEL_BG);
        RenderUtil.drawBorder(context, x, y, w, h, PANEL_BORDER);
    }

    private void drawAccountRow(DrawContext context, int x, int y, String name, String type, boolean active) {
        // Skin placeholder
        RenderUtil.fill(context, x, y, x + 18, y + 18, 0xFF1A2040);
        RenderUtil.drawBorder(context, x, y, 18, 18, 0xFF2D3560);
        RenderUtil.drawText(context, textRenderer, name, x + 24, y + 1, TEXT, false);
        RenderUtil.drawText(context, textRenderer, type, x + 24, y + 10, TEXT_MUTED, false);
        if (active) {
            RenderUtil.fill(context, x + 175, y + 6, x + 181, y + 12, 0xFF40C057);
        }
    }

    private void drawServerRow(DrawContext context, int x, int y, String name, String addr, String ping, boolean online) {
        int dot = online ? 0xFF40C057 : 0xFFFA5252;
        RenderUtil.fill(context, x + 2, y + 5, x + 8, y + 11, dot);
        RenderUtil.drawText(context, textRenderer, name, x + 14, y, TEXT, false);
        RenderUtil.drawText(context, textRenderer, addr, x + 14, y + 9, TEXT_MUTED, false);
        RenderUtil.drawText(context, textRenderer, ping, x + 155, y + 4, online ? 0xFF40C057 : 0xFFFA5252, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseY < 30) {
            if (mouseX >= 200 && mouseX < 245) { selectedTab = 0; return true; }
            if (mouseX >= 250 && mouseX < 290) {
                client.setScreen(new ModsScreen(this));
                return true;
            }
            if (mouseX >= 300 && mouseX < 360) {
                client.setScreen(new CrosshairScreen(this));
                return true;
            }
            if (mouseX >= 370 && mouseX < 430) {
                client.setScreen(new AccountsScreen(this));
                return true;
            }
        }

        // Manage accounts click
        int px = width - 220;
        if (mouseX >= px + 150 && mouseX <= px + 195 && mouseY >= 56 && mouseY <= 68) {
            client.setScreen(new AccountsScreen(this));
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static class Star {
        float x, y, speed, alpha;
        Star(float x, float y, float speed, float alpha) {
            this.x = x; this.y = y; this.speed = speed; this.alpha = alpha;
        }
    }
}
