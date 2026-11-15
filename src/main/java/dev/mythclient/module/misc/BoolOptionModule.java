package dev.mythclient.module.misc;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.option.SimpleOption;

import java.util.function.Supplier;

/** Base for modules that force one vanilla on/off option while enabled, then restore it. */
public abstract class BoolOptionModule extends ClientModule {
    private final Supplier<SimpleOption<Boolean>> option;
    private final boolean target;
    private Boolean saved;

    protected BoolOptionModule(String name, String desc, Category cat,
                               Supplier<SimpleOption<Boolean>> option, boolean target) {
        super(name, desc, cat);
        this.option = option;
        this.target = target;
        ClientLifecycleEvents.CLIENT_STOPPING.register(c -> restore());
    }

    private void restore() {
        if (saved != null) { option.get().setValue(saved); saved = null; }
    }

    @Override
    protected void onEnable() {
        saved = option.get().getValue();
        option.get().setValue(target);
    }

    @Override
    protected void onDisable() { restore(); }

    @Override
    public void onTick() {
        // keep it forced even if changed in the vanilla options screen
        if (isEnabled() && saved != null && option.get().getValue() != target) option.get().setValue(target);
    }
}
