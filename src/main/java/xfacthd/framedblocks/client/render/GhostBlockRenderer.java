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
        GHOST_MODEL_DATA.setCamoState(Blocks.AIR.defaultBlockState());

        //Needed to render ghosts of double blocks
        GHOST_MODEL_DATA.setData(FramedDoubleTileEntity.DATA_LEFT, GHOST_MODEL_DATA_LEFT);
        GHOST_MODEL_DATA.setData(FramedDoubleTileEntity.DATA_RIGHT, GHOST_MODEL_DATA_RIGHT);
    }

    public static void drawGhostBlock(IRenderTypeBuffer buffers, MatrixStack mstack)
    {
        if (!ClientConfig.showGhostBlocks) { return; }

        RayTraceResult mouseOver = mc().hitResult;
        if (mouseOver == null || mouseOver.getType() != RayTraceResult.Type.BLOCK) { return; }

        BlockRayTraceResult target = (BlockRayTraceResult) mouseOver;

        Block block;
        boolean blueprint = false;
        boolean rail = false;

        ItemStack stack = mc().player.getMainHandItem();
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

                RailShape shape = FramedRailSlopeBlock.shapeFromDirection(state.getValue(PropertyHolder.FACING_HOR));
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
            BlockItemUseContext context = new BlockItemUseContext(mc().player, Hand.MAIN_HAND, stack, target);

            renderPos = context.getClickedPos();
            renderState = getStateForPlacement(context, block);
            doRender = renderState != null &&
                    mc().level.isUnobstructed(renderState, renderPos, ISelectionContext.of(mc().player)) &&
                    mc().level.getBlockState(renderPos).canBeReplaced(context);
        }

        if (doRender)
        {
            BlockState camoState = Blocks.AIR.defaultBlockState();
            BlockState camoStateTwo = Blocks.AIR.defaultBlockState();
            if (blueprint)
            {
                CompoundNBT beTag = stack.getOrCreateTagElement("blueprint_data").getCompound("camo_data");
                camoState = NBTUtil.readBlockState(beTag.getCompound("camo_state"));

                if (renderState.getBlock() instanceof AbstractFramedDoubleBlock)
                {
                    camoStateTwo = NBTUtil.readBlockState(beTag.getCompound("camo_state_two"));

                    if (block == FBContent.blockFramedDoublePanel.get() && renderState.getValue(PropertyHolder.FACING_NE) != mc().player.getDirection())
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
                    CompoundNBT beTag = stack.getOrCreateTagElement("blueprint_data").getCompound("camo_data_two");
                    camoState = NBTUtil.readBlockState(beTag.getCompound("camo_state"));
                    GHOST_MODEL_DATA.setCamoState(camoState);
                }

                doRenderGhostBlock(mstack, buffers, renderPos.above(), renderState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), camoState, camoStateTwo);
            }

            if (blueprint)
            {
                GHOST_MODEL_DATA.setCamoState(Blocks.AIR.defaultBlockState());
                GHOST_MODEL_DATA_LEFT.setCamoState(Blocks.AIR.defaultBlockState());
                GHOST_MODEL_DATA_RIGHT.setCamoState(Blocks.AIR.defaultBlockState());
            }
        }
    }

    private static void doRenderGhostBlock(MatrixStack mstack, IRenderTypeBuffer buffers, BlockPos renderPos, BlockState renderState, BlockState camoState, BlockState camoStateTwo)
    {
        GHOST_MODEL_DATA.setWorld(mc().level);
        GHOST_MODEL_DATA.setPos(renderPos);
        GHOST_MODEL_DATA_LEFT.setWorld(mc().level);
        GHOST_MODEL_DATA_LEFT.setPos(renderPos);
        GHOST_MODEL_DATA_RIGHT.setWorld(mc().level);
        GHOST_MODEL_DATA_RIGHT.setPos(renderPos);

        Vector3d offset = Vector3d.atLowerCornerOf(renderPos).subtract(mc().gameRenderer.getMainCamera().getPosition());
        IVertexBuilder builder = new GhostVertexBuilder(buffers.getBuffer(CustomRenderType.GHOST_BLOCK), 0xAA);

        //noinspection deprecation
        if (camoState.isAir() && camoStateTwo.isAir())
        {
            doRenderGhostBlockInLayer(mstack, builder, renderPos, renderState, RenderType.cutout(), offset);
        }
        else
        {
            for (RenderType type : RenderType.chunkBufferLayers())
            {
                if (canRenderInLayer(camoState, type) || canRenderInLayer(camoStateTwo, type))
                {
                    doRenderGhostBlockInLayer(mstack, builder, renderPos, renderState, type, offset);
                }
            }
        }

        ((IRenderTypeBuffer.Impl) buffers).endBatch(CustomRenderType.GHOST_BLOCK);
        ForgeHooksClient.setRenderLayer(null);
    }

    private static boolean canRenderInLayer(BlockState camoState, RenderType layer)
    {
        //noinspection deprecation
        if (camoState.isAir()) { return layer == RenderType.cutout(); }
        return RenderTypeLookup.canRenderInLayer(camoState, layer);
    }

    private static void doRenderGhostBlockInLayer(MatrixStack mstack, IVertexBuilder builder, BlockPos renderPos, BlockState renderState, RenderType layer, Vector3d offset)
    {
        ForgeHooksClient.setRenderLayer(layer);

        mstack.pushPose();
        mstack.translate(offset.x, offset.y, offset.z);

        mc().getBlockRenderer().renderModel(
                renderState,
                renderPos,
                mc().level,
                mstack,
                builder,
                false,
                RANDOM,
                GHOST_MODEL_DATA
        );

        mstack.popPose();
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