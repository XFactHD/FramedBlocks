package xfacthd.framedblocks.client.render;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xfacthd.framedblocks.api.ghost.CamoPair;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GhostBlockRenderer
{
    private static final Random RANDOM = new Random();
    private static final FramedBlockData GHOST_MODEL_DATA = new FramedBlockData(true);
    private static final FramedBlockData GHOST_MODEL_DATA_TWO = new FramedBlockData(true);
    private static final Map<Item, GhostRenderBehaviour> RENDER_BEHAVIOURS = new IdentityHashMap<>();
    private static final GhostRenderBehaviour DEFAULT_BEHAVIOUR = new GhostRenderBehaviour() {};
    private static final String PROFILER_KEY = FramedConstants.MOD_ID + "_ghost_block";

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        GHOST_MODEL_DATA.setCamoState(Blocks.AIR.defaultBlockState());
        GHOST_MODEL_DATA_TWO.setCamoState(Blocks.AIR.defaultBlockState());

        //Needed to render ghosts of double blocks
        GHOST_MODEL_DATA.setData(FramedDoubleBlockEntity.DATA_LEFT, GHOST_MODEL_DATA);
        GHOST_MODEL_DATA.setData(FramedDoubleBlockEntity.DATA_RIGHT, GHOST_MODEL_DATA_TWO);

        MinecraftForge.EVENT_BUS.addListener(GhostBlockRenderer::onRenderStage);
    }

    public static void onRenderStage(final RenderLevelStageEvent event)
    {
        if (!ClientConfig.showGhostBlocks || event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES)
        {
            return;
        }

        mc().getProfiler().push(PROFILER_KEY);
        tryDrawGhostBlock(event.getPoseStack());
        mc().getProfiler().pop();
    }

    private static void tryDrawGhostBlock(PoseStack poseStack)
    {
        if (!(mc().hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) { return; }

        ItemStack stack = mc().player.getMainHandItem();
        if (stack.isEmpty()) { return; }

        GhostRenderBehaviour behaviour = RENDER_BEHAVIOURS.getOrDefault(stack.getItem(), DEFAULT_BEHAVIOUR);

        mc().getProfiler().push("get_stack");
        ItemStack proxiedStack = behaviour.getProxiedStack(stack);
        mc().getProfiler().pop(); //get_stack

        mc().getProfiler().push("may_render");
        if (!behaviour.mayRender(stack, proxiedStack))
        {
            mc().getProfiler().pop(); //may_render
            return;
        }
        mc().getProfiler().pop(); //may_render

        mc().getProfiler().push("make_context");
        BlockPlaceContext context = new BlockPlaceContext(mc().player, InteractionHand.MAIN_HAND, stack, hit);
        BlockState hitState = mc().level.getBlockState(hit.getBlockPos());
        mc().getProfiler().pop(); //make_context

        drawGhostBlock(poseStack, behaviour, stack, proxiedStack, hit, context, hitState, false);
    }

    private static void drawGhostBlock(
            PoseStack poseStack,
            GhostRenderBehaviour behaviour,
            ItemStack stack,
            ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext context,
            BlockState hitState,
            boolean secondPass
    )
    {
        mc().getProfiler().push("get_state");
        BlockState renderState = behaviour.getRenderState(stack, proxiedStack, hit, context, hitState, secondPass);
        mc().getProfiler().pop(); //get_state
        if (renderState == null) { return; }

        mc().getProfiler().push("get_pos");
        BlockPos renderPos = behaviour.getRenderPos(stack, proxiedStack, hit, context, hitState, context.getClickedPos(), secondPass);
        mc().getProfiler().popPush("can_render"); //get_pos
        if (!secondPass && !behaviour.canRenderAt(stack, proxiedStack, hit, context, hitState, renderState, renderPos))
        {
            mc().getProfiler().pop(); //can_render
            return;
        }
        mc().getProfiler().pop(); //can_render

        mc().getProfiler().push("get_camo");
        CamoPair camo = behaviour.readCamo(stack, proxiedStack, secondPass);
        camo = behaviour.postProcessCamo(stack, proxiedStack, context, renderState, secondPass, camo);
        GHOST_MODEL_DATA.setCamoState(camo.getCamoOne());
        GHOST_MODEL_DATA_TWO.setCamoState(camo.getCamoTwo());
        mc().getProfiler().pop(); //get_camo

        mc().getProfiler().push("append_modeldata");
        IModelData modelData = behaviour.appendModelData(stack, proxiedStack, context, renderState, secondPass, GHOST_MODEL_DATA);
        mc().getProfiler().pop(); //append_modeldata

        MultiBufferSource buffers = mc().renderBuffers().bufferSource();

        doRenderGhostBlock(poseStack, buffers, renderPos, renderState, camo, modelData);

        GHOST_MODEL_DATA.setCamoState(Blocks.AIR.defaultBlockState());
        GHOST_MODEL_DATA_TWO.setCamoState(Blocks.AIR.defaultBlockState());

        if (!secondPass && behaviour.hasSecondBlock(stack, proxiedStack))
        {
            drawGhostBlock(poseStack, behaviour, stack, proxiedStack, hit, context, hitState, true);
        }
    }

    private static void doRenderGhostBlock(PoseStack mstack, MultiBufferSource buffers, BlockPos renderPos, BlockState renderState, CamoPair camoPair, IModelData modelData)
    {
        mc().getProfiler().push("buffer");
        Vec3 offset = Vec3.atLowerCornerOf(renderPos).subtract(mc().gameRenderer.getMainCamera().getPosition());
        VertexConsumer builder = new GhostVertexConsumer(buffers.getBuffer(CustomRenderType.GHOST_BLOCK), 0xAA);
        mc().getProfiler().pop(); //buffer

        mc().getProfiler().push("draw");
        BlockState camoState = camoPair.getCamoOne();
        BlockState camoStateTwo = camoPair.getCamoTwo();
        if (camoState.isAir() && camoStateTwo.isAir())
        {
            doRenderGhostBlockInLayer(mstack, builder, renderPos, renderState, RenderType.cutout(), offset, modelData);
        }
        else
        {
            for (RenderType type : RenderType.chunkBufferLayers())
            {
                if (canRenderInLayer(camoState, type) || canRenderInLayer(camoStateTwo, type))
                {
                    doRenderGhostBlockInLayer(mstack, builder, renderPos, renderState, type, offset, modelData);
                }
            }
        }
        mc().getProfiler().pop(); //draw

        mc().getProfiler().push("upload");
        ((MultiBufferSource.BufferSource) buffers).endBatch(CustomRenderType.GHOST_BLOCK);
        ForgeHooksClient.setRenderType(null);
        mc().getProfiler().pop(); //upload
    }

    private static boolean canRenderInLayer(BlockState camoState, RenderType layer)
    {
        if (camoState.isAir()) { return layer == RenderType.cutout(); }
        return ItemBlockRenderTypes.canRenderInLayer(camoState, layer);
    }

    private static void doRenderGhostBlockInLayer(PoseStack mstack, VertexConsumer builder, BlockPos renderPos, BlockState renderState, RenderType layer, Vec3 offset, IModelData modelData)
    {
        ForgeHooksClient.setRenderType(layer);

        mstack.pushPose();
        mstack.translate(offset.x, offset.y, offset.z);

        mc().getBlockRenderer().renderBatched(
                renderState,
                renderPos,
                mc().level,
                mstack,
                builder,
                false,
                RANDOM,
                modelData
        );

        mstack.popPose();
    }



    public static synchronized void registerBehaviour(GhostRenderBehaviour behaviour, Block... blocks)
    {
        Preconditions.checkNotNull(behaviour, "GhostRenderBehaviour must be non-null");
        Preconditions.checkNotNull(blocks, "Blocks array must be non-null to register a GhostRenderBehaviour");
        Preconditions.checkState(blocks.length > 0, "At least one block must be provided to register a GhostRenderBehaviour");

        for (Block block : blocks)
        {
            Item item = block.asItem();
            Preconditions.checkState(item instanceof BlockItem, "Block must have an associated BlockItem");
            registerBehaviour(behaviour, item);
        }
    }

    public static synchronized void registerBehaviour(GhostRenderBehaviour behaviour, Item... items)
    {
        Preconditions.checkNotNull(behaviour, "GhostRenderBehaviour must be non-null");
        Preconditions.checkNotNull(items, "Items array must be non-null to register a GhostRenderBehaviour");
        Preconditions.checkState(items.length > 0, "At least one item must be provided to register a GhostRenderBehaviour");

        for (Item item : items)
        {
            RENDER_BEHAVIOURS.put(item, behaviour);
        }
    }

    public static GhostRenderBehaviour getBehaviour(Item item)
    {
        return RENDER_BEHAVIOURS.getOrDefault(item, DEFAULT_BEHAVIOUR);
    }

    private static Minecraft mc() { return Minecraft.getInstance(); }



    private GhostBlockRenderer() { }
}