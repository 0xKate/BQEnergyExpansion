package zeroxstudios.bqenergyexpansion.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import zeroxstudios.bqenergyexpansion.core.Tags;
import zeroxstudios.bqenergyexpansion.tasks.TaskEUCharge;

@SuppressWarnings("deprecation")
public class FactoryTaskEUCharge implements IFactoryData<ITask, NBTTagCompound> {

    public static final FactoryTaskEUCharge INSTANCE = new FactoryTaskEUCharge();

    @Override
    public ITask loadFromData(NBTTagCompound tags) {
        TaskEUCharge task = new TaskEUCharge();
        task.readFromNBT(tags);
        return task;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(Tags.MODID + ":eucharge");
    }

    @Override
    public ITask createNew() {
        return new TaskEUCharge();
    }
}
