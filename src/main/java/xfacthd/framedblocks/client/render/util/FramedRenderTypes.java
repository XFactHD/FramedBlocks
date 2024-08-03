package xfacthd.framedblocks.client.render.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public final class FramedRenderTypes
{
    public static final RenderType LINES_NO_DEPTH = RenderType.create(
            "lines",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.LINES,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
                    .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderType.ITEM_ENTITY_TARGET)
                    .setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
                    .setCullState(RenderType.NO_CULL)
                    .setDepthTestState(RenderType.NO_DEPTH_TEST)
                    .createCompositeState(false)
    );
}
