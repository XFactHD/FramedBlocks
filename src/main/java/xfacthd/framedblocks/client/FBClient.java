package xfacthd.framedblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
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
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.client.ClientUtils;
import xfacthd.framedblocks.api.util.client.ModelCache;
import xfacthd.framedblocks.client.model.*;
import xfacthd.framedblocks.client.model.FluidModel;
import xfacthd.framedblocks.client.render.*;
import xfacthd.framedblocks.client.render.outline.*;
import xfacthd.framedblocks.client.screen.*;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedWeightedPressurePlateBlock;
import xfacthd.framedblocks.common.blockentity.FramedSignBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.client.data.GhostRenderBehaviours;

import java.util.List;
import java.util.Map;

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
                .filter(block -> block instanceof IFramedBlock)
                .toArray(Block[]::new);

        event.register(FramedBlockColor.INSTANCE, blocks);
    }

    @SubscribeEvent
    public static void onOverlayRegister(final RegisterGuiOverlaysEvent event)
    {
        event.registerAboveAll("state_lock", new StateLockOverlay());
    }

    @SubscribeEvent
    public static void onModelRegister(final ModelEvent.RegisterAdditional event)
    {
        event.register(FramedMarkedCubeModel.SLIME_FRAME_LOCATION);
        event.register(FramedMarkedCubeModel.REDSTONE_FRAME_LOCATION);
        FramedMarkedPressurePlateModel.registerFrameModels(event);
        FramedStoneButtonModel.registerFrameModels(event);
        event.register(FluidModel.BARE_MODEL);
    }

    @SubscribeEvent
    public static void onModelsLoaded(final ModelEvent.BakingCompleted event)
    {
        Map<ResourceLocation, BakedModel> registry = event.getModels();

        ModelCache.clear(event.getModelBakery());
        FramedChestRenderer.onModelsLoaded(registry); //Must happen before the chest model is replaced
        FramedMarkedPressurePlateModel.cacheFrameModels(registry);
        FramedStoneButtonModel.cacheFrameModels(registry);

        List<Property<?>> ignoreWaterlogged = List.of(BlockStateProperties.WATERLOGGED);
        List<Property<?>> ignoreWaterloggedLock = List.of(BlockStateProperties.WATERLOGGED, FramedProperties.STATE_LOCKED);
        List<Property<?>> ignoreSolid = List.of(FramedProperties.SOLID, FramedProperties.GLOWING);
        List<Property<?>> ignoreDefault = List.of(BlockStateProperties.WATERLOGGED, FramedProperties.SOLID, FramedProperties.GLOWING);
        List<Property<?>> ignoreDefaultLock = List.of(BlockStateProperties.WATERLOGGED, FramedProperties.SOLID, FramedProperties.GLOWING, FramedProperties.STATE_LOCKED);

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
        ClientUtils.replaceModels(FBContent.blockFramedGate, registry, FramedFenceGateModel::new, List.of(BlockStateProperties.POWERED));
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
        ClientUtils.replaceModels(FBContent.blockFramedVerticalHalfStairs, registry, FramedVerticalHalfStairsModel::new, ignoreWaterlogged);
        ClientUtils.replaceModels(FBContent.blockFramedSlopePanel, registry, FramedSlopePanelModel::new, FramedSlopePanelModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedExtendedSlopePanel, registry, FramedExtendedSlopePanelModel::new, FramedExtendedSlopePanelModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedDoubleSlopePanel, registry, FramedDoubleSlopePanelModel::new, FramedDoubleSlopePanelModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedInverseDoubleSlopePanel, registry, FramedInverseDoubleSlopePanelModel::new, FramedInverseDoubleSlopePanelModel.itemSource(), ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedDoubleStairs, registry, FramedDoubleStairsModel::new, FramedDoubleStairsModel.itemSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedVerticalDoubleStairs, registry, FramedVerticalDoubleStairsModel::new, FramedVerticalDoubleStairsModel.itemSource(), ignoreSolid);
        ClientUtils.replaceModels(FBContent.blockFramedWallBoard, registry, FramedWallBoardModel::new, ignoreDefault);
        ClientUtils.replaceModels(FBContent.blockFramedGlowingCube, registry, FramedGlowingCubeModel::new, ignoreSolid);
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