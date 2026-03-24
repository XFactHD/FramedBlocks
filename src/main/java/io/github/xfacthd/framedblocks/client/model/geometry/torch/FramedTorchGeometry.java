package io.github.xfacthd.framedblocks.client.model.geometry.torch;

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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedTorchGeometry extends Geometry
{
    private static final float MIN = 7F/16F;
    private static final float MAX = 9F/16F;
    private static final float HEIGHT = 8F/16F;
    private static final float HEIGHT_REDSTONE_LIT = 7F/16F;

    private final BlockState state;
    private final BlockStateModel baseModel;
    private final BlockState auxShaderState;
    private final float height;

    private FramedTorchGeometry(GeometryFactory.Context ctx, BlockState auxShaderState, float height)
    {
        this.state = ctx.state();
        this.baseModel = ctx.baseModel();
        this.auxShaderState = auxShaderState;
        this.height = height;
    }

    @Override
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        consumer.acceptAll(baseModel, level, pos, random, state, true, false, false, auxShaderState, null);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (quadDir == Direction.DOWN)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(MIN, MIN, MAX, MAX))
                    .applyIf(Modifiers.setPosition(height), false)
                    .export(quadMap, quadDir);
        }
        else if (quadDir != Direction.UP)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(MIN, 0, MAX, height))
                    .apply(Modifiers.setPosition(MAX))
                    .export(quadMap, null);
        }
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }

    public static FramedTorchGeometry normal(GeometryFactory.Context ctx)
    {
        return new FramedTorchGeometry(ctx, Blocks.TORCH.defaultBlockState(), HEIGHT);
    }

    public static FramedTorchGeometry soul(GeometryFactory.Context ctx)
    {
        return new FramedTorchGeometry(ctx, Blocks.SOUL_TORCH.defaultBlockState(), HEIGHT);
    }

    public static FramedTorchGeometry copper(GeometryFactory.Context ctx)
    {
        return new FramedTorchGeometry(ctx, Blocks.COPPER_TORCH.defaultBlockState(), HEIGHT);
    }

    public static FramedTorchGeometry redstone(GeometryFactory.Context ctx)
    {
        float topHeight = ctx.state().getValue(BlockStateProperties.LIT) ? HEIGHT_REDSTONE_LIT : HEIGHT;
        return new FramedTorchGeometry(ctx, Blocks.REDSTONE_TORCH.defaultBlockState(), topHeight);
    }
}
