package com.pixelforge.gui;

import com.pixelforge.PixelForgeClient;
import com.pixelforge.module.Category;
import com.pixelforge.module.Module;
import com.pixelforge.util.ColorUtil;
import com.pixelforge.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClickGui extends Screen {

    private final List<Panel> panels = new ArrayList<>();
    private String search = "";

    public ClickGui() {
        super(Text.literal("PixelForge ClickGUI"));

        int x = 20;
        for (Category category : Category.values()) {
            if (category == Category.SYSTEM) continue;
            panels.add(new Panel(category, x, 30));
            x += 120;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Dark transparent background
        RenderUtil.fill(context, 0, 0, width, height, 0xCC0A0A18);

        // Top bar
        RenderUtil.fill(context, 0, 0, width, 24, 0xEE111133);
        RenderUtil.drawText(context, textRenderer, "PixelForge • Search: " + search + (System.currentTimeMillis() % 1000 > 500 ? "_" : ""),
                8, 8, 0xFFAABBFF, true);

        for (Panel panel : panels) {
            panel.render(context, mouseX, mouseY, search);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Panel panel : panels) {
            if (panel.mouseClicked(mouseX, mouseY, button, search)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // Escape
            close();
            return true;
        }
        if (keyCode == 259 && !search.isEmpty()) { // Backspace
            search = search.substring(0, search.length() - 1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (Character.isLetterOrDigit(chr) || chr == ' ' || chr == '_' || chr == '-') {
            search += chr;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private class Panel {
        final Category category;
        int x, y;
        boolean open = true;

        Panel(Category category, int x, int y) {
            this.category = category;
            this.x = x;
            this.y = y;
        }

        void render(DrawContext context, int mouseX, int mouseY, String filter) {
            List<Module> mods = PixelForgeClient.getInstance().getModuleManager()
                    .getModulesByCategory(category).stream()
                    .filter(m -> filter.isEmpty() || m.getName().toLowerCase().contains(filter.toLowerCase()))
                    .toList();

            int headerH = 18;
            int bodyH = open ? mods.size() * 16 + 4 : 0;

            RenderUtil.drawRect(context, x, y, 110, headerH + bodyH, 0xDD0D0D22, 0xFF3344AA);
            RenderUtil.drawText(context, textRenderer, category.getDisplayName(), x + 6, y + 5, 0xFFCCDDEE, true);

            if (open) {
                int oy = y + headerH + 2;
                for (Module m : mods) {
                    int bg = m.isEnabled() ? 0xAA225522 : 0xAA222233;
                    RenderUtil.fill(context, x + 2, oy, x + 108, oy + 14, bg);
                    RenderUtil.drawText(context, textRenderer, m.getName(), x + 6, oy + 3,
                            m.isEnabled() ? 0xFF55FF55 : 0xFFAAAAAA, false);
                    oy += 16;
                }
            }
        }

        boolean mouseClicked(double mouseX, double mouseY, int button, String filter) {
            if (mouseX >= x && mouseX <= x + 110 && mouseY >= y && mouseY <= y + 18) {
                if (button == 1) open = !open;
                return true;
            }

            if (!open) return false;

            List<Module> mods = PixelForgeClient.getInstance().getModuleManager()
                    .getModulesByCategory(category).stream()
                    .filter(m -> filter.isEmpty() || m.getName().toLowerCase().contains(filter.toLowerCase()))
                    .toList();

            int oy = y + 20;
            for (Module m : mods) {
                if (mouseX >= x + 2 && mouseX <= x + 108 && mouseY >= oy && mouseY <= oy + 14) {
                    if (button == 0) m.toggle();
                    return true;
                }
                oy += 16;
            }
            return false;
        }
    }
}
