/**
 * Portions of this file have been copied from BetterQuesting and is copyrighted by Funwayguy under the terms of the MIT license.
 */
package zeroxstudios.bqenergyexpansion.tasks.gui;

import static ic2.core.Ic2Items.generator;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.controls.IValueIO;
import betterquesting.api2.client.gui.controls.io.FloatSimpleIO;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.bars.PanelHBarFill;
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.textures.ItemTexture;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.util.vector.Vector4f;
import zeroxstudios.bqenergyexpansion.tasks.TaskEUCharge;

public class PanelTaskEUCharge extends CanvasEmpty {
    private final TaskEUCharge task;
    private PanelTextBox txtValue;
    private IValueIO<Float> barValue;
    private UUID uuid;

    private boolean flipFlop = false;

    public PanelTaskEUCharge(IGuiRect rect, TaskEUCharge task) {
        super(rect);
        this.task = task;
    }

    @Override
    public void drawPanel(int mx, int my, float partialTick) {
        if ((System.currentTimeMillis() / 500L) % 2 == 0 != flipFlop) {
            flipFlop = !flipFlop;

            Double progress = task.getUsersProgress(uuid);

            String strReq = String.valueOf(task.getRequiredEnergy());
            String strPrg = progress.toString();

            final float percent = (float) (progress / task.getRequiredEnergy());

            txtValue.setText(EnumChatFormatting.BOLD + strPrg + " / " + strReq + " EU");
            barValue.writeValue(percent);
        }

        super.drawPanel(mx, my, partialTick);
    }

    @Override
    public void initPanel() {
        super.initPanel();

        Minecraft mc = Minecraft.getMinecraft();
        uuid = QuestingAPI.getQuestingUUID(mc.thePlayer);

        this.addPanel(new PanelGeneric(
                new GuiTransform(GuiAlign.MID_CENTER, -16, -32, 32, 32, 0),
                new ItemTexture(new BigItemStack(generator))));

        Double progress = task.getUsersProgress(uuid);

        String strReq = String.valueOf(task.getRequiredEnergy());
        String strPrg = progress.toString();

        final float percent = (float) (progress / task.getRequiredEnergy());

        PanelHBarFill fillBar = new PanelHBarFill(
                new GuiTransform(new Vector4f(0.25F, 0.5F, 0.75F, 0.5F), new GuiPadding(0, 0, 0, -16), 0));
        fillBar.setFillColor(new GuiColorStatic(0xFFFF0000));
        barValue = new FloatSimpleIO(percent, 0F, 1F).setLerp(true, 0.01F);
        fillBar.setFillDriver(barValue);
        this.addPanel(fillBar);

        txtValue = new PanelTextBox(
                        new GuiTransform(new Vector4f(0.25F, 0.5F, 0.75F, 0.5F), new GuiPadding(0, 4, 0, -16), -1),
                        EnumChatFormatting.BOLD + strPrg + " / " + strReq + " EU")
                .setAlignment(1);
        this.addPanel(txtValue);
    }
}
