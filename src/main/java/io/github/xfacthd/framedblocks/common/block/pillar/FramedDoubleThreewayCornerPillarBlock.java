package io.github.xfacthd.framedblocks.common.block.pillar;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedDoubleThreewayCornerPillarBlock extends FramedDoubleBlock
{
    public FramedDoubleThreewayCornerPillarBlock(Properties props)
    {
        super(BlockType.FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR, props);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withHalfFacing()
                .withTop()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        return switch (mode)
        {
            case PRIMARY -> super.rotate(state, direction, mode);
            case SECONDARY -> state.cycle(FramedProperties.TOP);
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return BlockUtils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state)
    {
        BlockState partState = FBContent.BLOCK_FRAMED_THREEWAY_CORNER_PILLAR.value().defaultBlockState();
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        return new DoubleBlockParts(
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

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return state.setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}
