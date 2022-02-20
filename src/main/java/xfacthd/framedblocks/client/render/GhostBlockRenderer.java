package xfacthd.framedblocks.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.*;
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
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

@SuppressWarnings("ConstantConditions")
@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GhostBlockRenderer
{
    private static final Random RANDOM = new Random();
    private static final FramedBlockData GHOST_MODEL_DATA = new FramedBlockData(true);
    private static final FramedBlockData GHOST_MODEL_DATA_LEFT = new FramedBlockData(true);
    private static final FramedBlockData GHOST_MODEL_DATA_RIGHT = new FramedBlockData(true);

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        GHOST_MODEL_DATA.setCamoState(Blocks.AIR.getDefaultState());

        //Needed to render ghosts of double blocks
        GHOST_MODEL_DATA.setData(FramedDoubleTileEntity.DATA_LEFT, GHOST_MODEL_DATA_LEFT);
        GHOST_MODEL_DATA.setData(FramedDoubleTileEntity.DATA_RIGHT, GHOST_MODEL_DATA_RIGHT);
    }

    public static void drawGhostBlock(IRenderTypeBuffer buffers, MatrixStack mstack)
    {
        if (!ClientConfig.showGhostBlocks) { return; }

        RayTraceResult mouseOver = mc().objectMouseOver;
        if (mouseOver == null || mouseOver.getType() != RayTraceResult.Type.BLOCK) { return; }

        BlockRayTraceResult target = (BlockRayTraceResult) mouseOver;

        Block block;
        boolean blueprint = false;
        boolean rail = false;

        ItemStack stack = mc().player.getHeldItemMainhand();
        if (stack.getItem() instanceof FramedBlueprintItem)
        {
            block = ((FramedBlueprintItem)stack.getItem()).getTargetBlock(stack);
            blueprint = true;
        }
        else if (stack.getItem() instanceof BlockItem)
        {
            block = ((BlockItem)stack.getItem()).getBlock();
            rail = stack.getItem() == Items.RAIL;
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
            renderPos = target.getPos();
        }
        else if (!blueprint && (renderState = tryBuildDoubleSlab(target, block)) != null)
        {
            doRender = true;
            renderPos = target.getPos();
        }
        else if (rail)
        {
            BlockState state = mc().world.getBlockState(target.getPos());
            if (state.getBlock() == FBContent.blockFramedSlope.get())
            {
                renderPos = target.getPos();

                RailShape shape = FramedRailSlopeBlock.shapeFromDirection(state.get(PropertyHolder.FACING_HOR));
                renderState = block.getDefaultState().with(BlockStateProperties.RAIL_SHAPE, shape);

                BlockState railSlope = FBContent.blockFramedRailSlope.get()
                        .getDefaultState()
                        .with(PropertyHolder.ASCENDING_RAIL_SHAPE, shape);
                doRender = railSlope.isValidPosition(mc().world, renderPos);
            }
            else
            {
                doRender = false;
                renderPos = BlockPos.ZERO;
                renderState = Blocks.AIR.getDefaultState();
            }
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
            BlockState camoStateTwo = Blocks.AIR.getDefaultState();
            if (blueprint)
            {
                CompoundNBT beTag = stack.getOrCreateChildTag("blueprint_data").getCompound("camo_data");
                camoState = NBTUtil.readBlockState(beTag.getCompound("camo_state"));

                if (renderState.getBlock() instanceof AbstractFramedDoubleBlock)
                {
                    camoStateTwo = NBTUtil.readBlockState(beTag.getCompound("camo_state_two"));

                    if (block == FBContent.blockFramedDoublePanel.get() && renderState.get(PropertyHolder.FACING_NE) != mc().player.getHorizontalFacing())
                    {
                        BlockState temp = camoState;
                        camoState = camoStateTwo;
                        camoStateTwo = temp;
                    }

                    GHOST_MODEL_DATA_LEFT.setCamoState(camoState);
                    GHOST_MODEL_DATA_RIGHT.setCamoState(camoStateTwo);
                }
                else
                {
                    GHOST_MODEL_DATA.setCamoState(camoState);
                }
            }

            doRenderGhostBlock(mstack, buffers, renderPos, renderState, camoState, camoStateTwo);

            if (renderState.getBlock() == FBContent.blockFramedDoor.get())
            {
                if (blueprint)
                {
                    CompoundNBT beTag = stack.getOrCreateChildTag("blueprint_data").getCompound("camo_data_two");
                    camoState = NBTUtil.readBlockState(beTag.getCompound("camo_state"));
                    GHOST_MODEL_DATA.setCamoState(camoState);
                }

                doRenderGhostBlock(mstack, buffers, renderPos.up(), renderState.with(DoorBlock.HALF, DoubleBlockHalf.UPPER), camoState, camoStateTwo);
            }

            if (blueprint)
            {
                GHOST_MODEL_DATA.setCamoState(Blocks.AIR.getDefaultState());
                GHOST_MODEL_DATA_LEFT.setCamoState(Blocks.AIR.getDefaultState());
                GHOST_MODEL_DATA_RIGHT.setCamoState(Blocks.AIR.getDefaultState());
            }
        }
    }

    private static void doRenderGhostBlock(MatrixStack mstack, IRenderTypeBuffer buffers, BlockPos renderPos, BlockState renderState, BlockState camoState, BlockState camoStateTwo)
    {
        GHOST_MODEL_DATA.setWorld(mc().world);
        GHOST_MODEL_DATA.setPos(renderPos);
        GHOST_MODEL_DATA_LEFT.setWorld(mc().world);
        GHOST_MODEL_DATA_LEFT.setPos(renderPos);
        GHOST_MODEL_DATA_RIGHT.setWorld(mc().world);
        GHOST_MODEL_DATA_RIGHT.setPos(renderPos);

        Vector3d offset = Vector3d.copy(renderPos).subtract(mc().gameRenderer.getActiveRenderInfo().getProjectedView());
        IVertexBuilder builder = new GhostVertexBuilder(buffers.getBuffer(RenderType.getTranslucent()), 0xAA);

        //noinspection deprecation
        if (camoState.isAir() && camoStateTwo.isAir())
        {
            doRenderGhostBlockInLayer(mstack, builder, renderPos, renderState, RenderType.getCutout(), offset);
        }
        else
        {
            for (RenderType type : RenderType.getBlockRenderTypes())
            {
                if (canRenderInLayer(camoState, type) || canRenderInLayer(camoStateTwo, type))
                {
                    doRenderGhostBlockInLayer(mstack, builder, renderPos, renderState, type, offset);
                }
            }
        }

        ((IRenderTypeBuffer.Impl) buffers).finish(RenderType.getTranslucent());
        ForgeHooksClient.setRenderLayer(null);
    }

    private static boolean canRenderInLayer(BlockState camoState, RenderType layer)
    {
        //noinspection deprecation
        if (camoState.isAir()) { return layer == RenderType.getCutout(); }
        return RenderTypeLookup.canRenderInLayer(camoState, layer);
    }

    private static void doRenderGhostBlockInLayer(MatrixStack mstack, IVertexBuilder builder, BlockPos renderPos, BlockState renderState, RenderType layer, Vector3d offset)
    {
        ForgeHooksClient.setRenderLayer(layer);

        mstack.push();
        mstack.translate(offset.x, offset.y, offset.z);

        mc().getBlockRendererDispatcher().renderModel(
                renderState,
                renderPos,
                mc().world,
                mstack,
                builder,
                false,
                RANDOM,
                GHOST_MODEL_DATA
        );

        mstack.pop();
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