package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.setting.BoolSetting;
import dev.mythclient.setting.NumberSetting;

/** Lowers the first-person shield. Rendering is done in HeldItemRendererMixin. */
public class LowShieldModule extends ClientModule {
    public static LowShieldModule INSTANCE;
    private final NumberSetting lower = (NumberSetting) setting(new NumberSetting("Lower (%)", 25, 0, 60, 5));
    private final BoolSetting onlyBlocking = (BoolSetting) setting(new BoolSetting("Only while blocking", true));

    public LowShieldModule() {
        super("Low Shield", "Lowers your shield so it blocks less of your view.", Category.RENDER);
        INSTANCE = this;
    }

    public static boolean active() { return INSTANCE != null && INSTANCE.isEnabled(); }
    public float offset() { return (float) (lower.doubleValue() / 100.0); }
    public boolean onlyBlocking() { return onlyBlocking.get(); }
}
