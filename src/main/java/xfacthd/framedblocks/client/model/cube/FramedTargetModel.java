package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

import java.util.*;

public class FramedTargetModel extends FramedBlockModel
{
    public static final ResourceLocation OVERLAY_LOCATION = Utils.rl("block/target_overlay");
    public static final int OVERLAY_TINT_IDX = 1024;
    private static BakedModel overlayModel;

    public FramedTargetModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad) { }

    @Override
    protected boolean hasAdditionalQuadsInLayer(RenderType layer) { return layer == RenderType.cutout(); }

    @Override
    protected void getAdditionalQuads(List<BakedQuad> quads, Direction side, BlockState state, Random rand, IModelData data, RenderType layer)
    {
        if (layer == RenderType.cutout())
        {
            quads.addAll(overlayModel.getQuads(state, side, rand, data));
        }
    }



    public static BlockState itemSource() { return FBContent.blockFramedTarget.get().defaultBlockState(); }

    public static void cacheOverlayModel(Map<ResourceLocation, BakedModel> registry)
    {
        overlayModel = registry.get(OVERLAY_LOCATION);
    }
}
