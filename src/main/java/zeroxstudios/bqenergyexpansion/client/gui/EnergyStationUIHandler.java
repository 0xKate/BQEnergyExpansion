package zeroxstudios.bqenergyexpansion.client.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import zeroxstudios.bqenergyexpansion.blocks.EnergyStationContainer;
import zeroxstudios.bqenergyexpansion.blocks.EnergyStationTileEnt;

public class EnergyStationUIHandler implements IGuiHandler {
    /**
     * Returns a Server side Container to be displayed to the user.
     *
     * @param ID     The Gui ID Number
     * @param player The player viewing the Gui
     * @param world  The current world
     * @param x      X Position
     * @param y      Y Position
     * @param z      Z Position
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEnt = world.getTileEntity(x, y, z);

        if (tileEnt instanceof EnergyStationTileEnt) {
            if (ID == 0) // Gui ID for storage block, will add later
            {
                return new EnergyStationContainer((EnergyStationTileEnt) tileEnt, player);
            }
        }
        return null;
    }

    /**
     * Returns a Container to be displayed to the user. On the client side, this
     * needs to return a instance of GuiScreen On the server side, this needs to
     * return a instance of Container
     *
     * @param ID     The Gui ID Number
     * @param player The player viewing the Gui
     * @param world  The current world
     * @param x      X Position
     * @param y      Y Position
     * @param z      Z Position
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEnt = world.getTileEntity(x, y, z);

        if (tileEnt instanceof EnergyStationTileEnt) {
            if (ID == 0) // Gui ID for storage block, will add later
            {
                return new EnergyStationUIContainer((EnergyStationTileEnt) tileEnt, player);
            }
        }
        return null;
    }
}
