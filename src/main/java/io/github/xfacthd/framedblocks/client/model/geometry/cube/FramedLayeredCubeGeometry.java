package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedLayeredCubeGeometry extends Geometry
{
    private static final float LAYER_HEIGHT = 1F/8F;

    private final Direction facing;
    private final float height;

    public FramedLayeredCubeGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(BlockStateProperties.FACING);
        this.height = ctx.state().getValue(BlockStateProperties.LAYERS) * LAYER_HEIGHT;
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (quadDir == facing)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.setPosition(height))
                    .export(quadMap, null);
        }
        else
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, height))
                    .export(quadMap, quadDir);
        }
    }

    @Override
    public boolean useBaseModel()
    {
        return true;
    }
}
