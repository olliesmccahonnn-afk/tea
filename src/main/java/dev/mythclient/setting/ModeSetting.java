package dev.mythclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.List;

/** Pick one option from a fixed list; the GUI cycles through them. */
public final class ModeSetting extends Setting<String> {

    private final List<String> modes;

    public ModeSetting(String name, String defaultValue, String... modes) {
        super(name, defaultValue);
        this.modes = List.of(modes);
    }

    public void cycle() {
        int i = modes.indexOf(value);
        value = modes.get((i + 1) % modes.size());
    }

    public boolean is(String mode) {
        return value.equals(mode);
    }

    @Override
    public String display() {
        return value;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public void fromJson(JsonElement json) {
        String s = json.getAsString();
        if (modes.contains(s)) value = s;
    }
}
