package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.CtmPredicate;
import xfacthd.framedblocks.common.util.Utils;

import java.util.EnumMap;
import java.util.Map;

public class FramedSlopePanelBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        if (state.getValue(PropertyHolder.FRONT))
        {
            return false;
        }
        return side == state.getValue(PropertyHolder.FACING_HOR);
    };

    public FramedSlopePanelBlock()
    {
        super(BlockType.FRAMED_SLOPE_PANEL);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.FRONT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.ROTATION, PropertyHolder.FRONT, PropertyHolder.SOLID, PropertyHolder.GLOWING, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) { return getStateForPlacement(this, context); }

    public static BlockState getStateForPlacement(Block block, BlockItemUseContext context)
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

        boolean front = false;
        if (side.getAxis() != facing.getAxis())
        {
            Vector3d subHit = Utils.fraction(context.getClickLocation());
            double xz = Utils.isX(facing) ? subHit.x : subHit.z;
            front = (xz < .5) == Utils.isPositive(facing);
        }

        BlockState state = block.defaultBlockState()
                .setValue(PropertyHolder.FACING_HOR, facing)
                .setValue(PropertyHolder.ROTATION, rotation)
                .setValue(PropertyHolder.FRONT, front);
        return withWater(state, context.getLevel(), context.getClickedPos());
    }



    public static final Map<Rotation, VoxelShape> SHAPES = Util.make(new EnumMap<>(Rotation.class), map ->
    {
        map.put(Rotation.UP, VoxelShapes.or(
                box(0, 0, 0, 16, .5, 8),
                box(0, .5, 0, 16, 4, 7.75),
                box(0, 4, 0, 16, 8, 6),
                box(0, 8, 0, 16, 12, 4),
                box(0, 12, 0, 16, 15, 2),
                box(0, 15, 0, 16, 16, 0.5)
        ).optimize());

        map.put(Rotation.RIGHT, VoxelShapes.or(
                box(0, 0, 0, .5, 16, 8),
                box(.5, 0, 0, 4, 16, 7.75),
                box(4, 0, 0, 8, 16, 6),
                box(8, 0, 0, 12, 16, 4),
                box(12, 0, 0, 15, 16, 2),
                box(15, 0, 0, 16, 16, 0.5)
        ).optimize());

        map.put(Rotation.DOWN, VoxelShapes.or(
                box(0, 15.5, 0, 16, 16, 8),
                box(0, 12, 0, 16, 15.5, 7.75),
                box(0, 8, 0, 16, 12, 6),
                box(0, 4, 0, 16, 8, 4),
                box(0, 1, 0, 16, 4, 2),
                box(0, 0, 0, 16, 1, 0.5)
        ).optimize());

        map.put(Rotation.LEFT, VoxelShapes.or(
                box(15.5, 0, 0, 16, 16, 8),
                box(12, 0, 0, 15.5, 16, 7.75),
                box(8, 0, 0, 12, 16, 6),
                box(4, 0, 0, 8, 16, 4),
                box(1, 0, 0, 4, 16, 2),
                box(0, 0, 0, 1, 16, 0.5)
        ).optimize());
    });

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            VoxelShape shape = SHAPES.get(state.getValue(PropertyHolder.ROTATION));
            if (state.getValue(PropertyHolder.FRONT))
            {
                shape = shape.move(0, 0, .5);
            }

            Direction facing = state.getValue(PropertyHolder.FACING_HOR);
            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, shape)
            );
        }

        return builder.build();
    }
}
