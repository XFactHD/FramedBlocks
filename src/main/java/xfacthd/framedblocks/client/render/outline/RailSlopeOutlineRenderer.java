package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.render.BlockOutlineRenderer;
import xfacthd.framedblocks.common.block.FramedRailSlopeBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class RailSlopeOutlineRenderer implements OutlineRender
{
    @Override
    public void draw(BlockState state, MatrixStack poseStack, IVertexBuilder builder)
    {
        BlockOutlineRenderer.drawSlopeBox(state, poseStack, builder);
    }

    @Override
    public Direction getRotationDir(BlockState state)
    {
        return FramedRailSlopeBlock.directionFromShape(state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE));
    }
}
