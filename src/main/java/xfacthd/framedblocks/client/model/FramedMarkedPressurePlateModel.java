package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.api.util.FramedBlockData;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;

import java.util.*;

public class FramedMarkedPressurePlateModel extends FramedPressurePlateModel
{
    public static final ResourceLocation STONE_FRAME_LOCATION = new ResourceLocation(FramedConstants.MOD_ID, "block/stone_plate_frame");
    public static final ResourceLocation STONE_FRAME_DOWN_LOCATION = new ResourceLocation(FramedConstants.MOD_ID, "block/stone_plate_down_frame");
    public static final ResourceLocation GOLD_FRAME_LOCATION = new ResourceLocation(FramedConstants.MOD_ID, "block/gold_plate_frame");
    public static final ResourceLocation GOLD_FRAME_DOWN_LOCATION = new ResourceLocation(FramedConstants.MOD_ID, "block/gold_plate_down_frame");
    public static final ResourceLocation IRON_FRAME_LOCATION = new ResourceLocation(FramedConstants.MOD_ID, "block/iron_plate_frame");
    public static final ResourceLocation IRON_FRAME_DOWN_LOCATION = new ResourceLocation(FramedConstants.MOD_ID, "block/iron_plate_down_frame");
    private static final Map<ResourceLocation, BakedModel> FRAME_MODELS = new HashMap<>();

    private final BakedModel frameModel;

    private FramedMarkedPressurePlateModel(BlockState state, BakedModel baseModel, ResourceLocation frameLocation, boolean powered)
    {
        super(state, baseModel, powered);
        frameModel = FRAME_MODELS.get(frameLocation);
    }

    @Override
    protected boolean hasAdditionalQuadsInLayer(RenderType layer) { return layer == RenderType.cutout(); }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, Random rand, IModelData data, RenderType layer)
    {
        BlockState camo = data.getData(FramedBlockData.CAMO);
        if (camo != null && !camo.isAir())
        {
            quadMap.get(null).addAll(frameModel.getQuads(state, null, rand, data));
            for (Direction side : Direction.values())
            {
                quadMap.get(side).addAll(frameModel.getQuads(state, side, rand, data));
            }
        }
    }

    @Override
    protected BakedModel getCamoModel(BlockState camoState)
    {
        if (camoState.is(FBContent.blockFramedCube.get()))
        {
            return baseModel;
        }
        return super.getCamoModel(camoState);
    }

    @Override
    protected boolean forceUngeneratedBaseModel() { return true; }



    public static FramedMarkedPressurePlateModel stone(BlockState state, BakedModel baseModel)
    {
        boolean powered = state.getValue(PressurePlateBlock.POWERED);
        ResourceLocation frame = powered ? STONE_FRAME_DOWN_LOCATION : STONE_FRAME_LOCATION;
        return new FramedMarkedPressurePlateModel(state, baseModel, frame, powered);
    }

    public static FramedMarkedPressurePlateModel gold(BlockState state, BakedModel baseModel)
    {
        boolean powered = state.getValue(WeightedPressurePlateBlock.POWER) > 0;
        ResourceLocation frame = powered ? GOLD_FRAME_DOWN_LOCATION : GOLD_FRAME_LOCATION;
        return new FramedMarkedPressurePlateModel(state, baseModel, frame, powered);
    }

    public static FramedMarkedPressurePlateModel iron(BlockState state, BakedModel baseModel)
    {
        boolean powered = state.getValue(WeightedPressurePlateBlock.POWER) > 0;
        ResourceLocation frame = powered ? IRON_FRAME_DOWN_LOCATION : IRON_FRAME_LOCATION;
        return new FramedMarkedPressurePlateModel(state, baseModel, frame, powered);
    }

    public static void registerFrameModels()
    {
        ForgeModelBakery.addSpecialModel(FramedMarkedPressurePlateModel.STONE_FRAME_LOCATION);
        ForgeModelBakery.addSpecialModel(FramedMarkedPressurePlateModel.STONE_FRAME_DOWN_LOCATION);
        ForgeModelBakery.addSpecialModel(FramedMarkedPressurePlateModel.GOLD_FRAME_LOCATION);
        ForgeModelBakery.addSpecialModel(FramedMarkedPressurePlateModel.GOLD_FRAME_DOWN_LOCATION);
        ForgeModelBakery.addSpecialModel(FramedMarkedPressurePlateModel.IRON_FRAME_LOCATION);
        ForgeModelBakery.addSpecialModel(FramedMarkedPressurePlateModel.IRON_FRAME_DOWN_LOCATION);
    }

    public static void cacheFrameModels(Map<ResourceLocation, BakedModel> registry)
    {
        FRAME_MODELS.clear();

        FRAME_MODELS.put(STONE_FRAME_LOCATION, registry.get(STONE_FRAME_LOCATION));
        FRAME_MODELS.put(STONE_FRAME_DOWN_LOCATION, registry.get(STONE_FRAME_DOWN_LOCATION));
        FRAME_MODELS.put(GOLD_FRAME_LOCATION, registry.get(GOLD_FRAME_LOCATION));
        FRAME_MODELS.put(GOLD_FRAME_DOWN_LOCATION, registry.get(GOLD_FRAME_DOWN_LOCATION));
        FRAME_MODELS.put(IRON_FRAME_LOCATION, registry.get(IRON_FRAME_LOCATION));
        FRAME_MODELS.put(IRON_FRAME_DOWN_LOCATION, registry.get(IRON_FRAME_DOWN_LOCATION));
    }
}
