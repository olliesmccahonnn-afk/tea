package dev.mythclient.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.mythclient.MythClient;
import dev.mythclient.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Saves/loads module state + settings to {@code config/mythclient.json}. */
public final class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path file = FabricLoader.getInstance().getConfigDir().resolve("mythclient.json");
    private final ModuleManager modules;

    public ConfigManager(ModuleManager modules) {
        this.modules = modules;
    }

    public void load() {
        if (!Files.exists(file)) return;
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject mods = root.has("modules") ? root.getAsJsonObject("modules") : null;
            if (mods == null) return;
            for (ClientModule m : modules.all()) {
                if (!mods.has(m.name())) continue;
                JsonObject o = mods.getAsJsonObject(m.name());
                if (o.has("settings")) {
                    JsonObject s = o.getAsJsonObject("settings");
                    for (Setting<?> setting : m.settings()) {
                        if (s.has(setting.name())) setting.fromJson(s.get(setting.name()));
                    }
                }
                if (o.has("enabled")) m.setEnabled(o.get("enabled").getAsBoolean());
            }
        } catch (Exception e) {
            MythClient.LOGGER.warn("Could not read {} - using defaults", file, e);
        }
    }

    public void save() {
        JsonObject root = new JsonObject();
        JsonObject mods = new JsonObject();
        for (ClientModule m : modules.all()) {
            JsonObject o = new JsonObject();
            o.addProperty("enabled", m.isEnabled());
            JsonObject s = new JsonObject();
            for (Setting<?> setting : m.settings()) {
                s.add(setting.name(), setting.toJson());
            }
            o.add("settings", s);
            mods.add(m.name(), o);
        }
        root.add("modules", mods);
        try (Writer writer = Files.newBufferedWriter(file)) {
            GSON.toJson(root, writer);
        } catch (Exception e) {
            MythClient.LOGGER.warn("Could not write {}", file, e);
        }
    }
}
