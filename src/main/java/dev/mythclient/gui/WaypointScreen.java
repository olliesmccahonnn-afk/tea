package dev.mythclient.gui;

import dev.mythclient.MythClient;
import dev.mythclient.core.ClientModule;
import dev.mythclient.module.world.Waypoint;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Add and delete waypoints for the current server / world. Uses normal buttons + a text box
 * so typing a name works like anywhere else in the game.
 */
public class WaypointScreen extends Screen implements MythScreen {

    private static int page = 0;

    private final Screen parent;
    private String nameText = "";
    private List<Waypoint> rows = List.of();
    private int perPage = 6;
    private int left;

    public WaypointScreen(Screen parent) {
        super(Text.literal("Waypoints"));
        this.parent = parent;
    }

    private static String shortDimension(String dim) {
        int i = dim.indexOf(':');
        return i >= 0 ? dim.substring(i + 1) : dim;
    }

    @Override
    protected void init() {
        left = this.width / 2 - 150;
        rows = MythClient.waypoints().inCurrentWorld();
        perPage = Math.max(3, Math.min(10, (this.height - 130) / 24));
        int pages = Math.max(1, (rows.size() + perPage - 1) / perPage);
        page = Math.max(0, Math.min(page, pages - 1));

        // ---- add row
        TextFieldWidget name = new TextFieldWidget(this.textRenderer, left, 34, 190, 20, Text.literal("Name"));
        name.setMaxLength(32);
        name.setPlaceholder(Text.literal("Name (optional)"));
        name.setText(nameText);
        name.setChangedListener(s -> nameText = s);
        addDrawableChild(name);
        addDrawableChild(ButtonWidget.builder(Text.literal("Add here"), b -> {
            if (MythClient.waypoints().addHere(nameText) != null) {
                nameText = "";
                page = Integer.MAX_VALUE; // jump to the newest
                clearAndInit();
            }
        }).dimensions(left + 196, 34, 104, 20).build());

        // ---- list rows
        int start = page * perPage;
        for (int i = 0; i < perPage && start + i < rows.size(); i++) {
            Waypoint w = rows.get(start + i);
            int y = 64 + i * 24;
            addDrawableChild(ButtonWidget.builder(Text.literal(w.visible ? "Shown" : "Hidden"), b -> {
                w.visible = !w.visible;
                MythClient.waypoints().save();
                clearAndInit();
            }).dimensions(left + 196, y, 52, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Delete"), b -> {
                MythClient.waypoints().remove(w);
                clearAndInit();
            }).dimensions(left + 252, y, 48, 20).build());
        }

        // ---- footer: paging, module switch, back
        int fy = this.height - 28;
        addDrawableChild(ButtonWidget.builder(Text.literal("<"), b -> {
            page--;
            clearAndInit();
        }).dimensions(left, fy, 20, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal(">"), b -> {
            page++;
            clearAndInit();
        }).dimensions(left + 24, fy, 20, 20).build());

        ClientModule module = waypointsModule();
        if (module != null) {
            addDrawableChild(ButtonWidget.builder(Text.literal("HUD list: " + (module.isEnabled() ? "ON" : "OFF")), b -> {
                module.toggle();
                MythClient.config().save();
                clearAndInit();
            }).dimensions(left + 52, fy, 110, 20).build());
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> close())
                .dimensions(left + 220, fy, 80, 20).build());
    }

    private static ClientModule waypointsModule() {
        for (ClientModule m : MythClient.modules().all()) {
            if (m.name().equals("Waypoints")) return m;
        }
        return null;
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Waypoints"), this.width / 2, 12, 0xFFFFFFFF);

        int start = page * perPage;
        for (int i = 0; i < perPage && start + i < rows.size(); i++) {
            Waypoint w = rows.get(start + i);
            int y = 64 + i * 24;
            String label = this.textRenderer.trimToWidth(w.name, 90);
            ctx.drawTextWithShadow(this.textRenderer, label, left, y + 2, w.visible ? 0xFFFFFFFF : 0xFF9A9A9A);
            ctx.drawTextWithShadow(this.textRenderer, w.x + " " + w.y + " " + w.z + "  " + shortDimension(w.dimension),
                    left, y + 11, 0xFF9A9A9A);
        }
        if (rows.isEmpty()) {
            ctx.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("No waypoints here yet. Stand where you want one and press Add here (or the B key)."),
                    this.width / 2, 80, 0xFF9A9A9A);
        }
        int pages = Math.max(1, (rows.size() + perPage - 1) / perPage);
        ctx.drawTextWithShadow(this.textRenderer, (page + 1) + "/" + pages, left + 52 + 116, this.height - 22, 0xFF9A9A9A);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
