package dev.mythclient.module.hud;

import dev.mythclient.setting.BoolSetting;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

/** Shows how many totems, golden apples, ender pearls (and optionally arrows) you are carrying. */
public class ConsumablesModule extends HudModule {
    private final BoolSetting totems = (BoolSetting) setting(new BoolSetting("Totems", true));
    private final BoolSetting gapples = (BoolSetting) setting(new BoolSetting("Golden apples", true));
    private final BoolSetting pearls = (BoolSetting) setting(new BoolSetting("Ender pearls", true));
    private final BoolSetting arrows = (BoolSetting) setting(new BoolSetting("Arrows", false));

    public ConsumablesModule() { super("Consumables", "Counts totems, golden apples and pearls.", 270, 4); }

    @Override
    protected List<String> lines() {
        List<String> out = new ArrayList<>();
        if (mc.player == null) return out;
        PlayerInventory inv = mc.player.getInventory();
        if (totems.get()) out.add("Totems: " + inv.count(Items.TOTEM_OF_UNDYING));
        if (gapples.get()) out.add("Gapples: " + (inv.count(Items.GOLDEN_APPLE) + inv.count(Items.ENCHANTED_GOLDEN_APPLE)));
        if (pearls.get()) out.add("Pearls: " + inv.count(Items.ENDER_PEARL));
        if (arrows.get()) out.add("Arrows: " + inv.count(Items.ARROW));
        return out;
    }
}
