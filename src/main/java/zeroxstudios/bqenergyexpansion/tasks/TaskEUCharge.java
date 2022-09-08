/**
 * Portions of this file have been copied from BetterQuesting and is copyrighted by Funwayguy under the terms of the MIT license.
 */
package zeroxstudios.bqenergyexpansion.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import betterquesting.api2.utils.Tuple2;
import bq_standard.tasks.base.TaskProgressableBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import zeroxstudios.bqenergyexpansion.tasks.factory.FactoryTaskEUCharge;
import zeroxstudios.bqenergyexpansion.tasks.gui.PanelTaskEUCharge;

public class TaskEUCharge extends TaskProgressableBase<Double> implements ITask, IEUTask {
    public double requiredEnergy = 0;
    public String name = "bqenergy.tasks.eu.charge";

    @Override
    public String getUnlocalisedName() {
        return name;
    }

    @Override
    public ResourceLocation getFactoryID() {
        return FactoryTaskEUCharge.INSTANCE.getRegistryName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
        return new PanelTaskEUCharge(rect, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
        return null;
    }

    /**
     * Returns the amount of energy needed to complete the task
     */
    public double getRequiredEnergy() {
        return this.requiredEnergy;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tags) {
        tags.setDouble("requiredEnergy", getRequiredEnergy());
        return tags;
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        this.requiredEnergy = tags.getDouble("requiredEnergy");
    }

    @Override
    public Double getUsersProgress(UUID uuid) {
        Double n = userProgress.get(uuid);
        return n == null ? 0 : n;
    }

    @Override
    public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest) {
        final List<Tuple2<UUID, Double>> bulkProgress = getBulkProgress(pInfo.ALL_UUIDS);

        bulkProgress.forEach((value) -> {
            Double progress = value.getSecond();
            UUID player = value.getFirst();
            if (progress > getRequiredEnergy()) {
                setUserProgress(player, getRequiredEnergy());
                this.setComplete(player);
            }
        });

        pInfo.markDirtyParty(Collections.singletonList(quest.getID()));
    }

    @Override
    public Double readUserProgressFromNBT(NBTTagCompound nbt) {
        return nbt.getDouble("value");
    }

    @Override
    public void writeUserProgressToNBT(NBTTagCompound nbt, Double progress) {
        nbt.setDouble("value", progress);
    }

    @Override
    public void submitEnergy(DBEntry<IQuest> quest, UUID owner, double amount, double voltage) {
        Double progress = getUsersProgress(owner);

        if (progress + amount < getRequiredEnergy()) {
            setUserProgress(owner, progress + amount);
        } else {
            setUserProgress(owner, getRequiredEnergy());
            this.setComplete(owner);
        }
    }

    /**
     * Checks if the task accepts raw EU energy
     *
     * @param quest
     * @param owner
     * @param amount
     * @param voltage
     */
    @Override
    public boolean canSubmitEnergy(DBEntry<IQuest> quest, UUID owner, double amount, double voltage) {
        return getUsersProgress(owner) < getRequiredEnergy();
    }
}
