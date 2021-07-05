package xfacthd.framedblocks.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Vector3f;
import xfacthd.framedblocks.common.block.FramedSignBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedSignTileEntity;

import java.util.List;

public class FramedSignRenderer extends TileEntityRenderer<FramedSignTileEntity>
{
    public FramedSignRenderer(TileEntityRendererDispatcher dispatcher) { super(dispatcher); }

    @Override
    public void render(FramedSignTileEntity tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay)
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

        FontRenderer fontrenderer = renderer.getFont();
        for (int line = 0; line < 4; line++)
        {
            IReorderingProcessor text = tile.getRenderedLine(line, component ->
            {
                List<IReorderingProcessor> parts = fontrenderer.split(component, 90);
                return parts.isEmpty() ? IReorderingProcessor.EMPTY : parts.get(0);
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