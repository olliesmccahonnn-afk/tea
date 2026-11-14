package dev.mythclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public final class BoolSetting extends Setting<Boolean> {

    public BoolSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    public void toggle() {
        value = !value;
    }

    @Override
    public String display() {
        return value ? "ON" : "OFF";
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public void fromJson(JsonElement json) {
        value = json.getAsBoolean();
    }
}
