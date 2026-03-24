package io.github.xfacthd.framedblocks.client.screen.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.model.block.FramedBlockDisplayContext;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.client.model.block.AdvancedBlockModelRenderState;
import io.github.xfacthd.framedblocks.client.render.util.FramedPipelineModifiers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public final class BlockPictureInPictureRenderer extends PictureInPictureRenderer<BlockPictureInPictureRenderer.RenderState>
{
    private final FeatureRenderDispatcher featureRenderDispatcher;
    private final SubmitNodeCollector collector;
    private RenderConfig lastConfig = RenderConfig.DEFAULT;
    @Nullable
    private BlockState lastSignState;
    private BlockPos lastSignPos = BlockPos.ZERO;
    private FramedBlockData lastBlockData = FramedBlockData.EMPTY;

    public BlockPictureInPictureRenderer(MultiBufferSource.BufferSource bufferSource)
    {
        super(bufferSource);
        this.featureRenderDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        this.collector = featureRenderDispatcher.getSubmitNodeStorage();
    }

    @Override
    protected void renderToTexture(RenderState renderState, PoseStack poseStack)
    {
        RenderConfig config = renderState.config;
        config.poseTransform.accept(poseStack);

        Minecraft.getInstance().gameRenderer.getLighting().setupFor(config.lighting);
        renderState.modelRenderState.submitMultiLayer(poseStack, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        RenderSystem.pushPipelineModifier(FramedPipelineModifiers.FORCE_ENTITY_SOLID);
        featureRenderDispatcher.renderAllFeatures();
        bufferSource.endBatch();
        RenderSystem.popPipelineModifier();

        lastConfig = config;
        lastSignState = renderState.state;
        lastSignPos = renderState.pos;
        lastBlockData = renderState.blockData;
    }

    @Override
    protected boolean textureIsReadyToBlit(RenderState renderState)
    {
        if (renderState.modelRenderState.isAnimated()) return false;
        if (lastConfig != renderState.config) return false;
        if (lastSignState != renderState.state) return false;
        if (!lastSignPos.equals(renderState.pos)) return false;
        return lastBlockData.equals(renderState.blockData);
    }

    @Override
    protected float getTranslateY(int height, int guiScale)
    {
        return height / 2F;
    }

    @Override
    protected String getTextureLabel()
    {
        return "framedblocks block-in-ui";
    }

    @Override
    public Class<RenderState> getRenderStateClass()
    {
        return RenderState.class;
    }

    public record RenderState(
            AdvancedBlockModelRenderState modelRenderState,
            BlockState state,
            BlockPos pos,
            FramedBlockData blockData,
            RenderConfig config,
            int x0,
            int y0,
            int x1,
            int y1,
            float scale,
            @Nullable ScreenRectangle scissorArea,
            @Nullable ScreenRectangle bounds
    ) implements PictureInPictureRenderState
    {
        public static RenderState create(
                IFramedBlockEntity be,
                RenderConfig transform,
                int x0,
                int y0,
                int x1,
                int y1,
                float scale,
                @Nullable ScreenRectangle scissorArea
        )
        {
            return create(be, be.getBlockState(), transform, x0, y0, x1, y1, scale, scissorArea);
        }

        public static RenderState create(
                IFramedBlockEntity be,
                BlockState state,
                RenderConfig config,
                int x0,
                int y0,
                int x1,
                int y1,
                float scale,
                @Nullable ScreenRectangle scissorArea
        )
        {
            AdvancedBlockModelRenderState modelRenderState = new AdvancedBlockModelRenderState();
            ModelData modelData = config.useModelData ? be.getModelData(false, state) : ModelData.EMPTY;
            BlockAndTintGetter level = ClientUtils.asTintGetter(be.getLevel());
            BlockDisplayContext context = new FramedBlockDisplayContext(level, be.getBlockPos(), state, modelData);
            Minecraft.getInstance().getBlockModelResolver().update(modelRenderState, state, context);
            return new RenderState(
                    modelRenderState,
                    state,
                    be.getBlockPos(),
                    unpackData(modelData),
                    config,
                    x0,
                    y0,
                    x1,
                    y1,
                    scale,
                    scissorArea,
                    PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea)
            );
        }

        private static FramedBlockData unpackData(ModelData modelData)
        {
            AbstractFramedBlockData data = modelData.get(AbstractFramedBlockData.PROPERTY);
            return data != null ? data.unwrap(false) : FramedBlockData.EMPTY;
        }
    }

    public record RenderConfig(Consumer<PoseStack> poseTransform, Lighting.Entry lighting, boolean useModelData)
    {
        public static final RenderConfig DEFAULT = new RenderConfig(_ -> {}, Lighting.Entry.ITEMS_3D, true);

        @Override
        public boolean equals(Object obj)
        {
            return obj == this;
        }

        @Override
        public int hashCode()
        {
            return System.identityHashCode(this);
        }
    }
}
