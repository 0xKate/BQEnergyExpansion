package zeroxstudios.bqenergyexpansion.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zeroxstudios.bqenergyexpansion.blocks.BlockManager;

public class CreativeTab extends CreativeTabs {

    public CreativeTab() {
        super("BQEnergyExpansion");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return Item.getItemFromBlock(BlockManager.energy_submission_station);
    }

    @Override
    public void displayAllReleventItems(List items) {
        super.displayAllReleventItems(items);
        items.add(new ItemStack(Item.getItemFromBlock(BlockManager.energy_submission_station)));
    }
}
