package zeroxstudios.bqenergyexpansion.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zeroxstudios.bqenergyexpansion.blocks.EnergyStationContainer;
import zeroxstudios.bqenergyexpansion.blocks.EnergyStationTileEnt;
import zeroxstudios.bqenergyexpansion.core.Tags;

public class EnergyStationUIContainer extends GuiContainer {
    private final ResourceLocation gui =
            new ResourceLocation(Tags.MODID, "textures/gui/container/energy_station_ui.png");
    private final InventoryPlayer inventory;
    private final EnergyStationTileEnt tile;

    public EnergyStationUIContainer(EnergyStationTileEnt tileEnt, EntityPlayer player) {
        super(new EnergyStationContainer(tileEnt, player));
        inventory = player.inventory;
        this.tile = tileEnt;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        Minecraft.getMinecraft().renderEngine.bindTexture(gui);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRendererObj.drawString(
                I18n.format(tile.getInventoryName()),
                (xSize / 2) - (fontRendererObj.getStringWidth(I18n.format(tile.getInventoryName())) / 2),
                6,
                4210752,
                false);
        fontRendererObj.drawString(I18n.format(inventory.getInventoryName()), 8, ySize - 96 + 2, 4210752);
    }
}
