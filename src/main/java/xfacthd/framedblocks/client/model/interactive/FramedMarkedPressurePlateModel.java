package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.model.util.ModelUtils;

import java.util.*;

public class FramedMarkedPressurePlateModel extends FramedPressurePlateModel
{
    public static final ResourceLocation STONE_FRAME_LOCATION = new ResourceLocation(FramedConstants.MOD_ID, "block/stone_plate_frame");
    public static final ResourceLocation STONE_FRAME_DOWN_LOCATION = new ResourceLocation(FramedConstants.MOD_ID, "block/stone_plate_down_frame");
    public static final ResourceLocation OBSIDIAN_FRAME_LOCATION = new ResourceLocation(FramedConstants.MOD_ID, "block/obsidian_plate_frame");
    public static final ResourceLocation OBSIDIAN_FRAME_DOWN_LOCATION = new ResourceLocation(FramedConstants.MOD_ID, "block/obsidian_plate_down_frame");
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
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        FramedBlockData fbData = extraData.get(FramedBlockData.PROPERTY);
        if (fbData != null && !fbData.getCamoState().isAir())
        {
            return ModelUtils.CUTOUT;
        }
        return ChunkRenderTypeSet.none();
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, RandomSource rand, ModelData data, RenderType layer)
    {
        FramedBlockData fbData = data.get(FramedBlockData.PROPERTY);
        if (fbData == null || fbData.getCamoState().isAir()) { return; }

        quadMap.get(null).addAll(frameModel.getQuads(state, null, rand, data, layer));
        for (Direction side : Direction.values())
        {
            quadMap.get(side).addAll(frameModel.getQuads(state, side, rand, data, layer));
        }
    }

    @Override
    protected boolean useBaseModel() { return true; }



    public static FramedMarkedPressurePlateModel stone(BlockState state, BakedModel baseModel)
    {
        boolean powered = state.getValue(PressurePlateBlock.POWERED);
        ResourceLocation frame = powered ? STONE_FRAME_DOWN_LOCATION : STONE_FRAME_LOCATION;
        return new FramedMarkedPressurePlateModel(state, baseModel, frame, powered);
    }

    public static FramedMarkedPressurePlateModel obsidian(BlockState state, BakedModel baseModel)
    {
        boolean powered = state.getValue(PressurePlateBlock.POWERED);
        ResourceLocation frame = powered ? OBSIDIAN_FRAME_DOWN_LOCATION : OBSIDIAN_FRAME_LOCATION;
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

    public static void registerFrameModels(ModelEvent.RegisterAdditional event)
    {
        event.register(FramedMarkedPressurePlateModel.STONE_FRAME_LOCATION);
        event.register(FramedMarkedPressurePlateModel.STONE_FRAME_DOWN_LOCATION);
        event.register(FramedMarkedPressurePlateModel.OBSIDIAN_FRAME_LOCATION);
        event.register(FramedMarkedPressurePlateModel.OBSIDIAN_FRAME_DOWN_LOCATION);
        event.register(FramedMarkedPressurePlateModel.GOLD_FRAME_LOCATION);
        event.register(FramedMarkedPressurePlateModel.GOLD_FRAME_DOWN_LOCATION);
        event.register(FramedMarkedPressurePlateModel.IRON_FRAME_LOCATION);
        event.register(FramedMarkedPressurePlateModel.IRON_FRAME_DOWN_LOCATION);
    }

    public static void cacheFrameModels(Map<ResourceLocation, BakedModel> registry)
    {
        FRAME_MODELS.clear();

        FRAME_MODELS.put(STONE_FRAME_LOCATION, registry.get(STONE_FRAME_LOCATION));
        FRAME_MODELS.put(STONE_FRAME_DOWN_LOCATION, registry.get(STONE_FRAME_DOWN_LOCATION));
        FRAME_MODELS.put(OBSIDIAN_FRAME_LOCATION, registry.get(OBSIDIAN_FRAME_LOCATION));
        FRAME_MODELS.put(OBSIDIAN_FRAME_DOWN_LOCATION, registry.get(OBSIDIAN_FRAME_DOWN_LOCATION));
        FRAME_MODELS.put(GOLD_FRAME_LOCATION, registry.get(GOLD_FRAME_LOCATION));
        FRAME_MODELS.put(GOLD_FRAME_DOWN_LOCATION, registry.get(GOLD_FRAME_DOWN_LOCATION));
        FRAME_MODELS.put(IRON_FRAME_LOCATION, registry.get(IRON_FRAME_LOCATION));
        FRAME_MODELS.put(IRON_FRAME_DOWN_LOCATION, registry.get(IRON_FRAME_DOWN_LOCATION));
    }
}
