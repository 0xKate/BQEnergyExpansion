package zeroxstudios.bqenergyexpansion.blocks.base;

import ic2.api.energy.event.EnergyTileEvent;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityEU extends TileEntityBase implements IEnergySink {

    public double internalEUStorage = 0;
    public double internalEUMax = 100000;
    private boolean connectedToEUNet = false;
    private boolean enableWorldTick;
    private boolean loaded = false;
    private int tier = 1;

    /**
     * Get the maximum amount of energy this sink can hold in its buffer.
     *
     * @return Capacity in EU.
     */
    public double getCapacity() {
        return internalEUMax;
    }
    /**
     * Set the maximum amount of energy this sink can hold in its buffer.
     *
     * @param newMax Capacity in EU.
     */
    public void setCapacity(double newMax) {
        this.internalEUMax = newMax;
        MinecraftForge.EVENT_BUS.post(new EnergyTileEvent(this));
        this.markDirty();
    }

    /**
     * Determine the energy stored in the sink's input buffer.
     *
     * @return amount in EU, may be above capacity
     */
    public double getEnergyStored() {
        return internalEUStorage;
    }

    /**
     * Set the stored energy to the specified amount.
     *
     * This is intended for server -> client synchronization, e.g. to display the stored energy in
     * a GUI through getEnergyStored().
     *
     */
    public void setEnergyStored(double amount) {
        internalEUStorage = amount;
    }

    /**
     * Set the IC2 energy tier for this sink.
     *
     * @param newTier IC2 Tier.
     */
    public void setSinkTier(int newTier) {
        this.tier = newTier;
    }

    /**
     * Determine how much energy the sink accepts.
     * <p>
     * Make sure that injectEnergy() accepts energy if demandsEnergy() returns anything > 0.
     *
     * @return max accepted input in eu
     * @note Modifying the energy net from this method is disallowed.
     */
    @Override
    public double getDemandedEnergy() {
        return internalEUMax - internalEUStorage;
    }

    /**
     * Determine the tier of this energy sink.
     * 1 = LV, 2 = MV, 3 = HV, 4 = EV etc.
     *
     * @return tier of this energy sink
     * @note Modifying the energy net from this method is disallowed.
     * @note Return Integer.MAX_VALUE to allow any voltage.
     */
    @Override
    public int getSinkTier() {
        return tier;
    }

    /**
     * Transfer energy to the sink.
     * <p>
     * It's highly recommended to accept all energy by letting the internal buffer overflow to
     * increase the performance and accuracy of the distribution simulation.
     *
     * @param directionFrom direction from which the energy comes from
     * @param amount        energy to be transferred
     * @param voltage
     * @return Energy not consumed (leftover)
     */
    @Override
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        internalEUStorage += amount;

        // Per Ic2 usage doc, should always return zero, and overfill the buffer.
        return 0;
    }

    /**
     * Determine if this acceptor can accept current from an adjacent emitter in a direction.
     * <p>
     * The TileEntity in the emitter parameter is what was originally added to the energy net,
     * which may be normal in-world TileEntity, a delegate or an IMetaDelegate.
     *
     * @param emitter   energy emitter, may also be null or an IMetaDelegate
     * @param direction direction the energy is being received from
     */
    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        // Accepts energy from all sides.
        return true;
    }

    @Override
    public final void validate() {
        super.validate();
        IC2.tickHandler.addSingleTickCallback(this.worldObj, world -> {
            if (!TileEntityEU.this.isInvalid()
                    && world.blockExists(
                            TileEntityEU.this.xCoord, TileEntityEU.this.yCoord, TileEntityEU.this.zCoord)) {
                TileEntityEU.this.onLoaded();
                if (!TileEntityEU.this.isInvalid() && (TileEntityEU.this.enableWorldTick)) {
                    world.loadedTileEntityList.add(TileEntityEU.this);
                }
            }
        });
    }

    @Override
    public final void invalidate() {
        this.onUnloaded();
        super.invalidate();
    }

    @Override
    public final void onChunkUnload() {
        this.onUnloaded();
        super.onChunkUnload();
    }

    public void onLoaded() {
        this.loaded = true;
        this.enableWorldTick = true;
        if (IC2.platform.isSimulating()) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this.connectedToEUNet = true;
        }
    }

    public void onUnloaded() {
        if (this.loaded) {
            if (IC2.platform.isSimulating() && this.connectedToEUNet) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
                this.connectedToEUNet = false;
            }
            this.worldObj.loadedTileEntityList.remove(this);
            this.loaded = false;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        this.internalEUStorage = tags.getDouble("internalEUStorage");
        this.internalEUMax = tags.getDouble("internalEUMax");
    }

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        tags.setDouble("internalEUStorage", this.internalEUStorage);
        tags.setDouble("internalEUMax", this.internalEUMax);
    }

    /**
     * Discharge the supplied ItemStack into this sink's energy buffer.
     *
     * @param stack ItemStack to discharge (null is ignored)
     * @param limit Transfer limit, values <= 0 will use the battery's limit
     * @return true if energy was transferred
     */
    public boolean discharge(ItemStack stack, int limit) {
        if (stack == null || !Info.isIc2Available()) return false;

        double amount = internalEUMax - internalEUStorage;
        if (amount <= 0) return false;

        if (limit > 0 && limit < amount) amount = limit;

        amount = ElectricItem.manager.discharge(stack, amount, tier, limit > 0, true, false);

        internalEUStorage += amount;

        return amount > 0;
    }
}
