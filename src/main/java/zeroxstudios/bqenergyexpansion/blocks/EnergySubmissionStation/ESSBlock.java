package zeroxstudios.bqenergyexpansion.blocks.EnergySubmissionStation;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import zeroxstudios.bqenergyexpansion.core.BQEnergyExpansion;
import zeroxstudios.bqenergyexpansion.core.Tags;

public class ESSBlock extends BlockContainer {

    public static final String name = "energy_submission_station";
    private final Random rng = new Random();

    public ESSBlock() {
        super(Material.iron);
        this.setBlockName(Tags.MODID + "." + name);
        this.setBlockTextureName(Tags.MODID + ":" + name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean onBlockActivated(
            World world, int x, int y, int z, EntityPlayer player, int side, float lx, float ly, float lz) {
        if (world.isRemote) return true;

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof ESSTileEntity) {
            player.openGui(BQEnergyExpansion.instance, 0, world, x, y, z);
            return true;
        }
        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int par6) {
        if (world.isRemote) return;
        ArrayList<ItemStack> itemsToDrop = new ArrayList<>();
        Object tile = world.getTileEntity(x, y, z);
        if (tile instanceof ESSTileEntity) {
            ESSTileEntity ess = (ESSTileEntity) tile;
            for (int i = 0; i < ess.getSizeInventory(); i++) {
                ItemStack stack = ess.getStackInSlot(i);
                if (stack != null) itemsToDrop.add(stack.copy());
            }
        }

        for (ItemStack itemStack : itemsToDrop) {
            EntityItem itemEnt = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, itemStack);
            itemEnt.setVelocity(
                    (rng.nextDouble() - 0.5) * 0.25, rng.nextDouble() * 0.5 * 0.25, (rng.nextDouble() - 0.5) * 0.25);
            world.spawnEntityInWorld(itemEnt);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World w, int p_149915_2_) {
        return new ESSTileEntity();
    }
}
