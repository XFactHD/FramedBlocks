package xfacthd.framedblocks.common.block.prism;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CompoundDirection;

public class FramedElevatedSlopedPrismBlock extends FramedBlock implements IFramedPrismBlock
{
    public FramedElevatedSlopedPrismBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.NORTH_UP)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                PropertyHolder.FACING_DIR, BlockStateProperties.WATERLOGGED,
                FramedProperties.SOLID, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return FramedSlopedPrismBlock.getStateForPlacement(context, this);
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (player.getMainHandItem().canPerformAction(Utils.ACTION_WRENCH))
        {
            level.setBlockAndUpdate(pos, state.setValue(FramedProperties.Y_SLOPE, !state.getValue(FramedProperties.Y_SLOPE)));
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return state.setValue(PropertyHolder.FACING_DIR, cmpDir.rotate(rot));
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return state.setValue(PropertyHolder.FACING_DIR, cmpDir.mirror(mirror));
    }

    @Override
    public BlockState getItemModelSource()
    {
        boolean inner = getBlockType() == BlockType.FRAMED_ELEVATED_INNER_SLOPED_PRISM;
        CompoundDirection cmpDir = inner ? CompoundDirection.UP_EAST : CompoundDirection.UP_WEST;
        return defaultBlockState().setValue(PropertyHolder.FACING_DIR, cmpDir);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }

    @Override
    public boolean isInnerPrism()
    {
        return getBlockType() == BlockType.FRAMED_ELEVATED_INNER_SLOPED_PRISM;
    }



    /*public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                box( 0, 0, 0,   16, .5,   16),
                box(.5, 0, 0, 15.5,  4, 15.5),
                box( 4, 0, 0,   12,  8,   12)
        );

        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                box( 0, 15.5, 0,   16, 16,   16),
                box(.5,   12, 0, 15.5, 16, 15.5),
                box( 4,    8, 0,   12, 16,   12)
        );

        VoxelShape shapeRight = ShapeUtils.orUnoptimized(
                box(0,  0, 15.5,   16,   16, 16),
                box(0, .5,   12, 15.5, 15.5, 16),
                box(0,  4,    8,   12,   12, 16)
        );

        VoxelShape shapeLeft = ShapeUtils.orUnoptimized(
                box( 0,  0, 15.5, 16,   16, 16),
                box(.5, .5,   12, 16, 15.5, 16),
                box( 4,  4,    8, 16,   12, 16)
        );

        VoxelShape shapeUp = ShapeUtils.orUnoptimized(
                box( 0,  0, 15.5,   16, 16, 16),
                box(.5, .5,   12, 15.5, 16, 16),
                box( 4, 4,    8,   12,   16, 16)
        );

        VoxelShape shapeDown = ShapeUtils.orUnoptimized(
                box( 0, 0, 15.5,   16,   16, 16),
                box(.5, 0,   12, 15.5, 15.5, 16),
                box( 4,  0,    8,   12, 12, 16)
        );

        VoxelShape[] shapes = new VoxelShape[CompoundDirection.COUNT];
        for (CompoundDirection cmpDir : CompoundDirection.values())
        {
            Direction facing = cmpDir.direction();
            Direction orientation = cmpDir.orientation();

            if (Utils.isY(facing))
            {
                shapes[cmpDir.ordinal()] = ShapeUtils.rotateShape(
                        Direction.NORTH,
                        orientation,
                        facing == Direction.UP ? shapeBottom : shapeTop
                );
            }
            else
            {
                VoxelShape shape;
                if (orientation == Direction.UP)
                {
                    shape = shapeUp;
                }
                else if (orientation == Direction.DOWN)
                {
                    shape = shapeDown;
                }
                else if (orientation == facing.getClockWise())
                {
                    shape = shapeLeft;
                }
                else if (orientation == facing.getCounterClockWise())
                {
                    shape = shapeRight;
                }
                else
                {
                    throw new IllegalArgumentException("Invalid orientation for direction!");
                }

                shapes[cmpDir.ordinal()] = ShapeUtils.rotateShape(Direction.NORTH, facing, shape);
            }
        }

        for (BlockState state : states)
        {
            CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
            builder.put(state, shapes[cmpDir.ordinal()]);
        }

        return ShapeProvider.of(builder.build());
    }*/

    public static ShapeProvider generateInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                box(   4, 0,    0,   12,   12,   16),
                box(15.5, 0,    0,   16,   16,   16),
                box(   0, 0,    0,   .5,   16,   16),
                box(  12, 0,    0, 15.5, 15.5,   16),
                box(  .5, 0,    0,    4, 15.5,   16),
                box(   0, 0, 15.5,   16,   16,   16),
                box(  .5, 0,   12, 15.5, 15.5, 15.5)
        );

        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                box(   4,  4,    0,   12, 16,   16),
                box(15.5,  0,    0,   16, 16,   16),
                box(   0,  0,    0,   .5, 16,   16),
                box(  12, .5,    0, 15.5, 16,   16),
                box(  .5, .5,    0,    4, 16,   16),
                box(   0,  0, 15.5,   16, 16,   16),
                box(  .5, .5,   12, 15.5, 16, 15.5)
        );

        VoxelShape shapeRight = ShapeUtils.orUnoptimized(
                box( 0,    4,  4, 16,   12, 16),
                box( 0,    0,  0, 16,   .5, 16),
                box( 0, 15.5,  0, 16,   16, 16),
                box( 0,   .5, .5, 16,    4, 16),
                box( 0,   12, .5, 16, 15.5, 16),
                box( 0,    0,  0, .5,   16, 16),
                box(.5,   .5, .5,  4, 15.5, 16)
        );

        VoxelShape shapeLeft = ShapeUtils.orUnoptimized(
                box(0, 4, 4, 16, 12, 16),
                box(0, 15.5, 0, 16, 16, 16),
                box(0, 0, 0, 16, 0.5, 16),
                box(0, 12, 0.5, 16, 15.5, 16),
                box(0, 0.5, 0.5, 16, 4, 16),
                box(15.5, 0, 0, 16, 16, 16),
                box(12, 0.5, 0.5, 15.5, 15.5, 16)
        );

        VoxelShape shapeUp = ShapeUtils.orUnoptimized(
                box(   4,  0,  4,   12, 16, 16),
                box(15.5,  0,  0,   16, 16, 16),
                box(   0,  0,  0,   .5, 16, 16),
                box(  12,  0, .5, 15.5, 16, 16),
                box(  .5,  0, .5,    4, 16, 16),
                box(   0,  0,  0,   16, .5, 16),
                box(  .5, .5, .5, 15.5,  4, 16)
        );

        VoxelShape shapeDown = ShapeUtils.orUnoptimized(
                box(   4,    0,  4,   12,   16, 16),
                box(   0,    0,  0,   .5,   16, 16),
                box(15.5,    0,  0,   16,   16, 16),
                box(  .5,    0, .5,    4,   16, 16),
                box(  12,    0, .5, 15.5,   16, 16),
                box(   0, 15.5,  0,   16,   16, 16),
                box(  .5,   12, .5, 15.5, 15.5, 16)
        );

        VoxelShape[] shapes = new VoxelShape[CompoundDirection.COUNT];
        for (CompoundDirection cmpDir : CompoundDirection.values())
        {
            Direction facing = cmpDir.direction();
            Direction orientation = cmpDir.orientation();

            if (Utils.isY(facing))
            {
                shapes[cmpDir.ordinal()] = ShapeUtils.rotateShape(
                        Direction.NORTH,
                        orientation,
                        facing == Direction.UP ? shapeBottom : shapeTop
                );
            }
            else
            {
                VoxelShape shape;
                if (orientation == Direction.UP)
                {
                    shape = shapeUp;
                }
                else if (orientation == Direction.DOWN)
                {
                    shape = shapeDown;
                }
                else if (orientation == facing.getClockWise())
                {
                    shape = shapeRight;
                }
                else if (orientation == facing.getCounterClockWise())
                {
                    shape = shapeLeft;
                }
                else
                {
                    throw new IllegalArgumentException("Invalid orientation for direction!");
                }

                shapes[cmpDir.ordinal()] = ShapeUtils.rotateShape(Direction.NORTH, facing, shape);
            }
        }

        for (BlockState state : states)
        {
            CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
            builder.put(state, shapes[cmpDir.ordinal()]);
        }

        return ShapeProvider.of(builder.build());
    }
}
