package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class FramedBookshelfGeometry extends Geometry
{
    private static final BlockState AUX_SHADER_STATE = Blocks.BOOKSHELF.defaultBlockState();

    private final BlockState state;
    private final BlockStateModel baseModel;
    private final Predicate<Direction> frontFacePred;

    private FramedBookshelfGeometry(GeometryFactory.Context ctx, Predicate<Direction> frontFacePred)
    {
        this.state = ctx.state();
        this.baseModel = ctx.baseModel();
        this.frontFacePred = frontFacePred;
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (Utils.isY(quadDir) || !frontFacePred.test(quadDir))
        {
            return;
        }

        List<BakedQuad> quads = quadMap.get(quadDir);

        QuadModifier.of(quad)
                .apply(Modifiers.cut(Direction.DOWN, 1F/16F))
                .export(quads);

        QuadModifier.of(quad)
                .apply(Modifiers.cut(Direction.UP, 1F/16F))
                .export(quads);

        QuadModifier.of(quad)
                .apply(Modifiers.cut(quadDir.getClockWise(), 1F/16F))
                .apply(Modifiers.cut(Direction.Axis.Y, 15F/16F))
                .export(quads);

        QuadModifier.of(quad)
                .apply(Modifiers.cut(quadDir.getCounterClockWise(), 1F/16F))
                .apply(Modifiers.cut(Direction.Axis.Y, 15F/16F))
                .export(quads);

        QuadModifier.of(quad)
                .apply(Modifiers.cut(Direction.Axis.Y, 9F/16F))
                .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 15F/16F))
                .export(quads);
    }

    @Override
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        consumer.acceptAll(baseModel, level, pos, random, state, false, false, false, false, AUX_SHADER_STATE, null);
    }



    public static FramedBookshelfGeometry normal(GeometryFactory.Context ctx)
    {
        return new FramedBookshelfGeometry(ctx, dir -> true);
    }

    public static FramedBookshelfGeometry chiseled(GeometryFactory.Context ctx)
    {
        Direction facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        return new FramedBookshelfGeometry(ctx, facing::equals);
    }
}
