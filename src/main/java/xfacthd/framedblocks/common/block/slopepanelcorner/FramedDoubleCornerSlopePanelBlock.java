package xfacthd.framedblocks.common.block.slopepanelcorner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.slopepanelcorner.FramedLargeDoubleCornerSlopePanelBlockEntity;
import xfacthd.framedblocks.common.blockentity.doubled.slopepanelcorner.FramedSmallDoubleCornerSlopePanelBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;
import xfacthd.framedblocks.common.item.VerticalAndWallBlockItem;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

public class FramedDoubleCornerSlopePanelBlock extends AbstractFramedDoubleBlock
{
    public FramedDoubleCornerSlopePanelBlock(BlockType blockType)
    {
        super(blockType);
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
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return FramedCornerSlopePanelBlock.getStateForPlacement(
                this, ctx, getBlockType() == BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL, false
        );
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
        return state.cycle(FramedProperties.TOP);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return switch (getBlockType())
        {
            case FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL -> new FramedSmallDoubleCornerSlopePanelBlockEntity(pos, state);
            case FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL -> new FramedLargeDoubleCornerSlopePanelBlockEntity(pos, state);
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return switch (getBlockType())
        {
            case FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(FramedProperties.Y_SLOPE, ySlope),
                    FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(FramedProperties.TOP, !top)
                            .setValue(FramedProperties.Y_SLOPE, ySlope)
            );
            case FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(FramedProperties.Y_SLOPE, ySlope),
                    FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(FramedProperties.TOP, !top)
                            .setValue(FramedProperties.Y_SLOPE, ySlope)
            );
            default -> throw new IllegalArgumentException("Unexpected type: " + getBlockType());
        };
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
        return switch (getBlockType())
        {
            case FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL ->
            {
                Direction facing = state.getValue(FramedProperties.FACING_HOR);
                if (side == facing && edge == facing.getCounterClockWise())
                {
                    yield CamoGetter.SECOND;
                }
                else if (side == facing.getCounterClockWise() && edge == facing)
                {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
            case FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL ->
            {
                Direction facing = state.getValue(FramedProperties.FACING_HOR);
                Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;
                if (side == facing.getOpposite() || side == facing.getClockWise())
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == dirTwo && (edge == facing.getOpposite() || edge == facing.getClockWise()))
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == dirTwo.getOpposite() && (edge == facing.getOpposite() || edge == facing.getClockWise()))
                {
                    yield CamoGetter.SECOND;
                }
                else if (side == facing.getCounterClockWise() && edge == facing.getOpposite())
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == facing && edge == facing.getClockWise())
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
            case FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL -> SolidityCheck.NONE;
            case FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL ->
            {
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
    public BlockItem createBlockItem()
    {
        Block other = switch (getBlockType())
        {
            case FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_WALL.value();
            case FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_WALL.value();
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
        return new VerticalAndWallBlockItem(this, other, new Item.Properties());
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.EAST);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }
}
