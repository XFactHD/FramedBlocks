package xfacthd.framedblocks.common.data.camo;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.api.camo.*;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

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
    public CamoContainer.Factory getFactory()
    {
        return FBContent.FACTORY_BLOCK.get();
    }

    @Override
    public ContainerType getType()
    {
        return ContainerType.BLOCK;
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



    public static final class Factory extends CamoContainer.Factory
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
    }
}
