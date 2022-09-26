package xfacthd.framedblocks.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.client.ClientUtils;
import xfacthd.framedblocks.api.util.client.ModelCache;
import xfacthd.framedblocks.client.loader.overlay.OverlayLoader;
import xfacthd.framedblocks.client.model.*;
import xfacthd.framedblocks.client.model.FluidModel;
import xfacthd.framedblocks.client.render.*;
import xfacthd.framedblocks.client.render.outline.*;
import xfacthd.framedblocks.client.screen.*;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedWeightedPressurePlateBlock;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.client.data.GhostRenderBehaviours;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FBClient
{
    static { FramedBlocksClientAPI.INSTANCE.accept(new ClientApiImpl()); }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            MenuScreens.register(FBContent.menuTypeFramedStorage.get(), FramedStorageScreen::new);

            BlueprintPropertyOverride.register();
        });

        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPE, new SlopeOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_CORNER_SLOPE, new CornerSlopeOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INNER_CORNER_SLOPE, new InnerCornerSlopeOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_PRISM_CORNER, new PrismCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INNER_PRISM_CORNER, new InnerPrismCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_THREEWAY_CORNER, new ThreewayCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INNER_THREEWAY_CORNER, new InnerThreewayCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_RAIL_SLOPE, new RailSlopeOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_COLLAPSIBLE_BLOCK, new CollapsibleBlockOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_PRISM, new PrismOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPED_PRISM, new SlopedPrismOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPE_SLAB, new SlopeSlabOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_ELEVATED_SLOPE_SLAB, new ElevatedSlopeSlabOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB, new InverseDoubleSlopeSlabOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER, new FlatSlopeSlabCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, new FlatInnerSlopeSlabCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER, new FlatElevatedSlopeSlabCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER, new FlatElevatedInnerSlopeSlabCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER, new FlatInverseSlopeSlabCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPE_PANEL, new SlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_EXTENDED_SLOPE_PANEL, new ExtendedSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL, new InverseDoubleSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER, new FlatSlopePanelCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER, new FlatInnerSlopePanelCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER, new FlatExtendedSlopePanelCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER, new FlatExtendedInnerSlopePanelCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_PYRAMID, new PyramidOutlineRenderer(false));
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_PYRAMID_SLAB, new PyramidOutlineRenderer(true));

        GhostRenderBehaviours.register();
    }

    @SubscribeEvent
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(FBContent.blockEntityTypeFramedSign.get(), FramedSignRenderer::new);
        event.registerBlockEntityRenderer(FBContent.blockEntityTypeFramedChest.get(), FramedChestRenderer::new);
    }

    @SubscribeEvent
    public static void onBlockColors(final RegisterColorHandlersEvent.Block event)
    {
        //noinspection SuspiciousToArrayCall
        Block[] blocks = FBContent.getRegisteredBlocks()
                .stream()
                .map(RegistryObject::get)
                .filter(IFramedBlock.class::isInstance)
                .map(IFramedBlock.class::cast)
                .filter(FBClient::useDefaultColorHandler)
                .toArray(Block[]::new);

        event.register(FramedBlockColor.INSTANCE, blocks);

        event.register(FramedTargetBlockColor.INSTANCE, FBContent.blockFramedTarget.get());
    }

    @SubscribeEvent
    public static void onItemColors(final RegisterColorHandlersEvent.Item event)
    {
        event.register(FramedTargetBlockColor.INSTANCE, FBContent.blockFramedTarget.get());
    }

    @SubscribeEvent
    public static void onOverlayRegister(final RegisterGuiOverlaysEvent event)
    {
        event.registerAboveAll("state_lock", new StateLockOverlay());
    }

    @SubscribeEvent
    public static void onGeometryLoaderRegister(final ModelEvent.RegisterGeometryLoaders event)
    {
        event.register(OverlayLoader.ID.getPath(), new OverlayLoader());
    }

    @SubscribeEvent
    public static void onModelRegister(final ModelEvent.RegisterAdditional event)
    {
        event.register(FluidModel.BARE_MODEL);
        event.register(FramedMarkedCubeModel.SLIME_FRAME_LOCATION);
        event.register(FramedMarkedCubeModel.REDSTONE_FRAME_LOCATION);
        FramedMarkedPressurePlateModel.registerFrameModels(event);
        FramedStoneButtonModel.registerFrameModels(event);
        FramedLargeStoneButtonModel.registerFrameModels(event);
        event.register(FramedTargetModel.OVERLAY_LOCATION);

        if (SupplementariesCompat.isLoaded())
        {
            event.register(SupplementariesCompat.HANGING_MODEL_LOCATION);
        }
    }

    @SubscribeEvent
    public static void onModelsLoaded(final ModelEvent.BakingCompleted event)
    {
        Map<ResourceLocation, BakedModel> registry = event.getModels();

        ModelCache.clear(event.getModelBakery());
        FramedChestRenderer.onModelsLoaded(registry); //Must happen before the chest model is replaced
        FramedMarkedPressurePlateModel.cacheFrameModels(registry);
        FramedStoneButtonModel.cacheFrameModels(registry);
        FramedLargeStoneButtonModel.cacheFrameModels(registry);
        FramedTargetModel.cacheOverlayModel(registry);

        if (SupplementariesCompat.isLoaded())
        {
            FramedFlowerPotModel.cacheHangingModel(registry);
        }

        List<Property<?>> ignoreWaterlogged = List.of(BlockStateProperties.WATERLOGGED);
        List<Property<?>> ignoreWaterloggedLock = List.of(BlockStateProperties.WATERLOGGED, FramedProperties.STATE_LOCKED);
        List<Property<?>> ignoreSolid = List.of(FramedProperties.SOLID, FramedProperties.GLOWING);
        List<Property<?>> ignoreDefault = List.of(BlockStateProperties.WATERLOGGED, FramedProperties.SOLID, FramedProperties.GLOWING);
        List<Property<?>> ignoreDefaultLock = List.of(BlockStateProperties.WATERLOGGED, FramedProperties.SOLID, FramedProperties.GLOWING, FramedProperties.STATE_LOCKED);
        Function<BlockState, BlockState> ignoreAll = state -> state.getBlock().defaultBlockState();

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
        ClientUtils.replaceModels(FBContent.blockFramedStairs, registry, FramedStairsModel::new, ignoreDefaultLock);
        ClientUtils.replaceModels(FBContent.blockFramedWall, registry, FramedWallModel::new, ignoreWaterloggedLock);
        ClientUtils.replaceModels(FBContent.blockFramedFence, registry, FramedFenceModel::createFenceModel, ignoreWaterloggedLock);
        ClientUtils.replaceModels(FBContent.blockFramedFenceGate, registry, FramedFenceGateModel::new, List.of(BlockStateProperties.POWERED));
        ClientUtils.replaceModels(FBContent.blockFramedDoor, registry, FramedDoorModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedIronDoor, registry, FramedIronDoorModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedTrapDoor, registry, FramedTrapDoorModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedIronTrapDoor, registry, FramedIronTrapDoorModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedPressurePlate, registry, FramedPressurePlateModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedStonePressurePlate, registry, FramedMarkedPressurePlateModel::stone, null);
        ClientUtils.replaceModels(FBContent.blockFramedObsidianPressurePlate, registry, FramedMarkedPressurePlateModel::obsidian, null);
        ClientUtils.replaceModelsSpecial(FBContent.blockFramedGoldPressurePlate, registry, FramedMarkedPressurePlateModel::gold, FramedWeightedPressurePlateBlock::mergeWeightedState);
        ClientUtils.replaceModelsSpecial(FBContent.blockFramedIronPressurePlate, registry, FramedMarkedPressurePlateModel::iron, FramedWeightedPressurePlateBlock::mergeWeightedState);
        ClientUtils.replaceModels(FBContent.blockFramedLadder, registry, FramedLadderModel::new, FramedLadderModel.itemSource(), ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedButton, registry, FramedButtonModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedStoneButton, registry, FramedStoneButtonModel::new, null);
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
        ClientUtils.replaceModels(FBContent.blockFramedLattice, registry, FramedLatticeModel::new, ignoreWaterloggedLock);
        ClientUtils.replaceModels(FBContent.blockFramedVerticalStairs, registry, FramedVerticalStairsModel::new, ignoreDefaultLock);
        ClientUtils.replaceModels(FBContent.blockFramedChest, registry, FramedChestModel::new, FramedChestModel.itemSource(), ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedBars, registry, FramedBarsModel::new, ignoreWaterloggedLock);
        ClientUtils.replaceModels(FBContent.blockFramedPane, registry, FramedPaneModel::new, ignoreWaterloggedLock);
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
        ClientUtils.replaceModels(FBContent.blockFramedElevatedDoubleSlopeSlab, registry, FramedElevatedDoubleSlopeSlabModel::new, FramedElevatedDoubleSlopeSlabModel.itemSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedFlatSlopeSlabCorner, registry, FramedFlatSlopeSlabCornerModel::new, FramedFlatSlopeSlabCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatInnerSlopeSlabCorner, registry, FramedFlatInnerSlopeSlabCornerModel::new, FramedFlatInnerSlopeSlabCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatElevatedSlopeSlabCorner, registry, FramedFlatElevatedSlopeSlabCornerModel::new, FramedFlatElevatedSlopeSlabCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner, registry, FramedFlatElevatedInnerSlopeSlabCornerModel::new, FramedFlatElevatedInnerSlopeSlabCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatDoubleSlopeSlabCorner, registry, FramedFlatDoubleSlopeSlabCornerModel::new, FramedFlatDoubleSlopeSlabCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatInverseDoubleSlopeSlabCorner, registry, FramedFlatInverseDoubleSlopeSlabCornerModel::new, FramedFlatInverseDoubleSlopeSlabCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatElevatedDoubleSlopeSlabCorner, registry, FramedFlatElevatedDoubleSlopeSlabCornerModel::new, FramedFlatElevatedDoubleSlopeSlabCornerModel.itemSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedFlatElevatedInnerDoubleSlopeSlabCorner, registry, FramedFlatElevatedInnerDoubleSlopeSlabCornerModel::new, FramedFlatElevatedInnerDoubleSlopeSlabCornerModel.itemSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedVerticalHalfStairs, registry, FramedVerticalHalfStairsModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedSlopePanel, registry, FramedSlopePanelModel::new, FramedSlopePanelModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedExtendedSlopePanel, registry, FramedExtendedSlopePanelModel::new, FramedExtendedSlopePanelModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedDoubleSlopePanel, registry, FramedDoubleSlopePanelModel::new, FramedDoubleSlopePanelModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedInverseDoubleSlopePanel, registry, FramedInverseDoubleSlopePanelModel::new, FramedInverseDoubleSlopePanelModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedExtendedDoubleSlopePanel, registry, FramedExtendedDoubleSlopePanelModel::new, FramedExtendedDoubleSlopePanelModel.itemSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedFlatSlopePanelCorner, registry, FramedFlatSlopePanelCornerModel::new, FramedFlatSlopePanelCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatInnerSlopePanelCorner, registry, FramedFlatInnerSlopePanelCornerModel::new, FramedFlatInnerSlopePanelCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatExtendedSlopePanelCorner, registry, FramedFlatExtendedSlopePanelCornerModel::new, FramedFlatExtendedSlopePanelCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatExtendedInnerSlopePanelCorner, registry, FramedFlatExtendedInnerSlopePanelCornerModel::new, FramedFlatExtendedInnerSlopePanelCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedDoubleStairs, registry, FramedDoubleStairsModel::new, FramedDoubleStairsModel.itemSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedVerticalDoubleStairs, registry, FramedVerticalDoubleStairsModel::new, FramedVerticalDoubleStairsModel.itemSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedWallBoard, registry, FramedWallBoardModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedGlowingCube, registry, FramedGlowingCubeModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedPyramid, registry, FramedPyramidModel::new, FramedPyramidModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedPyramidSlab, registry, FramedPyramidSlabModel::new, FramedPyramidSlabModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModelsSpecial(FBContent.blockFramedLargeButton, registry, FramedLargeButtonModel::new, FramedLargeButtonModel::mergeStates);
        ClientUtils.replaceModelsSpecial(FBContent.blockFramedLargeStoneButton, registry, FramedLargeStoneButtonModel::new, FramedLargeButtonModel::mergeStates);
        ClientUtils.replaceModels(FBContent.blockFramedHorizontalPane, registry, FramedHorizontalPaneModel::new, ignoreDefault);
        ClientUtils.replaceModelsSpecial(FBContent.blockFramedTarget, registry, FramedTargetModel::new, FramedTargetModel.itemSource(), ignoreAll);
        ClientUtils.replaceModels(FBContent.blockFramedGate, registry, FramedDoorModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedIronGate, registry, FramedIronDoorModel::new, ignoreSolid);
    }



    private static boolean useDefaultColorHandler(IFramedBlock block)
    {
        IBlockType type = block.getBlockType();
        return type != BlockType.FRAMED_TARGET;
    }



    private FBClient() { }
}