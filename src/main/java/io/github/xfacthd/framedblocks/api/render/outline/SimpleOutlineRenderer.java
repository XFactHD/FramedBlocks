package io.github.xfacthd.framedblocks.api.render.outline;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Unit;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

public interface SimpleOutlineRenderer extends OutlineRenderer<Unit> {
    /**
     * Draw the outlines of the block. Provides access to the {@link BlockState} of the block being targeted,
     * sufficient for most blocks
     */
    void draw(BlockState state, LineDrawer drawer);

    @Override
    @ApiStatus.NonExtendable
    default Unit extractOutlineData(BlockState state, Level level, BlockPos pos) {
        return Unit.INSTANCE;
    }

    @Override
    @ApiStatus.NonExtendable
    default void draw(BlockState state, Unit data, LineDrawer drawer) {
        draw(state, drawer);
    }
}
