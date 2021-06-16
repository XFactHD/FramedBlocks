package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.tileentity.TileEntity;
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
    public AbstractFramedDoubleBlock(String name, BlockType blockType) { super(name, blockType); }

    @Nonnull
    @Override
    public BlockState getFacade(@Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nullable Direction side, @Nonnull BlockPos connection)
    {
        BlockState state = world.getBlockState(pos);
        if (getCtmPredicate().test(state, side))
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof FramedDoubleTileEntity)
            {
                return ((FramedDoubleTileEntity) te).getCamoState(side);
            }
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSound(BlockState state, IWorldReader world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof FramedDoubleTileEntity)
        {
            BlockState camoState = ((FramedDoubleTileEntity) te).getCamoStateTwo();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }

            camoState = ((FramedDoubleTileEntity) te).getCamoState();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }
        }
        return getSoundType(state);
    }
}