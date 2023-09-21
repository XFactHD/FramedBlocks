package xfacthd.framedblocks.client;

import com.github.benmanes.caffeine.cache.RemovalCause;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.client.ClientUtils;
import xfacthd.framedblocks.api.util.client.ModelCache;
import xfacthd.framedblocks.client.data.BlockOutlineRenderers;
import xfacthd.framedblocks.client.loader.overlay.OverlayLoader;
import xfacthd.framedblocks.client.model.FluidModel;
import xfacthd.framedblocks.client.model.FramedDoubleBlockModel;
import xfacthd.framedblocks.client.model.cube.*;
import xfacthd.framedblocks.client.model.door.*;
import xfacthd.framedblocks.client.model.interactive.*;
import xfacthd.framedblocks.client.model.pane.*;
import xfacthd.framedblocks.client.model.pillar.*;
import xfacthd.framedblocks.client.model.prism.*;
import xfacthd.framedblocks.client.model.rail.*;
import xfacthd.framedblocks.client.model.slab.*;
import xfacthd.framedblocks.client.model.slope.*;
import xfacthd.framedblocks.client.model.slopepanel.*;
import xfacthd.framedblocks.client.model.slopeslab.*;
import xfacthd.framedblocks.client.model.stairs.*;
import xfacthd.framedblocks.client.model.torch.*;
import xfacthd.framedblocks.client.render.*;
import xfacthd.framedblocks.client.screen.*;
import xfacthd.framedblocks.client.screen.overlay.*;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.interactive.FramedWeightedPressurePlateBlock;
import xfacthd.framedblocks.common.block.prism.FramedDoublePrismBlock;
import xfacthd.framedblocks.common.block.prism.FramedDoubleSlopedPrismBlock;
import xfacthd.framedblocks.common.block.rail.*;
import xfacthd.framedblocks.common.block.slope.*;
import xfacthd.framedblocks.common.block.slopepanel.*;
import xfacthd.framedblocks.common.block.slopeslab.*;
import xfacthd.framedblocks.common.block.stairs.FramedDoubleStairsBlock;
import xfacthd.framedblocks.common.block.stairs.FramedVerticalDoubleStairsBlock;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.client.data.GhostRenderBehaviours;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FBClient
{
    static
    {
        FramedBlocksClientAPI.INSTANCE.accept(new ClientApiImpl());
        // Forcefully classload RemovalCause because EventBus and ThreadPools can't get their classloader shit together
        RemovalCause.EXPLICIT.wasEvicted();
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            MenuScreens.register(FBContent.menuTypeFramedStorage.get(), FramedStorageScreen::new);
            MenuScreens.register(FBContent.menuTypeFramingSaw.get(), FramingSawScreen::new);

            BlueprintPropertyOverride.register();
        });

        BlockOutlineRenderers.register();
        GhostBlockRenderer.init();
        GhostRenderBehaviours.register();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(KeyMappings::register);
        modBus.addListener(FramedSignScreen::onTextureStitch);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(ClientUtils::onClientTick);
        forgeBus.addListener(BlockOutlineRenderer::onRenderBlockHighlight);
        forgeBus.addListener(KeyMappings::onClientTick);
        forgeBus.addListener(GhostBlockRenderer::onRenderLevelStage);
        forgeBus.addListener(ClientEventHandler::onRecipesUpdated);
        forgeBus.addListener(ClientEventHandler::onClientDisconnect);
    }

    @SubscribeEvent
    public static void onLoadComplete(final FMLLoadCompleteEvent event)
    {
        GhostBlockRenderer.lockRegistration();
        BlockOutlineRenderer.lockRegistration();
    }

    @SubscribeEvent
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(FBContent.blockEntityTypeFramedSign.get(), FramedSignRenderer::new);
        event.registerBlockEntityRenderer(FBContent.blockEntityTypeFramedChest.get(), FramedChestRenderer::new);
        event.registerBlockEntityRenderer(FBContent.blockEntityTypeFramedItemFrame.get(), FramedItemFrameRenderer::new);
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
        event.registerAboveAll("toggle_waterloggable", new ToggleWaterloggableOverlay());
        event.registerAboveAll("y_slope", new ToggleYSlopeOverlay());
        event.registerAboveAll("reinforced", new ReinforcementOverlay());
        event.registerAboveAll("prism_offset", new PrismOffsetOverlay());
        event.registerAboveAll("split_line", new SplitLineOverlay());
        event.registerAboveAll("one_way_window", new OneWayWindowOverlay());
        event.registerAboveAll("frame_background", new FrameBackgroundOverlay());
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
        event.register(FramedBlockModel.REINFORCEMENT_LOCATION);
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
        FramedBlockModel.captureReinforcementModel(event.getModels());
        FramedMarkedPressurePlateModel.cacheFrameModels(registry);
        FramedStoneButtonModel.cacheFrameModels(registry);
        FramedLargeStoneButtonModel.cacheFrameModels(registry);
        FramedTargetModel.cacheOverlayModel(registry);
        FramedOneWayWindowModel.captureTintedGlassModel(event.getModels());

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

        Function<BlockState, DoubleBlockParticleMode> particleTopFirstBottomSecond = state ->
                state.getValue(FramedProperties.TOP) ? DoubleBlockParticleMode.FIRST : DoubleBlockParticleMode.SECOND;
        Function<BlockState, DoubleBlockParticleMode> particleTopFirstElseEither = state ->
                state.getValue(FramedProperties.TOP) ? DoubleBlockParticleMode.FIRST : DoubleBlockParticleMode.EITHER;

        Vec3 yHalfUp = new Vec3(0, .5, 0);

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
        replaceDoubleBlockModels(FBContent.blockFramedDividedSlab, registry, DoubleBlockParticleMode.EITHER, null, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedPanel, registry, FramedPanelModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedCornerPillar, registry, FramedCornerPillarModel::new, ignoreWaterlogged);
        replaceDoubleBlockModels(FBContent.blockFramedDividedPanelHor, registry, DoubleBlockParticleMode.SECOND, null, ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedDividedPanelVert, registry, DoubleBlockParticleMode.EITHER, null, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedStairs, registry, FramedStairsModel::new, ignoreDefaultLock);
        ClientUtils.replaceModels(FBContent.blockFramedWall, registry, FramedWallModel::new, ignoreWaterloggedLock);
        ClientUtils.replaceModels(FBContent.blockFramedFence, registry, FramedFenceModel::createFenceModel, ignoreWaterloggedLock);
        ClientUtils.replaceModels(FBContent.blockFramedFenceGate, registry, FramedFenceGateModel::new, List.of(BlockStateProperties.POWERED));
        ClientUtils.replaceModels(FBContent.blockFramedDoor, registry, FramedDoorModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedIronDoor, registry, FramedIronDoorModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedTrapDoor, registry, FramedTrapDoorModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedIronTrapDoor, registry, FramedIronTrapDoorModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedPressurePlate, registry, FramedPressurePlateModel::new, null);
        ClientUtils.reuseModels(FBContent.blockFramedWaterloggablePressurePlate, registry, FBContent.blockFramedPressurePlate, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedStonePressurePlate, registry, FramedMarkedPressurePlateModel::stone, null);
        ClientUtils.reuseModels(FBContent.blockFramedWaterloggableStonePressurePlate, registry, FBContent.blockFramedStonePressurePlate, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedObsidianPressurePlate, registry, FramedMarkedPressurePlateModel::obsidian, null);
        ClientUtils.reuseModels(FBContent.blockFramedWaterloggableObsidianPressurePlate, registry, FBContent.blockFramedObsidianPressurePlate, ignoreWaterlogged);
        ClientUtils.replaceModelsSpecial(FBContent.blockFramedGoldPressurePlate, registry, FramedMarkedPressurePlateModel::gold, FramedWeightedPressurePlateBlock::mergeWeightedState);
        ClientUtils.reuseModels(FBContent.blockFramedWaterloggableGoldPressurePlate, registry, FBContent.blockFramedGoldPressurePlate, ignoreWaterlogged);
        ClientUtils.replaceModelsSpecial(FBContent.blockFramedIronPressurePlate, registry, FramedMarkedPressurePlateModel::iron, FramedWeightedPressurePlateBlock::mergeWeightedState);
        ClientUtils.reuseModels(FBContent.blockFramedWaterloggableIronPressurePlate, registry, FBContent.blockFramedIronPressurePlate, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedLadder, registry, FramedLadderModel::new, FramedLadderModel.itemSource(), ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedButton, registry, FramedButtonModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedStoneButton, registry, FramedStoneButtonModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedLever, registry, FramedLeverModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedSign, registry, FramedSignModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedWallSign, registry, FramedWallSignModel::new, ignoreWaterlogged);
        replaceDoubleBlockModels(FBContent.blockFramedDoubleSlab, registry, DoubleBlockParticleMode.SECOND, null, ignoreSolid);
        replaceDoubleBlockModels(FBContent.blockFramedDoublePanel, registry, DoubleBlockParticleMode.EITHER, null, ignoreSolid);
        replaceDoubleBlockModels(FBContent.blockFramedDoubleSlope, registry, FramedDoubleSlopeBlock::particleMode, FramedDoubleSlopeBlock.itemModelSource(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.blockFramedDoubleCorner, registry, FramedDoubleCornerBlock::particleMode, FramedDoubleCornerBlock.itemModelSource(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.blockFramedDoublePrismCorner, registry, particleTopFirstElseEither, FramedDoublePrismCornerBlock.itemModelSourcePrism(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.blockFramedDoubleThreewayCorner, registry, particleTopFirstElseEither, FramedDoubleThreewayCornerBlock.itemModelSourceThreeway(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedTorch, registry, FramedTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedWallTorch, registry, FramedWallTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedSoulTorch, registry, FramedSoulTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedSoulWallTorch, registry, FramedSoulWallTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedRedstoneTorch, registry, FramedRedstoneTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedRedstoneWallTorch, registry, FramedRedstoneWallTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedFloor, registry, FramedFloorModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedLattice, registry, FramedLatticeModel::new, ignoreWaterloggedLock);
        ClientUtils.replaceModels(FBContent.blockFramedVerticalStairs, registry, FramedVerticalStairsModel::new, ignoreDefaultLock);
        ClientUtils.replaceModels(FBContent.blockFramedChest, registry, FramedChestModel::new, FramedChestModel.itemSource(), ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedBars, registry, FramedBarsModel::new, ignoreWaterloggedLock);
        ClientUtils.replaceModels(FBContent.blockFramedPane, registry, FramedPaneModel::createPaneModel, ignoreWaterloggedLock);
        ClientUtils.replaceModels(FBContent.blockFramedRailSlope, registry, FramedRailSlopeModel::normal, FramedRailSlopeModel.itemSourceNormal(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedPoweredRailSlope, registry, FramedRailSlopeModel::powered, FramedRailSlopeModel.itemSourcePowered(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedDetectorRailSlope, registry, FramedRailSlopeModel::detector, FramedRailSlopeModel.itemSourceDetector(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedActivatorRailSlope, registry, FramedRailSlopeModel::activator, FramedRailSlopeModel.itemSourceActivator(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlowerPot, registry, FramedFlowerPotModel::new, null);
        ClientUtils.replaceModels(FBContent.blockFramedPillar, registry, FramedPillarModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedHalfPillar, registry, FramedHalfPillarModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedPost, registry, FramedPillarModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedCollapsibleBlock, registry, FramedCollapsibleBlockModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedHalfStairs, registry, FramedHalfStairsModel::new, ignoreWaterlogged);
        replaceDoubleBlockModels(FBContent.blockFramedDividedStairs, registry, DoubleBlockParticleMode.EITHER, null, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedBouncyCube, registry, (state, baseModel) -> FramedMarkedCubeModel.slime(state, baseModel, registry), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedSecretStorage, registry, FramedCubeBaseModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedRedstoneBlock, registry, (state, baseModel) -> FramedMarkedCubeModel.redstone(state, baseModel, registry), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedPrism, registry, FramedPrismModel::new, FramedPrismModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedInnerPrism, registry, FramedInnerPrismModel::new, FramedInnerPrismModel.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedDoublePrism, registry, FramedDoublePrismBlock::particleMode, FramedDoublePrismBlock.itemModelSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedSlopedPrism, registry, FramedSlopedPrismModel::new, FramedSlopedPrismModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedInnerSlopedPrism, registry, FramedInnerSlopedPrismModel::new, FramedInnerSlopedPrismModel.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedDoubleSlopedPrism, registry, FramedDoubleSlopedPrismBlock::particleMode, FramedDoubleSlopedPrismBlock.itemModelSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedSlopeSlab, registry, FramedSlopeSlabModel::new, FramedSlopeSlabModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedElevatedSlopeSlab, registry, FramedElevatedSlopeSlabModel::new, FramedElevatedSlopeSlabModel.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedDoubleSlopeSlab, registry, FramedDoubleSlopeSlabBlock::particleMode, yHalfUp, FramedDoubleSlopeSlabBlock.itemModelSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedInverseDoubleSlopeSlab, registry, DoubleBlockParticleMode.SECOND, FramedInverseDoubleSlopeSlabBlock.itemModelSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedElevatedDoubleSlopeSlab, registry, particleTopFirstBottomSecond, FramedElevatedDoubleSlopeSlabBlock.itemModelSource(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.blockFramedStackedSlopeSlab, registry, particleTopFirstBottomSecond, FramedStackedSlopeSlabBlock.itemModelSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatSlopeSlabCorner, registry, FramedFlatSlopeSlabCornerModel::new, FramedFlatSlopeSlabCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatInnerSlopeSlabCorner, registry, FramedFlatInnerSlopeSlabCornerModel::new, FramedFlatInnerSlopeSlabCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatElevatedSlopeSlabCorner, registry, FramedFlatElevatedSlopeSlabCornerModel::new, FramedFlatElevatedSlopeSlabCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner, registry, FramedFlatElevatedInnerSlopeSlabCornerModel::new, FramedFlatElevatedInnerSlopeSlabCornerModel.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedFlatDoubleSlopeSlabCorner, registry, particleTopFirstBottomSecond, yHalfUp, FramedFlatDoubleSlopeSlabCornerBlock.itemModelSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedFlatInverseDoubleSlopeSlabCorner, registry, particleTopFirstBottomSecond, FramedFlatInverseDoubleSlopeSlabCornerBlock.itemModelSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedFlatElevatedDoubleSlopeSlabCorner, registry, particleTopFirstBottomSecond, FramedFlatElevatedDoubleSlopeSlabCornerBlock.itemModelSource(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.blockFramedFlatElevatedInnerDoubleSlopeSlabCorner, registry, particleTopFirstBottomSecond, FramedFlatElevatedDoubleSlopeSlabCornerBlock.itemModelSourceInner(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.blockFramedFlatStackedSlopeSlabCorner, registry, particleTopFirstBottomSecond, FramedFlatStackedSlopeSlabCornerBlock.itemModelSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedFlatStackedInnerSlopeSlabCorner, registry, particleTopFirstBottomSecond, FramedFlatStackedSlopeSlabCornerBlock.itemModelSourceInner(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedVerticalHalfStairs, registry, FramedVerticalHalfStairsModel::new, ignoreWaterlogged);
        replaceDoubleBlockModels(FBContent.blockFramedVerticalDividedStairs, registry, DoubleBlockParticleMode.SECOND, null, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedSlopePanel, registry, FramedSlopePanelModel::new, FramedSlopePanelModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedExtendedSlopePanel, registry, FramedExtendedSlopePanelModel::new, FramedExtendedSlopePanelModel.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedDoubleSlopePanel, registry, FramedDoubleSlopePanelBlock::particleMode, FramedDoubleSlopePanelBlock.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedInverseDoubleSlopePanel, registry, DoubleBlockParticleMode.EITHER, FramedInverseDoubleSlopePanelBlock.itemModelSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedExtendedDoubleSlopePanel, registry, DoubleBlockParticleMode.EITHER, FramedExtendedDoubleSlopePanelBlock.itemSource(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.blockFramedStackedSlopePanel, registry, DoubleBlockParticleMode.EITHER, FramedStackedSlopePanelBlock.itemModelSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatSlopePanelCorner, registry, FramedFlatSlopePanelCornerModel::new, FramedFlatSlopePanelCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatInnerSlopePanelCorner, registry, FramedFlatInnerSlopePanelCornerModel::new, FramedFlatInnerSlopePanelCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatExtendedSlopePanelCorner, registry, FramedFlatExtendedSlopePanelCornerModel::new, FramedFlatExtendedSlopePanelCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedFlatExtendedInnerSlopePanelCorner, registry, FramedFlatExtendedInnerSlopePanelCornerModel::new, FramedFlatExtendedInnerSlopePanelCornerModel.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedFlatDoubleSlopePanelCorner, registry, FramedFlatDoubleSlopePanelCornerBlock::particleMode, yHalfUp, FramedFlatDoubleSlopePanelCornerBlock.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedFlatInverseDoubleSlopePanelCorner, registry, DoubleBlockParticleMode.EITHER, yHalfUp, FramedFlatInverseDoubleSlopePanelCornerBlock.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedFlatExtendedDoubleSlopePanelCorner, registry, DoubleBlockParticleMode.EITHER, FramedFlatExtendedDoubleSlopePanelCornerBlock.itemModelSource(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.blockFramedFlatExtendedInnerDoubleSlopePanelCorner, registry, FramedFlatExtendedDoubleSlopePanelCornerBlock::particleModeInner, FramedFlatExtendedDoubleSlopePanelCornerBlock.itemModelSourceInner(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.blockFramedFlatStackedSlopePanelCorner, registry, DoubleBlockParticleMode.EITHER, FramedFlatStackedSlopePanelCornerBlock.itemModelSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedFlatStackedInnerSlopePanelCorner, registry, DoubleBlockParticleMode.EITHER, FramedFlatStackedSlopePanelCornerBlock.itemModelSourceInner(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedDoubleStairs, registry, particleTopFirstElseEither, FramedDoubleStairsBlock.itemSource(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.blockFramedVerticalDoubleStairs, registry, DoubleBlockParticleMode.EITHER, FramedVerticalDoubleStairsBlock.itemModelSource(), ignoreSolid);
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
        ClientUtils.replaceModels(FBContent.blockFramedItemFrame, registry, FramedItemFrameModel::normal, null);
        ClientUtils.replaceModels(FBContent.blockFramedGlowingItemFrame, registry, FramedItemFrameModel::glowing, null);
        ClientUtils.replaceModels(FBContent.blockFramedFancyRail, registry, FramedFancyRailModel::normal, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedFancyPoweredRail, registry, FramedFancyRailModel::straight, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedFancyDetectorRail, registry, FramedFancyRailModel::straight, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedFancyActivatorRail, registry, FramedFancyRailModel::straight, ignoreWaterlogged);
        replaceDoubleBlockModels(FBContent.blockFramedFancyRailSlope, registry, DoubleBlockParticleMode.FIRST, FramedRailSlopeBlock.itemModelSourceFancy(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedFancyPoweredRailSlope, registry, DoubleBlockParticleMode.FIRST, FramedPoweredRailSlopeBlock.itemModelSourceFancyPowered(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedFancyDetectorRailSlope, registry, DoubleBlockParticleMode.FIRST, FramedDetectorRailSlopeBlock.itemModelSourceFancy(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedFancyActivatorRailSlope, registry, DoubleBlockParticleMode.FIRST, FramedPoweredRailSlopeBlock.itemModelSourceFancyActivator(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedHalfSlope, registry, FramedHalfSlopeModel::new, FramedHalfSlopeModel.itemSource(), ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedVerticalHalfSlope, registry, FramedVerticalHalfSlopeModel::new, ignoreWaterlogged);
        replaceDoubleBlockModels(FBContent.blockFramedDividedSlope, registry, FramedDividedSlopeBlock::particleMode, FramedDividedSlopeBlock.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedDoubleHalfSlope, registry, DoubleBlockParticleMode.SECOND, FramedDoubleHalfSlopeBlock.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.blockFramedVerticalDoubleHalfSlope, registry, DoubleBlockParticleMode.SECOND, null, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedSlopedStairs, registry, FramedSlopedStairsModel::new, FramedSlopedStairsModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedVerticalSlopedStairs, registry, FramedVerticalSlopedStairsModel::new, FramedVerticalSlopedStairsModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedMiniCube, registry, FramedMiniCubeModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedOneWayWindow, registry, FramedOneWayWindowModel::new, null);
    }

    @SubscribeEvent
    public static void onRegisterReloadListener(final RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener((ResourceManagerReloadListener) BlockInteractOverlay::onResourceReload);
    }



    @SuppressWarnings("SameParameterValue")
    private static void replaceDoubleBlockModels(
            RegistryObject<Block> block, Map<ResourceLocation, BakedModel> models,
            DoubleBlockParticleMode particleMode,
            Vec3 firstpersonTransform,
            @Nullable BlockState itemModelSource,
            @Nullable List<Property<?>> ignoredProps
    )
    {
        replaceDoubleBlockModels(block, models, state -> particleMode, firstpersonTransform, itemModelSource, ignoredProps);
    }

    private static void replaceDoubleBlockModels(
            RegistryObject<Block> block, Map<ResourceLocation, BakedModel> models,
            DoubleBlockParticleMode particleMode,
            @Nullable BlockState itemModelSource,
            @Nullable List<Property<?>> ignoredProps
    )
    {
        replaceDoubleBlockModels(block, models, state -> particleMode, null, itemModelSource, ignoredProps);
    }

    private static void replaceDoubleBlockModels(
            RegistryObject<Block> block, Map<ResourceLocation, BakedModel> models,
            Function<BlockState, DoubleBlockParticleMode> particleMode,
            @Nullable BlockState itemModelSource,
            @Nullable List<Property<?>> ignoredProps
    )
    {
        replaceDoubleBlockModels(block, models, particleMode, null, itemModelSource, ignoredProps);
    }

    private static void replaceDoubleBlockModels(
            RegistryObject<Block> block, Map<ResourceLocation, BakedModel> models,
            Function<BlockState, DoubleBlockParticleMode> particleMode,
            Vec3 firstpersonTransform,
            @Nullable BlockState itemModelSource,
            @Nullable List<Property<?>> ignoredProps
    )
    {
        ClientUtils.replaceModels(
                block,
                models,
                (state, model) -> new FramedDoubleBlockModel(
                        state,
                        model,
                        particleMode.apply(state),
                        firstpersonTransform,
                        itemModelSource != null
                ),
                itemModelSource,
                ignoredProps
        );
    }

    private static boolean useDefaultColorHandler(IFramedBlock block)
    {
        IBlockType type = block.getBlockType();
        return type != BlockType.FRAMED_TARGET;
    }



    private FBClient() { }
}