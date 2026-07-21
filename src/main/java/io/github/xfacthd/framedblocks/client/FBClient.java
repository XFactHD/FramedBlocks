package io.github.xfacthd.framedblocks.client;

import com.mojang.datafixers.util.Pair;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.render.FramedClientBlockExtensions;
import io.github.xfacthd.framedblocks.api.block.render.FramedClientDoubleBlockExtensions;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelDataProvider;
import io.github.xfacthd.framedblocks.api.model.item.RegisterItemModelDataProvidersEvent;
import io.github.xfacthd.framedblocks.api.model.item.block.BlockItemModelProvider;
import io.github.xfacthd.framedblocks.api.model.item.block.RegisterBlockItemModelProvidersEvent;
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
import io.github.xfacthd.framedblocks.client.model.ResourceCubeModel;
import io.github.xfacthd.framedblocks.client.model.RuntimeMaterialBaker;
import io.github.xfacthd.framedblocks.client.model.baked.FramedBlockStateModel;
import io.github.xfacthd.framedblocks.client.model.geometry.cube.*;
import io.github.xfacthd.framedblocks.client.model.geometry.interactive.*;
import io.github.xfacthd.framedblocks.client.model.geometry.pane.*;
import io.github.xfacthd.framedblocks.client.model.geometry.pillar.*;
import io.github.xfacthd.framedblocks.client.model.geometry.prism.*;
import io.github.xfacthd.framedblocks.client.model.geometry.rail.*;
import io.github.xfacthd.framedblocks.client.model.geometry.slope.*;
import io.github.xfacthd.framedblocks.client.model.geometry.slopeedge.*;
import io.github.xfacthd.framedblocks.client.model.geometry.slopepanel.*;
import io.github.xfacthd.framedblocks.client.model.geometry.slopepanelcorner.*;
import io.github.xfacthd.framedblocks.client.model.geometry.slopeslab.*;
import io.github.xfacthd.framedblocks.client.model.geometry.stairs.*;
import io.github.xfacthd.framedblocks.client.model.geometry.templated.TemplateSpecs;
import io.github.xfacthd.framedblocks.client.model.geometry.torch.*;
import io.github.xfacthd.framedblocks.client.model.item.BlockItemModelProviders;
import io.github.xfacthd.framedblocks.client.model.item.FramedBlockItemModel;
import io.github.xfacthd.framedblocks.client.model.item.ItemModelDataProviders;
import io.github.xfacthd.framedblocks.client.model.item.PaintRollerItemModel;
import io.github.xfacthd.framedblocks.client.model.item.TankItemModel;
import io.github.xfacthd.framedblocks.client.model.item.dataprovider.AdjustableDoubleBlockItemModelDataProvider;
import io.github.xfacthd.framedblocks.client.model.item.modelprovider.FenceBlockItemModelProvider;
import io.github.xfacthd.framedblocks.client.model.item.property.BlueprintProperty;
import io.github.xfacthd.framedblocks.client.model.loader.fallback.FallbackLoader;
import io.github.xfacthd.framedblocks.client.model.special.FramedBannerFlagModel;
import io.github.xfacthd.framedblocks.client.model.special.FramedChestLidModel;
import io.github.xfacthd.framedblocks.client.model.template.GeometryTemplateManager;
import io.github.xfacthd.framedblocks.client.model.unbaked.FramedBlockModelDefinition;
import io.github.xfacthd.framedblocks.client.model.wrapping.ModelWrappingManager;
import io.github.xfacthd.framedblocks.client.net.ClientNetworkHandler;
import io.github.xfacthd.framedblocks.client.render.block.FramedBannerRenderer;
import io.github.xfacthd.framedblocks.client.render.block.FramedChestRenderer;
import io.github.xfacthd.framedblocks.client.render.block.FramedItemFrameRenderer;
import io.github.xfacthd.framedblocks.client.render.block.FramedStandingSignRenderer;
import io.github.xfacthd.framedblocks.client.render.block.FramedTankRenderer;
import io.github.xfacthd.framedblocks.client.render.debug.FramedBlockDebugRenderer;
import io.github.xfacthd.framedblocks.client.render.debug.impl.CollapsibleBlockDebugRenderer;
import io.github.xfacthd.framedblocks.client.render.debug.impl.ConnectionPredicateDebugRenderer;
import io.github.xfacthd.framedblocks.client.render.debug.impl.DoubleBlockPartDebugRenderer;
import io.github.xfacthd.framedblocks.client.render.debug.impl.QuadWindingDebugRenderer;
import io.github.xfacthd.framedblocks.client.render.item.BannerItemRenderer;
import io.github.xfacthd.framedblocks.client.render.item.CamoApplicatorRenderer;
import io.github.xfacthd.framedblocks.client.render.item.TankItemRenderer;
import io.github.xfacthd.framedblocks.client.render.particle.BlockOverlayParticleProvider;
import io.github.xfacthd.framedblocks.client.render.particle.CamoParticleProvider;
import io.github.xfacthd.framedblocks.client.render.special.BlockOutlineRenderer;
import io.github.xfacthd.framedblocks.client.render.special.CollapsibleBlockIndicatorRenderer;
import io.github.xfacthd.framedblocks.client.render.special.GhostBlockFeatureRenderer;
import io.github.xfacthd.framedblocks.client.render.special.GhostBlockRenderer;
import io.github.xfacthd.framedblocks.client.render.util.AnimationSplitterSource;
import io.github.xfacthd.framedblocks.client.render.util.AreaMaskSource;
import io.github.xfacthd.framedblocks.client.render.util.FramedPipelineModifiers;
import io.github.xfacthd.framedblocks.client.render.util.FramedRenderPipelines;
import io.github.xfacthd.framedblocks.client.screen.CamoApplicatorScreen;
import io.github.xfacthd.framedblocks.client.screen.FramedStorageScreen;
import io.github.xfacthd.framedblocks.client.screen.PaintRollerScreen;
import io.github.xfacthd.framedblocks.client.screen.overlay.*;
import io.github.xfacthd.framedblocks.client.screen.overlay.impl.*;
import io.github.xfacthd.framedblocks.client.screen.pip.BlockPictureInPictureRenderer;
import io.github.xfacthd.framedblocks.client.screen.pip.SpinningItemPictureInPictureRenderer;
import io.github.xfacthd.framedblocks.client.screen.saw.FramingSawScreen;
import io.github.xfacthd.framedblocks.client.screen.saw.PoweredFramingSawScreen;
import io.github.xfacthd.framedblocks.client.screen.widget.BlockPreviewTooltipComponent;
import io.github.xfacthd.framedblocks.client.screen.widget.PaintRollerClientTooltipComponent;
import io.github.xfacthd.framedblocks.client.util.CacheCleaner;
import io.github.xfacthd.framedblocks.client.util.ClientEventHandler;
import io.github.xfacthd.framedblocks.client.util.ClientTaskQueue;
import io.github.xfacthd.framedblocks.client.util.KeyMappings;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.cube.FramedMiniCubeBlock;
import io.github.xfacthd.framedblocks.common.block.cube.FramedOneWayWindowBlock;
import io.github.xfacthd.framedblocks.common.block.cube.FramedTargetBlock;
import io.github.xfacthd.framedblocks.common.block.door.FramedDoorBlock;
import io.github.xfacthd.framedblocks.common.block.door.FramedFenceGateBlock;
import io.github.xfacthd.framedblocks.common.block.interactive.FramedFlowerPotBlock;
import io.github.xfacthd.framedblocks.common.block.interactive.FramedItemFrameBlock;
import io.github.xfacthd.framedblocks.common.block.interactive.button.FramedButtonBlock;
import io.github.xfacthd.framedblocks.common.block.interactive.button.FramedLargeButtonBlock;
import io.github.xfacthd.framedblocks.common.block.interactive.pressureplate.FramedWeightedPressurePlateBlock;
import io.github.xfacthd.framedblocks.common.block.sign.FramedStandingSignBlock;
import io.github.xfacthd.framedblocks.common.block.stairs.standard.FramedStairsBlock;
import io.github.xfacthd.framedblocks.common.data.component.PaintRollerContents;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.ShelfRenderer;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
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

