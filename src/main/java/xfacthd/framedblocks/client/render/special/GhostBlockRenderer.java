package xfacthd.framedblocks.client.render.special;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.client.render.util.GhostVertexConsumer;
import xfacthd.framedblocks.common.config.ClientConfig;

import java.util.IdentityHashMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public final class GhostBlockRenderer
{
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Map<Item, GhostRenderBehaviour> RENDER_BEHAVIOURS = new IdentityHashMap<>();
    private static boolean locked = false;
    private static final GhostRenderBehaviour DEFAULT_BEHAVIOUR = new GhostRenderBehaviour() {};
    private static final String PROFILER_KEY = FramedConstants.MOD_ID + "_ghost_block";

    public static void onRenderLevelStage(final RenderLevelStageEvent event)
    {
        if (!ClientConfig.VIEW.showGhostBlocks() || event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES)
        {
            return;
        }

        ProfilerFiller profiler = mc().getProfiler();
        profiler.push(PROFILER_KEY);
        try
        {
            tryDrawGhostBlock(event.getPoseStack(), profiler);
        }
        catch (Throwable t)
        {
            CrashReport report = CrashReport.forThrowable(t, "FramedBlocks: Rendering placement preview");

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
        }
        profiler.pop();
    }

    private static void tryDrawGhostBlock(PoseStack poseStack, ProfilerFiller profiler)
    {
        if (mc().player.isSpectator())
        {
            return;
        }
        if (!(mc().hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK)
        {
            return;
        }

        ItemStack stack = mc().player.getMainHandItem();
        if (stack.isEmpty())
        {
            return;
        }

        GhostRenderBehaviour behaviour = RENDER_BEHAVIOURS.getOrDefault(stack.getItem(), DEFAULT_BEHAVIOUR);

        profiler.push("get_stack");
        ItemStack proxiedStack = behaviour.getProxiedStack(stack);
        profiler.pop(); //get_stack

        profiler.push("may_render");
        if (!behaviour.mayRender(stack, proxiedStack))
        {
            profiler.pop(); //may_render
            return;
        }
        profiler.pop(); //may_render

        profiler.push("make_context");
        BlockPlaceContext context = new BlockPlaceContext(mc().player, InteractionHand.MAIN_HAND, stack, hit);
        BlockState hitState = mc().level.getBlockState(hit.getBlockPos());
        profiler.pop(); //make_context

        int passCount = behaviour.getPassCount(stack, proxiedStack);
        for (int pass = 0; pass < passCount; pass++)
        {
            if (!drawGhostBlock(poseStack, profiler, behaviour, stack, proxiedStack, hit, context, hitState, pass))
            {
                break;
            }
        }
    }

    private static boolean drawGhostBlock(
            PoseStack poseStack,
            ProfilerFiller profiler,
            GhostRenderBehaviour behaviour,
            ItemStack stack,
            ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext context,
            BlockState hitState,
            int renderPass
    )
    {
        profiler.push("get_state");
        BlockState renderState = behaviour.getRenderState(stack, proxiedStack, hit, context, hitState, renderPass);
        profiler.pop(); //get_state
        if (renderState == null)
        {
            return true;
        }

        profiler.push("get_pos");
        BlockPos renderPos = behaviour.getRenderPos(stack, proxiedStack, hit, context, hitState, context.getClickedPos(), renderPass);
        profiler.popPush("can_render"); //get_pos
        if (renderPass == 0 && !behaviour.canRenderAt(stack, proxiedStack, hit, context, hitState, renderState, renderPos))
        {
            profiler.pop(); //can_render
            return false;
        }
        profiler.pop(); //can_render

        profiler.push("get_camo");
        CamoList camo = behaviour.readCamo(stack, proxiedStack, renderPass);
        camo = behaviour.postProcessCamo(stack, proxiedStack, context, renderState, renderPass, camo);
        profiler.popPush("build_modeldata"); //get_camo
        ModelData modelData = behaviour.buildModelData(stack, proxiedStack, context, renderState, renderPass, camo);
        profiler.pop(); //get_camo

        profiler.push("append_modeldata");
        modelData = behaviour.appendModelData(stack, proxiedStack, context, renderState, renderPass, modelData);
        profiler.pop(); //append_modeldata

        MultiBufferSource.BufferSource buffers = mc().renderBuffers().bufferSource();

        doRenderGhostBlock(poseStack, buffers, profiler, renderPos, renderState, modelData);

        return true;
    }

    private static void doRenderGhostBlock(
            PoseStack poseStack,
            MultiBufferSource.BufferSource buffers,
            ProfilerFiller profiler,
            BlockPos renderPos,
            BlockState renderState,
            ModelData modelData
    )
    {
        RenderType bufferType = ClientConfig.VIEW.useAltGhostRenderer() ?
                Sheets.translucentCullBlockSheet() :
                NeoForgeRenderTypes.TRANSLUCENT_ON_PARTICLES_TARGET.get();
        int opacity = ClientConfig.VIEW.getGhostRenderOpacity();

        profiler.push("buffer");
        Vec3 offset = Vec3.atLowerCornerOf(renderPos).subtract(mc().gameRenderer.getMainCamera().getPosition());
        VertexConsumer builder = new GhostVertexConsumer(buffers.getBuffer(bufferType), opacity);
        profiler.pop(); //buffer

        profiler.push("draw");
        BakedModel model = ModelUtils.getModel(renderState);
        poseStack.pushPose();
        poseStack.translate(offset.x, offset.y, offset.z);
        for (RenderType type : model.getRenderTypes(renderState, RANDOM, modelData))
        {
            doRenderGhostBlockInLayer(poseStack, builder, renderPos, renderState, type, modelData);
        }
        poseStack.popPose();
        profiler.pop(); //draw

        profiler.push("upload");
        RenderSystem.enableCull();
        buffers.endBatch(bufferType);
        profiler.pop(); //upload
    }

    private static void doRenderGhostBlockInLayer(
            PoseStack poseStack,
            VertexConsumer builder,
            BlockPos renderPos,
            BlockState renderState,
            RenderType layer,
            ModelData modelData
    )
    {
        mc().getBlockRenderer().renderBatched(
                renderState,
                renderPos,
                mc().level,
                poseStack,
                builder,
                false,
                RANDOM,
                modelData,
                layer
        );
    }



    public static synchronized void registerBehaviour(GhostRenderBehaviour behaviour, Block... blocks)
    {
        Preconditions.checkState(!locked, "GhostRenderBehaviour registry is locked!");

        Preconditions.checkNotNull(behaviour, "GhostRenderBehaviour must be non-null");
        Preconditions.checkNotNull(blocks, "Blocks array must be non-null to register a GhostRenderBehaviour");
        Preconditions.checkArgument(blocks.length > 0, "At least one block must be provided to register a GhostRenderBehaviour");

        for (Block block : blocks)
        {
            Item item = block.asItem();
            Preconditions.checkState(item instanceof BlockItem, "Block must have an associated BlockItem");
            registerBehaviour(behaviour, item);
        }
    }

    public static synchronized void registerBehaviour(GhostRenderBehaviour behaviour, Item... items)
    {
        Preconditions.checkState(!locked, "GhostRenderBehaviour registry is locked!");

        Preconditions.checkNotNull(behaviour, "GhostRenderBehaviour must be non-null");
        Preconditions.checkNotNull(items, "Items array must be non-null to register a GhostRenderBehaviour");
        Preconditions.checkArgument(items.length > 0, "At least one item must be provided to register a GhostRenderBehaviour");

        for (Item item : items)
        {
            RENDER_BEHAVIOURS.put(item, behaviour);
        }
    }

    public static GhostRenderBehaviour getBehaviour(Item item)
    {
        return RENDER_BEHAVIOURS.getOrDefault(item, DEFAULT_BEHAVIOUR);
    }

    public static void lockRegistration()
    {
        locked = true;
    }

    private static Minecraft mc()
    {
        return Minecraft.getInstance();
    }



    private GhostBlockRenderer() { }
}