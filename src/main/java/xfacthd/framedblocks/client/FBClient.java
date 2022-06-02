package xfacthd.framedblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.FramedProperties;
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

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FBClient
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

        KeyMappings.register();

        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPE, BlockOutlineRenderer::drawSlopeBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_CORNER_SLOPE, BlockOutlineRenderer::drawCornerSlopeBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INNER_CORNER_SLOPE, BlockOutlineRenderer::drawInnerCornerSlopeBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_PRISM_CORNER, BlockOutlineRenderer::drawPrismCornerBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INNER_PRISM_CORNER, BlockOutlineRenderer::drawInnerPrismCornerBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_THREEWAY_CORNER, BlockOutlineRenderer::drawThreewayCornerBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INNER_THREEWAY_CORNER, BlockOutlineRenderer::drawInnerThreewayCornerBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_RAIL_SLOPE, new RailSlopeOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_COLLAPSIBLE_BLOCK, new CollapsibleBlockOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_PRISM, new PrismOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPED_PRISM, new SlopedPrismOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPE_SLAB, BlockOutlineRenderer::drawSlopeSlabBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_ELEVATED_SLOPE_SLAB, BlockOutlineRenderer::drawElevatedSlopeSlabBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB, BlockOutlineRenderer::drawInverseDoubleSlopeSlabBox);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPE_PANEL, new SlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_EXTENDED_SLOPE_PANEL, new ExtendedSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL, new InverseDoubleSlopePanelOutlineRenderer());
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
        ForgeModelBakery.addSpecialModel(FramedMarkedCubeModel.SLIME_FRAME_LOCATION);
        ForgeModelBakery.addSpecialModel(FramedMarkedCubeModel.REDSTONE_FRAME_LOCATION);
    }

    @SubscribeEvent
    public static void onModelsLoaded(final ModelBakeEvent event)
    {
        Map<ResourceLocation, BakedModel> registry = event.getModelRegistry();

        FramedChestRenderer.onModelsLoaded(registry); //Must happen before the chest model is replaced

        List<Property<?>> ignoreWaterlogged = List.of(BlockStateProperties.WATERLOGGED);
        List<Property<?>> ignoreSolid = List.of(FramedProperties.SOLID);
        List<Property<?>> ignoreDefault = List.of(BlockStateProperties.WATERLOGGED, FramedProperties.SOLID);

        ClientUtils.replaceModels(FBContent.blockFramedCube, registry, FramedCubeModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedSlope, registry, FramedSlopeModel::new, FramedSlopeModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedCornerSlope, registry, FramedCornerSlopeModel::new, FramedCornerSlopeModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedInnerCornerSlope, registry, FramedInnerCornerSlopeModel::new, FramedInnerCornerSlopeModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedPrismCorner, registry, FramedPrismCornerModel::new, FramedPrismCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedInnerPrismCorner, registry, FramedInnerPrismCornerModel::new, FramedInnerPrismCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedThreewayCorner, registry, FramedThreewayCornerModel::new, FramedThreewayCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedInnerThreewayCorner, registry, FramedInnerThreewayCornerModel::new, FramedInnerThreewayCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedSlab, registry, FramedSlabModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedSlabEdge, registry, FramedSlabEdgeModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedSlabCorner, registry, FramedSlabCornerModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedPanel, registry, FramedPanelModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedCornerPillar, registry, FramedCornerPillarModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedStairs, registry, FramedStairsModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedWall, registry, FramedWallModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedFence, registry, FramedFenceModel::createFenceModel, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedGate, registry, FramedFenceGateModel::new, List.of(BlockStateProperties.POWERED));
        ClientUtils.replaceModels(FBContent.blockFramedDoor, registry, FramedDoorModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedTrapDoor, registry, FramedTrapDoorModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedPressurePlate, registry, FramedPressurePlateModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedLadder, registry, FramedLadderModel::new, FramedLadderModel.itemSource(), ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedButton, registry, FramedButtonModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedLever, registry, FramedLeverModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedSign, registry, FramedSignModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedWallSign, registry, FramedWallSignModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedDoubleSlab, registry, FramedDoubleSlabModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedDoublePanel, registry, FramedDoublePanelModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedDoubleSlope, registry, FramedDoubleSlopeModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedDoubleCorner, registry, FramedDoubleCornerModel::new, FramedDoubleCornerModel.itemSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedDoublePrismCorner, registry, FramedDoublePrismCornerModel::new, FramedDoublePrismCornerModel.itemSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedDoubleThreewayCorner, registry, FramedDoubleThreewayCornerModel::new, FramedDoubleThreewayCornerModel.itemSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedTorch, registry, FramedTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedWallTorch, registry, FramedWallTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedSoulTorch, registry, FramedSoulTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedSoulWallTorch, registry, FramedSoulWallTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedFloor, registry, FramedFloorModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedLattice, registry, FramedLatticeModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedVerticalStairs, registry, FramedVerticalStairsModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedChest, registry, FramedChestModel::new, FramedChestModel.itemSource(), ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedBars, registry, FramedBarsModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedPane, registry, FramedPaneModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedRailSlope, registry, FramedRailSlopeModel::new, FramedRailSlopeModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlowerPot, registry, FramedFlowerPotModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedPillar, registry, FramedPillarModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedHalfPillar, registry, FramedHalfPillarModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedPost, registry, FramedPillarModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedCollapsibleBlock, registry, FramedCollapsibleBlockModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedHalfStairs, registry, FramedHalfStairsModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedBouncyCube, registry, (state, baseModel) -> FramedMarkedCubeModel.slime(state, baseModel, registry), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedSecretStorage, registry, FramedCubeModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedRedstoneBlock, registry, (state, baseModel) -> FramedMarkedCubeModel.redstone(state, baseModel, registry), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedPrism, registry, FramedPrismModel::new, FramedPrismModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedSlopedPrism, registry, FramedSlopedPrismModel::new, FramedSlopedPrismModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedSlopeSlab, registry, FramedSlopeSlabModel::new, FramedSlopeSlabModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedElevatedSlopeSlab, registry, FramedElevatedSlopeSlabModel::new, FramedElevatedSlopeSlabModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedDoubleSlopeSlab, registry, FramedDoubleSlopeSlabModel::new, FramedDoubleSlopeSlabModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedInverseDoubleSlopeSlab, registry, FramedInverseDoubleSlopeSlabModel::new, FramedInverseDoubleSlopeSlabModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedVerticalHalfStairs, registry, FramedVerticalHalfStairsModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedSlopePanel, registry, FramedSlopePanelModel::new, FramedSlopePanelModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedExtendedSlopePanel, registry, FramedExtendedSlopePanelModel::new, FramedExtendedSlopePanelModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedDoubleSlopePanel, registry, FramedDoubleSlopePanelModel::new, FramedDoubleSlopePanelModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedInverseDoubleSlopePanel, registry, FramedInverseDoubleSlopePanelModel::new, FramedInverseDoubleSlopePanelModel.itemSource(), ignoreDefault);
    }



    public static void openSignScreen(BlockPos pos)
    {
        //noinspection ConstantConditions
        if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof FramedSignBlockEntity be)
        {
            Minecraft.getInstance().setScreen(new FramedSignScreen(be));
        }
    }



    private FBClient() { }
}