package clans.clan.utils.map;

import java.util.HashMap;

import net.minecraft.world.level.material.MaterialMapColor;
import playerstoragev2.PlayerStorage;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;

public class NMSHandler {
    private HashMap<MaterialMapColor, MaterialMapColorWrapper> colors = new HashMap<MaterialMapColor, MaterialMapColorWrapper>();
    {
        for (MaterialMapColor color : MaterialMapColor.a)
            if (color != null)
                colors.put(color, new MaterialMapColorWrapper(color));
    }

    public MaterialMapColorWrapper getColorNeutral() {
        return colors.get(MaterialMapColor.b);
    }

    public MaterialMapColorWrapper getBlockColor(Block block) {
        net.minecraft.world.level.block.Block nmsblock = CraftMagicNumbers.getBlock(block.getType());

        MaterialMapColor nms = nmsblock.s();

        if (!colors.containsKey(nms))
            PlayerStorage.log("[error]  unknown color, error in NMSHandler - please report to author!");

        return colors.get(nms);
    }

    public boolean hasTwoHands() {
        return true;
    }
}