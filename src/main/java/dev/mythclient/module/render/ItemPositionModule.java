package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.setting.NumberSetting;

/** Shifts held items in first person. Rendering is done in HeldItemRendererMixin. */
public class ItemPositionModule extends ClientModule {
    public static ItemPositionModule INSTANCE;
    private final NumberSetting x = (NumberSetting) setting(new NumberSetting("X (%)", 0, -50, 50, 5));
    private final NumberSetting y = (NumberSetting) setting(new NumberSetting("Y (%)", 0, -50, 50, 5));
    private final NumberSetting z = (NumberSetting) setting(new NumberSetting("Z (%)", 0, -50, 50, 5));

    public ItemPositionModule() {
        super("Item Position", "Moves your held items in first person.", Category.RENDER);
        INSTANCE = this;
    }

    public static boolean active() { return INSTANCE != null && INSTANCE.isEnabled(); }
    public float dx() { return (float) (x.doubleValue() / 100.0); }
    public float dy() { return (float) (y.doubleValue() / 100.0); }
    public float dz() { return (float) (z.doubleValue() / 100.0); }
}
