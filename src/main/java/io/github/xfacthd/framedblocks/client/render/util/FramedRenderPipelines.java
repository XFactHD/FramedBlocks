package io.github.xfacthd.framedblocks.client.render.util;

import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

public final class FramedRenderPipelines {
    public static final RenderPipeline DEBUG_QUADS_DEPTH = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Utils.id("pipeline/debug_quads_depth"))
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
    public static final OitPipelineSet OIT_ENTITY_CULL_NO_SHADE = OitPipelineSet.builder(Utils.id("entity_cull_no_shade"), RenderPipeline.builder(RenderPipelines.OIT_ENTITY_SNIPPET))
            .withAccumulateModifier(accumulate -> accumulate
                    .withShaderDefine("NO_CARDINAL_LIGHTING")
                    .withBindGroupLayout(BindGroupLayouts.SAMPLER1)
                    .withBindGroupLayout(BindGroupLayouts.SAMPLER2)
            )
            .build();

    public static void onRegisterRenderPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(DEBUG_QUADS_DEPTH);
        event.registerPipeline(ENTITY_SOLID_NO_SHADE);
        event.registerPipeline(ENTITY_CUTOUT_CULL_NO_SHADE);
        event.registerPipeline(ENTITY_TRANSLUCENT_CULL_NO_SHADE);
        event.registerOitPipelineSet(OIT_ENTITY_CULL_NO_SHADE);
    }

    private FramedRenderPipelines() { }
}
