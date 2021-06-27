package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.tileentity.*;
import xfacthd.framedblocks.common.util.CtmPredicate;
import xfacthd.framedblocks.common.util.Utils;

public class FramedDoubleCornerBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);

        if (type.isHorizontal())
        {
            if (side == null) { return false; }

            return  side == dir || side == dir.getOpposite() ||
                   (side == dir.rotateYCCW() && !type.isRight()) || (side == dir.rotateY() && type.isRight()) ||
                   (side == Direction.DOWN && !type.isTop()) || (side == Direction.UP && type.isTop());
        }
        else
        {
            return (side != null && side.getAxis() == Direction.Axis.Y) || side == dir || side == dir.rotateYCCW();
        }
    };

    public FramedDoubleCornerBlock() { super(BlockType.FRAMED_DOUBLE_CORNER); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.CORNER_TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState();

        Direction side = context.getFace();
        Vector3d hitPoint = Utils.fraction(context.getHitVec());
        if (side.getAxis() != Direction.Axis.Y)
        {
            if (hitPoint.getY() < (3D / 16D))
            {
                side = Direction.UP;
            }
            else if (hitPoint.getY() > (13D / 16D))
            {
                side = Direction.DOWN;
            }
        }

        Direction facing = context.getPlacementHorizontalFacing();
        state = state.with(PropertyHolder.FACING_HOR, facing);

        if (side == Direction.DOWN)
        {
            state = state.with(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        }
        else if (side == Direction.UP)
        {
            state = state.with(PropertyHolder.CORNER_TYPE, CornerType.BOTTOM);
        }
        else
        {
            boolean xAxis = context.getFace().getAxis() == Direction.Axis.X;
            boolean positive = context.getFace().rotateYCCW().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            double xz = xAxis ? hitPoint.getZ() : hitPoint.getX();
            double y = hitPoint.getY();

            CornerType type;
            if ((xz > .5D) == positive)
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_RIGHT : CornerType.HORIZONTAL_BOTTOM_RIGHT;
            }
            else
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_LEFT : CornerType.HORIZONTAL_BOTTOM_LEFT;
            }
            state = state.with(PropertyHolder.CORNER_TYPE, type);
        }

        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSound(BlockState state, IWorldReader world, BlockPos pos)
    {
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof FramedDoubleTileEntity)
        {
            FramedDoubleTileEntity dte = (FramedDoubleTileEntity) te;
            BlockState camoState = (type.isHorizontal() || type.isTop()) ? dte.getCamoState() : dte.getCamoStateTwo();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }

            camoState = (type.isHorizontal() || type.isTop()) ? dte.getCamoStateTwo() : dte.getCamoState();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }
        }
        return getSoundType(state);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedDoubleCornerTileEntity(); }
}