package xfacthd.framedblocks.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.*;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.client.util.FramedBlockData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;

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
        GHOST_MODEL_DATA.setCamoState(FBContent.blockFramedGhostBlock.get().defaultBlockState());

        //Needed to render ghosts of double blocks
        GHOST_MODEL_DATA.setData(FramedDoubleTileEntity.DATA_LEFT, GHOST_MODEL_DATA);
        GHOST_MODEL_DATA.setData(FramedDoubleTileEntity.DATA_RIGHT, GHOST_MODEL_DATA);
    }

    public static void drawGhostBlock(IRenderTypeBuffer buffers, MatrixStack mstack)
    {
        if (!ClientConfig.showGhostBlocks) { return; }

        RayTraceResult mouseOver = mc().hitResult;
        if (mouseOver == null || mouseOver.getType() != RayTraceResult.Type.BLOCK) { return; }

        BlockRayTraceResult target = (BlockRayTraceResult) mouseOver;

        ItemStack stack = mc().player.getMainHandItem();
        if (!(stack.getItem() instanceof BlockItem item)) { return; }

        Block block = item.getBlock();
        if (!(block instanceof IFramedBlock)) { return; }

        boolean doRender;
        BlockPos renderPos;
        BlockState renderState;

        if ((renderState = tryBuildDoublePanel(target, block)) != null)
        {
            doRender = true;
            renderPos = target.getBlockPos();
        }
        else if ((renderState = tryBuildDoubleSlab(target, block)) != null)
        {
            doRender = true;
            renderPos = target.getBlockPos();
        }
        else
        {
            BlockItemUseContext context = new BlockItemUseContext(mc().player, Hand.MAIN_HAND, stack, target);

            renderPos = context.getClickedPos();
            renderState = getStateForPlacement(context, block);
            doRender = renderState != null &&
                    mc().level.isUnobstructed(renderState, renderPos, ISelectionContext.of(mc().player)) &&
                    mc().level.getBlockState(renderPos).canBeReplaced(context);
        }

        if (doRender)
        {
            doRenderGhostBlock(mstack, buffers, renderPos, renderState);

            if (renderState.getBlock() == FBContent.blockFramedDoor.get())
            {
                doRenderGhostBlock(mstack, buffers, renderPos.above(), renderState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
            }
        }
    }

    private static void doRenderGhostBlock(MatrixStack mstack, IRenderTypeBuffer buffers, BlockPos renderPos, BlockState renderState)
    {
        GHOST_MODEL_DATA.setWorld(mc().level);
        GHOST_MODEL_DATA.setPos(renderPos);

        ForgeHooksClient.setRenderLayer(RenderType.translucent());

        Vector3d offset = Vector3d.atLowerCornerOf(renderPos).subtract(mc().gameRenderer.getMainCamera().getPosition());

        mstack.pushPose();
        mstack.translate(offset.x, offset.y, offset.z);

        IVertexBuilder builder = buffers.getBuffer(RenderType.translucent());

        mc().getBlockRenderer().renderModel(
                renderState,
                renderPos,
                mc().level,
                mstack,
                builder,
                false,
                mc().level.getRandom(),
                GHOST_MODEL_DATA
        );

        ((IRenderTypeBuffer.Impl) buffers).endBatch(RenderType.translucent());

        mstack.popPose();

        ForgeHooksClient.setRenderLayer(null);
    }



    private static BlockState tryBuildDoubleSlab(BlockRayTraceResult trace, Block heldBlock)
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

    private static BlockState tryBuildDoublePanel(BlockRayTraceResult trace, Block heldBlock)
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

    private static final Method BLOCKITEM_GETPLACESTATE = ObfuscationReflectionHelper.findMethod(BlockItem.class, "func_195945_b", BlockItemUseContext.class);
    private static BlockState getStateForPlacement(BlockItemUseContext ctx, Block block)
    {
        Item item = ctx.getItemInHand().getItem();
        if (item instanceof WallOrFloorItem)
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