package clans.clan.utils.map;

import net.minecraft.world.level.material.MaterialMapColor;

public class MaterialMapColorWrapper {
    private MaterialMapColor color;

    public MaterialMapColorWrapper(MaterialMapColor color) {
        this.color = color;
    }

    public int getM() {
        return color.am * 4 + 2;

    }
}