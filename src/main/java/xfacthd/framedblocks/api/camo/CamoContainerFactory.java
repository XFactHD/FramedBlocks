package xfacthd.framedblocks.api.camo;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.util.Utils;

import java.util.function.Consumer;

public abstract class CamoContainerFactory
{
    private String id;
    private int syncId = -1;

    public final String getId()
    {
        if (id == null)
        {
            //noinspection ConstantConditions
            id = FramedBlocksAPI.INSTANCE.getCamoContainerFactoryRegistry().getKey(this).toString();
        }
        return id;
    }

    public final int getSyncId()
    {
        if (syncId == -1)
        {
            syncId = FramedBlocksAPI.INSTANCE.getCamoContainerFactoryRegistry().getId(this);
            Preconditions.checkState(syncId != -1, "Attempted to get sync ID for unregistered CamoContainer.Factory");
        }
        return syncId;
    }

    /**
     * Reconstruct the {@link CamoContainer} from the given {@link CompoundTag}
     */
    public abstract CamoContainer fromNbt(CompoundTag tag);

    /**
     * Reconstruct the {@link CamoContainer} from the given {@link CompoundTag}
     */
    public abstract CamoContainer fromNetwork(CompoundTag tag);

    /**
     * Construct a {@link CamoContainer} from the given {@link ItemStack}
     *
     * @return A new CamoContainer or {@link EmptyCamoContainer#EMPTY}
     */
    public abstract CamoContainer fromItem(ItemStack stack);

    /**
     * Called at startup to capture all items for which this factory should be used when applying them as a camo
     */
    public abstract void registerTriggerItems(Consumer<Item> registrar);
}
