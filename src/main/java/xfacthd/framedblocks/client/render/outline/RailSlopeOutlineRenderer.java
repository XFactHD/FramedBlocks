package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.client.OutlineRender;
import xfacthd.framedblocks.client.render.BlockOutlineRenderer;
import xfacthd.framedblocks.common.block.FramedRailSlopeBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class RailSlopeOutlineRenderer implements OutlineRender
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        BlockOutlineRenderer.drawSlopeBox(state, poseStack, builder);
    }

    @Override
    public Direction getRotationDir(BlockState state)
    {
        return FramedRailSlopeBlock.directionFromShape(state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE));
    }
}
