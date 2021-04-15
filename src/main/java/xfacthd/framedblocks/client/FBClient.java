package xfacthd.framedblocks.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.model.v2.*;
import xfacthd.framedblocks.client.render.FramedSignRenderer;
import xfacthd.framedblocks.client.screen.FramedSignScreen;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.tileentity.*;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FBClient
{
    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        ForgeRegistries.BLOCKS.getValues()
                .stream()
                .filter(block -> block.getRegistryName().getNamespace().equals(FramedBlocks.MODID))
                .filter(block -> block instanceof IFramedBlock)
                .forEach(block -> RenderTypeLookup.setRenderLayer(block, type ->
                        type == RenderType.getSolid() ||
                        type == RenderType.getCutout() ||
                        type == RenderType.getCutoutMipped() ||
                        type == RenderType.getTranslucent()
                ));
        RenderTypeLookup.setRenderLayer(FBContent.blockFramedGhostBlock, RenderType.getTranslucent());

        ClientRegistry.bindTileEntityRenderer(FBContent.tileTypeFramedSign, FramedSignRenderer::new);
    }

    @SubscribeEvent
    @SuppressWarnings({"ConstantConditions", "deprecation"})
    public static void onBlockColors(final ColorHandlerEvent.Block event)
    {
        Block[] blocks = ForgeRegistries.BLOCKS.getValues()
                .stream()
                .filter(block -> block.getRegistryName().getNamespace().equals(FramedBlocks.MODID))
                .filter(block -> block instanceof IFramedBlock)
                .toArray(Block[]::new);

        event.getBlockColors().register((state, world, pos, tintIndex) ->
        {
            if (world != null && pos != null)
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof FramedTileEntity)
                {
                    BlockState camoState = ((FramedTileEntity) te).getCamoState();
                    if (!camoState.isAir())
                    {
                        int color = event.getBlockColors().getColor(camoState, world, pos, tintIndex);
                        if (color != -1) { return color; }
                    }

                    if (te instanceof FramedDoubleTileEntity)
                    {
                        camoState = ((FramedDoubleTileEntity) te).getCamoStateTwo();
                        if (!camoState.isAir())
                        {
                            return event.getBlockColors().getColor(camoState, world, pos, tintIndex);
                        }
                    }
                }
            }
            return -1;
        }, blocks);
    }

    @SubscribeEvent
    public static void onModelsLoaded(final ModelBakeEvent event)
    {
        Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();

        replaceModels(FBContent.blockFramedCube, registry, FramedCubeModelV2::new);
        replaceModels(FBContent.blockFramedSlope, registry, FramedSlopeModelV2::new, FramedSlopeModelV2::new);
        replaceModels(FBContent.blockFramedCornerSlope, registry, FramedCornerSlopeModelV2::new, FramedCornerSlopeModelV2::new);
        replaceModels(FBContent.blockFramedInnerCornerSlope, registry, FramedInnerCornerSlopeModelV2::new, FramedInnerCornerSlopeModelV2::new);
        replaceModels(FBContent.blockFramedPrismCorner, registry, FramedPrismCornerModelV2::new, FramedPrismCornerModelV2::new);
        replaceModels(FBContent.blockFramedInnerPrismCorner, registry, FramedInnerPrismCornerModelV2::new, FramedInnerPrismCornerModelV2::new);
        replaceModels(FBContent.blockFramedThreewayCorner, registry, FramedThreewayCornerModelV2::new, FramedThreewayCornerModelV2::new);
        replaceModels(FBContent.blockFramedInnerThreewayCorner, registry, FramedInnerThreewayCornerModelV2::new, FramedInnerThreewayCornerModelV2::new);
        replaceModels(FBContent.blockFramedSlab, registry, FramedSlabModelV2::new);
        replaceModels(FBContent.blockFramedSlabEdge, registry, FramedSlabEdgeModel::new);
        replaceModels(FBContent.blockFramedPanel, registry, FramedPanelModelV2::new);
        replaceModels(FBContent.blockFramedCornerPillar, registry, FramedCornerPillarModelV2::new);
        replaceModels(FBContent.blockFramedStairs, registry, FramedStairsModelV2::new);
        replaceModels(FBContent.blockFramedWall, registry, FramedWallModelV2::new);
        replaceModels(FBContent.blockFramedFence, registry, FramedFenceModelV2::new);
        replaceModels(FBContent.blockFramedGate, registry, FramedFenceGateModelV2::new);
        replaceModels(FBContent.blockFramedDoor, registry, FramedDoorModelV2::new);
        replaceModels(FBContent.blockFramedTrapDoor, registry, FramedTrapDoorModelV2::new);
        replaceModels(FBContent.blockFramedPressurePlate, registry, FramedPressurePlateModelV2::new);
        replaceModels(FBContent.blockFramedLadder, registry, FramedLadderModelV2::new);
        replaceModels(FBContent.blockFramedButton, registry, FramedButtonModelV2::new);
        replaceModels(FBContent.blockFramedLever, registry, FramedLeverModelV2::new);
        replaceModels(FBContent.blockFramedSign, registry, FramedSignModelV2::new);
        replaceModels(FBContent.blockFramedWallSign, registry, FramedWallSignModelV2::new);
        replaceModels(FBContent.blockFramedDoubleSlab, registry, FramedDoubleSlabModel::new);
        replaceModels(FBContent.blockFramedDoublePanel, registry, FramedDoublePanelModel::new);
        replaceModels(FBContent.blockFramedDoubleSlope, registry, FramedDoubleSlopeModel::new);

        //Framed Collapsible Block
        //replaceModelsSimple(FBContent.blockFramedCollapsibleBlock, registry, FramedCollapsibleBlockModel::new);
    }

    private static void replaceModels(Block block, Map<ResourceLocation, IBakedModel> models,
                                      BiFunction<BlockState, IBakedModel, IBakedModel> blockModelGen)
    {
        replaceModels(block, models, blockModelGen, model -> model);
    }

    private static void replaceModels(Block block, Map<ResourceLocation, IBakedModel> models,
                                      BiFunction<BlockState, IBakedModel, IBakedModel> blockModelGen,
                                      Function<IBakedModel, IBakedModel> itemModelGen)
    {
        for (BlockState state : block.getStateContainer().getValidStates())
        {
            ResourceLocation location = BlockModelShapes.getModelLocation(state);
            IBakedModel baseModel = models.get(location);
            IBakedModel replacement = blockModelGen.apply(state, baseModel);
            models.put(location, replacement);
        }

        //noinspection ConstantConditions
        ResourceLocation location = new ModelResourceLocation(block.getRegistryName(), "inventory");
        IBakedModel replacement = itemModelGen.apply(models.get(location));
        models.put(location, replacement);
    }



    public static void openSignScreen(BlockPos pos)
    {
        //noinspection ConstantConditions
        TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
        if (te instanceof FramedSignTileEntity)
        {
            Minecraft.getInstance().displayGuiScreen(new FramedSignScreen((FramedSignTileEntity)te));
        }
    }
}