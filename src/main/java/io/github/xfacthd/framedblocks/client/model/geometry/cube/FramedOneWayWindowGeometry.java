package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.geometry.QuadListModifier;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.NullableDirection;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class FramedOneWayWindowGeometry extends Geometry
{
    private static final BlockState GLASS_STATE = Blocks.TINTED_GLASS.defaultBlockState();

    private final Supplier<BlockStateModel> tintedGlassModel;
    private final NullableDirection face;
    private final QuadListModifier faceFilter;

    public FramedOneWayWindowGeometry(GeometryFactory.Context ctx)
    {
        this.face = ctx.state().getValue(PropertyHolder.NULLABLE_FACE);
        this.tintedGlassModel = ModelUtils.getModelDeferred(GLASS_STATE);
        this.faceFilter = QuadListModifier.filteringCullFace(side -> side != face.toNullableDirection());
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, ModelData modelData) { }

    @Override
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, ModelData data, @Nullable Object cacheKeyUserData)
    {
        if (face != NullableDirection.NONE)
        {
            for (BlockModelPart part : ModelUtils.collectModelParts(tintedGlassModel.get(), level, pos, GLASS_STATE, random, cacheKeyUserData != null))
            {
                consumer.accept(part, GLASS_STATE, true, false, false, false, GLASS_STATE, faceFilter);
            }
        }
    }

    @Override
    @Nullable
    public Object computeCacheKeyUserData(BlockAndTintGetter level, BlockPos pos, RandomSource random, ModelData data)
    {
        BlockStateModel model = tintedGlassModel.get();
        Object geometryKey = model.createGeometryKey(level, pos, GLASS_STATE, random);
        // Only include the geometry key if it's not the SingleVariant's default value (i.e. the model itself)
        return geometryKey != model ? geometryKey : null;
    }

    @Override
    public boolean useBaseModel()
    {
        return true;
    }
}
