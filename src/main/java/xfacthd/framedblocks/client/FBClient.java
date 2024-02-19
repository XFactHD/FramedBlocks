package xfacthd.framedblocks.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.Block;
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
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.model.wrapping.*;
import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import xfacthd.framedblocks.client.model.*;
import xfacthd.framedblocks.client.model.slopeedge.*;
import xfacthd.framedblocks.client.modelwrapping.ModelWrappingManager;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.model.util.ModelCache;
import xfacthd.framedblocks.client.data.*;
import xfacthd.framedblocks.client.loader.overlay.OverlayLoader;
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
import xfacthd.framedblocks.client.overlaygen.OverlayQuadGenerator;
import xfacthd.framedblocks.client.render.block.*;
import xfacthd.framedblocks.client.render.color.FramedBlockColor;
import xfacthd.framedblocks.client.render.color.FramedTargetBlockColor;
import xfacthd.framedblocks.client.render.item.BlueprintPropertyOverride;
import xfacthd.framedblocks.client.render.special.*;
import xfacthd.framedblocks.client.render.util.AnimationSplitterSource;
import xfacthd.framedblocks.client.screen.*;
import xfacthd.framedblocks.client.screen.overlay.*;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.interactive.*;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.compat.modernfix.ModernFixCompat;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.StateCacheBuilder;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FBClient
{
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(BlueprintPropertyOverride::register);

        BlockOutlineRenderers.register();
        GhostBlockRenderer.init();
        GhostRenderBehaviours.register();

        NeoForge.EVENT_BUS.addListener(ClientTaskQueue::onClientTick);
        NeoForge.EVENT_BUS.addListener(BlockOutlineRenderer::onRenderBlockHighlight);
        NeoForge.EVENT_BUS.addListener(KeyMappings::onClientTick);
        NeoForge.EVENT_BUS.addListener(GhostBlockRenderer::onRenderLevelStage);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ClientEventHandler::onRecipesUpdated);
        NeoForge.EVENT_BUS.addListener(ClientEventHandler::onClientDisconnect);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, true, CollapsibleBlockIndicatorRenderer::onRenderBlockHighlight);
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(final RegisterMenuScreensEvent event)
    {
        event.register(FBContent.MENU_TYPE_FRAMED_STORAGE.value(), FramedStorageScreen::new);
        event.register(FBContent.MENU_TYPE_FRAMING_SAW.value(), FramingSawScreen::new);
        event.register(FBContent.MENU_TYPE_POWERED_FRAMING_SAW.value(), PoweredFramingSawScreen::new);
    }

    @SubscribeEvent
    public static void onImcMessageReceived(final InterModProcessEvent event)
    {
        event.getIMCStream()
                .filter(msg -> msg.method().equals(FramedConstants.IMC_METHOD_ADD_PROPERTY))
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
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_SIGN.value(), FramedSignRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_HANGING_SIGN.value(), FramedHangingSignRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_CHEST.value(), FramedChestRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_ITEM_FRAME.value(), FramedItemFrameRenderer::new);

        if (!FMLEnvironment.production && TestProperties.ENABLE_DOUBLE_BLOCK_PART_HIT_DEBUG_RENDERER)
        {
            BlockEntityRendererProvider<FramedDoubleBlockEntity> provider = FramedDoubleBlockDebugRenderer::new;
            FBContent.getDoubleBlockEntities().forEach(type -> event.registerBlockEntityRenderer(type.value(), provider));
        }
        if (!FMLEnvironment.production && TestProperties.ENABLE_CONNECTION_DEBUG_RENDERER)
        {
            BlockEntityRendererProvider<FramedBlockEntity> provider = FramedBlockConnectionDebugRenderer::new;
            FBContent.getBlockEntities().forEach(type -> event.registerBlockEntityRenderer(type.value(), provider));
        }
    }

    @SubscribeEvent
    public static void onBlockColors(final RegisterColorHandlersEvent.Block event)
    {
        //noinspection SuspiciousToArrayCall
        Block[] blocks = FBContent.getRegisteredBlocks()
                .stream()
                .map(Holder::value)
                .filter(IFramedBlock.class::isInstance)
                .map(IFramedBlock.class::cast)
                .filter(FBClient::useDefaultColorHandler)
                .toArray(Block[]::new);

        event.register(FramedBlockColor.INSTANCE, blocks);

        event.register(FramedTargetBlockColor.INSTANCE, FBContent.BLOCK_FRAMED_TARGET.value());
    }

    @SubscribeEvent
    public static void onItemColors(final RegisterColorHandlersEvent.Item event)
    {
        event.register(FramedTargetBlockColor.INSTANCE, FBContent.BLOCK_FRAMED_TARGET.value());
    }

    @SubscribeEvent
    public static void onOverlayRegister(final RegisterGuiOverlaysEvent event)
    {
        event.registerAboveAll(Utils.rl("state_lock"), new StateLockOverlay());
        event.registerAboveAll(Utils.rl("toggle_waterloggable"), new ToggleWaterloggableOverlay());
        event.registerAboveAll(Utils.rl("y_slope"), new ToggleYSlopeOverlay());
        event.registerAboveAll(Utils.rl("reinforced"), new ReinforcementOverlay());
        event.registerAboveAll(Utils.rl("prism_offset"), new PrismOffsetOverlay());
        event.registerAboveAll(Utils.rl("split_line"), new SplitLineOverlay());
        event.registerAboveAll(Utils.rl("one_way_window"), new OneWayWindowOverlay());
        event.registerAboveAll(Utils.rl("frame_background"), new FrameBackgroundOverlay());
        event.registerAboveAll(Utils.rl("camo_rotation"), new CamoRotationOverlay());
    }

    @SubscribeEvent
    public static void onGeometryLoaderRegister(final ModelEvent.RegisterGeometryLoaders event)
    {
        event.register(OverlayLoader.ID, new OverlayLoader());
    }

    @SubscribeEvent
    public static void onRegisterModelWrappers(final RegisterModelWrappersEvent event)
    {
        Vec3 yHalfUp = new Vec3(0, .5, 0);

        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CUBE, FramedCubeGeometry::new, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE, FramedSlopeGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE, null, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_SLOPE, FramedHalfSlopeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE, FramedVerticalHalfSlopeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_SLOPE, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_SLOPE, FramedCornerSlopeGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE, FramedInnerCornerSlopeGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_CORNER, null, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PRISM_CORNER, FramedPrismCornerGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER, FramedInnerPrismCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER, null, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_THREEWAY_CORNER, FramedThreewayCornerGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER, FramedInnerThreewayCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER, null, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_EDGE, FramedSlopeEdgeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_EDGE, FramedElevatedSlopeEdgeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_SLOPE_EDGE, null, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLAB, FramedSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLAB, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_SLAB, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLAB_EDGE, FramedSlabEdgeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLAB_CORNER, FramedSlabCornerGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PANEL, FramedPanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_PANEL, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_HOR, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_PILLAR, FramedCornerPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_STAIRS, FramedStairsGeometry::new, WrapHelper.IGNORE_DEFAULT_LOCK);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_STAIRS, null, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_STAIRS, FramedHalfStairsGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_STAIRS, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_HALF_STAIRS, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SLICED_STAIRS_SLAB, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SLICED_STAIRS_PANEL, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPED_STAIRS, FramedSlopedStairsGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS, FramedVerticalStairsGeometry::new, WrapHelper.IGNORE_DEFAULT_LOCK);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS, null, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS, FramedVerticalHalfStairsGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_STAIRS, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_STAIRS, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS, FramedVerticalSlopedStairsGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_THREEWAY_CORNER_PILLAR, FramedThreewayCornerPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR, null, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL, FramedWallGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FENCE, FramedFenceGeometry::createFenceGeometry, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FENCE_GATE, FramedFenceGateGeometry::new, Utils.concat(Set.of(BlockStateProperties.POWERED), WrapHelper.IGNORE_ALWAYS));
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_DOOR, FramedDoorGeometry::new, Utils.concat(Set.of(BlockStateProperties.POWERED), WrapHelper.IGNORE_SOLID));
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_DOOR, FramedIronDoorGeometry::new, Utils.concat(Set.of(BlockStateProperties.POWERED), WrapHelper.IGNORE_SOLID));
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TRAP_DOOR, FramedTrapDoorGeometry::new, Utils.concat(Set.of(BlockStateProperties.POWERED), WrapHelper.IGNORE_DEFAULT));
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR, FramedIronTrapDoorGeometry::new, Utils.concat(Set.of(BlockStateProperties.POWERED), WrapHelper.IGNORE_DEFAULT));
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PRESSURE_PLATE, FramedPressurePlateGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_PRESSURE_PLATE, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE, FramedMarkedPressurePlateGeometry::stone, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE, FramedMarkedPressurePlateGeometry::obsidian, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE, FramedMarkedPressurePlateGeometry::gold, FramedWeightedPressurePlateBlock.STATE_MERGER);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE, FramedWeightedPressurePlateBlock.STATE_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE, FramedMarkedPressurePlateGeometry::iron, FramedWeightedPressurePlateBlock.STATE_MERGER);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE, FramedWeightedPressurePlateBlock.STATE_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LADDER, FramedLadderGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BUTTON, FramedButtonGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_STONE_BUTTON, FramedStoneButtonGeometry::create, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_BUTTON, FramedLargeButtonGeometry::new, FramedLargeButtonBlock.STATE_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_STONE_BUTTON, FramedLargeStoneButtonGeometry::create, FramedLargeButtonBlock.STATE_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LEVER, FramedLeverGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SIGN, FramedSignGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_SIGN, FramedWallSignGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HANGING_SIGN, FramedCeilingHangingSignGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_HANGING_SIGN, FramedWallHangingSignGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TORCH, FramedTorchGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_TORCH, FramedWallTorchGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SOUL_TORCH, FramedSoulTorchGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SOUL_WALL_TORCH, FramedSoulWallTorchGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_REDSTONE_TORCH, FramedRedstoneTorchGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_REDSTONE_WALL_TORCH, FramedRedstoneWallTorchGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLOOR, FramedFloorBoardGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_BOARD, FramedWallBoardGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_STRIP, FramedCornerStripGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LATTICE, FramedLatticeGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_THICK_LATTICE, FramedLatticeGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHEST, FramedChestGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SECRET_STORAGE, FramedCubeGeometry::new, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BARS, FramedBarsGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PANE, FramedPaneGeometry::createPaneGeometry, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HORIZONTAL_PANE, FramedHorizontalPaneGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_RAIL_SLOPE, FramedRailSlopeGeometry::normal, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE, FramedRailSlopeGeometry::powered, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE, FramedRailSlopeGeometry::detector, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE, FramedRailSlopeGeometry::activator, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_RAIL, FramedFancyRailGeometry::normal, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL, FramedFancyRailGeometry::straight, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL, FramedFancyRailGeometry::straight, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL, FramedFancyRailGeometry::straight, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLOWER_POT, FramedFlowerPotGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PILLAR, FramedPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_PILLAR, FramedHalfPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_POST, FramedPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK, FramedCollapsibleBlockGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK, FramedCollapsibleCopycatBlockGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BOUNCY_CUBE, FramedMarkedCubeGeometry::slime, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_REDSTONE_BLOCK, FramedMarkedCubeGeometry::redstone, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PRISM, FramedPrismGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_PRISM, FramedInnerPrismGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_PRISM, null, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPED_PRISM, FramedSlopedPrismGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_SLOPED_PRISM, FramedInnerSlopedPrismGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPED_PRISM, null, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_SLAB, FramedSlopeSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB, FramedElevatedSlopeSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COMPOUND_SLOPE_SLAB, FramedCompoundSlopeSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB, yHalfUp, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_SLOPE_SLAB, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER, FramedFlatSlopeSlabCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, FramedFlatInnerSlopeSlabCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER, FramedFlatElevatedSlopeSlabCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER, FramedFlatElevatedInnerSlopeSlabCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER, yHalfUp, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_PANEL, FramedSlopePanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_SLOPE_PANEL, FramedExtendedSlopePanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COMPOUND_SLOPE_PANEL, FramedCompoundSlopePanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_SLOPE_PANEL, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER, FramedFlatSlopePanelCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER, FramedFlatInnerSlopePanelCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER, FramedFlatExtendedSlopePanelCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER, FramedFlatExtendedInnerSlopePanelCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER, yHalfUp, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER, yHalfUp, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL, FramedSmallCornerSlopePanelGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL_WALL, FramedSmallCornerSlopePanelWallGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL, FramedLargeCornerSlopePanelGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL_WALL, FramedLargeCornerSlopePanelWallGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL, FramedSmallInnerCornerSlopePanelGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_WALL, FramedSmallInnerCornerSlopePanelWallGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL, FramedLargeInnerCornerSlopePanelGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_WALL, FramedLargeInnerCornerSlopePanelWallGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL, FramedExtendedCornerSlopePanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL_WALL, FramedExtendedCornerSlopePanelWallGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL, FramedExtendedInnerCornerSlopePanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL_WALL, FramedExtendedInnerCornerSlopePanelWallGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL, null, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_WALL, null, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_WALL, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL_WALL, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL_WALL, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL_WALL, null, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL_WALL, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL, null, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_WALL, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GLOWING_CUBE, FramedGlowingCubeGeometry::new, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PYRAMID, FramedPyramidGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PYRAMID_SLAB, FramedPyramidSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TARGET, FramedTargetGeometry::new, StateMerger.IGNORE_ALL);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GATE, FramedDoorGeometry::new, Utils.concat(Set.of(BlockStateProperties.POWERED), WrapHelper.IGNORE_SOLID));
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_GATE, FramedIronDoorGeometry::new, Utils.concat(Set.of(BlockStateProperties.POWERED), WrapHelper.IGNORE_SOLID));
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ITEM_FRAME, FramedItemFrameGeometry::normal, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME, FramedItemFrameGeometry::glowing, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_MINI_CUBE, FramedMiniCubeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW, FramedOneWayWindowGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BOOKSHELF, FramedBookshelfGeometry::normal, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHISELED_BOOKSHELF, FramedBookshelfGeometry::chiseled, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CENTERED_SLAB, FramedCenteredSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CENTERED_PANEL, FramedCenteredPanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_MASONRY_CORNER_SEGMENT, FramedMasonryCornerSegmentGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_MASONRY_CORNER, null, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHECKERED_CUBE_SEGMENT, FramedCheckeredCubeSegmentGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_CHECKERED_CUBE, null, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHECKERED_SLAB_SEGMENT, FramedCheckeredSlabSegmentGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_CHECKERED_SLAB, null, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHECKERED_PANEL_SEGMENT, FramedCheckeredPanelSegmentGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_CHECKERED_PANEL, null, WrapHelper.IGNORE_DEFAULT);
    }

    @SubscribeEvent
    public static void onModelRegister(final ModelEvent.RegisterAdditional event)
    {
        event.register(FluidModel.BARE_MODEL);
        event.register(ReinforcementModel.LOCATION);
        event.register(FramedMarkedCubeGeometry.SLIME_FRAME_LOCATION);
        event.register(FramedMarkedCubeGeometry.REDSTONE_FRAME_LOCATION);
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
        TextureLookup textureLookup = TextureLookup.bindBlockAtlas(event.getTextureGetter());

        if (!ModernFixCompat.dynamicResourcesEnabled())
        {
            ModelWrappingManager.handleAll(registry, textureLookup);
        }
    }

    @SubscribeEvent
    public static void onModelsLoaded(final ModelEvent.BakingCompleted event)
    {
        ModelCache.clear();
        FramedChestRenderer.onModelsLoaded(event.getModels());
        ReinforcementModel.reload(event.getModels());
    }

    @SubscribeEvent
    public static void onRegisterReloadListener(final RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener((ResourceManagerReloadListener) BlockInteractOverlay::onResourceReload);
        event.registerReloadListener((ResourceManagerReloadListener) OverlayQuadGenerator::onResourceReload);
        ModelWrappingManager.fireRegistration();
    }

    @SubscribeEvent
    public static void onRegisterSpriteSources(final RegisterSpriteSourceTypesEvent event)
    {
        AnimationSplitterSource.register(event::register);
    }

    @SubscribeEvent
    public static void onTexturesStitched(final TextureAtlasStitchedEvent event)
    {
        //noinspection deprecation
        if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS))
        {
            FramedBlockConnectionDebugRenderer.captureDummySprite(event.getAtlas());
        }
    }



    private static void wrapDoubleModel(
            Holder<Block> block,
            @Nullable Vec3 firstpersonTransform,
            @Nullable Set<Property<?>> ignoredProps
    )
    {
        WrapHelper.wrapSpecial(
                block,
                ctx -> new FramedDoubleBlockModel(
                        ctx,
                        firstpersonTransform
                ),
                StateMerger.ignoring(ignoredProps)
        );
    }

    private static boolean useDefaultColorHandler(IFramedBlock block)
    {
        IBlockType type = block.getBlockType();
        return type != BlockType.FRAMED_TARGET;
    }



    private FBClient() { }
}
