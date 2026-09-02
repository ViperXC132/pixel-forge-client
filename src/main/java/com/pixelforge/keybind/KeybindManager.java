package com.pixelforge.keybind;

import com.pixelforge.PixelForgeClient;
import com.pixelforge.module.Module;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class KeybindManager {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen != null) return;

            long window = client.getWindow().getHandle();

            for (Module module : PixelForgeClient.getInstance().getModuleManager().getModules()) {
                int key = module.getKeybind();
                if (key == -1 || key == 0) continue;

                if (GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS) {
                    // Simple edge detection would be better, but for now we use a basic toggle on press
                    // Real implementation should track previous state
                }
            }
        });
    }

    public static String getKeyName(int key) {
        if (key == -1 || key == 0) return "None";
        String name = GLFW.glfwGetKeyName(key, 0);
        if (name != null) return name.toUpperCase();
        return switch (key) {
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
            case GLFW.GLFW_KEY_LEFT_ALT -> "LALT";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "RALT";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            case GLFW.GLFW_KEY_CAPS_LOCK -> "CAPS";
            default -> "KEY" + key;
        };
    }
}
