package dev.mythclient.gui;

import dev.mythclient.MythClient;
import dev.mythclient.module.hud.HudModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Drag any enabled HUD element (Coordinates, FPS, Watermark, Waypoints...) where you want it.
 * Elements can't be dragged off the screen. Positions are saved when you let go.
 */
public class HudEditorScreen extends Screen implements MythScreen {

    private final Screen parent;
    private final MouseInput mouse = new MouseInput();

    private HudModule dragging;
    private int grabX;
    private int grabY;
    private boolean closeRequested;

    public HudEditorScreen(Screen parent) {
        super(Text.literal("HUD Editor"));
        this.parent = parent;
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(Math.max(lo, hi), v));
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        mouse.poll();

        List<HudModule> mods = MythClient.modules().enabledHud();

        // ---- dragging (input first, so the element follows the cursor this frame)
        if (dragging != null) {
            if (mouse.left) {
                List<String> lines = dragging.editorLines();
                int w = dragging.widthOf(lines);
                int h = dragging.heightOf(lines);
                dragging.moveTo(clamp(mx - grabX, 0, this.width - w), clamp(my - grabY, 0, this.height - h));
            } else {
                dragging = null;
                MythClient.config().save();
            }
        } else if (mouse.leftPressed) {
            // topmost = last drawn
            for (int i = mods.size() - 1; i >= 0; i--) {
                HudModule m = mods.get(i);
                List<String> lines = m.editorLines();
                int w = m.widthOf(lines);
                int h = m.heightOf(lines);
                int x = m.px(w);
                int y = m.py(h);
                if (MouseInput.inside(mx, my, x - 2, y - 2, w + 4, h + 4)) {
                    dragging = m;
                    grabX = mx - x;
                    grabY = my - y;
                    break;
                }
            }
        }

        // ---- draw
        String title = "HUD EDITOR";
        ctx.drawText(this.textRenderer, title, this.width / 2 - this.textRenderer.getWidth(title) / 2, 8, 0xFFFFFFFF, false);
        String hint = mods.isEmpty()
                ? "No HUD modules are on. Turn on Coordinates, FPS, Watermark or Waypoints in the click GUI first."
                : "Drag an element to move it. Esc or Done to finish.";
        ctx.drawText(this.textRenderer, hint, this.width / 2 - this.textRenderer.getWidth(hint) / 2, 20, 0xFF9A9A9A, false);

        for (HudModule m : mods) {
            List<String> lines = m.editorLines();
            int w = m.widthOf(lines);
            int h = m.heightOf(lines);
            int x = m.px(w);
            int y = m.py(h);
            boolean hover = MouseInput.inside(mx, my, x - 2, y - 2, w + 4, h + 4);
            boolean active = dragging == m;
            int edge = active ? 0xFFFFFFFF : hover ? 0xFFCFCFCF : 0xFF7A7A7A;
            ctx.fill(x - 3, y - 3, x + w + 3, y + h + 3, edge);
            ctx.fill(x - 2, y - 2, x + w + 2, y + h + 2, 0xE0202020);
            m.drawAt(ctx, lines, x, y);
            if (hover || active) {
                ctx.drawText(this.textRenderer, m.name() + "  (" + m.px(w) + ", " + m.py(h) + ")", x - 2, y - 14, 0xFFE8E8E8, false);
            }
        }

        // ---- Done button
        int bw = 86;
        int bx = (this.width - bw) / 2;
        int by = this.height - 28;
        boolean hover = MouseInput.inside(mx, my, bx, by, bw, 18);
        ctx.fill(bx - 1, by - 1, bx + bw + 1, by + 19, 0xFF555555);
        ctx.fill(bx, by, bx + bw, by + 18, hover ? 0xFFE2E2E2 : 0xFF3A3A3A);
        ctx.drawText(this.textRenderer, "Done", bx + bw / 2 - this.textRenderer.getWidth("Done") / 2, by + 5,
                hover ? 0xFF121212 : 0xFFFFFFFF, false);
        if (hover && mouse.leftPressed && dragging == null) closeRequested = true;

        if (closeRequested) {
            closeRequested = false;
            close();
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        MythClient.config().save();
        this.client.setScreen(parent);
    }
}
