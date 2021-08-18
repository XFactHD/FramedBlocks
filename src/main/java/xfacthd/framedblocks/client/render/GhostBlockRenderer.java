package xfacthd.framedblocks.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
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
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
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
        GHOST_MODEL_DATA.setCamoState(FBContent.blockFramedCube.get().getDefaultState());

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

        Block block;
        boolean blueprint = false;

        ItemStack stack = mc().player.getHeldItemMainhand();
        if (stack.getItem() instanceof FramedBlueprintItem)
        {
            block = ((FramedBlueprintItem)stack.getItem()).getTargetBlock(stack);
            blueprint = true;
        }
        else if (stack.getItem() instanceof BlockItem)
        {
            block = ((BlockItem)stack.getItem()).getBlock();
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
            renderPos = target.getPos();
        }
        else if (!blueprint && (renderState = tryBuildDoubleSlab(target, block)) != null)
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
            BlockState camoState = Blocks.AIR.getDefaultState();
            if (blueprint)
            {
                CompoundNBT beTag = stack.getOrCreateChildTag("blueprint_data").getCompound("camo_data");
                camoState = NBTUtil.readBlockState(beTag.getCompound("camo_state"));
                GHOST_MODEL_DATA.setCamoState(camoState);
            }

            doRenderGhostBlock(mstack, buffers, renderPos, renderState, camoState);

            if (renderState.getBlock() == FBContent.blockFramedDoor.get())
            {
                if (blueprint)
                {
                    CompoundNBT beTag = stack.getOrCreateChildTag("blueprint_data").getCompound("camo_data_two");
                    camoState = NBTUtil.readBlockState(beTag.getCompound("camo_state"));
                    GHOST_MODEL_DATA.setCamoState(camoState);
                }
                doRenderGhostBlock(mstack, buffers, renderPos.up(), renderState.with(DoorBlock.HALF, DoubleBlockHalf.UPPER), camoState);
            }

            if (blueprint)
            {
                GHOST_MODEL_DATA.setCamoState(Blocks.AIR.getDefaultState());
            }
        }
    }

    private static void doRenderGhostBlock(MatrixStack mstack, IRenderTypeBuffer buffers, BlockPos renderPos, BlockState renderState, BlockState camoState)
    {
        GHOST_MODEL_DATA.setWorld(mc().world);
        GHOST_MODEL_DATA.setPos(renderPos);

        //noinspection deprecation
        if (camoState.isAir())
        {
            ForgeHooksClient.setRenderLayer(RenderType.getCutout());
        }
        else
        {
            for (RenderType type : RenderType.getBlockRenderTypes())
            {
                if (RenderTypeLookup.canRenderInLayer(camoState, type))
                {
                    ForgeHooksClient.setRenderLayer(type);
                    break;
                }
            }
        }

        Vector3d offset = Vector3d.copy(renderPos).subtract(mc().gameRenderer.getActiveRenderInfo().getProjectedView());

        mstack.push();
        mstack.translate(offset.x, offset.y, offset.z);

        IVertexBuilder builder = new GhostVertexBuilder(buffers.getBuffer(RenderType.getTranslucent()), 0xAA);

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