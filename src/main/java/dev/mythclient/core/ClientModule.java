package dev.mythclient.core;

import dev.mythclient.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for every Myth Client feature.
 *
 * Override the hooks you need:
 *   onEnable / onDisable   - state changes
 *   onTick                 - every client tick, only while enabled and in a world
 *   onHudRender            - every HUD frame, only while enabled
 * Add settings with {@link #setting(Setting)}. They are saved automatically.
 * Each module also gets its own (unbound) keybind in Options > Controls > Myth Client.
 */
public abstract class ClientModule {

    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final String description;
    private final Category category;
    private final List<Setting<?>> settings = new ArrayList<>();
    private boolean enabled;
    private KeyBinding keyBinding;

    protected ClientModule(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected <S extends Setting<?>> S setting(S setting) {
        settings.add(setting);
        return setting;
    }

    // ---- hooks -------------------------------------------------------
    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public void onTick() {
    }

    public void onHudRender(DrawContext context) {
    }

    // ---- state -------------------------------------------------------
    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean value) {
        if (enabled == value) return;
        enabled = value;
        if (value) onEnable();
        else onDisable();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public Category category() {
        return category;
    }

    public List<Setting<?>> settings() {
        return Collections.unmodifiableList(settings);
    }

    public KeyBinding keyBinding() {
        return keyBinding;
    }

    void bindKey(KeyBinding keyBinding) {
        this.keyBinding = keyBinding;
    }
}
