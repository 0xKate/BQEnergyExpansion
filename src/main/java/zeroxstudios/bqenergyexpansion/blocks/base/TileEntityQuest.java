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
import zeroxstudios.bqenergyexpansion.core.BQEnergyExpansion;
import zeroxstudios.bqenergyexpansion.tasks.IEUTask;
import zeroxstudios.bqenergyexpansion.tasks.TaskEUCharge;

public class TileEntityQuest extends TileEntityEU {
    // <editor-fold desc="Quest Aware Block Methods">
    private boolean needsUpdate = false;
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
            MinecraftServer server = MinecraftServer.getServer();
            EntityPlayerMP player = getPlayerByUUID(owner);
            QuestCache qc = player == null
                    ? null
                    : (QuestCache) player.getExtendedProperties(QuestCache.LOC_QUEST_CACHE.toString());

            if (q != null && t != null) {
                if (t.isComplete(owner)) {
                    reset();
                    needsUpdate = true;
                    if (server != null) {
                        server.getConfigurationManager()
                                .sendToAllNearExcept(
                                        null,
                                        xCoord,
                                        yCoord,
                                        zCoord,
                                        128,
                                        worldObj.provider.dimensionId,
                                        getDescriptionPacket());
                    }
                }
            }

            if (needsUpdate) {
                needsUpdate = false;

                if (q != null && qc != null) {
                    qc.markQuestDirty(questID); // Let the cache take care of syncing
                }
            }

            if (t != null && t.isComplete(owner)) {
                reset();
                MinecraftServer.getServer()
                        .getConfigurationManager()
                        .sendToAllNearExcept(
                                null,
                                xCoord,
                                yCoord,
                                zCoord,
                                128,
                                worldObj.provider.dimensionId,
                                getDescriptionPacket());
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
        BQEnergyExpansion.logToChat(String.format("Setting up Task - {[%s], [%s], [%s]}", owner, quest, task));
        if (owner == null || quest == null || task == null) {
            reset();
            return;
        }

        this.questID = QuestingAPI.getAPI(ApiReference.QUEST_DB).getID(quest);
        this.qCached = new DBEntry<>(questID, quest);
        this.taskID = quest.getTasks().getID(task);

        BQEnergyExpansion.logToChat(
                String.format("Setting up Task - {[%s], [%s], [%s]}", this.questID, this.qCached, this.taskID));

        if (task instanceof TaskEUCharge) {
            double req = ((TaskEUCharge) task).getRequiredEnergy();
            BQEnergyExpansion.logToChat(String.format("Setting Capacity to %s", req));
            this.setCapacity(req);
            this.setEnergyStored(0);
            this.setSinkTier(4);
            this.markDirty();

            MinecraftServer.getServer()
                    .getConfigurationManager()
                    .sendToAllNearExcept(
                            null, xCoord, yCoord, zCoord, 128, worldObj.provider.dimensionId, getDescriptionPacket());
        }

        if (this.questID < 0 || this.taskID < 0) {
            reset();
            return;
        }

        this.owner = owner;
        this.markDirty();
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
        super.injectEnergy(directionFrom, amount, voltage);

        if (!isSetup()
                || amount <= 0
                || QuestingAPI.getAPI(ApiReference.SETTINGS).getProperty(NativeProps.EDIT_MODE)) return 0;

        DBEntry<IQuest> quest = getQuest();
        IEUTask task = getEUTask();

        if (task != null) {
            if (task.canSubmitEnergy(quest, owner, amount, voltage)) {
                task.submitEnergy(quest, owner, amount, voltage);
            }

            if (task.isComplete(owner)) {
                needsUpdate = true;
                reset();
                MinecraftServer.getServer()
                        .getConfigurationManager()
                        .sendToAllNearExcept(
                                null,
                                xCoord,
                                yCoord,
                                zCoord,
                                128,
                                worldObj.provider.dimensionId,
                                getDescriptionPacket());
            } else {
                needsUpdate = true; // remainder != energy;
            }
        }

        return 0;
    }
    // </editor-fold>
}
