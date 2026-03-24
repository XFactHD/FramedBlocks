package io.github.xfacthd.framedblocks.client.model.geometry.interactive;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class FramedWallHangingSignGeometry extends Geometry
{
    private static final BlockState AUX_SHADER_STATE = Blocks.OAK_WALL_HANGING_SIGN.defaultBlockState();

    private final BlockState state;
    private final BlockStateModel baseModel;
    private final Direction dir;

    public FramedWallHangingSignGeometry(GeometryFactory.Context ctx)
    {
        this.state = ctx.state();
        this.baseModel = ctx.baseModel();
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (quadDir.getAxis() == dir.getAxis())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.UP, 10F/16F))
                    .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 15F/16F))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.DOWN, 2F/16F))
                    .apply(Modifiers.setPosition(10F/16F))
                    .export(quadMap, null);
        }
        else if (quadDir.getAxis() == dir.getClockWise().getAxis())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.UP, 10F/16F))
                    .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 9F/16F))
                    .apply(Modifiers.setPosition(15F/16F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.DOWN, 2F/16F))
                    .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 10F/16F))
                    .export(quadMap, quadDir);
        }
        else
        {
            boolean up = quadDir == Direction.UP;
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getAxis(), 9F/16F))
                    .apply(Modifiers.cut(dir.getClockWise().getAxis(), 15F/16F))
                    .applyIf(Modifiers.setPosition(10F/16F), up)
                    .export(quadMap, up ? null : quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getAxis(), 10F/16F))
                    .applyIf(Modifiers.setPosition(2F/16F), !up)
                    .export(quadMap, up ? quadDir : null);
        }
    }

    @Override
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        consumer.acceptAll(baseModel, level, pos, random, state, true, false, false, AUX_SHADER_STATE, null);
    }
}
