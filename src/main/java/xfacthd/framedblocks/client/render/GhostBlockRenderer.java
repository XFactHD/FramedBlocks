package xfacthd.framedblocks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("ConstantConditions")
@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GhostBlockRenderer
{
    private static final FramedBlockData GHOST_MODEL_DATA = new FramedBlockData();

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        GHOST_MODEL_DATA.setCamoState(FBContent.blockFramedCube.get().defaultBlockState());

        //Needed to render ghosts of double blocks
        GHOST_MODEL_DATA.setData(FramedDoubleBlockEntity.DATA_LEFT, GHOST_MODEL_DATA);
        GHOST_MODEL_DATA.setData(FramedDoubleBlockEntity.DATA_RIGHT, GHOST_MODEL_DATA);
    }

    public static void drawGhostBlock(MultiBufferSource buffers, PoseStack mstack)
    {
        if (!ClientConfig.showGhostBlocks) { return; }

        HitResult mouseOver = mc().hitResult;
        if (mouseOver == null || mouseOver.getType() != HitResult.Type.BLOCK) { return; }

        BlockHitResult target = (BlockHitResult) mouseOver;

        Block block;
        boolean blueprint = false;

        ItemStack stack = mc().player.getMainHandItem();
        if (stack.getItem() instanceof FramedBlueprintItem item)
        {
            block = item.getTargetBlock(stack);
            blueprint = true;
        }
        else if (stack.getItem() instanceof BlockItem item)
        {
            block = item.getBlock();
        }
        else
        {
            return;
        }

        if (!(block instanceof IFramedBlock)) { return; }

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
            BlockState camoState = Blocks.AIR.defaultBlockState();
            if (blueprint)
            {
                CompoundTag beTag = stack.getOrCreateTagElement("blueprint_data").getCompound("camo_data");
                camoState = NbtUtils.readBlockState(beTag.getCompound("camo_state"));
                GHOST_MODEL_DATA.setCamoState(camoState);
            }

            doRenderGhostBlock(mstack, buffers, renderPos, renderState, camoState);

            if (renderState.getBlock() == FBContent.blockFramedDoor.get())
            {
                if (blueprint)
                {
                    CompoundTag beTag = stack.getOrCreateTagElement("blueprint_data").getCompound("camo_data_two");
                    camoState = NbtUtils.readBlockState(beTag.getCompound("camo_state"));
                    GHOST_MODEL_DATA.setCamoState(camoState);
                }

                doRenderGhostBlock(mstack, buffers, renderPos.above(), renderState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), camoState);
            }
        }
    }

    private static void doRenderGhostBlock(PoseStack mstack, MultiBufferSource buffers, BlockPos renderPos, BlockState renderState, BlockState camoState)
    {
        GHOST_MODEL_DATA.setLevel(mc().level);
        GHOST_MODEL_DATA.setPos(renderPos);

        if (camoState.isAir())
        {
            ForgeHooksClient.setRenderLayer(RenderType.cutout());
        }
        else
        {
            for (RenderType type : RenderType.chunkBufferLayers())
            {
                if (ItemBlockRenderTypes.canRenderInLayer(camoState, type))
                {
                    ForgeHooksClient.setRenderLayer(type);
                    break;
                }
            }
        }

        Vec3 offset = Vec3.atLowerCornerOf(renderPos).subtract(mc().gameRenderer.getMainCamera().getPosition());

        mstack.pushPose();
        mstack.translate(offset.x, offset.y, offset.z);

        VertexConsumer builder = new GhostVertexConsumer(buffers.getBuffer(RenderType.translucent()), 0xAA);

        mc().getBlockRenderer().renderBatched(
                renderState,
                renderPos,
                mc().level,
                mstack,
                builder,
                false,
                mc().level.getRandom(),
                GHOST_MODEL_DATA
        );

        ((MultiBufferSource.BufferSource) buffers).endBatch(RenderType.translucent());

        mstack.popPose();

        ForgeHooksClient.setRenderLayer(null);
    }



    private static BlockState tryBuildDoubleSlab(BlockHitResult trace, Block heldBlock)
    {
        if (heldBlock != FBContent.blockFramedSlab.get()) { return null; }

        BlockState target = mc().level.getBlockState(trace.getBlockPos());
        if (target.getBlock() == heldBlock)
        {
            boolean top = target.getValue(PropertyHolder.TOP);
            if ((top && trace.getDirection() == Direction.DOWN) || (!top && trace.getDirection() == Direction.UP))
            {
                return target.setValue(PropertyHolder.TOP, !top);
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
            Direction dir = target.getValue(PropertyHolder.FACING_HOR);
            if (dir.getOpposite() == trace.getDirection())
            {
                return target.setValue(PropertyHolder.FACING_HOR, dir.getOpposite());
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
}