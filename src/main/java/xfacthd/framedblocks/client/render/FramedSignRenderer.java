package xfacthd.framedblocks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.util.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.block.interactive.FramedSignBlock;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;

import java.util.List;

public class FramedSignRenderer implements BlockEntityRenderer<FramedSignBlockEntity>
{
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);

    private final Font fontrenderer;

    public FramedSignRenderer(BlockEntityRendererProvider.Context ctx) { fontrenderer = ctx.getFont(); }

    @Override
    public void render(
            FramedSignBlockEntity tile,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    )
    {
        poseStack.pushPose();

        BlockState state = tile.getBlockState();
        if (state.getBlock() instanceof FramedSignBlock)
        {
            poseStack.translate(0.5D, 0.5D, 0.5D);
            float rot = -((state.getValue(BlockStateProperties.ROTATION_16) * 360) / 16.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(rot));
        }
        else
        {
            poseStack.translate(0.5D, 0.5D, 0.5D);
            float rot = -state.getValue(FramedProperties.FACING_HOR).toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(rot));
            poseStack.translate(0.0D, -0.3125D, -0.4375D);
        }

        poseStack.translate(0.0, 0.33, 0.065);
        poseStack.scale(0.01F, -0.01F, 0.01F);

        boolean showOutline;
        int textColor;
        int outlineColor;
        int textLight;

        if (tile.hasGlowingText())
        {
            showOutline = showOutline(tile);
            textColor = tile.getTextColor().getTextColor();
            outlineColor = getDarkTextColor(tile);
            textLight = 0x00F000F0;
        }
        else
        {
            showOutline = false;
            textColor = getDarkTextColor(tile);
            outlineColor = 0;
            textLight = light;
        }

        for (int line = 0; line < 4; line++)
        {
            FormattedCharSequence text = tile.getRenderedLine(line, component ->
            {
                List<FormattedCharSequence> parts = fontrenderer.split(component, 90);
                return parts.isEmpty() ? FormattedCharSequence.EMPTY : parts.get(0);
            });

            if (text != null)
            {
                float xOff = -fontrenderer.width(text) / 2F;
                float y = line * 10 - 20;

                if (showOutline)
                {
                    fontrenderer.drawInBatch8xOutline(text, xOff, y, textColor, outlineColor, poseStack.last().pose(), buffer, textLight);
                }
                else
                {
                    fontrenderer.drawInBatch(text, xOff, y, textColor, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, textLight);
                }
            }
        }

        poseStack.popPose();
    }

    private static int getDarkTextColor(FramedSignBlockEntity sign)
    {
        int color = sign.getTextColor().getTextColor();
        if (color == DyeColor.BLACK.getTextColor() && sign.hasGlowingText())
        {
            return 0xFFF0EBCC;
        }

        int r = (int) ((double) FastColor.ARGB32.red(color) * 0.4D);
        int g = (int) ((double) FastColor.ARGB32.green(color) * 0.4D);
        int b = (int) ((double) FastColor.ARGB32.blue(color) * 0.4D);
        return FastColor.ARGB32.color(0, r, g, b);
    }

    private static boolean showOutline(FramedSignBlockEntity sign)
    {
        if (sign.getTextColor().getTextColor() == DyeColor.BLACK.getTextColor()) { return true; }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.options.getCameraType().isFirstPerson() && mc.player.isScoping())
        {
            return true;
        }
        else
        {
            Entity camera = mc.getCameraEntity();
            return camera != null && camera.distanceToSqr(Vec3.atCenterOf(sign.getBlockPos())) < (double) OUTLINE_RENDER_DISTANCE;
        }
    }
}