package io.github.xfacthd.framedblocks.api.render.outline;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Unit;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

/// Base interface for outline renderers which only need the blockstate for
/// rendering the block's outline.
public interface SimpleOutlineRenderer extends OutlineRenderer<Unit> {
    /// Draw the outlines of the block.
    ///
    /// @param state  The blockstate of the targetted block
    /// @param drawer The line drawer to submit line segments to
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
