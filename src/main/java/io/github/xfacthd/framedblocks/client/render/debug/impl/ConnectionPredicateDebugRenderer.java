package io.github.xfacthd.framedblocks.client.render.debug.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.cache.DoubleBlockStateCache;
import io.github.xfacthd.framedblocks.api.block.cache.StateCache;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.render.Quaternions;
import io.github.xfacthd.framedblocks.api.render.debug.BlockDebugRenderer;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.render.util.FramedRenderTypes;
import io.github.xfacthd.framedblocks.common.config.DevToolsConfig;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.phys.BlockHitResult;

public class ConnectionPredicateDebugRenderer implements BlockDebugRenderer<IFramedBlockEntity>
{
    public static final ConnectionPredicateDebugRenderer INSTANCE = new ConnectionPredicateDebugRenderer();
    private static final ContextKey<ConnectionPredicateRenderState> DATA_KEY = new ContextKey<>(Utils.id("con_pred_debug_renderer"));

    private ConnectionPredicateDebugRenderer() { }

    @Override
    public void extract(IFramedBlockEntity be, BlockHitResult blockHit, float partialTick, LevelRenderState renderState)
    {
        Direction face = blockHit.getDirection();
        StateCache cache = be.getBlockState().framedblocks$getCache();
        renderState.setRenderData(DATA_KEY, new ConnectionPredicateRenderState(face, cache));
    }

    @Override
    public void submit(LevelRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector)
    {
        ConnectionPredicateRenderState data = renderState.getRenderData(DATA_KEY);
        if (data == null) return;

        poseStack.translate(.5, .5, .5);

        Direction face = data.face;
        StateCache cache = data.cache;
        switch (face)
        {
            case UP ->
            {
                poseStack.mulPose(Quaternions.XN_90);
                submitIndicators(collector, poseStack, cache, face, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
            }
            case DOWN ->
            {
                poseStack.mulPose(Quaternions.XP_90);
                submitIndicators(collector, poseStack, cache, face, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST);
            }
            default ->
            {
                poseStack.mulPose(Axis.YN.rotationDegrees(face.toYRot()));
                submitIndicators(collector, poseStack, cache, face, Direction.UP, Direction.DOWN, face.getCounterClockWise(), face.getClockWise());
            }
        }
    }

    private static void submitIndicators(
            SubmitNodeCollector collector,
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

        collector.submitCustomGeometry(poseStack, FramedRenderTypes.DEBUG_QUADS_DEPTH, (pose, buffer) ->
        {
            // Null / all edges
            int color = cache.canConnectFullEdge(face, null) ? 0xFF00FF00 : 0xFFFF0000;
            renderBorderedIndicator(buffer, pose, -.125F, .125F, -.125F, .125F, .501F, color);
            if (doubleCache != null)
            {
                color = doubleCache.getCamoGetter(face, null) != CamoGetter.NONE ? 0xFF00FF00 : 0xFFFF0000;
                renderBorderedIndicator(buffer, pose, -.1875F, .1875F, -.1875F, .1875F, color);
            }

            // Up edge
            color = cache.canConnectFullEdge(face, upEdge) ? 0xFF00FF00 : (cache.canConnectDetailed(face, upEdge) ? 0xFFFFAA00 : 0xFFFF0000);
            renderBorderedIndicator(buffer, pose, -.375F, .375F, .375F, .5F, color);
            if (doubleCache != null)
            {
                color = doubleCache.getCamoGetter(face, upEdge) != CamoGetter.NONE ? 0xFF00FF00 : 0xFFFF0000;
                renderBorderedIndicator(buffer, pose, -.25F, .25F, .3125F, .375F, color);
            }

            // Down edge
            color = cache.canConnectFullEdge(face, downEdge) ? 0xFF00FF00 : (cache.canConnectDetailed(face, downEdge) ? 0xFFFFAA00 : 0xFFFF0000);
            renderBorderedIndicator(buffer, pose, -.375F, .375F, -.5F, -.375F, color);
            if (doubleCache != null)
            {
                color = doubleCache.getCamoGetter(face, downEdge) != CamoGetter.NONE ? 0xFF00FF00 : 0xFFFF0000;
                renderBorderedIndicator(buffer, pose, -.25F, .25F, -.375F, -.3125F, color);
            }

            // Counterclockwise edge
            color = cache.canConnectFullEdge(face, ccwEdge) ? 0xFF00FF00 : (cache.canConnectDetailed(face, ccwEdge) ? 0xFFFFAA00 : 0xFFFF0000);
            renderBorderedIndicator(buffer, pose, .375F, .5F, -.375F, .375F, color);
            if (doubleCache != null)
            {
                color = doubleCache.getCamoGetter(face, ccwEdge) != CamoGetter.NONE ? 0xFF00FF00 : 0xFFFF0000;
                renderBorderedIndicator(buffer, pose, .3125F, .375F, -.25F, .25F, color);
            }

            // Clockwise edge
            color = cache.canConnectFullEdge(face, cwEdge) ? 0xFF00FF00 : (cache.canConnectDetailed(face, cwEdge) ? 0xFFFFAA00 : 0xFFFF0000);
            renderBorderedIndicator(buffer, pose, -.5F, -.375F, -.375F, .375F, color);
            if (doubleCache != null)
            {
                color = doubleCache.getCamoGetter(face, cwEdge) != CamoGetter.NONE ? 0xFF00FF00 : 0xFFFF0000;
                renderBorderedIndicator(buffer, pose, -.375F, -.3125F, -.25F, .25F, color);
            }
        });
    }

    private static void renderBorderedIndicator(VertexConsumer buffer, PoseStack.Pose pose, float minX, float maxX, float minY, float maxY, int color)
    {
        renderBorderedIndicator(buffer, pose, minX, maxX, minY, maxY, .5F, color);
    }

    private static void renderBorderedIndicator(VertexConsumer buffer, PoseStack.Pose pose, float minX, float maxX, float minY, float maxY, float z, int color)
    {
        vertex(buffer, pose, minX, maxY, z + .0005F, 0xFF000000);
        vertex(buffer, pose, minX, minY, z + .0005F, 0xFF000000);
        vertex(buffer, pose, maxX, minY, z + .0005F, 0xFF000000);
        vertex(buffer, pose, maxX, maxY, z + .0005F, 0xFF000000);

        vertex(buffer, pose, minX + .01F, maxY - .01F, z + .001F, color);
        vertex(buffer, pose, minX + .01F, minY + .01F, z + .001F, color);
        vertex(buffer, pose, maxX - .01F, minY + .01F, z + .001F, color);
        vertex(buffer, pose, maxX - .01F, maxY - .01F, z + .001F, color);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z, int color)
    {
        buffer.addVertex(pose, x, y, z).setColor(color);
    }

    @Override
    public boolean isEnabled()
    {
        return DevToolsConfig.VIEW.isConnectionDebugRendererEnabled();
    }

    private record ConnectionPredicateRenderState(Direction face, StateCache cache) { }
}
