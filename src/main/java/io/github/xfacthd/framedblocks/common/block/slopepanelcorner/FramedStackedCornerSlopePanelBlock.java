package io.github.xfacthd.framedblocks.common.block.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.item.block.VerticalAndWallBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedStackedCornerSlopePanelBlock extends FramedDoubleBlock
{
    public FramedStackedCornerSlopePanelBlock(BlockType blockType, Properties props)
    {
        super(blockType, props);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, FramedProperties.TOP,
                FramedProperties.Y_SLOPE, BlockStateProperties.WATERLOGGED
        );
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return FramedCornerSlopePanelBlock.getStateForPlacement(
                this, ctx, getBlockType() == BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL, true
        );
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
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
    public DoubleBlockParts calculateParts(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return switch (getBlockType())
        {
            case FRAMED_STACKED_CORNER_SLOPE_PANEL -> new DoubleBlockParts(
                    FBContent.BLOCK_FRAMED_CORNER_PILLAR.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir),
                    FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(FramedProperties.Y_SLOPE, ySlope)
            );
            case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL -> new DoubleBlockParts(
                    FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir.getOpposite()),
                    FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(FramedProperties.Y_SLOPE, ySlope)
            );
            default -> throw new IllegalArgumentException("Unexpected type: " + getBlockType());
        };
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        return switch (getBlockType())
        {
            case FRAMED_STACKED_CORNER_SLOPE_PANEL ->
            {
                Direction facing = state.getValue(FramedProperties.FACING_HOR);
                Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;

                if (side == facing && edge == facing.getCounterClockWise())
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == facing.getCounterClockWise() && edge == facing)
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == dirTwo && (edge == facing.getOpposite() || edge == facing.getClockWise()))
                {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
            case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL ->
            {
                Direction facing = state.getValue(FramedProperties.FACING_HOR);
                if (side == facing.getOpposite() || side == facing.getClockWise())
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == facing && edge == facing.getClockWise())
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == facing.getCounterClockWise() && edge == facing.getOpposite())
                {
                    yield CamoGetter.FIRST;
                }
                else if (Utils.isY(side) && (edge == facing.getOpposite() || edge == facing.getClockWise()))
                {
                    yield CamoGetter.FIRST;
                }
                yield CamoGetter.NONE;
            }
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        return switch (getBlockType())
        {
            case FRAMED_STACKED_CORNER_SLOPE_PANEL ->
            {
                if (Utils.isY(side))
                {
                    boolean top = state.getValue(FramedProperties.TOP);
                    if ((!top && side == Direction.DOWN) || (top && side == Direction.UP))
                    {
                        yield SolidityCheck.BOTH;
                    }
                }
                yield SolidityCheck.NONE;
            }
            case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL ->
            {
                if (Utils.isY(side))
                {
                    boolean top = state.getValue(FramedProperties.TOP);
                    if ((!top && side == Direction.DOWN) || (top && side == Direction.UP))
                    {
                        yield SolidityCheck.BOTH;
                    }
                }

                Direction facing = state.getValue(FramedProperties.FACING_HOR);
                if (side == facing.getOpposite() || side == facing.getClockWise())
                {
                    yield SolidityCheck.FIRST;
                }
                yield SolidityCheck.NONE;
            }
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
    }

    @Override
    public BlockItem createBlockItem(Item.Properties props)
    {
        Block other = switch (getBlockType())
        {
            case FRAMED_STACKED_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL_WALL.value();
            case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_WALL.value();
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
        return new VerticalAndWallBlockItem(this, other, props);
    }

    @Override
    public BlockState getItemModelSource()
    {
        boolean inner = getBlockType() == BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL;
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, inner ? Direction.EAST : Direction.WEST);
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