import java.util.function.Function;

@Mod(value = FramedConstants.MOD_ID, dist = Dist.CLIENT)
public final class FBClient {
    public FBClient(IEventBus modBus, ModContainer container) {
        modBus.addListener(FBClient::onRegisterConditionalItemModelProperties);
        modBus.addListener(FBClient::onRegisterBlockModels);
        modBus.addListener(FBClient::onRegisterItemModels);
        modBus.addListener(FBClient::onRegisterSpecialModelRenderers);
        modBus.addListener(FBClient::onRegisterMenuScreens);
        modBus.addListener(FBClient::onAttachDebugRenderers);
        modBus.addListener(FBClient::onRegisterRenderers);
        modBus.addListener(FBClient::onRegisterBlockItemModelProviders);
        modBus.addListener(FBClient::onRegisterItemModelDataProviders);
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
        modBus.addListener(FramedPipelineModifiers::onRegisterModifiers);

        NeoForge.EVENT_BUS.addListener(FBClient::onRegisterFeatureRenderers);
        NeoForge.EVENT_BUS.addListener(ClientTaskQueue::onClientTick);
        NeoForge.EVENT_BUS.addListener(BlockOutlineRenderer::onRenderBlockHighlight);
        NeoForge.EVENT_BUS.addListener(KeyMappings::onClientTick);
        NeoForge.EVENT_BUS.addListener(ClientEventHandler::onClientDisconnect);
        NeoForge.EVENT_BUS.addListener(ClientEventHandler::onScrollInput);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, true, CollapsibleBlockIndicatorRenderer::onRenderBlockHighlight);

        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private static void onRegisterConditionalItemModelProperties(RegisterConditionalItemModelPropertyEvent event) {
        event.register(BlueprintProperty.HAS_DATA, BlueprintProperty.TYPE);
    }

