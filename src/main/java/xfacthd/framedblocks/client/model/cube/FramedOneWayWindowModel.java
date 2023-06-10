package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.NullableDirection;

import java.util.List;
import java.util.Map;

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
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        if (face != NullableDirection.NONE)
        {
            return ModelUtils.TRANSLUCENT;
        }
        return ChunkRenderTypeSet.none();
    }

    @Override
    protected void getAdditionalQuads(
            Map<Direction, List<BakedQuad>> quadMap,
            BlockState state,
            RandomSource rand,
            ModelData data,
            RenderType renderType
    )
    {
        if (face != NullableDirection.NONE && renderType == RenderType.translucent())
        {
            Direction side = face.toDirection();
            quadMap.get(side).addAll(tintedGlassModel.getQuads(
                    Blocks.TINTED_GLASS.defaultBlockState(),
                    side,
                    rand,
                    ModelData.EMPTY,
                    renderType
            ));
        }
    }

    @Override
    protected boolean useBaseModel()
    {
        return true;
    }



    public static void captureTintedGlassModel(Map<ResourceLocation, BakedModel> models)
    {
        ResourceLocation loc = BlockModelShaper.stateToModelLocation(Blocks.TINTED_GLASS.defaultBlockState());
        tintedGlassModel = models.get(loc);
    }
}
