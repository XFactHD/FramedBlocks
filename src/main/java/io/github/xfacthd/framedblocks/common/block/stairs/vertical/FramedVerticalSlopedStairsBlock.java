package io.github.xfacthd.framedblocks.common.block.stairs.vertical;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
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

public class FramedVerticalSlopedStairsBlock extends FramedBlock implements SlopeToggleBlock {
    public FramedVerticalSlopedStairsBlock(BlockType type, Properties props) {
        super(type, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.ROTATION);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) -> {
                    Direction facing = modCtx.getHorizontalDirection();
                    state = state.setValue(FramedProperties.FACING_HOR, facing);

                    Direction face = modCtx.getClickedFace();
                    HorizontalRotation rot;
                    if (face == facing.getOpposite()) {
                        rot = HorizontalRotation.fromWallCorner(modCtx.getClickLocation(), face);
                    } else {
                        rot = HorizontalRotation.fromPerpendicularWallCorner(facing, face, modCtx.getClickLocation());
                    }
                    return state.setValue(PropertyHolder.ROTATION, rot);
                })
                .tryWithWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return switch (mode) {
            case PRIMARY -> HorizontalRotation.rotate(state, direction);
            case SECONDARY -> super.rotate(state, direction, mode);
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if ((mirror == Mirror.FRONT_BACK && DirUtils.isX(dir)) || (mirror == Mirror.LEFT_RIGHT && DirUtils.isZ(dir))) {
            state = state.setValue(FramedProperties.FACING_HOR, dir.getOpposite());
        }

        HorizontalRotation horRot = state.getValue(PropertyHolder.ROTATION);
        horRot = horRot.isVertical() ? horRot.rotate(Rotation.CLOCKWISE_90) : horRot.rotate(Rotation.COUNTERCLOCKWISE_90);
        return state.setValue(PropertyHolder.ROTATION, horRot);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state) {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return getItemModelSource();
    }
}
