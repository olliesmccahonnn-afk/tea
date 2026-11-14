package dev.mythclient.module.hud;

import dev.mythclient.MythClient;
import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.setting.ModeSetting;
import dev.mythclient.setting.NumberSetting;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

/**
 * A module that draws text lines on the HUD. Subclasses only supply {@link #lines()}.
 * Position is the X/Y settings; drag it in the HUD Editor (click GUI > HUD Editor).
 * It always stays fully on screen. Nothing is drawn unless the module is enabled.
 */
public abstract class HudModule extends ClientModule {

    public static final int LINE_HEIGHT = 10;

    protected final NumberSetting x = setting(new NumberSetting("X", 4, 0, 4000, 1));
    protected final NumberSetting y = setting(new NumberSetting("Y", 4, 0, 4000, 1));
    protected final ModeSetting color = setting(new ModeSetting("Color", "White", "White", "Grey", "Cyan", "Gold", "Pink"));

    protected HudModule(String name, String description, int defaultX, int defaultY) {
        super(name, description, Category.HUD);
        x.set((double) defaultX);
        y.set((double) defaultY);
    }

    /** What to draw right now. Return an empty list to draw nothing. */
    protected abstract List<String> lines();

    /** What the HUD Editor shows (so an empty module can still be grabbed). */
    public List<String> editorLines() {
        List<String> l = lines();
        return l.isEmpty() ? List.of(name()) : l;
    }

    public int widthOf(List<String> lines) {
        int w = 0;
        for (String s : lines) w = Math.max(w, mc.textRenderer.getWidth(s));
        return w;
    }

    public int heightOf(List<String> lines) {
        return lines.size() * LINE_HEIGHT;
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(Math.max(lo, hi), v));
    }

    /** Position kept inside the screen for a block of this size. */
    public int px(int width) {
        return clamp(x.intValue(), 0, mc.getWindow().getScaledWidth() - width);
    }

    public int py(int height) {
        return clamp(y.intValue(), 0, mc.getWindow().getScaledHeight() - height);
    }

    public void moveTo(int nx, int ny) {
        x.set((double) nx);
        y.set((double) ny);
    }

    private int argb() {
        return switch (color.get()) {
            case "Grey" -> 0xFFBDBDBD;
            case "Cyan" -> 0xFF4FC3F7;
            case "Gold" -> 0xFFFFD27F;
            case "Pink" -> 0xFFFF8FB1;
            default -> 0xFFFFFFFF; // White
        };
    }

    public void drawAt(DrawContext context, List<String> lines, int drawX, int drawY) {
        int c = argb();
        for (int i = 0; i < lines.size(); i++) {
            context.drawTextWithShadow(mc.textRenderer, lines.get(i), drawX, drawY + i * LINE_HEIGHT, c);
        }
    }

    @Override
    public void onHudRender(DrawContext context) {
        if (!MythClient.hudVisible() || mc.options.hudHidden || mc.player == null) return;
        List<String> lines = lines();
        if (lines.isEmpty()) return;
        drawAt(context, lines, px(widthOf(lines)), py(heightOf(lines)));
    }
}
