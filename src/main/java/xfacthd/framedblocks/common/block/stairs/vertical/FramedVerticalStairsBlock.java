package xfacthd.framedblocks.common.block.stairs.vertical;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.block.stairs.standard.FramedHalfStairsBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.StairsType;

public class FramedVerticalStairsBlock extends FramedBlock
{
    public FramedVerticalStairsBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.STATE_LOCKED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.STAIRS_TYPE, BlockStateProperties.WATERLOGGED,
                FramedProperties.SOLID, FramedProperties.STATE_LOCKED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withHalfFacing()
                .withCustom((state, modCtx) -> getStateFromContext(state, modCtx.getLevel(), modCtx.getClickedPos()))
                .tryWithWater() // Vertical double stairs don't support waterlogging
                .build();
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (facing != dir.getOpposite() && facing != dir.getClockWise())
        {
            state = getStateFromContext(state, level, pos);
        }
        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    private static BlockState getStateFromContext(BlockState state, LevelAccessor level, BlockPos pos)
    {
        if (state.getValue(FramedProperties.STATE_LOCKED))
        {
            return state;
        }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);

        BlockState front = level.getBlockState(pos.relative(dir));
        BlockState left = level.getBlockState(pos.relative(dir.getCounterClockWise()));

        if (isNoStair(front) && isNoStair(left))
        {
            return state.setValue(PropertyHolder.STAIRS_TYPE, StairsType.VERTICAL);
        }
        else
        {
            boolean topCornerFront = false;
            boolean bottomCornerFront = false;

            if (front.getBlock() instanceof StairBlock && front.getValue(BlockStateProperties.HORIZONTAL_FACING) == dir.getCounterClockWise())
            {
                topCornerFront = front.getValue(BlockStateProperties.HALF) == Half.BOTTOM;
                bottomCornerFront = front.getValue(BlockStateProperties.HALF) == Half.TOP;
            }
            else if (front.getBlock() instanceof FramedHalfStairsBlock && front.getValue(FramedProperties.FACING_HOR) == dir.getCounterClockWise())
            {
                boolean top = front.getValue(FramedProperties.TOP);

                if (!front.getValue(PropertyHolder.RIGHT))
                {
                    topCornerFront = !top;
                    bottomCornerFront = top;
                }
            }

            boolean topCornerLeft = false;
            boolean bottomCornerLeft = false;

            if (left.getBlock() instanceof StairBlock && left.getValue(BlockStateProperties.HORIZONTAL_FACING) == dir)
            {
                topCornerLeft = left.getValue(BlockStateProperties.HALF) == Half.BOTTOM;
                bottomCornerLeft = left.getValue(BlockStateProperties.HALF) == Half.TOP;
            }
            else if (left.getBlock() instanceof FramedHalfStairsBlock && left.getValue(FramedProperties.FACING_HOR) == dir)
            {
                boolean top = left.getValue(FramedProperties.TOP);

                if (left.getValue(PropertyHolder.RIGHT))
                {
                    topCornerLeft = !top;
                    bottomCornerLeft = top;
                }
            }

            BlockState above = level.getBlockState(pos.above());
            BlockState below = level.getBlockState(pos.below());

            StairsType type = StairsType.VERTICAL;
            if ((topCornerFront || topCornerLeft) && !(above.getBlock() instanceof FramedVerticalStairsBlock))
            {
                if (!topCornerLeft)
                {
                    type = StairsType.TOP_FWD;
                }
                else if (!topCornerFront)
                {
                    type = StairsType.TOP_CCW;
                }
                else
                {
                    type = StairsType.TOP_BOTH;
                }
            }
            else if ((bottomCornerFront || bottomCornerLeft) && !(below.getBlock() instanceof FramedVerticalStairsBlock))
            {
                if (!bottomCornerLeft)
                {
                    type = StairsType.BOTTOM_FWD;
                }
                else if (!bottomCornerFront)
                {
                    type = StairsType.BOTTOM_CCW;
                }
                else
                {
                    type = StairsType.BOTTOM_BOTH;
                }
            }

            return state.setValue(PropertyHolder.STAIRS_TYPE, type);
        }
    }

    private static boolean isNoStair(BlockState state)
    {
        return !(state.getBlock() instanceof StairBlock) && !(state.getBlock() instanceof FramedHalfStairsBlock);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}
