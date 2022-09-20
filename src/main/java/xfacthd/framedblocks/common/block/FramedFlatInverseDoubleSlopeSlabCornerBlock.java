package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.blockentity.FramedFlatInverseDoubleSlopeSlabCornerBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedFlatInverseDoubleSlopeSlabCornerBlock extends AbstractFramedDoubleBlock
{
    public FramedFlatInverseDoubleSlopeSlabCornerBlock()
    {
        super(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction face = context.getClickedFace();
        Direction facing = Utils.isY(face) ? context.getHorizontalDirection() : face.getOpposite();

        BlockState state = defaultBlockState().setValue(FramedProperties.FACING_HOR, facing);

        state = withTop(state, context.getClickedFace(), context.getClickLocation());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedFlatInverseDoubleSlopeSlabCornerBlockEntity(pos, state);
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBot = Shapes.or(
                Shapes.join(
                        FramedSlopeSlabBlock.SHAPE_BOTTOM.move(0, .5, 0),
                        Utils.rotateShape(Direction.NORTH, Direction.WEST, FramedSlopeSlabBlock.SHAPE_BOTTOM.move(0, .5, 0)),
                        BooleanOp.AND
                ),
                Shapes.or(
                        Utils.rotateShape(Direction.NORTH, Direction.SOUTH, FramedSlopeSlabBlock.SHAPE_TOP),
                        Utils.rotateShape(Direction.NORTH, Direction.EAST, FramedSlopeSlabBlock.SHAPE_TOP)
                )
        );

        VoxelShape shapeTop = Shapes.or(
                Shapes.join(
                        FramedSlopeSlabBlock.SHAPE_TOP,
                        Utils.rotateShape(Direction.NORTH, Direction.WEST, FramedSlopeSlabBlock.SHAPE_TOP),
                        BooleanOp.AND
                ),
                Shapes.or(
                        Utils.rotateShape(Direction.NORTH, Direction.SOUTH, FramedSlopeSlabBlock.SHAPE_BOTTOM.move(0, .5, 0)),
                        Utils.rotateShape(Direction.NORTH, Direction.EAST, FramedSlopeSlabBlock.SHAPE_BOTTOM.move(0, .5, 0))
                )
        );

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);

            VoxelShape shape = Utils.rotateShape(
                    Direction.NORTH,
                    facing,
                    top ? shapeTop : shapeBot
            );
            builder.put(state, shape);
        }

        return builder.build();
    }
}
