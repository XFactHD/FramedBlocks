package xfacthd.framedblocks.client.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import xfacthd.framedblocks.FramedBlocks;

public abstract class CustomRenderType extends RenderType
{
    public static final RenderType GHOST_BLOCK = create(
            FramedBlocks.MODID + ":ghost_block",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            2097152,
            true,
            true,
            ghostState()
    );

    private static RenderType.CompositeState ghostState()
    {
        return RenderType.CompositeState.builder()
                .setLightmapState(LIGHTMAP)
                .setShaderState(RenderStateShard.RENDERTYPE_TRANSLUCENT_SHADER)
                .setTextureState(BLOCK_SHEET_MIPPED)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setOutputState(RenderStateShard.PARTICLES_TARGET)
                .createCompositeState(true);
    }



    private CustomRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState)
    {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }
}
