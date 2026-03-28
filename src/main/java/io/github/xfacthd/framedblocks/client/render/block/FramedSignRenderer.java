package io.github.xfacthd.framedblocks.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.xfacthd.framedblocks.api.render.Quaternions;
import io.github.xfacthd.framedblocks.client.render.block.state.FramedSignRenderState;
import io.github.xfacthd.framedblocks.common.block.sign.FramedStandingSignBlock;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.List;

public sealed class FramedSignRenderer implements BlockEntityRenderer<FramedSignBlockEntity, FramedSignRenderState> permits FramedHangingSignRenderer {
    private static final float RENDER_SCALE = 0.6666667F;
    private static final Vector3f TEXT_OFFSET = new Vector3f(0F, 5.6F/16F, 1.024F/16F);
    private static final Vector3f WALL_TEXT_OFFSET = new Vector3f(0F, 5.35F/16F, 1.024F/16F);

    private final Font font;

    public FramedSignRenderer(BlockEntityRendererProvider.Context ctx) {
        this.font = ctx.font();
    }

    @Override
    public void submit(FramedSignRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        int light = renderState.lightCoords;
        Vector3f textOffset = renderState.textOffset;
        int lineHeight = renderState.lineHeight;
        int lineWidth = renderState.lineWidth;
        boolean outline = renderState.outline;

        poseStack.pushPose();
        applyTransforms(poseStack, renderState.yRot, renderState.standing);
        renderText(renderState.frontText, poseStack, submitNodeCollector, light, textOffset, lineHeight, lineWidth, outline, true);
        renderText(renderState.backText, poseStack, submitNodeCollector, light, textOffset, lineHeight, lineWidth, outline, false);
        poseStack.popPose();
    }

    @Override
    public FramedSignRenderState createRenderState() {
        return new FramedSignRenderState();
    }

    @Override
    public void extractRenderState(
            FramedSignBlockEntity blockEntity,
            FramedSignRenderState renderState,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);

        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof SignBlock signBlock)) {
            return;
        }

        renderState.standing = state.getBlock() instanceof FramedStandingSignBlock;
        renderState.yRot = -signBlock.getYRotationDegrees(state);
        renderState.frontText = blockEntity.getText(true);
        renderState.backText = blockEntity.getText(false);
        renderState.textOffset = getTextOffset(signBlock);
        renderState.lineHeight = blockEntity.getTextLineHeight();
        renderState.lineWidth = blockEntity.getMaxTextLineWidth();
        renderState.outline = AbstractSignRenderer.isOutlineVisible(blockEntity.getBlockPos());
    }

    @Override
    public AABB getRenderBoundingBox(FramedSignBlockEntity blockEntity) {
        if (blockEntity.getBlockState().getBlock() instanceof FramedStandingSignBlock) {
            BlockPos pos = blockEntity.getBlockPos();
            return new AABB(pos.getX(), pos.getY() + .625, pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.125, pos.getZ() + 1.0);
        }
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
    }

    protected void applyTransforms(PoseStack poseStack, float yRot, boolean standing) {
        poseStack.translate(.5F, .75F * RENDER_SCALE, .5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        if (!standing) {
            poseStack.translate(0F, -5F/16F, -7F/16F);
        }
    }

    private void applyTextTransforms(PoseStack poseStack, Vector3f offset, boolean front) {
        if (!front) {
            poseStack.mulPose(Quaternions.YP_180);
        }

        poseStack.translate(offset.x, offset.y, offset.z);
        float scale = 0.015625F * getSignTextRenderScale();
        poseStack.scale(scale, -scale, scale);
    }

    protected float getSignTextRenderScale() {
        return RENDER_SCALE;
    }

    protected Vector3f getTextOffset(SignBlock signBlock) {
        boolean standing = signBlock instanceof FramedStandingSignBlock;
        return standing ? TEXT_OFFSET : WALL_TEXT_OFFSET;
    }

    private void renderText(
            SignText text,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int light,
            Vector3f textOffset,
            int lineHeight,
            int lineWidth,
            boolean outlineVisible,
            boolean front
    ) {
        poseStack.pushPose();
        applyTextTransforms(poseStack, textOffset, front);

        int darkColor = AbstractSignRenderer.getDarkColor(text);
        int textColor;
        boolean outline;
        int textLight;
        if (text.hasGlowingText()) {
            textColor = text.getColor().getTextColor();
            outline = textColor == DyeColor.BLACK.getTextColor() || outlineVisible;
            textLight = LightCoordsUtil.FULL_BRIGHT;
        } else {
            textColor = darkColor;
            outline = false;
            textLight = light;
        }

        boolean filter = Minecraft.getInstance().isTextFilteringEnabled();
        FormattedCharSequence[] lines = text.getRenderMessages(filter, line -> {
            List<FormattedCharSequence> parts = font.split(line, lineWidth);
            return parts.isEmpty() ? FormattedCharSequence.EMPTY : parts.getFirst();
        });

        int centerY = 4 * lineHeight / 2;
        for (int idx = 0; idx < 4; ++idx) {
            FormattedCharSequence line = lines[idx];
            float textX = (float) -font.width(line) / 2;
            float textY = idx * lineHeight - centerY;
            submitNodeCollector.submitText(poseStack, textX, textY, line, false, Font.DisplayMode.POLYGON_OFFSET, textLight, textColor, 0, outline ? darkColor : 0);
        }

        poseStack.popPose();
    }
}
