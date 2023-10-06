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
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedExtendedDoubleCornerSlopePanelBlockEntity;
import xfacthd.framedblocks.common.blockentity.doubled.FramedExtendedInnerDoubleCornerSlopePanelBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;
import xfacthd.framedblocks.common.item.VerticalAndWallBlockItem;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

@SuppressWarnings("deprecation")
public class FramedExtendedDoubleCornerSlopePanelBlock extends AbstractFramedDoubleBlock
{
    public FramedExtendedDoubleCornerSlopePanelBlock(BlockType blockType)
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
                FramedProperties.FACING_HOR, FramedProperties.TOP, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return FramedCornerSlopePanelBlock.getStateForPlacement(
                defaultBlockState(),
                ctx,
                getBlockType() == BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL,
                true
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
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return switch ((BlockType) getBlockType())
        {
            case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL -> new FramedExtendedDoubleCornerSlopePanelBlockEntity(pos, state);
            case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL -> new FramedExtendedInnerDoubleCornerSlopePanelBlockEntity(pos, state);
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return switch ((BlockType) getBlockType())
        {
            case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL.get()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(FramedProperties.Y_SLOPE, ySlope),
                    FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL.get()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(FramedProperties.TOP, !top)
                            .setValue(FramedProperties.Y_SLOPE, ySlope)
            );
            case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL.get()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(FramedProperties.Y_SLOPE, ySlope),
                    FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL.get()
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
        boolean top = state.getValue(FramedProperties.TOP);
        return top ? DoubleBlockTopInteractionMode.FIRST : DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        return switch ((BlockType) getBlockType())
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
        return switch ((BlockType) getBlockType())
        {
            case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL ->
            {
                if (Utils.isY(side))
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
                if (Utils.isY(side))
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
    public BlockItem createBlockItem()
    {
        Block other = switch ((BlockType) getBlockType())
        {
            case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL_WALL.get();
            case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL_WALL.get();
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
        return new VerticalAndWallBlockItem(this, other, new Item.Properties());
    }



    public static BlockState itemModelSource()
    {
        return FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL.get().defaultBlockState();
    }

    public static BlockState itemModelSourceInner()
    {
        return FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.EAST);
    }
}
