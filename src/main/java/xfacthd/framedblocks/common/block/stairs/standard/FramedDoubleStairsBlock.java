package xfacthd.framedblocks.common.block.stairs.standard;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.doubleblock.*;
import xfacthd.framedblocks.common.util.FramedUtils;

import java.util.function.Consumer;

public class FramedDoubleStairsBlock extends FramedStairsBlock implements IFramedDoubleBlock
{
    public FramedDoubleStairsBlock()
    {
        super(BlockType.FRAMED_DOUBLE_STAIRS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        FramedUtils.removeProperty(builder, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        BlockPos pos = ctx.getClickedPos();
        Direction side = ctx.getClickedFace();
        Half half = switch (side)
        {
            case UP -> Half.BOTTOM;
            case DOWN -> Half.TOP;
            default -> Utils.fractionInDir(ctx.getClickLocation(), Direction.UP) > .5D ? Half.TOP : Half.BOTTOM;
        };
        BlockState state = defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection())
                .setValue(HALF, half);
        return state.setValue(SHAPE, getStairsShape(state, ctx.getLevel(), pos));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction side, BlockState adjState, LevelAccessor level, BlockPos pos, BlockPos adjPos)
    {
        BlockState newState = updateShapeLockable(
                state, level, pos,
                () -> !Utils.isY(side) ? state.setValue(SHAPE, getStairsShape(state, level, pos)) : state
        );
        if (newState == state)
        {
            updateCulling(level, pos);
        }
        return newState;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return isIntangible(state, level, pos, ctx) ? Shapes.empty() : Shapes.block();
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid)
    {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState)
    {
        return false;
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state)
    {
        return ItemStack.EMPTY;
    }

    @Override
    protected FluidState getFluidState(BlockState state)
    {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FACING);
        StairsShape shape = state.getValue(SHAPE);
        boolean top = state.getValue(BlockStateProperties.HALF) == Half.TOP;

        BlockState partTwo = switch (shape)
        {
            case STRAIGHT -> FBContent.BLOCK_FRAMED_SLAB_EDGE.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                    .setValue(FramedProperties.TOP, !top);
            case INNER_LEFT -> FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                    .setValue(FramedProperties.TOP, !top);
            case INNER_RIGHT -> FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, facing.getCounterClockWise())
                    .setValue(FramedProperties.TOP, !top);
            case OUTER_LEFT -> FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                    .setValue(FramedProperties.TOP, !top);
            case OUTER_RIGHT -> FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, facing.getCounterClockWise())
                    .setValue(FramedProperties.TOP, !top);
        };

        return new Tuple<>(
                FBContent.BLOCK_FRAMED_STAIRS.value()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                        .setValue(BlockStateProperties.STAIRS_SHAPE, shape)
                        .setValue(BlockStateProperties.HALF, top ? Half.TOP : Half.BOTTOM),
                partTwo
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        if (state.getValue(BlockStateProperties.HALF) == Half.TOP)
        {
            return DoubleBlockTopInteractionMode.FIRST;
        }
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FACING);
        boolean top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        return switch (state.getValue(SHAPE))
        {
            case STRAIGHT ->
            {
                if (side == facing || side == dirTwo)
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == facing.getOpposite())
                {
                    if (edge == dirTwo)
                    {
                        yield CamoGetter.FIRST;
                    }
                    else if (edge == dirTwo.getOpposite())
                    {
                        yield CamoGetter.SECOND;
                    }
                }
                else if (side == dirTwo.getOpposite())
                {
                    if (edge == facing)
                    {
                        yield CamoGetter.FIRST;
                    }
                    else if (edge == facing.getOpposite())
                    {
                        yield CamoGetter.SECOND;
                    }
                }
                else if (side.getAxis() == facing.getClockWise().getAxis())
                {
                    if (edge == facing || edge == dirTwo)
                    {
                        yield CamoGetter.FIRST;
                    }
                }
                yield CamoGetter.NONE;
            }
            case INNER_LEFT ->
            {
                if (side == facing || side == facing.getCounterClockWise() || side == dirTwo)
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == dirTwo.getOpposite())
                {
                    if (edge == facing || edge == facing.getCounterClockWise())
                    {
                        yield CamoGetter.FIRST;
                    }
                }
                else if (side == facing.getOpposite())
                {
                    if (edge == facing.getCounterClockWise() || edge == dirTwo)
                    {
                        yield CamoGetter.FIRST;
                    }
                }
                else if (side == facing.getClockWise())
                {
                    if (edge == facing || edge == dirTwo)
                    {
                        yield CamoGetter.FIRST;
                    }
                }
                yield CamoGetter.NONE;
            }
            case INNER_RIGHT ->
            {
                if (side == facing || side == facing.getClockWise() || side == dirTwo)
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == dirTwo.getOpposite())
                {
                    if (edge == facing || edge == facing.getClockWise())
                    {
                        yield CamoGetter.FIRST;
                    }
                }
                else if (side == facing.getOpposite())
                {
                    if (edge == facing.getClockWise() || edge == dirTwo)
                    {
                        yield CamoGetter.FIRST;
                    }
                }
                else if (side == facing.getCounterClockWise())
                {
                    if (edge == facing || edge == dirTwo)
                    {
                        yield CamoGetter.FIRST;
                    }
                }
                yield CamoGetter.NONE;
            }
            case OUTER_LEFT ->
            {
                if (side == dirTwo || (side != dirTwo.getOpposite() && edge == dirTwo))
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == facing && edge == facing.getCounterClockWise())
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == dirTwo.getOpposite() && (edge == facing.getOpposite() || edge == facing.getClockWise()))
                {
                    yield CamoGetter.SECOND;
                }
                else if ((side == facing.getOpposite() || side == facing.getClockWise()) && edge == dirTwo.getOpposite())
                {
                    yield CamoGetter.SECOND;
                }
                else if (side == facing.getCounterClockWise() && edge == facing)
                {
                    yield CamoGetter.FIRST;
                }
                yield CamoGetter.NONE;
            }
            case OUTER_RIGHT ->
            {
                if (side == dirTwo || (side != dirTwo.getOpposite() && edge == dirTwo))
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == facing && edge == facing.getClockWise())
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == dirTwo.getOpposite() && (edge == facing.getOpposite() || edge == facing.getCounterClockWise()))
                {
                    yield CamoGetter.SECOND;
                }
                else if ((side == facing.getOpposite() || side == facing.getCounterClockWise()) && edge == dirTwo.getOpposite())
                {
                    yield CamoGetter.SECOND;
                }
                else if (side == facing.getClockWise() && edge == facing)
                {
                    yield CamoGetter.FIRST;
                }
                yield CamoGetter.NONE;
            }
        };
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        Direction facing = state.getValue(FACING);
        StairsShape shape = state.getValue(SHAPE);
        boolean top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == facing && shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT)
        {
            return SolidityCheck.FIRST;
        }
        else if (side == dirTwo)
        {
            return SolidityCheck.FIRST;
        }
        else if (shape == StairsShape.INNER_LEFT && side == facing.getCounterClockWise())
        {
            return SolidityCheck.FIRST;
        }
        else if (shape == StairsShape.INNER_RIGHT && side == facing.getClockWise())
        {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.BOTH;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleBlockEntity(pos, state);
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        consumer.accept(FramedDoubleBlockRenderProperties.INSTANCE);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FACING, Direction.SOUTH);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }
}
