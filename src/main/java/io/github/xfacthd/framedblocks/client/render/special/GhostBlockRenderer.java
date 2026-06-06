package io.github.xfacthd.framedblocks.client.render.special;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import io.github.xfacthd.framedblocks.api.ghost.RegisterGhostRenderBehavioursEvent;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.render.fakelevel.DelegatingBlockRenderFakeLevel;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.render.util.GhostVertexConsumer;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@SuppressWarnings("ConstantConditions")
public final class GhostBlockRenderer {
    private static final Map<Item, GhostRenderBehaviour> RENDER_BEHAVIOURS = new IdentityHashMap<>();
    private static final GhostRenderBehaviour DEFAULT_BEHAVIOUR = new GhostRenderBehaviour() {};
    private static final String DEBUG_NAME = FramedConstants.MOD_ID + "_ghost_block";
    private static final float SCALE = 1.0001F;
    private static final ByteBufferBuilder BUFFER_BUILDER = new ByteBufferBuilder(RenderType.TRANSIENT_BUFFER_SIZE);
    private static final ContextKey<List<GhostRenderState>> DATA_KEY = new ContextKey<>(Utils.id("placement_preview"));

    private static void onExtractRenderState(ExtractLevelRenderStateEvent event) {
        if (!ClientConfig.VIEW.showGhostBlocks()) {
            return;
        }

        ProfilerFiller profiler = Profiler.get();
        profiler.push(DEBUG_NAME);
        try {
            tryExtractGhostBlock(event.getRenderState(), profiler);
        } catch (Throwable t) {
            CrashReport report = CrashReport.forThrowable(t, "FramedBlocks: Extracting placement preview render state");

            CrashReportCategory category = report.addCategory("Placement preview context");
            mc().player.fillCrashReportCategory(category);
            category.setDetail("Rotation", mc().player.getYRot());
            category.setDetail("Direction", mc().player.getDirection());
            category.setDetail("Held item", Utils.formatItemStack(mc().player.getMainHandItem()));
            category.setDetail("Level", mc().level);
            category.setDetail("Hit result", Utils.formatHitResult(mc().hitResult));
            // Nuke pointless stacktrace spam
            category.trimStacktrace(category.getStacktrace().length);

            throw new ReportedException(report);
        } finally {
            profiler.pop(); // DEBUG_NAME
        }
    }

