package io.github.xfacthd.framedblocks.common.block.prism;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.prism.FramedElevatedDoubleSlopedPrismBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedElevatedDoubleSlopedPrismBlock extends FramedDoubleBlock implements PrismBlock, SlopeToggleBlock {
    public FramedElevatedDoubleSlopedPrismBlock(BlockType type, Properties props) {
        super(type, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.FACING_DIR);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return FramedSlopedPrismBlock.getStateForPlacement(context, this);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return state.setValue(PropertyHolder.FACING_DIR, cmpDir.rotate(rotation));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return state.setValue(PropertyHolder.FACING_DIR, cmpDir.mirror(mirror));
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        return new DoubleBlockParts(
                FBContent.BLOCK_FRAMED_ELEVATED_INNER_SLOPED_PRISM.value()
                        .defaultBlockState()
                        .setValue(PropertyHolder.FACING_DIR, cmpDir)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope),
                FBContent.BLOCK_FRAMED_SLOPED_PRISM.value()
                        .defaultBlockState()
                        .setValue(PropertyHolder.FACING_DIR, CompoundDirection.of(
                                cmpDir.direction().getOpposite(),
                                cmpDir.orientation()
                        ))
                        .setValue(FramedProperties.ALT_SLOPE, altSlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        if (cmpDir.direction() == Direction.UP) {
            return DoubleBlockTopInteractionMode.SECOND;
        } else if (cmpDir.direction() == Direction.DOWN || cmpDir.orientation() != Direction.UP) {
            return DoubleBlockTopInteractionMode.FIRST;
        }
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        Direction facing = cmpDir.direction();
        if (side == facing) {
            return CamoGetter.SECOND;
        }
        if (side == cmpDir.orientation()) {
            if (edge == facing) {
                return CamoGetter.SECOND;
            } else if (edge != null) {
                return CamoGetter.FIRST;
            }
            return CamoGetter.NONE;
        }
        return CamoGetter.FIRST;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        Direction facing = cmpDir.direction();
        if (side == facing) {
            return SolidityCheck.SECOND;
        }
        if (side == cmpDir.orientation()) {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.FIRST;
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedElevatedDoubleSlopedPrismBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource() {
        boolean inner = getBlockType() == BlockType.FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM;
        CompoundDirection cmpDir = inner ? CompoundDirection.UP_EAST : CompoundDirection.UP_WEST;
        return defaultBlockState().setValue(PropertyHolder.FACING_DIR, cmpDir);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        if (!DirUtils.isY(cmpDir.direction())) {
            return cmpDir.direction();
        }
        if (!DirUtils.isY(cmpDir.orientation())) {
            return cmpDir.orientation();
        }
        return Direction.NORTH;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return getItemModelSource();
    }

    @Override
    public boolean isInnerPrism() {
        return getBlockType() == BlockType.FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM;
    }
}
