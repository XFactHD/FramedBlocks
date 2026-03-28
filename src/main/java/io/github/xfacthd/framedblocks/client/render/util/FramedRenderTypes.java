package io.github.xfacthd.framedblocks.client.render.util;

import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public final class FramedRenderTypes {
    public static final RenderType LINES_NO_DEPTH = RenderType.create(
            "lines",
            RenderSetup.builder(FramedRenderPipelines.LINES_NO_DEPTH)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .bufferSize(256)
                    .createRenderSetup()
    );
    public static final RenderType DEBUG_QUADS_DEPTH = RenderType.create(
            "debug_quads_depth",
            RenderSetup.builder(FramedRenderPipelines.DEBUG_QUADS_DEPTH)
                    .sortOnUpload()
                    .createRenderSetup()
    );

    private FramedRenderTypes() { }
}
