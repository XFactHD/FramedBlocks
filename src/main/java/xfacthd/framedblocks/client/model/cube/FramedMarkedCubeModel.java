package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.api.util.*;

import java.util.*;

public class FramedMarkedCubeModel extends FramedCubeModel
{
    public static final ResourceLocation SLIME_FRAME_LOCATION = Utils.rl("block/slime_frame");
    public static final ResourceLocation REDSTONE_FRAME_LOCATION = Utils.rl("block/redstone_frame");
    private final BakedModel frameModel;

    public FramedMarkedCubeModel(BlockState state, BakedModel baseModel, Map<ResourceLocation, BakedModel> registry, ResourceLocation frameLocation)
    {
        super(state, baseModel);
        frameModel = registry.get(frameLocation);
    }

    @Override
    protected boolean hasAdditionalQuadsInLayer(RenderType layer) { return layer == RenderType.cutout(); }

    @Override
    protected void getAdditionalQuads(List<BakedQuad> quads, Direction side, BlockState state, Random rand, IModelData data, RenderType layer)
    {
        BlockState camo = data.getData(FramedBlockData.CAMO);
        if (camo != null && !camo.isAir())
        {
            quads.addAll(frameModel.getQuads(state, side, rand, data));
        }
    }



    public static FramedMarkedCubeModel slime(BlockState state, BakedModel baseModel, Map<ResourceLocation, BakedModel> registry)
    {
        return new FramedMarkedCubeModel(state, baseModel, registry, SLIME_FRAME_LOCATION);
    }

    public static FramedMarkedCubeModel redstone(BlockState state, BakedModel baseModel, Map<ResourceLocation, BakedModel> registry)
    {
        return new FramedMarkedCubeModel(state, baseModel, registry, REDSTONE_FRAME_LOCATION);
    }
}
