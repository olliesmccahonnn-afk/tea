package dev.mythclient.gui;

import dev.mythclient.MythClient;
import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.setting.BoolSetting;
import dev.mythclient.setting.ModeSetting;
import dev.mythclient.setting.NumberSetting;
import dev.mythclient.setting.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Myth Client menu (default key: Right Shift) - flat grey and white.
 *
 *   Left click a module  = turn it on / off (white row = on)
 *   Right click a module = open / close its settings
 *   Settings: click to toggle / cycle, drag a bar to change a number
 *
 * Drawn by hand and driven by {@link MouseInput}, so it does not rely on the 1.21.9+ click classes.
 */
public class ClickGuiScreen extends Screen implements MythScreen {

    // ---- grey & white palette
    private static final int C_BORDER = 0xFF555555;
    private static final int C_PANEL = 0xF0202020;
    private static final int C_HEADER = 0xFF3A3A3A;
    private static final int C_ROW = 0xFF2B2B2B;
    private static final int C_ROW_HOVER = 0xFF3C3C3C;
    private static final int C_ROW_ON = 0xFFE2E2E2;
    private static final int C_ROW_ON_HOVER = 0xFFFFFFFF;
    private static final int C_SET = 0xFF242424;
    private static final int C_SET_HOVER = 0xFF303030;
    private static final int C_TRACK = 0xFF454545;
    private static final int C_WHITE = 0xFFFFFFFF;
    private static final int C_TEXT = 0xFFE8E8E8;
    private static final int C_MUTED = 0xFF9A9A9A;
    private static final int C_DARK = 0xFF121212;

    private static final int PANEL_W = 124;
    private static final int GAP = 10;
    private static final int HEADER_H = 18;
    private static final int ROW_H = 16;

    private static final Set<String> EXPANDED = new HashSet<>();

    private final Screen parent;
    private final MouseInput mouse = new MouseInput();

    private NumberSetting sliderSetting;
    private int sliderX;
    private int sliderW;
    private Screen pending;
    private boolean closeRequested;

    public ClickGuiScreen(Screen parent) {
        super(Text.literal(MythClient.NAME));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta); // dimmed / blurred world behind
        mouse.poll();

        // slider drag first so the bar you see matches the cursor this frame
        if (sliderSetting != null) {
            if (mouse.left) {
                sliderSetting.setFraction((mx - sliderX) / (double) sliderW);
            } else {
                sliderSetting = null;
                MythClient.config().save();
            }
        }

        List<Category> cats = new ArrayList<>();
        for (Category c : Category.values()) {
            if (!MythClient.modules().in(c).isEmpty()) cats.add(c);
        }

        text(ctx, "MYTH CLIENT", this.width / 2 - this.textRenderer.getWidth("MYTH CLIENT") / 2, 8, C_WHITE);
        String hint = "Left click: on / off     Right click: settings";
        text(ctx, hint, this.width / 2 - this.textRenderer.getWidth(hint) / 2, 20, C_MUTED);

        int total = cats.size() * PANEL_W + Math.max(0, cats.size() - 1) * GAP;
        int x0 = (this.width - total) / 2;
        int y0 = 38;
        String tip = null;

        for (int i = 0; i < cats.size(); i++) {
            Category cat = cats.get(i);
            List<ClientModule> mods = MythClient.modules().in(cat);
            int x = x0 + i * (PANEL_W + GAP);

            // panel height
            int h = HEADER_H;
            for (ClientModule m : mods) {
                h += ROW_H;
                if (EXPANDED.contains(m.name())) {
                    for (Setting<?> s : m.settings()) h += settingHeight(s);
                }
            }
            h += 3;

            ctx.fill(x - 1, y0 - 1, x + PANEL_W + 1, y0 + h + 1, C_BORDER);
            ctx.fill(x, y0, x + PANEL_W, y0 + h, C_PANEL);
            ctx.fill(x, y0, x + PANEL_W, y0 + HEADER_H, C_HEADER);
            text(ctx, cat.label().toUpperCase(), x + 6, y0 + 5, C_WHITE);
            String count = String.valueOf(mods.size());
            text(ctx, count, x + PANEL_W - 6 - this.textRenderer.getWidth(count), y0 + 5, C_MUTED);

            int y = y0 + HEADER_H;
            for (ClientModule m : mods) {
                boolean hover = MouseInput.inside(mx, my, x, y, PANEL_W, ROW_H);
                if (hover) {
                    tip = m.description();
                    if (mouse.leftPressed) {
                        m.toggle();
                        MythClient.config().save();
                    } else if (mouse.rightPressed && !m.settings().isEmpty()) {
                        if (!EXPANDED.remove(m.name())) EXPANDED.add(m.name());
                    }
                }
                boolean on = m.isEnabled();
                int bg = on ? (hover ? C_ROW_ON_HOVER : C_ROW_ON) : (hover ? C_ROW_HOVER : C_ROW);
                ctx.fill(x, y, x + PANEL_W, y + ROW_H, bg);
                text(ctx, m.name(), x + 6, y + 4, on ? C_DARK : C_TEXT);
                if (!m.settings().isEmpty()) {
                    String mark = EXPANDED.contains(m.name()) ? "-" : "+";
                    text(ctx, mark, x + PANEL_W - 6 - this.textRenderer.getWidth(mark), y + 4, on ? C_DARK : C_MUTED);
                }
                y += ROW_H;

                if (EXPANDED.contains(m.name())) {
                    for (Setting<?> s : m.settings()) {
                        drawSetting(ctx, s, x, y, PANEL_W, mx, my);
                        y += settingHeight(s);
                    }
                }
            }
        }

