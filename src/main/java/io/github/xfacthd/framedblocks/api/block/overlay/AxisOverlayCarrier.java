package io.github.xfacthd.framedblocks.api.block.overlay;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface AxisOverlayCarrier
{
    Direction.Axis getAxis(BlockState state);
}
