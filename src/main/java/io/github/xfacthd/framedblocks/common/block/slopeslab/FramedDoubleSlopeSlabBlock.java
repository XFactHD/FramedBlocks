package io.github.xfacthd.framedblocks.common.block.slopeslab;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.block.render.NullCullPredicate;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slopeslab.FramedDoubleSlopeSlabBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedDoubleSlopeSlabBlock extends FramedDoubleBlock implements SlopeToggleBlock
{
    public static final NullCullPredicate NULL_CULL_PREDICATE = new NullCullPredicate(
            state -> !state.getValue(PropertyHolder.TOP_HALF),
            state -> state.getValue(PropertyHolder.TOP_HALF)
    );

    public FramedDoubleSlopeSlabBlock(Properties props)
    {
        super(BlockType.FRAMED_DOUBLE_SLOPE_SLAB, props);
        registerDefaultState(defaultBlockState()
                .setValue(PropertyHolder.TOP_HALF, false)
                .setValue(FramedProperties.ALT_SLOPE, true)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.TOP_HALF);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTargetOrHorizontalFacing()
                .withTop(PropertyHolder.TOP_HALF)
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        return switch (mode)
        {
            case PRIMARY -> super.rotate(state, direction, mode);
            case SECONDARY -> state.cycle(PropertyHolder.TOP_HALF);
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
        return BlockUtils.mirrorFaceBlock(state, mirror);
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        BlockState defState = FBContent.BLOCK_FRAMED_SLOPE_SLAB.value().defaultBlockState();
        return new DoubleBlockParts(
                defState.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.TOP_HALF, topHalf)
                        .setValue(FramedProperties.TOP, false)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope),
                defState.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.TOP_HALF, topHalf)
                        .setValue(FramedProperties.TOP, true)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(PropertyHolder.TOP_HALF);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if ((side == Direction.UP && top) || (side == facing.getOpposite() && edge == dirTwo))
        {
            return CamoGetter.SECOND;
        }
        else if ((side == Direction.DOWN && !top) || (side == facing && edge == dirTwo))
        {
            return CamoGetter.FIRST;
        }
        else if (side.getAxis() == facing.getClockWise().getAxis() && edge == dirTwo)
        {
            return top ? CamoGetter.SECOND : CamoGetter.FIRST;
        }

        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        if (topHalf && side == Direction.UP)
        {
            return SolidityCheck.SECOND;
        }
        else if (!topHalf && side == Direction.DOWN)
        {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleSlopeSlabBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }
}
