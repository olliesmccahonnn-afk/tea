package dev.mythclient.module.render;

import dev.mythclient.MythClient;
import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.setting.ModeSetting;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Wears a custom skin and/or cape on YOUR character, from PNG files you drop in
 * <config>/mythclient/skins and <config>/mythclient/capes.
 *
 * This is client-side only: you see it, other players keep seeing your real skin and cape.
 * Files are scanned when the game starts, so restart after adding new PNGs.
 * The skin is swapped in AbstractClientPlayerEntityMixin.
 */
public class CosmeticsModule extends ClientModule {
    public static CosmeticsModule INSTANCE;
    private static final String NONE = "Default";

    private final Path skinsDir;
    private final Path capesDir;
    private final ModeSetting skin;
    private final ModeSetting cape;
    private final ModeSetting model;

    private final Map<Path, AssetInfo.TextureAssetInfo> cache = new HashMap<>();
    private final Set<Path> failed = new HashSet<>();

    public CosmeticsModule() {
        super("Cosmetics", "Wear a custom skin and cape (only you can see it).", Category.RENDER);
        INSTANCE = this;
        Path root = FabricLoader.getInstance().getConfigDir().resolve("mythclient");
        skinsDir = root.resolve("skins");
        capesDir = root.resolve("capes");
        try {
            Files.createDirectories(skinsDir);
            Files.createDirectories(capesDir);
        } catch (IOException e) {
            MythClient.LOGGER.warn("Could not create cosmetics folders", e);
        }
        skin = (ModeSetting) setting(new ModeSetting("Skin", NONE, names(skinsDir)));
        model = (ModeSetting) setting(new ModeSetting("Model", "Auto", "Auto", "Wide", "Slim"));
        cape = (ModeSetting) setting(new ModeSetting("Cape", NONE, names(capesDir)));
    }

    public static boolean active() { return INSTANCE != null && INSTANCE.isEnabled(); }

    private static String[] names(Path dir) {
        List<String> out = new ArrayList<>();
        out.add(NONE);
        try (Stream<Path> s = Files.list(dir)) {
            s.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".png"))
             .map(p -> p.getFileName().toString())
             .map(n -> n.substring(0, n.length() - 4))
             .sorted()
             .forEach(out::add);
        } catch (IOException ignored) { }
        return out.toArray(new String[0]);
    }

    /** Returns the player's textures with the chosen skin, model and cape swapped in. */
    public SkinTextures apply(SkinTextures orig) {
        AssetInfo.TextureAsset body = orig.body();
        AssetInfo.TextureAsset capeAsset = orig.cape();
        AssetInfo.TextureAsset elytra = orig.elytra();
        PlayerSkinType type = orig.model();

        if (!skin.is(NONE)) {
            var t = load(skinsDir.resolve(String.valueOf(skin.get()) + ".png"), false);
            if (t != null) body = t;
        }
        if (model.is("Wide")) type = PlayerSkinType.WIDE;
        else if (model.is("Slim")) type = PlayerSkinType.SLIM;

        if (!cape.is(NONE)) {
            var t = load(capesDir.resolve(String.valueOf(cape.get()) + ".png"), true);
            if (t != null) { capeAsset = t; elytra = t; }
        }
        return new SkinTextures(body, capeAsset, elytra, type, orig.secure());
    }

    private AssetInfo.TextureAssetInfo load(Path file, boolean isCape) {
        AssetInfo.TextureAssetInfo hit = cache.get(file);
        if (hit != null) return hit;
        if (failed.contains(file)) return null;
        try (InputStream in = Files.newInputStream(file)) {
            NativeImage img = NativeImage.read(in);
            int w = img.getWidth(), h = img.getHeight();
            boolean ok = isCape ? (w == 2 * h) : (w == h);   // cape 64x32 (or scaled), skin 64x64 (or scaled)
            if (!ok) {
                MythClient.LOGGER.warn("{} has size {}x{}; expected {}", file.getFileName(), w, h,
                        isCape ? "2:1 (e.g. 64x32)" : "square (e.g. 64x64; old 64x32 skins are not supported)");
                img.close();
                failed.add(file);
                return null;
            }
            String clean = file.getFileName().toString().toLowerCase().replaceAll("[^a-z0-9._-]", "_");
            Identifier id = Identifier.of("mythclient",
                    "cosmetic/" + (isCape ? "cape/" : "skin/") + Integer.toHexString(file.hashCode()) + "_" + clean);
            mc.getTextureManager().registerTexture(id, new NativeImageBackedTexture(() -> "mythclient " + clean, img));
            var asset = new AssetInfo.TextureAssetInfo(id, id);
            cache.put(file, asset);
            return asset;
        } catch (Exception e) {
            MythClient.LOGGER.warn("Could not load {}", file, e);
            failed.add(file);
            return null;
        }
    }
}
