package io.github.xfacthd.framedblocks.client;

import com.mojang.datafixers.util.Pair;
import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.render.FramedBlockColor;
import io.github.xfacthd.framedblocks.api.block.render.FramedClientBlockExtensions;
import io.github.xfacthd.framedblocks.api.block.render.FramedClientDoubleBlockExtensions;
import io.github.xfacthd.framedblocks.api.block.render.NullCullPredicate;
import io.github.xfacthd.framedblocks.api.model.item.DoubleBlockItemModelInfo;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelInfo;
import io.github.xfacthd.framedblocks.api.model.item.block.BlockItemModelProvider;
import io.github.xfacthd.framedblocks.api.model.item.block.RegisterBlockItemModelProvidersEvent;
import io.github.xfacthd.framedblocks.api.model.item.tint.FramedBlockItemTintProvider;
import io.github.xfacthd.framedblocks.api.model.item.tint.RegisterItemTintProvidersEvent;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.wrapping.RegisterModelWrappersEvent;
import io.github.xfacthd.framedblocks.api.model.wrapping.WrapHelper;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.render.debug.AttachDebugRenderersEvent;
import io.github.xfacthd.framedblocks.api.screen.overlay.RegisterBlockInteractOverlaysEvent;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.data.BlockOutlineRenderers;
import io.github.xfacthd.framedblocks.client.data.GhostRenderBehaviours;
import io.github.xfacthd.framedblocks.client.data.extensions.block.*;
import io.github.xfacthd.framedblocks.client.model.FluidModel;
import io.github.xfacthd.framedblocks.client.model.baked.FramedBlockModel;
import io.github.xfacthd.framedblocks.client.model.geometry.cube.*;
import io.github.xfacthd.framedblocks.client.model.geometry.door.*;
import io.github.xfacthd.framedblocks.client.model.geometry.interactive.*;
import io.github.xfacthd.framedblocks.client.model.geometry.pane.*;
import io.github.xfacthd.framedblocks.client.model.geometry.pillar.*;
import io.github.xfacthd.framedblocks.client.model.geometry.prism.*;
import io.github.xfacthd.framedblocks.client.model.geometry.rail.*;
import io.github.xfacthd.framedblocks.client.model.geometry.slab.*;
import io.github.xfacthd.framedblocks.client.model.geometry.slope.*;
import io.github.xfacthd.framedblocks.client.model.geometry.slopeedge.*;
import io.github.xfacthd.framedblocks.client.model.geometry.slopepanel.*;
import io.github.xfacthd.framedblocks.client.model.geometry.slopepanelcorner.*;
import io.github.xfacthd.framedblocks.client.model.geometry.slopeslab.*;
import io.github.xfacthd.framedblocks.client.model.geometry.stairs.*;
import io.github.xfacthd.framedblocks.client.model.geometry.torch.*;
import io.github.xfacthd.framedblocks.client.model.item.BlockItemModelProviders;
import io.github.xfacthd.framedblocks.client.model.item.DynamicItemTintProviders;
import io.github.xfacthd.framedblocks.client.model.item.FramedBlockItemModel;
import io.github.xfacthd.framedblocks.client.model.item.TankItemModel;
import io.github.xfacthd.framedblocks.client.model.item.modelprovider.FenceBlockItemModelProvider;
import io.github.xfacthd.framedblocks.client.model.item.property.BlueprintProperty;
import io.github.xfacthd.framedblocks.client.model.item.tintprovider.FramedTargetItemTintProvider;
import io.github.xfacthd.framedblocks.client.model.loader.fallback.FallbackLoader;
import io.github.xfacthd.framedblocks.client.model.special.FramedChestLidModel;
import io.github.xfacthd.framedblocks.client.model.unbaked.FramedBlockModelDefinition;
import io.github.xfacthd.framedblocks.client.model.wrapping.ModelWrappingManager;
import io.github.xfacthd.framedblocks.client.net.ClientNetworkHandler;
import io.github.xfacthd.framedblocks.client.render.block.FramedChestRenderer;
import io.github.xfacthd.framedblocks.client.render.block.FramedHangingSignRenderer;
import io.github.xfacthd.framedblocks.client.render.block.FramedItemFrameRenderer;
import io.github.xfacthd.framedblocks.client.render.block.FramedSignRenderer;
import io.github.xfacthd.framedblocks.client.render.block.FramedTankRenderer;
import io.github.xfacthd.framedblocks.client.render.color.FramedFlowerPotColor;
import io.github.xfacthd.framedblocks.client.render.color.FramedTargetBlockColor;
import io.github.xfacthd.framedblocks.client.render.debug.FramedBlockDebugRenderer;
import io.github.xfacthd.framedblocks.client.render.debug.impl.CollapsibleBlockDebugRenderer;
import io.github.xfacthd.framedblocks.client.render.debug.impl.ConnectionPredicateDebugRenderer;
import io.github.xfacthd.framedblocks.client.render.debug.impl.DoubleBlockPartDebugRenderer;
import io.github.xfacthd.framedblocks.client.render.debug.impl.QuadWindingDebugRenderer;
import io.github.xfacthd.framedblocks.client.render.item.TankItemRenderer;
import io.github.xfacthd.framedblocks.client.render.particle.BlockOverlayParticle;
import io.github.xfacthd.framedblocks.client.render.particle.FluidSpriteParticle;
import io.github.xfacthd.framedblocks.client.render.special.BlockOutlineRenderer;
import io.github.xfacthd.framedblocks.client.render.special.CollapsibleBlockIndicatorRenderer;
import io.github.xfacthd.framedblocks.client.render.special.GhostBlockRenderer;
import io.github.xfacthd.framedblocks.client.render.util.AnimationSplitterSource;
import io.github.xfacthd.framedblocks.client.render.util.AreaMaskSource;
import io.github.xfacthd.framedblocks.client.render.util.FramedRenderPipelines;
import io.github.xfacthd.framedblocks.client.screen.FramedStorageScreen;
import io.github.xfacthd.framedblocks.client.screen.FramingSawScreen;
import io.github.xfacthd.framedblocks.client.screen.PoweredFramingSawScreen;
import io.github.xfacthd.framedblocks.client.screen.overlay.BlockInteractOverlayLayer;
import io.github.xfacthd.framedblocks.client.screen.overlay.impl.*;
import io.github.xfacthd.framedblocks.client.screen.pip.BlockPictureInPictureRenderer;
import io.github.xfacthd.framedblocks.client.screen.pip.SignBlockPictureInPictureRenderer;
import io.github.xfacthd.framedblocks.client.screen.pip.SpinningItemPictureInPictureRenderer;
import io.github.xfacthd.framedblocks.client.screen.widget.BlockPreviewTooltipComponent;
import io.github.xfacthd.framedblocks.client.util.CacheCleaner;
import io.github.xfacthd.framedblocks.client.util.ClientEventHandler;
import io.github.xfacthd.framedblocks.client.util.ClientTaskQueue;
import io.github.xfacthd.framedblocks.client.util.KeyMappings;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.cube.FramedMiniCubeBlock;
import io.github.xfacthd.framedblocks.common.block.cube.FramedOneWayWindowBlock;
import io.github.xfacthd.framedblocks.common.block.door.FramedDoorBlock;
import io.github.xfacthd.framedblocks.common.block.door.FramedFenceGateBlock;
import io.github.xfacthd.framedblocks.common.block.interactive.FramedItemFrameBlock;
import io.github.xfacthd.framedblocks.common.block.interactive.button.FramedButtonBlock;
import io.github.xfacthd.framedblocks.common.block.interactive.button.FramedLargeButtonBlock;
import io.github.xfacthd.framedblocks.common.block.interactive.pressureplate.FramedWeightedPressurePlateBlock;
import io.github.xfacthd.framedblocks.common.block.sign.FramedStandingSignBlock;
import io.github.xfacthd.framedblocks.common.block.slopepanel.FramedDoubleSlopePanelBlock;
import io.github.xfacthd.framedblocks.common.block.slopeslab.FramedDoubleSlopeSlabBlock;
import io.github.xfacthd.framedblocks.common.block.slopeslab.FramedFlatDoubleSlopeSlabCornerBlock;
import io.github.xfacthd.framedblocks.common.block.stairs.standard.FramedStairsBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.Holder;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Set;
import java.util.function.Function;

