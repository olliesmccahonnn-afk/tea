package dev.mythclient.addons.gui;

import dev.mythclient.MythClient;
import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.gui.HudEditorScreen;
import dev.mythclient.gui.MouseInput;
import dev.mythclient.gui.WaypointScreen;
import dev.mythclient.setting.BoolSetting;
import dev.mythclient.setting.ModeSetting;
import dev.mythclient.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Nature-themed replacement for the Myth Client menu: vines, flowers and grass around the edges,
 * category tabs, a mod list with ON/OFF switches and a settings pane.
 * Like the base GUI it polls the mouse itself (MouseInput) instead of overriding Screen input methods.
 */
public class NatureGuiScreen extends Screen {
    // palette
    private static final int PANEL = 0xF0172117, BARK = 0xFF5A3E26, BARK_DARK = 0xFF3F2A19, MOSS = 0xFF2F5A2F;
    private static final int PANE = 0xB00D160D;
    private static final int ROW = 0x60324832, ROW_HOVER = 0x80456545, ROW_ON = 0xA02E6B34, ROW_ON_HOVER = 0xB0398040, ROW_SEL = 0xFFB6E88A;
    private static final int TEXT = 0xFFE6F2DC, MUTED = 0xFF8FA88A, TITLE = 0xFFA6E87A, ON = 0xFF7CFF7C, OFF = 0xFF7A8576;
    private static final int TAB = 0xFF263826, TAB_HOVER = 0xFF2F6B33, TAB_SEL = 0xFF3E8E41;
    private static final int VINE = 0xFF2A5A2E, VINE_LIGHT = 0xFF3F7F3F, LEAF = 0xFF4CAF50, LEAF_DARK = 0xFF2E7D32, GRASS_A = 0xFF3C8D40, GRASS_B = 0xFF5DB85C;
    private static final int[] PETALS = {0xFFF28CB1, 0xFFF5F5F0, 0xFFB59BE8, 0xFFF6A04D, 0xFFFF6F91};
    private static final int CENTER = 0xFFF7D44C;

    private static final int ROW_H = 16, SET_H = 24;

    private final Screen parent;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final MouseInput mouse = new MouseInput();

    private int tab = 0;
    private ClientModule selected;
    private int scrollL = 0, scrollR = 0;
    private int dragBar = 0;                 // 1 = list scrollbar, 2 = settings scrollbar
    private NumberSetting sliderDrag;
    private int sliderX, sliderW;
    private Screen pending;
    private boolean closeRequested;

    public NatureGuiScreen(Screen parent) {
        super(Text.literal("Myth Client"));
        this.parent = parent;
    }

    @Override public boolean shouldPause() { return false; }

    @Override
    public void close() {
        try { MythClient.config().save(); } catch (Throwable ignored) { }
        mc.setScreen(parent);
    }

    // ------------------------------------------------------------------ render

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        mouse.poll();
        if (!mouse.left) { dragBar = 0; sliderDrag = null; }

        int pw = Math.min(width - 24, 470), ph = Math.min(height - 24, 300);
        int px = (width - pw) / 2, py = (height - ph) / 2;

        // dim the world
        ctx.fill(0, 0, width, height, 0xB0081008);
        // frame + panel
        ctx.fill(px - 3, py - 3, px + pw + 3, py + ph + 3, BARK_DARK);
        ctx.fill(px - 2, py - 2, px + pw + 2, py + ph + 2, BARK);
        ctx.fill(px, py, px + pw, py + ph, PANEL);
        ctx.fill(px + 2, py + 2, px + pw - 2, py + 3, MOSS);
        ctx.fill(px + 2, py + ph - 3, px + pw - 2, py + ph - 2, MOSS);

        nature(ctx, px, py, pw, ph);

        // ---- header
        text(ctx, "Myth Client", px + 14, py + 10, TITLE);
        flower(ctx, px + 14 + mc.textRenderer.getWidth("Myth Client") + 8, py + 13, 1, 0);
        int active = 0;
        for (Category c : Category.values()) for (ClientModule m : MythClient.modules().in(c)) if (m.isEnabled()) active++;
        text(ctx, active + " mod" + (active == 1 ? "" : "s") + " on", px + 14, py + 21, active > 0 ? ON : OFF);

