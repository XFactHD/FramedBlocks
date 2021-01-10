package xfacthd.framedblocks.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.*;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.text.ITextComponent;
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
        matrix.push();

        BlockState state = tile.getBlockState();
        if (state.getBlock() instanceof FramedSignBlock)
        {
            matrix.translate(0.5D, 0.5D, 0.5D);
            float rot = -((state.get(BlockStateProperties.ROTATION_0_15) * 360) / 16.0F);
            matrix.rotate(Vector3f.YP.rotationDegrees(rot));
        }
        else
        {
            matrix.translate(0.5D, 0.5D, 0.5D);
            float rot = -state.get(PropertyHolder.FACING_HOR).getHorizontalAngle();
            matrix.rotate(Vector3f.YP.rotationDegrees(rot));
            matrix.translate(0.0D, -0.3125D, -0.4375D);
        }

        matrix.translate(0.0, 0.33, 0.065);
        matrix.scale(0.01F, -0.01F, 0.01F);

        int textColor = tile.getTextColor().getTextColor();
        int r = (int)(NativeImage.getRed(textColor) * 0.4D);
        int g = (int)(NativeImage.getGreen(textColor) * 0.4D);
        int b = (int)(NativeImage.getBlue(textColor) * 0.4D);
        int argb = NativeImage.getCombined(0, b, g, r);

        FontRenderer fontrenderer = renderDispatcher.getFontRenderer();
        for (int line = 0; line < 4; line++)
        {
            String text = tile.getRenderedLine(line, component ->
            {
                List<ITextComponent> parts = RenderComponentsUtil.splitText(component, 90, fontrenderer, false, true);
                return parts.isEmpty() ? "" : parts.get(0).getFormattedText();
            });

            if (text != null)
            {
                float xOff = -fontrenderer.getStringWidth(text) / 2F;
                float y = line * 10 - 20;
                fontrenderer.renderString(text, xOff, y, argb, false, matrix.getLast().getMatrix(), buffer, false, 0, light);
            }
        }

        matrix.pop();
    }
}