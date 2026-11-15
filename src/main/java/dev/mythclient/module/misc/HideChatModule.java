package dev.mythclient.module.misc;

import dev.mythclient.core.Category;

/** Hides the chat while enabled (useful for clean screenshots or recording). Restores your setting on disable. */
@SuppressWarnings({"rawtypes", "unchecked"})
public class HideChatModule extends OptionModule {
    public HideChatModule() {
        super("Hide Chat", "Hides the chat window.", Category.MISC,
              () -> mc.options.getChatVisibility(), null);
    }

    @Override protected Object target() { return enumConst(mc.options.getChatVisibility(), "HIDDEN"); }
}
