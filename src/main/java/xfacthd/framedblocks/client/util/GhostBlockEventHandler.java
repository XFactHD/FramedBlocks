package xfacthd.framedblocks.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.*;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("ConstantConditions")
@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, value = Dist.CLIENT)
public class GhostBlockEventHandler
{
    @SubscribeEvent(receiveCanceled = true)
    public static void onRenderBlockHighlight(final DrawHighlightEvent.HighlightBlock event)
    {
        if (!ClientConfig.showGhostBlocks) { return; }

        ItemStack stack = mc().player.getHeldItemMainhand();
        if (stack.getItem() instanceof BlockItem)
        {
            Block block = ((BlockItem) stack.getItem()).getBlock();
            if (block instanceof IFramedBlock)
            {
                BlockItemUseContext context = new BlockItemUseContext(
                        mc().player,
                        Hand.MAIN_HAND,
                        stack,
                        event.getTarget()
                );

                boolean doRender;
                BlockPos renderPos;
                BlockState renderState;

                if ((renderState = tryBuildDoublePanel(event.getTarget(), block)) != null)
                {
                    doRender = true;
                    renderPos = event.getTarget().getPos();
                }
                else if ((renderState = tryBuildDoubleSlab(event.getTarget(), block)) != null)
                {
                    doRender = true;
                    renderPos = event.getTarget().getPos();
                }
                else
                {
                    renderPos = context.getPos();
                    renderState = getStateForPlacement(context, block);
                    doRender = renderState != null &&
                            mc().world.placedBlockCollides(renderState, renderPos, ISelectionContext.forEntity(mc().player)) &&
                            mc().world.getBlockState(renderPos).isReplaceable(context);
                }

                if (doRender)
                {
                    RenderType layer = MinecraftForgeClient.getRenderLayer();
                    ForgeHooksClient.setRenderLayer(RenderType.getCutout());

                    Vector3d offset = Vector3d.copy(renderPos).subtract(event.getInfo().getProjectedView());

                    MatrixStack mstack = event.getMatrix();
                    mstack.push();
                    mstack.translate(offset.x, offset.y, offset.z);

                    IVertexBuilder builder = event.getBuffers().getBuffer(RenderType.getCutout());
                    mc().getBlockRendererDispatcher().renderModel(
                            renderState,
                            renderPos,
                            mc().world,
                            mstack,
                            builder,
                            false,
                            mc().world.getRandom(),
                            EmptyModelData.INSTANCE
                    );

                    mstack.pop();

                    ForgeHooksClient.setRenderLayer(layer);
                }
            }
        }
    }

    private static BlockState tryBuildDoubleSlab(BlockRayTraceResult trace, Block heldBlock)
    {
        if (heldBlock != FBContent.blockFramedSlab) { return null; }

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
        if (heldBlock != FBContent.blockFramedPanel) { return null; }

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