    private static void onRegisterBlockModels(RegisterBlockModelsEvent event) {
        ModelWrappingManager.onRegisterBlockModels(event);
    }

    private static void onRegisterItemModels(RegisterItemModelsEvent event) {
        event.register(FramedBlockItemModel.Unbaked.ID, FramedBlockItemModel.Unbaked.CODEC);
        event.register(TankItemModel.Unbaked.ID, TankItemModel.Unbaked.CODEC);
        event.register(PaintRollerItemModel.Unbaked.ID, PaintRollerItemModel.Unbaked.CODEC);
    }

    private static void onRegisterSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(TankItemRenderer.Unbaked.ID, TankItemRenderer.Unbaked.CODEC);
        event.register(BannerItemRenderer.Unbaked.ID, BannerItemRenderer.Unbaked.CODEC);
        event.register(CamoApplicatorRenderer.Unbaked.ID, CamoApplicatorRenderer.Unbaked.CODEC);
    }

    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(FBContent.MENU_TYPE_FRAMED_STORAGE.value(), FramedStorageScreen::new);
        event.register(FBContent.MENU_TYPE_FRAMED_DOUBLE_CHEST.value(), FramedStorageScreen::new);
        event.register(FBContent.MENU_TYPE_FRAMING_SAW.value(), FramingSawScreen::create);
        event.register(FBContent.MENU_TYPE_POWERED_FRAMING_SAW.value(), PoweredFramingSawScreen::new);
        event.register(FBContent.MENU_TYPE_CAMO_APPLICATOR.value(), CamoApplicatorScreen::new);
        event.register(FBContent.MENU_TYPE_PAINT_ROLLER.value(), PaintRollerScreen::new);
    }

    private static void onAttachDebugRenderers(AttachDebugRenderersEvent event) {
        FBContent.getBlockEntities().forEach(type -> event.attach(type.value(), ConnectionPredicateDebugRenderer.INSTANCE));
        FBContent.getBlockEntities().forEach(type -> event.attach(type.value(), QuadWindingDebugRenderer.INSTANCE));
        FBContent.getDoubleBlockEntities().forEach(type -> event.attach(type.value(), DoubleBlockPartDebugRenderer.INSTANCE));

        event.attach(FBContent.BE_TYPE_FRAMED_COLLAPSIBLE_BLOCK.value(), CollapsibleBlockDebugRenderer.INSTANCE);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_SIGN.value(), FramedStandingSignRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_HANGING_SIGN.value(), HangingSignRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_CHEST.value(), FramedChestRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_ITEM_FRAME.value(), FramedItemFrameRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_TANK.value(), FramedTankRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_SHELF.value(), ShelfRenderer::new);
        event.registerBlockEntityRenderer(FBContent.BE_TYPE_FRAMED_BANNER.value(), FramedBannerRenderer::new);
    }

    private static void onRegisterBlockItemModelProviders(RegisterBlockItemModelProvidersEvent event) {
        event.register(Utils.id("default"), BlockItemModelProvider.DEFAULT);
        event.register(Utils.id("fence"), FenceBlockItemModelProvider.INSTANCE);
    }

    private static void onRegisterItemModelDataProviders(RegisterItemModelDataProvidersEvent event) {
        event.register(Utils.id("default"), ItemModelDataProvider.DEFAULT);
        event.register(Utils.id("double_block"), ItemModelDataProvider.DOUBLE_BLOCK);
        event.register(Utils.id("target"), FramedTargetGeometry.ITEM_MODEL_DATA_PROVIDER);
        event.register(Utils.id("adjustable_double_block_standard"), AdjustableDoubleBlockItemModelDataProvider.STANDARD);
        event.register(Utils.id("adjustable_double_block_copycat"), AdjustableDoubleBlockItemModelDataProvider.COPYCAT);
    }

    private static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        BlockInteractOverlayLayer.init();

        event.registerAboveAll(Utils.id("block_interact"), new BlockInteractOverlayLayer());
        event.registerAboveAll(Utils.id("placement_state"), new PlacementStateCycleOverlay());
    }

    private static void onRegisterBlockInteractOverlays(RegisterBlockInteractOverlaysEvent event) {
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

    private static void onGeometryLoaderRegister(ModelEvent.RegisterLoaders event) {
        event.register(FallbackLoader.ID, new FallbackLoader());
    }

    private static void onRegisterModelWrappers(RegisterModelWrappersEvent event) {
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CUBE, FramedCubeGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE, FramedSlopeGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_SLOPE, FramedHalfSlopeGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE, FramedVerticalHalfSlopeGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_SLOPE, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_SLOPE, FramedCornerSlopeGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE, FramedInnerCornerSlopeGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_CORNER, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PRISM_CORNER, FramedPrismCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER, FramedInnerPrismCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_THREEWAY_CORNER, FramedThreewayCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER, FramedInnerThreewayCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_EDGE, FramedSlopeEdgeGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_EDGE, FramedElevatedSlopeEdgeGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_SLOPE_EDGE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_SLOPE_EDGE, FramedCornerSlopeEdgeGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE_EDGE, FramedInnerCornerSlopeEdgeGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_CORNER_SLOPE_EDGE, FramedElevatedCornerSlopeEdgeGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE, FramedElevatedInnerCornerSlopeEdgeGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_CORNER_SLOPE_EDGE, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_INNER_CORNER_SLOPE_EDGE, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_EDGE, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_EDGE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_THREEWAY_CORNER_SLOPE_EDGE, FramedThreewayCornerSlopeEdgeGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE, FramedInnerThreewayCornerSlopeEdgeGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_EDGE_SLAB, FramedSlopeEdgeSlabGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_EDGE_PANEL, FramedSlopeEdgePanelGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLAB, TemplateSpecs.SLAB, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLAB, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_SLAB, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_SLAB, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_SLAB, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLAB_EDGE, TemplateSpecs.SLAB_EDGE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLAB_CORNER, TemplateSpecs.SLAB_CORNER, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PANEL, TemplateSpecs.PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_HOR, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_PILLAR, TemplateSpecs.CORNER_PILLAR, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_STAIRS, TemplateSpecs.STAIRS, FramedStairsBlock.STATE_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_STAIRS, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_STAIRS, TemplateSpecs.HALF_STAIRS, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_STAIRS, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_HALF_STAIRS, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SLICED_STAIRS_SLAB, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SLICED_STAIRS_PANEL, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPED_STAIRS, FramedSlopedStairsGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SLOPED_DOUBLE_STAIRS, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SLICED_SLOPED_STAIRS_SLAB, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SLICED_SLOPED_STAIRS_SLOPE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS, TemplateSpecs.VERTICAL_STAIRS, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS, TemplateSpecs.VERTICAL_HALF_STAIRS, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_STAIRS, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_STAIRS, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS, FramedVerticalSlopedStairsGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_DOUBLE_STAIRS, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_SLOPE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_THREEWAY_CORNER_PILLAR, TemplateSpecs.THREEWAY_CORNER_PILLAR, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL, FramedWallGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FENCE, TemplateSpecs.FENCE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FENCE_GATE, TemplateSpecs.FENCE_GATE, FramedFenceGateBlock.FenceGateStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_DOOR, TemplateSpecs.DOOR, FramedDoorBlock.DoorStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_DOOR, TemplateSpecs.IRON_DOOR, FramedDoorBlock.DoorStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TRAP_DOOR, TemplateSpecs.TRAPDOOR, WrapHelper.POWERED_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR, TemplateSpecs.IRON_TRAPDOOR, WrapHelper.POWERED_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PRESSURE_PLATE, TemplateSpecs.PRESSURE_PLATE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_PRESSURE_PLATE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE, TemplateSpecs.STONE_PRESSURE_PLATE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE, TemplateSpecs.OBSIDIAN_PRESSURE_PLATE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE, TemplateSpecs.GOLD_PRESSURE_PLATE, FramedWeightedPressurePlateBlock.STATE_MERGER);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE, FramedWeightedPressurePlateBlock.STATE_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE, TemplateSpecs.IRON_PRESSURE_PLATE, FramedWeightedPressurePlateBlock.STATE_MERGER);
        WrapHelper.copy(FBContent.BLOCK_FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE, FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE, FramedWeightedPressurePlateBlock.STATE_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LADDER, TemplateSpecs.LADDER, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BUTTON, TemplateSpecs.BUTTON, FramedButtonBlock.STATE_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_STONE_BUTTON, TemplateSpecs.STONE_BUTTON, FramedButtonBlock.STATE_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_BUTTON, TemplateSpecs.LARGE_BUTTON, FramedLargeButtonBlock.LARGE_STATE_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_STONE_BUTTON, TemplateSpecs.LARGE_STONE_BUTTON, FramedLargeButtonBlock.LARGE_STATE_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LEVER, FramedLeverGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SIGN, FramedSignGeometry::new, FramedStandingSignBlock.RotatingSignStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_SIGN, TemplateSpecs.WALL_SIGN, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HANGING_SIGN, FramedCeilingHangingSignGeometry::new, FramedStandingSignBlock.RotatingSignStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_HANGING_SIGN, FramedWallHangingSignGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TORCH, FramedTorchGeometry::normal, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_WALL_TORCH, FramedWallTorchGeometry::normal, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SOUL_TORCH, FramedTorchGeometry::soul, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SOUL_WALL_TORCH, FramedWallTorchGeometry::soul, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COPPER_TORCH, FramedTorchGeometry::copper, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COPPER_WALL_TORCH, FramedWallTorchGeometry::copper, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_REDSTONE_TORCH, FramedTorchGeometry::redstone, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_REDSTONE_WALL_TORCH, FramedWallTorchGeometry::redstone, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BOARD, FramedBoardGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_BOARD, TemplateSpecs.HALF_BOARD, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DIVIDED_BOARD, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_BOARD, TemplateSpecs.CORNER_BOARD, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_INNER_CORNER_BOARD, TemplateSpecs.INNER_CORNER_BOARD, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_CORNER_BOARD, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_STRIP, TemplateSpecs.CORNER_STRIP, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LATTICE, TemplateSpecs.LATTICE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_THICK_LATTICE, TemplateSpecs.THICK_LATTICE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHEST, FramedChestGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SECRET_STORAGE, FramedCubeGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TANK, FramedCubeGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BARS, FramedBarsGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PANE, FramedPaneGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HORIZONTAL_PANE, TemplateSpecs.HORIZONTAL_PANE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_RAIL_SLOPE, FramedRailSlopeGeometry::normal, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE, FramedRailSlopeGeometry::powered, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE, FramedRailSlopeGeometry::detector, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE, FramedRailSlopeGeometry::activator, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_RAIL, FramedFancyRailGeometry::normal, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL, FramedFancyRailGeometry::powered, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL, FramedFancyRailGeometry::detector, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL, FramedFancyRailGeometry::activator, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLOWER_POT, FramedFlowerPotGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PILLAR, TemplateSpecs.PILLAR, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HALF_PILLAR, TemplateSpecs.HALF_PILLAR, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PILLAR_SOCKET, TemplateSpecs.PILLAR_SOCKET, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SPLIT_PILLAR_SOCKET, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_POST, TemplateSpecs.POST, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK, FramedCollapsibleBlockGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK, FramedCollapsibleCopycatBlockGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BOUNCY_CUBE, FramedMarkedCubeGeometry::slime, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_REDSTONE_BLOCK, FramedMarkedCubeGeometry::redstone, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PRISM, FramedPrismGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_INNER_PRISM, FramedElevatedInnerPrismGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_INNER_DOUBLE_PRISM, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPED_PRISM, FramedSlopedPrismGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_INNER_SLOPED_PRISM, FramedElevatedInnerSlopedPrismGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_SLAB, FramedSlopeSlabGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB, FramedElevatedSlopeSlabGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COMPOUND_SLOPE_SLAB, FramedCompoundSlopeSlabGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_SLOPE_SLAB, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER, FramedFlatSlopeSlabCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, FramedFlatInnerSlopeSlabCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER, FramedFlatElevatedSlopeSlabCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER, FramedFlatElevatedInnerSlopeSlabCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SLOPE_PANEL, FramedSlopePanelGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_SLOPE_PANEL, FramedExtendedSlopePanelGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COMPOUND_SLOPE_PANEL, FramedCompoundSlopePanelGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_SLOPE_PANEL, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER, FramedFlatSlopePanelCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER, FramedFlatInnerSlopePanelCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER, FramedFlatExtendedSlopePanelCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER, FramedFlatExtendedInnerSlopePanelCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL, FramedSmallCornerSlopePanelGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL_WALL, FramedSmallCornerSlopePanelWallGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL, FramedLargeCornerSlopePanelGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL_WALL, FramedLargeCornerSlopePanelWallGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL, FramedSmallInnerCornerSlopePanelGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_WALL, FramedSmallInnerCornerSlopePanelWallGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL, FramedLargeInnerCornerSlopePanelGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_WALL, FramedLargeInnerCornerSlopePanelWallGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL, FramedExtendedCornerSlopePanelGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL_WALL, FramedExtendedCornerSlopePanelWallGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL, FramedExtendedInnerCornerSlopePanelGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL_WALL, FramedExtendedInnerCornerSlopePanelWallGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_WALL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_WALL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL_WALL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL_WALL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL_WALL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL_WALL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_WALL, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_PRISM_SLOPE_PANEL_CORNER, FramedSmallPrismSlopePanelCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_PRISM_SLOPE_PANEL_CORNER_WALL, FramedSmallPrismSlopePanelCornerWallGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_PRISM_SLOPE_PANEL_CORNER, FramedLargePrismSlopePanelCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_PRISM_SLOPE_PANEL_CORNER_WALL, FramedLargePrismSlopePanelCornerWallGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_INNER_PRISM_SLOPE_PANEL_CORNER, FramedSmallInnerPrismSlopePanelCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SMALL_INNER_PRISM_SLOPE_PANEL_CORNER_WALL, FramedSmallInnerPrismSlopePanelCornerWallGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_INNER_PRISM_SLOPE_PANEL_CORNER, FramedLargeInnerPrismSlopePanelCornerGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LARGE_INNER_PRISM_SLOPE_PANEL_CORNER_WALL, FramedLargeInnerPrismSlopePanelCornerWallGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PYRAMID, FramedPyramidGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PYRAMID_SLAB, FramedPyramidSlabGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ELEVATED_PYRAMID_SLAB, FramedElevatedPyramidSlabGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_UPPER_PYRAMID_SLAB, FramedUpperPyramidSlabGeometry::new, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_STACKED_PYRAMID_SLAB, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TARGET, FramedTargetGeometry::new, StateMerger.IGNORE_ALL);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GATE, TemplateSpecs.GATE, FramedDoorBlock.DoorStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_IRON_GATE, TemplateSpecs.IRON_GATE, FramedDoorBlock.DoorStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ITEM_FRAME, FramedItemFrameGeometry::normal, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME, FramedItemFrameGeometry::glowing, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_MINI_CUBE, FramedMiniCubeGeometry::new, FramedMiniCubeBlock.MiniCubeStateMerger.INSTANCE);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW, FramedOneWayWindowGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_BOOKSHELF, TemplateSpecs.BOOKSHELF, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHISELED_BOOKSHELF, TemplateSpecs.CHISELED_BOOKSHELF, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CENTERED_SLAB, TemplateSpecs.CENTERED_SLAB, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CENTERED_PANEL, TemplateSpecs.CENTERED_PANEL, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_MASONRY_CORNER_SEGMENT, TemplateSpecs.MASONRY_CORNER_SEGMENT, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_MASONRY_CORNER, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHECKERED_CUBE_SEGMENT, TemplateSpecs.CHECKERED_CUBE_SEGMENT, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_CHECKERED_CUBE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHECKERED_SLAB_SEGMENT, TemplateSpecs.CHECKERED_SLAB_SEGMENT, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_CHECKERED_SLAB, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHECKERED_PANEL_SEGMENT, TemplateSpecs.CHECKERED_PANEL_SEGMENT, WrapHelper.DEFAULT_MERGER);
        wrapDoubleModel(FBContent.BLOCK_FRAMED_CHECKERED_PANEL, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_TUBE, TemplateSpecs.TUBE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CORNER_TUBE, TemplateSpecs.CORNER_TUBE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_CHAIN, FramedChainGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LANTERN, FramedLanternGeometry::normal, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SOUL_LANTERN, FramedLanternGeometry::soul, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_COPPER_LANTERN, FramedLanternGeometry::copper, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_HOPPER, TemplateSpecs.HOPPER, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LAYERED_CUBE, TemplateSpecs.LAYERED_CUBE, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_LIGHTNING_ROD, FramedLightningRodGeometry::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_PATH, TemplateSpecs.PATH, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrap(FBContent.BLOCK_FRAMED_SHELF, TemplateSpecs.SHELF, WrapHelper.DEFAULT_MERGER);

        WrapHelper.wrapEmpty(FBContent.BLOCK_FRAMED_BANNER);
        WrapHelper.wrapEmpty(FBContent.BLOCK_FRAMED_WALL_BANNER);

        WrapHelper.wrapStandalone(FramedChestRenderer.WRAPPER_KEY, FramedChestLidGeometry::new, FramedChestLidModel::new, WrapHelper.DEFAULT_MERGER);
        WrapHelper.wrapStandalone(FramedBannerRenderer.WRAPPER_KEY, FramedBannerFlagGeometry::new, FramedBannerFlagModel::new, FramedBannerFlagGeometry.STATE_MERGER);

        WrapHelper.overrideBlockModelFactory(FBContent.BLOCK_FRAMED_BANNER, BannerItemRenderer::createBlockModel);
        WrapHelper.overrideBlockModelFactory(FBContent.BLOCK_FRAMED_WALL_BANNER, BannerItemRenderer::createBlockModel);
    }

    private static void onBlockStateModelRegister(RegisterBlockStateModels event) {
        event.registerDefinition(Utils.id("wrapper"), FramedBlockModelDefinition.CODEC);
    }

    private static void onRegisterStandaloneModels(ModelEvent.RegisterStandalone event) {
        ModelUtils.registerStandaloneForLoading(event, ResourceCubeModel.MODEL_BARE);
        ModelUtils.registerStandaloneForLoading(event, ResourceCubeModel.MODEL_BARE_TINTED);
        ModelUtils.registerStandaloneForLoading(event, ResourceCubeModel.MODEL_BARE_SINGLE);
        ModelUtils.registerStandaloneForLoading(event, ResourceCubeModel.MODEL_BARE_SINGLE_TINTED);
    }

    private static void onModelsLoaded(ModelEvent.BakingCompleted event) {
        CacheCleaner.clearExternalGeometryCaches(CacheCleaner.Reason.RELOAD);
        FramedBlockStateModel.collectCubeBaseModels(event.getBakingResult().blockStateModels());

        ModelWrappingManager.printWrappingInfo(event.getBakingResult().blockStateModels());
    }

    private static void onRegisterReloadListener(AddClientReloadListenersEvent event) {
        event.addListener(BlockInteractOverlayLayer.LISTENER_ID, BlockInteractOverlayLayer.RELOAD_LISTENER);
        event.addListener(RuntimeMaterialBaker.LISTENER_ID, RuntimeMaterialBaker::reload);
        event.addListener(GeometryTemplateManager.LISTENER_ID, new GeometryTemplateManager());
    }

    private static void onInitClientRegistries(InitializeClientRegistriesEvent event) {
        ModelWrappingManager.fireRegistration();
        FramedBlockDebugRenderer.init();
        BlockOutlineRenderer.init();
        GhostBlockRenderer.init();
        BlockItemModelProviders.init();
        ItemModelDataProviders.init();
    }

    private static void onRegisterSpriteSources(RegisterSpriteSourcesEvent event) {
        event.register(Utils.id("anim_splitter"), AnimationSplitterSource.CODEC);
        event.register(Utils.id("area_mask"), AreaMaskSource.CODEC);
    }

    private static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpecial(FBContent.CAMO_PARTICLE.value(), new CamoParticleProvider());
        event.registerSpecial(FBContent.BLOCK_OVERLAY_PARTICLE.value(), new BlockOverlayParticleProvider());
    }

    private static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        FBContent.getRegisteredBlocks()
                .stream()
                .map(Holder::value)
                .filter(IFramedBlock.class::isInstance)
                .map(block -> Pair.of(block, switch (block) {
                    case FramedItemFrameBlock _ -> NoEffectsClientBlockExtensions.INSTANCE;
                    case FramedOneWayWindowBlock _ -> new OneWayWindowClientBlockExtensions();
                    case FramedTargetBlock _ -> new TargetClientBlockExtensions();
                    case FramedFlowerPotBlock _ -> new FlowerPotClientBlockExtensions();
                    case IFramedDoubleBlock _ -> FramedClientDoubleBlockExtensions.INSTANCE;
                    default -> FramedClientBlockExtensions.INSTANCE;
                }))
                .forEach(pair -> event.registerBlock(pair.getSecond(), pair.getFirst()));
    }

    private static void onRegisterClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(BlockPreviewTooltipComponent.class, Function.identity());
        event.register(PaintRollerContents.class, PaintRollerClientTooltipComponent::new);
    }

    private static void onRegisterPictureInPictureRenderers(RegisterPictureInPictureRenderersEvent event) {
        event.register(BlockPictureInPictureRenderer.RenderState.class, BlockPictureInPictureRenderer::new);
        event.register(SpinningItemPictureInPictureRenderer.RenderState.class, SpinningItemPictureInPictureRenderer::new);
    }

    private static void onRegisterFeatureRenderers(RegisterFeatureRenderersEvent event) {
        event.register(GhostBlockFeatureRenderer.TYPE, new GhostBlockFeatureRenderer());
    }

    private static void wrapDoubleModel(Holder<Block> block, @SuppressWarnings("SameParameterValue") StateMerger stateMerger) {
        WrapHelper.wrapDouble(block, stateMerger);
    }
}
