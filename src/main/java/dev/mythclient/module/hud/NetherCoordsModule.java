package dev.mythclient.module.hud;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/** Shows the matching coordinates in the other dimension (Overworld <-> Nether, 8:1). */
public class NetherCoordsModule extends HudModule {
    public NetherCoordsModule() { super("Nether Coords", "Shows your coordinates in the other dimension.", 270, 72); }

    @Override
    protected List<String> lines() {
        if (mc.player == null || mc.world == null) return List.of();
        BlockPos p = mc.player.getBlockPos();
        var dim = mc.world.getRegistryKey();
        if (dim == World.OVERWORLD) {
            return List.of("Nether: " + Math.floorDiv(p.getX(), 8) + " " + Math.floorDiv(p.getZ(), 8));
        }
        if (dim == World.NETHER) {
            return List.of("Overworld: " + (p.getX() * 8) + " " + (p.getZ() * 8));
        }
        return List.of();
    }
}
