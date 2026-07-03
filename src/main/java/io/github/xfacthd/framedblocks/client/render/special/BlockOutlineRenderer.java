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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class BlockOutlineRenderer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int DEFAULT_LINE_COLOR = ARGB.color(0x66, 0xFF000000);
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
            BlockState newState = state;
            if (newState.hasProperty(FramedProperties.SOLID)) {
                newState = newState.setValue(FramedProperties.SOLID, true);
            }
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
            event.addCustomRenderer((renderState, buffer, poseStack, translucentPass, _) -> {
                if (translucentPass == renderState.isTranslucent()) {
                    poseStack.pushPose();
                    poseStack.translate(offset.x, offset.y, offset.z);
                    poseStack.translate(.5, .5, .5);
                    renderer.rotateMatrix(poseStack, state);
                    poseStack.translate(-.5, -.5, -.5);

                    AbstractLineDrawer drawer = createDrawer(poseStack.last(), buffer, highContrast);
                    renderer.draw(state, data, drawer);
                    drawer.finish();

                    poseStack.popPose();
                }
                return true;
            });
        }
    }

    @SuppressWarnings("unchecked")
    private static @Nullable OutlineRenderer<Object> getRenderer(IBlockType type) {
        return (OutlineRenderer<Object>) OUTLINE_RENDERERS.get(type);
    }

    private static AbstractLineDrawer createDrawer(PoseStack.Pose pose, MultiBufferSource buffer, boolean highContrast) {
        if (highContrast) {
            return new HighContrastLineDrawer(pose, buffer);
        }
        return new DefaultLineDrawer(pose, buffer.getBuffer(RenderTypes.lines()), DEFAULT_LINE_COLOR);
    }

    public static void init() {
        ModLoader.postEvent(new RegisterOutlineRenderersEvent((type, renderer) -> {
            Preconditions.checkArgument(type.hasSpecialOutline(), "IBlockType %s returns false from IBlockType#hasSpecialOutline()", type);
            OUTLINE_RENDERERS.put(type, renderer);
        }));
    }

    public static boolean hasOutlineRenderer(IBlockType type) {
        return OUTLINE_RENDERERS.containsKey(type);
    }

    private static abstract class AbstractLineDrawer implements SimpleOutlineRenderer.LineDrawer {
        final PoseStack.Pose pose;
        final float lineWidth;

        AbstractLineDrawer(PoseStack.Pose pose) {
            this.pose = pose;
            this.lineWidth = Minecraft.getInstance().getWindow().getAppropriateLineWidth();
        }

        abstract void finish();

        final void drawLine(VertexConsumer builder, float x1, float y1, float z1, float x2, float y2, float z2, int color, float lineWidth) {
            float nX = x2 - x1;
            float nY = y2 - y1;
            float nZ = z2 - z1;
            float nLen = Mth.sqrt(nX * nX + nY * nY + nZ * nZ);

            nX = nX / nLen;
            nY = nY / nLen;
            nZ = nZ / nLen;

            builder.addVertex(pose, x1, y1, z1).setColor(color).setNormal(pose, nX, nY, nZ).setLineWidth(lineWidth);
            builder.addVertex(pose, x2, y2, z2).setColor(color).setNormal(pose, nX, nY, nZ).setLineWidth(lineWidth);
        }
    }

    static final class DefaultLineDrawer extends AbstractLineDrawer {
        private final VertexConsumer builder;
        private final int lineColor;

        DefaultLineDrawer(PoseStack.Pose pose, VertexConsumer builder, int lineColor) {
            super(pose);
            this.builder = builder;
            this.lineColor = lineColor;
        }

        @Override
        public void drawLine(float x1, float y1, float z1, float x2, float y2, float z2) {
            drawLine(builder, x1, y1, z1, x2, y2, z2, lineColor, lineWidth);
        }

        @Override
        public void drawLines(float[] vertices) {
            for (int i = 0; i < vertices.length; i += 6) {
                drawLine(vertices[i], vertices[i + 1], vertices[i + 2], vertices[i + 3], vertices[i + 4], vertices[i + 5]);
            }
        }

        @Override
        void finish() { }
    }

    private static final class HighContrastLineDrawer extends AbstractLineDrawer {
        private static final int LINE_STRIDE = 6;
        private static final int INITIAL_LINE_COUNT = 32;
        private static final String STRIDE_ERROR = "Packed vertex array size must be multiple of " + LINE_STRIDE;

        private final MultiBufferSource buffer;
        private float[] lines;
        private int pointer;

        HighContrastLineDrawer(PoseStack.Pose pose, MultiBufferSource buffer) {
            super(pose);
            this.buffer = buffer;
            this.lines = new float[INITIAL_LINE_COUNT * LINE_STRIDE];
        }

        @Override
        public void drawLine(float x1, float y1, float z1, float x2, float y2, float z2) {
            ensureCapacity(pointer + LINE_STRIDE);

            lines[pointer] = x1;
            lines[pointer + 1] = y1;
            lines[pointer + 2] = z1;
            lines[pointer + 3] = x2;
            lines[pointer + 4] = y2;
            lines[pointer + 5] = z2;

            pointer += LINE_STRIDE;
        }

        @Override
        public void drawLines(float[] vertices) {
            Preconditions.checkArgument(vertices.length % LINE_STRIDE == 0, STRIDE_ERROR);

            ensureCapacity(pointer + vertices.length);
            System.arraycopy(vertices, 0, lines, pointer, vertices.length);
            pointer += vertices.length;
        }

        private void ensureCapacity(int size) {
            if (size > lines.length) {
                int newSize = Math.max(lines.length + (INITIAL_LINE_COUNT * LINE_STRIDE), size);
                float[] newLines = new float[newSize];
                System.arraycopy(lines, 0, newLines, 0, lines.length);
                lines = newLines;
            }
        }

        @Override
        void finish() {
            drawBufferedLines(RenderTypes.secondaryBlockOutline(), CommonColors.BLACK, 7F);
            drawBufferedLines(RenderTypes.lines(), CommonColors.HIGH_CONTRAST_DIAMOND, lineWidth);
        }

        private void drawBufferedLines(RenderType renderType, int color, float lineWidth) {
            VertexConsumer builder = buffer.getBuffer(renderType);
            for (int i = 0; i < pointer; i += LINE_STRIDE) {
                drawLine(builder, lines[i], lines[i + 1], lines[i + 2], lines[i + 3], lines[i + 4], lines[i + 5], color, lineWidth);
            }
        }
    }

    private BlockOutlineRenderer() { }
}
