package xfacthd.framedblocks.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.model.*;
import xfacthd.framedblocks.client.render.FramedSignRenderer;
import xfacthd.framedblocks.client.screen.FramedSignScreen;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.tileentity.FramedSignTileEntity;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FBClient
{
    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        ForgeRegistries.BLOCKS.getValues()
                .stream()
                .filter(block -> block.getRegistryName().getNamespace().equals(FramedBlocks.MODID))
                .forEach(block -> RenderTypeLookup.setRenderLayer(block, type ->
                        type == RenderType.getSolid() || type == RenderType.getCutout() || type == RenderType.getCutoutMipped()));

        ClientRegistry.bindTileEntityRenderer(FBContent.tileTypeFramedSign, FramedSignRenderer::new);
    }

    @SubscribeEvent
    @SuppressWarnings({"ConstantConditions", "deprecation"})
    public static void onBlockColors(final ColorHandlerEvent.Block event)
    {
        Block[] blocks = ForgeRegistries.BLOCKS.getValues()
                .stream()
                .filter(block -> block.getRegistryName().getNamespace().equals(FramedBlocks.MODID))
                .toArray(Block[]::new);

        event.getBlockColors().register((state, world, pos, tintIndex) ->
        {
            if (world != null && pos != null)
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof FramedTileEntity)
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
    public static void onModelsLoaded(final ModelBakeEvent event)
    {
        Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();

        //Framed Cube
        replaceModelsSimple(FBContent.blockFramedCube, registry);

        //Framed Slope
        replaceModelsAdvanced(FBContent.blockFramedSlope, registry,
                (state, baseModel) ->
                {
                    Direction dir = state.get(PropertyHolder.FACING_HOR);
                    SlopeType type = state.get(PropertyHolder.SLOPE_TYPE);
                    return new FramedSlopeModel(baseModel, dir, type);
                },
                baseModel -> new FramedSlopeModel(baseModel, Direction.SOUTH, SlopeType.BOTTOM));

        //Framed Corner Slope
        replaceModelsAdvanced(FBContent.blockFramedCornerSlope, registry,
                (state, baseModel) ->
                {
                    Direction dir = state.get(PropertyHolder.FACING_HOR);
                    CornerType type = state.get(PropertyHolder.CORNER_TYPE);
                    return new FramedCornerSlopeModel(baseModel, dir, type);
                },
                baseModel -> new FramedCornerSlopeModel(baseModel, Direction.SOUTH, CornerType.BOTTOM));

        //Framed Inner Corner Slope
        replaceModelsAdvanced(FBContent.blockFramedInnerCornerSlope, registry,
                (state, baseModel) ->
                {
                    Direction dir = state.get(PropertyHolder.FACING_HOR);
                    CornerType type = state.get(PropertyHolder.CORNER_TYPE);
                    return new FramedInnerCornerSlopeModel(baseModel, dir, type);
                },
                baseModel -> new FramedInnerCornerSlopeModel(baseModel, Direction.SOUTH, CornerType.BOTTOM)
        );

        //Framed Prism Corner
        replaceModelsAdvanced(FBContent.blockFramedPrismCorner, registry,
                (state, baseModel) ->
                {
                    Direction dir = state.get(PropertyHolder.FACING_HOR);
                    boolean top = state.get(PropertyHolder.TOP);
                    return new FramedPrismCornerModel(baseModel, dir, top);
                },
                baseModel -> new FramedPrismCornerModel(baseModel, Direction.SOUTH, false)
        );

        //Framed Inner Prism Corner
        replaceModelsAdvanced(FBContent.blockFramedInnerPrismCorner, registry,
                (state, baseModel) ->
                {
                    Direction dir = state.get(PropertyHolder.FACING_HOR);
                    boolean top = state.get(PropertyHolder.TOP);
                    return new FramedInnerPrismCornerModel(baseModel, dir, top);
                },
                baseModel -> new FramedInnerPrismCornerModel(baseModel, Direction.SOUTH, false)
        );

        //Framed Threeway Corner
        replaceModelsAdvanced(FBContent.blockFramedThreewayCorner, registry,
                (state, baseModel) ->
                {
                    Direction dir = state.get(PropertyHolder.FACING_HOR);
                    boolean top = state.get(PropertyHolder.TOP);
                    return new FramedThreewayCornerModel(baseModel, dir, top);
                },
                baseModel -> new FramedThreewayCornerModel(baseModel, Direction.SOUTH, false)
        );

        //Framed Inner Threeway Corner
        replaceModelsAdvanced(FBContent.blockFramedInnerThreewayCorner, registry,
                (state, baseModel) ->
                {
                    Direction dir = state.get(PropertyHolder.FACING_HOR);
                    boolean top = state.get(PropertyHolder.TOP);
                    return new FramedInnerThreewayCornerModel(baseModel, dir, top);
                },
                baseModel -> new FramedInnerThreewayCornerModel(baseModel, Direction.SOUTH, false)
        );

        //Framed Slab
        replaceModelsSimple(FBContent.blockFramedSlab, registry);

        //Framed Panel
        replaceModelsSimple(FBContent.blockFramedPanel, registry);

        //Framed Corner Pillar
        replaceModelsSimple(FBContent.blockFramedCornerPillar, registry);

        //Framed Stairs
        replaceModelsSimple(FBContent.blockFramedStairs, registry);

        //Framed Wall
        replaceModelsSimple(FBContent.blockFramedWall, registry);

        //Framed Fence
        replaceModelsSimple(FBContent.blockFramedFence, registry);

        //Framed Gate
        replaceModelsSimple(FBContent.blockFramedGate, registry);

        //Framed Door
        replaceModelsSimple(FBContent.blockFramedDoor, registry);

        //Framed Trapdoor
        replaceModelsSimple(FBContent.blockFramedTrapDoor, registry);

        //Framed Pressure Plate
        replaceModelsSimple(FBContent.blockFramedPressurePlate, registry);

        //Framed Ladder
        replaceModelsSimple(FBContent.blockFramedLadder, registry);

        //Framed Button
        replaceModelsSimple(FBContent.blockFramedButton, registry);

        //Framed Lever
        replaceModelsSimple(FBContent.blockFramedLever, registry, FramedLeverModel::new);

        //Framed Sign
        replaceModelsSimple(FBContent.blockFramedSign, registry);

        //Framed Wall Sign
        replaceModelsSimple(FBContent.blockFramedWallSign, registry);

        //Framed Collapsible Block
        //replaceModelsSimple(FBContent.blockFramedCollapsibleBlock, registry, FramedCollapsibleBlockModel::new);
    }

    private static void replaceModelsSimple(Block block, Map<ResourceLocation, IBakedModel> models)
    {
        replaceModelsSimple(block, models, FramedBlockModel::new);
    }

    private static void replaceModelsSimple(Block block, Map<ResourceLocation, IBakedModel> models,
                                            BiFunction<BlockType, IBakedModel, IBakedModel> blockModelGen)
    {
        BlockType type = ((IFramedBlock)block).getBlockType();
        for (BlockState state : block.getStateContainer().getValidStates())
        {
            ResourceLocation location = BlockModelShapes.getModelLocation(state);
            IBakedModel baseModel = models.get(location);
            IBakedModel replacement = blockModelGen.apply(type, baseModel);
            models.put(location, replacement);
        }
    }

    private static void replaceModelsAdvanced(Block block, Map<ResourceLocation, IBakedModel> models,
                                              BiFunction<BlockState, IBakedModel, IBakedModel> blockModelGen,
                                              Function<IBakedModel, IBakedModel> itemModelGen)
    {
        for (BlockState state : block.getStateContainer().getValidStates())
        {
            ResourceLocation location = BlockModelShapes.getModelLocation(state);
            IBakedModel baseModel = models.get(location);
            IBakedModel replacement = blockModelGen.apply(state, baseModel);
            models.put(location, replacement);
        }

        //noinspection ConstantConditions
        ResourceLocation location = new ModelResourceLocation(block.getRegistryName(), "inventory");
        IBakedModel baseModel = models.get(location);
        IBakedModel replacement = itemModelGen.apply(baseModel);
        models.put(location, replacement);
    }



    public static void openSignScreen(BlockPos pos)
    {
        //noinspection ConstantConditions
        TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
        if (te instanceof FramedSignTileEntity)
        {
            Minecraft.getInstance().displayGuiScreen(new FramedSignScreen((FramedSignTileEntity)te));
        }
    }
}