@Mod(value = FramedConstants.MOD_ID, dist = Dist.CLIENT)
public final class FBClient
{
    public FBClient(IEventBus modBus, ModContainer container)
    {
        modBus.addListener(FBClient::onRegisterConditionalItemModelProperties);
        modBus.addListener(FBClient::onRegisterItemModels);
        modBus.addListener(FBClient::onRegisterSpecialModelRenderers);
        modBus.addListener(FBClient::onRegisterMenuScreens);
        modBus.addListener(FBClient::onAttachDebugRenderers);
        modBus.addListener(FBClient::onRegisterRenderers);
        modBus.addListener(FBClient::onRegisterBlockColors);
        modBus.addListener(FBClient::onRegisterBlockItemModelProviders);
        modBus.addListener(FBClient::onRegisterItemTintProviders);
        modBus.addListener(FBClient::onRegisterGuiLayers);
        modBus.addListener(FBClient::onRegisterBlockInteractOverlays);
        modBus.addListener(FBClient::onGeometryLoaderRegister);
        modBus.addListener(FBClient::onRegisterModelWrappers);
        modBus.addListener(FBClient::onBlockStateModelRegister);
        modBus.addListener(FBClient::onRegisterStandaloneModels);
        modBus.addListener(FBClient::onModelsLoaded);
        modBus.addListener(FBClient::onRegisterReloadListener);
        modBus.addListener(FBClient::onInitClientRegistries);
        modBus.addListener(FBClient::onRegisterSpriteSources);
        modBus.addListener(FBClient::onRegisterParticleProviders);
        modBus.addListener(FBClient::onRegisterClientExtensions);
        modBus.addListener(FBClient::onRegisterClientTooltipComponentFactories);
        modBus.addListener(FBClient::onRegisterPictureInPictureRenderers);
        modBus.addListener(KeyMappings::onRegisterKeyMappings);
        modBus.addListener(BlockOutlineRenderers::onRegisterOutlineRenderers);
        modBus.addListener(GhostRenderBehaviours::onRegisterGhostRenderBehaviours);
        modBus.addListener(FramedRenderPipelines::onRegisterRenderPipelines);
        modBus.addListener(ClientNetworkHandler::onRegisterPayloadHandlers);
        modBus.addListener(ModelWrappingManager::onRegisterStandaloneModels);

        NeoForge.EVENT_BUS.addListener(ClientTaskQueue::onClientTick);
        NeoForge.EVENT_BUS.addListener(BlockOutlineRenderer::onRenderBlockHighlight);
        NeoForge.EVENT_BUS.addListener(KeyMappings::onClientTick);
        NeoForge.EVENT_BUS.addListener(ClientEventHandler::onClientDisconnect);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, true, CollapsibleBlockIndicatorRenderer::onRenderBlockHighlight);

        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private static void onRegisterConditionalItemModelProperties(RegisterConditionalItemModelPropertyEvent event)
    {
        event.register(BlueprintProperty.HAS_DATA, BlueprintProperty.TYPE);
    }

