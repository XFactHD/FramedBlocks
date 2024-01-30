package xfacthd.framedblocks.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.cache.StateCache;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockStateCache;

public class FramedBlockConnectionDebugRenderer implements BlockEntityRenderer<FramedBlockEntity>
{
    private static float dummyU0 = 0F;
    private static float dummyU1 = 1F;
    private static float dummyV0 = 0F;
    private static float dummyV1 = 1F;

    public FramedBlockConnectionDebugRenderer(@SuppressWarnings("unused") BlockEntityRendererProvider.Context ctx) { }

    @Override
    public void render(
            FramedBlockEntity be,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    )
    {
        HitResult hit = Minecraft.getInstance().hitResult;
        if (!(hit instanceof BlockHitResult blockHit) || !blockHit.getBlockPos().equals(be.getBlockPos()))
        {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(.5, .5, .5);

        Direction face = blockHit.getDirection();
        StateCache cache = be.getBlock().getCache(be.getBlockState());
        switch (face)
        {
            case UP ->
            {
                poseStack.mulPose(Quaternions.XN_90);
                renderIndicators(buffer, poseStack, cache, face, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
            }
            case DOWN ->
            {
                poseStack.mulPose(Quaternions.XP_90);
                renderIndicators(buffer, poseStack, cache, face, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST);
            }
            default ->
            {
                poseStack.mulPose(Axis.YN.rotationDegrees(face.toYRot()));
                renderIndicators(buffer, poseStack, cache, face, Direction.UP, Direction.DOWN, face.getCounterClockWise(), face.getClockWise());
            }
        }

        poseStack.popPose();
    }

    private static void renderIndicators(
            MultiBufferSource bufferSource,
            PoseStack poseStack,
            StateCache cache,
            Direction face,
            Direction upEdge,
            Direction downEdge,
            Direction ccwEdge,
            Direction cwEdge
    )
    {
        DoubleBlockStateCache doubleCache = cache instanceof DoubleBlockStateCache dbCache ? dbCache : null;
        VertexConsumer buffer = bufferSource.getBuffer(Sheets.solidBlockSheet());

        // Null / all edges
        int color = cache.canConnectFullEdge(face, null) ? 0xFF00FF00 : 0xFFFF0000;
        renderBorderedIndicator(buffer, poseStack, -.125F, .125F, -.125F, .125F, .501F, color);
        if (doubleCache != null)
        {
            color = doubleCache.getCamoGetter(face, null) != CamoGetter.NONE ? 0xFF00FF00 : 0xFFFF0000;
            renderBorderedIndicator(buffer, poseStack, -.1875F, .1875F, -.1875F, .1875F, color);
        }

        // Up edge
        color = cache.canConnectFullEdge(face, upEdge) ? 0xFF00FF00 : (cache.canConnectDetailed(face, upEdge) ? 0xFFFFAA00 : 0xFFFF0000);
        renderBorderedIndicator(buffer, poseStack, -.375F, .375F, .375F, .5F, color);
        if (doubleCache != null)
        {
            color = doubleCache.getCamoGetter(face, upEdge) != CamoGetter.NONE ? 0xFF00FF00 : 0xFFFF0000;
            renderBorderedIndicator(buffer, poseStack, -.25F, .25F, .3125F, .375F, color);
        }

        // Down edge
        color = cache.canConnectFullEdge(face, downEdge) ? 0xFF00FF00 : (cache.canConnectDetailed(face, downEdge) ? 0xFFFFAA00 : 0xFFFF0000);
        renderBorderedIndicator(buffer, poseStack, -.375F, .375F, -.5F, -.375F, color);
        if (doubleCache != null)
        {
            color = doubleCache.getCamoGetter(face, downEdge) != CamoGetter.NONE ? 0xFF00FF00 : 0xFFFF0000;
            renderBorderedIndicator(buffer, poseStack, -.25F, .25F, -.375F, -.3125F, color);
        }

        // Counterclockwise edge
        color = cache.canConnectFullEdge(face, ccwEdge) ? 0xFF00FF00 : (cache.canConnectDetailed(face, ccwEdge) ? 0xFFFFAA00 : 0xFFFF0000);
        renderBorderedIndicator(buffer, poseStack, .375F, .5F, -.375F, .375F, color);
        if (doubleCache != null)
        {
            color = doubleCache.getCamoGetter(face, ccwEdge) != CamoGetter.NONE ? 0xFF00FF00 : 0xFFFF0000;
            renderBorderedIndicator(buffer, poseStack, .3125F, .375F, -.25F, .25F, color);
        }

        // Clockwise edge
        color = cache.canConnectFullEdge(face, cwEdge) ? 0xFF00FF00 : (cache.canConnectDetailed(face, cwEdge) ? 0xFFFFAA00 : 0xFFFF0000);
        renderBorderedIndicator(buffer, poseStack, -.5F, -.375F, -.375F, .375F, color);
        if (doubleCache != null)
        {
            color = doubleCache.getCamoGetter(face, cwEdge) != CamoGetter.NONE ? 0xFF00FF00 : 0xFFFF0000;
            renderBorderedIndicator(buffer, poseStack, -.375F, -.3125F, -.25F, .25F, color);
        }
    }

    private static void renderBorderedIndicator(VertexConsumer buffer, PoseStack poseStack, float minX, float maxX, float minY, float maxY, int color)
    {
        renderBorderedIndicator(buffer, poseStack, minX, maxX, minY, maxY, .5F, color);
    }

    private static void renderBorderedIndicator(VertexConsumer buffer, PoseStack poseStack, float minX, float maxX, float minY, float maxY, float z, int color)
    {
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        buffer.vertex(pose, minX, maxY, z + .0005F).color(0xFF000000).uv(dummyU0, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0F, 0F, 1F).endVertex();
        buffer.vertex(pose, minX, minY, z + .0005F).color(0xFF000000).uv(dummyU0, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0F, 0F, 1F).endVertex();
        buffer.vertex(pose, maxX, minY, z + .0005F).color(0xFF000000).uv(dummyU1, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0F, 0F, 1F).endVertex();
        buffer.vertex(pose, maxX, maxY, z + .0005F).color(0xFF000000).uv(dummyU1, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0F, 0F, 1F).endVertex();

        buffer.vertex(pose, minX + .01F, maxY - .01F, z + .001F).color(color).uv(dummyU0, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0F, 0F, 1F).endVertex();
        buffer.vertex(pose, minX + .01F, minY + .01F, z + .001F).color(color).uv(dummyU0, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0F, 0F, 1F).endVertex();
        buffer.vertex(pose, maxX - .01F, minY + .01F, z + .001F).color(color).uv(dummyU1, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0F, 0F, 1F).endVertex();
        buffer.vertex(pose, maxX - .01F, maxY - .01F, z + .001F).color(color).uv(dummyU1, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0F, 0F, 1F).endVertex();
    }

    public static void captureDummySprite(TextureAtlas atlas)
    {
        TextureAtlasSprite sprite = atlas.getSprite(ClientUtils.DUMMY_TEXTURE);
        dummyU0 = sprite.getU0();
        dummyU1 = sprite.getU1();
        dummyV0 = sprite.getV0();
        dummyV1 = sprite.getV1();
    }
}
