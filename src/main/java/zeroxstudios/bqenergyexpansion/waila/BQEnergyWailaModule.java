package zeroxstudios.bqenergyexpansion.waila;

import java.lang.reflect.Field;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import zeroxstudios.bqenergyexpansion.blocks.EnergySubmissionStation.ESSTileEntity;
import zeroxstudios.bqenergyexpansion.core.BQEnergyExpansion;
import zeroxstudios.bqenergyexpansion.core.Tags;

public class BQEnergyWailaModule {
    public static Class RefESSTileEntity = null;
    public static Field RefESSTileEntity_storage = null;
    public static Field RefESSTileEntity_maxStorage = null;
    public static Field RefESSTileEntity_owner = null;
    public static Field RefESSTileEntity_questID = null;
    public static Field RefESSTileEntity_taskID = null;

    public static void register() {
        try {
            BQEnergyExpansion.info("Loading waila integration...");

            // RefParent = Class.forName("zeroxstudios.bqenergyexpansion.blocks.EnergySubmissionStation.ESSTileEntity");

            RefESSTileEntity = ESSTileEntity.class;
            RefESSTileEntity_storage =
                    RefESSTileEntity.getSuperclass().getSuperclass().getDeclaredField("internalEUStorage");
            RefESSTileEntity_maxStorage =
                    RefESSTileEntity.getSuperclass().getSuperclass().getDeclaredField("internalEUMax");
            RefESSTileEntity_owner = RefESSTileEntity.getSuperclass().getDeclaredField("owner");
            RefESSTileEntity_questID = RefESSTileEntity.getSuperclass().getDeclaredField("questID");
            RefESSTileEntity_taskID = RefESSTileEntity.getSuperclass().getDeclaredField("taskID");

            ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerESS(), RefESSTileEntity);
            ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerESS(), RefESSTileEntity);

            ModuleRegistrar.instance().addConfigRemote(Tags.MODNAME, Tags.MODID + ".shown");

        } catch (Exception e) {
            BQEnergyExpansion.error("Error while loading Waila integration." + e);
        }
    }
}
