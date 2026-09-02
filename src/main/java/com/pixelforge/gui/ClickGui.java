package com.pixelforge.gui;

import com.pixelforge.PixelForgeClient;
import com.pixelforge.config.ProfileManager;
import com.pixelforge.module.Category;
import com.pixelforge.module.Module;
import com.pixelforge.util.ColorUtil;
import com.pixelforge.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Lunar-style ClickGUI — short, classy, transparent panels.
 * Open with Right Shift.
 */
public class ClickGui extends Screen {

    private final List<Panel> panels = new ArrayList<>();
    private String search = "";
    private int profileIndex = 0;
    private final String[] profileNames;

    // Colors — Lunar inspired
    private static final int BG_PANEL     = 0xB80C0C1A; // transparent dark
    private static final int BG_HEADER    = 0xD0121230;
    private static final int ACCENT       = 0xFF5B7CFF;
    private static final int TEXT_MAIN    = 0xFFE8ECFF;
    private static final int TEXT_DIM     = 0xFF8A90B0;
    private static final int ENABLED_BG   = 0xA0183A28;
    private static final int DISABLED_BG  = 0xA0161628;
    private static final int HOVER_BG     = 0xC0222240;

    public ClickGui() {
        super(Text.literal("PixelForge"));
        profileNames = ProfileManager.getProfileNames();

        int x = 16;
        for (Category category : Category.values()) {
            if (category == Category.SYSTEM) continue;
            panels.add(new Panel(category, x, 36));
            x += 108;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Very light dim so the world stays visible (Lunar style)
        RenderUtil.fill(context, 0, 0, width, height, 0x66000000);

        // Top bar
        RenderUtil.fill(context, 0, 0, width, 28, 0xE00C0C1E);
        RenderUtil.fill(context, 0, 27, width, 28, ACCENT);

        RenderUtil.drawText(context, textRenderer, "PixelForge", 10, 9, ACCENT, false);

        // Search
        String searchDisplay = search.isEmpty() ? "Search..." : search + (System.currentTimeMillis() % 1000 > 500 ? "|" : "");
        int searchColor = search.isEmpty() ? TEXT_DIM : TEXT_MAIN;
        RenderUtil.drawText(context, textRenderer, searchDisplay, 90, 9, searchColor, false);

        // Profile selector on the right
        String profileLabel = "Profile: " + profileNames[profileIndex];
        int pw = textRenderer.getWidth(profileLabel);
        RenderUtil.drawText(context, textRenderer, profileLabel, width - pw - 12, 9, TEXT_MAIN, false);

        for (Panel panel : panels) {
            panel.render(context, mouseX, mouseY, search);
        }

        // Bottom hint
        RenderUtil.drawText(context, textRenderer, "RShift close  |  LMB toggle  |  RMB collapse  |  Click profile name to cycle", 8, height - 12, TEXT_DIM, false);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Profile cycle
        String profileLabel = "Profile: " + profileNames[profileIndex];
        int pw = textRenderer.getWidth(profileLabel);
        if (mouseY < 28 && mouseX >= width - pw - 16) {
            if (button == 0) {
                profileIndex = (profileIndex + 1) % profileNames.length;
                ProfileManager.loadProfile(profileNames[profileIndex]);
                PixelForgeClient.getInstance().getNotificationManager()
                        .push("Profile: " + profileNames[profileIndex], ACCENT);
            }
            return true;
        }

        for (Panel panel : panels) {
            if (panel.mouseClicked(mouseX, mouseY, button, search)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 || keyCode == 344) { // Escape or Right Shift
            close();
            return true;
        }
        if (keyCode == 259 && !search.isEmpty()) {
            search = search.substring(0, search.length() - 1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (Character.isLetterOrDigit(chr) || chr == ' ' || chr == '_' || chr == '-') {
            if (search.length() < 24) search += chr;
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
        final int x;
        final int y;
        boolean open = true;
        private static final int WIDTH = 100;

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

            int headerH = 16;
            int rowH = 14;
            int bodyH = open ? mods.size() * rowH + 4 : 0;

            // Transparent panel background
            RenderUtil.fill(context, x, y, x + WIDTH, y + headerH + bodyH, BG_PANEL);
            // Accent line on left
            RenderUtil.fill(context, x, y, x + 2, y + headerH + bodyH, ACCENT);
            // Header
            RenderUtil.fill(context, x + 2, y, x + WIDTH, y + headerH, BG_HEADER);
            RenderUtil.drawText(context, textRenderer, category.getDisplayName(), x + 7, y + 4, TEXT_MAIN, false);

            if (open) {
                int oy = y + headerH + 2;
                for (Module m : mods) {
                    boolean hover = mouseX >= x + 2 && mouseX <= x + WIDTH - 2 && mouseY >= oy && mouseY <= oy + rowH - 1;
                    int bg = m.isEnabled() ? ENABLED_BG : (hover ? HOVER_BG : DISABLED_BG);
                    RenderUtil.fill(context, x + 3, oy, x + WIDTH - 2, oy + rowH - 1, bg);

                    int tc = m.isEnabled() ? 0xFF6DFF9A : TEXT_DIM;
                    RenderUtil.drawText(context, textRenderer, m.getName(), x + 7, oy + 3, tc, false);
                    oy += rowH;
                }
            }
        }

        boolean mouseClicked(double mouseX, double mouseY, int button, String filter) {
            if (mouseX >= x && mouseX <= x + WIDTH && mouseY >= y && mouseY <= y + 16) {
                if (button == 1) open = !open;
                return true;
            }
            if (!open) return false;

            List<Module> mods = PixelForgeClient.getInstance().getModuleManager()
                    .getModulesByCategory(category).stream()
                    .filter(m -> filter.isEmpty() || m.getName().toLowerCase().contains(filter.toLowerCase()))
                    .toList();

            int oy = y + 18;
            for (Module m : mods) {
                if (mouseX >= x + 3 && mouseX <= x + WIDTH - 2 && mouseY >= oy && mouseY <= oy + 13) {
                    if (button == 0) m.toggle();
                    return true;
                }
                oy += 14;
            }
            return false;
        }
    }
}