        if (tip != null) {
            text(ctx, tip, this.width / 2 - this.textRenderer.getWidth(tip) / 2, this.height - 44, C_MUTED);
        }

        // ---- bottom buttons
        String[] labels = {"HUD Editor", "Waypoints", "Close"};
        int bw = 86;
        int bTotal = labels.length * bw + (labels.length - 1) * 8;
        int bx = (this.width - bTotal) / 2;
        int by = this.height - 28;
        for (int i = 0; i < labels.length; i++) {
            int x = bx + i * (bw + 8);
            boolean hover = MouseInput.inside(mx, my, x, by, bw, 18);
            ctx.fill(x - 1, by - 1, x + bw + 1, by + 19, C_BORDER);
            ctx.fill(x, by, x + bw, by + 18, hover ? C_ROW_ON : C_HEADER);
            text(ctx, labels[i], x + bw / 2 - this.textRenderer.getWidth(labels[i]) / 2, by + 5, hover ? C_DARK : C_WHITE);
            if (hover && mouse.leftPressed) {
                switch (i) {
                    case 0 -> pending = new HudEditorScreen(this);
                    case 1 -> pending = new WaypointScreen(this);
                    default -> closeRequested = true;
                }
            }
        }

        // screen changes happen after everything is drawn
        if (pending != null) {
            Screen next = pending;
            pending = null;
            this.client.setScreen(next);
        } else if (closeRequested) {
            closeRequested = false;
            close();
        }
    }

    private static int settingHeight(Setting<?> s) {
        return s instanceof NumberSetting ? 20 : 15;
    }

    private void drawSetting(DrawContext ctx, Setting<?> s, int x, int y, int w, int mx, int my) {
        int h = settingHeight(s);
        boolean hover = MouseInput.inside(mx, my, x, y, w, h);
        ctx.fill(x, y, x + w, y + h, hover ? C_SET_HOVER : C_SET);

        String value = s.display();
        int valueX = x + w - 6 - this.textRenderer.getWidth(value);

        if (s instanceof NumberSetting n) {
            text(ctx, s.name(), x + 8, y + 3, C_TEXT);
            text(ctx, value, valueX, y + 3, C_WHITE);
            int tx = x + 8;
            int tw = w - 16;
            ctx.fill(tx, y + h - 6, tx + tw, y + h - 3, C_TRACK);
            ctx.fill(tx, y + h - 6, tx + (int) (tw * n.fraction()), y + h - 3, C_WHITE);
            if (hover && mouse.leftPressed) {
                sliderSetting = n;
                sliderX = tx;
                sliderW = tw;
            }
        } else {
            text(ctx, s.name(), x + 8, y + 3, C_TEXT);
            boolean highlight = s instanceof BoolSetting b ? b.get() : s instanceof ModeSetting;
            text(ctx, value, valueX, y + 3, highlight ? C_WHITE : C_MUTED);
            if (hover && mouse.leftPressed) {
                if (s instanceof BoolSetting b) b.toggle();
                else if (s instanceof ModeSetting m) m.cycle();
                MythClient.config().save();
            }
        }
    }

    private void text(DrawContext ctx, String s, int x, int y, int color) {
        ctx.drawText(this.textRenderer, s, x, y, color, false);
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
