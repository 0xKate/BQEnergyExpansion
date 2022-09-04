package zeroxstudios.bqenergyexpansion.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class BlockManager {
    public static Block energy_submission_station;

    public static void init() {
        energy_submission_station = new EnergySubmissionStation();
        GameRegistry.registerBlock(energy_submission_station, energy_submission_station.getUnlocalizedName());
    }
}
