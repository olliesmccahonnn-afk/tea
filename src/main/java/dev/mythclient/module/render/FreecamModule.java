package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.setting.NumberSetting;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;

/**
 * Detaches the camera and lets you fly it around while your character stays where it is.
 * Movement keys fly the camera (jump = up, sneak = down); your character receives no input
 * and clicks do nothing while freecam is on. It turns itself off if you take damage.
 *
 * Only shows what your client has already loaded. Many servers prohibit freecam, so check the rules.
 * Camera position is applied in CameraMixin; mouse look in EntityMixin.
 */
public class FreecamModule extends ClientModule {
    public static FreecamModule INSTANCE;
    public static double x, y, z;
    public static float yaw, pitch;

    private final NumberSetting speed = (NumberSetting) setting(new NumberSetting("Speed (b/s)", 20, 5, 100, 5));
    private Perspective savedPerspective;
    private long lastNanos;

    public FreecamModule() {
        super("Freecam", "Fly the camera around while your character stays put.", Category.RENDER);
        INSTANCE = this;
        // Runs before the player reads input each tick: release the keys so the character stays still.
        ClientTickEvents.START_CLIENT_TICK.register(c -> {
            if (!isEnabled() || c.player == null) return;
            for (KeyBinding k : suppressed()) k.setPressed(false);
        });
    }

    public static boolean active() { return INSTANCE != null && INSTANCE.isEnabled() && mc.player != null; }

    private static KeyBinding[] suppressed() {
        var o = mc.options;
        return new KeyBinding[]{o.forwardKey, o.leftKey, o.backKey, o.rightKey, o.jumpKey, o.sneakKey,
                                o.sprintKey, o.attackKey, o.useKey, o.pickItemKey};
    }

    /** Physical state of a key, read straight from the keyboard (the KeyBinding itself is being held at false). */
    private static boolean down(KeyBinding kb) {
        InputUtil.Key key = InputUtil.fromTranslationKey(kb.getBoundKeyTranslationKey());
        if (key.getCategory() != InputUtil.Type.KEYSYM) return false;
        return InputUtil.isKeyPressed(mc.getWindow(), key.getCode());   // older versions: mc.getWindow().getHandle()
    }

    @Override
    protected void onEnable() {
        if (mc.player == null) return;
        var eye = mc.player.getEyePos();
        x = eye.x; y = eye.y; z = eye.z;
        yaw = mc.player.getYaw();
        pitch = mc.player.getPitch();
        lastNanos = System.nanoTime();
        savedPerspective = mc.options.getPerspective();
        mc.options.setPerspective(Perspective.FIRST_PERSON);
    }

    @Override
    protected void onDisable() {
        if (savedPerspective != null) {
            mc.options.setPerspective(savedPerspective);
            savedPerspective = null;
        }
        // Put movement keys back in sync with what is physically held.
        var o = mc.options;
        for (KeyBinding k : new KeyBinding[]{o.forwardKey, o.leftKey, o.backKey, o.rightKey, o.jumpKey, o.sneakKey, o.sprintKey}) {
            k.setPressed(down(k));
        }
    }

    @Override
    public void onTick() {
        if (isEnabled() && (mc.player == null || mc.player.hurtTime > 0)) setEnabled(false);
    }

    /** Called once per rendered frame from CameraMixin to move the camera smoothly. */
    public static void frame() {
        FreecamModule m = INSTANCE;
        long now = System.nanoTime();
        double dt = Math.min(0.1, (now - m.lastNanos) / 1.0e9);
        m.lastNanos = now;
        if (mc.currentScreen != null) return;

        var o = mc.options;
        double fwd = (down(o.forwardKey) ? 1 : 0) - (down(o.backKey) ? 1 : 0);
        double side = (down(o.rightKey) ? 1 : 0) - (down(o.leftKey) ? 1 : 0);
        double vert = (down(o.jumpKey) ? 1 : 0) - (down(o.sneakKey) ? 1 : 0);

        double yr = Math.toRadians(yaw), pr = Math.toRadians(pitch);
        double dx = -Math.sin(yr) * Math.cos(pr), dy = -Math.sin(pr), dz = Math.cos(yr) * Math.cos(pr);
        double rx = -Math.cos(yr), rz = -Math.sin(yr);

        double step = m.speed.doubleValue() * dt;
        x += (dx * fwd + rx * side) * step;
        y += (dy * fwd + vert) * step;
        z += (dz * fwd + rz * side) * step;
    }
}
