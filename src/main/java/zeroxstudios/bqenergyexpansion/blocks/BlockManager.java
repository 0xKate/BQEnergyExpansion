package zeroxstudios.bqenergyexpansion.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import zeroxstudios.bqenergyexpansion.core.Tags;

public class BlockManager {
    public static Block energy_submission_station;
    public static EnergyStationTileEnt energy_station_tile_ent;

    public static void init() {
        energy_submission_station = new EnergyStation();
        energy_station_tile_ent = new EnergyStationTileEnt();
        GameRegistry.registerBlock(energy_submission_station, energy_submission_station.getUnlocalizedName());
        GameRegistry.registerTileEntity(energy_station_tile_ent.getClass(), Tags.MODID + "energy_station_tile_ent");
    }
}
