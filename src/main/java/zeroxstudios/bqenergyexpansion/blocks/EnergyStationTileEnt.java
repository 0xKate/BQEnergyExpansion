package zeroxstudios.bqenergyexpansion.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import zeroxstudios.bqenergyexpansion.core.BQEnergyExpansion;

public class EnergyStationTileEnt extends TileEntity implements IInventory {

    private ItemStack[] internalStorage = new ItemStack[15];

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return internalStorage.length;
    }

    /**
     * Returns the stack in slot i
     *
     * @param slot: Slot to get item stack from.
     */
    @Override
    public ItemStack getStackInSlot(int slot) {
        return internalStorage[slot];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     *
     * @param slot
     * @param amount
     */
    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (internalStorage[slot] != null) {
            ItemStack itemstack;
            if (internalStorage[slot].stackSize == amount) {
                itemstack = internalStorage[slot];
                internalStorage[slot] = null;
            } else {
                itemstack = internalStorage[slot].splitStack(amount);
                if (internalStorage[slot].stackSize == 0) internalStorage[slot] = null;
            }
            markDirty();
            return itemstack;
        } else {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     *
     * @param slot
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (internalStorage[slot] != null) {
            ItemStack itemstack = internalStorage[slot];
            internalStorage[slot] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     *
     * @param slot
     * @param stack
     */
    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        internalStorage[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }

        markDirty();
    }

    /**
     * Returns the name of the inventory
     */
    @Override
    public String getInventoryName() {
        return "tile.bqenergyexpansion.energy_submission_station.storage";
    }

    /**
     * Returns if the inventory is named
     */
    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    /**
     * Returns the maximum stack size for a inventory slot.
     */
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     *
     * @param player
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
                && player.getDistanceSq((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D)
                        <= 64.0D;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList list = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        internalStorage = new ItemStack[getSizeInventory()];

        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound comp = list.getCompoundTagAt(i);
            int j = comp.getByte("Slot") & 255;
            if (j >= 0 && j < internalStorage.length) {
                internalStorage[j] = ItemStack.loadItemStackFromNBT(comp);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList tags = new NBTTagList();

        for (int i = 0; i < this.getSizeInventory(); ++i) {
            if (internalStorage[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                NBTTagCompound newTag = internalStorage[i].writeToNBT(tag);
                tags.appendTag(newTag);
            }
        }
        nbt.setTag("Items", tags);
    }

    @Override
    public void openInventory() {
        BQEnergyExpansion.info("openInventory was called");
    }

    @Override
    public void closeInventory() {
        BQEnergyExpansion.info("closeInventory was called");
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     *
     * @param slot
     * @param stack
     */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }
}
