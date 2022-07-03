package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.util.client.OutlineRender;
import xfacthd.framedblocks.common.blockentity.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.data.property.CollapseFace;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class CollapsibleBlockOutlineRenderer implements OutlineRender
{
    private static final Quaternion ROTATION = Vector3f.YN.rotationDegrees(180);

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state) { poseStack.mulPose(ROTATION); }

    @Override
    public void draw(BlockState state, Level level, BlockPos pos, PoseStack poseStack, VertexConsumer builder)
    {
        CollapseFace face = state.getValue(PropertyHolder.COLLAPSED_FACE);
        if (face == CollapseFace.NONE)
        {
            Shapes.block().forAllEdges((pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ) -> OutlineRender.drawLine(builder, poseStack, pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ));
        }
        else
        {
            if (!(level.getBlockEntity(pos) instanceof FramedCollapsibleBlockEntity be)) { return; }

            byte[] offets = be.getVertexOffsets();
            Direction faceDir = face.toDirection().getOpposite();

            poseStack.pushPose();
            poseStack.translate(.5, .5, .5);
            if (faceDir == Direction.UP)
            {
                poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
            }
            else if (faceDir != Direction.DOWN)
            {
                poseStack.mulPose(Vector3f.YN.rotationDegrees(faceDir.toYRot() + 180F));
                poseStack.mulPose(Vector3f.XN.rotationDegrees(90));
            }
            poseStack.translate(-.5, -.5, -.5);

            //Top
            OutlineRender.drawLine(builder, poseStack, 0, 1D - (offets[2] / 16D), 0, 0, 1D - (offets[3] / 16D), 1);
            OutlineRender.drawLine(builder, poseStack, 0, 1D - (offets[2] / 16D), 0, 1, 1D - (offets[1] / 16D), 0);
            OutlineRender.drawLine(builder, poseStack, 1, 1D - (offets[1] / 16D), 0, 1, 1D - (offets[0] / 16D), 1);
            OutlineRender.drawLine(builder, poseStack, 0, 1D - (offets[3] / 16D), 1, 1, 1D - (offets[0] / 16D), 1);

            //Bottom
            OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, 0, 1);
            OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 0);
            OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, 0, 1);
            OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);

            //Vertical
            OutlineRender.drawLine(builder, poseStack, 1, 0, 1, 1, 1D - (offets[0] / 16D), 1);
            OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, 1D - (offets[1] / 16D), 0);
            OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, 1D - (offets[2] / 16D), 0);
            OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 0, 1D - (offets[3] / 16D), 1);

            poseStack.popPose();
        }
    }

    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder) { throw new UnsupportedOperationException(); }
}