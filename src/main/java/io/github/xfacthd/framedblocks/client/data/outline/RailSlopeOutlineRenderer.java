package io.github.xfacthd.framedblocks.client.data.outline;

import io.github.xfacthd.framedblocks.api.render.outline.SimpleOutlineRenderer;
import io.github.xfacthd.framedblocks.common.block.SlopeBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class RailSlopeOutlineRenderer implements SimpleOutlineRenderer {
    public static final RailSlopeOutlineRenderer INSTANCE = new RailSlopeOutlineRenderer();

    private RailSlopeOutlineRenderer() { }

    @Override
    public void draw(BlockState state, LineDrawer drawer) {
        //Back edges
        drawer.drawLine(0, 0, 1, 0, 1, 1);
        drawer.drawLine(1, 0, 1, 1, 1, 1);

        //Bottom face
        drawer.drawLine(0, 0, 0, 0, 0, 1);
        drawer.drawLine(0, 0, 0, 1, 0, 0);
        drawer.drawLine(1, 0, 0, 1, 0, 1);
        drawer.drawLine(0, 0, 1, 1, 0, 1);

        //Top edge
        drawer.drawLine(0, 1, 1, 1, 1, 1);

        //Slope
        drawer.drawLine(0, 0, 0, 0, 1, 1);
        drawer.drawLine(1, 0, 0, 1, 1, 1);
    }

    @Override
    public Direction getRotationDir(BlockState state) {
        return ((SlopeBlock) state.getBlock()).getFacing(state);
    }
}
