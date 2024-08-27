package xfacthd.framedblocks.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.*;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.render.FramedBlockColor;
import xfacthd.framedblocks.api.block.render.FramedBlockRenderProperties;
import xfacthd.framedblocks.api.model.wrapping.*;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.ItemModelInfo;
import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import xfacthd.framedblocks.api.render.debug.AttachDebugRenderersEvent;
import xfacthd.framedblocks.client.data.extensions.block.NoEffectsClientBlockExtensions;
import xfacthd.framedblocks.client.data.extensions.block.OneWayWindowClientBlockExtensions;
import xfacthd.framedblocks.client.modelwrapping.StateLocationCache;
import xfacthd.framedblocks.client.render.block.debug.*;
import xfacthd.framedblocks.client.render.color.*;
import xfacthd.framedblocks.client.render.particle.FluidSpriteParticle;
import xfacthd.framedblocks.common.block.IFramedDoubleBlock;
import xfacthd.framedblocks.common.block.cube.FramedOneWayWindowBlock;
import xfacthd.framedblocks.common.block.interactive.FramedItemFrameBlock;
import xfacthd.framedblocks.common.block.slopepanel.*;
import xfacthd.framedblocks.common.block.slopeslab.*;
import xfacthd.framedblocks.common.data.camo.fluid.FluidCamoClientHandler;
import xfacthd.framedblocks.client.loader.fallback.FallbackLoader;
import xfacthd.framedblocks.client.model.*;
import xfacthd.framedblocks.client.model.slopeedge.*;
import xfacthd.framedblocks.client.modelwrapping.ModelWrappingManager;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;
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
import xfacthd.framedblocks.client.render.item.BlueprintPropertyOverride;
import xfacthd.framedblocks.client.render.special.*;
import xfacthd.framedblocks.client.render.util.AnimationSplitterSource;
import xfacthd.framedblocks.client.screen.*;
import xfacthd.framedblocks.client.screen.overlay.*;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.cube.FramedMiniCubeBlock;
import xfacthd.framedblocks.common.block.door.FramedDoorBlock;
import xfacthd.framedblocks.common.block.door.FramedFenceGateBlock;
import xfacthd.framedblocks.common.block.interactive.button.FramedButtonBlock;
import xfacthd.framedblocks.common.block.interactive.button.FramedLargeButtonBlock;
import xfacthd.framedblocks.common.block.interactive.pressureplate.FramedWeightedPressurePlateBlock;
import xfacthd.framedblocks.common.block.sign.AbstractFramedSignBlock;
import xfacthd.framedblocks.common.block.stairs.standard.FramedStairsBlock;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.StateCacheBuilder;
import xfacthd.framedblocks.common.data.doubleblock.FramedDoubleBlockRenderProperties;
import xfacthd.framedblocks.common.data.doubleblock.NullCullPredicate;

import java.util.*;
import java.util.function.Supplier;

