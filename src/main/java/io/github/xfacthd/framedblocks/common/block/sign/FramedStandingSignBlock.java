package io.github.xfacthd.framedblocks.common.block.sign;

import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.item.block.FramedSignItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class FramedStandingSignBlock extends AbstractFramedSignBlock
{
    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public FramedStandingSignBlock(Properties props)
    {
        super(BlockType.FRAMED_SIGN, props.noCollision());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.ROTATION_16);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) ->
                {
                    int rotation = RotationSegment.convertToSegment(modCtx.getRotation() + 180.0F);
                    return state.setValue(BlockStateProperties.ROTATION_16, rotation);
                })
                .withWater()
                .build();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return SHAPE;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state)
    {
        return Shapes.empty();
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
        if (side == Direction.DOWN && !canSurvive(state, level, pos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, tickAccess, pos, side, adjPos, adjState, random);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return level.getBlockState(pos.below()).isSolid();
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        return false;
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        rotation += switch (direction)
        {
            case CLOCKWISE -> 1;
            case COUNTERCLOCKWISE -> 15;
        };
        return state.setValue(BlockStateProperties.ROTATION_16, rotation % 16);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        int rotationStep = state.getValue(BlockStateProperties.ROTATION_16);
        return state.setValue(BlockStateProperties.ROTATION_16, rotation.rotate(rotationStep, 16));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        int rot = state.getValue(BlockStateProperties.ROTATION_16);
        return state.setValue(BlockStateProperties.ROTATION_16, mirror.mirror(rot, 16));
    }

    @Override
    public float getYRotationDegrees(BlockState state)
    {
        return RotationSegment.convertToDegrees(state.getValue(BlockStateProperties.ROTATION_16));
    }

    @Override
    public BlockItem createBlockItem(Item.Properties props)
    {
        return new FramedSignItem(props);
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
        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        return Direction.from2DDataValue(rotation / 4);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }
}
