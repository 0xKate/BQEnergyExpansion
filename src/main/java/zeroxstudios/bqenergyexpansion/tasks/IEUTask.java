package zeroxstudios.bqenergyexpansion.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.storage.DBEntry;
import java.util.UUID;

public interface IEUTask extends ITask {
    /**
     * Submits raw RF energy to the task and returns any left over
     */
    int submitEnergy(DBEntry<IQuest> quest, UUID owner, int amount);
}
