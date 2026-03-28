package io.github.xfacthd.framedblocks.common.block;

import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.util.FramedUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public interface SlopeBlock {
    Direction getFacing(BlockState state);

    SlopeType getSlopeType(BlockState state);

    interface RailSlopeBlock extends SlopeBlock {
        @Override
        default Direction getFacing(BlockState state) {
            RailShape shape = state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
            return FramedUtils.getDirectionFromAscendingRailShape(shape);
        }

        @Override
        default SlopeType getSlopeType(BlockState state) {
            return SlopeType.BOTTOM;
        }
    }
}
