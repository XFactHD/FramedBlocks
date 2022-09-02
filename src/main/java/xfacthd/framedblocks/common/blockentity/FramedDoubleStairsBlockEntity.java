package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDoubleStairsBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleStairsBlockEntity(BlockPos worldPosition, BlockState blockState)
    {
        super(FBContent.blockEntityTypeFramedDoubleStairs.get(), worldPosition, blockState);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction side = hit.getDirection();

        if (side == facing || (top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            return false;
        }

        Vec3 vec = Utils.fraction(hit.getLocation());

        if (side == facing.getOpposite())
        {
            return (vec.y > .5) ^ top;
        }
        else if ((!top && side == Direction.UP) || (top && side == Direction.DOWN))
        {
            double xz = Utils.isX(facing) ? vec.x : vec.z;
            boolean positive = Utils.isPositive(facing);
            return xz > .5 != positive;
        }
        else
        {
            if (vec.y > .5 == top)
            {
                return false;
            }
            double xz = Utils.isX(facing) ? vec.x : vec.z;
            boolean positive = Utils.isPositive(facing);
            return xz > .5 != positive;
        }
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        return top ? DoubleSoundMode.FIRST : DoubleSoundMode.EITHER;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        if (side == facing || (top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            return getCamoState();
        }
        else if (side == facing.getOpposite() || (!top && side == Direction.UP) || (top && side == Direction.DOWN))
        {
            return getCamoStateTwo();
        }

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        if (side == facing || (top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            //noinspection ConstantConditions
            return getCamoState().isSolidRender(level, worldPosition);
        }

        return false;
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        return getBlockPair(state.getValue(FramedProperties.FACING_HOR), state.getValue(FramedProperties.TOP));
    }

    public static Tuple<BlockState, BlockState> getBlockPair(Direction facing, boolean top)
    {
        return new Tuple<>(
                FBContent.blockFramedStairs.get()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                        .setValue(BlockStateProperties.HALF, top ? Half.TOP : Half.BOTTOM),
                FBContent.blockFramedSlabEdge.get()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.TOP, !top)
        );
    }
}
