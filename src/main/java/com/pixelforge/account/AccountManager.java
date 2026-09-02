package com.pixelforge.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pixelforge.PixelForgeClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Local account list for Offline / ely.by / LittleSkin / Microsoft (label only for MS for now).
 * Full Microsoft OAuth requires browser flow and is intentionally not auto-completed here.
 * Offline accounts work immediately for local play.
 */
public final class AccountManager {

    public enum AccountType {
        MICROSOFT("Microsoft · Premium"),
        ELYBY("ely.by · Cracked"),
        LITTLESKIN("LittleSkin"),
        OFFLINE("Offline");

        public final String displayName;
        AccountType(String displayName) { this.displayName = displayName; }
    }

    public static class Account {
        public String username;
        public AccountType type;
        public boolean active;

        public Account() {}

        public Account(String username, AccountType type, boolean active) {
            this.username = username;
            this.type = type;
            this.active = active;
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("pixelforge/accounts.json");
    private static final List<Account> ACCOUNTS = new ArrayList<>();

    static {
        load();
        if (ACCOUNTS.isEmpty()) {
            // Seed with current session name if possible
            ACCOUNTS.add(new Account("Player", AccountType.OFFLINE, true));
            save();
        }
    }

    private AccountManager() {}

    public static List<Account> getAccounts() {
        return Collections.unmodifiableList(ACCOUNTS);
    }

    public static void add(String username, AccountType type) {
        for (Account a : ACCOUNTS) a.active = false;
        ACCOUNTS.add(new Account(username, type, true));
        save();
        PixelForgeClient.LOGGER.info("Added account {} ({})", username, type);
    }

    public static void switchTo(Account account) {
        for (Account a : ACCOUNTS) a.active = (a == account);
        save();
        PixelForgeClient.getInstance().getNotificationManager()
                .push("Switched to " + account.username, 0xFF3B5BDB);
    }

    public static Account getActive() {
        return ACCOUNTS.stream().filter(a -> a.active).findFirst().orElse(null);
    }

    private static void load() {
        try {
            if (!Files.exists(FILE)) return;
            String json = Files.readString(FILE);
            Type type = new TypeToken<List<Account>>(){}.getType();
            List<Account> loaded = GSON.fromJson(json, type);
            if (loaded != null) {
                ACCOUNTS.clear();
                ACCOUNTS.addAll(loaded);
            }
        } catch (Exception e) {
            PixelForgeClient.LOGGER.warn("Failed to load accounts", e);
        }
    }

    private static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(ACCOUNTS));
        } catch (IOException e) {
            PixelForgeClient.LOGGER.error("Failed to save accounts", e);
        }
    }
}
