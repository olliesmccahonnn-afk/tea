package dev.mythclient.module.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.mythclient.MythClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** All waypoints, saved to {@code config/mythclient-waypoints.json}. */
public final class WaypointManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path file = FabricLoader.getInstance().getConfigDir().resolve("mythclient-waypoints.json");
    private final List<Waypoint> all = new ArrayList<>();

    // ---- where are we? ----------------------------------------------------
    public static String currentWorld() {
        ServerInfo server = MinecraftClient.getInstance().getCurrentServerEntry();
        return server != null ? server.address : "singleplayer";
    }

    public static String currentDimension() {
        var world = MinecraftClient.getInstance().world;
        return world == null ? "" : world.getRegistryKey().getValue().toString();
    }

    // ---- queries ----------------------------------------------------------
    public List<Waypoint> all() {
        return all;
    }

    /** Every waypoint saved for this server / singleplayer (any dimension). */
    public List<Waypoint> inCurrentWorld() {
        String w = currentWorld();
        List<Waypoint> out = new ArrayList<>();
        for (Waypoint p : all) {
            if (p.world.equals(w)) out.add(p);
        }
        return out;
    }

    /** Visible waypoints in the dimension you are standing in right now. */
    public List<Waypoint> visibleHere() {
        String w = currentWorld();
        String d = currentDimension();
        List<Waypoint> out = new ArrayList<>();
        for (Waypoint p : all) {
            if (p.visible && p.world.equals(w) && p.dimension.equals(d)) out.add(p);
        }
        return out;
    }

    // ---- changes ----------------------------------------------------------
    /** Adds a waypoint at the player's feet. Returns null if not in a world. */
    public Waypoint addHere(String requestedName) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return null;
        String name = requestedName == null ? "" : requestedName.trim();
        if (name.isEmpty()) name = "Waypoint " + (inCurrentWorld().size() + 1);
        Waypoint p = new Waypoint(name, player.getBlockX(), player.getBlockY(), player.getBlockZ(),
                currentWorld(), currentDimension());
        all.add(p);
        save();
        return p;
    }

    public void remove(Waypoint p) {
        all.remove(p);
        save();
    }

    // ---- persistence ------------------------------------------------------
    public void load() {
        all.clear();
        if (!Files.exists(file)) return;
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (!root.has("waypoints")) return;
            for (JsonElement e : root.getAsJsonArray("waypoints")) {
                JsonObject o = e.getAsJsonObject();
                Waypoint p = new Waypoint(
                        o.get("name").getAsString(),
                        o.get("x").getAsInt(), o.get("y").getAsInt(), o.get("z").getAsInt(),
                        o.get("world").getAsString(), o.get("dimension").getAsString());
                if (o.has("visible")) p.visible = o.get("visible").getAsBoolean();
                all.add(p);
            }
        } catch (Exception ex) {
            MythClient.LOGGER.warn("Could not read {} - starting with no waypoints", file, ex);
        }
    }

    public void save() {
        JsonArray arr = new JsonArray();
        for (Waypoint p : all) {
            JsonObject o = new JsonObject();
            o.addProperty("name", p.name);
            o.addProperty("x", p.x);
            o.addProperty("y", p.y);
            o.addProperty("z", p.z);
            o.addProperty("world", p.world);
            o.addProperty("dimension", p.dimension);
            o.addProperty("visible", p.visible);
            arr.add(o);
        }
        JsonObject root = new JsonObject();
        root.add("waypoints", arr);
        try (Writer writer = Files.newBufferedWriter(file)) {
            GSON.toJson(root, writer);
        } catch (Exception ex) {
            MythClient.LOGGER.warn("Could not write {}", file, ex);
        }
    }
}
