package io.github.xfacthd.framedblocks.client.render.util;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

public final class FramedRenderPipelines {
    public static final RenderPipeline LINES_NO_DEPTH = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(Utils.id("pipeline/lines_no_depth"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .build();
    public static final RenderPipeline DEBUG_QUADS_DEPTH = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Utils.id("pipeline/debug_quads_depth"))
            .withCull(false)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .build();

    public static void onRegisterRenderPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(LINES_NO_DEPTH);
        event.registerPipeline(DEBUG_QUADS_DEPTH);
    }

    private FramedRenderPipelines() { }
}
