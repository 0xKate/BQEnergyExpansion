package zeroxstudios.bqenergyexpansion.waila;

import java.lang.reflect.Field;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import zeroxstudios.bqenergyexpansion.core.BQEnergyExpansion;
import zeroxstudios.bqenergyexpansion.core.Tags;

public class BQEnergyWailaModule {

    public static Class TileEnergyStation = null;
    public static Field TileEnergyStation_storage = null;
    public static Field TileEnergyStation_maxStorage = null;

    public static void register() {
        // XXX : We register the Energy interface first
        try {
            TileEnergyStation =
                    Class.forName("zeroxstudios.bqenergyexpansion.blocks.EnergySubmissionStation.ESSTileEntityEU");
            TileEnergyStation_storage = TileEnergyStation.getDeclaredField("internalEUStorage");
            TileEnergyStation_maxStorage = TileEnergyStation.getDeclaredField("internalEUMax");

            ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerESS(), TileEnergyStation);
            ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerESS(), TileEnergyStation);

            ModuleRegistrar.instance().addConfigRemote(Tags.MODNAME, Tags.MODID + ".shown");

        } catch (Exception e) {
            BQEnergyExpansion.error("Error while loading Waila hooks." + e);
        }
    }
}