    private static void tryExtractGhostBlock(LevelRenderState renderState, ProfilerFiller profiler) {
        if (mc().player.isSpectator()) {
            return;
        }
        if (!(mc().hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        ItemStack stack = mc().player.getMainHandItem();
        if (stack.isEmpty()) {
            return;
        }

        GhostRenderBehaviour behaviour = RENDER_BEHAVIOURS.getOrDefault(stack.getItem(), DEFAULT_BEHAVIOUR);

        profiler.push("get_stack");
        ItemStack proxiedStack = behaviour.getProxiedStack(stack);
        profiler.pop(); //get_stack

        profiler.push("may_render");
        if (!behaviour.mayRender(stack, proxiedStack)) {
            profiler.pop(); //may_render
            return;
        }
        profiler.pop(); //may_render

        profiler.push("make_context");
        BlockPlaceContext context = new BlockPlaceContext(mc().player, InteractionHand.MAIN_HAND, stack, hit);
        BlockState hitState = mc().level.getBlockState(hit.getBlockPos());
        profiler.pop(); //make_context

        int passCount = behaviour.getPassCount(stack, proxiedStack);
        List<GhostRenderState> renderStates = new ArrayList<>(passCount);
        for (int pass = 0; pass < passCount; pass++) {
            if (!extractGhostBlock(renderStates, profiler, behaviour, stack, proxiedStack, hit, context, hitState, pass)) {
                break;
            }
        }
        if (!renderStates.isEmpty()) {
            renderState.setRenderData(DATA_KEY, renderStates);
        }
    }

    private static boolean extractGhostBlock(
            List<GhostRenderState> renderStates,
            ProfilerFiller profiler,
            GhostRenderBehaviour behaviour,
            ItemStack stack,
            ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext context,
            BlockState hitState,
            int renderPass
    ) {
        profiler.push("get_state");
        BlockState renderState = behaviour.getRenderState(stack, proxiedStack, hit, context, hitState, renderPass);
        profiler.pop(); //get_state
        if (renderState == null) {
            return true;
        }

        profiler.push("get_pos");
        BlockPos renderPos = behaviour.getRenderPos(stack, proxiedStack, hit, context, hitState, context.getClickedPos(), renderPass);
        profiler.popPush("can_render"); //get_pos
        if (renderPass == 0 && !behaviour.canRenderAt(stack, proxiedStack, hit, context, hitState, renderState, renderPos)) {
            profiler.pop(); //can_render
            return false;
        }
        profiler.pop(); //can_render

        profiler.push("get_camo");
        CamoList camo = behaviour.readCamo(stack, proxiedStack, renderPass);
        camo = behaviour.postProcessCamo(stack, proxiedStack, context, renderState, renderPass, camo);
        profiler.popPush("get_overlay"); // get_camo
        Holder<BlockOverlay> overlay = behaviour.readBlockOverlay(stack, proxiedStack, renderPass);
        profiler.popPush("build_modeldata"); //get_overlay
        ModelData modelData = behaviour.buildModelData(stack, proxiedStack, context, renderState, renderPass, camo, overlay);
        profiler.pop(); //get_camo

        profiler.push("append_modeldata");
        modelData = behaviour.appendModelData(stack, proxiedStack, context, renderState, renderPass, modelData);
        profiler.pop(); //append_modeldata

        profiler.push("get_render_offset");
        Vector3fc renderOffset = behaviour.getRenderOffset(stack, proxiedStack, context, renderState, renderPass, modelData);
        profiler.pop(); //get_render_offset

        renderStates.add(new GhostRenderState(mc().level, renderPos, renderState, renderOffset, modelData));

        return true;
    }

    private static void onRenderLevelStage(RenderLevelStageEvent.AfterTranslucentParticles event) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push(DEBUG_NAME);

        List<GhostRenderState> renderStates = event.getLevelRenderState().getRenderData(DATA_KEY);
        if (renderStates == null) {
            profiler.pop(); // DEBUG_NAME
            return;
        }

        profiler.push("setup_buffer");
        GhostBlockRenderConfig config = GhostBlockRenderConfig.get();
        RenderPipeline pipeline = config.getPipeline();
        BufferBuilder buffer = new BufferBuilder(BUFFER_BUILDER, pipeline.getVertexFormatMode(), pipeline.getVertexFormat());
        VertexConsumer ghostBuffer = new GhostVertexConsumer(buffer, ClientConfig.VIEW.getGhostRenderOpacity());
        profiler.pop(); //setup_buffer

        profiler.push("get_renderer");
        boolean ao = mc().options.ambientOcclusion().get();
        ModelBlockRenderer blockRenderer = new ModelBlockRenderer(ao, false, mc().getBlockColors());
        profiler.pop(); // get_renderer

        profiler.push("render_all");
        PoseStack poseStack = event.getPoseStack();
        for (GhostRenderState renderState : renderStates) {
            doRenderGhostBlock(blockRenderer, ghostBuffer, poseStack, profiler, renderState);
        }
        profiler.pop(); // render_all

        profiler.push("upload");
        MeshData meshData = buffer.build();
        if (meshData != null) {
            try (meshData) {
                uploadAndDraw(config, meshData);
            }
        }
        profiler.pop(); //upload

        profiler.pop();
    }

    private static void doRenderGhostBlock(ModelBlockRenderer blockRenderer, VertexConsumer builder, PoseStack poseStack, ProfilerFiller profiler, GhostRenderState renderState) {
        profiler.push("prepare");
        BlockPos pos = renderState.pos;
        BlockState state = renderState.state;
        Vec3 offset = Vec3.atLowerCornerOf(pos).subtract(mc().gameRenderer.getMainCamera().position());
        BlockQuadOutput output = (_, _, _, quad, instance) ->
                builder.putBakedQuad(poseStack.last(), quad, instance);
        profiler.pop(); //prepare

        profiler.push("render");
        BlockStateModel model = ModelUtils.getModel(state);
        poseStack.pushPose();
        Vector3fc renderOffset = renderState.offset;
        poseStack.translate(renderOffset.x(), renderOffset.y(), renderOffset.z());
        poseStack.translate(offset.x + .5, offset.y + .5, offset.z + .5);
        poseStack.scale(SCALE, SCALE, SCALE); // Scale up very slightly to avoid z-fighting with replaceable blocks like snow layers
        poseStack.translate(-.5F, -.5F, -.5F);
        blockRenderer.tesselateBlock(output, 0, 0, 0, renderState, pos, state, model, 0);
        poseStack.popPose();
        profiler.pop(); //render
    }

    private static void uploadAndDraw(GhostBlockRenderConfig config, MeshData meshData) {
        meshData.sortQuads(BUFFER_BUILDER, RenderSystem.getProjectionType().vertexSorting());

        RenderPipeline pipeline = config.getPipeline();
        VertexFormat vertexFormat = pipeline.getVertexFormat();
        GpuBuffer vertexBuffer = vertexFormat.uploadImmediateVertexBuffer(meshData.vertexBuffer());
        GpuBuffer indexBuffer;
        VertexFormat.IndexType indexType;
        if (meshData.indexBuffer() != null) {
            indexBuffer = vertexFormat.uploadImmediateIndexBuffer(meshData.indexBuffer());
            indexType = meshData.drawState().indexType();
        } else {
            RenderSystem.AutoStorageIndexBuffer autoIndexBuffer = RenderSystem.getSequentialBuffer(meshData.drawState().mode());
            indexBuffer = autoIndexBuffer.getBuffer(meshData.drawState().indexCount());
            indexType = autoIndexBuffer.type();
        }

        GpuBufferSlice dynamicUniforms = RenderSystem.getDynamicUniforms()
                .writeTransform(
                        RenderSystem.getModelViewMatrix(),
                        new Vector4f(1.0F, 1.0F, 1.0F, 1.0F),
                        new Vector3f(),
                        new Matrix4f()
                );

        RenderTarget target = config.getRenderTarget();
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                () -> DEBUG_NAME,
                target.getColorTextureView(),
                OptionalInt.empty(),
                target.getDepthTextureView(),
                OptionalDouble.empty()
        )) {
            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicUniforms);
            renderPass.setVertexBuffer(0, vertexBuffer);
            renderPass.setIndexBuffer(indexBuffer, indexType);

