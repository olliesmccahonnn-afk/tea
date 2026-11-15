package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.mixin.SimpleOptionAccessor;
import dev.mythclient.setting.NumberSetting;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

/** Narrows the FOV while enabled. Bind a key to it in the module key list. */
public class ZoomModule extends ClientModule {
    private final NumberSetting zoomFov = (NumberSetting) setting(new NumberSetting("Zoom FOV", 20, 5, 60, 1));
    private Integer saved;

    public ZoomModule() {
        super("Zoom", "Zooms in by narrowing your field of view.", Category.RENDER);
        ClientLifecycleEvents.CLIENT_STOPPING.register(c -> restore());
    }

    @SuppressWarnings("unchecked")
    private void apply(int v) {
        ((SimpleOptionAccessor<Integer>) (Object) mc.options.getFov()).mythclient$setRaw(v);
    }

    private void restore() {
        if (saved != null) { apply(saved); saved = null; }
    }

    @Override
    protected void onEnable() {
        saved = mc.options.getFov().getValue();
        apply(zoomFov.intValue());
    }

    @Override
    protected void onDisable() { restore(); }

    @Override
    public void onTick() {
        if (isEnabled() && saved != null) apply(zoomFov.intValue());
    }
}
