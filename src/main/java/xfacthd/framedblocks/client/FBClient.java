package xfacthd.framedblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.model.*;
import xfacthd.framedblocks.client.render.FramedChestRenderer;
import xfacthd.framedblocks.client.render.FramedSignRenderer;
import xfacthd.framedblocks.client.screen.FramedChestScreen;
import xfacthd.framedblocks.client.screen.FramedSignScreen;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.tileentity.*;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

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
                .forEach(block -> ItemBlockRenderTypes.setRenderLayer(block, type ->
                        type == RenderType.solid() ||
                        type == RenderType.cutout() ||
                        type == RenderType.cutoutMipped() ||
                        type == RenderType.translucent()
                ));
        ItemBlockRenderTypes.setRenderLayer(FBContent.blockFramedGhostBlock.get(), RenderType.translucent());

        BlockEntityRenderers.register(FBContent.tileTypeFramedSign.get(), FramedSignRenderer::new);
        BlockEntityRenderers.register(FBContent.tileTypeFramedChest.get(), FramedChestRenderer::new);

        event.enqueueWork(() -> MenuScreens.register(FBContent.containerTypeFramedChest.get(), FramedChestScreen::new));
    }

    @SubscribeEvent
    public static void onBlockColors(final ColorHandlerEvent.Block event)
    {
        Block[] blocks = FBContent.getRegisteredBlocks()
                .stream()
                .map(RegistryObject::get)
                .filter(block -> block instanceof IFramedBlock)
                .toArray(Block[]::new);

        event.getBlockColors().register((state, world, pos, tintIndex) ->
        {
            if (world != null && pos != null)
            {
                if (world.getBlockEntity(pos) instanceof FramedTileEntity te)
                {
                    BlockState camoState = te.getCamoState();
                    if (!camoState.isAir())
                    {
                        int color = event.getBlockColors().getColor(camoState, world, pos, tintIndex);
                        if (color != -1) { return color; }
                    }

                    if (te instanceof FramedDoubleTileEntity dTe)
                    {
                        camoState = dTe.getCamoStateTwo();
                        if (!camoState.isAir())
                        {
                            return event.getBlockColors().getColor(camoState, world, pos, tintIndex);
                        }
                    }
                }
            }
            return -1;
        }, blocks);
    }

    @SubscribeEvent
    public static void onModelsLoaded(final ModelBakeEvent event)
    {
        Map<ResourceLocation, BakedModel> registry = event.getModelRegistry();

        FramedChestRenderer.onModelsLoaded(registry); //Must happen before the chest model is replaced

        replaceModels(FBContent.blockFramedCube, registry, FramedCubeModel::new);
        replaceModels(FBContent.blockFramedSlope, registry, FramedSlopeModel::new, FramedSlopeModel::new);
        replaceModels(FBContent.blockFramedCornerSlope, registry, FramedCornerSlopeModel::new, FramedCornerSlopeModel::new);
        replaceModels(FBContent.blockFramedInnerCornerSlope, registry, FramedInnerCornerSlopeModel::new, FramedInnerCornerSlopeModel::new);
        replaceModels(FBContent.blockFramedPrismCorner, registry, FramedPrismCornerModel::new, FramedPrismCornerModel::new);
        replaceModels(FBContent.blockFramedInnerPrismCorner, registry, FramedInnerPrismCornerModel::new, FramedInnerPrismCornerModel::new);
        replaceModels(FBContent.blockFramedThreewayCorner, registry, FramedThreewayCornerModel::new, FramedThreewayCornerModel::new);
        replaceModels(FBContent.blockFramedInnerThreewayCorner, registry, FramedInnerThreewayCornerModel::new, FramedInnerThreewayCornerModel::new);
        replaceModels(FBContent.blockFramedSlab, registry, FramedSlabModel::new);
        replaceModels(FBContent.blockFramedSlabEdge, registry, FramedSlabEdgeModel::new);
        replaceModels(FBContent.blockFramedSlabCorner, registry, FramedSlabCornerModel::new);
        replaceModels(FBContent.blockFramedPanel, registry, FramedPanelModel::new);
        replaceModels(FBContent.blockFramedCornerPillar, registry, FramedCornerPillarModel::new);
        replaceModels(FBContent.blockFramedStairs, registry, FramedStairsModel::new);
        replaceModels(FBContent.blockFramedWall, registry, FramedWallModel::new);
        replaceModels(FBContent.blockFramedFence, registry, FramedFenceModel::createFenceModel);
        replaceModels(FBContent.blockFramedGate, registry, FramedFenceGateModel::new);
        replaceModels(FBContent.blockFramedDoor, registry, FramedDoorModel::new);
        replaceModels(FBContent.blockFramedTrapDoor, registry, FramedTrapDoorModel::new);
        replaceModels(FBContent.blockFramedPressurePlate, registry, FramedPressurePlateModel::new);
        replaceModels(FBContent.blockFramedLadder, registry, FramedLadderModel::new, FramedLadderModel::new);
        replaceModels(FBContent.blockFramedButton, registry, FramedButtonModel::new);
        replaceModels(FBContent.blockFramedLever, registry, FramedLeverModel::new);
        replaceModels(FBContent.blockFramedSign, registry, FramedSignModel::new);
        replaceModels(FBContent.blockFramedWallSign, registry, FramedWallSignModel::new);
        replaceModels(FBContent.blockFramedDoubleSlab, registry, FramedDoubleSlabModel::new);
        replaceModels(FBContent.blockFramedDoublePanel, registry, FramedDoublePanelModel::new);
        replaceModels(FBContent.blockFramedDoubleSlope, registry, FramedDoubleSlopeModel::new);
        replaceModels(FBContent.blockFramedDoubleCorner, registry, FramedDoubleCornerModel::new, FramedDoubleCornerModel::new);
        replaceModels(FBContent.blockFramedDoublePrismCorner, registry, FramedDoublePrismCornerModel::new, FramedDoublePrismCornerModel::new);
        replaceModels(FBContent.blockFramedDoubleThreewayCorner, registry, FramedDoubleThreewayCornerModel::new, FramedDoubleThreewayCornerModel::new);
        replaceModels(FBContent.blockFramedTorch, registry, FramedTorchModel::new);
        replaceModels(FBContent.blockFramedWallTorch, registry, FramedWallTorchModel::new);
        replaceModels(FBContent.blockFramedSoulTorch, registry, FramedSoulTorchModel::new);
        replaceModels(FBContent.blockFramedSoulWallTorch, registry, FramedSoulWallTorchModel::new);
        replaceModels(FBContent.blockFramedFloor, registry, FramedFloorModel::new);
        replaceModels(FBContent.blockFramedLattice, registry, FramedLatticeModel::new);
        replaceModels(FBContent.blockFramedVerticalStairs, registry, FramedVerticalStairsModel::new);
        replaceModels(FBContent.blockFramedChest, registry, FramedChestModel::new, FramedChestModel::new);

        //Framed Collapsible Block
        //replaceModelsSimple(FBContent.blockFramedCollapsibleBlock, registry, FramedCollapsibleBlockModel::new);
    }

    private static void replaceModels(RegistryObject<Block> block, Map<ResourceLocation, BakedModel> models,
                                      BiFunction<BlockState, BakedModel, BakedModel> blockModelGen)
    {
        replaceModels(block, models, blockModelGen, model -> model);
    }

    private static void replaceModels(RegistryObject<Block> block, Map<ResourceLocation, BakedModel> models,
                                      BiFunction<BlockState, BakedModel, BakedModel> blockModelGen,
                                      Function<BakedModel, BakedModel> itemModelGen)
    {
        for (BlockState state : block.get().getStateDefinition().getPossibleStates())
        {
            ResourceLocation location = BlockModelShaper.stateToModelLocation(state);
            BakedModel baseModel = models.get(location);
            BakedModel replacement = blockModelGen.apply(state, baseModel);
            models.put(location, replacement);
        }

        //noinspection ConstantConditions
        ResourceLocation location = new ModelResourceLocation(block.get().getRegistryName(), "inventory");
        BakedModel replacement = itemModelGen.apply(models.get(location));
        models.put(location, replacement);
    }



    public static void openSignScreen(BlockPos pos)
    {
        //noinspection ConstantConditions
        if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof FramedSignTileEntity te)
        {
            Minecraft.getInstance().setScreen(new FramedSignScreen(te));
        }
    }
}