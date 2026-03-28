package io.github.xfacthd.framedblocks.client.render.util;

import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.client.pipeline.PipelineModifier;
import net.neoforged.neoforge.client.pipeline.RegisterPipelineModifiersEvent;

public final class FramedPipelineModifiers {
    public static final ResourceKey<PipelineModifier> FORCE_ENTITY_SOLID = ResourceKey.create(PipelineModifier.MODIFIERS_KEY, Utils.id("force_solid"));

    public static void onRegisterModifiers(RegisterPipelineModifiersEvent event) {
        event.register(FORCE_ENTITY_SOLID, (pipeline, name) -> {
            if (pipeline == RenderPipelines.ENTITY_SOLID) {
                return pipeline.toBuilder()
                        .withLocation(name)
                        .withFragmentShader(Utils.id("core/entity_forced_solid"))
                        .build();
            }
            return pipeline;
        });
    }

    private FramedPipelineModifiers() { }
}
