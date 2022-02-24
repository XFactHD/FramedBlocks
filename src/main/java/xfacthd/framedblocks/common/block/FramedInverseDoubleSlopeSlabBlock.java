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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.blockentity.FramedInverseDoubleSlopeSlabBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedInverseDoubleSlopeSlabBlock extends AbstractFramedDoubleBlock
{
    public FramedInverseDoubleSlopeSlabBlock() { super(BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction face = context.getClickedFace();
        Direction facing = Utils.isY(face) ? context.getHorizontalDirection() : face.getOpposite();
        BlockState state = defaultBlockState().setValue(FramedProperties.FACING_HOR, facing);
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedInverseDoubleSlopeSlabBlockEntity(pos, state);
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shape = Shapes.or(
                FramedSlopeSlabBlock.SHAPE_BOTTOM.move(0, .5, 0),
                Utils.rotateShape(Direction.NORTH, Direction.SOUTH, FramedSlopeSlabBlock.SHAPE_TOP)
        );

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, shape)
            );
        }

        return builder.build();
    }
}
