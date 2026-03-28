package io.github.xfacthd.framedblocks.client.render.special;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;

abstract sealed class GhostBlockRenderConfig {
    private static final GhostBlockRenderConfig DEFAULT = new Default();
    private static final GhostBlockRenderConfig FALLBACK = new Fallback();

    static GhostBlockRenderConfig get() {
        return ClientConfig.VIEW.useAltGhostRenderer() ? FALLBACK : DEFAULT;
    }

    void setupSamplers(RenderPass renderPass) {
        renderPass.bindTexture(
                "Sampler0",
                Minecraft.getInstance().getTextureManager().getTexture(ClientUtils.BLOCK_ATLAS).getTextureView(),
                RenderSystem.getSamplerCache().getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.NEAREST, true)
        );
        renderPass.bindTexture(
                "Sampler2",
                Minecraft.getInstance().gameRenderer.lightmap(),
                RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)
        );
    }

    abstract RenderPipeline getPipeline();

    abstract RenderTarget getRenderTarget();

    private static final class Default extends GhostBlockRenderConfig {
        @Override
        void setupSamplers(RenderPass renderPass) {
            super.setupSamplers(renderPass);
            renderPass.bindTexture("Sampler1", null, null);
        }

        @Override
        RenderPipeline getPipeline() {
            return RenderPipelines.TRANSLUCENT_BLOCK;
        }

        @Override
        RenderTarget getRenderTarget() {
            RenderTarget particlesTarget = Minecraft.getInstance().levelRenderer.getParticlesTarget();
            return particlesTarget != null ? particlesTarget : Minecraft.getInstance().getMainRenderTarget();
        }
    }

    private static final class Fallback extends GhostBlockRenderConfig {
        @Override
        void setupSamplers(RenderPass renderPass) {
            super.setupSamplers(renderPass);
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
        RenderTarget getRenderTarget() {
            RenderTarget itemEntityTarget = Minecraft.getInstance().levelRenderer.getItemEntityTarget();
            return itemEntityTarget != null ? itemEntityTarget : Minecraft.getInstance().getMainRenderTarget();
        }
    }
}
