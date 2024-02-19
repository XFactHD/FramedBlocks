package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.util.ClientConfig;

import java.util.*;

public class FramedMarkedCubeModel extends FramedCubeBaseModel
{
    public static final ResourceLocation SLIME_FRAME_LOCATION = Utils.rl("block/slime_frame");
    public static final ResourceLocation REDSTONE_FRAME_LOCATION = Utils.rl("block/redstone_frame");
    private final BakedModel frameModel;

    public FramedMarkedCubeModel(
            BlockState state,
            BakedModel baseModel,
            Map<ResourceLocation, BakedModel> registry,
            ResourceLocation frameLocation
    )
    {
        super(state, baseModel);
        frameModel = registry.get(frameLocation);
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
    protected void getAdditionalQuads(
            ArrayList<BakedQuad> quads,
            Direction side,
            BlockState state,
            RandomSource rand,
            ModelData data,
            RenderType renderType
    )
    {
        FramedBlockData fbData = data.get(FramedBlockData.PROPERTY);
        if (fbData != null && !fbData.getCamoState().isAir())
        {
            Utils.copyAll(frameModel.getQuads(state, side, rand, data, renderType), quads);
        }
    }



    public static FramedCubeBaseModel slime(
            BlockState state, BakedModel baseModel, Map<ResourceLocation, BakedModel> registry
    )
    {
        if (ClientConfig.showSpecialCubeOverlay)
        {
            return new FramedMarkedCubeModel(state, baseModel, registry, SLIME_FRAME_LOCATION);
        }
        return new FramedCubeBaseModel(state, baseModel);
    }

    public static FramedCubeBaseModel redstone(
            BlockState state, BakedModel baseModel, Map<ResourceLocation, BakedModel> registry
    )
    {
        if (ClientConfig.showSpecialCubeOverlay)
        {
            return new FramedMarkedCubeModel(state, baseModel, registry, REDSTONE_FRAME_LOCATION);
        }
        return new FramedCubeBaseModel(state, baseModel);
    }
}