        int bx = px + pw - 12;
        String[] btn = {"HUD Editor", "Waypoints"};
        for (int i = 0; i < btn.length; i++) {
            int bw = mc.textRenderer.getWidth(btn[i]) + 12;
            bx -= bw;
            boolean hov = inside(mx, my, bx, py + 9, bw, 14);
            ctx.fill(bx, py + 9, bx + bw, py + 23, hov ? TAB_HOVER : TAB);
            text(ctx, btn[i], bx + 6, py + 12, TEXT);
            if (hov && mouse.leftPressed) pending = i == 0 ? new HudEditorScreen(this) : new WaypointScreen(this);
            bx -= 4;
        }

        // ---- tabs
        Category[] cats = Category.values();
        int tx = px + 12;
        for (int i = 0; i < cats.length; i++) {
            int n = 0;
            for (ClientModule m : MythClient.modules().in(cats[i])) if (m.isEnabled()) n++;
            String label = cats[i].label() + (n > 0 ? "  " + n : "");
            int tw = mc.textRenderer.getWidth(label) + 14;
            boolean hov = inside(mx, my, tx, py + 32, tw, 14);
            ctx.fill(tx, py + 32, tx + tw, py + 46, i == tab ? TAB_SEL : hov ? TAB_HOVER : TAB);
            text(ctx, label, tx + 7, py + 35, i == tab ? 0xFFFFFFFF : TEXT);
            if (hov && mouse.leftPressed && i != tab) { tab = i; scrollL = 0; scrollR = 0; selected = null; }
            tx += tw + 3;
        }

        // ---- body geometry
        int bodyX = px + 12, bodyY = py + 51, bodyW = pw - 24, bodyH = ph - 51 - 32;
        int leftW = bodyW * 46 / 100, rightX = bodyX + leftW + 8, rightW = bodyW - leftW - 8;

        List<ClientModule> mods = new ArrayList<>(MythClient.modules().in(cats[tab]));
        if (selected == null || !mods.contains(selected)) selected = mods.isEmpty() ? null : mods.get(0);

        // ---- module list
        ctx.fill(bodyX, bodyY, bodyX + leftW, bodyY + bodyH, PANE);
        int visibleRows = Math.max(1, (bodyH - 4) / ROW_H);
        int maxScrollL = Math.max(0, mods.size() - visibleRows);
        scrollL = clamp(scrollL, 0, maxScrollL);
        int listW = leftW - (mods.size() > visibleRows ? 10 : 4);
        for (int i = 0; i < visibleRows && scrollL + i < mods.size(); i++) {
            ClientModule m = mods.get(scrollL + i);
            int ry = bodyY + 2 + i * ROW_H, rx = bodyX + 2;
            boolean hov = inside(mx, my, rx, ry, listW, ROW_H - 2);
            boolean on = m.isEnabled();
            ctx.fill(rx, ry, rx + listW, ry + ROW_H - 2, on ? (hov ? ROW_ON_HOVER : ROW_ON) : (hov ? ROW_HOVER : ROW));
            if (m == selected) ctx.fill(rx, ry, rx + 2, ry + ROW_H - 2, ROW_SEL);
            int swX = rx + listW - 26, swY = ry + 2;
            String state = on ? "ON" : "off";
            int stW = mc.textRenderer.getWidth(state);
            text(ctx, fit(m.name(), swX - stW - rx - 16), rx + 6, ry + 3, on ? TEXT : MUTED);
            text(ctx, state, swX - stW - 4, ry + 3, on ? ON : OFF);
            toggle(ctx, swX, swY, on);
            if (mouse.leftPressed && hov) {
                if (inside(mx, my, swX - 2, ry, 28, ROW_H - 2)) m.toggle(); else selected = m;
            } else if (mouse.rightPressed && hov) {
                m.toggle();
            }
        }
        if (maxScrollL > 0) scrollL = scrollbar(ctx, mx, my, bodyX + leftW - 7, bodyY + 2, bodyH - 4, mods.size(), visibleRows, scrollL, 1);

