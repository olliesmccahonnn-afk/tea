package dev.mythclient.module.hud;

import net.minecraft.client.option.KeyBinding;

import java.util.List;

/** Shows WASD and jump, with brackets around the keys you are holding. */
public class KeystrokesModule extends HudModule {
    public KeystrokesModule() { super("Keystrokes", "Shows which movement keys are held.", 180, 4); }

    private static String k(KeyBinding b, String label) {
        return b.isPressed() ? "[" + label + "]" : " " + label + " ";
    }

    @Override
    protected List<String> lines() {
        if (mc.player == null) return List.of();
        var o = mc.options;
        return List.of(
            "     " + k(o.forwardKey, "W"),
            k(o.leftKey, "A") + " " + k(o.backKey, "S") + " " + k(o.rightKey, "D"),
            k(o.jumpKey, "SPACE"));
    }
}
