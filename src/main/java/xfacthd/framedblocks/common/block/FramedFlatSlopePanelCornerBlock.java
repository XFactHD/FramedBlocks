package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedFlatSlopePanelCornerBlock extends FramedBlock
{
    public FramedFlatSlopePanelCornerBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.FRONT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.ROTATION, PropertyHolder.FRONT,
                FramedProperties.SOLID, FramedProperties.GLOWING, BlockStateProperties.WATERLOGGED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return getStateForPlacement(this, true, context);
    }

    public static BlockState getStateForPlacement(Block block, boolean hasFront, BlockPlaceContext context)
    {
        return getStateForPlacement(block, hasFront, true, context);
    }

    public static BlockState getStateForPlacement(Block block, boolean hasFront, boolean hasWater, BlockPlaceContext context)
    {
        Direction facing = context.getHorizontalDirection();

        Direction side = context.getClickedFace();
        HorizontalRotation rotation;
        if (side == facing.getOpposite())
        {
            rotation = HorizontalRotation.fromWallCorner(context.getClickLocation(), side);
        }
        else
        {
            rotation = HorizontalRotation.fromDirection(facing, side);
        }

        BlockState state = block.defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, facing)
                .setValue(PropertyHolder.ROTATION, rotation);
        if (hasFront)
        {
            boolean front = false;
            if (side.getAxis() != facing.getAxis())
            {
                Vec3 subHit = Utils.fraction(context.getClickLocation());
                double xz = Utils.isX(facing) ? subHit.x : subHit.z;
                front = (xz < .5) == Utils.isPositive(facing);
            }

            state = state.setValue(PropertyHolder.FRONT, front);
        }
        if (hasWater)
        {
            state = withWater(state, context.getLevel(), context.getClickedPos());
        }
        return state;
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            VoxelShape shape = Shapes.join(
                    FramedSlopePanelBlock.SHAPES.get(rot),
                    FramedSlopePanelBlock.SHAPES.get(rot.rotate(Rotation.COUNTERCLOCKWISE_90)),
                    BooleanOp.AND
            );
            if (state.getValue(PropertyHolder.FRONT))
            {
                shape = shape.move(0, 0, .5);
            }

            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, shape)
            );
        }

        return builder.build();
    }

    public static ImmutableMap<BlockState, VoxelShape> generateInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            VoxelShape shape = Shapes.or(
                    FramedSlopePanelBlock.SHAPES.get(rot),
                    FramedSlopePanelBlock.SHAPES.get(rot.rotate(Rotation.COUNTERCLOCKWISE_90))
            );
            if (state.getValue(PropertyHolder.FRONT))
            {
                shape = shape.move(0, 0, .5);
            }

            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, shape)
            );
        }

        return builder.build();
    }
}