        // ---- detail pane
        ctx.fill(rightX, bodyY, rightX + rightW, bodyY + bodyH, PANE);
        if (selected == null) {
            text(ctx, "Nothing here yet.", rightX + 8, bodyY + 8, MUTED);
        } else {
            drawDetail(ctx, mx, my, rightX, bodyY, rightW, bodyH);
        }

        // ---- footer
        String hint = "ESC to close   |   Right-click a mod to toggle   |   Bind keys in Controls";
        text(ctx, fit(hint, pw - 28), px + 14, py + ph - 29, MUTED);

        // deferred screen changes (never switch screens in the middle of drawing)
        if (pending != null) { Screen s = pending; pending = null; mc.setScreen(s); }
        else if (closeRequested) { closeRequested = false; close(); }
    }

    private void drawDetail(DrawContext ctx, int mx, int my, int x, int y, int w, int h) {
        ClientModule m = selected;
        boolean on = m.isEnabled();
        int ix = x + 8, iw = w - 16, cy = y + 6;

        text(ctx, fit(m.name(), iw - 56), ix, cy, TITLE);
        // status
        String state = on ? "ON" : "OFF";
        int sw = mc.textRenderer.getWidth(state);
        int sx = x + w - 8 - sw;
        ctx.fill(sx - 9, cy + 2, sx - 4, cy + 7, on ? ON : OFF);
        text(ctx, state, sx, cy, on ? ON : OFF);
        cy += 12;
        text(ctx, on ? "This mod is on" : "This mod is off", ix, cy, on ? ON : OFF);
        cy += 12;
        for (String line : wrap(m.description(), iw, 2)) { text(ctx, line, ix, cy, MUTED); cy += 10; }
        cy += 2;

        // big toggle button
        boolean hov = inside(mx, my, ix, cy, iw, 14);
        ctx.fill(ix, cy, ix + iw, cy + 14, on ? (hov ? 0xFF3E8E41 : 0xFF2E6B34) : (hov ? 0xFF4A5A4A : 0xFF303C30));
        String bt = on ? "Turn off" : "Turn on";
        text(ctx, bt, ix + (iw - mc.textRenderer.getWidth(bt)) / 2, cy + 3, 0xFFFFFFFF);
        if (hov && mouse.leftPressed) m.toggle();
        cy += 20;

        // settings
        List<Object> sets = new ArrayList<>(m.settings());
        if (sets.isEmpty()) { text(ctx, "No settings", ix, cy, MUTED); return; }
        text(ctx, "Settings", ix, cy, TITLE);
        cy += 12;
        int areaH = y + h - cy - 3;
        int visible = Math.max(1, areaH / SET_H);
        int maxScroll = Math.max(0, sets.size() - visible);
        scrollR = clamp(scrollR, 0, maxScroll);
        int cw = iw - (maxScroll > 0 ? 9 : 0);

        for (int i = 0; i < visible && scrollR + i < sets.size(); i++) {
            Object o = sets.get(scrollR + i);
            int ry = cy + i * SET_H;
            ctx.fill(ix, ry, ix + cw, ry + SET_H - 3, ROW);
            if (o instanceof BoolSetting b) {
                boolean v = (Boolean) b.get();
                text(ctx, fit(b.name(), cw - 60), ix + 5, ry + 6, TEXT);
                text(ctx, v ? "ON" : "off", ix + cw - 26 - mc.textRenderer.getWidth(v ? "ON" : "off") - 4, ry + 6, v ? ON : OFF);
                toggle(ctx, ix + cw - 26, ry + 5, v);
                if (mouse.leftPressed && inside(mx, my, ix, ry, cw, SET_H - 3)) b.toggle();
            } else if (o instanceof ModeSetting ms) {
                String val = String.valueOf(ms.get());
                int bw = mc.textRenderer.getWidth(val) + 12;
                int bx = ix + cw - bw - 4;
                boolean bh = inside(mx, my, bx, ry + 4, bw, 14);
                text(ctx, fit(ms.name(), bx - ix - 10), ix + 5, ry + 6, TEXT);
                ctx.fill(bx, ry + 4, bx + bw, ry + 18, bh ? 0xFF4F7A4F : 0xFF3B5B3B);
                text(ctx, val, bx + 6, ry + 7, 0xFFFFFFFF);
                if (mouse.leftPressed && bh) ms.cycle();
            } else if (o instanceof NumberSetting n) {
                String val = n.display();
                text(ctx, fit(n.name(), cw - mc.textRenderer.getWidth(val) - 16), ix + 5, ry + 2, TEXT);
                text(ctx, val, ix + cw - 5 - mc.textRenderer.getWidth(val), ry + 2, TITLE);
                int tx = ix + 6, tw = cw - 12, ty = ry + 14;
                ctx.fill(tx, ty, tx + tw, ty + 4, 0xFF1B2A1B);
                int fw = (int) Math.round(tw * n.fraction());
                ctx.fill(tx, ty, tx + fw, ty + 4, LEAF);
                ctx.fill(tx + fw - 2, ty - 2, tx + fw + 2, ty + 6, 0xFFFFFFFF);
                if (mouse.leftPressed && inside(mx, my, tx - 3, ty - 3, tw + 6, 12) && dragBar == 0) {
                    sliderDrag = n; sliderX = tx; sliderW = tw;
                }
            } else {
                text(ctx, "?", ix + 5, ry + 6, MUTED);
            }
        }
        if (sliderDrag != null && mouse.left) {
            sliderDrag.setFraction(Math.max(0.0, Math.min(1.0, (mx - sliderX) / (double) sliderW)));
        }
        if (maxScroll > 0) scrollR = scrollbar(ctx, mx, my, x + w - 12, cy, visible * SET_H - 3, sets.size(), visible, scrollR, 2);
    }

    // ------------------------------------------------------------------ small widgets

    private void toggle(DrawContext c, int x, int y, boolean on) {
        c.fill(x, y, x + 22, y + 10, on ? 0xFF4CAF50 : 0xFF4A524A);
        c.fill(on ? x + 13 : x + 1, y + 1, on ? x + 21 : x + 9, y + 9, 0xFFF2F6EE);
    }

    private int scrollbar(DrawContext c, int mx, int my, int x, int y, int h, int total, int visible, int scroll, int id) {
        int maxScroll = Math.max(1, total - visible);
        int thumbH = Math.max(10, h * visible / total);
        int thumbY = y + (h - thumbH) * scroll / maxScroll;
        c.fill(x, y, x + 4, y + h, 0xFF1B2A1B);
        boolean over = inside(mx, my, x - 3, y, 10, h);
        c.fill(x, thumbY, x + 4, thumbY + thumbH, dragBar == id || over ? GRASS_B : GRASS_A);
        if (mouse.leftPressed && over) dragBar = id;
        if (dragBar == id && mouse.left) {
            double f = (my - y - thumbH / 2.0) / Math.max(1, h - thumbH);
            scroll = (int) Math.round(Math.max(0.0, Math.min(1.0, f)) * maxScroll);
        }
        return clamp(scroll, 0, maxScroll);
    }

    private void text(DrawContext c, String s, int x, int y, int color) {
        c.drawText(mc.textRenderer, s, x, y, color, true);
    }

    private String fit(String s, int maxW) {
        if (s == null) return "";
        if (mc.textRenderer.getWidth(s) <= maxW) return s;
        while (s.length() > 1 && mc.textRenderer.getWidth(s + "..") > maxW) s = s.substring(0, s.length() - 1);
        return s + "..";
    }

    private List<String> wrap(String s, int maxW, int maxLines) {
        List<String> out = new ArrayList<>();
        if (s == null) return out;
        StringBuilder line = new StringBuilder();
        for (String word : s.split(" ")) {
            String t = line.length() == 0 ? word : line + " " + word;
            if (mc.textRenderer.getWidth(t) > maxW && line.length() > 0) {
                out.add(line.toString());
                line = new StringBuilder(word);
                if (out.size() == maxLines) break;
            } else line = new StringBuilder(t);
        }
        if (out.size() < maxLines && line.length() > 0) out.add(line.toString());
        if (out.size() == maxLines) out.set(maxLines - 1, fit(out.get(maxLines - 1), maxW));
        return out;
    }

    private static boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && my >= y && mx < x + w && my < y + h;
    }

    private static int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }

    // ------------------------------------------------------------------ nature decoration

    private void nature(DrawContext c, int x, int y, int w, int h) {
        double t = System.currentTimeMillis() / 1100.0;
        Random r = new Random(1337);

        // vines hanging from the top edge
        for (int cx = x + 8; cx < x + w - 8; cx += 13 + r.nextInt(12)) {
            int len = 8 + r.nextInt(14);
            boolean busy = cx < x + 150 || cx > x + w - 130;   // keep the title and buttons readable
            if (busy) len = 6 + r.nextInt(4);
            double ph = r.nextDouble() * 6.28;
            int lastX = cx, lastY = y;
            for (int i = 0; i < len; i += 2) {
                int dx = (int) Math.round(Math.sin(i * 0.4 + ph + t) * 2);
                c.fill(cx + dx, y + i, cx + dx + 2, y + i + 2, i % 4 == 0 ? VINE_LIGHT : VINE);
                if (i % 8 == 4) leaf(c, cx + dx + ((i / 8) % 2 == 0 ? 2 : -3), y + i, (i / 8) % 2 == 0);
                lastX = cx + dx; lastY = y + i;
            }
            int fc = r.nextInt(PETALS.length);
            if (!busy && r.nextInt(3) != 0) flower(c, lastX + 1, lastY + 4, 1, fc);
        }

        // vines climbing up both sides
        for (int side = 0; side < 2; side++) {
            int baseX = side == 0 ? x + 1 : x + w - 3;
            double ph = r.nextDouble() * 6.28;
            for (int i = 14; i < h - 14; i += 2) {
                int dx = (int) Math.round(Math.sin(i * 0.12 + ph + t * 0.8) * 2);
                c.fill(baseX + dx, y + i, baseX + dx + 2, y + i + 2, i % 6 == 0 ? VINE_LIGHT : VINE);
                if (i % 12 == 0) leaf(c, side == 0 ? baseX + dx + 2 : baseX + dx - 3, y + i, side == 0);
                if (i % 36 == 18) flower(c, side == 0 ? baseX + dx - 1 : baseX + dx + 3, y + i, 1, r.nextInt(PETALS.length));
            }
        }

        // grass + little flowers along the bottom
        for (int gx = x + 5; gx < x + w - 5; gx += 3) {
            int gh = 3 + r.nextInt(6);
            int sway = (int) Math.round(Math.sin(gx * 0.3 + t * 1.5));
            c.fill(gx, y + h - gh, gx + 1, y + h, r.nextBoolean() ? GRASS_A : GRASS_B);
            c.fill(gx + sway, y + h - gh - 1, gx + sway + 1, y + h - gh, GRASS_B);
            if (r.nextInt(9) == 0) {
                int sh = gh + 2 + r.nextInt(3);
                c.fill(gx + 1, y + h - sh, gx + 2, y + h - gh, VINE);
                flower(c, gx + 1, y + h - sh - 3, 1, r.nextInt(PETALS.length));
            }
        }

        // big flower clusters in the corners
        flower(c, x + 4, y + 4, 2, 0);
        flower(c, x + w - 5, y + 4, 2, 2);
        flower(c, x + 4, y + h - 5, 2, 3);
        flower(c, x + w - 5, y + h - 5, 2, 1);
            }

    private void leaf(DrawContext c, int x, int y, boolean right) {
        c.fill(x, y, x + 3, y + 2, LEAF);
        c.fill(x + (right ? 2 : 0), y - 1, x + (right ? 3 : 1), y, LEAF_DARK);
        c.fill(x + (right ? 0 : 2), y + 2, x + (right ? 1 : 3), y + 3, LEAF_DARK);
    }

    /** Plus-shaped flower; size 1 = 9px, size 2 = 15px. */
    private void flower(DrawContext c, int cx, int cy, int size, int color) {
        int p = 2 * size + 1, half = p / 2;
        int col = PETALS[Math.floorMod(color, PETALS.length)];
        c.fill(cx - half, cy - half - p, cx + half + 1, cy - half, col);
        c.fill(cx - half, cy + half + 1, cx + half + 1, cy + half + 1 + p, col);
        c.fill(cx - half - p, cy - half, cx - half, cy + half + 1, col);
        c.fill(cx + half + 1, cy - half, cx + half + 1 + p, cy + half + 1, col);
        c.fill(cx - half, cy - half, cx + half + 1, cy + half + 1, CENTER);
    }
}
