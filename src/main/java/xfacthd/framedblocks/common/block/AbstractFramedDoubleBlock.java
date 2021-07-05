package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractFramedDoubleBlock extends FramedBlock
{
    public AbstractFramedDoubleBlock(BlockType blockType) { super(blockType); }

    @Nonnull
    @Override
    public BlockState getFacade(@Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nullable Direction side, @Nonnull BlockPos connection)
    {
        BlockState state = world.getBlockState(pos);
        if (getCtmPredicate().test(state, side))
        {
            if (world.getBlockEntity(pos) instanceof FramedDoubleTileEntity te)
            {
                return te.getCamoState(side);
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSound(BlockState state, IWorldReader world, BlockPos pos)
    {
        if (world.getBlockEntity(pos) instanceof FramedDoubleTileEntity te)
        {
            BlockState camoState = te.getCamoStateTwo();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }

            camoState = te.getCamoState();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }
        }
        return getSoundType(state);
    }
}