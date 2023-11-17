package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.api.util.Utils;

public class FramedMarkedPressurePlateGeometry extends FramedPressurePlateGeometry
{
    public static final ResourceLocation STONE_FRAME_LOCATION = Utils.rl("block/stone_plate_frame");
    public static final ResourceLocation STONE_FRAME_DOWN_LOCATION = Utils.rl("block/stone_plate_down_frame");
    public static final ResourceLocation OBSIDIAN_FRAME_LOCATION = Utils.rl("block/obsidian_plate_frame");
    public static final ResourceLocation OBSIDIAN_FRAME_DOWN_LOCATION = Utils.rl("block/obsidian_plate_down_frame");
    public static final ResourceLocation GOLD_FRAME_LOCATION = Utils.rl("block/gold_plate_frame");
    public static final ResourceLocation GOLD_FRAME_DOWN_LOCATION = Utils.rl("block/gold_plate_down_frame");
    public static final ResourceLocation IRON_FRAME_LOCATION = Utils.rl("block/iron_plate_frame");
    public static final ResourceLocation IRON_FRAME_DOWN_LOCATION = Utils.rl("block/iron_plate_down_frame");

    private final BakedModel frameModel;

    private FramedMarkedPressurePlateGeometry(BakedModel frameModel, boolean powered)
    {
        super(powered);
        this.frameModel = frameModel;
    }

    @Override
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        FramedBlockData fbData = extraData.get(FramedBlockData.PROPERTY);
        if (fbData != null && !fbData.getCamoState().isAir())
        {
            return ModelUtils.CUTOUT;
        }
        return ChunkRenderTypeSet.none();
    }

    @Override
    public void getAdditionalQuads(
            QuadMap quadMap,
            BlockState state,
            RandomSource rand,
            ModelData data,
            RenderType layer
    )
    {
        FramedBlockData fbData = data.get(FramedBlockData.PROPERTY);
        if (fbData == null || fbData.getCamoState().isAir())
        {
            return;
        }

        quadMap.get(null).addAll(frameModel.getQuads(state, null, rand, data, layer));
        for (Direction side : Direction.values())
        {
            quadMap.get(side).addAll(frameModel.getQuads(state, side, rand, data, layer));
        }
    }

    @Override
    public boolean useBaseModel()
    {
        return true;
    }



    public static FramedMarkedPressurePlateGeometry stone(GeometryFactory.Context ctx)
    {
        boolean powered = ctx.state().getValue(PressurePlateBlock.POWERED);
        BakedModel frame = ctx.modelAccessor().get(powered ? STONE_FRAME_DOWN_LOCATION : STONE_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(frame, powered);
    }

    public static FramedMarkedPressurePlateGeometry obsidian(GeometryFactory.Context ctx)
    {
        boolean powered = ctx.state().getValue(PressurePlateBlock.POWERED);
        BakedModel frame = ctx.modelAccessor().get(powered ? OBSIDIAN_FRAME_DOWN_LOCATION : OBSIDIAN_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(frame, powered);
    }

    public static FramedMarkedPressurePlateGeometry gold(GeometryFactory.Context ctx)
    {
        boolean powered = ctx.state().getValue(WeightedPressurePlateBlock.POWER) > 0;
        BakedModel frame = ctx.modelAccessor().get(powered ? GOLD_FRAME_DOWN_LOCATION : GOLD_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(frame, powered);
    }

    public static FramedMarkedPressurePlateGeometry iron(GeometryFactory.Context ctx)
    {
        boolean powered = ctx.state().getValue(WeightedPressurePlateBlock.POWER) > 0;
        BakedModel frame = ctx.modelAccessor().get(powered ? IRON_FRAME_DOWN_LOCATION : IRON_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(frame, powered);
    }

    public static void registerFrameModels(ModelEvent.RegisterAdditional event)
    {
        event.register(FramedMarkedPressurePlateGeometry.STONE_FRAME_LOCATION);
        event.register(FramedMarkedPressurePlateGeometry.STONE_FRAME_DOWN_LOCATION);
        event.register(FramedMarkedPressurePlateGeometry.OBSIDIAN_FRAME_LOCATION);
        event.register(FramedMarkedPressurePlateGeometry.OBSIDIAN_FRAME_DOWN_LOCATION);
        event.register(FramedMarkedPressurePlateGeometry.GOLD_FRAME_LOCATION);
        event.register(FramedMarkedPressurePlateGeometry.GOLD_FRAME_DOWN_LOCATION);
        event.register(FramedMarkedPressurePlateGeometry.IRON_FRAME_LOCATION);
        event.register(FramedMarkedPressurePlateGeometry.IRON_FRAME_DOWN_LOCATION);
    }
}
