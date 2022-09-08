package zeroxstudios.bqenergyexpansion.core;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import betterquesting.api2.registry.IRegistry;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.nbt.NBTTagCompound;
import zeroxstudios.bqenergyexpansion.blocks.BlockManager;
import zeroxstudios.bqenergyexpansion.blocks.EnergySubmissionStation.gui.ESSGuiHandler;
import zeroxstudios.bqenergyexpansion.blocks.EnergySubmissionStation.network.ESSPacketHandler;
import zeroxstudios.bqenergyexpansion.tasks.factory.FactoryTaskEUCharge;
import zeroxstudios.bqenergyexpansion.waila.BQEnergyWailaModule;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items,
    // etc., and register them with the GameRegistry."
    public void preInit(FMLPreInitializationEvent event) {
        Config.syncronizeConfiguration(event.getSuggestedConfigurationFile());

        BQEnergyExpansion.info("Hello from " + Tags.MODNAME + " v" + Tags.VERSION);

        BlockManager.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(BQEnergyExpansion.instance, new ESSGuiHandler());
        ESSPacketHandler.registerHandler();
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes."
    public void init(FMLInitializationEvent event) {}

    // postInit "Handle interaction with other mods, complete your setup based on this."

    @SuppressWarnings("deprecation")
    public void postInit(FMLPostInitializationEvent event) {

        // Register Task to BetterQuesting
        IRegistry<IFactoryData<ITask, NBTTagCompound>, ITask> bqTaskRegistry =
                QuestingAPI.getAPI(ApiReference.TASK_REG);
        bqTaskRegistry.register(FactoryTaskEUCharge.INSTANCE);

        BQEnergyWailaModule.register();
    }

    public void serverAboutToStart(FMLServerAboutToStartEvent event) {}

    // register server commands in this event handler
    public void serverStarting(FMLServerStartingEvent event) {}

    public void serverStarted(FMLServerStartedEvent event) {}

    public void serverStopping(FMLServerStoppingEvent event) {}

    public void serverStopped(FMLServerStoppedEvent event) {}
}
