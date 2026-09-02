package com.pixelforge.gui.screens;

import com.pixelforge.PixelForgeClient;
import com.pixelforge.module.modules.visual.CustomCrosshairModule;
import com.pixelforge.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CrosshairScreen extends Screen {

    private final Screen parent;

    private static final int ACCENT = 0xFF3B5BDB;
    private static final int TEXT = 0xFFC8D0E0;
    private static final int DIM = 0xFF3D4A6A;

    public CrosshairScreen(Screen parent) {
        super(Text.literal("Crosshair"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderUtil.fill(context, 0, 0, width, height, 0xFF0E1117);

        RenderUtil.fill(context, 0, 0, width, 36, 0xCC0A0C14);
        RenderUtil.drawText(context, textRenderer, "Crosshair Module", 16, 12, TEXT, false);

        CustomCrosshairModule mod = PixelForgeClient.getInstance() != null
                ? PixelForgeClient.getInstance().getModuleManager().getModule(CustomCrosshairModule.class)
                : null;

        RenderUtil.drawText(context, textRenderer, "STYLE", 20, 50, ACCENT, false);

        String[] styles = {"Cross", "Dot", "Circle", "Cross+Dot", "Gap"};
        CustomCrosshairModule.Style[] enums = {
                CustomCrosshairModule.Style.CROSS,
                CustomCrosshairModule.Style.DOT,
                CustomCrosshairModule.Style.CIRCLE,
                CustomCrosshairModule.Style.CROSS_DOT,
                CustomCrosshairModule.Style.GAP
        };

        int x = 20;
        for (int i = 0; i < styles.length; i++) {
            boolean on = mod != null && mod.getStyle() == enums[i];
            int tw = textRenderer.getWidth(styles[i]) + 16;
            RenderUtil.fill(context, x, 66, x + tw, 82, on ? 0x263B5BDB : 0);
            RenderUtil.drawBorder(context, x, 66, tw, 16, on ? ACCENT : 0xFF1E2540);
            RenderUtil.drawText(context, textRenderer, styles[i], x + 8, 70, on ? 0xFF748FFF : DIM, false);
            x += tw + 8;
        }

        // Live preview
        int cx = 60;
        int cy = 130;
        RenderUtil.fill(context, 20, 100, 100, 180, 0xFF111827);
        RenderUtil.drawBorder(context, 20, 100, 80, 80, 0xFF1E2540);
        if (mod != null) {
            mod.renderCrosshair(context, cx, cy);
        }

        RenderUtil.drawText(context, textRenderer, "Replace vanilla: ON", 120, 110, 0xFF40C057, false);
        RenderUtil.drawText(context, textRenderer, "Status: " + (mod != null && mod.isEnabled() ? "Enabled" : "Disabled"),
                120, 124, TEXT, false);

        RenderUtil.drawText(context, textRenderer, "ESC back", 12, height - 14, DIM, false);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        CustomCrosshairModule mod = PixelForgeClient.getInstance() != null
                ? PixelForgeClient.getInstance().getModuleManager().getModule(CustomCrosshairModule.class)
                : null;
        if (mod == null) return super.mouseClicked(mouseX, mouseY, button);

        CustomCrosshairModule.Style[] enums = {
                CustomCrosshairModule.Style.CROSS,
                CustomCrosshairModule.Style.DOT,
                CustomCrosshairModule.Style.CIRCLE,
                CustomCrosshairModule.Style.CROSS_DOT,
                CustomCrosshairModule.Style.GAP
        };
        String[] styles = {"Cross", "Dot", "Circle", "Cross+Dot", "Gap"};

        int x = 20;
        for (int i = 0; i < styles.length; i++) {
            int tw = textRenderer.getWidth(styles[i]) + 16;
            if (mouseX >= x && mouseX <= x + tw && mouseY >= 66 && mouseY <= 82) {
                mod.setStyle(enums[i]);
                return true;
            }
            x += tw + 8;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            client.setScreen(parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
