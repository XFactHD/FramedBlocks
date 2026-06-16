package io.github.xfacthd.framedblocks.client.render.special;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.render.outline.OutlineRenderer;
import io.github.xfacthd.framedblocks.api.render.outline.SimpleOutlineRenderer;
import io.github.xfacthd.framedblocks.api.render.outline.RegisterOutlineRenderersEvent;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import io.github.xfacthd.framedblocks.common.config.DevToolsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.neoforged.neoforge.client.submit.RenderPhaseKey;
import net.neoforged.neoforge.client.submit.RenderPhaseKeys;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class BlockOutlineRenderer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int DEFAULT_LINE_COLOR = ARGB.black(0x66);
    private static final Map<IBlockType, OutlineRenderer<?>> OUTLINE_RENDERERS = new IdentityHashMap<>();
    private static final Set<IBlockType> ERRORED_TYPES = new HashSet<>();

    public static void onRenderBlockHighlight(ExtractBlockOutlineRenderStateEvent event) {
        if (!ClientConfig.VIEW.useFancySelectionBoxes() && !DevToolsConfig.VIEW.isOcclusionShapeDebugRenderingEnabled()) {
            return;
        }

        BlockHitResult result = event.getHitResult();
        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
        BlockState state = level.getBlockState(result.getBlockPos());
        if (!(state.getBlock() instanceof IFramedBlock block)) {
            return;
        }

        if (DevToolsConfig.VIEW.isOcclusionShapeDebugRenderingEnabled()) {
            BlockState newState = state.trySetValue(FramedProperties.SOLID, true);
            event.getLevelRenderState().blockOutlineRenderState = new BlockOutlineRenderState(
                    result.getBlockPos(),
                    event.isInTranslucentPass(),
                    event.isHighContrast(),
                    newState.getOcclusionShape(),
                    List.of()
            );
            event.setCanceled(true);
            return;
        }

        IBlockType type = block.getBlockType();
        if (type.hasSpecialOutline()) {
            OutlineRenderer<Object> renderer = getRenderer(type);
            if (renderer == null) {
                if (ERRORED_TYPES.add(type)) {
                    LOGGER.error("IBlockType '{}' requests custom outline rendering but no OutlineRender was registered!", type.getName());
                }
                return;
            }

            Object data = renderer.extractOutlineData(state, level, result.getBlockPos());
            if (data == null) {
                return;
            }

            Vec3 offset = Vec3.atLowerCornerOf(result.getBlockPos()).subtract(event.getCamera().position());
            boolean highContrast = event.isHighContrast();
            event.addCustomRenderer((renderState, submitNodeCollector, poseStack, _) -> {
                poseStack.pushPose();
                poseStack.translate(offset.x + .5, offset.y + .5, offset.z + .5);
                renderer.rotateMatrix(poseStack, state);
                poseStack.translate(-.5, -.5, -.5);

                float lineWidth = Minecraft.getInstance().gameRenderer.gameRenderState().windowRenderState.appropriateLineWidth;
                submitLineDraw(submitNodeCollector, poseStack, highContrast, renderState.isTranslucent(), lineWidth, drawer -> renderer.draw(state, data, drawer));

                poseStack.popPose();

                return true;
            });
        }
    }

    @SuppressWarnings("unchecked")
    private static @Nullable OutlineRenderer<Object> getRenderer(IBlockType type) {
        return (OutlineRenderer<Object>) OUTLINE_RENDERERS.get(type);
    }

    private static void submitLineDraw(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, boolean highContrast, boolean translucent, float lineWidth, LineRenderer renderer) {
        RenderPhaseKey<SubmitNode> phase = translucent ? RenderPhaseKeys.AFTER_TERRAIN : RenderPhaseKeys.SHAPE_OUTLINES;
        if (highContrast) {
            submitLineDraw(submitNodeCollector, poseStack, RenderTypes.secondaryBlockOutline(), CommonColors.BLACK, 7F, phase, renderer);
            submitLineDraw(submitNodeCollector, poseStack, RenderTypes.lines(), CommonColors.HIGH_CONTRAST_DIAMOND, lineWidth, phase, renderer);
        } else {
            submitLineDraw(submitNodeCollector, poseStack, RenderTypes.lines(), DEFAULT_LINE_COLOR, lineWidth, phase, renderer);
        }
    }

    static void submitLineDraw(SubmitNodeCollector collector, PoseStack poseStack, RenderType renderType, int color, float lineWidth, RenderPhaseKey<SubmitNode> phase, LineRenderer renderer) {
        SubmitNodeCollector.CustomGeometryRenderer customRenderer = (pose, buffer) -> renderer.accept(new LineDrawerImpl(pose, buffer, color, lineWidth));
        collector.submitSpecial(phase, new CustomFeatureRenderer.Submit(poseStack.last().copy(), renderType, customRenderer));
    }

    public static void init() {
        ModLoader.postEvent(new RegisterOutlineRenderersEvent((type, renderer) -> {
            Preconditions.checkArgument(
                    type.hasSpecialOutline(),
                    "IBlockType %s doesn't return true from IBlockType#hasSpecialOutline()",
                    type
            );
            OUTLINE_RENDERERS.put(type, renderer);
        }));
    }

    public static boolean hasOutlineRenderer(IBlockType type) {
        return OUTLINE_RENDERERS.containsKey(type);
    }

    private record LineDrawerImpl(PoseStack.Pose pose, VertexConsumer buffer, int lineColor, float lineWidth, Vector3f normal) implements SimpleOutlineRenderer.LineDrawer {
        private static final int LINE_STRIDE = 6;
        private static final String STRIDE_ERROR = "Packed vertex array size must be multiple of " + LINE_STRIDE;

        LineDrawerImpl(PoseStack.Pose pose, VertexConsumer buffer, int lineColor, float lineWidth) {
            this(pose, buffer, lineColor, lineWidth, new Vector3f());
        }

        @Override
        public void drawLine(float x1, float y1, float z1, float x2, float y2, float z2) {
            normal.set(x2 - x1, y2 - y1, z2 - z1).normalize();

            buffer.addVertex(pose, x1, y1, z1).setColor(lineColor).setNormal(pose, normal).setLineWidth(lineWidth);
            buffer.addVertex(pose, x2, y2, z2).setColor(lineColor).setNormal(pose, normal).setLineWidth(lineWidth);
        }

        @Override
        public void drawLines(float[] vertices) {
            Preconditions.checkArgument(vertices.length % LINE_STRIDE == 0, STRIDE_ERROR);

            for (int i = 0; i < vertices.length; i += LINE_STRIDE) {
                drawLine(vertices[i], vertices[i + 1], vertices[i + 2], vertices[i + 3], vertices[i + 4], vertices[i + 5]);
            }
        }
    }

    interface LineRenderer extends Consumer<OutlineRenderer.LineDrawer> { }

    private BlockOutlineRenderer() { }
}
