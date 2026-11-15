package dev.mythclient.module.misc;

import dev.mythclient.core.Category;

/** Turns on vanilla sound subtitles while enabled. */
public class SubtitlesModule extends BoolOptionModule {
    public SubtitlesModule() {
        super("Subtitles", "Shows captions for nearby sounds.", Category.MISC,
              () -> mc.options.getShowSubtitles(), true);
    }
}
