package xfacthd.framedblocks.common.block.sign;

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
import xfacthd.framedblocks.api.block.*;
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
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) ->
                {
                    LevelReader level = modCtx.getLevel();
                    BlockPos pos = modCtx.getClickedPos();
                    Direction[] dirs = modCtx.getNearestLookingDirections();

                    for (Direction direction : dirs)
                    {
                        if (direction.getAxis().isHorizontal())
                        {
                            Direction dir = direction.getOpposite();
                            state = state.setValue(FramedProperties.FACING_HOR, dir);
                            if (state.canSurvive(level, pos))
                            {
                                return state;
                            }
                        }
                    }

                    return null;
                })
                .withWater()
                .build();
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
}