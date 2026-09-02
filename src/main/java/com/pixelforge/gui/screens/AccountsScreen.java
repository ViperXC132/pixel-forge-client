package com.pixelforge.gui.screens;

import com.pixelforge.account.AccountManager;
import com.pixelforge.account.AccountManager.Account;
import com.pixelforge.account.AccountManager.AccountType;
import com.pixelforge.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/**
 * Accounts: Microsoft, ely.by, LittleSkin, Offline.
 * VulkanMod safe.
 */
public class AccountsScreen extends Screen {

    private final Screen parent;
    private TextFieldWidget input;
    private AccountType selectedType = AccountType.OFFLINE;
    private String error = "";

    private static final int ACCENT = 0xFF3B5BDB;
    private static final int TEXT = 0xFFC8D0E0;
    private static final int DIM = 0xFF3D4A6A;

    public AccountsScreen(Screen parent) {
        super(Text.literal("Accounts"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        input = new TextFieldWidget(textRenderer, 20, 160, width - 40, 18, Text.literal("Username"));
        input.setPlaceholder(Text.literal("Username / email"));
        addSelectableChild(input);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderUtil.fill(context, 0, 0, width, height, 0xFF0E1117);

        RenderUtil.fill(context, 0, 0, width, 36, 0xCC0A0C14);
        RenderUtil.drawText(context, textRenderer, "Accounts", 16, 12, TEXT, false);

        RenderUtil.drawText(context, textRenderer, "YOUR ACCOUNTS", 16, 48, ACCENT, false);

        int y = 64;
        for (Account acc : AccountManager.getAccounts()) {
            RenderUtil.fill(context, 16, y, width - 16, y + 28, 0x0AFFFFFF);
            RenderUtil.drawBorder(context, 16, y, width - 32, 28, 0xFF1E2540);

            RenderUtil.fill(context, 24, y + 5, 42, y + 23, 0xFF1A2040);
            RenderUtil.drawText(context, textRenderer, acc.username, 50, y + 5, TEXT, false);
            RenderUtil.drawText(context, textRenderer, acc.type.displayName + (acc.active ? " · Active" : ""),
                    50, y + 15, DIM, false);

            if (!acc.active) {
                RenderUtil.drawText(context, textRenderer, "Switch", width - 60, y + 10, ACCENT, false);
            } else {
                RenderUtil.fill(context, width - 30, y + 11, width - 24, y + 17, 0xFF40C057);
            }
            y += 32;
        }

        // Add account form
        y += 10;
        RenderUtil.drawText(context, textRenderer, "ADD ACCOUNT", 16, y, ACCENT, false);
        y += 16;

        drawTypeBtn(context, 16, y, "Microsoft", selectedType == AccountType.MICROSOFT);
        drawTypeBtn(context, 90, y, "ely.by", selectedType == AccountType.ELYBY);
        drawTypeBtn(context, 145, y, "LittleSkin", selectedType == AccountType.LITTLESKIN);
        drawTypeBtn(context, 220, y, "Offline", selectedType == AccountType.OFFLINE);

        input.setY(y + 24);
        input.render(context, mouseX, mouseY, delta);

        RenderUtil.fill(context, 20, y + 50, width - 20, y + 68, 0x403B5BDB);
        RenderUtil.drawBorder(context, 20, y + 50, width - 40, 18, ACCENT);
        RenderUtil.drawCenteredText(context, textRenderer, "+ Add account", width / 2, y + 55, 0xFF748FFF, false);

        if (!error.isEmpty()) {
            RenderUtil.drawText(context, textRenderer, error, 20, y + 74, 0xFFFA5252, false);
        }

        RenderUtil.drawText(context, textRenderer, "ESC back", 12, height - 14, DIM, false);
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawTypeBtn(DrawContext context, int x, int y, String label, boolean on) {
        int c = on ? 0xFF748FFF : DIM;
        int b = on ? ACCENT : 0xFF1E2540;
        int tw = textRenderer.getWidth(label) + 12;
        RenderUtil.fill(context, x, y, x + tw, y + 14, on ? 0x263B5BDB : 0);
        RenderUtil.drawBorder(context, x, y, tw, 14, b);
        RenderUtil.drawText(context, textRenderer, label, x + 6, y + 3, c, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int y = 64;
        for (Account acc : AccountManager.getAccounts()) {
            if (!acc.active && mouseX >= width - 70 && mouseX <= width - 20 && mouseY >= y && mouseY <= y + 28) {
                AccountManager.switchTo(acc);
                return true;
            }
            y += 32;
        }

        y += 26;
        // Type buttons
        if (mouseY >= y && mouseY <= y + 14) {
            if (mouseX >= 16 && mouseX < 85) { selectedType = AccountType.MICROSOFT; return true; }
            if (mouseX >= 90 && mouseX < 140) { selectedType = AccountType.ELYBY; return true; }
            if (mouseX >= 145 && mouseX < 215) { selectedType = AccountType.LITTLESKIN; return true; }
            if (mouseX >= 220 && mouseX < 275) { selectedType = AccountType.OFFLINE; return true; }
        }

        // Add button
        if (mouseY >= y + 50 && mouseY <= y + 68 && mouseX >= 20 && mouseX <= width - 20) {
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
