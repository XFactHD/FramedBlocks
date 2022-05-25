package xfacthd.framedblocks.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.model.*;
import xfacthd.framedblocks.client.render.*;
import xfacthd.framedblocks.client.render.outline.*;
import xfacthd.framedblocks.client.screen.FramedStorageScreen;
import xfacthd.framedblocks.client.screen.FramedSignScreen;
import xfacthd.framedblocks.client.util.BlueprintPropertyOverride;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FBClient
{
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        FBContent.getRegisteredBlocks()
                .stream()
                .map(RegistryObject::get)
                .filter(block -> block instanceof IFramedBlock)
                .forEach(block -> RenderTypeLookup.setRenderLayer(block, type ->
                        type == RenderType.solid() ||
                        type == RenderType.cutout() ||
                        type == RenderType.cutoutMipped() ||
                        type == RenderType.translucent()
                ));
        RenderTypeLookup.setRenderLayer(FBContent.blockFramedGhostBlock.get(), RenderType.translucent());

        ClientRegistry.bindTileEntityRenderer(FBContent.tileTypeFramedSign.get(), FramedSignRenderer::new);
        ClientRegistry.bindTileEntityRenderer(FBContent.tileTypeFramedChest.get(), FramedChestRenderer::new);

        event.enqueueWork(() ->
        {
            ScreenManager.register(FBContent.containerTypeFramedChest.get(), FramedStorageScreen::new);

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
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public static void onBlockColors(final ColorHandlerEvent.Block event)
    {
        //noinspection SuspiciousToArrayCall
        Block[] blocks = FBContent.getRegisteredBlocks()
                .stream()
                .map(RegistryObject::get)
                .filter(block -> block instanceof IFramedBlock)
                .toArray(Block[]::new);

        event.getBlockColors().register((state, world, pos, tintIndex) ->
        {
            if (world != null && pos != null)
            {
                TileEntity te = world.getBlockEntity(pos);
                if (tintIndex < -1)
                {
                    tintIndex = ModelUtils.decodeSecondaryTintIndex(tintIndex);

                    if (te instanceof FramedDoubleTileEntity)
                    {
                        BlockState camoState = ((FramedDoubleTileEntity) te).getCamoStateTwo();
                        if (!camoState.isAir())
                        {
                            return event.getBlockColors().getColor(camoState, world, pos, tintIndex);
                        }
                    }
                    else if (te instanceof FramedFlowerPotTileEntity)
                    {
                        BlockState plantState = ((FramedFlowerPotTileEntity) te).getFlowerBlock().defaultBlockState();
                        if (!plantState.isAir())
                        {
                            return event.getBlockColors().getColor(plantState, world, pos, tintIndex);
                        }
                    }
                }
                else if (te instanceof FramedTileEntity)
                {
                    BlockState camoState = ((FramedTileEntity) te).getCamoState();
                    if (!camoState.isAir())
                    {
                        return event.getBlockColors().getColor(camoState, world, pos, tintIndex);
                    }
                }
            }
            return -1;
        }, blocks);
    }

    @SubscribeEvent
    public static void onModelRegistry(final ModelRegistryEvent event)
    {
        ModelLoader.addSpecialModel(FramedMarkedCubeModel.SLIME_FRAME_LOCATION);
        ModelLoader.addSpecialModel(FramedMarkedCubeModel.REDSTONE_FRAME_LOCATION);
    }

    @SubscribeEvent
    public static void onModelsLoaded(final ModelBakeEvent event)
    {
        Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();

        FramedChestRenderer.onModelsLoaded(registry); //Must happen before the chest model is replaced

        List<Property<?>> ignoreWaterlogged = Collections.singletonList(BlockStateProperties.WATERLOGGED);
        List<Property<?>> ignoreSolid = Collections.singletonList(PropertyHolder.SOLID);
        List<Property<?>> ignoreDefault = Arrays.asList(BlockStateProperties.WATERLOGGED, PropertyHolder.SOLID);

        replaceModels(FBContent.blockFramedCube, registry, FramedCubeModel::new, ignoreSolid);
        replaceModels(FBContent.blockFramedSlope, registry, FramedSlopeModel::new, FramedSlopeModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedCornerSlope, registry, FramedCornerSlopeModel::new, FramedCornerSlopeModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedInnerCornerSlope, registry, FramedInnerCornerSlopeModel::new, FramedInnerCornerSlopeModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedPrismCorner, registry, FramedPrismCornerModel::new, FramedPrismCornerModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedInnerPrismCorner, registry, FramedInnerPrismCornerModel::new, FramedInnerPrismCornerModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedThreewayCorner, registry, FramedThreewayCornerModel::new, FramedThreewayCornerModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedInnerThreewayCorner, registry, FramedInnerThreewayCornerModel::new, FramedInnerThreewayCornerModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedSlab, registry, FramedSlabModel::new, ignoreDefault);
        replaceModels(FBContent.blockFramedSlabEdge, registry, FramedSlabEdgeModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedSlabCorner, registry, FramedSlabCornerModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedPanel, registry, FramedPanelModel::new, ignoreDefault);
        replaceModels(FBContent.blockFramedCornerPillar, registry, FramedCornerPillarModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedStairs, registry, FramedStairsModel::new, ignoreDefault);
        replaceModels(FBContent.blockFramedWall, registry, FramedWallModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedFence, registry, FramedFenceModel::createFenceModel, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedGate, registry, FramedFenceGateModel::new, Collections.singletonList(BlockStateProperties.POWERED));
        replaceModels(FBContent.blockFramedDoor, registry, FramedDoorModel::new, ignoreSolid);
        replaceModels(FBContent.blockFramedTrapDoor, registry, FramedTrapDoorModel::new, ignoreDefault);
        replaceModels(FBContent.blockFramedPressurePlate, registry, FramedPressurePlateModel::new, null);
        replaceModels(FBContent.blockFramedLadder, registry, FramedLadderModel::new, FramedLadderModel.itemSource(), ignoreWaterlogged);
        replaceModels(FBContent.blockFramedButton, registry, FramedButtonModel::new, null);
        replaceModels(FBContent.blockFramedLever, registry, FramedLeverModel::new, null);
        replaceModels(FBContent.blockFramedSign, registry, FramedSignModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedWallSign, registry, FramedWallSignModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedDoubleSlab, registry, FramedDoubleSlabModel::new, ignoreSolid);
        replaceModels(FBContent.blockFramedDoublePanel, registry, FramedDoublePanelModel::new, ignoreSolid);
        replaceModels(FBContent.blockFramedDoubleSlope, registry, FramedDoubleSlopeModel::new, ignoreSolid);
        replaceModels(FBContent.blockFramedDoubleCorner, registry, FramedDoubleCornerModel::new, FramedDoubleCornerModel.itemSource(), ignoreSolid);
        replaceModels(FBContent.blockFramedDoublePrismCorner, registry, FramedDoublePrismCornerModel::new, FramedDoublePrismCornerModel.itemSource(), ignoreSolid);
        replaceModels(FBContent.blockFramedDoubleThreewayCorner, registry, FramedDoubleThreewayCornerModel::new, FramedDoubleThreewayCornerModel.itemSource(), ignoreSolid);
        replaceModels(FBContent.blockFramedTorch, registry, FramedTorchModel::new, null);
        replaceModels(FBContent.blockFramedWallTorch, registry, FramedWallTorchModel::new, null);
        replaceModels(FBContent.blockFramedSoulTorch, registry, FramedSoulTorchModel::new, null);
        replaceModels(FBContent.blockFramedSoulWallTorch, registry, FramedSoulWallTorchModel::new, null);
        replaceModels(FBContent.blockFramedFloor, registry, FramedFloorModel::new, ignoreDefault);
        replaceModels(FBContent.blockFramedLattice, registry, FramedLatticeModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedVerticalStairs, registry, FramedVerticalStairsModel::new, ignoreDefault);
        replaceModels(FBContent.blockFramedChest, registry, FramedChestModel::new, FramedChestModel.itemSource(), ignoreWaterlogged);
        replaceModels(FBContent.blockFramedBars, registry, FramedBarsModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedPane, registry, FramedPaneModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedRailSlope, registry, FramedRailSlopeModel::new, FramedRailSlopeModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedFlowerPot, registry, FramedFlowerPotModel::new, null);
        replaceModels(FBContent.blockFramedPillar, registry, FramedPillarModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedHalfPillar, registry, FramedHalfPillarModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedPost, registry, FramedPillarModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedCollapsibleBlock, registry, FramedCollapsibleBlockModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedHalfStairs, registry, FramedHalfStairsModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedBouncyCube, registry, (state, baseModel) -> FramedMarkedCubeModel.slime(state, baseModel, registry), ignoreSolid);
        replaceModels(FBContent.blockFramedSecretStorage, registry, FramedCubeModel::new, ignoreSolid);
        replaceModels(FBContent.blockFramedRedstoneBlock, registry, (state, baseModel) -> FramedMarkedCubeModel.redstone(state, baseModel, registry), ignoreSolid);
        replaceModels(FBContent.blockFramedPrism, registry, FramedPrismModel::new, FramedPrismModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedSlopedPrism, registry, FramedSlopedPrismModel::new, FramedSlopedPrismModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedSlopeSlab, registry, FramedSlopeSlabModel::new, FramedSlopeSlabModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedElevatedSlopeSlab, registry, FramedElevatedSlopeSlabModel::new, FramedElevatedSlopeSlabModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedDoubleSlopeSlab, registry, FramedDoubleSlopeSlabModel::new, FramedDoubleSlopeSlabModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedInverseDoubleSlopeSlab, registry, FramedInverseDoubleSlopeSlabModel::new, FramedInverseDoubleSlopeSlabModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedVerticalHalfStairs, registry, FramedVerticalHalfStairsModel::new, ignoreWaterlogged);
        replaceModels(FBContent.blockFramedSlopePanel, registry, FramedSlopePanelModel::new, FramedSlopePanelModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedExtendedSlopePanel, registry, FramedExtendedSlopePanelModel::new, FramedExtendedSlopePanelModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedDoubleSlopePanel, registry, FramedDoubleSlopePanelModel::new, FramedDoubleSlopePanelModel.itemSource(), ignoreDefault);
        replaceModels(FBContent.blockFramedInverseDoubleSlopePanel, registry, FramedInverseDoubleSlopePanelModel::new, FramedInverseDoubleSlopePanelModel.itemSource(), ignoreDefault);
    }

    private static void replaceModels(RegistryObject<Block> block, Map<ResourceLocation, IBakedModel> models,
                                      BiFunction<BlockState, IBakedModel, IBakedModel> blockModelGen,
                                      @Nullable List<Property<?>> ignoredProps)
    {
        replaceModels(block, models, blockModelGen, null, ignoredProps);
    }

    private static void replaceModels(RegistryObject<Block> block, Map<ResourceLocation, IBakedModel> models,
                                      BiFunction<BlockState, IBakedModel, IBakedModel> blockModelGen,
                                      @Nullable BlockState itemModelSource,
                                      @Nullable List<Property<?>> ignoredProps)
    {
        Map<BlockState, IBakedModel> visitedStates = new HashMap<>();

        for (BlockState state : block.get().getStateDefinition().getPossibleStates())
        {
            ResourceLocation location = BlockModelShapes.stateToModelLocation(state);
            IBakedModel baseModel = models.get(location);
            IBakedModel replacement = visitedStates.computeIfAbsent(
                    ignoreProps(state, ignoredProps),
                    key -> blockModelGen.apply(key, baseModel)
            );
            models.put(location, replacement);
        }

        if (itemModelSource != null)
        {
            //noinspection ConstantConditions
            ResourceLocation location = new ModelResourceLocation(block.get().getRegistryName(), "inventory");
            IBakedModel replacement = models.get(BlockModelShapes.stateToModelLocation(itemModelSource));
            models.put(location, replacement);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static BlockState ignoreProps(BlockState state, @Nullable List<Property<?>> ignoredProps)
    {
        if (ignoredProps == null || ignoredProps.isEmpty()) { return state; }

        BlockState defaultState = state.getBlock().defaultBlockState();
        for (Property prop : ignoredProps)
        {
            if (!state.hasProperty(prop))
            {
                FramedBlocks.LOGGER.warn("Found invalid ignored property {} for block {}!", prop, state.getBlock());
                continue;
            }
            state = state.setValue(prop, defaultState.getValue(prop));
        }

        return state;
    }



    public static void openSignScreen(BlockPos pos)
    {
        //noinspection ConstantConditions
        TileEntity te = Minecraft.getInstance().level.getBlockEntity(pos);
        if (te instanceof FramedSignTileEntity)
        {
            Minecraft.getInstance().setScreen(new FramedSignScreen((FramedSignTileEntity)te));
        }
    }
}