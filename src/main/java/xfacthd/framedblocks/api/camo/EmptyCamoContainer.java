package xfacthd.framedblocks.api.camo;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import xfacthd.framedblocks.api.FramedBlocksAPI;

public final class EmptyCamoContainer extends CamoContainer
{
    public static final EmptyCamoContainer EMPTY = new EmptyCamoContainer();

    private EmptyCamoContainer()
    {
        super(Blocks.AIR.defaultBlockState());
    }

    @Override
    public int getColor(BlockAndTintGetter level, BlockPos pos, int tintIdx)
    {
        return -1;
    }

    @Override
    public MapColor getMapColor(BlockGetter level, BlockPos pos)
    {
        return null;
    }

    @Override
    public ItemStack toItemStack(ItemStack stack)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    public ContainerType getType()
    {
        return ContainerType.EMPTY;
    }

    @Override
    public CamoContainer.Factory getFactory()
    {
        return FramedBlocksAPI.getInstance().emptyCamoContainerFactory();
    }

    @Override
    public void save(CompoundTag tag) { }

    @Override
    public void toNetwork(CompoundTag tag) { }



    public static final class Factory extends CamoContainer.Factory
    {
        @Override
        public CamoContainer fromNbt(CompoundTag tag)
        {
            return EMPTY;
        }

        @Override
        public CamoContainer fromNetwork(CompoundTag tag)
        {
            return EMPTY;
        }

        @Override
        public CamoContainer fromItem(ItemStack stack)
        {
            throw new UnsupportedOperationException("Empty camo container cannot be created from ItemStack");
        }
    }
}
