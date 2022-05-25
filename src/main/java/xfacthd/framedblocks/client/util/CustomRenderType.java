package xfacthd.framedblocks.client.util;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;
import xfacthd.framedblocks.FramedBlocks;

public abstract class CustomRenderType extends RenderType
{
    public static final RenderType GHOST_BLOCK = create(
            FramedBlocks.MODID + ":ghost_block",
            DefaultVertexFormats.BLOCK,
            GL11.GL_QUADS,
            2097152,
            true,
            true,
            ghostState()
    );

    private static RenderType.State ghostState()
    {
        return RenderType.State.builder()
                .setShadeModelState(SMOOTH_SHADE)
                .setLightmapState(LIGHTMAP)
                .setTextureState(BLOCK_SHEET_MIPPED)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setOutputState(RenderState.PARTICLES_TARGET)
                .createCompositeState(true);
    }



    private CustomRenderType(String name, VertexFormat format, int mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState)
    {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }
}
