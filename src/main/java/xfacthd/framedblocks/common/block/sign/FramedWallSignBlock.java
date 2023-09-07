package xfacthd.framedblocks.common.block.sign;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.common.data.BlockType;

@SuppressWarnings("deprecation")
public class FramedWallSignBlock extends AbstractFramedSignBlock
{
    private static final Vec3[] HITBOX_CENTERS = Util.make(new Vec3[4], arr ->
    {
        arr[Direction.NORTH.get2DDataValue()] = new Vec3(.5, .5, 15D/16D);
        arr[Direction.EAST.get2DDataValue()] = new Vec3(1D/16D, .5, .5);
        arr[Direction.SOUTH.get2DDataValue()] = new Vec3(.5, .5, 1D/16D);
        arr[Direction.WEST.get2DDataValue()] = new Vec3(15D/16D, .5, .5);
    });

    public FramedWallSignBlock()
    {
        super(BlockType.FRAMED_WALL_SIGN, IFramedBlock.createProperties(BlockType.FRAMED_WALL_SIGN).noCollission());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction[] dirs = context.getNearestLookingDirections();

        for (Direction direction : dirs)
        {
            if (direction.getAxis().isHorizontal())
            {
                Direction dir = direction.getOpposite();
                state = state.setValue(FramedProperties.FACING_HOR, dir);
                if (state.canSurvive(level, pos))
                {
                    return withWater(state, level, pos);
                }
            }
        }

        return null;
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction dir,
            BlockState facingState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos facingPos
    )
    {
        if (dir.getOpposite() == state.getValue(FramedProperties.FACING_HOR) && !state.canSurvive(level, pos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, dir, facingState, level, pos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR).getOpposite();
        return level.getBlockState(pos.relative(dir)).isSolid();
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        //Not rotatable by wrench
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }

    @Override
    public float getYRotationDegrees(BlockState state)
    {
        return state.getValue(FramedProperties.FACING_HOR).toYRot();
    }

    @Override
    public Vec3 getSignHitboxCenterPosition(BlockState state)
    {
        return HITBOX_CENTERS[state.getValue(FramedProperties.FACING_HOR).get2DDataValue()];
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            switch (state.getValue(FramedProperties.FACING_HOR))
            {
                case NORTH -> builder.put(state, box(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D));
                case EAST -> builder.put(state, box(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D));
                case SOUTH -> builder.put(state, box(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D));
                case WEST -> builder.put(state, box(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D));
            }
        }

        return ShapeProvider.of(builder.build());
    }
}