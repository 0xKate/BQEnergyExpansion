package zeroxstudios.bqenergyexpansion.blocks;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.IC2;
import ic2.core.ITickCallback;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyStationEUMachine extends EnergyStationTileEnt implements IEnergySink {

    private double internalEUStorage = 0;
    private double internalEUMax = 100000;
    private int tier = 4;
    private float guiChargeLevel;
    private boolean connectedToEUNet = false;
    private boolean init = false;
    private boolean enableWorldTick;
    private boolean loaded = false;

    public void setMaxEUStorage(double newValue) {
        internalEUMax = newValue;
    }

    /**
     * Determine how much energy the sink accepts.
     * <p>
     * Make sure that injectEnergy() does accepts energy if demandsEnergy() returns anything > 0.
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
        return 4;
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
        // TODO: Interpret Quest Voltage Here
        internalEUStorage += amount;
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
        IC2.tickHandler.addSingleTickCallback(this.worldObj, new ITickCallback() {
            public void tickCallback(World world) {
                if (!EnergyStationEUMachine.this.isInvalid()
                        && world.blockExists(
                                EnergyStationEUMachine.this.xCoord,
                                EnergyStationEUMachine.this.yCoord,
                                EnergyStationEUMachine.this.zCoord)) {
                    EnergyStationEUMachine.this.onLoaded();
                    if (!EnergyStationEUMachine.this.isInvalid() && (EnergyStationEUMachine.this.enableWorldTick)) {
                        world.loadedTileEntityList.add(EnergyStationEUMachine.this);
                    }
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
            this.loaded = false;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.internalEUStorage = nbttagcompound.getDouble("energy");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("energy", this.internalEUStorage);
    }

    @Override
    public final void updateEntity() {
        super.updateEntity();

        if (this.enableWorldTick) {
            if (this.worldObj.isRemote) {
                this.updateEntityClient();
            } else {
                this.updateEntityServer();
            }
        }
    }

    protected void updateEntityClient() {}

    protected void updateEntityServer() {
        if (this.internalEUMax - this.internalEUStorage >= 1.0) {

            // double amount = this.dischargeSlot.discharge((double)this.maxEnergy - this.energy, false);
            // if (amount > 0.0) {
            //    this.energy += amount;
            //    this.markDirty();
            // }
        }

        this.guiChargeLevel = Math.min(1.0F, (float) this.internalEUStorage / (float) this.internalEUMax);
    }
}
