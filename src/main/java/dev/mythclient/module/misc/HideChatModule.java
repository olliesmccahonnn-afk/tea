package dev.mythclient.module.misc;

import dev.mythclient.core.Category;
import net.minecraft.client.option.ChatVisibility;

/** Hides the chat while enabled (useful for clean screenshots or recording). Restores your setting on disable. */
public class HideChatModule extends OptionModule<ChatVisibility> {
    public HideChatModule() {
        super("Hide Chat", "Hides the chat window.", Category.MISC,
              () -> mc.options.getChatVisibility(), ChatVisibility.HIDDEN);
    }
}
