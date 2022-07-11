package xfacthd.framedblocks.client.render;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xfacthd.framedblocks.api.ghost.CamoPair;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.client.ModelCache;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

import java.util.IdentityHashMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GhostBlockRenderer
{
    private static final RandomSource RANDOM = RandomSource.create();
    private static ModelData MODEL_DATA;
    private static final FramedBlockData GHOST_MODEL_DATA = new FramedBlockData();
    private static final FramedBlockData GHOST_MODEL_DATA_TWO = new FramedBlockData();
    private static final Map<Item, GhostRenderBehaviour> RENDER_BEHAVIOURS = new IdentityHashMap<>();
    private static final GhostRenderBehaviour DEFAULT_BEHAVIOUR = new GhostRenderBehaviour() {};

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        MODEL_DATA = ModelData.builder()
                .with(FramedBlockData.PROPERTY, GHOST_MODEL_DATA)
                .with(FramedDoubleBlockEntity.DATA_LEFT, ModelData.builder()
                        .with(FramedBlockData.PROPERTY, GHOST_MODEL_DATA)
                        .build()
                )
                .with(FramedDoubleBlockEntity.DATA_RIGHT, ModelData.builder()
                        .with(FramedBlockData.PROPERTY, GHOST_MODEL_DATA_TWO)
                        .build()
                )
                .build();

        GHOST_MODEL_DATA.setCamoState(Blocks.AIR.defaultBlockState());
        GHOST_MODEL_DATA_TWO.setCamoState(Blocks.AIR.defaultBlockState());

        MinecraftForge.EVENT_BUS.addListener(GhostBlockRenderer::onRenderLevelStage);
    }

    private static void onRenderLevelStage(final RenderLevelStageEvent event)
    {
        if (!ClientConfig.showGhostBlocks || event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES)
        {
            return;
        }

        if (!(mc().hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) { return; }

        ItemStack stack = mc().player.getMainHandItem();
        if (stack.isEmpty()) { return; }

        GhostRenderBehaviour behaviour = RENDER_BEHAVIOURS.getOrDefault(stack.getItem(), DEFAULT_BEHAVIOUR);

        ItemStack proxiedStack = behaviour.getProxiedStack(stack);
        if (!behaviour.mayRender(stack, proxiedStack)) { return; }

        BlockPlaceContext context = new BlockPlaceContext(mc().player, InteractionHand.MAIN_HAND, stack, hit);
        BlockState hitState = mc().level.getBlockState(hit.getBlockPos());

        drawGhostBlock(event, behaviour, stack, proxiedStack, hit, context, hitState, false);
    }

    private static void drawGhostBlock(
            RenderLevelStageEvent event,
            GhostRenderBehaviour behaviour,
            ItemStack stack,
            ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext context,
            BlockState hitState,
            boolean secondPass
    )
    {
        BlockState renderState = behaviour.getRenderState(stack, proxiedStack, hit, context, hitState, secondPass);
        if (renderState == null) { return; }

        BlockPos renderPos = behaviour.getRenderPos(stack, proxiedStack, hit, context, hitState, context.getClickedPos(), secondPass);
        if (!secondPass && !behaviour.canRenderAt(stack, proxiedStack, hit, context, hitState, renderState, renderPos)) { return; }

        CamoPair camo = behaviour.readCamo(stack, proxiedStack, secondPass);
        camo = behaviour.postProcessCamo(stack, proxiedStack, context, renderState, secondPass, camo);
        GHOST_MODEL_DATA.setCamoState(camo.getCamoOne());
        GHOST_MODEL_DATA_TWO.setCamoState(camo.getCamoTwo());

        ModelData modelData = behaviour.appendModelData(stack, proxiedStack, context, renderState, secondPass, MODEL_DATA);

        MultiBufferSource buffers = mc().renderBuffers().bufferSource();
        PoseStack mstack = event.getPoseStack();

        doRenderGhostBlock(mstack, buffers, renderPos, renderState, modelData);

        GHOST_MODEL_DATA.setCamoState(Blocks.AIR.defaultBlockState());
        GHOST_MODEL_DATA_TWO.setCamoState(Blocks.AIR.defaultBlockState());

        if (!secondPass && behaviour.hasSecondBlock(stack, proxiedStack))
        {
            drawGhostBlock(event, behaviour, stack, proxiedStack, hit, context, hitState, true);
        }
    }

    private static void doRenderGhostBlock(PoseStack mstack, MultiBufferSource buffers, BlockPos renderPos, BlockState renderState, ModelData modelData)
    {
        Vec3 offset = Vec3.atLowerCornerOf(renderPos).subtract(mc().gameRenderer.getMainCamera().getPosition());
        VertexConsumer builder = new GhostVertexConsumer(buffers.getBuffer(ForgeRenderTypes.TRANSLUCENT_ON_PARTICLES_TARGET.get()), 0xAA);

        BakedModel model = ModelCache.getModel(renderState);
        for (RenderType type : model.getRenderTypes(renderState, RANDOM, modelData))
        {
            doRenderGhostBlockInLayer(mstack, builder, renderPos, renderState, type, offset, modelData);
        }

        ((MultiBufferSource.BufferSource) buffers).endBatch(ForgeRenderTypes.TRANSLUCENT_ON_PARTICLES_TARGET.get());
    }

    private static void doRenderGhostBlockInLayer(PoseStack mstack, VertexConsumer builder, BlockPos renderPos, BlockState renderState, RenderType layer, Vec3 offset, ModelData modelData)
    {
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
                modelData,
                layer
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