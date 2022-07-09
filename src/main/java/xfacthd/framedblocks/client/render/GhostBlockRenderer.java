package xfacthd.framedblocks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.client.ModelCache;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("ConstantConditions")
@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GhostBlockRenderer
{
    private static final RandomSource RANDOM = RandomSource.create();
    private static ModelData MODEL_DATA;
    private static final FramedBlockData GHOST_MODEL_DATA = new FramedBlockData();
    private static final FramedBlockData GHOST_MODEL_DATA_TWO = new FramedBlockData();

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

        MinecraftForge.EVENT_BUS.addListener(GhostBlockRenderer::drawGhostBlock);
    }

    public static void drawGhostBlock(final RenderLevelStageEvent event)
    {
        if (!ClientConfig.showGhostBlocks || event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) { return; }

        MultiBufferSource buffers = mc().renderBuffers().bufferSource();
        PoseStack mstack = event.getPoseStack();

        HitResult mouseOver = mc().hitResult;
        if (mouseOver == null || mouseOver.getType() != HitResult.Type.BLOCK) { return; }

        BlockHitResult target = (BlockHitResult) mouseOver;

        Block block;
        boolean blueprint = false;
        boolean rail = false;

        ItemStack stack = mc().player.getMainHandItem();
        if (stack.getItem() instanceof FramedBlueprintItem)
        {
            block = FramedBlueprintItem.getTargetBlock(stack);
            blueprint = true;
        }
        else if (stack.getItem() instanceof BlockItem item)
        {
            block = item.getBlock();
            rail = item == Items.RAIL;
        }
        else
        {
            return;
        }

        if (!(block instanceof IFramedBlock) && !rail) { return; }

        boolean doRender;
        BlockPos renderPos;
        BlockState renderState;

        if (!blueprint && (renderState = tryBuildDoublePanel(target, block)) != null)
        {
            doRender = true;
            renderPos = target.getBlockPos();
        }
        else if (!blueprint && (renderState = tryBuildDoubleSlab(target, block)) != null)
        {
            doRender = true;
            renderPos = target.getBlockPos();
        }
        else if (rail)
        {
            BlockState state = mc().level.getBlockState(target.getBlockPos());
            if (state.getBlock() == FBContent.blockFramedSlope.get())
            {
                renderPos = target.getBlockPos();

                RailShape shape = FramedRailSlopeBlock.shapeFromDirection(state.getValue(FramedProperties.FACING_HOR));
                renderState = block.defaultBlockState().setValue(BlockStateProperties.RAIL_SHAPE, shape);

                BlockState railSlope = FBContent.blockFramedRailSlope.get()
                        .defaultBlockState()
                        .setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, shape);
                doRender = railSlope.canSurvive(mc().level, renderPos);
            }
            else
            {
                doRender = false;
                renderPos = BlockPos.ZERO;
                renderState = Blocks.AIR.defaultBlockState();
            }
        }
        else
        {
            BlockPlaceContext context = new BlockPlaceContext(mc().player, InteractionHand.MAIN_HAND, stack, target);

            renderPos = context.getClickedPos();
            renderState = getStateForPlacement(context, block);
            doRender = renderState != null &&
                    mc().level.isUnobstructed(renderState, renderPos, CollisionContext.of(mc().player)) &&
                    mc().level.getBlockState(renderPos).canBeReplaced(context);
        }

        if (doRender)
        {
            BlockState camoState;
            BlockState camoStateTwo;
            if (blueprint)
            {
                CompoundTag beTag = stack.getOrCreateTagElement("blueprint_data").getCompound("camo_data");
                camoState = CamoContainer.load(beTag.getCompound("camo")).getState();

                if (renderState.getBlock() instanceof AbstractFramedDoubleBlock)
                {
                    camoStateTwo = CamoContainer.load(beTag.getCompound("camo_two")).getState();

                    if (block == FBContent.blockFramedDoublePanel.get() && renderState.getValue(FramedProperties.FACING_NE) != mc().player.getDirection())
                    {
                        BlockState temp = camoState;
                        camoState = camoStateTwo;
                        camoStateTwo = temp;
                    }

                    GHOST_MODEL_DATA.setCamoState(camoState);
                    GHOST_MODEL_DATA_TWO.setCamoState(camoStateTwo);
                }
                else
                {
                    GHOST_MODEL_DATA.setCamoState(camoState);
                }
            }

            doRenderGhostBlock(mstack, buffers, renderPos, renderState);

            if (renderState.getBlock() instanceof FramedDoorBlock)
            {
                if (blueprint)
                {
                    CompoundTag beTag = stack.getOrCreateTagElement("blueprint_data").getCompound("camo_data_two");
                    camoState = CamoContainer.load(beTag.getCompound("camo")).getState();
                    GHOST_MODEL_DATA.setCamoState(camoState);
                }

                doRenderGhostBlock(mstack, buffers, renderPos.above(), renderState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
            }

            if (blueprint)
            {
                GHOST_MODEL_DATA.setCamoState(Blocks.AIR.defaultBlockState());
                GHOST_MODEL_DATA_TWO.setCamoState(Blocks.AIR.defaultBlockState());
            }
        }
    }

    private static void doRenderGhostBlock(PoseStack mstack, MultiBufferSource buffers, BlockPos renderPos, BlockState renderState)
    {
        Vec3 offset = Vec3.atLowerCornerOf(renderPos).subtract(mc().gameRenderer.getMainCamera().getPosition());
        VertexConsumer builder = new GhostVertexConsumer(buffers.getBuffer(CustomRenderType.GHOST_BLOCK), 0xAA);

        BakedModel model = ModelCache.getModel(renderState);
        for (RenderType type : model.getRenderTypes(renderState, RANDOM, MODEL_DATA))
        {
            doRenderGhostBlockInLayer(mstack, builder, renderPos, renderState, type, offset);
        }

        ((MultiBufferSource.BufferSource) buffers).endBatch(CustomRenderType.GHOST_BLOCK);
    }

    private static void doRenderGhostBlockInLayer(PoseStack mstack, VertexConsumer builder, BlockPos renderPos, BlockState renderState, RenderType layer, Vec3 offset)
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
                MODEL_DATA,
                layer
        );

        mstack.popPose();
    }



    private static BlockState tryBuildDoubleSlab(BlockHitResult trace, Block heldBlock)
    {
        if (heldBlock != FBContent.blockFramedSlab.get()) { return null; }

        BlockState target = mc().level.getBlockState(trace.getBlockPos());
        if (target.getBlock() == heldBlock)
        {
            boolean top = target.getValue(FramedProperties.TOP);
            if ((top && trace.getDirection() == Direction.DOWN) || (!top && trace.getDirection() == Direction.UP))
            {
                return target.setValue(FramedProperties.TOP, !top);
            }
        }
        return null;
    }

    private static BlockState tryBuildDoublePanel(BlockHitResult trace, Block heldBlock)
    {
        if (heldBlock != FBContent.blockFramedPanel.get()) { return null; }

        BlockState target = mc().level.getBlockState(trace.getBlockPos());
        if (target.getBlock() == heldBlock)
        {
            Direction dir = target.getValue(FramedProperties.FACING_HOR);
            if (dir.getOpposite() == trace.getDirection())
            {
                return target.setValue(FramedProperties.FACING_HOR, dir.getOpposite());
            }
        }
        return null;
    }

    private static final Method BLOCKITEM_GETPLACESTATE = ObfuscationReflectionHelper.findMethod(BlockItem.class, "m_5965_", BlockPlaceContext.class);
    private static BlockState getStateForPlacement(BlockPlaceContext ctx, Block block)
    {
        Item item = ctx.getItemInHand().getItem();
        if (item instanceof StandingAndWallBlockItem)
        {
            try
            {
                return (BlockState) BLOCKITEM_GETPLACESTATE.invoke(item, ctx);
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                FramedBlocks.LOGGER.error("Encountered an error while getting placement state of ", e);
            }
        }
        return block.getStateForPlacement(ctx);
    }



    private static Minecraft mc() { return Minecraft.getInstance(); }



    private GhostBlockRenderer() { }
}