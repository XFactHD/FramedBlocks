package xfacthd.framedblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.client.ClientUtils;
import xfacthd.framedblocks.client.model.*;
import xfacthd.framedblocks.client.render.*;
import xfacthd.framedblocks.client.render.outline.*;
import xfacthd.framedblocks.client.screen.FramedStorageScreen;
import xfacthd.framedblocks.client.screen.FramedSignScreen;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedSignBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.Map;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FBClient
{
    static { FramedBlocksClientAPI.INSTANCE.accept(new ClientApiImpl()); }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        FBContent.getRegisteredBlocks()
                .stream()
                .map(RegistryObject::get)
                .filter(block -> block instanceof IFramedBlock)
                .forEach(block -> ItemBlockRenderTypes.setRenderLayer(block, type ->
                        type == RenderType.solid() ||
                        type == RenderType.cutout() ||
                        type == RenderType.cutoutMipped() ||
                        type == RenderType.translucent()
                ));

        event.enqueueWork(() ->
        {
            MenuScreens.register(FBContent.menuTypeFramedStorage.get(), FramedStorageScreen::new);

            BlueprintPropertyOverride.register();
        });

        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPE, BlockOutlineRenderer::drawSlopeBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_CORNER_SLOPE, BlockOutlineRenderer::drawCornerSlopeBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INNER_CORNER_SLOPE, BlockOutlineRenderer::drawInnerCornerSlopeBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_PRISM_CORNER, BlockOutlineRenderer::drawPrismCornerBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INNER_PRISM_CORNER, BlockOutlineRenderer::drawInnerPrismCornerBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_THREEWAY_CORNER, BlockOutlineRenderer::drawThreewayCornerBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INNER_THREEWAY_CORNER, BlockOutlineRenderer::drawInnerThreewayCornerBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_RAIL_SLOPE, new RailSlopeOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_COLLAPSIBLE_BLOCK, new CollapsibleBlockOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_PRISM, new SlopeCapOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPED_PRISM, new SlopeEndCapOutlineRenderer());
    }

    @SubscribeEvent
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(FBContent.blockEntityTypeFramedSign.get(), FramedSignRenderer::new);
        event.registerBlockEntityRenderer(FBContent.blockEntityTypeFramedChest.get(), FramedChestRenderer::new);
    }

    @SubscribeEvent
    public static void onBlockColors(final ColorHandlerEvent.Block event)
    {
        //noinspection SuspiciousToArrayCall
        Block[] blocks = FBContent.getRegisteredBlocks()
                .stream()
                .map(RegistryObject::get)
                .filter(block -> block instanceof IFramedBlock)
                .toArray(Block[]::new);

        event.getBlockColors().register(FramedBlockColor.INSTANCE, blocks);
    }

    @SubscribeEvent
    public static void onModelRegister(final ModelRegistryEvent event)
    {
        ForgeModelBakery.addSpecialModel(FramedBouncyCubeModel.FRAME_LOCATION);
    }

    @SubscribeEvent
    public static void onModelsLoaded(final ModelBakeEvent event)
    {
        Map<ResourceLocation, BakedModel> registry = event.getModelRegistry();

        FramedChestRenderer.onModelsLoaded(registry); //Must happen before the chest model is replaced

        ClientUtils.replaceModels(FBContent.blockFramedCube, registry, FramedCubeModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedSlope, registry, FramedSlopeModel::new, FramedSlopeModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedCornerSlope, registry, FramedCornerSlopeModel::new, FramedCornerSlopeModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedInnerCornerSlope, registry, FramedInnerCornerSlopeModel::new, FramedInnerCornerSlopeModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedPrismCorner, registry, FramedPrismCornerModel::new, FramedPrismCornerModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedInnerPrismCorner, registry, FramedInnerPrismCornerModel::new, FramedInnerPrismCornerModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedThreewayCorner, registry, FramedThreewayCornerModel::new, FramedThreewayCornerModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedInnerThreewayCorner, registry, FramedInnerThreewayCornerModel::new, FramedInnerThreewayCornerModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedSlab, registry, FramedSlabModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedSlabEdge, registry, FramedSlabEdgeModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedSlabCorner, registry, FramedSlabCornerModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedPanel, registry, FramedPanelModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedCornerPillar, registry, FramedCornerPillarModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedStairs, registry, FramedStairsModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedWall, registry, FramedWallModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedFence, registry, FramedFenceModel::createFenceModel);
        ClientUtils.replaceModels(FBContent.blockFramedGate, registry, FramedFenceGateModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedDoor, registry, FramedDoorModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedTrapDoor, registry, FramedTrapDoorModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedPressurePlate, registry, FramedPressurePlateModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedLadder, registry, FramedLadderModel::new, FramedLadderModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedButton, registry, FramedButtonModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedLever, registry, FramedLeverModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedSign, registry, FramedSignModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedWallSign, registry, FramedWallSignModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedDoubleSlab, registry, FramedDoubleSlabModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedDoublePanel, registry, FramedDoublePanelModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedDoubleSlope, registry, FramedDoubleSlopeModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedDoubleCorner, registry, FramedDoubleCornerModel::new, FramedDoubleCornerModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedDoublePrismCorner, registry, FramedDoublePrismCornerModel::new, FramedDoublePrismCornerModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedDoubleThreewayCorner, registry, FramedDoubleThreewayCornerModel::new, FramedDoubleThreewayCornerModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedTorch, registry, FramedTorchModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedWallTorch, registry, FramedWallTorchModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedSoulTorch, registry, FramedSoulTorchModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedSoulWallTorch, registry, FramedSoulWallTorchModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedFloor, registry, FramedFloorModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedLattice, registry, FramedLatticeModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedVerticalStairs, registry, FramedVerticalStairsModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedChest, registry, FramedChestModel::new, FramedChestModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedBars, registry, FramedBarsModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedPane, registry, FramedPaneModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedRailSlope, registry, FramedRailSlopeModel::new, FramedRailSlopeModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedFlowerPot, registry, FramedFlowerPotModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedPillar, registry, FramedPillarModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedHalfPillar, registry, FramedHalfPillarModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedPost, registry, FramedPillarModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedCollapsibleBlock, registry, FramedCollapsibleBlockModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedHalfStairs, registry, FramedHalfStairsModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedBouncyCube, registry, (state, baseModel) -> new FramedBouncyCubeModel(state, baseModel, registry));
        ClientUtils.replaceModels(FBContent.blockFramedSecretStorage, registry, FramedCubeModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedPrism, registry, FramedPrismModel::new, FramedPrismModel::new);
        ClientUtils.replaceModels(FBContent.blockFramedSlopedPrism, registry, FramedSlopedPrismModel::new, FramedSlopedPrismModel::new);
    }



    public static void openSignScreen(BlockPos pos)
    {
        //noinspection ConstantConditions
        if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof FramedSignBlockEntity be)
        {
            Minecraft.getInstance().setScreen(new FramedSignScreen(be));
        }
    }
}