package zeroxstudios.bqenergyexpansion.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.storage.DBEntry;
import java.util.UUID;

public interface IEUTask extends ITask {
    /**
     * Submits EU energy to the task
     */
    int submitEnergy(DBEntry<IQuest> quest, UUID owner, double amount, double voltage);

    /**
     * Checks if you can submit EU energy to the task
     */
    boolean canSubmitEnergy(DBEntry<IQuest> quest, UUID owner, double amount, double voltage);
}
