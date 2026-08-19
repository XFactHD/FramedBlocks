package io.github.xfacthd.framedblocks.client.render.special;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import io.github.xfacthd.framedblocks.api.ghost.RegisterGhostRenderBehavioursEvent;
import io.github.xfacthd.framedblocks.api.render.fakelevel.DelegatingBlockRenderFakeLevel;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
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
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.neoforged.neoforge.client.submit.RenderPhaseKeys;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class GhostBlockRenderer {
    private static final Map<Item, GhostRenderBehaviour> RENDER_BEHAVIOURS = new IdentityHashMap<>();
    private static final GhostRenderBehaviour DEFAULT_BEHAVIOUR = new GhostRenderBehaviour() {};
    static final String DEBUG_NAME = FramedConstants.MOD_ID + "_ghost_block";
    private static final float SCALE = 1.0001F;
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

            Minecraft minecraft = Minecraft.getInstance();
            Player player = Objects.requireNonNull(minecraft.player);
            CrashReportCategory category = report.addCategory("Placement preview context");
            player.fillCrashReportCategory(category);
            category.setDetail("Rotation", player.getYRot());
            category.setDetail("Direction", player.getDirection());
            category.setDetail("Held item", Utils.formatItemStack(player.getMainHandItem()));
            category.setDetail("Level", minecraft.level);
            category.setDetail("Hit result", Utils.formatHitResult(minecraft.hitResult));

            throw new ReportedException(report);
        } finally {
            profiler.pop(); // DEBUG_NAME
        }
    }

    private static void tryExtractGhostBlock(LevelRenderState renderState, ProfilerFiller profiler) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = Objects.requireNonNull(minecraft.player);
        if (player.isSpectator()) {
            return;
        }
        if (!(minecraft.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
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
        BlockPlaceContext context = behaviour.buildPlaceContext(player, stack, proxiedStack, hit);
        ClientLevel level = Objects.requireNonNull(minecraft.level);
        BlockState hitState = level.getBlockState(hit.getBlockPos());
        profiler.pop(); //make_context

        int passCount = behaviour.getPassCount(stack, proxiedStack);
        List<GhostRenderState> renderStates = new ArrayList<>(passCount);
        for (int pass = 0; pass < passCount; pass++) {
            if (!extractGhostBlock(renderStates, profiler, level, behaviour, stack, proxiedStack, hit, context, hitState, pass)) {
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
            ClientLevel level,
            GhostRenderBehaviour behaviour,
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
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

        renderStates.add(new GhostRenderState(level, renderPos, renderState, renderOffset, modelData));

        return true;
    }

    private static void onSubmitCustomGeometry(SubmitCustomGeometryEvent event) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push(DEBUG_NAME);

        List<GhostRenderState> renderStates = event.getLevelRenderState().getRenderData(DATA_KEY);
        if (renderStates != null) {
            PoseStack poseStack = event.getPoseStack();
            Vec3 cameraPos = event.getLevelRenderState().cameraRenderState.pos;
            SubmitNodeCollector submitNodeCollector = event.getSubmitNodeCollector();
            for (GhostRenderState renderState : renderStates) {
                submitGhostBlock(submitNodeCollector, poseStack, renderState, cameraPos);
            }
        }

        profiler.pop(); // DEBUG_NAME
    }

    private static void submitGhostBlock(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, GhostRenderState renderState, Vec3 cameraPos) {
        Vec3 offset = Vec3.atLowerCornerOf(renderState.pos).subtract(cameraPos);

        poseStack.pushPose();
        Vector3fc renderOffset = renderState.offset;
        poseStack.translate(offset.x + renderOffset.x() + .5, offset.y + renderOffset.y() + .5, offset.z + renderOffset.z() + .5);
        poseStack.scale(SCALE, SCALE, SCALE); // Scale up very slightly to avoid z-fighting with replaceable blocks like snow layers
        poseStack.translate(-.5F, -.5F, -.5F);
        submitNodeCollector.submitSpecial(RenderPhaseKeys.AFTER_TERRAIN, new GhostBlockFeatureRenderer.Submit(poseStack.last().copy(), renderState));
        poseStack.popPose();
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
        NeoForge.EVENT_BUS.addListener(GhostBlockRenderer::onSubmitCustomGeometry);
    }

    public static GhostRenderBehaviour getBehaviour(Item item) {
        return RENDER_BEHAVIOURS.getOrDefault(item, DEFAULT_BEHAVIOUR);
    }

    record GhostRenderState(
            ClientLevel realLevel,
            BlockPos pos,
            BlockState state,
            Vector3fc offset,
            ModelData modelData
    ) implements DelegatingBlockRenderFakeLevel { }

    private GhostBlockRenderer() { }
}
