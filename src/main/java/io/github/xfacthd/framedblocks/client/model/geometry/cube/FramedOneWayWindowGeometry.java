package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.geometry.QuadListModifier;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.NullableDirection;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class FramedOneWayWindowGeometry extends Geometry {
    private static final BlockState GLASS_STATE = Blocks.TINTED_GLASS.defaultBlockState();

    private final Supplier<BlockStateModel> tintedGlassModel;
    private final NullableDirection face;
    private final QuadListModifier faceFilter;

    public FramedOneWayWindowGeometry(GeometryFactory.Context ctx) {
        this.face = ctx.state().getValue(PropertyHolder.NULLABLE_FACE);
        this.tintedGlassModel = ModelUtils.getModelDeferred(GLASS_STATE);
        this.faceFilter = QuadListModifier.filteringCullFace(side -> side != face.toNullableDirection());
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) { }

    @Override
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        if (face != NullableDirection.NONE) {
            if (cacheKeyUserData == null) {
                level = BlockAndTintGetter.EMPTY;
                pos = BlockPos.ZERO;
            }
            consumer.acceptAll(tintedGlassModel.get(), level, pos, random, GLASS_STATE, true, false, false, GLASS_STATE, faceFilter);
        }
    }

    @Override
    public @Nullable Object computeCacheKeyUserData(BlockAndTintGetter level, BlockPos pos, RandomSource random, ModelData data) {
        if (face != NullableDirection.NONE) {
            return ModelUtils.getGeometryKeyFiltered(tintedGlassModel.get(), level, pos, GLASS_STATE, random);
        }
        return null;
    }

    @Override
    public boolean useBaseModel() {
        return true;
    }

    @Override
    public int getMaterialFlags(BlockAndTintGetter level, BlockPos pos, ModelData modelData, FramedBlockData blockData) {
        return face != NullableDirection.NONE ? tintedGlassModel.get().materialFlags(level, pos, GLASS_STATE) : 0;
    }
}
