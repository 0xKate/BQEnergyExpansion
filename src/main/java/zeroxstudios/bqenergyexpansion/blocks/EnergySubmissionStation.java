package zeroxstudios.bqenergyexpansion.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import zeroxstudios.bqenergyexpansion.core.Tags;

public class EnergySubmissionStation extends BlockContainer {

    public static final String name = "energy_submission_station";

    protected EnergySubmissionStation() {
        super(Material.iron);
        this.setBlockName(Tags.MODID + "." + name);
        this.setBlockTextureName(Tags.MODID + ":" + name);
    }

    @Override
    public TileEntity createNewTileEntity(World w, int p_149915_2_) {
        return null;
    }
}
