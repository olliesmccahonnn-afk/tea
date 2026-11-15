package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;

/** Stops the camera tilting when you take damage. Handled in GameRendererMixin. */
public class NoHurtCamModule extends ClientModule {
    public static NoHurtCamModule INSTANCE;

    public NoHurtCamModule() {
        super("No Hurt Cam", "Disables the camera shake when you are hit.", Category.RENDER);
        INSTANCE = this;
    }

    public static boolean active() { return INSTANCE != null && INSTANCE.isEnabled(); }
}
