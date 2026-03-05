package io.github.xfacthd.framedblocks.common.block.sign;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FramedWallSignBlock extends AbstractFramedSignBlock
{
    private static final Vec3[] HITBOX_CENTERS = Util.make(new Vec3[4], arr ->
    {
        arr[Direction.NORTH.get2DDataValue()] = new Vec3(.5, .5, 15D/16D);
        arr[Direction.EAST.get2DDataValue()] = new Vec3(1D/16D, .5, .5);
        arr[Direction.SOUTH.get2DDataValue()] = new Vec3(.5, .5, 1D/16D);
        arr[Direction.WEST.get2DDataValue()] = new Vec3(15D/16D, .5, .5);
    });

    public FramedWallSignBlock(Properties props)
    {
        super(BlockType.FRAMED_WALL_SIGN, props.noCollision());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR);
    }

    @Override
    @Nullable
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
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess tickAccess,
            BlockPos pos,
            Direction side,
            BlockPos adjPos,
            BlockState adjState,
            RandomSource random
    )
    {
        if (side.getOpposite() == state.getValue(FramedProperties.FACING_HOR) && !state.canSurvive(level, pos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, tickAccess, pos, side, adjPos, adjState, random);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR).getOpposite();
        return level.getBlockState(pos.relative(dir)).isSolid();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        //Not rotatable by wrench
        return state;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return BlockUtils.rotate(state, rotation);
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

    @Override
    @Nullable
    public BlockState getItemModelSource()
    {
        return null;
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }
}
