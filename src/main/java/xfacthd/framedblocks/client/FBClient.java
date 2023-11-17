package xfacthd.framedblocks.client;

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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.bus.api.*;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.*;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.model.wrapping.*;
import xfacthd.framedblocks.client.modelwrapping.ModelWrappingManager;
import xfacthd.framedblocks.client.model.FramedBlockModel;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.model.util.ModelCache;
import xfacthd.framedblocks.client.data.*;
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
import xfacthd.framedblocks.client.model.slopepanelcorner.*;
import xfacthd.framedblocks.client.model.slopeslab.*;
import xfacthd.framedblocks.client.model.stairs.*;
import xfacthd.framedblocks.client.model.torch.*;
import xfacthd.framedblocks.client.render.block.*;
import xfacthd.framedblocks.client.render.item.BlueprintPropertyOverride;
import xfacthd.framedblocks.client.render.special.*;
import xfacthd.framedblocks.client.render.util.AnimationSplitterSource;
import xfacthd.framedblocks.client.screen.*;
import xfacthd.framedblocks.client.screen.overlay.*;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.interactive.*;
import xfacthd.framedblocks.common.block.prism.*;
import xfacthd.framedblocks.common.block.rail.*;
import xfacthd.framedblocks.common.block.slope.*;
import xfacthd.framedblocks.common.block.slopepanel.*;
import xfacthd.framedblocks.common.block.slopepanelcorner.*;
import xfacthd.framedblocks.common.block.slopeslab.*;
import xfacthd.framedblocks.common.block.stairs.*;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.compat.modernfix.ModernFixCompat;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.StateCacheBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FBClient
{
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            MenuScreens.register(FBContent.MENU_TYPE_FRAMED_STORAGE.get(), FramedStorageScreen::new);
            MenuScreens.register(FBContent.MENU_TYPE_FRAMING_SAW.get(), FramingSawScreen::new);
            MenuScreens.register(FBContent.MENU_TYPE_POWERED_FRAMING_SAW.get(), PoweredFramingSawScreen::new);

            BlueprintPropertyOverride.register();
        });

        BlockOutlineRenderers.register();
        GhostBlockRenderer.init();
        GhostRenderBehaviours.register();

        IEventBus forgeBus = NeoForge.EVENT_BUS;
        forgeBus.addListener(ClientTaskQueue::onClientTick);
        forgeBus.addListener(BlockOutlineRenderer::onRenderBlockHighlight);
        forgeBus.addListener(KeyMappings::onClientTick);
        forgeBus.addListener(GhostBlockRenderer::onRenderLevelStage);
        forgeBus.addListener(EventPriority.HIGH, ClientEventHandler::onRecipesUpdated);
        forgeBus.addListener(ClientEventHandler::onClientDisconnect);
        forgeBus.addListener(EventPriority.LOW, true, CollapsibleBlockIndicatorRenderer::onRenderBlockHighlight);
    }

    @SubscribeEvent
    public static void onImcMessageReceived(final InterModProcessEvent event)
    {
        event.getIMCStream()
                .filter(msg -> msg.method().equals(ConTexDataHandler.IMC_METHOD_ADD_PROPERTY))
                .map(InterModComms.IMCMessage::messageSupplier)
                .map(Supplier::get)
                .filter(ModelProperty.class::isInstance)
                .map(ModelProperty.class::cast)
                .forEach(ConTexDataHandler::addConTexProperty);
    }

    @SubscribeEvent
    public static void onLoadComplete(final FMLLoadCompleteEvent event)
    {
        GhostBlockRenderer.lockRegistration();
        BlockOutlineRenderer.lockRegistration();
        ConTexDataHandler.lockRegistration();
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
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_HANGING_SIGN.get(), FramedHangingSignRenderer::new);
        event.registerBlockEntityRenderer(FBContent.blockEntityTypeFramedChest.get(), FramedChestRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_ITEM_FRAME.get(), FramedItemFrameRenderer::new);

        if (!FMLEnvironment.production && FramedDoubleBlockEntity.ENABLE_DOUBLE_BLOCK_DEBUG_RENDERER)
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
        event.registerAboveAll("camo_rotation", new CamoRotationOverlay());
    }

    @SubscribeEvent
    public static void onGeometryLoaderRegister(final ModelEvent.RegisterGeometryLoaders event)
    {
        event.register(OverlayLoader.ID.getPath(), new OverlayLoader());
    }

    @SubscribeEvent
    public static void onRegisterModelWrappers(final RegisterModelWrappersEvent event)
    {
        Vec3 yHalfUp = new Vec3(0, .5, 0);

        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CUBE, FramedCubeGeometry::new, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE, FramedSlopeGeometry::new, FramedSlopeGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_SLOPE, FramedCornerSlopeGeometry::new, FramedCornerSlopeGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE, FramedInnerCornerSlopeGeometry::new, FramedInnerCornerSlopeGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PRISM_CORNER, FramedPrismCornerGeometry::new, FramedPrismCornerGeometry.itemSource(), WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER, FramedInnerPrismCornerGeometry::new, FramedInnerPrismCornerGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_THREEWAY_CORNER, FramedThreewayCornerGeometry::new, FramedThreewayCornerGeometry.itemSource(), WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER, FramedInnerThreewayCornerGeometry::new, FramedInnerThreewayCornerGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLAB, FramedSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLAB_EDGE, FramedSlabEdgeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLAB_CORNER, FramedSlabCornerGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_SLAB, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PANEL, FramedPanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_PILLAR, FramedCornerPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_HOR, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_STAIRS, FramedStairsGeometry::new, WrapHelper.IGNORE_DEFAULT_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL, FramedWallGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FENCE, FramedFenceGeometry::createFenceGeometry, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FENCE_GATE, FramedFenceGateGeometry::new, List.of(BlockStateProperties.POWERED));
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_DOOR, FramedDoorGeometry::new, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_DOOR, FramedIronDoorGeometry::new, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TRAP_DOOR, FramedTrapDoorGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR, FramedIronTrapDoorGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PRESSURE_PLATE, FramedPressurePlateGeometry::new, StateMerger.NO_OP);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_PRESSURE_PLATE, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE, FramedMarkedPressurePlateGeometry::stone, StateMerger.NO_OP);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE, FramedMarkedPressurePlateGeometry::obsidian, StateMerger.NO_OP);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE, FramedMarkedPressurePlateGeometry::gold, FramedWeightedPressurePlateBlock::mergeWeightedState);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE, FramedMarkedPressurePlateGeometry::iron, FramedWeightedPressurePlateBlock::mergeWeightedState);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LADDER, FramedLadderGeometry::new, FramedLadderGeometry.itemSource(), WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BUTTON, FramedButtonGeometry::new, StateMerger.NO_OP);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_STONE_BUTTON, FramedStoneButtonGeometry::new, StateMerger.NO_OP);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LEVER, FramedLeverGeometry::new, StateMerger.NO_OP);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SIGN, FramedSignGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_SIGN, FramedWallSignGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HANGING_SIGN, FramedCeilingHangingSignGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_HANGING_SIGN, FramedWallHangingSignGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLAB, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_PANEL, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE, FramedDoubleSlopeBlock.itemModelSource(), WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_CORNER, FramedDoubleCornerBlock.itemModelSource(), WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER, FramedDoublePrismCornerBlock.itemModelSourcePrism(), WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER, FramedDoubleThreewayCornerBlock.itemModelSourceThreeway(), WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TORCH, FramedTorchGeometry::new, StateMerger.NO_OP);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_TORCH, FramedWallTorchGeometry::new, StateMerger.NO_OP);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SOUL_TORCH, FramedSoulTorchGeometry::new, StateMerger.NO_OP);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SOUL_WALL_TORCH, FramedSoulWallTorchGeometry::new, StateMerger.NO_OP);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_REDSTONE_TORCH, FramedRedstoneTorchGeometry::new, StateMerger.NO_OP);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_REDSTONE_WALL_TORCH, FramedRedstoneWallTorchGeometry::new, StateMerger.NO_OP);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLOOR, FramedFloorBoardGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LATTICE, FramedLatticeGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_THICK_LATTICE, FramedLatticeGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS, FramedVerticalStairsGeometry::new, WrapHelper.IGNORE_DEFAULT_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHEST, FramedChestGeometry::new, FramedChestGeometry.itemSource(), WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BARS, FramedBarsGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PANE, FramedPaneGeometry::createPaneGeometry, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_RAIL_SLOPE, FramedRailSlopeGeometry::normal, FramedRailSlopeGeometry.itemSourceNormal(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE, FramedRailSlopeGeometry::powered, FramedRailSlopeGeometry.itemSourcePowered(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE, FramedRailSlopeGeometry::detector, FramedRailSlopeGeometry.itemSourceDetector(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE, FramedRailSlopeGeometry::activator, FramedRailSlopeGeometry.itemSourceActivator(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLOWER_POT, FramedFlowerPotGeometry::new, StateMerger.NO_OP);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PILLAR, FramedPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_PILLAR, FramedHalfPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_POST, FramedPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK, FramedCollapsibleBlockGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_STAIRS, FramedHalfStairsGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_STAIRS, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BOUNCY_CUBE, FramedMarkedCubeGeometry::slime, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SECRET_STORAGE, FramedCubeGeometry::new, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_REDSTONE_BLOCK, FramedMarkedCubeGeometry::redstone, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PRISM, FramedPrismGeometry::new, FramedPrismGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_PRISM, FramedInnerPrismGeometry::new, FramedInnerPrismGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_PRISM, FramedDoublePrismBlock.itemModelSource(), WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPED_PRISM, FramedSlopedPrismGeometry::new, FramedSlopedPrismGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_SLOPED_PRISM, FramedInnerSlopedPrismGeometry::new, FramedInnerSlopedPrismGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPED_PRISM, FramedDoubleSlopedPrismBlock.itemModelSource(), WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_SLAB, FramedSlopeSlabGeometry::new, FramedSlopeSlabGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB, FramedElevatedSlopeSlabGeometry::new, FramedElevatedSlopeSlabGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB, yHalfUp, FramedDoubleSlopeSlabBlock.itemModelSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB, FramedInverseDoubleSlopeSlabBlock.itemModelSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB, FramedElevatedDoubleSlopeSlabBlock.itemModelSource(), WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_SLOPE_SLAB, FramedStackedSlopeSlabBlock.itemModelSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER, FramedFlatSlopeSlabCornerGeometry::new, FramedFlatSlopeSlabCornerGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, FramedFlatInnerSlopeSlabCornerGeometry::new, FramedFlatInnerSlopeSlabCornerGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER, FramedFlatElevatedSlopeSlabCornerGeometry::new, FramedFlatElevatedSlopeSlabCornerGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER, FramedFlatElevatedInnerSlopeSlabCornerGeometry::new, FramedFlatElevatedInnerSlopeSlabCornerGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER, yHalfUp, FramedFlatDoubleSlopeSlabCornerBlock.itemModelSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER, FramedFlatInverseDoubleSlopeSlabCornerBlock.itemModelSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER, FramedFlatElevatedDoubleSlopeSlabCornerBlock.itemModelSource(), WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER, FramedFlatElevatedDoubleSlopeSlabCornerBlock.itemModelSourceInner(), WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER, FramedFlatStackedSlopeSlabCornerBlock.itemModelSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER, FramedFlatStackedSlopeSlabCornerBlock.itemModelSourceInner(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS, FramedVerticalHalfStairsGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_PANEL, FramedSlopePanelGeometry::new, FramedSlopePanelGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_SLOPE_PANEL, FramedExtendedSlopePanelGeometry::new, FramedExtendedSlopePanelGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL, FramedDoubleSlopePanelBlock.itemSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL, FramedInverseDoubleSlopePanelBlock.itemModelSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL, FramedExtendedDoubleSlopePanelBlock.itemSource(), WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_SLOPE_PANEL, FramedStackedSlopePanelBlock.itemModelSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER, FramedFlatSlopePanelCornerGeometry::new, FramedFlatSlopePanelCornerGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER, FramedFlatInnerSlopePanelCornerGeometry::new, FramedFlatInnerSlopePanelCornerGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER, FramedFlatExtendedSlopePanelCornerGeometry::new, FramedFlatExtendedSlopePanelCornerGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER, FramedFlatExtendedInnerSlopePanelCornerGeometry::new, FramedFlatExtendedInnerSlopePanelCornerGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER, yHalfUp, FramedFlatDoubleSlopePanelCornerBlock.itemSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER, yHalfUp, FramedFlatInverseDoubleSlopePanelCornerBlock.itemSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER, FramedFlatExtendedDoubleSlopePanelCornerBlock.itemModelSource(), WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER, FramedFlatExtendedDoubleSlopePanelCornerBlock.itemModelSourceInner(), WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER, FramedFlatStackedSlopePanelCornerBlock.itemModelSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER, FramedFlatStackedSlopePanelCornerBlock.itemModelSourceInner(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL, FramedSmallCornerSlopePanelGeometry::new, FramedSmallCornerSlopePanelGeometry.itemModelSource(), WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL_WALL, FramedSmallCornerSlopePanelWallGeometry::new, null, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL, FramedLargeCornerSlopePanelGeometry::new, FramedLargeCornerSlopePanelGeometry.itemModelSource(), WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL_WALL, FramedLargeCornerSlopePanelWallGeometry::new, null, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL, FramedSmallInnerCornerSlopePanelGeometry::new, FramedSmallInnerCornerSlopePanelGeometry.itemModelSource(), WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_WALL, FramedSmallInnerCornerSlopePanelWallGeometry::new, null, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL, FramedLargeInnerCornerSlopePanelGeometry::new, FramedLargeInnerCornerSlopePanelGeometry.itemModelSource(), WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_WALL, FramedLargeInnerCornerSlopePanelWallGeometry::new, null, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL, FramedExtendedCornerSlopePanelGeometry::new, FramedExtendedCornerSlopePanelGeometry.itemModelSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL_WALL, FramedExtendedCornerSlopePanelWallGeometry::new, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL, FramedExtendedInnerCornerSlopePanelGeometry::new, FramedExtendedInnerCornerSlopePanelGeometry.itemModelSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL_WALL, FramedExtendedInnerCornerSlopePanelWallGeometry::new, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL, FramedDoubleCornerSlopePanelBlock.itemModelSourceSmall(), WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_WALL, null, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL, FramedDoubleCornerSlopePanelBlock.itemModelSourceLarge(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_WALL, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL, FramedInverseDoubleCornerSlopePanelBlock.itemModelSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL_WALL, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL, FramedExtendedDoubleCornerSlopePanelBlock.itemModelSource(), WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL_WALL, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL, FramedExtendedDoubleCornerSlopePanelBlock.itemModelSourceInner(), WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL_WALL, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL, FramedStackedCornerSlopePanelBlock.itemModelSource(), WrapHelper.IGNORE_DEFAULT); // stacked corner
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL_WALL, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL, FramedStackedCornerSlopePanelBlock.itemModelSourceInner(), WrapHelper.IGNORE_DEFAULT); // stacked inner corner
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_WALL, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_STAIRS, FramedDoubleStairsBlock.itemSource(), WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS, FramedVerticalDoubleStairsBlock.itemModelSource(), WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_BOARD, FramedWallBoardGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GLOWING_CUBE, FramedGlowingCubeGeometry::new, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PYRAMID, FramedPyramidGeometry::new, FramedPyramidGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PYRAMID_SLAB, FramedPyramidSlabGeometry::new, FramedPyramidSlabGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_BUTTON, FramedLargeButtonGeometry::new, FramedLargeButtonGeometry::mergeStates);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_STONE_BUTTON, FramedLargeStoneButtonGeometry::new, FramedLargeButtonGeometry::mergeStates);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HORIZONTAL_PANE, FramedHorizontalPaneGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TARGET, FramedTargetGeometry::new, FramedTargetGeometry.itemSource(), WrapHelper.IGNORE_ALL);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GATE, FramedDoorGeometry::new, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_GATE, FramedIronDoorGeometry::new, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ITEM_FRAME, FramedItemFrameGeometry::normal, StateMerger.NO_OP);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME, FramedItemFrameGeometry::glowing, StateMerger.NO_OP);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_RAIL, FramedFancyRailGeometry::normal, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL, FramedFancyRailGeometry::straight, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL, FramedFancyRailGeometry::straight, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL, FramedFancyRailGeometry::straight, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE, FramedRailSlopeBlock.itemModelSourceFancy(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE, FramedPoweredRailSlopeBlock.itemModelSourceFancyPowered(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE, FramedDetectorRailSlopeBlock.itemModelSourceFancy(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE, FramedPoweredRailSlopeBlock.itemModelSourceFancyActivator(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_SLOPE, FramedHalfSlopeGeometry::new, FramedHalfSlopeGeometry.itemSource(), WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE, FramedVerticalHalfSlopeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_SLOPE, FramedDividedSlopeBlock.itemSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE, FramedDoubleHalfSlopeBlock.itemSource(), WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPED_STAIRS, FramedSlopedStairsGeometry::new, FramedSlopedStairsGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS, FramedVerticalSlopedStairsGeometry::new, FramedVerticalSlopedStairsGeometry.itemSource(), WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_MINI_CUBE, FramedMiniCubeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW, FramedOneWayWindowGeometry::new, List.of(FramedProperties.GLOWING));
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BOOKSHELF, FramedBookshelfGeometry::normal, FramedBookshelfGeometry.itemSourceNormal(), WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHISELED_BOOKSHELF, FramedBookshelfGeometry::chiseled, FramedBookshelfGeometry.itemSourceChiseled(), WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CENTERED_SLAB, FramedCenteredSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CENTERED_PANEL, FramedCenteredPanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
    }

    @SubscribeEvent
    public static void onModelRegister(final ModelEvent.RegisterAdditional event)
    {
        event.register(FluidModel.BARE_MODEL);
        event.register(FramedBlockModel.REINFORCEMENT_LOCATION);
        event.register(FramedMarkedCubeGeometry.SLIME_FRAME_LOCATION);
        event.register(FramedMarkedCubeGeometry.REDSTONE_FRAME_LOCATION);
        FramedMarkedPressurePlateGeometry.registerFrameModels(event);
        FramedStoneButtonGeometry.registerFrameModels(event);
        FramedLargeStoneButtonGeometry.registerFrameModels(event);
        event.register(FramedTargetGeometry.OVERLAY_LOCATION);

        if (SupplementariesCompat.isLoaded())
        {
            event.register(SupplementariesCompat.HANGING_MODEL_LOCATION);
        }

        ModelWrappingManager.reset();
    }

    @SubscribeEvent
    public static void onModifyBakingResult(final ModelEvent.ModifyBakingResult event)
    {
        StateCacheBuilder.ensureStateCachesInitialized();

        Map<ResourceLocation, BakedModel> registry = event.getModels();

        FramedChestRenderer.onModelsLoaded(registry); //Must happen before the chest model is replaced

        if (!ModernFixCompat.dynamicResourcesEnabled())
        {
            ModelWrappingManager.handleAll(registry);
        }
    }

    @SubscribeEvent
    public static void onModelsLoaded(final ModelEvent.BakingCompleted event)
    {
        ModelCache.clear(event.getModelBakery());
        FramedChestRenderer.onModelLoadingComplete();
        FramedBlockModel.captureReinforcementModel(event.getModels());
    }

    @SubscribeEvent
    public static void onRegisterReloadListener(final RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener((ResourceManagerReloadListener) BlockInteractOverlay::onResourceReload);
        AnimationSplitterSource.register();
        ModelWrappingManager.fireRegistration();
    }



    private static void wrapDoubleModel(
            RegistryObject<Block> block,
            @Nullable BlockState itemModelSource,
            @Nullable List<Property<?>> ignoredProps
    )
    {
        wrapDoubleModel(block, null, itemModelSource, ignoredProps);
    }

    private static void wrapDoubleModel(
            RegistryObject<Block> block,
            @Nullable Vec3 firstpersonTransform,
            @Nullable BlockState itemModelSource,
            @Nullable List<Property<?>> ignoredProps
    )
    {
        WrapHelper.wrapSpecial(
                block,
                ctx -> new FramedDoubleBlockModel(
                        ctx,
                        firstpersonTransform,
                        itemModelSource != null
                ),
                itemModelSource,
                WrapHelper.ignoreProps(ignoredProps)
        );
    }

    private static boolean useDefaultColorHandler(IFramedBlock block)
    {
        IBlockType type = block.getBlockType();
        return type != BlockType.FRAMED_TARGET;
    }



    private FBClient() { }
}
