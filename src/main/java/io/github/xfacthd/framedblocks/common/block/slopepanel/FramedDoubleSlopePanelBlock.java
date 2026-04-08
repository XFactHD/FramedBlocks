package io.github.xfacthd.framedblocks.common.block.slopepanel;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.block.render.NullCullPredicate;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slopepanel.FramedDoubleSlopePanelBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedDoubleSlopePanelBlock extends FramedDoubleBlock implements SlopeToggleBlock {
    public static final NullCullPredicate NULL_CULL_PREDICATE = new NullCullPredicate(
            state -> !state.getValue(PropertyHolder.FRONT),
            state -> state.getValue(PropertyHolder.FRONT)
    );

    public FramedDoubleSlopePanelBlock(Properties props) {
        super(BlockType.FRAMED_DOUBLE_SLOPE_PANEL, props);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.FRONT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.ROTATION, PropertyHolder.FRONT);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return FramedSlopePanelBlock.getStateForPlacement(this, context);
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
        return FramedSlopePanelBlock.mirrorPanel(state, mirror);
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        boolean front = state.getValue(PropertyHolder.FRONT);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        BlockState defState = FBContent.BLOCK_FRAMED_SLOPE_PANEL.value().defaultBlockState();
        return new DoubleBlockParts(
                defState.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.ROTATION, rotation)
                        .setValue(PropertyHolder.FRONT, front)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope),
                defState.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.ROTATION, rotation.isVertical() ? rotation.getOpposite() : rotation)
                        .setValue(PropertyHolder.FRONT, !front)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        return switch (state.getValue(PropertyHolder.ROTATION)) {
            case LEFT, RIGHT -> DoubleBlockTopInteractionMode.BOTH;
            case UP -> DoubleBlockTopInteractionMode.SECOND;
            case DOWN -> DoubleBlockTopInteractionMode.FIRST;
        };
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean front = state.getValue(PropertyHolder.FRONT);

        if (side == facing) {
            return front ? CamoGetter.NONE : CamoGetter.FIRST;
        } else if (side == facing.getOpposite()) {
            return front ? CamoGetter.SECOND : CamoGetter.NONE;
        }

        if ((!front && edge == facing) || (front && edge == facing.getOpposite())) {
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            Direction orientation = rot.withFacing(facing);
            Direction perpOrientation = rot.rotate(Rotation.CLOCKWISE_90).withFacing(facing);
            if (side == orientation || (side.getAxis() == perpOrientation.getAxis() && front)) {
                return CamoGetter.SECOND;
            } else if (side == orientation.getOpposite() || (side.getAxis() == perpOrientation.getAxis())) {
                return CamoGetter.FIRST;
            }
        }

        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean front = state.getValue(PropertyHolder.FRONT);

        if (!front && side == facing) {
            return SolidityCheck.FIRST;
        } else if (front && side == facing.getOpposite()) {
            return SolidityCheck.SECOND;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedDoubleSlopePanelBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state) {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
