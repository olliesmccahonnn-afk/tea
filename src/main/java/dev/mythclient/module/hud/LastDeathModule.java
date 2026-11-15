package dev.mythclient.module.hud;

import java.util.List;

/** Shows where you last died, using the position the game already tracks. */
public class LastDeathModule extends HudModule {
    public LastDeathModule() { super("Last Death", "Shows the coordinates of your last death.", 180, 40); }

    @Override
    protected List<String> lines() {
        if (mc.player == null) return List.of();
        return mc.player.getLastDeathPos()
            .map(g -> List.of("Death: " + g.pos().getX() + " " + g.pos().getY() + " " + g.pos().getZ()
                              + " (" + g.dimension().getValue().getPath() + ")"))
            .orElse(List.of());
    }
}