@Mod(value = FramedConstants.MOD_ID, dist = Dist.CLIENT)
public final class FBClient
{
    public FBClient(IEventBus modBus, ModContainer container)
    {
        modBus.addListener(FBClient::onClientSetup);
        modBus.addListener(FBClient::onRegisterMenuScreens);
        modBus.addListener(FBClient::onImcMessageReceived);
        modBus.addListener(FBClient::onLoadComplete);
        modBus.addListener(FBClient::onRegisterKeyMappings);
        modBus.addListener(FBClient::onAttachDebugRenderers);
        modBus.addListener(FBClient::onRegisterRenderers);
        modBus.addListener(FBClient::onRegisterDebugRenderers);
        modBus.addListener(FBClient::onBlockColors);
        modBus.addListener(FBClient::onItemColors);
        modBus.addListener(FBClient::onOverlayRegister);
        modBus.addListener(FBClient::onGeometryLoaderRegister);
        modBus.addListener(FBClient::onRegisterModelWrappers);
        modBus.addListener(FBClient::onModelRegister);
        modBus.addListener(FBClient::onModifyBakingResult);
        modBus.addListener(FBClient::onModelsLoaded);
        modBus.addListener(FBClient::onRegisterReloadListener);
        modBus.addListener(FBClient::onRegisterSpriteSources);
        modBus.addListener(FBClient::onTexturesStitched);
        modBus.addListener(FBClient::onRegisterParticleProviders);
        modBus.addListener(FBClient::onRegisterClientExtensions);
        modBus.addListener(BlockOutlineRenderers::onRegisterOutlineRenderers);
        modBus.addListener(GhostRenderBehaviours::onRegisterGhostRenderBehaviours);

        NeoForge.EVENT_BUS.addListener(ClientTaskQueue::onClientTick);
        NeoForge.EVENT_BUS.addListener(BlockOutlineRenderer::onRenderBlockHighlight);
        NeoForge.EVENT_BUS.addListener(KeyMappings::onClientTick);
        NeoForge.EVENT_BUS.addListener(GhostBlockRenderer::onRenderLevelStage);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ClientEventHandler::onRecipesUpdated);
        NeoForge.EVENT_BUS.addListener(ClientEventHandler::onClientDisconnect);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, true, CollapsibleBlockIndicatorRenderer::onRenderBlockHighlight);

        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private static void onClientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(BlueprintPropertyOverride::register);
    }

    private static void onRegisterMenuScreens(final RegisterMenuScreensEvent event)
    {
        event.register(FBContent.MENU_TYPE_FRAMED_STORAGE.value(), FramedStorageScreen::new);
        event.register(FBContent.MENU_TYPE_FRAMING_SAW.value(), FramingSawScreen::create);
        event.register(FBContent.MENU_TYPE_POWERED_FRAMING_SAW.value(), PoweredFramingSawScreen::new);
    }

    private static void onImcMessageReceived(final InterModProcessEvent event)
    {
        event.getIMCStream()
                .filter(msg -> msg.method().equals(FramedConstants.IMC_METHOD_ADD_PROPERTY))
                .map(InterModComms.IMCMessage::messageSupplier)
                .map(Supplier::get)
                .filter(ModelProperty.class::isInstance)
                .map(ModelProperty.class::cast)
                .forEach(ConTexDataHandler::addConTexProperty);
    }

    private static void onLoadComplete(final FMLLoadCompleteEvent event)
    {
        ConTexDataHandler.lockRegistration();
    }

    private static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event)
    {
        event.register(KeyMappings.KEYMAPPING_UPDATE_CULLING.get());
        event.register(KeyMappings.KEYMAPPING_WIPE_CACHE.get());
    }

    private static void onAttachDebugRenderers(final AttachDebugRenderersEvent event)
    {
        FBContent.getBlockEntities().forEach(type -> event.attach(type.value(), ConnectionPredicateDebugRenderer.INSTANCE));
        FBContent.getBlockEntities().forEach(type -> event.attach(type.value(), QuadWindingDebugRenderer.INSTANCE));
        FBContent.getDoubleBlockEntities().forEach(type -> event.attach(type.value(), DoubleBlockPartDebugRenderer.INSTANCE));
    }

    private static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_SIGN.value(), FramedSignRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_HANGING_SIGN.value(), FramedHangingSignRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_CHEST.value(), FramedChestRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_ITEM_FRAME.value(), FramedItemFrameRenderer::new);
    }

    private static void onRegisterDebugRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        if (!FMLEnvironment.production)
        {
            BlockEntityRendererProvider<FramedBlockEntity> provider = FramedBlockDebugRenderer::new;
            FramedBlockDebugRenderer.getTargetTypes().forEach(type -> event.registerBlockEntityRenderer(type, provider));
        }
    }

    private static void onBlockColors(final RegisterColorHandlersEvent.Block event)
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

        event.register(FramedFlowerPotColor.INSTANCE, FBContent.BLOCK_FRAMED_FLOWER_POT.value());
        event.register(FramedTargetBlockColor.INSTANCE, FBContent.BLOCK_FRAMED_TARGET.value());
    }

    private static void onItemColors(final RegisterColorHandlersEvent.Item event)
    {
        //noinspection SuspiciousToArrayCall
        ItemLike[] blocks = FBContent.getRegisteredBlocks()
                .stream()
                .map(Holder::value)
                .filter(IFramedBlock.class::isInstance)
                .map(IFramedBlock.class::cast)
                .filter(FBClient::useDefaultColorHandler)
                .toArray(ItemLike[]::new);

        event.register(FramedBlockColor.INSTANCE, blocks);

        event.register(FramedTargetBlockColor.INSTANCE, FBContent.BLOCK_FRAMED_TARGET.value());
    }

    private static void onOverlayRegister(final RegisterGuiLayersEvent event)
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

    private static void onGeometryLoaderRegister(final ModelEvent.RegisterGeometryLoaders event)
    {
        event.register(OverlayLoader.ID, new OverlayLoader());
        event.register(FallbackLoader.ID, new FallbackLoader());
    }

    private static void onRegisterModelWrappers(final RegisterModelWrappersEvent event)
    {
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CUBE, FramedCubeGeometry::new, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE, FramedSlopeGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_SLOPE, FramedHalfSlopeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE, FramedVerticalHalfSlopeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_SLOPE, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_SLOPE, FramedCornerSlopeGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE, FramedInnerCornerSlopeGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_CORNER, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PRISM_CORNER, FramedPrismCornerGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER, FramedInnerPrismCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_THREEWAY_CORNER, FramedThreewayCornerGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER, FramedInnerThreewayCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_EDGE, FramedSlopeEdgeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_EDGE, FramedElevatedSlopeEdgeGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_SLOPE_EDGE, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_SLOPE_EDGE, FramedCornerSlopeEdgeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE_EDGE, FramedInnerCornerSlopeEdgeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_CORNER_SLOPE_EDGE, FramedElevatedCornerSlopeEdgeGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE, FramedElevatedInnerCornerSlopeEdgeGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLAB, FramedSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLAB, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_SLAB, NullCullPredicate.ALWAYS, AdjustableDoubleBlockItemModelInfo.STANDARD, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_SLAB, NullCullPredicate.ALWAYS, AdjustableDoubleBlockItemModelInfo.COPYCAT, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_SLAB, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLAB_EDGE, FramedSlabEdgeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLAB_CORNER, FramedSlabCornerGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PANEL, FramedPanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_PANEL, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_PANEL, NullCullPredicate.ALWAYS, AdjustableDoubleBlockItemModelInfo.STANDARD, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_PANEL, NullCullPredicate.ALWAYS, AdjustableDoubleBlockItemModelInfo.COPYCAT, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_HOR, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_PILLAR, FramedCornerPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_STAIRS, FramedStairsGeometry::new, new FramedStairsBlock.StairStateMerger());
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_STAIRS, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_STAIRS, FramedHalfStairsGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_STAIRS, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT_LOCK);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_HALF_STAIRS, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SLICED_STAIRS_SLAB, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT_LOCK);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SLICED_STAIRS_PANEL, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPED_STAIRS, FramedSlopedStairsGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS, FramedVerticalStairsGeometry::new, WrapHelper.IGNORE_DEFAULT_LOCK);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS, FramedVerticalHalfStairsGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT_LOCK);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_STAIRS, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_STAIRS, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS, FramedVerticalSlopedStairsGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_THREEWAY_CORNER_PILLAR, FramedThreewayCornerPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL, FramedWallGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FENCE, FramedFenceGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FENCE_GATE, FramedFenceGateGeometry::new, FramedFenceGateBlock.FenceGateStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_DOOR, FramedDoorGeometry::new, FramedDoorBlock.DoorStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_DOOR, FramedIronDoorGeometry::new, FramedDoorBlock.DoorStateMerger.INSTANCE);
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
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BUTTON, FramedButtonGeometry::new, FramedButtonBlock.STATE_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_STONE_BUTTON, FramedStoneButtonGeometry::create, FramedButtonBlock.STATE_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_BUTTON, FramedLargeButtonGeometry::new, FramedLargeButtonBlock.LARGE_STATE_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_STONE_BUTTON, FramedLargeStoneButtonGeometry::create, FramedLargeButtonBlock.LARGE_STATE_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LEVER, FramedLeverGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SIGN, FramedSignGeometry::new, AbstractFramedSignBlock.RotatingSignStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_SIGN, FramedWallSignGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HANGING_SIGN, FramedCeilingHangingSignGeometry::new, AbstractFramedSignBlock.RotatingSignStateMerger.INSTANCE);
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
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PANE, FramedPaneGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HORIZONTAL_PANE, FramedHorizontalPaneGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_RAIL_SLOPE, FramedRailSlopeGeometry::normal, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE, FramedRailSlopeGeometry::powered, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE, FramedRailSlopeGeometry::detector, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE, FramedRailSlopeGeometry::activator, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_RAIL, FramedFancyRailGeometry::normal, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL, FramedFancyRailGeometry::straight, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL, FramedFancyRailGeometry::straight, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL, FramedFancyRailGeometry::straight, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLOWER_POT, FramedFlowerPotGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PILLAR, FramedPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_PILLAR, FramedHalfPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_POST, FramedPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK, FramedCollapsibleBlockGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK, FramedCollapsibleCopycatBlockGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BOUNCY_CUBE, FramedMarkedCubeGeometry::slime, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_REDSTONE_BLOCK, FramedMarkedCubeGeometry::redstone, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PRISM, FramedPrismGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_INNER_PRISM, FramedElevatedInnerPrismGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_INNER_DOUBLE_PRISM, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPED_PRISM, FramedSlopedPrismGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_INNER_SLOPED_PRISM, FramedElevatedInnerSlopedPrismGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_SLAB, FramedSlopeSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB, FramedElevatedSlopeSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COMPOUND_SLOPE_SLAB, FramedCompoundSlopeSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB, FramedDoubleSlopeSlabBlock.NULL_CULL_PREDICATE, DoubleBlockItemModelInfo.Y_HALF_UP, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_SLOPE_SLAB, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER, FramedFlatSlopeSlabCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, FramedFlatInnerSlopeSlabCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER, FramedFlatElevatedSlopeSlabCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER, FramedFlatElevatedInnerSlopeSlabCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER, FramedFlatDoubleSlopeSlabCornerBlock.NULL_CULL_PREDICATE, DoubleBlockItemModelInfo.Y_HALF_UP, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_PANEL, FramedSlopePanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_SLOPE_PANEL, FramedExtendedSlopePanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COMPOUND_SLOPE_PANEL, FramedCompoundSlopePanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL, FramedDoubleSlopePanelBlock.NULL_CULL_PREDICATE, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_SLOPE_PANEL, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER, FramedFlatSlopePanelCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER, FramedFlatInnerSlopePanelCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER, FramedFlatExtendedSlopePanelCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER, FramedFlatExtendedInnerSlopePanelCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER, FramedDoubleSlopePanelBlock.NULL_CULL_PREDICATE, DoubleBlockItemModelInfo.Y_HALF_UP, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER, NullCullPredicate.NEVER, DoubleBlockItemModelInfo.Y_HALF_UP, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL, FramedSmallCornerSlopePanelGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL_WALL, FramedSmallCornerSlopePanelWallGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL, FramedLargeCornerSlopePanelGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL_WALL, FramedLargeCornerSlopePanelWallGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL, FramedSmallInnerCornerSlopePanelGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_WALL, FramedSmallInnerCornerSlopePanelWallGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL, FramedLargeInnerCornerSlopePanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_WALL, FramedLargeInnerCornerSlopePanelWallGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL, FramedExtendedCornerSlopePanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL_WALL, FramedExtendedCornerSlopePanelWallGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL, FramedExtendedInnerCornerSlopePanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL_WALL, FramedExtendedInnerCornerSlopePanelWallGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL, NullCullPredicate.ONLY_RIGHT, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_WALL, NullCullPredicate.ONLY_RIGHT, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_WALL, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL_WALL, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL_WALL, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL_WALL, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL_WALL, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_WALL, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GLOWING_CUBE, FramedGlowingCubeGeometry::new, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PYRAMID, FramedPyramidGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PYRAMID_SLAB, FramedPyramidSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TARGET, FramedTargetGeometry::new, StateMerger.IGNORE_ALL);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GATE, FramedDoorGeometry::new, FramedDoorBlock.DoorStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_GATE, FramedIronDoorGeometry::new, FramedDoorBlock.DoorStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ITEM_FRAME, FramedItemFrameGeometry::normal, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME, FramedItemFrameGeometry::glowing, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_MINI_CUBE, FramedMiniCubeGeometry::new, FramedMiniCubeBlock.MiniCubeStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW, FramedOneWayWindowGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BOOKSHELF, FramedBookshelfGeometry::normal, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHISELED_BOOKSHELF, FramedBookshelfGeometry::chiseled, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CENTERED_SLAB, FramedCenteredSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CENTERED_PANEL, FramedCenteredPanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_MASONRY_CORNER_SEGMENT, FramedMasonryCornerSegmentGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_MASONRY_CORNER, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHECKERED_CUBE_SEGMENT, FramedCheckeredCubeSegmentGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_CHECKERED_CUBE, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHECKERED_SLAB_SEGMENT, FramedCheckeredSlabSegmentGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_CHECKERED_SLAB, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHECKERED_PANEL_SEGMENT, FramedCheckeredPanelSegmentGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_CHECKERED_PANEL, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TUBE, FramedTubeGeometry::new, WrapHelper.IGNORE_DEFAULT);
    }

    private static void onModelRegister(final ModelEvent.RegisterAdditional event)
    {
        event.register(FluidModel.BARE_MODEL);
        event.register(ReinforcementModel.LOCATION);
        event.register(FramedMarkedCubeGeometry.SLIME_FRAME_LOCATION);
        event.register(FramedMarkedCubeGeometry.REDSTONE_FRAME_LOCATION);
        event.register(FramedTargetGeometry.OVERLAY_LOCATION);
        event.register(FramedCollapsibleBlockGeometry.ALT_BASE_MODEL_LOC);
        event.register(FramedCollapsibleCopycatBlockGeometry.ALT_BASE_MODEL_LOC);

        if (SupplementariesCompat.isLoaded())
        {
            event.register(SupplementariesCompat.Client.HANGING_MODEL_LOCATION);
        }

        ModelWrappingManager.reset();
    }

    private static void onModifyBakingResult(final ModelEvent.ModifyBakingResult event)
    {
        StateCacheBuilder.ensureStateCachesInitialized();

        Map<ModelResourceLocation, BakedModel> registry = event.getModels();
        TextureLookup textureLookup = TextureLookup.bindBlockAtlas(event.getTextureGetter());

        ModelWrappingManager.handleAll(registry, textureLookup);
    }

    private static void onModelsLoaded(final ModelEvent.BakingCompleted event)
    {
        StateLocationCache.clear();
        FluidCamoClientHandler.clearModelCache();
        FramedChestRenderer.onModelsLoaded(event.getModels());
        ReinforcementModel.reload(event.getModels());
    }

    private static void onRegisterReloadListener(final RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener((ResourceManagerReloadListener) BlockInteractOverlay::onResourceReload);
        event.registerReloadListener((ResourceManagerReloadListener) OverlayQuadGenerator::onResourceReload);

        ModelWrappingManager.fireRegistration();
        FramedBlockDebugRenderer.init();
        BlockOutlineRenderer.init();
        GhostBlockRenderer.init();
    }

    private static void onRegisterSpriteSources(final RegisterSpriteSourceTypesEvent event)
    {
        AnimationSplitterSource.register(event::register);
    }

    private static void onTexturesStitched(final TextureAtlasStitchedEvent event)
    {
        //noinspection deprecation
        if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS))
        {
            ConnectionPredicateDebugRenderer.captureDummySprite(event.getAtlas());
        }
    }

    private static void onRegisterParticleProviders(final RegisterParticleProvidersEvent event)
    {
        event.registerSpecial(FBContent.FLUID_PARTICLE.get(), new FluidSpriteParticle.Provider());
    }

    private static void onRegisterClientExtensions(final RegisterClientExtensionsEvent event)
    {
        FBContent.getRegisteredBlocks()
                .stream()
                .map(Holder::value)
                .filter(IFramedBlock.class::isInstance)
                .map(block -> Pair.of(block, switch (block)
                {
                    case FramedItemFrameBlock ignored -> NoEffectsClientBlockExtensions.INSTANCE;
                    case FramedOneWayWindowBlock ignored -> new OneWayWindowClientBlockExtensions();
                    case IFramedDoubleBlock ignored -> FramedDoubleBlockRenderProperties.INSTANCE;
                    default -> FramedBlockRenderProperties.INSTANCE;
                }))
                .forEach(pair -> event.registerBlock(pair.getSecond(), pair.getFirst()));
    }



    private static void wrapDoubleModel(
            Holder<Block> block,
            NullCullPredicate nullCullPredicate,
            @Nullable Set<Property<?>> ignoredProps
    )
    {
        wrapDoubleModel(block, nullCullPredicate, DoubleBlockItemModelInfo.INSTANCE, ignoredProps);
    }

    private static void wrapDoubleModel(
            Holder<Block> block,
            NullCullPredicate nullCullPredicate,
            ItemModelInfo itemModelInfo,
            @Nullable Set<Property<?>> ignoredProps
    )
    {
        WrapHelper.wrapSpecial(
                block,
                ctx -> new FramedDoubleBlockModel(ctx, nullCullPredicate, itemModelInfo),
                StateMerger.ignoring(ignoredProps)
        );
    }

    private static boolean useDefaultColorHandler(IFramedBlock block)
    {
        IBlockType type = block.getBlockType();
        return type != BlockType.FRAMED_FLOWER_POT && type != BlockType.FRAMED_TARGET;
    }
}
