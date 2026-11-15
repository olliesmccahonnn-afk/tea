package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.setting.BoolSetting;
import net.minecraft.client.option.Perspective;

/**
 * Look around without turning your character. The mouse moves the camera only; your body,
 * aim and movement direction keep the rotation you had when freelook started.
 * The camera is driven by EntityMixin (mouse input) and CameraMixin (render).
 *
 * Bind a key to this module. With "Hold to use" on, freelook is active only while that key is held.
 */
public class FreelookModule extends ClientModule {
    public static FreelookModule INSTANCE;
    public static float cameraYaw, cameraPitch;

    private final BoolSetting hold = (BoolSetting) setting(new BoolSetting("Hold to use", true));
    private final BoolSetting thirdPerson = (BoolSetting) setting(new BoolSetting("Third person", true));
    private Perspective savedPerspective;
    private boolean sawPressed;

    public FreelookModule() {
        super("Freelook", "Look around without turning your character.", Category.RENDER);
        INSTANCE = this;
    }

    public static boolean active() { return INSTANCE != null && INSTANCE.isEnabled() && mc.player != null; }

    @Override
    protected void onEnable() {
        if (mc.player == null) return;
        cameraYaw = mc.player.getYaw();
        cameraPitch = mc.player.getPitch();
        sawPressed = false;
        if (thirdPerson.get()) {
            savedPerspective = mc.options.getPerspective();
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        }
    }

    @Override
    protected void onDisable() {
        if (savedPerspective != null) {
            mc.options.setPerspective(savedPerspective);
            savedPerspective = null;
        }
    }

    @Override
    public void onTick() {
        if (!isEnabled() || !hold.get()) return;
        var key = keyBinding();
        if (key == null || key.isUnbound()) return;      // enabled from the menu: stay on until toggled off
        if (key.isPressed()) sawPressed = true;
        else if (sawPressed) setEnabled(false);          // released after being held
    }
}
