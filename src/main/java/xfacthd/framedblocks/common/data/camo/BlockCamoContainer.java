package xfacthd.framedblocks.common.data.camo;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.api.camo.*;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

import java.util.function.Consumer;

public class BlockCamoContainer extends CamoContainer
{
    private BlockCamoContainer(BlockState state)
    {
        super(state);
    }

    @Override
    public int getColor(BlockAndTintGetter level, BlockPos pos, int tintIdx)
    {
        if (FMLEnvironment.dist.isClient())
        {
            return ClientUtils.getBlockColor(level, pos, state, tintIdx);
        }
        throw new UnsupportedOperationException("Block color is not available on the server!");
    }

    @Override
    public ItemStack toItemStack(ItemStack stack)
    {
        return new ItemStack(state.getBlock());
    }

    @Override
    public boolean equals(Object o)
    {
        return this == o || (o != null && getClass() == o.getClass() && state == ((CamoContainer) o).getState());
    }

    @Override
    public int hashCode()
    {
        return state.hashCode();
    }

    @Override
    public CamoContainerFactory getFactory()
    {
        return FBContent.FACTORY_BLOCK.value();
    }

    @Override
    public CamoContainerType getType()
    {
        return CamoContainerType.BLOCK;
    }

    @Override
    public void save(CompoundTag tag)
    {
        tag.put("state", NbtUtils.writeBlockState(state));
    }

    @Override
    public void toNetwork(CompoundTag tag)
    {
        tag.putInt("state", Block.getId(state));
    }



    public static final class Factory extends CamoContainerFactory
    {
        @Override
        public CamoContainer fromNbt(CompoundTag tag)
        {
            BlockState state = NbtUtils.readBlockState(Utils.getBlockHolderLookup(null), tag.getCompound("state"));
            return new BlockCamoContainer(state);
        }

        @Override
        public CamoContainer fromNetwork(CompoundTag tag)
        {
            BlockState state = Block.stateById(tag.getInt("state"));
            return new BlockCamoContainer(state);
        }

        @Override
        public CamoContainer fromItem(ItemStack stack)
        {
            if (stack.getItem() instanceof BlockItem item)
            {
                BlockState state = item.getBlock().defaultBlockState();
                return new BlockCamoContainer(state);
            }
            return EmptyCamoContainer.EMPTY;
        }

        @Override
        public void registerTriggerItems(Consumer<Item> registrar) { }
    }
}
