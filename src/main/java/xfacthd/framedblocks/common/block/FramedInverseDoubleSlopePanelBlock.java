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
import xfacthd.framedblocks.common.blockentity.FramedInverseDoubleSlopePanelBlockEntity;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.Rotation;

public class FramedInverseDoubleSlopePanelBlock extends AbstractFramedDoubleBlock
{
    public FramedInverseDoubleSlopePanelBlock() { super(BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.ROTATION, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction facing = context.getHorizontalDirection();

        Direction side = context.getClickedFace();
        Rotation rotation;
        if (side == facing.getOpposite())
        {
            rotation = Rotation.fromWallCross(context.getClickLocation(), side);
        }
        else
        {
            rotation = Rotation.fromDirection(facing, side);
        }

        BlockState state = defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, facing)
                .setValue(PropertyHolder.ROTATION, rotation);
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedInverseDoubleSlopePanelBlockEntity(pos, state);
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            Rotation rotation = state.getValue(PropertyHolder.ROTATION);

            VoxelShape shapeOne = FramedSlopePanelBlock.SHAPES.get(rotation.isVertical() ? rotation.getOpposite() : rotation);
            VoxelShape shape = Shapes.or(
                    Utils.rotateShape(Direction.NORTH, Direction.SOUTH, shapeOne.move(0, 0, .5)),
                    FramedSlopePanelBlock.SHAPES.get(rotation).move(0, 0, .5)
            );

            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, shape)
            );
        }

        return builder.build();
    }
}
