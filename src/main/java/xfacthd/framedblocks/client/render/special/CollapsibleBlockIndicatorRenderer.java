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
    private static final float[] VERTEX_NO_OFFSET = new float[] { 1F, 1F, 1F, 1F };

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

        float[] vY = getVertexHeights(level, hit.getBlockPos(), face);
        drawSectionOverlay(builder, poseStack, vY);
        drawCornerMarkers(builder, poseStack, faceDir, hit, vY);

        poseStack.popPose();

        ((MultiBufferSource.BufferSource) event.getMultiBufferSource()).endBatch(FramedRenderTypes.LINES_NO_DEPTH);
    }

    private static float[] getVertexHeights(Level level, BlockPos pos, NullableDirection face)
    {
        if (face == NullableDirection.NONE || !(level.getBlockEntity(pos) instanceof FramedCollapsibleBlockEntity be))
        {
            return VERTEX_NO_OFFSET;
        }
        byte[] offsets = be.getVertexOffsets();
        return new float[] {
                1F - (offsets[0] / 16F),
                1F - (offsets[1] / 16F),
                1F - (offsets[2] / 16F),
                1F - (offsets[3] / 16F)
        };
    }

    private static void drawSectionOverlay(VertexConsumer builder, PoseStack poseStack, float[] vY)
    {
        float cenx = Mth.lerp(.5F, vY[0], vY[1]); // center edge negative X
        float cepx = Mth.lerp(.5F, vY[3], vY[2]); // center edge positive X
        float cenz = Mth.lerp(.5F, vY[0], vY[3]); // center edge negative Z
        float cepz = Mth.lerp(.5F, vY[1], vY[2]); // center edge positive Z

        float cinx = Mth.lerp(.25F, cenx, cepx); // center inset negative X
        float cipx = Mth.lerp(.75F, cenx, cepx); // center inset positive X
        float cinz = Mth.lerp(.25F, cenz, cepz); // center inset negative Z
        float cipz = Mth.lerp(.75F, cenz, cepz); // center inset positive Z

        drawLine(builder, poseStack,  .5F, cenz,   0F,  .5F, cinz, .25F);
        drawLine(builder, poseStack,  .5F, cipz, .75F,  .5F, cepz,   1F);
        drawLine(builder, poseStack,   0F, cenx,  .5F, .25F, cinx,  .5F);
        drawLine(builder, poseStack, .75F, cipx,  .5F,   1F, cepx,  .5F);

        drawLine(builder, poseStack, .5F, cinz, .25F, .25F, cinx, .5F);
        drawLine(builder, poseStack, .5F, cinz, .25F, .75F, cipx, .5F);
        drawLine(builder, poseStack, .5F, cipz, .75F, .25F, cinx, .5F);
        drawLine(builder, poseStack, .5F, cipz, .75F, .75F, cipx, .5F);
    }

    private static void drawCornerMarkers(
            VertexConsumer builder, PoseStack poseStack, Direction faceDir, BlockHitResult hit, float[] vY
    )
    {
        int vert = FramedCollapsibleBlockEntity.vertexFromHit(faceDir, Utils.fraction(hit.getLocation()));
        if (vert == 0 || vert == 4)
        {
            drawCubeFrame(builder, poseStack,  0.25F/16F,  0.25F/16F, vY[0]);
        }
        if (vert == 1 || vert == 4)
        {
            drawCubeFrame(builder, poseStack,  0.25F/16F, 15.75F/16F, vY[1]);
        }
        if (vert == 2 || vert == 4)
        {
            drawCubeFrame(builder, poseStack, 15.75F/16F, 15.75F/16F, vY[2]);
        }
        if (vert == 3 || vert == 4)
        {
            drawCubeFrame(builder, poseStack, 15.75F/16F,  0.25F/16F, vY[3]);
        }
    }

    private static void drawCubeFrame(VertexConsumer builder, PoseStack poseStack, float x, float z, float vY)
    {
        float minX = x - .5F/16F;
        float maxX = x + .5F/16F;
        float minZ = z - .5F/16F;
        float maxZ = z + .5F/16F;

        float minY = vY - .75F/16F;
        float maxY = vY + .25F/16F;

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
            VertexConsumer builder, PoseStack poseStack, float x1, float y1, float z1, float x2, float y2, float z2
    )
    {
        float nX = x2 - x1;
        float nY = y2 - y1;
        float nZ = z2 - z1;
        float nLen = Mth.sqrt(nX * nX + nY * nY + nZ * nZ);

        nX = nX / nLen;
        nY = nY / nLen;
        nZ = nZ / nLen;

        PoseStack.Pose pose = poseStack.last();
        builder.addVertex(pose, x1, y1, z1).setColor(255, 0, 0, 153).setNormal(pose, nX, nY, nZ);
        builder.addVertex(pose, x2, y2, z2).setColor(255, 0, 0, 153).setNormal(pose, nX, nY, nZ);
    }



    private CollapsibleBlockIndicatorRenderer() { }
}
