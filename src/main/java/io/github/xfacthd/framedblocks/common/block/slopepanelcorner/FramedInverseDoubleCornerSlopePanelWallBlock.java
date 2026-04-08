package io.github.xfacthd.framedblocks.common.block.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
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

public class FramedInverseDoubleCornerSlopePanelWallBlock extends FramedDoubleBlock implements SlopeToggleBlock {
    public FramedInverseDoubleCornerSlopePanelWallBlock(Properties props) {
        super(BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W, props);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.ALT_SLOPE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.ROTATION);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return FramedCornerSlopePanelWallBlock.getStateForPlacement(this, ctx, true);
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
        return FramedCornerSlopePanelWallBlock.mirrorCornerPanel(state, mirror);
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        HorizontalRotation backRot = rot.rotate(rot.isVertical() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        return new DoubleBlockParts(
                FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL_WALL.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, dir)
                        .setValue(PropertyHolder.ROTATION, rot)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope),
                FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_WALL.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, dir.getOpposite())
                        .setValue(PropertyHolder.ROTATION, backRot)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        if (rot == HorizontalRotation.UP || rot == HorizontalRotation.RIGHT) {
            return DoubleBlockTopInteractionMode.BOTH;
        }
        return DoubleBlockTopInteractionMode.FIRST;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(dir).getOpposite();
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir).getOpposite();
        if (side == dir && (edge == rotDir || edge == perpRotDir)) {
            return CamoGetter.FIRST;
        }
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        return SolidityCheck.NONE;
    }

    @Override
    public @Nullable BlockState getItemModelSource() {
        return null;
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state) {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return ((IFramedBlock) FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL.value()).getJadeRenderState(state);
    }
}
