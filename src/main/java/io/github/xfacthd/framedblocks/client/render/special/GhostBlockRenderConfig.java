package io.github.xfacthd.framedblocks.client.render.special;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.rendertype.OutputTarget;

abstract sealed class GhostBlockRenderConfig {
    private static final GhostBlockRenderConfig DEFAULT = new Default();
    private static final GhostBlockRenderConfig FALLBACK = new Fallback();

    static GhostBlockRenderConfig get() {
        return ClientConfig.VIEW.useAltGhostRenderer() ? FALLBACK : DEFAULT;
    }

    void setupSamplers(FeatureFrameContext context, RenderPass renderPass) {
        renderPass.bindTexture(
                "Sampler0",
                context.textureManager().getTexture(ClientUtils.BLOCK_ATLAS).getTextureView(),
                RenderSystem.getSamplerCache().getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.NEAREST, true)
        );
        renderPass.bindTexture(
                "Sampler2",
                context.lightmap(),
                RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)
        );
    }

    abstract RenderPipeline getPipeline();

    abstract OutputTarget getOutputTarget();

    private static final class Default extends GhostBlockRenderConfig {
        private static final OutputTarget PARTICLE = new OutputTarget("particle_target", () -> Minecraft.getInstance().levelRenderer.particlesTarget());

        @Override
        RenderPipeline getPipeline() {
            return RenderPipelines.TRANSLUCENT_BLOCK;
        }

        @Override
        OutputTarget getOutputTarget() {
            return PARTICLE;
        }
    }

    private static final class Fallback extends GhostBlockRenderConfig {
        @Override
        void setupSamplers(FeatureFrameContext context, RenderPass renderPass) {
            super.setupSamplers(context, renderPass);
            renderPass.bindTexture(
                    "Sampler1",
                    Minecraft.getInstance().gameRenderer.overlayTexture().getTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)
            );
        }

        @Override
        RenderPipeline getPipeline() {
            return RenderPipelines.ITEM_TRANSLUCENT;
        }

        @Override
        OutputTarget getOutputTarget() {
            return OutputTarget.ITEM_ENTITY_TARGET;
        }
    }
}
