/**
 * Portions of this file have been copied from BetterQuesting and is copyrighted by Funwayguy under the terms of the MIT license.
 */
package zeroxstudios.bqenergyexpansion.blocks.base;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.cache.QuestCache;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.ForgeDirection;
import zeroxstudios.bqenergyexpansion.tasks.IEUTask;
import zeroxstudios.bqenergyexpansion.tasks.TaskEUCharge;

public class TileEntityQuest extends TileEntityEU {
    // <editor-fold desc="Quest Aware Block Methods">
    public UUID owner = null;
    public int questID = -1;
    public int taskID = -1;

    private DBEntry<IQuest> qCached;

    public DBEntry<IQuest> getQuest() {
        if (questID < 0) return null;

        if (qCached == null) {
            IQuest q = QuestingAPI.getAPI(ApiReference.QUEST_DB).getValue(questID);
            if (q != null) qCached = new DBEntry<>(questID, q);
        }

        return qCached;
    }

    @SuppressWarnings("WeakerAccess")
    public ITask getRawTask() {
        DBEntry<IQuest> q = getQuest();
        if (q == null || taskID < 0) return null;
        return q.getValue().getTasks().getValue(taskID);
    }

    public IEUTask getEUTask() {
        ITask t = getRawTask();
        return t instanceof IEUTask ? (IEUTask) t : null;
    }

    public void sendReset() {
        reset();
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            server.getConfigurationManager()
                    .sendToAllNearExcept(
                            null, xCoord, yCoord, zCoord, 128, worldObj.provider.dimensionId, getDescriptionPacket());
        }
    }

    public void syncQuest() {
        if (!isSetup()) return;

        EntityPlayerMP player = getPlayerByUUID(owner);
        if (player != null) {
            QuestCache qc = (QuestCache) player.getExtendedProperties(QuestCache.LOC_QUEST_CACHE.toString());
            if (qc != null && questID > -1) {
                qc.markQuestDirty(questID); // Let the cache take care of syncing
            }
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote
                || !isSetup()
                || QuestingAPI.getAPI(ApiReference.SETTINGS).getProperty(NativeProps.EDIT_MODE)) return;

        long wtt = worldObj.getTotalWorldTime();
        if (wtt % 10 == 0 && owner != null) {
            if (wtt % 20 == 0) qCached = null; // Reset and lookup quest again once every second
            DBEntry<IQuest> q = getQuest();
            IEUTask t = getEUTask();

            if (q != null && t != null && owner != null) {
                if (t.isComplete(owner)) {
                    syncQuest();
                    sendReset();
                }
            }

            if (t != null && owner != null && t.isComplete(owner)) {
                syncQuest();
                sendReset();
            }
        }
    }

    public static EntityPlayerMP getPlayerByUUID(UUID uuid) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) return null;

        for (EntityPlayerMP player : (List<EntityPlayerMP>) server.getConfigurationManager().playerEntityList) {
            if (player.getGameProfile().getId().equals(uuid)) return player;
        }

        return null;
    }

    public void setupTask(UUID owner, IQuest quest, ITask task) {
        if (owner == null || quest == null || task == null) {
            reset();
            return;
        }

        this.questID = QuestingAPI.getAPI(ApiReference.QUEST_DB).getID(quest);
        this.qCached = new DBEntry<>(questID, quest);
        this.taskID = quest.getTasks().getID(task);

        if (this.questID < 0 || this.taskID < 0) {
            reset();
            return;
        }

        if (task instanceof TaskEUCharge) {
            double req = ((TaskEUCharge) task).getRequiredEnergy();
            this.setCapacity(req);
            this.setEnergyStored(0);
            this.setSinkTier(4);
            this.setOwner(owner);
            this.markDirty();
        }
    }

    private void setOwner(UUID newOwner) {
        this.owner = newOwner;
    }

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

    /**
     * Overridden in a sign to provide the text.
     */
    @Nonnull
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        this.writeToNBT(nbtTagCompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbtTagCompound);
    }

    /**
     * Called when you receive a TileEntityData packet for the location this
     * TileEntity is currently in. On the client, the NetworkManager will always
     * be the remote server. On the server, it will be whomever is responsible for
     * sending the packet.
     *
     * @param net The NetworkManager the packet originated from
     * @param pkt The data packet
     */
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);

        try {
            owner = UUID.fromString(tags.getString("owner"));
        } catch (Exception e) {
            this.reset();
            return;
        }

        questID = tags.hasKey("questID") ? tags.getInteger("questID") : -1;
        taskID = tags.hasKey("task") ? tags.getInteger("task") : -1;

        if (!isSetup()) // All data must be present for this to run correctly
        {
            this.reset();
        }
    }

    @Nonnull
    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        tags.setString("owner", owner != null ? owner.toString() : "");
        tags.setInteger("questID", questID);
        tags.setInteger("task", taskID);
    }
    // </editor-fold>

    // <editor-fold desc="IEnergySink Methods">
    @Override
    public double getDemandedEnergy() {
        if (!isSetup() || QuestingAPI.getAPI(ApiReference.SETTINGS).getProperty(NativeProps.EDIT_MODE)) return 0.0;
        return super.getDemandedEnergy();
    }

    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        if (!isSetup()
                || amount <= 0
                || QuestingAPI.getAPI(ApiReference.SETTINGS).getProperty(NativeProps.EDIT_MODE)) return 0;

        DBEntry<IQuest> quest = getQuest();
        IEUTask task = getEUTask();

        double remainder = 0.0;
        if (task != null) {

            if (task.canSubmitEnergy(quest, owner, amount, voltage)) {
                EntityPlayerMP player = getPlayerByUUID(owner);
                if (player != null) {
                    task.submitEnergy(new ParticipantInfo(player), quest, owner, amount, voltage);
                }

                remainder = super.injectEnergy(directionFrom, amount, voltage);
            }

            if (task.isComplete(owner) && super.getDemandedEnergy() <= 0.0) {
                if (worldObj.isRemote) syncQuest();
                sendReset();
            }
        } // </editor-fold>
        return remainder;
    }
}
