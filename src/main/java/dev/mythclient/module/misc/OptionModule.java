package dev.mythclient.module.misc;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.option.SimpleOption;

import java.util.function.Supplier;

/** Generic version of BoolOptionModule: forces any vanilla option while enabled, then restores it. */
public abstract class OptionModule<T> extends ClientModule {
    private final Supplier<SimpleOption<T>> option;
    private final T target;
    private T saved;
    private boolean active;

    protected OptionModule(String name, String desc, Category cat, Supplier<SimpleOption<T>> option, T target) {
        super(name, desc, cat);
        this.option = option;
        this.target = target;
        ClientLifecycleEvents.CLIENT_STOPPING.register(c -> restore());
    }

    /** Value to force. Subclasses may override to resolve it lazily (e.g. enum constants looked up at runtime). */
    protected T target() { return target; }

    /** Finds an enum constant of an option's value type by name (falls back to the last constant), without naming the enum class. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static Object enumConst(SimpleOption<?> opt, String name) {
        Enum cur = (Enum) opt.getValue();
        Object[] all = cur.getDeclaringClass().getEnumConstants();
        for (Object o : all) if (((Enum) o).name().equals(name)) return o;
        return all[all.length - 1];
    }

    private void restore() {
        if (active) { option.get().setValue(saved); active = false; }
    }

    @Override protected void onEnable() {
        saved = option.get().getValue();
        active = true;
        option.get().setValue(target());
    }

    @Override protected void onDisable() { restore(); }

    @Override public void onTick() {
        if (isEnabled() && active && !target.equals(option.get().getValue())) option.get().setValue(target());
    }
}
