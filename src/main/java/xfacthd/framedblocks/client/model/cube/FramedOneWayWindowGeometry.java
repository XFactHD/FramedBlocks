package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.camo.CamoContent;
import xfacthd.framedblocks.api.model.cache.QuadCacheKey;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.client.data.ConTexDataHandler;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.NullableDirection;

import java.util.Objects;

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
                    Objects.requireNonNullElse(data.get(FramedBlockData.AUX_DATA), ModelData.EMPTY),
                    renderType
            ));
        }
    }

    @Override
    public ModelData getAuxModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData tileData)
    {
        try
        {
            if (state.getValue(PropertyHolder.NULLABLE_FACE) != NullableDirection.NONE)
            {
                return tintedGlassModel.getModelData(level, pos, state, ModelData.EMPTY);
            }
        }
        catch (Throwable ignored) { }
        return ModelData.EMPTY;
    }

    @Override
    public QuadCacheKey makeCacheKey(CamoContent<?> camo, Object ctCtx, ModelData data)
    {
        ModelData auxData = data.get(FramedBlockData.AUX_DATA);
        if (auxData != null)
        {
            Object auxCtCtx = ConTexDataHandler.extractConTexData(auxData);
            if (auxCtCtx != null)
            {
                return new OneWayWindowCacheKey(camo, ctCtx, auxCtCtx);
            }
        }
        return super.makeCacheKey(camo, ctCtx, data);
    }

    @Override
    public boolean useBaseModel()
    {
        return true;
    }



    private record OneWayWindowCacheKey(CamoContent<?> camo, Object ctCtx, Object auxCtCtx) implements QuadCacheKey { }
}
