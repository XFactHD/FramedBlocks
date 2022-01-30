package xfacthd.framedblocks.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.model.*;
import xfacthd.framedblocks.client.render.FramedChestRenderer;
import xfacthd.framedblocks.client.render.FramedSignRenderer;
import xfacthd.framedblocks.client.screen.FramedStorageScreen;
import xfacthd.framedblocks.client.screen.FramedSignScreen;
import xfacthd.framedblocks.client.util.BlueprintPropertyOverride;
import xfacthd.framedblocks.client.util.ModelUtils;
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
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        FBContent.getRegisteredBlocks()
                .stream()
                .map(RegistryObject::get)
                .filter(block -> block instanceof IFramedBlock)
                .forEach(block -> RenderTypeLookup.setRenderLayer(block, type ->
                        type == RenderType.getSolid() ||
                        type == RenderType.getCutout() ||
                        type == RenderType.getCutoutMipped() ||
                        type == RenderType.getTranslucent()
                ));
        RenderTypeLookup.setRenderLayer(FBContent.blockFramedGhostBlock.get(), RenderType.getTranslucent());

        ClientRegistry.bindTileEntityRenderer(FBContent.tileTypeFramedSign.get(), FramedSignRenderer::new);
        ClientRegistry.bindTileEntityRenderer(FBContent.tileTypeFramedChest.get(), FramedChestRenderer::new);

        event.enqueueWork(() ->
        {
            ScreenManager.registerFactory(FBContent.containerTypeFramedChest.get(), FramedStorageScreen::new);

            BlueprintPropertyOverride.register();
        });
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public static void onBlockColors(final ColorHandlerEvent.Block event)
    {
        Block[] blocks = FBContent.getRegisteredBlocks()
                .stream()
                .map(RegistryObject::get)
                .filter(block -> block instanceof IFramedBlock)
                .toArray(Block[]::new);

        event.getBlockColors().register((state, world, pos, tintIndex) ->
        {
            if (world != null && pos != null)
            {
                TileEntity te = world.getTileEntity(pos);
                if (tintIndex < -1)
                {
                    tintIndex = ModelUtils.decodeSecondaryTintIndex(tintIndex);

                    if (te instanceof FramedDoubleTileEntity)
                    {
                        BlockState camoState = ((FramedDoubleTileEntity) te).getCamoStateTwo();
                        if (!camoState.isAir())
                        {
                            return event.getBlockColors().getColor(camoState, world, pos, tintIndex);
                        }
                    }
                    else if (te instanceof FramedFlowerPotTileEntity)
                    {
                        BlockState plantState = ((FramedFlowerPotTileEntity) te).getFlowerBlock().getDefaultState();
                        if (!plantState.isAir())
                        {
                            return event.getBlockColors().getColor(plantState, world, pos, tintIndex);
                        }
                    }
                }
                else if (te instanceof FramedTileEntity)
                {
                    BlockState camoState = ((FramedTileEntity) te).getCamoState();
                    if (!camoState.isAir())
                    {
                        return event.getBlockColors().getColor(camoState, world, pos, tintIndex);
                    }
                }
            }
            return -1;
        }, blocks);
    }

    @SubscribeEvent
    public static void onModelRegistry(final ModelRegistryEvent event)
    {
        ModelLoader.addSpecialModel(FramedBouncyCubeModel.FRAME_LOCATION);
    }

    @SubscribeEvent
    public static void onModelsLoaded(final ModelBakeEvent event)
    {
        Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();

        FramedChestRenderer.onModelsLoaded(registry); //Must happen before the chest model is replaced

        replaceModels(FBContent.blockFramedCube, registry, FramedCubeModel::new);
        replaceModels(FBContent.blockFramedSlope, registry, FramedSlopeModel::new, FramedSlopeModel::new);
        replaceModels(FBContent.blockFramedCornerSlope, registry, FramedCornerSlopeModel::new, FramedCornerSlopeModel::new);
        replaceModels(FBContent.blockFramedInnerCornerSlope, registry, FramedInnerCornerSlopeModel::new, FramedInnerCornerSlopeModel::new);
        replaceModels(FBContent.blockFramedPrismCorner, registry, FramedPrismCornerModel::new, FramedPrismCornerModel::new);
        replaceModels(FBContent.blockFramedInnerPrismCorner, registry, FramedInnerPrismCornerModel::new, FramedInnerPrismCornerModel::new);
        replaceModels(FBContent.blockFramedThreewayCorner, registry, FramedThreewayCornerModel::new, FramedThreewayCornerModel::new);
        replaceModels(FBContent.blockFramedInnerThreewayCorner, registry, FramedInnerThreewayCornerModel::new, FramedInnerThreewayCornerModel::new);
        replaceModels(FBContent.blockFramedSlab, registry, FramedSlabModel::new);
        replaceModels(FBContent.blockFramedSlabEdge, registry, FramedSlabEdgeModel::new);
        replaceModels(FBContent.blockFramedSlabCorner, registry, FramedSlabCornerModel::new);
        replaceModels(FBContent.blockFramedPanel, registry, FramedPanelModel::new);
        replaceModels(FBContent.blockFramedCornerPillar, registry, FramedCornerPillarModel::new);
        replaceModels(FBContent.blockFramedStairs, registry, FramedStairsModel::new);
        replaceModels(FBContent.blockFramedWall, registry, FramedWallModel::new);
        replaceModels(FBContent.blockFramedFence, registry, FramedFenceModel::createFenceModel);
        replaceModels(FBContent.blockFramedGate, registry, FramedFenceGateModel::new);
        replaceModels(FBContent.blockFramedDoor, registry, FramedDoorModel::new);
        replaceModels(FBContent.blockFramedTrapDoor, registry, FramedTrapDoorModel::new);
        replaceModels(FBContent.blockFramedPressurePlate, registry, FramedPressurePlateModel::new);
        replaceModels(FBContent.blockFramedLadder, registry, FramedLadderModel::new, FramedLadderModel::new);
        replaceModels(FBContent.blockFramedButton, registry, FramedButtonModel::new);
        replaceModels(FBContent.blockFramedLever, registry, FramedLeverModel::new);
        replaceModels(FBContent.blockFramedSign, registry, FramedSignModel::new);
        replaceModels(FBContent.blockFramedWallSign, registry, FramedWallSignModel::new);
        replaceModels(FBContent.blockFramedDoubleSlab, registry, FramedDoubleSlabModel::new);
        replaceModels(FBContent.blockFramedDoublePanel, registry, FramedDoublePanelModel::new);
        replaceModels(FBContent.blockFramedDoubleSlope, registry, FramedDoubleSlopeModel::new);
        replaceModels(FBContent.blockFramedDoubleCorner, registry, FramedDoubleCornerModel::new, FramedDoubleCornerModel::new);
        replaceModels(FBContent.blockFramedDoublePrismCorner, registry, FramedDoublePrismCornerModel::new, FramedDoublePrismCornerModel::new);
        replaceModels(FBContent.blockFramedDoubleThreewayCorner, registry, FramedDoubleThreewayCornerModel::new, FramedDoubleThreewayCornerModel::new);
        replaceModels(FBContent.blockFramedTorch, registry, FramedTorchModel::new);
        replaceModels(FBContent.blockFramedWallTorch, registry, FramedWallTorchModel::new);
        replaceModels(FBContent.blockFramedSoulTorch, registry, FramedSoulTorchModel::new);
        replaceModels(FBContent.blockFramedSoulWallTorch, registry, FramedSoulWallTorchModel::new);
        replaceModels(FBContent.blockFramedFloor, registry, FramedFloorModel::new);
        replaceModels(FBContent.blockFramedLattice, registry, FramedLatticeModel::new);
        replaceModels(FBContent.blockFramedVerticalStairs, registry, FramedVerticalStairsModel::new);
        replaceModels(FBContent.blockFramedChest, registry, FramedChestModel::new, FramedChestModel::new);
        replaceModels(FBContent.blockFramedBars, registry, FramedBarsModel::new);
        replaceModels(FBContent.blockFramedPane, registry, FramedPaneModel::new);
        replaceModels(FBContent.blockFramedRailSlope, registry, FramedRailSlopeModel::new, FramedRailSlopeModel::new);
        replaceModels(FBContent.blockFramedFlowerPot, registry, FramedFlowerPotModel::new);
        replaceModels(FBContent.blockFramedPillar, registry, FramedPillarModel::new);
        replaceModels(FBContent.blockFramedHalfPillar, registry, FramedHalfPillarModel::new);
        replaceModels(FBContent.blockFramedPost, registry, FramedPillarModel::new);
        replaceModels(FBContent.blockFramedCollapsibleBlock, registry, FramedCollapsibleBlockModel::new);
        replaceModels(FBContent.blockFramedHalfStairs, registry, FramedHalfStairsModel::new);
        replaceModels(FBContent.blockFramedBouncyCube, registry, (state, model) -> new FramedBouncyCubeModel(state, model, registry));
        replaceModels(FBContent.blockFramedSecretStorage, registry, FramedCubeModel::new);
    }

    private static void replaceModels(RegistryObject<Block> block, Map<ResourceLocation, IBakedModel> models,
                                      BiFunction<BlockState, IBakedModel, IBakedModel> blockModelGen)
    {
        replaceModels(block, models, blockModelGen, model -> model);
    }

    private static void replaceModels(RegistryObject<Block> block, Map<ResourceLocation, IBakedModel> models,
                                      BiFunction<BlockState, IBakedModel, IBakedModel> blockModelGen,
                                      Function<IBakedModel, IBakedModel> itemModelGen)
    {
        for (BlockState state : block.get().getStateContainer().getValidStates())
        {
            ResourceLocation location = BlockModelShapes.getModelLocation(state);
            IBakedModel baseModel = models.get(location);
            IBakedModel replacement = blockModelGen.apply(state, baseModel);
            models.put(location, replacement);
        }

        //noinspection ConstantConditions
        ResourceLocation location = new ModelResourceLocation(block.get().getRegistryName(), "inventory");
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