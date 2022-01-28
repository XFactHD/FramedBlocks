package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.util.FramedBlockData;

import java.util.*;

public class FramedBouncyCubeModel extends FramedCubeModel
{
    public static final ResourceLocation FRAME_LOCATION = new ResourceLocation(FramedBlocks.MODID, "block/slime_frame");
    private final BakedModel frameModel;

    public FramedBouncyCubeModel(BlockState state, BakedModel baseModel, Map<ResourceLocation, BakedModel> registry)
    {
        super(state, baseModel);
        frameModel = registry.get(FRAME_LOCATION);
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
}
