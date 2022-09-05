package zeroxstudios.bqenergyexpansion.blocks.EnergySubmissionStation;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.storage.DBEntry;
import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.ForgeDirection;
import zeroxstudios.bqenergyexpansion.tasks.IEUTask;

public class ESSQuestBlock extends ESSTileEntityEU {

    public UUID owner;
    public int questID;
    public int taskID;
    private DBEntry<IQuest> qCached;

    public boolean isSetup() {
        return owner != null && questID >= 0 && taskID >= 0;
    }

    public void reset() {
        owner = null;
        questID = -1;
        taskID = -1;
        qCached = null;
        this.markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        //
    }

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        //
        tags.setString("owner", owner != null ? owner.toString() : "");
        tags.setInteger("questID", questID);
        tags.setInteger("taskID", taskID);
    }

    public DBEntry<IQuest> getQuest() {
        if (questID < 0) {
            return null;
        } else {
            if (qCached == null) {
                IQuest tmp = QuestingAPI.getAPI(ApiReference.QUEST_DB).getValue(questID);
                if (tmp != null) qCached = new DBEntry<>(questID, tmp);
            }
            return qCached;
        }
    }

    public ITask getRawTask() {
        DBEntry<IQuest> q = getQuest();

        if (q == null || taskID < 0) {
            return null;
        } else {
            return q.getValue().getTasks().getValue(taskID);
        }
    }

    public IEUTask getTask() {
        ITask t = getRawTask();
        return t instanceof IEUTask ? (IEUTask) t : null;
    }

    // Todo: Needs Work
    @Override
    public S35PacketUpdateTileEntity getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    // Todo: Needs Work
    @Override
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        super.injectEnergy(directionFrom, amount, voltage);

        if (!isSetup()
                || amount <= 0
                || QuestingAPI.getAPI(ApiReference.SETTINGS).getProperty(NativeProps.EDIT_MODE)) return 0;

        DBEntry<IQuest> q = getQuest();
        IEUTask t = getTask();

        t.submitEnergy(q, owner, (int) amount);

        if (t.isComplete(owner)) {
            reset();
            MinecraftServer.getServer()
                    .getConfigurationManager()
                    .sendToAllNearExcept(
                            null, xCoord, yCoord, zCoord, 128, worldObj.provider.dimensionId, getDescriptionPacket());
        }

        return 0;
    }
}
