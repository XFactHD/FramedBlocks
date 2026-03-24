package io.github.xfacthd.framedblocks.common.block.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slopepanelcorner.FramedExtendedDoubleCornerSlopePanelBlockEntity;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slopepanelcorner.FramedExtendedInnerDoubleCornerSlopePanelBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.item.block.VerticalAndWallBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedExtendedDoubleCornerSlopePanelBlock extends FramedDoubleBlock implements SlopeToggleBlock
{
    public FramedExtendedDoubleCornerSlopePanelBlock(BlockType blockType, Properties props)
    {
        super(blockType, props);
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
        return FramedCornerSlopePanelBlock.getStateForPlacement(
                this, ctx, getBlockType() == BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL, true
        );
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
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return switch (getBlockType())
        {
            case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL -> new FramedExtendedDoubleCornerSlopePanelBlockEntity(pos, state);
            case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL -> new FramedExtendedInnerDoubleCornerSlopePanelBlockEntity(pos, state);
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        return switch (getBlockType())
        {
            case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL -> new DoubleBlockParts(
                    FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(FramedProperties.ALT_SLOPE, altSlope),
                    FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(FramedProperties.TOP, !top)
                            .setValue(FramedProperties.ALT_SLOPE, altSlope)
            );
            case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL -> new DoubleBlockParts(
                    FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(FramedProperties.ALT_SLOPE, altSlope),
                    FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(FramedProperties.TOP, !top)
                            .setValue(FramedProperties.ALT_SLOPE, altSlope)
            );
            default -> throw new IllegalArgumentException("Unexpected type: " + getBlockType());
        };
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        boolean top = state.getValue(FramedProperties.TOP);
        return top ? DoubleBlockTopInteractionMode.FIRST : DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        return switch (getBlockType())
        {
            case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL ->
            {
                Direction facing = state.getValue(FramedProperties.FACING_HOR);
                boolean top = state.getValue(FramedProperties.TOP);
                Direction dirTwo = top ? Direction.UP : Direction.DOWN;
                if (side == dirTwo)
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == facing.getOpposite() || side == facing.getClockWise())
                {
                    yield CamoGetter.SECOND;
                }
                else if (side == facing)
                {
                    if (edge == facing.getCounterClockWise() || edge == dirTwo)
                    {
                        yield CamoGetter.FIRST;
                    }
                    else if (edge == facing.getClockWise())
                    {
                        yield CamoGetter.SECOND;
                    }
                }
                else if (side == facing.getCounterClockWise())
                {
                    if (edge == facing || edge == dirTwo)
                    {
                        yield CamoGetter.FIRST;
                    }
                    else if (edge == facing.getOpposite())
                    {
                        yield CamoGetter.SECOND;
                    }
                }
                else if (side == dirTwo.getOpposite() && (edge == facing.getClockWise() || edge == facing.getOpposite()))
                {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
            case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL ->
            {
                Direction facing = state.getValue(FramedProperties.FACING_HOR);
                boolean top = state.getValue(FramedProperties.TOP);
                Direction dirTwo = top ? Direction.UP : Direction.DOWN;
                if (side == facing.getOpposite() || side == facing.getClockWise() || side == dirTwo)
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == dirTwo.getOpposite() && (edge == facing.getOpposite() || edge == facing.getClockWise()))
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == facing)
                {
                    if (edge == dirTwo || edge == facing.getClockWise())
                    {
                        yield CamoGetter.FIRST;
                    }
                    else if (edge == facing.getCounterClockWise())
                    {
                        yield CamoGetter.SECOND;
                    }
                }
                else if (side == facing.getCounterClockWise())
                {
                    if (edge == dirTwo || edge == facing.getOpposite())
                    {
                        yield CamoGetter.FIRST;
                    }
                    else if (edge == facing)
                    {
                        yield CamoGetter.SECOND;
                    }
                }
                yield CamoGetter.NONE;
            }
            default -> throw new IllegalArgumentException("Unexpected type: " + getBlockType());
        };
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        return switch (getBlockType())
        {
            case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL ->
            {
                if (DirUtils.isY(side))
                {
                    boolean top = state.getValue(FramedProperties.TOP);
                    if (top ? (side == Direction.UP) : (side == Direction.DOWN))
                    {
                        yield SolidityCheck.FIRST;
                    }
                }

                Direction facing = state.getValue(FramedProperties.FACING_HOR);
                if (side == facing.getOpposite() || side == facing.getClockWise())
                {
                    yield SolidityCheck.SECOND;
                }
                yield SolidityCheck.BOTH;
            }
            case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL ->
            {
                boolean primaryYFace = false;
                if (DirUtils.isY(side))
                {
                    boolean top = state.getValue(FramedProperties.TOP);
                    primaryYFace = top ? (side == Direction.UP) : (side == Direction.DOWN);
                }

                Direction facing = state.getValue(FramedProperties.FACING_HOR);
                if (primaryYFace || side == facing.getOpposite() || side == facing.getClockWise())
                {
                    yield SolidityCheck.FIRST;
                }
                yield SolidityCheck.BOTH;
            }
            default -> throw new IllegalArgumentException("Unexpected type: " + getBlockType());
        };
    }

    @Override
    public BlockItem createBlockItem(Item.Properties props)
    {
        Block other = switch (getBlockType())
        {
            case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL_WALL.value();
            case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL_WALL.value();
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
        return new VerticalAndWallBlockItem(this, other, props);
    }

    @Override
    public BlockState getItemModelSource()
    {
        boolean inner = getBlockType() == BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL;
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
