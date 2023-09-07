package xfacthd.framedblocks.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.common.block.sign.AbstractFramedSignBlock;
import xfacthd.framedblocks.common.block.sign.FramedStandingSignBlock;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;

import java.util.List;

public class FramedSignRenderer implements BlockEntityRenderer<FramedSignBlockEntity>
{
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
    private static final float RENDER_SCALE = 0.6666667F;
    private static final Vector3f TEXT_OFFSET = new Vector3f(0F, 5.6F/16F, 1.024F/16F);
    private static final Vector3f WALL_TEXT_OFFSET = new Vector3f(0F, 5.35F/16F, 1.024F/16F);

    private final Font font;

    public FramedSignRenderer(BlockEntityRendererProvider.Context ctx)
    {
        font = ctx.getFont();
    }

    @Override
    public void render(
            FramedSignBlockEntity sign,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    )
    {
        BlockState state = sign.getBlockState();
        if (!(state.getBlock() instanceof AbstractFramedSignBlock signBlock))
        {
            return;
        }

        BlockPos pos = sign.getBlockPos();
        int lineHeight = signBlock.getTextLineHeight();
        int lineWidth = signBlock.getMaxTextLineWidth();

        poseStack.pushPose();
        applyTransforms(poseStack, -signBlock.getYRotationDegrees(state), state);
        renderText(pos, signBlock, sign.getFrontText(), poseStack, buffer, light, lineHeight, lineWidth, true);
        renderText(pos, signBlock, sign.getBackText(), poseStack, buffer, light, lineHeight, lineWidth, false);
        poseStack.popPose();
    }

    protected void applyTransforms(PoseStack poseStack, float yRot, BlockState state)
    {
        poseStack.translate(.5F, .75F * RENDER_SCALE, .5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        if (!(state.getBlock() instanceof FramedStandingSignBlock))
        {
            poseStack.translate(0F, -5F/16F, -7F/16F);
        }
    }

    private void applyTextTransforms(PoseStack poseStack, AbstractFramedSignBlock signBlock, boolean front)
    {
        if (!front)
        {
            poseStack.mulPose(Quaternions.YP_180);
        }

        Vector3f offset = getTextOffset(signBlock);
        poseStack.translate(offset.x, offset.y, offset.z);
        float scale = 0.015625F * getSignTextRenderScale();
        poseStack.scale(scale, -scale, scale);
    }

    protected float getSignTextRenderScale()
    {
        return RENDER_SCALE;
    }

    protected Vector3f getTextOffset(AbstractFramedSignBlock signBlock)
    {
        boolean standing = signBlock instanceof FramedStandingSignBlock;
        return standing ? TEXT_OFFSET : WALL_TEXT_OFFSET;
    }

    private void renderText(
            BlockPos pos,
            AbstractFramedSignBlock signBlock,
            SignText text,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int lineHeight,
            int lineWidth,
            boolean front
    )
    {
        poseStack.pushPose();
        applyTextTransforms(poseStack, signBlock, front);

        int darkColor = getDarkTextColor(text);
        int textColor;
        boolean outline;
        int textLight;
        if (text.hasGlowingText())
        {
            textColor = text.getColor().getTextColor();
            outline = showOutline(pos, textColor);
            textLight = LightTexture.FULL_BRIGHT;
        }
        else
        {
            textColor = darkColor;
            outline = false;
            textLight = light;
        }

        boolean filter = Minecraft.getInstance().isTextFilteringEnabled();
        FormattedCharSequence[] lines = text.getRenderMessages(filter, line ->
        {
            List<FormattedCharSequence> parts = font.split(line, lineWidth);
            return parts.isEmpty() ? FormattedCharSequence.EMPTY : parts.get(0);
        });

        int centerY = 4 * lineHeight / 2;
        Matrix4f pose = poseStack.last().pose();
        for (int idx = 0; idx < 4; ++idx)
        {
            FormattedCharSequence line = lines[idx];
            float textX = (float) -font.width(line) / 2;
            float textY = idx * lineHeight - centerY;
            if (outline)
            {
                font.drawInBatch8xOutline(line, textX, textY, textColor, darkColor, pose, buffer, textLight);
            }
            else
            {
                font.drawInBatch(line, textX, textY, textColor, false, pose, buffer, Font.DisplayMode.POLYGON_OFFSET, 0, textLight);
            }
        }

        poseStack.popPose();
    }

    private static int getDarkTextColor(SignText text)
    {
        int color = text.getColor().getTextColor();
        if (color == DyeColor.BLACK.getTextColor() && text.hasGlowingText())
        {
            return 0xFFF0EBCC;
        }

        int r = (int) ((double) FastColor.ARGB32.red(color) * 0.4D);
        int g = (int) ((double) FastColor.ARGB32.green(color) * 0.4D);
        int b = (int) ((double) FastColor.ARGB32.blue(color) * 0.4D);
        return FastColor.ARGB32.color(0, r, g, b);
    }

    private static boolean showOutline(BlockPos pos, int textColor)
    {
        if (textColor == DyeColor.BLACK.getTextColor())
        {
            return true;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.options.getCameraType().isFirstPerson() && mc.player.isScoping())
        {
            return true;
        }
        else
        {
            Entity camera = mc.getCameraEntity();
            return camera != null && camera.distanceToSqr(Vec3.atCenterOf(pos)) < (double) OUTLINE_RENDER_DISTANCE;
        }
    }
}
