package com.pixelforge.gui.screens;

import com.pixelforge.account.AccountManager;
import com.pixelforge.account.AccountManager.Account;
import com.pixelforge.account.AccountManager.AccountType;
import com.pixelforge.account.SkinHelper;
import com.pixelforge.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AccountsScreen extends Screen {

    private final Screen parent;
    private TextFieldWidget input;
    private AccountType selectedType = AccountType.OFFLINE;
    private String error = "";

    private static final int ACCENT = 0xFF3B5BDB;
    private static final int TEXT = 0xFFC8D0E0;
    private static final int DIM = 0xFF8892A8;
    private static final int MUTED = 0xFF3D4A6A;
    private static final int PANEL = 0xD0101424; // opaque-transparent Lunar

    public AccountsScreen(Screen parent) {
        super(Text.literal("Accounts"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        input = new TextFieldWidget(textRenderer, 20, 180, width - 40, 18, Text.literal("Username"));
        input.setPlaceholder(Text.literal("Username / email"));
        addSelectableChild(input);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Lunar dim — still see through slightly
        RenderUtil.fill(context, 0, 0, width, height, 0xB0080A12);

        RenderUtil.fill(context, 0, 0, width, 32, 0xE00A0C14);
        RenderUtil.drawText(context, textRenderer, "Accounts", 14, 11, TEXT, false);

        RenderUtil.drawText(context, textRenderer, "YOUR ACCOUNTS", 16, 44, ACCENT, false);

        int y = 58;
        for (Account acc : AccountManager.getAccounts()) {
            RenderUtil.fill(context, 16, y, width - 16, y + 30, PANEL);
            RenderUtil.drawBorder(context, 16, y, width - 32, 30, 0xFF1E2540);

            // Real skin head preview
            SkinHelper.drawHead(context, acc.username, 22, y + 5, 20);

            RenderUtil.drawText(context, textRenderer, acc.username, 48, y + 5, TEXT, false);
            RenderUtil.drawText(context, textRenderer,
                    acc.type.displayName + (acc.active ? " · Active" : ""),
                    48, y + 16, MUTED, false);

            if (!acc.active) {
                RenderUtil.drawText(context, textRenderer, "Switch", width - 58, y + 11, ACCENT, false);
            } else {
                RenderUtil.fill(context, width - 28, y + 12, width - 22, y + 18, 0xFF40C057);
            }
            y += 34;
        }

        y += 8;
        RenderUtil.drawText(context, textRenderer, "ADD ACCOUNT", 16, y, ACCENT, false);
        y += 14;

        drawTypeBtn(context, 16, y, "Microsoft", selectedType == AccountType.MICROSOFT);
        drawTypeBtn(context, 90, y, "ely.by", selectedType == AccountType.ELYBY);
        drawTypeBtn(context, 145, y, "LittleSkin", selectedType == AccountType.LITTLESKIN);
        drawTypeBtn(context, 220, y, "Offline", selectedType == AccountType.OFFLINE);

        input.setY(y + 22);
        input.render(context, mouseX, mouseY, delta);

        // Live head preview for typed name
        String typed = input.getText().trim();
        if (!typed.isEmpty()) {
            SkinHelper.drawHead(context, typed, width - 48, y + 20, 24);
        }

        RenderUtil.fill(context, 20, y + 48, width - 20, y + 66, 0x403B5BDB);
        RenderUtil.drawBorder(context, 20, y + 48, width - 40, 18, ACCENT);
        RenderUtil.drawCenteredText(context, textRenderer, "+ Add account", width / 2, y + 53, 0xFF748FFF, false);

        if (!error.isEmpty()) {
            RenderUtil.drawText(context, textRenderer, error, 20, y + 72, 0xFFFA5252, false);
        }

        RenderUtil.drawText(context, textRenderer, "ESC back", 12, height - 14, MUTED, false);
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawTypeBtn(DrawContext context, int x, int y, String label, boolean on) {
        int c = on ? 0xFF748FFF : MUTED;
        int b = on ? ACCENT : 0xFF1E2540;
        int tw = textRenderer.getWidth(label) + 12;
        RenderUtil.fill(context, x, y, x + tw, y + 14, on ? 0x303B5BDB : PANEL);
        RenderUtil.drawBorder(context, x, y, tw, 14, b);
        RenderUtil.drawText(context, textRenderer, label, x + 6, y + 3, c, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int y = 58;
        for (Account acc : AccountManager.getAccounts()) {
            if (!acc.active && mouseX >= width - 70 && mouseX <= width - 16 && mouseY >= y && mouseY <= y + 30) {
                AccountManager.switchTo(acc);
                return true;
            }
            y += 34;
        }

        y += 22;
        if (mouseY >= y && mouseY <= y + 14) {
            if (mouseX >= 16 && mouseX < 85) { selectedType = AccountType.MICROSOFT; return true; }
            if (mouseX >= 90 && mouseX < 140) { selectedType = AccountType.ELYBY; return true; }
            if (mouseX >= 145 && mouseX < 215) { selectedType = AccountType.LITTLESKIN; return true; }
            if (mouseX >= 220 && mouseX < 275) { selectedType = AccountType.OFFLINE; return true; }
        }

        if (mouseY >= y + 48 && mouseY <= y + 66 && mouseX >= 20 && mouseX <= width - 20) {
            String name = input.getText().trim();
            if (name.isEmpty()) {
                error = "Enter a username first.";
                return true;
            }
            error = "";
            AccountManager.add(name, selectedType);
            input.setText("");
            return true;
        }

        return input.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            client.setScreen(parent);
            return true;
        }
        return input.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return input.charTyped(chr, modifiers) || super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
