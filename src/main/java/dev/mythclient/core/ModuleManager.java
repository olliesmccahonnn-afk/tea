package dev.mythclient.core;

import dev.mythclient.MythClient;
import dev.mythclient.gui.MythScreen;
import dev.mythclient.module.hud.CoordsModule;
import dev.mythclient.module.hud.FpsModule;
import dev.mythclient.module.hud.HudModule;
import dev.mythclient.module.hud.WatermarkModule;
import dev.mythclient.module.movement.ToggleSneakModule;
import dev.mythclient.module.movement.ToggleSprintModule;
import dev.mythclient.module.world.WaypointsModule;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Owns every module: registration, keybinds, per-tick and per-frame dispatch. */
public final class ModuleManager {

    private final List<ClientModule> modules = new ArrayList<>();
    private final KeyBinding.Category keyCategory;

    public ModuleManager(KeyBinding.Category keyCategory) {
        this.keyCategory = keyCategory;
    }

    /** === ADD NEW MODULES HERE === */
    public void registerDefaults() {
        // Everything starts OFF, so a fresh install shows nothing on screen until you turn something on.
        register(new WatermarkModule());
        register(new FpsModule());
        register(new CoordsModule());
        register(new WaypointsModule());
        register(new ToggleSprintModule());
        register(new ToggleSneakModule());
    }

    public <M extends ClientModule> M register(M module) {
        String id = module.name().toLowerCase(Locale.ROOT).replace(' ', '_');
        KeyBinding key = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.mythclient.module." + id, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, keyCategory));
        module.bindKey(key);
        modules.add(module);
        return module;
    }

    public List<ClientModule> all() {
        return Collections.unmodifiableList(modules);
    }

    public List<ClientModule> in(Category category) {
        List<ClientModule> out = new ArrayList<>();
        for (ClientModule m : modules) {
            if (m.category() == category) out.add(m);
        }
        return out;
    }

    public void tick(MinecraftClient client) {
        for (ClientModule m : modules) {
            while (m.keyBinding().wasPressed()) {
                m.toggle();
                MythClient.config().save();
            }
            if (m.isEnabled() && client.player != null) {
                m.onTick();
            }
        }
    }

    /** Enabled HUD modules (what the HUD Editor can move). */
    public List<HudModule> enabledHud() {
        List<HudModule> out = new ArrayList<>();
        for (ClientModule m : modules) {
            if (m instanceof HudModule h && h.isEnabled()) out.add(h);
        }
        return out;
    }

    public void renderHud(DrawContext context) {
        // hidden by the HUD toggle key, and while a Myth screen is open (those draw their own preview)
        if (!MythClient.hudVisible()) return;
        if (MinecraftClient.getInstance().currentScreen instanceof MythScreen) return;
        for (ClientModule m : modules) {
            if (m.isEnabled()) m.onHudRender(context);
        }
    }
}
