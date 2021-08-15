package xfacthd.framedblocks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.util.FormattedCharSequence;
import com.mojang.math.Vector3f;
import xfacthd.framedblocks.common.block.FramedSignBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.blockentity.FramedSignBlockEntity;

import java.util.List;

public class FramedSignRenderer implements BlockEntityRenderer<FramedSignBlockEntity>
{
    private final Font fontrenderer;

    public FramedSignRenderer(BlockEntityRendererProvider.Context ctx) { fontrenderer = ctx.getFont(); }

    @Override
    public void render(FramedSignBlockEntity tile, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int light, int overlay)
    {
        matrix.pushPose();

        BlockState state = tile.getBlockState();
        if (state.getBlock() instanceof FramedSignBlock)
        {
            matrix.translate(0.5D, 0.5D, 0.5D);
            float rot = -((state.getValue(BlockStateProperties.ROTATION_16) * 360) / 16.0F);
            matrix.mulPose(Vector3f.YP.rotationDegrees(rot));
        }
        else
        {
            matrix.translate(0.5D, 0.5D, 0.5D);
            float rot = -state.getValue(PropertyHolder.FACING_HOR).toYRot();
            matrix.mulPose(Vector3f.YP.rotationDegrees(rot));
            matrix.translate(0.0D, -0.3125D, -0.4375D);
        }

        matrix.translate(0.0, 0.33, 0.065);
        matrix.scale(0.01F, -0.01F, 0.01F);

        int textColor = tile.getTextColor().getTextColor();
        int r = (int)(NativeImage.getR(textColor) * 0.4D);
        int g = (int)(NativeImage.getG(textColor) * 0.4D);
        int b = (int)(NativeImage.getB(textColor) * 0.4D);
        int argb = NativeImage.combine(0, b, g, r);

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
                fontrenderer.drawInBatch(text, xOff, y, argb, false, matrix.last().pose(), buffer, false, 0, light);
            }
        }

        matrix.popPose();
    }
}