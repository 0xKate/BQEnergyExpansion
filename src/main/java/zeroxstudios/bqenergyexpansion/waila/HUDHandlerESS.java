package zeroxstudios.bqenergyexpansion.waila;

import static mcp.mobius.waila.api.SpecialChars.ALIGNRIGHT;
import static mcp.mobius.waila.api.SpecialChars.TAB;
import static zeroxstudios.bqenergyexpansion.blocks.base.TileEntityQuest.getPlayerByUUID;

import java.util.List;
import java.util.UUID;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.entity.player.EntityPlayer;
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
            String owner = accessor.getNBTData().getString("owner");
            // int questID = accessor.getNBTData().getInteger("questID");
            // int taskID = accessor.getNBTData().getInteger("taskID");
            // IQuest q;
            // ITask t;

            // Storage
            String EUStorageLocalized = LangUtil.translateG("bqenergy.waila.eu.storage");
            if (ConfigHandler.instance().getConfig(Tags.MODID + ".shown")) {
                // if (maxStorage > 0)
                currenttip.add(String.format(
                        "%s:%s\u00a7f%d\u00a7r / \u00a7f%d\u00a7r EU",
                        EUStorageLocalized, TAB + ALIGNRIGHT, Math.round(Math.min(storage, maxStorage)), (long)
                                maxStorage));
            }

            // Owner
            String ownerLabel = LangUtil.translateG("bqenergy.waila.owner");
            if (ConfigHandler.instance().getConfig(Tags.MODID + ".shown")) {
                currenttip.add(String.format("%s:%s\u00a7f%s\u00a7r", ownerLabel, TAB, owner));
            }

            /*
            if (questID >= 0 && taskID >= 0) {
                // Quest
                q = QuestingAPI.getAPI(ApiReference.QUEST_DB).getValue(questID);
                String questLabel = LangUtil.translateG("bqenergy.waila.quest");
                String questName = LangUtil.translateG(q.getProperty(NativeProps.NAME));
                if (ConfigHandler.instance().getConfig(Tags.MODID + ".shown")) {
                    currenttip.add(String.format("%s:%s\u00a7f%s\u00a7r", questLabel, TAB, questName));
                }

                // Task
                t = q.getTasks().getValue(taskID);
                String taskLabel = LangUtil.translateG("bqenergy.waila.task");
                String taskName = LangUtil.translateG(t.getUnlocalisedName());
                if (ConfigHandler.instance().getConfig(Tags.MODID + ".shown")) {
                    currenttip.add(String.format("%s:%s\u00a7f%s\u00a7r", taskLabel, TAB, taskName));
                }
            }
             */

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
            UUID owner = null;
            // int questID = -1;
            // int taskID = -1;

            if (BQEnergyWailaModule.RefESSTileEntity.isInstance(te)) {
                storage = BQEnergyWailaModule.RefESSTileEntity_storage.getDouble(te);
                maxStorage = BQEnergyWailaModule.RefESSTileEntity_maxStorage.getDouble(te);
                owner = (UUID) BQEnergyWailaModule.RefESSTileEntity_owner.get(te);
                // questID = BQEnergyWailaModule.RefTileEntityQuest_questID.getInt(te);
                // taskID = BQEnergyWailaModule.RefTileEntityQuest_questID.getInt(te);

                tag.setDouble("internalEUStorage", storage);
                tag.setDouble("internalEUMax", maxStorage);

                EntityPlayer owner_ply = getPlayerByUUID(owner);
                if (owner_ply != null) {
                    tag.setString("owner", owner_ply.getDisplayName());
                } else {
                    tag.setString("owner", "N/A");
                }

                // tag.setInteger("questID", questID);
                // tag.setInteger("taskID", taskID);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tag;
    }
}
