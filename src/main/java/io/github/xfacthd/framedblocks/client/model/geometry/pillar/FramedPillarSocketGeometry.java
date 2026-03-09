package io.github.xfacthd.framedblocks.client.model.geometry.pillar;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedPillarSocketGeometry extends Geometry
{
    private final Direction facing;

    public FramedPillarSocketGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(BlockStateProperties.FACING);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        Direction quadDir = quad.direction();
        Direction.Axis quadAxis = quadDir.getAxis();

        if (quadDir == facing.getOpposite())
        {
            boolean y = DirUtils.isY(quadDir);
            QuadModifier.of(quad)
                    .apply(y ? Modifiers.cutTopBottom(.25F, .25F, .75F, .75F) : Modifiers.cutSide(.25F, .25F, .75F, .75F))
                    .export(quadMap.get(quadDir));

            DirUtils.forAllDirections(dir ->
            {
                if (dir.getAxis() == facing.getAxis()) return;

                boolean perp = y ? DirUtils.isZ(dir) : DirUtils.isY(dir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir, .25F))
                        .applyIf(Modifiers.cut(dir.getClockWise(quadAxis).getAxis(), .75F), perp)
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            });
        }
        else if (quadDir != facing)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getOpposite(), .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, .5F))
                    .apply(Modifiers.cut(facing.getClockWise(quadAxis).getAxis(), .75F))
                    .apply(Modifiers.setPosition(.75F))
                    .export(quadMap.get(null));
        }
    }
}
