package zeroxstudios.bqenergyexpansion.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class TaskEUCharge implements ITask, IEUTask {
    /**
     * Submits raw RF energy to the task and returns any left over
     *
     * @param quest
     * @param owner
     * @param amount
     */
    @Override
    public int submitEnergy(DBEntry<IQuest> quest, UUID owner, int amount) {
        return 0;
    }

    @Override
    public String getUnlocalisedName() {
        return null;
    }

    @Override
    public ResourceLocation getFactoryID() {
        return null;
    }

    @Override
    public void detect(ParticipantInfo participant, DBEntry<IQuest> quest) {}

    @Override
    public boolean isComplete(UUID uuid) {
        return false;
    }

    @Override
    public void setComplete(UUID uuid) {}

    @Override
    public void resetUser(@Nullable UUID uuid) {}

    @Nullable
    @Override
    public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
        return null;
    }

    @Nullable
    @Override
    public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
        return null;
    }

    /**
     * Tasks that set this to true will be ignored by quest completion logic.
     *
     * @param uuid
     */
    @Override
    public boolean ignored(UUID uuid) {
        return IEUTask.super.ignored(uuid);
    }

    @Override
    public List<String> getTextsForSearch() {
        return IEUTask.super.getTextsForSearch();
    }

    /**
     * If users is not null, only the progress for the users in the list will be written to the NBT
     *
     * @param nbt
     * @param users
     */
    @Override
    public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<UUID> users) {
        return null;
    }

    /**
     * if merge is true, the progress for some users will be merged with the existing progress, otherwise it will be overwritten
     *
     * @param nbt
     * @param merge
     */
    @Override
    public void readProgressFromNBT(NBTTagCompound nbt, boolean merge) {}

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {}
}
