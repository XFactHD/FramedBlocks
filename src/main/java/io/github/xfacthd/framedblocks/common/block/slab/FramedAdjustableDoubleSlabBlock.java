package io.github.xfacthd.framedblocks.common.block.slab;

import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class FramedAdjustableDoubleSlabBlock extends FramedAdjustableDoubleBlock {
    public FramedAdjustableDoubleSlabBlock(BlockType type, Properties props) {
        super(type, props, _ -> Direction.UP);
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        return switch (side) {
            case DOWN -> SolidityCheck.FIRST;
            case UP -> SolidityCheck.SECOND;
            case NORTH, SOUTH, WEST, EAST -> SolidityCheck.BOTH;
        };
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        return switch (side) {
            case DOWN -> CamoGetter.FIRST;
            case UP -> CamoGetter.SECOND;
            case NORTH, SOUTH, WEST, EAST -> {
                if (edge == Direction.DOWN) {
                    yield CamoGetter.FIRST;
                }
                if (edge == Direction.UP) {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
        };
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.UNSUPPORTED;
    }
}
