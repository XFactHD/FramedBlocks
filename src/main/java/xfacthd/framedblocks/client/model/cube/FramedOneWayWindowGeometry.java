package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.NullableDirection;

public class FramedOneWayWindowGeometry extends Geometry
{
    private static final ModelResourceLocation TINTED_GLASS_LOC = BlockModelShaper.stateToModelLocation(
            Blocks.TINTED_GLASS.defaultBlockState()
    );

    private final BakedModel tintedGlassModel;
    private final NullableDirection face;

    public FramedOneWayWindowGeometry(GeometryFactory.Context ctx)
    {
        this.face = ctx.state().getValue(PropertyHolder.NULLABLE_FACE);
        this.tintedGlassModel = ctx.modelLookup().get(TINTED_GLASS_LOC);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad) { }

    @Override
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        if (face != NullableDirection.NONE)
        {
            return ModelUtils.TRANSLUCENT;
        }
        return ChunkRenderTypeSet.none();
    }

    @Override
    public void getAdditionalQuads(
            QuadMap quadMap,
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
    public boolean useBaseModel()
    {
        return true;
    }
}
