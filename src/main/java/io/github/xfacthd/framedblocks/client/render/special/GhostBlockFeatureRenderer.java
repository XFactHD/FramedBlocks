package io.github.xfacthd.framedblocks.client.render.special;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import io.github.xfacthd.framedblocks.client.render.util.GhostVertexConsumer;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderer;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;

// TODO: move ghost render infrastructure to library
public final class GhostBlockFeatureRenderer implements FeatureRenderer<GhostBlockFeatureRenderer.Submit> {
    public static final FeatureRendererType<Submit> TYPE = FeatureRendererType.create("FramedBlocks Ghost Block");

    private final PoseStack poseStack = new PoseStack();
    private final RendererCache rendererCache = new RendererCache();
    private final List<Group> groups = new ArrayList<>();
    @Nullable
    private GpuBufferSlice dynamicTransforms;

    @Override
    public void prepareGroup(FeatureFrameContext context, List<Submit> submits, boolean strictlyOrdered) {
        if (submits.isEmpty()) {
            return;
        }

        ModelBlockRenderer blockRenderer = rendererCache.get(context.options().ambientOcclusion, context.blockColors());

        GhostBlockRenderConfig config = GhostBlockRenderConfig.get();
        RenderPipeline pipeline = config.getPipeline();
        StagedVertexBuffer.Draw draw = context.stagedVertexBuffer().appendDraw(
                Objects.requireNonNull(pipeline.getVertexFormatBinding(0)),
                pipeline.getPrimitiveTopology(),
                VertexSorting.DISTANCE_TO_ORIGIN
        );
        VertexConsumer rawBuilder = context.stagedVertexBuffer().getVertexBuilder(draw);
        VertexConsumer builder = new GhostVertexConsumer(rawBuilder, ClientConfig.VIEW.getGhostRenderOpacity());

        for (Submit submit : submits) {
            poseStack.setIdentity();
            PoseStack.Pose pose = poseStack.last();
            pose.set(submit.pose);

            GhostBlockRenderer.GhostRenderState renderState = submit.renderState;
            BlockState state = renderState.state();
            BlockStateModel model = context.blockStateModelSet().get(state);
            BlockQuadOutput output = (_, _, _, quad, instance) ->
                    builder.putBakedQuad(pose, quad, instance);

            blockRenderer.tesselateBlock(output, 0, 0, 0, renderState, renderState.pos(), state, model, 0);
        }

        groups.add(new Group(config, draw));
    }

    @Override
    public void finishPrepare(FeatureFrameContext context) {
        dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy());
    }

    @Override
    public void executeGroup(FeatureFrameContext context, int groupIndex, List<Submit> submits, boolean strictlyOrdered) {
        Group group = groups.get(groupIndex);
        GhostBlockRenderConfig config = group.renderConfig;
        StagedVertexBuffer.ExecuteInfo executeInfo = context.stagedVertexBuffer().getExecuteInfo(group.draw);
        if (executeInfo == null) {
            return;
        }

        RenderTarget target = config.getOutputTarget().getRenderTarget();
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                () -> GhostBlockRenderer.DEBUG_NAME,
                Objects.requireNonNull(target.getColorTextureView()),
                Optional.empty(),
                target.getDepthTextureView(),
                OptionalDouble.empty()
        )) {
            renderPass.setPipeline(config.getPipeline());
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", Objects.requireNonNull(dynamicTransforms));
            renderPass.setVertexBuffer(0, executeInfo.vertexBuffer().slice());
            renderPass.setIndexBuffer(executeInfo.indexBuffer(), executeInfo.indexType());
            config.setupSamplers(context, renderPass);
            renderPass.drawIndexed(executeInfo.indexCount(), 1, executeInfo.firstIndex(), executeInfo.baseVertex(), 0);
        }
    }

    @Override
    public void finishExecute(FeatureFrameContext context) {
        groups.clear();
        dynamicTransforms = null;
    }

    private record Group(GhostBlockRenderConfig renderConfig, StagedVertexBuffer.Draw draw) { }

    public record Submit(PoseStack.Pose pose, GhostBlockRenderer.GhostRenderState renderState) implements TranslucentSubmit {
        @Override
        public float distanceToCameraSq() {
            return TranslucentSubmit.computeDistanceToCameraSq(pose.pose(), 0.5F, 0.5F, 0.5F);
        }

        @Override
        public FeatureRendererType<? extends TranslucentSubmit> featureType() {
            return TYPE;
        }
    }

    private static final class RendererCache {
        @Nullable
        private ModelBlockRenderer renderer;
        boolean lastAo;
        @Nullable
        private BlockColors lastBlockColors;

        ModelBlockRenderer get(boolean ao, BlockColors blockColors) {
            if (renderer == null || ao != lastAo || blockColors != lastBlockColors) {
                renderer = new ModelBlockRenderer(ao, false, blockColors);
                lastAo = ao;
                lastBlockColors = blockColors;
            }
            return renderer;
        }
    }
}
