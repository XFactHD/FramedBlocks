package io.github.xfacthd.framedblocks.client.render.util;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

public final class FramedRenderPipelines {
    public static final RenderPipeline DEBUG_QUADS_DEPTH = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Utils.id("pipeline/debug_quads_depth"))
            .withCull(false)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .build();
    public static final RenderPipeline ENTITY_SOLID_NO_SHADE = RenderPipelines.ENTITY_SOLID.toBuilder()
            .withLocation(Utils.id("pipeline/entity_solid_no_shade"))
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .build();
    public static final RenderPipeline ENTITY_CUTOUT_CULL_NO_SHADE = RenderPipelines.ENTITY_CUTOUT_CULL.toBuilder()
            .withLocation(Utils.id("pipeline/entity_cutout_cull_no_shade"))
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .build();
    public static final RenderPipeline ENTITY_TRANSLUCENT_CULL_NO_SHADE = RenderPipelines.ENTITY_TRANSLUCENT_CULL.toBuilder()
            .withLocation(Utils.id("pipeline/entity_translucent_cull_no_shade"))
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .build();

    public static void onRegisterRenderPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(DEBUG_QUADS_DEPTH);
        event.registerPipeline(ENTITY_SOLID_NO_SHADE);
        event.registerPipeline(ENTITY_CUTOUT_CULL_NO_SHADE);
        event.registerPipeline(ENTITY_TRANSLUCENT_CULL_NO_SHADE);
    }

    private FramedRenderPipelines() { }
}
