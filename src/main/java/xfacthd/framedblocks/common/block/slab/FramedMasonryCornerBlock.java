package xfacthd.framedblocks.common.block.slab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.doubleblock.*;

public class FramedMasonryCornerBlock extends AbstractFramedDoubleBlock
{
    public FramedMasonryCornerBlock()
    {
        super(BlockType.FRAMED_MASONRY_CORNER);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withHorizontalFacing()
                .withTop()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (!Utils.isY(face) && rot != Rotation.NONE)
        {
            return state.cycle(FramedProperties.TOP);
        }
        return super.rotate(state, face, rot);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleBlockEntity(pos, state);
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        BlockState edgeState = FBContent.BLOCK_FRAMED_MASONRY_CORNER_SEGMENT.value()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, state.getValue(FramedProperties.TOP));
        return new Tuple<>(
                edgeState.setValue(FramedProperties.FACING_HOR, dir),
                edgeState.setValue(FramedProperties.FACING_HOR, dir.getOpposite())
        );
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        return SolidityCheck.BOTH;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction bottom = top ? Direction.UP : Direction.DOWN;
        if (side == bottom)
        {
            if (edge == dir)
            {
                return CamoGetter.SECOND;
            }
            if (edge == dir.getOpposite())
            {
                return CamoGetter.FIRST;
            }
        }
        else if (side == bottom.getOpposite())
        {
            if (edge == dir.getClockWise())
            {
                return CamoGetter.FIRST;
            }
            if (edge == dir.getCounterClockWise())
            {
                return CamoGetter.SECOND;
            }
        }
        else if (side.getAxis() == dir.getAxis())
        {
            if (edge == bottom || edge == side.getCounterClockWise())
            {
                return side == dir ? CamoGetter.SECOND : CamoGetter.FIRST;
            }
        }
        else if (side.getAxis() == dir.getClockWise().getAxis())
        {
            if (edge == bottom.getOpposite() || edge == side.getClockWise())
            {
                return side == dir.getClockWise() ? CamoGetter.FIRST : CamoGetter.SECOND;
            }
        }
        return CamoGetter.NONE;
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }
}
