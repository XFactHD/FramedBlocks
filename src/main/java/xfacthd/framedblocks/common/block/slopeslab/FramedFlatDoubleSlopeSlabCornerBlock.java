package xfacthd.framedblocks.common.block.slopeslab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.slopeslab.FramedFlatDoubleSlopeSlabCornerBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

public class FramedFlatDoubleSlopeSlabCornerBlock extends AbstractFramedDoubleBlock
{
    public FramedFlatDoubleSlopeSlabCornerBlock()
    {
        super(BlockType.FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(PropertyHolder.TOP_HALF, false)
                .setValue(FramedProperties.Y_SLOPE, true)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, FramedProperties.TOP, PropertyHolder.TOP_HALF,
                BlockStateProperties.WATERLOGGED, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withHalfFacing()
                .withTop(PropertyHolder.TOP_HALF)
                .withCustom((state, modCtx) ->
                        state.setValue(FramedProperties.TOP, ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown())
                )
                .withWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (Utils.isY(face))
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE)
        {
            return state.cycle(PropertyHolder.TOP_HALF);
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
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return new Tuple<>(
                FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.TOP_HALF, topHalf)
                        .setValue(FramedProperties.TOP, top)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.TOP_HALF, topHalf)
                        .setValue(FramedProperties.TOP, !top)
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        if (state.getValue(FramedProperties.TOP))
        {
            return DoubleBlockTopInteractionMode.FIRST;
        }
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);

        if (side == Direction.UP && topHalf)
        {
            return top ? CamoGetter.FIRST : CamoGetter.SECOND;
        }
        else if (side == Direction.DOWN && !topHalf)
        {
            return top ? CamoGetter.SECOND : CamoGetter.FIRST;
        }

        if (side == facing || side == facing.getCounterClockWise())
        {
            if ((!topHalf && edge == Direction.DOWN) || (topHalf && edge == Direction.UP))
            {
                return CamoGetter.FIRST;
            }
        }
        else if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            if ((!topHalf && edge == Direction.DOWN) || (topHalf && edge == Direction.UP))
            {
                return top == topHalf ? CamoGetter.FIRST : CamoGetter.SECOND;
            }
        }

        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        if (topHalf && side == Direction.UP)
        {
            return top ? SolidityCheck.FIRST : SolidityCheck.SECOND;
        }
        else if (!topHalf && side == Direction.DOWN)
        {
            return top ? SolidityCheck.SECOND : SolidityCheck.FIRST;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedFlatDoubleSlopeSlabCornerBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }
}
