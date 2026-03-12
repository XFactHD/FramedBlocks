package io.github.xfacthd.framedblocks.client.model.geometry.pillar;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedHalfPillarGeometry extends Geometry
{
    private final Direction face;

    public FramedHalfPillarGeometry(GeometryFactory.Context ctx)
    {
        this.face = ctx.state().getValue(BlockStateProperties.FACING);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        QuadModifier mod = FramedPillarGeometry.createPillarQuad(quad, face.getAxis(), 4F / 16F, 12F / 16F, 12F / 16F);
        if (mod.hasFailed())
        {
            return;
        }

        Direction quadDir = quad.direction();
        if (quadDir == face)
        {
            mod.export(quadMap, face);
        }
        else if (quadDir == face.getOpposite())
        {
            mod.apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);
        }
        else if (DirUtils.isY(face))
        {
            mod.apply(Modifiers.cut(face.getOpposite(), .5F))
                    .export(quadMap, null);
        }
        else if (DirUtils.isY(quadDir))
        {
            mod.apply(Modifiers.cut(face.getOpposite(), .5F))
                    .export(quadMap, null);
        }
        else
        {
            mod.apply(Modifiers.cut(face.getOpposite(), .5F))
                    .export(quadMap, null);
        }
    }
}