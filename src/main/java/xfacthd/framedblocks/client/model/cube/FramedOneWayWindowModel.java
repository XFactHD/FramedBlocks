package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.NullableDirection;

import java.util.*;

public class FramedOneWayWindowModel extends FramedBlockModel
{
    private static BakedModel tintedGlassModel;

    private final NullableDirection face;

    public FramedOneWayWindowModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.face = state.getValue(PropertyHolder.NULLABLE_FACE);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad) { }

    @Override
    protected boolean hasAdditionalQuadsInLayer(RenderType layer)
    {
        return face != NullableDirection.NONE && layer == RenderType.translucent();
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, Random rand, IModelData data, RenderType renderType)
    {
        if (face != NullableDirection.NONE && renderType == RenderType.translucent())
        {
            Direction side = face.toDirection();
            quadMap.get(side).addAll(tintedGlassModel.getQuads(
                    Blocks.TINTED_GLASS.defaultBlockState(),
                    side,
                    rand,
                    EmptyModelData.INSTANCE
            ));
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



    public static void captureTintedGlassModel(Map<ResourceLocation, BakedModel> models)
    {
        ResourceLocation loc = BlockModelShaper.stateToModelLocation(Blocks.TINTED_GLASS.defaultBlockState());
        tintedGlassModel = models.get(loc);
    }
}
