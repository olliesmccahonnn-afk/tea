package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;

/**
 * Hides the sidebar scoreboard. Done client-side by clearing the sidebar slot
 * (re-cleared every tick in case the server sends it again) and restoring it on disable.
 */
public class NoScoreboardModule extends ClientModule {
    private ScoreboardObjective hidden;

    public NoScoreboardModule() {
        super("No Scoreboard", "Hides the sidebar scoreboard.", Category.RENDER);
        ClientLifecycleEvents.CLIENT_STOPPING.register(c -> restore());
    }

    private void restore() {
        if (hidden != null && mc.world != null) {
            mc.world.getScoreboard().setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, hidden);
        }
        hidden = null;
    }

    @Override protected void onDisable() { restore(); }

    @Override
    public void onTick() {
        if (!isEnabled() || mc.world == null) return;
        Scoreboard sb = mc.world.getScoreboard();
        ScoreboardObjective cur = sb.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        if (cur != null) {
            hidden = cur;
            sb.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, null);
        }
    }
}
