package dev.mythclient.gui;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/**
 * Reads the mouse buttons straight from GLFW once per frame.
 *
 * Minecraft changed its click/drag/key classes in 1.21.9, so the click GUI and HUD editor avoid
 * mouseClicked/mouseDragged entirely: they call {@link #poll()} in render() and read the edges below.
 * (Cursor position comes from render()'s own mouseX/mouseY, already in GUI coordinates.)
 */
public final class MouseInput {

    private boolean primed;
    private boolean prevLeft;
    private boolean prevRight;

    public boolean left;
    public boolean right;
    public boolean leftPressed;   // went down this frame
    public boolean rightPressed;
    public boolean leftReleased;  // went up this frame

    public void poll() {
        long window = MinecraftClient.getInstance().getWindow().getHandle();
        boolean l = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean r = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        if (!primed) { // ignore a button that was already held when the screen opened
            prevLeft = l;
            prevRight = r;
            primed = true;
        }
        leftPressed = l && !prevLeft;
        rightPressed = r && !prevRight;
        leftReleased = !l && prevLeft;
        left = l;
        right = r;
        prevLeft = l;
        prevRight = r;
    }

    public static boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }
}
