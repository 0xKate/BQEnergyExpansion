package zeroxstudios.bqenergyexpansion.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import zeroxstudios.bqenergyexpansion.blocks.EnergySubmissionStation.ESSBlock;
import zeroxstudios.bqenergyexpansion.blocks.EnergySubmissionStation.ESSTileEntity;
import zeroxstudios.bqenergyexpansion.core.Tags;

public class BlockManager {
    public static ESSBlock energy_submission_station;
    public static ESSTileEntity energy_station_tile_ent;

    public static void init() {
        energy_submission_station = new ESSBlock();
        energy_station_tile_ent = new ESSTileEntity();
        GameRegistry.registerBlock(energy_submission_station, energy_submission_station.getName());
        GameRegistry.registerTileEntity(energy_station_tile_ent.getClass(), Tags.MODID + "energy_station_tile_ent");
    }
}
