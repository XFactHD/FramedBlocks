package xfacthd.framedblocks.common.block.pillar;

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
import xfacthd.framedblocks.common.blockentity.doubled.pillar.FramedDoubleThreewayCornerPillarBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.doubleblock.*;

public class FramedDoubleThreewayCornerPillarBlock extends AbstractFramedDoubleBlock
{
    public FramedDoubleThreewayCornerPillarBlock()
    {
        super(BlockType.FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR);
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
                .withHalfFacing()
                .withTop()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (Utils.isY(face))
        {
            Direction dir = rot.rotate(state.getValue(FramedProperties.FACING_HOR));
            return state.setValue(FramedProperties.FACING_HOR, dir);
        }
        else if (rot != Rotation.NONE)
        {
            return state.cycle(FramedProperties.TOP);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleThreewayCornerPillarBlockEntity(pos, state);
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        BlockState partState = FBContent.BLOCK_FRAMED_THREEWAY_CORNER_PILLAR.value().defaultBlockState();
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        return new Tuple<>(
                partState.setValue(FramedProperties.FACING_HOR, dir)
                        .setValue(FramedProperties.TOP, top),
                partState.setValue(FramedProperties.FACING_HOR, dir.getOpposite())
                        .setValue(FramedProperties.TOP, !top)
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
        if (edge == null)
        {
            return CamoGetter.NONE;
        }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;
        if (side == dirTwo && (edge == dir || edge == dir.getCounterClockWise()))
        {
            return CamoGetter.FIRST;
        }
        else if (side == dirTwo.getOpposite() && (edge == dir.getOpposite() || edge == dir.getClockWise()))
        {
            return CamoGetter.SECOND;
        }
        else if (side == dir && (edge == dir.getCounterClockWise() || edge == dirTwo))
        {
            return CamoGetter.FIRST;
        }
        else if (side == dir.getCounterClockWise() && (edge == dir || edge == dirTwo))
        {
            return CamoGetter.FIRST;
        }
        else if (side == dir.getOpposite() && (edge == dir.getClockWise() || edge == dirTwo.getOpposite()))
        {
            return CamoGetter.SECOND;
        }
        else if (side == dir.getClockWise() && (edge == dir.getOpposite() || edge == dirTwo.getOpposite()))
        {
            return CamoGetter.SECOND;
        }
        return CamoGetter.NONE;
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}
