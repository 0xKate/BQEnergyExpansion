package zeroxstudios.bqenergyexpansion.blocks.EnergySubmissionStation;

import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;
import zeroxstudios.bqenergyexpansion.blocks.base.TileEntityQuest;

public class ESSTileEntity extends TileEntityQuest {

    public ESSTileEntity() {}

    public double injectEnergy__unused(ForgeDirection directionFrom, double amount, double voltage) {
        long state = Math.round((this.getEnergyStored() / this.getCapacity()) * 100);
        Block tmp = worldObj.getBlock(xCoord, yCoord, zCoord);
        if (tmp instanceof ESSBlock) {

            ESSBlock block = (ESSBlock) tmp;
            boolean changed;

            if (state < 7) {
                changed = block.setIcon(0);
            } else if (state < 14) {
                changed = block.setIcon(1);
            } else if (state < 21) {
                changed = block.setIcon(2);
            } else if (state < 28) {
                changed = block.setIcon(3);
            } else if (state < 35) {
                changed = block.setIcon(4);
            } else if (state < 42) {
                changed = block.setIcon(5);
            } else if (state < 49) {
                changed = block.setIcon(6);
            } else if (state < 56) {
                changed = block.setIcon(7);
            } else if (state < 63) {
                changed = block.setIcon(8);
            } else if (state < 70) {
                changed = block.setIcon(9);
            } else if (state < 77) {
                changed = block.setIcon(10);
            } else if (state < 84) {
                changed = block.setIcon(11);
            } else if (state < 91) {
                changed = block.setIcon(12);
            } else if (state <= 99) {
                changed = block.setIcon(13);
            } else {
                changed = block.setIcon(14);
            }

            if (changed) {
                // ToDO: Replace with write/read NBT data and store the icon as nbtdata/metadata
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3);
                worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 1);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }

        return super.injectEnergy(directionFrom, amount, voltage);
    }
}
