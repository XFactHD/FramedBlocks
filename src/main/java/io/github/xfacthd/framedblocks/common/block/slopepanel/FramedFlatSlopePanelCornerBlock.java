package io.github.xfacthd.framedblocks.common.block.slopepanel;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedFlatSlopePanelCornerBlock extends FramedBlock implements SlopeToggleBlock
{
    public FramedFlatSlopePanelCornerBlock(BlockType type, Properties props)
    {
        super(type, props);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.FRONT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.ROTATION, PropertyHolder.FRONT);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return getStateForPlacement(this, true, context);
    }

    @Nullable
    public static BlockState getStateForPlacement(Block block, boolean hasFront, BlockPlaceContext context)
    {
        ExtPlacementStateBuilder builder = ExtPlacementStateBuilder.of(block, context)
                .withHorizontalFacing()
                .withCornerOrSideRotation();

        if (hasFront)
        {
            builder = builder.withFront();
        }

        return builder.tryWithWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        return switch (mode)
        {
            case PRIMARY -> HorizontalRotation.rotate(state, direction);
            case SECONDARY -> super.rotate(state, direction, mode);
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return mirrorCorner(state, mirror);
    }

    public static BlockState mirrorCorner(BlockState state, Mirror mirror)
    {
        BlockState newState = BlockUtils.mirrorFaceBlock(state, mirror);
        if (newState != state)
        {
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            boolean vert = rot.isVertical();

            rot = rot.rotate(vert ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);

            return newState.setValue(PropertyHolder.ROTATION, rot);
        }
        return state;
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.ROTATION, HorizontalRotation.RIGHT);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
