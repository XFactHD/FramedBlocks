package io.github.xfacthd.framedblocks.client.model.geometry.pillar;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedLatticeGeometry extends Geometry
{
    private static final float MIN_COORD = 6F/16F;
    private static final float MAX_COORD = 10F/16F;
    private static final float MIN_COORD_THICK = 4F/16F;
    private static final float MAX_COORD_THICK = 12F/16F;

    private final boolean xAxis;
    private final boolean yAxis;
    private final boolean zAxis;
    private final float minCoord;
    private final float maxCoord;

    public FramedLatticeGeometry(GeometryFactory.Context ctx)
    {
        this.xAxis = ctx.state().getValue(FramedProperties.X_AXIS);
        this.yAxis = ctx.state().getValue(FramedProperties.Y_AXIS);
        this.zAxis = ctx.state().getValue(FramedProperties.Z_AXIS);
        boolean thick = ctx.state().getBlock() == FBContent.BLOCK_FRAMED_THICK_LATTICE.value();
        this.minCoord = thick ? MIN_COORD_THICK : MIN_COORD;
        this.maxCoord = thick ? MAX_COORD_THICK : MAX_COORD;
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (DirUtils.isY(quadDir))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(minCoord, minCoord, maxCoord, maxCoord))
                    .applyIf(Modifiers.setPosition(maxCoord), !yAxis)
                    .export(quadMap, yAxis ? quadDir : null);

            if (xAxis)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(0F, minCoord, minCoord, maxCoord))
                        .apply(Modifiers.setPosition(maxCoord))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(maxCoord, minCoord, 1F, maxCoord))
                        .apply(Modifiers.setPosition(maxCoord))
                        .export(quadMap, null);
            }

            if (zAxis)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(minCoord, 0F, maxCoord, minCoord))
                        .apply(Modifiers.setPosition(maxCoord))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(minCoord, maxCoord, maxCoord, 1F))
                        .apply(Modifiers.setPosition(maxCoord))
                        .export(quadMap, null);
            }
        }
        else if (DirUtils.isX(quadDir))
        {
            createHorizontalStrutSideQuads(quadMap, quad, xAxis, zAxis);
        }
        else if (DirUtils.isZ(quadDir))
        {
            createHorizontalStrutSideQuads(quadMap, quad, zAxis, xAxis);
        }

        if (!DirUtils.isY(quadDir) && yAxis)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(minCoord, 0F, maxCoord, minCoord))
                    .apply(Modifiers.setPosition(maxCoord))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(minCoord, maxCoord, maxCoord, 1F))
                    .apply(Modifiers.setPosition(maxCoord))
                    .export(quadMap, null);
        }
    }

    private void createHorizontalStrutSideQuads(QuadMapBuilder quadMap, BakedQuad quad, boolean frontAxis, boolean sideAxis)
    {
        QuadModifier.of(quad)
                .apply(Modifiers.cutSide(minCoord, minCoord, maxCoord, maxCoord))
                .applyIf(Modifiers.setPosition(maxCoord), !frontAxis)
                .export(quadMap, frontAxis ? quad.direction() : null);

        if (sideAxis)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(0F, minCoord, minCoord, maxCoord))
                    .apply(Modifiers.setPosition(maxCoord))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(maxCoord, minCoord, 1F, maxCoord))
                    .apply(Modifiers.setPosition(maxCoord))
                    .export(quadMap, null);
        }
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }
}
