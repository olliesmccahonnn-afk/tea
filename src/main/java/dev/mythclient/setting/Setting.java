package dev.mythclient.setting;

import com.google.gson.JsonElement;

/** A saved, GUI-editable value on a module. */
public abstract class Setting<T> {

    private final String name;
    protected T value;

    protected Setting(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    public String name() {
        return name;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    /** Text shown in the GUI after the name, e.g. "ON" or "12.5". */
    public abstract String display();

    public abstract JsonElement toJson();

    public abstract void fromJson(JsonElement json);
}
