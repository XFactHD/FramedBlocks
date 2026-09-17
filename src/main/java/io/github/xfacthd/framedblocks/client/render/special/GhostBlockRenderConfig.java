package io.github.xfacthd.framedblocks.client.render.special;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.oit.OitStage;
import org.jspecify.annotations.Nullable;

abstract sealed class GhostBlockRenderConfig {
    private static final GhostBlockRenderConfig DEFAULT = new Default();
    private static final GhostBlockRenderConfig FALLBACK = new Fallback();

    static GhostBlockRenderConfig get() {
        return ClientConfig.VIEW.useAltGhostRenderer() ? FALLBACK : DEFAULT;
    }

    void setupSamplers(FeatureFrameContext context, RenderPass renderPass) {
        renderPass.setUniform(
                "Sampler0",
                context.textureManager().getTexture(ClientUtils.BLOCK_ATLAS).getTextureView(),
                RenderSystem.getSamplerCache().getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.NEAREST, true)
        );
        renderPass.setUniform(
                "Sampler2",
                context.lightmap(),
                RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)
        );
    }

    abstract RenderPipeline getPipeline(@Nullable OitStage oitStage);

    //abstract OutputTarget getOutputTarget();

    private static final class Default extends GhostBlockRenderConfig {
        //private static final OutputTarget PARTICLE = new OutputTarget("particle_target", () -> Minecraft.getInstance().levelRenderer.particlesTarget());

        @Override
        RenderPipeline getPipeline(@Nullable OitStage oitStage) {
            if (oitStage != null) {
                return RenderPipelines.OIT_TRANSLUCENT_BLOCK.getPipeline(oitStage);
            }
            return RenderPipelines.TRANSLUCENT_BLOCK;
        }

        //@Override
        //OutputTarget getOutputTarget() {
        //    return PARTICLE;
        //}
    }

    private static final class Fallback extends GhostBlockRenderConfig {
        @Override
        void setupSamplers(FeatureFrameContext context, RenderPass renderPass) {
            super.setupSamplers(context, renderPass);
            renderPass.setUniform(
                    "Sampler1",
                    Minecraft.getInstance().gameRenderer.overlayTexture().getTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)
            );
        }

        @Override
        RenderPipeline getPipeline(@Nullable OitStage oitStage) {
            if (oitStage != null) {
                return RenderPipelines.OIT_ITEM.getPipeline(oitStage);
            }
            return RenderPipelines.ITEM_TRANSLUCENT;
        }

        //@Override
        //OutputTarget getOutputTarget() {
        //    return OutputTarget.ITEM_ENTITY_TARGET;
        //}
    }
}
