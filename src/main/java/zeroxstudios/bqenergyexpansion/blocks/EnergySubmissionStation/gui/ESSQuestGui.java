package zeroxstudios.bqenergyexpansion.blocks.EnergySubmissionStation.gui;

import betterquesting.api.client.gui.misc.INeedsRefresh;
import betterquesting.api2.client.gui.GuiContainerCanvas;
import betterquesting.api2.client.gui.events.IPEventListener;
import betterquesting.api2.client.gui.events.PanelEvent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

public class ESSQuestGui extends GuiContainerCanvas implements IPEventListener, INeedsRefresh {
    public ESSQuestGui(GuiScreen parent, Container container) {
        super(parent, container);
    }

    @Override
    public void refreshGui() {}

    @Override
    public void onPanelEvent(PanelEvent event) {}
}
