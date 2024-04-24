package xfacthd.framedblocks.client.render.special;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.render.util.FramedRenderTypes;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.NullableDirection;

public final class CollapsibleBlockIndicatorRenderer
{
    private static final double[] VERTEX_NO_OFFSET = new double[] { 1D, 1D, 1D, 1D };

    public static void onRenderBlockHighlight(final RenderHighlightEvent.Block event)
    {
        //noinspection ConstantConditions
        ItemStack heldItem = Minecraft.getInstance().player.getMainHandItem();
        if (heldItem.getItem() != FBContent.ITEM_FRAMED_HAMMER.value())
        {
            return;
        }

        BlockHitResult hit = event.getTarget();
        Level level = Minecraft.getInstance().level;
        //noinspection ConstantConditions
        BlockState state = level.getBlockState(hit.getBlockPos());
        if (state.getBlock() != FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK.value())
        {
            return;
        }

        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        Direction faceDir = hit.getDirection();
        if (face != NullableDirection.NONE && face.toDirection() != faceDir)
        {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Vec3 offset = Vec3.atLowerCornerOf(hit.getBlockPos()).subtract(event.getCamera().getPosition());
        VertexConsumer builder = event.getMultiBufferSource().getBuffer(FramedRenderTypes.LINES_NO_DEPTH);

        poseStack.pushPose();
        poseStack.translate(offset.x + .5, offset.y + .5, offset.z + .5);
        if (faceDir == Direction.DOWN)
        {
            poseStack.mulPose(Quaternions.XP_180);
        }
        else if (faceDir != Direction.UP)
        {
            poseStack.mulPose(OutlineRenderer.YN_DIR[faceDir.get2DDataValue()]);
            poseStack.mulPose(Quaternions.XP_90);
        }
        poseStack.translate(-.5, -.5, -.5);

        double[] vY = getVertexHeights(level, hit.getBlockPos(), face);
        drawSectionOverlay(builder, poseStack, vY);
        drawCornerMarkers(builder, poseStack, faceDir, hit, vY);

        poseStack.popPose();

        ((MultiBufferSource.BufferSource) event.getMultiBufferSource()).endBatch(FramedRenderTypes.LINES_NO_DEPTH);
    }

    private static double[] getVertexHeights(Level level, BlockPos pos, NullableDirection face)
    {
        if (face == NullableDirection.NONE || !(level.getBlockEntity(pos) instanceof FramedCollapsibleBlockEntity be))
        {
            return VERTEX_NO_OFFSET;
        }
        byte[] offsets = be.getVertexOffsets();
        return new double[] {
                1D - (offsets[0] / 16D),
                1D - (offsets[1] / 16D),
                1D - (offsets[2] / 16D),
                1D - (offsets[3] / 16D)
        };
    }

    private static void drawSectionOverlay(VertexConsumer builder, PoseStack poseStack, double[] vY)
    {
        double cenx = Mth.lerp(.5, vY[0], vY[1]); // center edge negative X
        double cepx = Mth.lerp(.5, vY[3], vY[2]); // center edge positive X
        double cenz = Mth.lerp(.5, vY[0], vY[3]); // center edge negative Z
        double cepz = Mth.lerp(.5, vY[1], vY[2]); // center edge positive Z

        double cinx = Mth.lerp(.25, cenx, cepx); // center inset negative X
        double cipx = Mth.lerp(.75, cenx, cepx); // center inset positive X
        double cinz = Mth.lerp(.25, cenz, cepz); // center inset negative Z
        double cipz = Mth.lerp(.75, cenz, cepz); // center inset positive Z

        drawLine(builder, poseStack,  .5, cenz,   0,  .5, cinz, .25);
        drawLine(builder, poseStack,  .5, cipz, .75,  .5, cepz,   1);
        drawLine(builder, poseStack,   0, cenx,  .5, .25, cinx,  .5);
        drawLine(builder, poseStack, .75, cipx,  .5,   1, cepx,  .5);

        drawLine(builder, poseStack, .5, cinz, .25, .25, cinx, .5);
        drawLine(builder, poseStack, .5, cinz, .25, .75, cipx, .5);
        drawLine(builder, poseStack, .5, cipz, .75, .25, cinx, .5);
        drawLine(builder, poseStack, .5, cipz, .75, .75, cipx, .5);
    }

    private static void drawCornerMarkers(
            VertexConsumer builder, PoseStack poseStack, Direction faceDir, BlockHitResult hit, double[] vY
    )
    {
        int vert = FramedCollapsibleBlockEntity.vertexFromHit(faceDir, Utils.fraction(hit.getLocation()));
        if (vert == 0 || vert == 4)
        {
            drawCubeFrame(builder, poseStack,  0.25/16D,  0.25/16D, vY[0]);
        }
        if (vert == 1 || vert == 4)
        {
            drawCubeFrame(builder, poseStack,  0.25/16D, 15.75/16D, vY[1]);
        }
        if (vert == 2 || vert == 4)
        {
            drawCubeFrame(builder, poseStack, 15.75/16D, 15.75/16D, vY[2]);
        }
        if (vert == 3 || vert == 4)
        {
            drawCubeFrame(builder, poseStack, 15.75/16D,  0.25/16D, vY[3]);
        }
    }

    private static void drawCubeFrame(
            VertexConsumer builder, PoseStack poseStack, double x, double z, double vY
    )
    {
        double minX = x - .5D/16D;
        double maxX = x + .5D/16D;
        double minZ = z - .5D/16D;
        double maxZ = z + .5D/16D;

        double minY = vY - .75/16D;
        double maxY = vY + .25/16D;

        // Bottom
        drawLine(builder, poseStack, minX, minY, minZ, minX, minY, maxZ);
        drawLine(builder, poseStack, minX, minY, minZ, maxX, minY, minZ);
        drawLine(builder, poseStack, maxX, minY, minZ, maxX, minY, maxZ);
        drawLine(builder, poseStack, minX, minY, maxZ, maxX, minY, maxZ);

        // Top
        drawLine(builder, poseStack, minX, maxY, minZ, minX, maxY, maxZ);
        drawLine(builder, poseStack, minX, maxY, minZ, maxX, maxY, minZ);
        drawLine(builder, poseStack, maxX, maxY, minZ, maxX, maxY, maxZ);
        drawLine(builder, poseStack, minX, maxY, maxZ, maxX, maxY, maxZ);

        // Vertical
        drawLine(builder, poseStack, minX, minY, minZ, minX, maxY, minZ);
        drawLine(builder, poseStack, minX, minY, maxZ, minX, maxY, maxZ);
        drawLine(builder, poseStack, maxX, minY, minZ, maxX, maxY, minZ);
        drawLine(builder, poseStack, maxX, minY, maxZ, maxX, maxY, maxZ);
    }

    private static void drawLine(
            VertexConsumer builder, PoseStack poseStack, double x1, double y1, double z1, double x2, double y2, double z2
    )
    {
        float nX = (float)(x2 - x1);
        float nY = (float)(y2 - y1);
        float nZ = (float)(z2 - z1);
        float nLen = Mth.sqrt(nX * nX + nY * nY + nZ * nZ);

        nX = nX / nLen;
        nY = nY / nLen;
        nZ = nZ / nLen;

        PoseStack.Pose pose = poseStack.last();
        builder.vertex(pose, (float)x1, (float)y1, (float)z1).color(1f, 0F, 0F, .6F).normal(pose, nX, nY, nZ).endVertex();
        builder.vertex(pose, (float)x2, (float)y2, (float)z2).color(1f, 0F, 0F, .6F).normal(pose, nX, nY, nZ).endVertex();
    }



    private CollapsibleBlockIndicatorRenderer() { }
}
