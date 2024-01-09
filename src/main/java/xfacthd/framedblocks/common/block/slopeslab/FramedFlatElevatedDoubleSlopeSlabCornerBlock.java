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
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedFlatElevatedDoubleSlopeSlabCornerBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

public class FramedFlatElevatedDoubleSlopeSlabCornerBlock extends AbstractFramedDoubleBlock
{
    private final boolean isInner;

    public FramedFlatElevatedDoubleSlopeSlabCornerBlock(BlockType blockType)
    {
        super(blockType);
        this.isInner = blockType == BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER;
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(FramedProperties.Y_SLOPE, true)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, FramedProperties.Y_SLOPE);
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
            return state.cycle(FramedProperties.TOP);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        return rotate(state, Direction.UP, rotation);
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
        BlockState defStateOne;
        BlockState defStateTwo;
        if (getBlockType() == BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER)
        {
            defStateOne = FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER.value().defaultBlockState();
            defStateTwo = FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.value().defaultBlockState();
        }
        else
        {
            defStateOne = FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER.value().defaultBlockState();
            defStateTwo = FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.value().defaultBlockState();
        }

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return new Tuple<>(
                defStateOne.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.TOP, top)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                defStateTwo.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.TOP, !top)
                        .setValue(PropertyHolder.TOP_HALF, !top)
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
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (Utils.isY(side))
        {
            if (side == dirTwo)
            {
                return CamoGetter.FIRST;
            }
            else if (side == dirTwo.getOpposite())
            {
                return CamoGetter.SECOND;
            }
        }

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (isInner && (side == facing || side == facing.getCounterClockWise()))
        {
            return CamoGetter.FIRST;
        }
        else if (!Utils.isY(side))
        {
            if (edge == dirTwo)
            {
                return CamoGetter.FIRST;
            }
            else if (edge == dirTwo.getOpposite())
            {
                return CamoGetter.SECOND;
            }
        }

        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        boolean top = state.getValue(FramedProperties.TOP);
        if (side == Direction.UP)
        {
            return top ? SolidityCheck.FIRST : SolidityCheck.SECOND;
        }
        else if (side == Direction.DOWN)
        {
            return top ? SolidityCheck.SECOND : SolidityCheck.FIRST;
        }
        else if (isInner)
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            if (side == facing || side == facing.getCounterClockWise())
            {
                return SolidityCheck.FIRST;
            }
        }
        return SolidityCheck.BOTH;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedFlatElevatedDoubleSlopeSlabCornerBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return switch (getBlockType())
        {
            case FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER ->
                    FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
            case FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER ->
                    FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
            default -> throw new IllegalStateException("Invalid block type: " + getBlockType());
        };
    }
}
