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
        GHOST_MODEL_DATA.setCamoState(FBContent.blockFramedGhostBlock.get().getDefaultState());

        //Needed to render ghosts of double blocks
        GHOST_MODEL_DATA.setData(FramedDoubleTileEntity.DATA_LEFT, GHOST_MODEL_DATA);
        GHOST_MODEL_DATA.setData(FramedDoubleTileEntity.DATA_RIGHT, GHOST_MODEL_DATA);
    }

    public static void drawGhostBlock(IRenderTypeBuffer buffers, MatrixStack mstack)
    {
        if (!ClientConfig.showGhostBlocks) { return; }

        RayTraceResult mouseOver = mc().objectMouseOver;
        if (mouseOver == null || mouseOver.getType() != RayTraceResult.Type.BLOCK) { return; }

        BlockRayTraceResult target = (BlockRayTraceResult) mouseOver;

        ItemStack stack = mc().player.getHeldItemMainhand();
        if (!(stack.getItem() instanceof BlockItem)) { return; }

        Block block = ((BlockItem) stack.getItem()).getBlock();
        if (!(block instanceof IFramedBlock)) { return; }

        boolean doRender;
        BlockPos renderPos;
        BlockState renderState;

        if ((renderState = tryBuildDoublePanel(target, block)) != null)
        {
            doRender = true;
            renderPos = target.getPos();
        }
        else if ((renderState = tryBuildDoubleSlab(target, block)) != null)
        {
            doRender = true;
            renderPos = target.getPos();
        }
        else
        {
            BlockItemUseContext context = new BlockItemUseContext(mc().player, Hand.MAIN_HAND, stack, target);

            renderPos = context.getPos();
            renderState = getStateForPlacement(context, block);
            doRender = renderState != null &&
                    mc().world.placedBlockCollides(renderState, renderPos, ISelectionContext.forEntity(mc().player)) &&
                    mc().world.getBlockState(renderPos).isReplaceable(context);
        }

        if (doRender)
        {
            doRenderGhostBlock(mstack, buffers, renderPos, renderState);

            if (renderState.getBlock() == FBContent.blockFramedDoor.get())
            {
                doRenderGhostBlock(mstack, buffers, renderPos.up(), renderState.with(DoorBlock.HALF, DoubleBlockHalf.UPPER));
            }
        }
    }

    private static void doRenderGhostBlock(MatrixStack mstack, IRenderTypeBuffer buffers, BlockPos renderPos, BlockState renderState)
    {
        GHOST_MODEL_DATA.setWorld(mc().world);
        GHOST_MODEL_DATA.setPos(renderPos);

        ForgeHooksClient.setRenderLayer(RenderType.getTranslucent());

        Vector3d offset = Vector3d.copy(renderPos).subtract(mc().gameRenderer.getActiveRenderInfo().getProjectedView());

        mstack.push();
        mstack.translate(offset.x, offset.y, offset.z);

        IVertexBuilder builder = buffers.getBuffer(RenderType.getTranslucent());

        mc().getBlockRendererDispatcher().renderModel(
                renderState,
                renderPos,
                mc().world,
                mstack,
                builder,
                false,
                mc().world.getRandom(),
                GHOST_MODEL_DATA
        );

        ((IRenderTypeBuffer.Impl) buffers).finish(RenderType.getTranslucent());

        mstack.pop();

        ForgeHooksClient.setRenderLayer(null);
    }



    private static BlockState tryBuildDoubleSlab(BlockRayTraceResult trace, Block heldBlock)
    {
        if (heldBlock != FBContent.blockFramedSlab.get()) { return null; }

        BlockState target = mc().world.getBlockState(trace.getPos());
        if (target.getBlock() == heldBlock)
        {
            boolean top = target.get(PropertyHolder.TOP);
            if ((top && trace.getFace() == Direction.DOWN) || (!top && trace.getFace() == Direction.UP))
            {
                return target.with(PropertyHolder.TOP, !top);
            }
        }
        return null;
    }

    private static BlockState tryBuildDoublePanel(BlockRayTraceResult trace, Block heldBlock)
    {
        if (heldBlock != FBContent.blockFramedPanel.get()) { return null; }

        BlockState target = mc().world.getBlockState(trace.getPos());
        if (target.getBlock() == heldBlock)
        {
            Direction dir = target.get(PropertyHolder.FACING_HOR);
            if (dir.getOpposite() == trace.getFace())
            {
                return target.with(PropertyHolder.FACING_HOR, dir.getOpposite());
            }
        }
        return null;
    }

    private static final Method BLOCKITEM_GETPLACESTATE = ObfuscationReflectionHelper.findMethod(BlockItem.class, "func_195945_b", BlockItemUseContext.class);
    private static BlockState getStateForPlacement(BlockItemUseContext ctx, Block block)
    {
        Item item = ctx.getItem().getItem();
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