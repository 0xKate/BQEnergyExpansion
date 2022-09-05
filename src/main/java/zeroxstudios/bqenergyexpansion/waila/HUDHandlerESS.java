package zeroxstudios.bqenergyexpansion.waila;

import static mcp.mobius.waila.api.SpecialChars.*;

import java.util.List;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import zeroxstudios.bqenergyexpansion.core.Tags;

public class HUDHandlerESS implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(
            ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(
            ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        try {
            double storage = accessor.getNBTData().getDouble("internalEUStorage");
            double maxStorage = accessor.getNBTData().getDouble("internalEUMax");
            String questName = "N/A";

            // Storage
            String EUStorageLocalized = LangUtil.translateG("bqenergy.eu.storage");
            // Quest Name
            String QuestLabelLocalized = LangUtil.translateG("bqenergy.questlabel");

            if (ConfigHandler.instance().getConfig(Tags.MODID + ".shown")) {
                if (maxStorage > 0)
                    currenttip.add(String.format(
                            "%s:%s\u00a7f%d\u00a7r / \u00a7f%d\u00a7r EU",
                            EUStorageLocalized, TAB + ALIGNRIGHT, Math.round(Math.min(storage, maxStorage)), (long)
                                    maxStorage));
            }

            if (ConfigHandler.instance().getConfig(Tags.MODID + ".shown")) {
                currenttip.add(String.format("%s:%s\u00a7f%s\u00a7r", QuestLabelLocalized, TAB, questName));
            }

        } catch (Exception e) {
            currenttip = WailaExceptionHandler.handleErr(
                    e, accessor.getTileEntity().getClass().getName(), currenttip);
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(
            ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(
            EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {

        try {
            double storage = -1;
            double maxStorage = -1;

            if (BQEnergyWailaModule.TileEnergyStation.isInstance(te)) {
                storage = BQEnergyWailaModule.TileEnergyStation_storage.getDouble(te);
                maxStorage = BQEnergyWailaModule.TileEnergyStation_maxStorage.getDouble(te);
            }

            tag.setDouble("internalEUStorage", storage);
            tag.setDouble("internalEUMax", maxStorage);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tag;
    }
}