            config.setupSamplers(renderPass);

            renderPass.drawIndexed(0, 0, meshData.drawState().indexCount(), 1);
        }
    }

    public static void init() {
        ModLoader.postEvent(new RegisterGhostRenderBehavioursEvent(
                (behaviour, blocks) -> {
                    Preconditions.checkNotNull(behaviour, "GhostRenderBehaviour must be non-null");
                    Preconditions.checkArgument(blocks.length > 0, "At least one block must be provided to register a GhostRenderBehaviour");

                    for (Block block : blocks) {
                        Item item = block.asItem();
                        Preconditions.checkState(item instanceof BlockItem, "Block %s must have an associated BlockItem", block);
                        RENDER_BEHAVIOURS.put(item, behaviour);
                    }
                },
                (behaviour, items) -> {
                    Preconditions.checkNotNull(behaviour, "GhostRenderBehaviour must be non-null");
                    Preconditions.checkArgument(items.length > 0, "At least one item must be provided to register a GhostRenderBehaviour");

                    for (Item item : items) {
                        Preconditions.checkNotNull(item);
                        RENDER_BEHAVIOURS.put(item, behaviour);
                    }
                }
        ));

        NeoForge.EVENT_BUS.addListener(GhostBlockRenderer::onExtractRenderState);
        NeoForge.EVENT_BUS.addListener(GhostBlockRenderer::onRenderLevelStage);
    }

    public static GhostRenderBehaviour getBehaviour(Item item) {
        return RENDER_BEHAVIOURS.getOrDefault(item, DEFAULT_BEHAVIOUR);
    }

    private static Minecraft mc() {
        return Minecraft.getInstance();
    }

    private record GhostRenderState(
            ClientLevel realLevel,
            BlockPos pos,
            BlockState state,
            Vector3fc offset,
            ModelData modelData
    ) implements DelegatingBlockRenderFakeLevel { }

    private GhostBlockRenderer() { }
}
