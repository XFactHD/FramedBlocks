package io.github.xfacthd.framedblocks.client.render.debug.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.xfacthd.framedblocks.api.render.Quaternions;
import io.github.xfacthd.framedblocks.api.render.debug.BlockDebugRenderer;
import io.github.xfacthd.framedblocks.api.render.outline.OutlineRenderer;
import io.github.xfacthd.framedblocks.api.util.Triangle;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.render.util.FramedRenderTypes;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import io.github.xfacthd.framedblocks.common.config.DevToolsConfig;
import io.github.xfacthd.framedblocks.common.data.collapsible.DebugInfo;
import io.github.xfacthd.framedblocks.common.data.collapsible.TargetCalculator;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class CollapsibleBlockDebugRenderer implements BlockDebugRenderer<FramedCollapsibleBlockEntity>
{
    public static final CollapsibleBlockDebugRenderer INSTANCE = new CollapsibleBlockDebugRenderer();
    private static final ContextKey<DebugInfo> DATA_KEY = new ContextKey<>(Utils.id("collapsible_block_debug_renderer"));
    private static final float VERT_TEXT_SCALE = 16F;
    private static final float TARGET_TEXT_SCALE = 64F;
    private static final int TARGET_COLOR = 0xFF404040;

    private CollapsibleBlockDebugRenderer() { }

    @Override
    public void extract(FramedCollapsibleBlockEntity be, BlockHitResult blockHit, float partialTick, LevelRenderState renderState)
    {
        Player player = Objects.requireNonNull(Minecraft.getInstance().player);
        DebugInfo debugInfo = TargetCalculator.computeDebugInfo(be, player, blockHit, partialTick);
        renderState.setRenderData(DATA_KEY, debugInfo);
    }

    @Override
    public void submit(LevelRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector)
    {
        DebugInfo data = renderState.getRenderData(DATA_KEY);
        if (data == null) return;

        poseStack.pushPose();
        {
            Direction face = data.face();
            float[] heights = data.heights();

            poseStack.translate(.5, .5, .5);
            if (face == Direction.DOWN)
            {
                poseStack.mulPose(Quaternions.XP_180);
            }
            else if (face != Direction.UP)
            {
                poseStack.mulPose(OutlineRenderer.YN_DIR[face.get2DDataValue()]);
                poseStack.mulPose(Quaternions.XP_90);
            }
            poseStack.translate(-.5, -.5, -.5);

            Font font = Minecraft.getInstance().font;

            submitVertIndex(collector, poseStack, -2.5F / 16F, -3.5F / 16F, heights[0], 0);
            submitVertIndex(collector, poseStack, -2.5F / 16F, 19.5F / 16F, heights[1], 1);
            submitVertIndex(collector, poseStack, 18.5F / 16F, 19.5F / 16F, heights[2], 2);
            submitVertIndex(collector, poseStack, 18.5F / 16F, -3.5F / 16F, heights[3], 3);

            String text = Optionull.mapOrDefault(data.target(), v -> String.format("%6.3f %6.3f %6.3f", v.x, v.y, v.z), "<null>");
            float x = .5F - (font.width(text) / (TARGET_TEXT_SCALE * 2F));
            submitText(collector, poseStack, x, 25F/16F, 1F, 0F, TARGET_TEXT_SCALE, text, TARGET_COLOR);

            text = data.rotated() ? "true" : "false";
            x = .5F - (font.width(text) / (TARGET_TEXT_SCALE * 2F));
            submitText(collector, poseStack, x, 22/16F, 1F, 0F, TARGET_TEXT_SCALE, text, TARGET_COLOR);
        }
        poseStack.popPose();

        poseStack.pushPose();
        {
            poseStack.translate(data.face().getUnitVec3().scale(.001F));

            collector.submitCustomGeometry(poseStack, FramedRenderTypes.DEBUG_QUADS_DEPTH, (pose, builder) ->
            {
                drawTriangle(builder, pose, data.pos(), data.tri1(), 0xAAFF0000);
                drawTriangle(builder, pose, data.pos(), data.tri2(), 0xAA00FF00);
            });
            submitTarget(collector, poseStack, data.target(), TARGET_COLOR);
        }
        poseStack.popPose();
    }

    private static void submitVertIndex(SubmitNodeCollector collector, PoseStack poseStack, float x, float z, float y, int vert)
    {
        submitText(collector, poseStack, x, z, y, -2.5F, VERT_TEXT_SCALE, Integer.toString(vert), 0xFFFFFFFF);
    }

    private static void submitText(SubmitNodeCollector collector, PoseStack poseStack, float x, float z, float y, float xOff, float scale, String text, int color)
    {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.scale(1F / scale, 1F / scale, 1F / scale);

        collector.submitText(
                poseStack,
                xOff,
                -3.5F,
                FormattedCharSequence.forward(text, Style.EMPTY),
                false,
                Font.DisplayMode.NORMAL,
                LightCoordsUtil.FULL_BRIGHT,
                color,
                0x00000000,
                0
        );

        poseStack.popPose();
    }

    private static void drawTriangle(VertexConsumer builder, PoseStack.Pose pose, BlockPos pos, Triangle tri, int color)
    {
        vertex(builder, pose, pos, tri.vertex0(), color);
        vertex(builder, pose, pos, tri.vertex1(), color);
        vertex(builder, pose, pos, tri.vertex2(), color);
        vertex(builder, pose, pos, tri.vertex2(), color);
    }

    @SuppressWarnings("SameParameterValue")
    private static void submitTarget(SubmitNodeCollector collector, PoseStack poseStack, @Nullable Vec3 target, int color)
    {
        if (target == null) return;

        poseStack.pushPose();
        poseStack.translate(target);
        collector.submitCustomGeometry(poseStack, FramedRenderTypes.DEBUG_QUADS_DEPTH, (pose, builder) ->
        {
            // Top
            vertex(builder, pose, -.025F,  .025F, -.025F, color);
            vertex(builder, pose, -.025F,  .025F,  .025F, color);
            vertex(builder, pose,  .025F,  .025F,  .025F, color);
            vertex(builder, pose,  .025F,  .025F, -.025F, color);

            // Bottom
            vertex(builder, pose, -.025F, -.025F, -.025F, color);
            vertex(builder, pose, -.025F, -.025F,  .025F, color);
            vertex(builder, pose,  .025F, -.025F,  .025F, color);
            vertex(builder, pose,  .025F, -.025F, -.025F, color);

            // North
            vertex(builder, pose, -.025F, -.025F, -.025F, color);
            vertex(builder, pose, -.025F,  .025F, -.025F, color);
            vertex(builder, pose,  .025F,  .025F, -.025F, color);
            vertex(builder, pose,  .025F, -.025F, -.025F, color);

            // South
            vertex(builder, pose, -.025F, -.025F,  .025F, color);
            vertex(builder, pose, -.025F,  .025F,  .025F, color);
            vertex(builder, pose,  .025F,  .025F,  .025F, color);
            vertex(builder, pose,  .025F, -.025F,  .025F, color);

            // West
            vertex(builder, pose, -.025F, -.025F, -.025F, color);
            vertex(builder, pose, -.025F, -.025F,  .025F, color);
            vertex(builder, pose, -.025F,  .025F,  .025F, color);
            vertex(builder, pose, -.025F,  .025F, -.025F, color);

            // East
            vertex(builder, pose,  .025F, -.025F, -.025F, color);
            vertex(builder, pose,  .025F, -.025F,  .025F, color);
            vertex(builder, pose,  .025F,  .025F,  .025F, color);
            vertex(builder, pose,  .025F,  .025F, -.025F, color);
        });
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer builder, PoseStack.Pose pose, BlockPos pos, Vec3 vec, int color)
    {
        float x = (float) (vec.x - pos.getX());
        float y = (float) (vec.y - pos.getY());
        float z = (float) (vec.z - pos.getZ());
        vertex(builder, pose, x, y, z, color);
    }

    private static void vertex(VertexConsumer builder, PoseStack.Pose pose, float x, float y, float z, int color)
    {
        builder.addVertex(pose, x, y, z).setColor(color);
    }

    @Override
    public boolean isEnabled()
    {
        return DevToolsConfig.VIEW.isCollapsibleBlockDebugRendererEnabled();
    }
}
