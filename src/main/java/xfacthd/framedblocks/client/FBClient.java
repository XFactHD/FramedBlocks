package xfacthd.framedblocks.client;

import com.github.benmanes.caffeine.cache.RemovalCause;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
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
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.api.model.util.ModelCache;
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
import xfacthd.framedblocks.common.block.interactive.*;
import xfacthd.framedblocks.common.block.prism.*;
import xfacthd.framedblocks.common.block.rail.*;
import xfacthd.framedblocks.common.block.slope.*;
import xfacthd.framedblocks.common.block.slopepanel.*;
import xfacthd.framedblocks.common.block.slopeslab.*;
import xfacthd.framedblocks.common.block.stairs.*;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.client.data.GhostRenderBehaviours;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FBClient
{
    private static final boolean ENABLE_DOUBLE_BLOCK_DEBUG_RENDERER = false;

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
            MenuScreens.register(FBContent.MENU_TYPE_FRAMED_STORAGE.get(), FramedStorageScreen::new);
            MenuScreens.register(FBContent.MENU_TYPE_FRAMING_SAW.get(), FramingSawScreen::new);

            BlueprintPropertyOverride.register();
        });

        BlockOutlineRenderers.register();
        GhostBlockRenderer.init();
        GhostRenderBehaviours.register();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
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
    public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event)
    {
        event.register(KeyMappings.KEYMAPPING_UPDATE_CULLING.get());
        event.register(KeyMappings.KEYMAPPING_WIPE_CACHE.get());
    }

    @SubscribeEvent
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_SIGN.get(), FramedSignRenderer::new);
        event.registerBlockEntityRenderer(FBContent.blockEntityTypeFramedChest.get(), FramedChestRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_ITEM_FRAME.get(), FramedItemFrameRenderer::new);

        if (!FMLEnvironment.production && ENABLE_DOUBLE_BLOCK_DEBUG_RENDERER)
        {
            BlockEntityRendererProvider<FramedDoubleBlockEntity> provider = FramedDoubleBlockDebugRenderer::new;
            FBContent.getDoubleBlockEntities().forEach(type ->
                    event.registerBlockEntityRenderer(type.get(), provider)
            );
        }
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

        event.register(FramedTargetBlockColor.INSTANCE, FBContent.BLOCK_FRAMED_TARGET.get());
    }

    @SubscribeEvent
    public static void onItemColors(final RegisterColorHandlersEvent.Item event)
    {
        event.register(FramedTargetBlockColor.INSTANCE, FBContent.BLOCK_FRAMED_TARGET.get());
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
    public static void onModifyBakingResult(final ModelEvent.ModifyBakingResult event)
    {
        Map<ResourceLocation, BakedModel> registry = event.getModels();

        FramedChestRenderer.onModelsLoaded(registry); //Must happen before the chest model is replaced
        FramedMarkedPressurePlateModel.cacheFrameModels(registry);
        FramedStoneButtonModel.cacheFrameModels(registry);
        FramedLargeStoneButtonModel.cacheFrameModels(registry);

        List<Property<?>> ignoreWaterlogged = List.of(BlockStateProperties.WATERLOGGED, FramedProperties.GLOWING);
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

        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_CUBE, registry, FramedCubeModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_SLOPE, registry, FramedSlopeModel::new, FramedSlopeModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_CORNER_SLOPE, registry, FramedCornerSlopeModel::new, FramedCornerSlopeModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE, registry, FramedInnerCornerSlopeModel::new, FramedInnerCornerSlopeModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_PRISM_CORNER, registry, FramedPrismCornerModel::new, FramedPrismCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER, registry, FramedInnerPrismCornerModel::new, FramedInnerPrismCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_THREEWAY_CORNER, registry, FramedThreewayCornerModel::new, FramedThreewayCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER, registry, FramedInnerThreewayCornerModel::new, FramedInnerThreewayCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_SLAB, registry, FramedSlabModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_SLAB_EDGE, registry, FramedSlabEdgeModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_SLAB_CORNER, registry, FramedSlabCornerModel::new, ignoreWaterlogged);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DIVIDED_SLAB, registry, DoubleBlockParticleMode.EITHER, null, ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_PANEL, registry, FramedPanelModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_CORNER_PILLAR, registry, FramedCornerPillarModel::new, ignoreWaterlogged);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_HOR, registry, DoubleBlockParticleMode.SECOND, null, ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT, registry, DoubleBlockParticleMode.EITHER, null, ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_STAIRS, registry, FramedStairsModel::new, ignoreDefaultLock);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_WALL, registry, FramedWallModel::new, ignoreWaterloggedLock);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FENCE, registry, FramedFenceModel::createFenceModel, ignoreWaterloggedLock);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FENCE_GATE, registry, FramedFenceGateModel::new, List.of(BlockStateProperties.POWERED));
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_DOOR, registry, FramedDoorModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_IRON_DOOR, registry, FramedIronDoorModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_TRAP_DOOR, registry, FramedTrapDoorModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR, registry, FramedIronTrapDoorModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_PRESSURE_PLATE, registry, FramedPressurePlateModel::new, null);
        ClientUtils.reuseModels(FBContent.BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE, registry, FBContent.BLOCK_FRAMED_PRESSURE_PLATE, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE, registry, FramedMarkedPressurePlateModel::stone, null);
        ClientUtils.reuseModels(FBContent.BLOCK_FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE, registry, FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE, registry, FramedMarkedPressurePlateModel::obsidian, null);
        ClientUtils.reuseModels(FBContent.BLOCK_FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE, registry, FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE, ignoreWaterlogged);
        ClientUtils.replaceModelsSpecial(FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE, registry, FramedMarkedPressurePlateModel::gold, FramedWeightedPressurePlateBlock::mergeWeightedState);
        ClientUtils.reuseModels(FBContent.BLOCK_FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE, registry, FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE, ignoreWaterlogged);
        ClientUtils.replaceModelsSpecial(FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE, registry, FramedMarkedPressurePlateModel::iron, FramedWeightedPressurePlateBlock::mergeWeightedState);
        ClientUtils.reuseModels(FBContent.BLOCK_FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE, registry, FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_LADDER, registry, FramedLadderModel::new, FramedLadderModel.itemSource(), ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_BUTTON, registry, FramedButtonModel::new, null);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_STONE_BUTTON, registry, FramedStoneButtonModel::new, null);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_LEVER, registry, FramedLeverModel::new, null);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_SIGN, registry, FramedSignModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_WALL_SIGN, registry, FramedWallSignModel::new, ignoreWaterlogged);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DOUBLE_SLAB, registry, DoubleBlockParticleMode.SECOND, null, ignoreSolid);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DOUBLE_PANEL, registry, DoubleBlockParticleMode.EITHER, null, ignoreSolid);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE, registry, FramedDoubleSlopeBlock::particleMode, FramedDoubleSlopeBlock.itemModelSource(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DOUBLE_CORNER, registry, FramedDoubleCornerBlock::particleMode, FramedDoubleCornerBlock.itemModelSource(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER, registry, particleTopFirstElseEither, FramedDoublePrismCornerBlock.itemModelSourcePrism(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER, registry, particleTopFirstElseEither, FramedDoubleThreewayCornerBlock.itemModelSourceThreeway(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_TORCH, registry, FramedTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_WALL_TORCH, registry, FramedWallTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_SOUL_TORCH, registry, FramedSoulTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_SOUL_WALL_TORCH, registry, FramedSoulWallTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_REDSTONE_TORCH, registry, FramedRedstoneTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_REDSTONE_WALL_TORCH, registry, FramedRedstoneWallTorchModel::new, null);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FLOOR, registry, FramedFloorModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_LATTICE, registry, FramedLatticeModel::new, ignoreWaterloggedLock);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS, registry, FramedVerticalStairsModel::new, ignoreDefaultLock);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_CHEST, registry, FramedChestModel::new, FramedChestModel.itemSource(), ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_BARS, registry, FramedBarsModel::new, ignoreWaterloggedLock);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_PANE, registry, FramedPaneModel::new, ignoreWaterloggedLock);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_RAIL_SLOPE, registry, FramedRailSlopeModel::normal, FramedRailSlopeModel.itemSourceNormal(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE, registry, FramedRailSlopeModel::powered, FramedRailSlopeModel.itemSourcePowered(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE, registry, FramedRailSlopeModel::detector, FramedRailSlopeModel.itemSourceDetector(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE, registry, FramedRailSlopeModel::activator, FramedRailSlopeModel.itemSourceActivator(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FLOWER_POT, registry, (state, model) -> new FramedFlowerPotModel(state, model, registry), null);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_PILLAR, registry, FramedPillarModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_HALF_PILLAR, registry, FramedHalfPillarModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_POST, registry, FramedPillarModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK, registry, FramedCollapsibleBlockModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_HALF_STAIRS, registry, FramedHalfStairsModel::new, ignoreWaterlogged);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DIVIDED_STAIRS, registry, DoubleBlockParticleMode.EITHER, null, ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_BOUNCY_CUBE, registry, (state, baseModel) -> FramedMarkedCubeModel.slime(state, baseModel, registry), ignoreSolid);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_SECRET_STORAGE, registry, FramedCubeBaseModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_REDSTONE_BLOCK, registry, (state, baseModel) -> FramedMarkedCubeModel.redstone(state, baseModel, registry), ignoreSolid);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_PRISM, registry, FramedPrismModel::new, FramedPrismModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_INNER_PRISM, registry, FramedInnerPrismModel::new, FramedInnerPrismModel.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DOUBLE_PRISM, registry, FramedDoublePrismBlock::particleMode, FramedDoublePrismBlock.itemModelSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_SLOPED_PRISM, registry, FramedSlopedPrismModel::new, FramedSlopedPrismModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_INNER_SLOPED_PRISM, registry, FramedInnerSlopedPrismModel::new, FramedInnerSlopedPrismModel.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DOUBLE_SLOPED_PRISM, registry, FramedDoubleSlopedPrismBlock::particleMode, FramedDoubleSlopedPrismBlock.itemModelSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_SLOPE_SLAB, registry, FramedSlopeSlabModel::new, FramedSlopeSlabModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB, registry, FramedElevatedSlopeSlabModel::new, FramedElevatedSlopeSlabModel.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB, registry, FramedDoubleSlopeSlabBlock::particleMode, yHalfUp, FramedDoubleSlopeSlabBlock.itemModelSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB, registry, DoubleBlockParticleMode.SECOND, FramedInverseDoubleSlopeSlabBlock.itemModelSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB, registry, particleTopFirstBottomSecond, FramedElevatedDoubleSlopeSlabBlock.itemModelSource(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_STACKED_SLOPE_SLAB, registry, particleTopFirstBottomSecond, FramedStackedSlopeSlabBlock.itemModelSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER, registry, FramedFlatSlopeSlabCornerModel::new, FramedFlatSlopeSlabCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, registry, FramedFlatInnerSlopeSlabCornerModel::new, FramedFlatInnerSlopeSlabCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER, registry, FramedFlatElevatedSlopeSlabCornerModel::new, FramedFlatElevatedSlopeSlabCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER, registry, FramedFlatElevatedInnerSlopeSlabCornerModel::new, FramedFlatElevatedInnerSlopeSlabCornerModel.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER, registry, particleTopFirstBottomSecond, yHalfUp, FramedFlatDoubleSlopeSlabCornerBlock.itemModelSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER, registry, particleTopFirstBottomSecond, FramedFlatInverseDoubleSlopeSlabCornerBlock.itemModelSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER, registry, particleTopFirstBottomSecond, FramedFlatElevatedDoubleSlopeSlabCornerBlock.itemModelSource(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER, registry, particleTopFirstBottomSecond, FramedFlatElevatedDoubleSlopeSlabCornerBlock.itemModelSourceInner(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER, registry, particleTopFirstBottomSecond, FramedFlatStackedSlopeSlabCornerBlock.itemModelSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER, registry, particleTopFirstBottomSecond, FramedFlatStackedSlopeSlabCornerBlock.itemModelSourceInner(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS, registry, FramedVerticalHalfStairsModel::new, ignoreWaterlogged);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS, registry, DoubleBlockParticleMode.SECOND, null, ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_SLOPE_PANEL, registry, FramedSlopePanelModel::new, FramedSlopePanelModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_EXTENDED_SLOPE_PANEL, registry, FramedExtendedSlopePanelModel::new, FramedExtendedSlopePanelModel.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL, registry, FramedDoubleSlopePanelBlock::particleMode, FramedDoubleSlopePanelBlock.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL, registry, DoubleBlockParticleMode.EITHER, FramedInverseDoubleSlopePanelBlock.itemModelSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL, registry, DoubleBlockParticleMode.EITHER, FramedExtendedDoubleSlopePanelBlock.itemSource(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_STACKED_SLOPE_PANEL, registry, DoubleBlockParticleMode.EITHER, FramedStackedSlopePanelBlock.itemModelSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER, registry, FramedFlatSlopePanelCornerModel::new, FramedFlatSlopePanelCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER, registry, FramedFlatInnerSlopePanelCornerModel::new, FramedFlatInnerSlopePanelCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER, registry, FramedFlatExtendedSlopePanelCornerModel::new, FramedFlatExtendedSlopePanelCornerModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER, registry, FramedFlatExtendedInnerSlopePanelCornerModel::new, FramedFlatExtendedInnerSlopePanelCornerModel.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER, registry, FramedFlatDoubleSlopePanelCornerBlock::particleMode, yHalfUp, FramedFlatDoubleSlopePanelCornerBlock.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER, registry, DoubleBlockParticleMode.EITHER, yHalfUp, FramedFlatInverseDoubleSlopePanelCornerBlock.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER, registry, DoubleBlockParticleMode.EITHER, FramedFlatExtendedDoubleSlopePanelCornerBlock.itemModelSource(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER, registry, FramedFlatExtendedDoubleSlopePanelCornerBlock::particleModeInner, FramedFlatExtendedDoubleSlopePanelCornerBlock.itemModelSourceInner(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER, registry, DoubleBlockParticleMode.EITHER, FramedFlatStackedSlopePanelCornerBlock.itemModelSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER, registry, DoubleBlockParticleMode.EITHER, FramedFlatStackedSlopePanelCornerBlock.itemModelSourceInner(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DOUBLE_STAIRS, registry, particleTopFirstElseEither, FramedDoubleStairsBlock.itemSource(), ignoreSolid);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS, registry, DoubleBlockParticleMode.EITHER, FramedVerticalDoubleStairsBlock.itemModelSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_WALL_BOARD, registry, FramedWallBoardModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_GLOWING_CUBE, registry, FramedGlowingCubeModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_PYRAMID, registry, FramedPyramidModel::new, FramedPyramidModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_PYRAMID_SLAB, registry, FramedPyramidSlabModel::new, FramedPyramidSlabModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModelsSpecial(FBContent.BLOCK_FRAMED_LARGE_BUTTON, registry, FramedLargeButtonModel::new, FramedLargeButtonModel::mergeStates);
        ClientUtils.replaceModelsSpecial(FBContent.BLOCK_FRAMED_LARGE_STONE_BUTTON, registry, FramedLargeStoneButtonModel::new, FramedLargeButtonModel::mergeStates);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_HORIZONTAL_PANE, registry, FramedHorizontalPaneModel::new, ignoreDefault);
        ClientUtils.replaceModelsSpecial(FBContent.BLOCK_FRAMED_TARGET, registry, (state, model) -> new FramedTargetModel(state, model, registry), FramedTargetModel.itemSource(), ignoreAll);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_GATE, registry, FramedDoorModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_IRON_GATE, registry, FramedIronDoorModel::new, ignoreSolid);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_ITEM_FRAME, registry, FramedItemFrameModel::normal, null);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME, registry, FramedItemFrameModel::glowing, null);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FANCY_RAIL, registry, FramedFancyRailModel::normal, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL, registry, FramedFancyRailModel::straight, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL, registry, FramedFancyRailModel::straight, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL, registry, FramedFancyRailModel::straight, ignoreWaterlogged);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE, registry, DoubleBlockParticleMode.FIRST, FramedRailSlopeBlock.itemModelSourceFancy(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE, registry, DoubleBlockParticleMode.FIRST, FramedPoweredRailSlopeBlock.itemModelSourceFancyPowered(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE, registry, DoubleBlockParticleMode.FIRST, FramedDetectorRailSlopeBlock.itemModelSourceFancy(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE, registry, DoubleBlockParticleMode.FIRST, FramedPoweredRailSlopeBlock.itemModelSourceFancyActivator(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_HALF_SLOPE, registry, FramedHalfSlopeModel::new, FramedHalfSlopeModel.itemSource(), ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE, registry, FramedVerticalHalfSlopeModel::new, ignoreWaterlogged);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DIVIDED_SLOPE, registry, FramedDividedSlopeBlock::particleMode, FramedDividedSlopeBlock.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE, registry, DoubleBlockParticleMode.SECOND, FramedDoubleHalfSlopeBlock.itemSource(), ignoreDefault);
        replaceDoubleBlockModels(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE, registry, DoubleBlockParticleMode.SECOND, null, ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_SLOPED_STAIRS, registry, FramedSlopedStairsModel::new, FramedSlopedStairsModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS, registry, FramedVerticalSlopedStairsModel::new, FramedVerticalSlopedStairsModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_MINI_CUBE, registry, FramedMiniCubeModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW, registry, FramedOneWayWindowModel::new, List.of(FramedProperties.GLOWING));
    }

    @SubscribeEvent
    public static void onModelsLoaded(final ModelEvent.BakingCompleted event)
    {
        ModelCache.clear(event.getModelBakery());
        FramedChestRenderer.onModelLoadingComplete();
        FramedBlockModel.captureReinforcementModel(event.getModels());
        FramedOneWayWindowModel.captureTintedGlassModel(event.getModels());
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