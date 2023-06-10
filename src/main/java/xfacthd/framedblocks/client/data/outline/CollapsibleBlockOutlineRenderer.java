package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import org.joml.Quaternionf;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.common.blockentity.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.data.property.NullableDirection;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class CollapsibleBlockOutlineRenderer implements OutlineRenderer
{
    private static final Quaternionf ROTATION = Axis.YN.rotationDegrees(180);

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        poseStack.mulPose(ROTATION);
    }

    @Override
    public void draw(BlockState state, Level level, BlockPos pos, PoseStack poseStack, VertexConsumer builder)
    {
        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        if (face == NullableDirection.NONE)
        {
            Shapes.block().forAllEdges((pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ) ->
                    OutlineRenderer.drawLine(builder, poseStack, pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ)
            );
        }
        else
        {
            if (!(level.getBlockEntity(pos) instanceof FramedCollapsibleBlockEntity be))
            {
                return;
            }

            byte[] offets = be.getVertexOffsets();
            Direction faceDir = face.toDirection().getOpposite();

            poseStack.pushPose();
            poseStack.translate(.5, .5, .5);
            if (faceDir == Direction.UP)
            {
                poseStack.mulPose(Quaternions.XP_180);
            }
            else if (faceDir != Direction.DOWN)
            {
                poseStack.mulPose(OutlineRenderer.YN_DIR[faceDir.getOpposite().get2DDataValue()]);
                poseStack.mulPose(Quaternions.XN_90);
            }
            poseStack.translate(-.5, -.5, -.5);

            //Top
            OutlineRenderer.drawLine(builder, poseStack, 0, 1D - (offets[2] / 16D), 0, 0, 1D - (offets[3] / 16D), 1);
            OutlineRenderer.drawLine(builder, poseStack, 0, 1D - (offets[2] / 16D), 0, 1, 1D - (offets[1] / 16D), 0);
            OutlineRenderer.drawLine(builder, poseStack, 1, 1D - (offets[1] / 16D), 0, 1, 1D - (offets[0] / 16D), 1);
            OutlineRenderer.drawLine(builder, poseStack, 0, 1D - (offets[3] / 16D), 1, 1, 1D - (offets[0] / 16D), 1);

            //Bottom
            OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 0, 0, 1);
            OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 0);
            OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0, 1, 0, 1);
            OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);

            //Vertical
            OutlineRenderer.drawLine(builder, poseStack, 1, 0, 1, 1, 1D - (offets[0] / 16D), 1);
            OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0, 1, 1D - (offets[1] / 16D), 0);
            OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 0, 1D - (offets[2] / 16D), 0);
            OutlineRenderer.drawLine(builder, poseStack, 0, 0, 1, 0, 1D - (offets[3] / 16D), 1);

            poseStack.popPose();
        }
    }

    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        throw new UnsupportedOperationException();
    }
}