package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public class FramedBookshelfGeometry extends Geometry {
    private static final BlockState AUX_SHADER_STATE = Blocks.BOOKSHELF.defaultBlockState();

    private final BlockState state;
    private final BlockStateModel baseModel;
    private final Predicate<Direction> frontFacePred;

    private FramedBookshelfGeometry(GeometryFactory.Context ctx, Predicate<Direction> frontFacePred) {
        this.state = ctx.state();
        this.baseModel = ctx.baseModel();
        this.frontFacePred = frontFacePred;
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction quadDir = quad.direction();
        if (DirUtils.isY(quadDir) || !frontFacePred.test(quadDir)) {
            return;
        }

        QuadModifier.of(quad)
                .apply(Modifiers.cut(Direction.DOWN, 1F/16F))
                .export(quadMap, quadDir);

        QuadModifier.of(quad)
                .apply(Modifiers.cut(Direction.UP, 1F/16F))
                .export(quadMap, quadDir);

        QuadModifier.of(quad)
                .apply(Modifiers.cut(quadDir.getClockWise(), 1F/16F))
                .apply(Modifiers.cut(Direction.Axis.Y, 15F/16F))
                .export(quadMap, quadDir);

        QuadModifier.of(quad)
                .apply(Modifiers.cut(quadDir.getCounterClockWise(), 1F/16F))
                .apply(Modifiers.cut(Direction.Axis.Y, 15F/16F))
                .export(quadMap, quadDir);

        QuadModifier.of(quad)
                .apply(Modifiers.cut(Direction.Axis.Y, 9F/16F))
                .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 15F/16F))
                .export(quadMap, quadDir);
    }

    @Override
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        consumer.acceptAll(baseModel, level, pos, random, state, false, false, false, AUX_SHADER_STATE, null);
    }

    public static FramedBookshelfGeometry normal(GeometryFactory.Context ctx) {
        return new FramedBookshelfGeometry(ctx, _ -> true);
    }

    public static FramedBookshelfGeometry chiseled(GeometryFactory.Context ctx) {
        Direction facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        return new FramedBookshelfGeometry(ctx, facing::equals);
    }
}
