package dev.mythclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public final class NumberSetting extends Setting<Double> {

    private final double min;
    private final double max;
    private final double step;

    public NumberSetting(String name, double defaultValue, double min, double max, double step) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public void set(Double v) {
        double snapped = Math.round(v / step) * step;
        value = Math.max(min, Math.min(max, snapped));
    }

    public void increase() {
        set(value + step);
    }

    public void decrease() {
        set(value - step);
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    /** 0..1 position of the current value, for sliders. */
    public double fraction() {
        return max == min ? 0 : (value - min) / (max - min);
    }

    public void setFraction(double f) {
        set(min + Math.max(0, Math.min(1, f)) * (max - min));
    }

    public int intValue() {
        return (int) Math.round(value);
    }

    public double doubleValue() {
        return value;
    }

    @Override
    public String display() {
        return step >= 1 ? String.valueOf(intValue()) : String.format("%.2f", value);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public void fromJson(JsonElement json) {
        set(json.getAsDouble());
    }
}
