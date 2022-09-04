package zeroxstudios.bqenergyexpansion.core;

import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import zeroxstudios.bqenergyexpansion.blocks.BlockManager;
import zeroxstudios.bqenergyexpansion.client.gui.EnergyStationUIHandler;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items,
    // etc., and register them with the GameRegistry."
    public void preInit(FMLPreInitializationEvent event) {
        Config.syncronizeConfiguration(event.getSuggestedConfigurationFile());
        BQEnergyExpansion.info(Config.greeting);
        BQEnergyExpansion.info(
                "I am " + Tags.MODNAME + " at version " + Tags.VERSION + " and group name " + Tags.GROUPNAME);

        BlockManager.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(BQEnergyExpansion.instance, new EnergyStationUIHandler());
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes."
    public void init(FMLInitializationEvent event) {}

    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void postInit(FMLPostInitializationEvent event) {}

    public void serverAboutToStart(FMLServerAboutToStartEvent event) {}

    // register server commands in this event handler
    public void serverStarting(FMLServerStartingEvent event) {}

    public void serverStarted(FMLServerStartedEvent event) {}

    public void serverStopping(FMLServerStoppingEvent event) {}

    public void serverStopped(FMLServerStoppedEvent event) {}
}
