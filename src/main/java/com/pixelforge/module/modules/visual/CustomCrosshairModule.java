package com.pixelforge.module.modules.visual;

import com.pixelforge.module.Category;
import com.pixelforge.module.Module;
import com.pixelforge.util.ColorUtil;
import com.pixelforge.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

public class CustomCrosshairModule extends Module {

    public enum Style { CROSS, DOT, CIRCLE, CROSS_DOT, GAP }

    private Style style = Style.CROSS;
    private int color = 0xFFFFFFFF;
    private int size = 6;
    private int thickness = 1;
    private int gap = 2;
    private boolean replaceVanilla = true;

    public CustomCrosshairModule() {
        super("Custom Crosshair", "Replaces the vanilla crosshair with custom styles", Category.VISUAL);
        setEnabled(true);
    }

    public void renderCrosshair(DrawContext context, int centerX, int centerY) {
        if (!isEnabled()) return;

        switch (style) {
            case DOT -> {
                RenderUtil.fill(context, centerX - 1, centerY - 1, centerX + 2, centerY + 2, color);
            }
            case CIRCLE -> {
                // Simple diamond / circle approximation
                for (int i = -size; i <= size; i++) {
                    for (int j = -size; j <= size; j++) {
                        int dist = i * i + j * j;
                        if (dist >= (size - 1) * (size - 1) && dist <= size * size) {
                            RenderUtil.fill(context, centerX + i, centerY + j, centerX + i + 1, centerY + j + 1, color);
                        }
                    }
                }
            }
            case CROSS_DOT -> {
                drawCross(context, centerX, centerY);
                RenderUtil.fill(context, centerX - 1, centerY - 1, centerX + 2, centerY + 2, color);
            }
            case GAP -> {
                // Cross with gap in the middle
                // horizontal left
                RenderUtil.fill(context, centerX - size - gap, centerY - thickness / 2, centerX - gap, centerY + thickness / 2 + 1, color);
                // horizontal right
                RenderUtil.fill(context, centerX + gap + 1, centerY - thickness / 2, centerX + size + gap + 1, centerY + thickness / 2 + 1, color);
                // vertical top
                RenderUtil.fill(context, centerX - thickness / 2, centerY - size - gap, centerX + thickness / 2 + 1, centerY - gap, color);
                // vertical bottom
                RenderUtil.fill(context, centerX - thickness / 2, centerY + gap + 1, centerX + thickness / 2 + 1, centerY + size + gap + 1, color);
            }
            default -> drawCross(context, centerX, centerY); // CROSS
        }
    }

    private void drawCross(DrawContext context, int cx, int cy) {
        // horizontal
        RenderUtil.fill(context, cx - size, cy - thickness / 2, cx + size + 1, cy + thickness / 2 + 1, color);
        // vertical
        RenderUtil.fill(context, cx - thickness / 2, cy - size, cx + thickness / 2 + 1, cy + size + 1, color);
    }

    public boolean shouldReplaceVanilla() {
        return isEnabled() && replaceVanilla;
    }

    public Style getStyle() { return style; }
    public void setStyle(Style style) { this.style = style; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