    private static void onRegisterItemModels(RegisterItemModelsEvent event)
    {
        event.register(FramedBlockItemModel.Unbaked.ID, FramedBlockItemModel.Unbaked.CODEC);
        event.register(TankItemModel.Unbaked.ID, TankItemModel.Unbaked.CODEC);
    }

    private static void onRegisterSpecialModelRenderers(RegisterSpecialModelRendererEvent event)
    {
        event.register(TankItemRenderer.Unbaked.ID, TankItemRenderer.Unbaked.CODEC);
    }

    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event)
    {
        event.register(FBContent.MENU_TYPE_FRAMED_STORAGE.value(), FramedStorageScreen::new);
        event.register(FBContent.MENU_TYPE_FRAMED_DOUBLE_CHEST.value(), FramedStorageScreen::new);
        event.register(FBContent.MENU_TYPE_FRAMING_SAW.value(), FramingSawScreen::create);
        event.register(FBContent.MENU_TYPE_POWERED_FRAMING_SAW.value(), PoweredFramingSawScreen::new);
    }

    private static void onAttachDebugRenderers(AttachDebugRenderersEvent event)
    {
        FBContent.getBlockEntities().forEach(type -> event.attach(type.value(), ConnectionPredicateDebugRenderer.INSTANCE));
        FBContent.getBlockEntities().forEach(type -> event.attach(type.value(), QuadWindingDebugRenderer.INSTANCE));
        FBContent.getDoubleBlockEntities().forEach(type -> event.attach(type.value(), DoubleBlockPartDebugRenderer.INSTANCE));

        event.attach(FBContent.BE_TYPE_FRAMED_COLLAPSIBLE_BLOCK.value(), CollapsibleBlockDebugRenderer.INSTANCE);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_SIGN.value(), FramedSignRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_HANGING_SIGN.value(), FramedHangingSignRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_CHEST.value(), FramedChestRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_ITEM_FRAME.value(), FramedItemFrameRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_TANK.value(), FramedTankRenderer::new);
    }

    private static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event)
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

    private static void onRegisterBlockItemModelProviders(RegisterBlockItemModelProvidersEvent event)
    {
        event.register(Utils.id("default"), BlockItemModelProvider.DEFAULT);
        event.register(Utils.id("fence"), FenceBlockItemModelProvider.INSTANCE);
    }

    private static void onRegisterItemTintProviders(RegisterItemTintProvidersEvent event)
    {
        event.register(Utils.id("single"), FramedBlockItemTintProvider.INSTANCE_SINGLE);
        event.register(Utils.id("double"), FramedBlockItemTintProvider.INSTANCE_DOUBLE);
        event.register(Utils.id("target"), FramedTargetItemTintProvider.INSTANCE);
    }

    private static void onRegisterGuiLayers(RegisterGuiLayersEvent event)
    {
        BlockInteractOverlayLayer.init();

        event.registerAboveAll(Utils.id("block_interact"), new BlockInteractOverlayLayer());
    }

    private static void onRegisterBlockInteractOverlays(RegisterBlockInteractOverlaysEvent event)
    {
        event.register("state_lock", new StateLockOverlay());
        event.register("toggle_waterloggable", new ToggleWaterloggableOverlay());
        event.register("toggle_alt_slope", new ToggleAltSlopeOverlay());
        event.register("reinforcement", new ReinforcementOverlay());
        event.register("prism_offset", new PrismOffsetOverlay());
        event.register("split_line", new SplitLineOverlay());
        event.register("one_way_window", new OneWayWindowOverlay());
        event.register("frame_background", new FrameBackgroundOverlay());
        event.register("camo_rotation", new CamoRotationOverlay());
        event.register("trapdoor_texture_rotation", new TrapdoorTextureRotationOverlay());
        event.register("copycat_style", new CopycatStyleOverlay());
    }

    private static void onGeometryLoaderRegister(ModelEvent.RegisterLoaders event)
    {
        event.register(FallbackLoader.ID, new FallbackLoader());
    }

    private static void onRegisterModelWrappers(RegisterModelWrappersEvent event)
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
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_CORNER_SLOPE_EDGE, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_INNER_CORNER_SLOPE_EDGE, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_EDGE, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_EDGE, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_THREEWAY_CORNER_SLOPE_EDGE, FramedThreewayCornerSlopeEdgeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE, FramedInnerThreewayCornerSlopeEdgeGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_EDGE_SLAB, FramedSlopeEdgeSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_EDGE_PANEL, FramedSlopeEdgePanelGeometry::new, WrapHelper.IGNORE_DEFAULT);
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
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_STAIRS, FramedStairsGeometry::new, FramedStairsBlock.STATE_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_STAIRS, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_STAIRS, FramedHalfStairsGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_STAIRS, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT_LOCK);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_HALF_STAIRS, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SLICED_STAIRS_SLAB, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT_LOCK);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SLICED_STAIRS_PANEL, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPED_STAIRS, FramedSlopedStairsGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SLOPED_DOUBLE_STAIRS, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SLICED_SLOPED_STAIRS_SLAB, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SLICED_SLOPED_STAIRS_SLOPE, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS, FramedVerticalStairsGeometry::new, WrapHelper.IGNORE_DEFAULT_LOCK);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS, FramedVerticalHalfStairsGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT_LOCK);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_STAIRS, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_STAIRS, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS, FramedVerticalSlopedStairsGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_DOUBLE_STAIRS, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_PANEL, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_SLOPE, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_THREEWAY_CORNER_PILLAR, FramedThreewayCornerPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL, FramedWallGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FENCE, FramedFenceGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FENCE_GATE, FramedFenceGateGeometry::new, FramedFenceGateBlock.FenceGateStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_DOOR, FramedDoorGeometry::wood, FramedDoorBlock.DoorStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_DOOR, FramedDoorGeometry::iron, FramedDoorBlock.DoorStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TRAP_DOOR, FramedTrapDoorGeometry::wood, Utils.concat(Set.of(BlockStateProperties.POWERED), WrapHelper.IGNORE_DEFAULT));
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR, FramedTrapDoorGeometry::iron, Utils.concat(Set.of(BlockStateProperties.POWERED), WrapHelper.IGNORE_DEFAULT));
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
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SIGN, FramedSignGeometry::new, FramedStandingSignBlock.RotatingSignStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_SIGN, FramedWallSignGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HANGING_SIGN, FramedCeilingHangingSignGeometry::new, FramedStandingSignBlock.RotatingSignStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_HANGING_SIGN, FramedWallHangingSignGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TORCH, FramedTorchGeometry::normal, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_TORCH, FramedWallTorchGeometry::normal, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SOUL_TORCH, FramedTorchGeometry::soul, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SOUL_WALL_TORCH, FramedWallTorchGeometry::soul, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COPPER_TORCH, FramedTorchGeometry::copper, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COPPER_WALL_TORCH, FramedWallTorchGeometry::copper, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_REDSTONE_TORCH, FramedTorchGeometry::redstone, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_REDSTONE_WALL_TORCH, FramedWallTorchGeometry::redstone, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BOARD, FramedBoardGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_STRIP, FramedCornerStripGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LATTICE, FramedLatticeGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_THICK_LATTICE, FramedLatticeGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHEST, FramedChestGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SECRET_STORAGE, FramedCubeGeometry::new, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TANK, FramedCubeGeometry::new, WrapHelper.IGNORE_SOLID);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BARS, FramedBarsGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PANE, FramedPaneGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HORIZONTAL_PANE, FramedHorizontalPaneGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_RAIL_SLOPE, FramedRailSlopeGeometry::normal, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE, FramedRailSlopeGeometry::powered, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE, FramedRailSlopeGeometry::detector, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE, FramedRailSlopeGeometry::activator, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_RAIL, FramedFancyRailGeometry::normal, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL, FramedFancyRailGeometry::powered, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL, FramedFancyRailGeometry::detector, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL, FramedFancyRailGeometry::activator, WrapHelper.IGNORE_WATERLOGGED);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLOWER_POT, FramedFlowerPotGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PILLAR, FramedPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_PILLAR, FramedHalfPillarGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PILLAR_SOCKET, FramedPillarSocketGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SPLIT_PILLAR_SOCKET, NullCullPredicate.ONLY_RIGHT, WrapHelper.IGNORE_DEFAULT);
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
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB, FramedDoubleSlopeSlabBlock.NULL_CULL_PREDICATE, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB, NullCullPredicate.ALWAYS, WrapHelper.IGNORE_SOLID);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_SLOPE_SLAB, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER, FramedFlatSlopeSlabCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, FramedFlatInnerSlopeSlabCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER, FramedFlatElevatedSlopeSlabCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER, FramedFlatElevatedInnerSlopeSlabCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER, FramedFlatDoubleSlopeSlabCornerBlock.NULL_CULL_PREDICATE, WrapHelper.IGNORE_DEFAULT);
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
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER, FramedDoubleSlopePanelBlock.NULL_CULL_PREDICATE, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER, NullCullPredicate.NEVER, WrapHelper.IGNORE_DEFAULT);
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
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_PRISM_SLOPE_PANEL_CORNER, FramedSmallPrismSlopePanelCornerGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_PRISM_SLOPE_PANEL_CORNER_WALL, FramedSmallPrismSlopePanelCornerWallGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_PRISM_SLOPE_PANEL_CORNER, FramedLargePrismSlopePanelCornerGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_PRISM_SLOPE_PANEL_CORNER_WALL, FramedLargePrismSlopePanelCornerWallGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_INNER_PRISM_SLOPE_PANEL_CORNER, FramedSmallInnerPrismSlopePanelCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_INNER_PRISM_SLOPE_PANEL_CORNER_WALL, FramedSmallInnerPrismSlopePanelCornerWallGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_INNER_PRISM_SLOPE_PANEL_CORNER, FramedLargeInnerPrismSlopePanelCornerGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_INNER_PRISM_SLOPE_PANEL_CORNER_WALL, FramedLargeInnerPrismSlopePanelCornerWallGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PYRAMID, FramedPyramidGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PYRAMID_SLAB, FramedPyramidSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_PYRAMID_SLAB, FramedElevatedPyramidSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_UPPER_PYRAMID_SLAB, FramedUpperPyramidSlabGeometry::new, WrapHelper.IGNORE_DEFAULT);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_PYRAMID_SLAB, NullCullPredicate.ONLY_LEFT, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TARGET, FramedTargetGeometry::new, StateMerger.IGNORE_ALL);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GATE, FramedDoorGeometry::wood, FramedDoorBlock.DoorStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_GATE, FramedDoorGeometry::iron, FramedDoorBlock.DoorStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ITEM_FRAME, FramedItemFrameGeometry::normal, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME, FramedItemFrameGeometry::glowing, WrapHelper.IGNORE_WATERLOGGED);
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
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_TUBE, FramedCornerTubeGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHAIN, FramedChainGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LANTERN, FramedLanternGeometry::normal, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SOUL_LANTERN, FramedLanternGeometry::soul, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COPPER_LANTERN, FramedLanternGeometry::copper, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HOPPER, FramedHopperGeometry::new, WrapHelper.IGNORE_ALWAYS);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LAYERED_CUBE, FramedLayeredCubeGeometry::new, WrapHelper.IGNORE_DEFAULT);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LIGHTNING_ROD, FramedLightningRodGeometry::new, WrapHelper.IGNORE_WATERLOGGED);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PATH, FramedPathGeometry::new, WrapHelper.IGNORE_SOLID);

        WrapHelper.wrapStandalone(FramedChestRenderer.WRAPPER_KEY, FramedChestLidGeometry::new, FramedChestLidModel::new, WrapHelper.IGNORE_WATERLOGGED);
    }

    private static void onBlockStateModelRegister(RegisterBlockStateModels event)
    {
        event.registerDefinition(Utils.id("wrapper"), FramedBlockModelDefinition.CODEC);
    }

    private static void onRegisterStandaloneModels(ModelEvent.RegisterStandalone event)
    {
        ModelUtils.registerStandaloneForLoading(event, FluidModel.BARE_MODEL);
        ModelUtils.registerStandaloneForLoading(event, FluidModel.BARE_MODEL_SINGLE);
    }

    private static void onModelsLoaded(ModelEvent.BakingCompleted event)
    {
        CacheCleaner.clearExternalGeometryCaches(CacheCleaner.Reason.RELOAD);
        FramedBlockModel.collectCubeBaseModels(event.getBakingResult().blockStateModels());

        ModelWrappingManager.printWrappingInfo(event.getBakingResult().blockStateModels());
    }

    private static void onRegisterReloadListener(AddClientReloadListenersEvent event)
    {
        event.addListener(BlockInteractOverlayLayer.LISTENER_ID, (ResourceManagerReloadListener) BlockInteractOverlayLayer::onResourceReload);
    }

    private static void onInitClientRegistries(InitializeClientRegistriesEvent event)
    {
        ModelWrappingManager.fireRegistration();
        FramedBlockDebugRenderer.init();
        BlockOutlineRenderer.init();
        GhostBlockRenderer.init();
        BlockItemModelProviders.init();
        DynamicItemTintProviders.init();
    }

    private static void onRegisterSpriteSources(RegisterSpriteSourcesEvent event)
    {
        event.register(Utils.id("anim_splitter"), AnimationSplitterSource.CODEC);
        event.register(Utils.id("area_mask"), AreaMaskSource.CODEC);
    }

    private static void onRegisterParticleProviders(RegisterParticleProvidersEvent event)
    {
        event.registerSpecial(FBContent.FLUID_PARTICLE.get(), new FluidSpriteParticle.Provider());
        event.registerSpecial(FBContent.BLOCK_OVERLAY_PARTICLE.value(), new BlockOverlayParticle.Provider());
    }

    private static void onRegisterClientExtensions(RegisterClientExtensionsEvent event)
    {
        FBContent.getRegisteredBlocks()
                .stream()
                .map(Holder::value)
                .filter(IFramedBlock.class::isInstance)
                .map(block -> Pair.of(block, switch (block)
                {
                    case FramedItemFrameBlock ignored -> NoEffectsClientBlockExtensions.INSTANCE;
                    case FramedOneWayWindowBlock ignored -> new OneWayWindowClientBlockExtensions();
                    case IFramedDoubleBlock ignored -> FramedClientDoubleBlockExtensions.INSTANCE;
                    default -> FramedClientBlockExtensions.INSTANCE;
                }))
                .forEach(pair -> event.registerBlock(pair.getSecond(), pair.getFirst()));
    }

    private static void onRegisterClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event)
    {
        event.register(BlockPreviewTooltipComponent.class, Function.identity());
    }

    private static void onRegisterPictureInPictureRenderers(RegisterPictureInPictureRenderersEvent event)
    {
        event.register(SignBlockPictureInPictureRenderer.RenderState.class, SignBlockPictureInPictureRenderer::new);
        event.register(SpinningItemPictureInPictureRenderer.RenderState.class, SpinningItemPictureInPictureRenderer::new);
        event.register(BlockPictureInPictureRenderer.RenderState.class, BlockPictureInPictureRenderer::new);
    }



    private static void wrapDoubleModel(Holder<Block> block, NullCullPredicate nullCullPredicate, Set<Property<?>> ignoredProps)
    {
        wrapDoubleModel(block, nullCullPredicate, DoubleBlockItemModelInfo.INSTANCE, ignoredProps);
    }

    private static void wrapDoubleModel(Holder<Block> block, NullCullPredicate nullCullPredicate, ItemModelInfo itemModelInfo, Set<Property<?>> ignoredProps)
    {
        WrapHelper.wrapDouble(block, nullCullPredicate, itemModelInfo, ignoredProps);
    }

    private static boolean useDefaultColorHandler(IFramedBlock block)
    {
        IBlockType type = block.getBlockType();
        return type != BlockType.FRAMED_FLOWER_POT && type != BlockType.FRAMED_TARGET;
    }
}
