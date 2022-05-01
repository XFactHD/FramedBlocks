package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.FramedBlockData;

import java.util.*;

public class FramedMarkedCubeModel extends FramedCubeModel
{
    public static final ResourceLocation SLIME_FRAME_LOCATION = new ResourceLocation(FramedBlocks.MODID, "block/slime_frame");
    public static final ResourceLocation REDSTONE_FRAME_LOCATION = new ResourceLocation(FramedBlocks.MODID, "block/redstone_frame");
    private final IBakedModel frameModel;

    public FramedMarkedCubeModel(BlockState state, IBakedModel baseModel, Map<ResourceLocation, IBakedModel> registry, ResourceLocation frameLocation)
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
        //noinspection deprecation
        if (camo != null && !camo.isAir())
        {
            quads.addAll(frameModel.getQuads(state, side, rand, data));
        }
    }



    public static FramedMarkedCubeModel slime(BlockState state, IBakedModel baseModel, Map<ResourceLocation, IBakedModel> registry)
    {
        return new FramedMarkedCubeModel(state, baseModel, registry, SLIME_FRAME_LOCATION);
    }

    public static FramedMarkedCubeModel redstone(BlockState state, IBakedModel baseModel, Map<ResourceLocation, IBakedModel> registry)
    {
        return new FramedMarkedCubeModel(state, baseModel, registry, REDSTONE_FRAME_LOCATION);
    }
}
