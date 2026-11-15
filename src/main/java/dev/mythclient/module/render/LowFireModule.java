package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.setting.NumberSetting;

/** Lowers the on-screen fire overlay so it covers less of your view. Rendering is done in InGameOverlayRendererMixin. */
public class LowFireModule extends ClientModule {
    public static LowFireModule INSTANCE;
    private final NumberSetting lower = (NumberSetting) setting(new NumberSetting("Lower (%)", 30, 0, 60, 5));

    public LowFireModule() {
        super("Low Fire", "Lowers the fire overlay when you burn.", Category.RENDER);
        INSTANCE = this;
    }

    public static boolean active() { return INSTANCE != null && INSTANCE.isEnabled(); }
    public float offset() { return (float) (lower.doubleValue() / 100.0); }
}
