package io.github.xfacthd.framedblocks.client.model.geometry.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedLadderGeometry extends Geometry
{
    private static final float RUNG_DEPTH = 1F/16F;
    private static final float LEG_DEPTH = RUNG_DEPTH * 2F;
    private static final float RUNG_OFFSET = .5F/16F;
    private static final float[] RUNGS = new float[] {
            1.5F/16F,
            5.5F/16F,
            9.5F/16F,
            13.5F/16F
    };

    private final Direction dir;

    public FramedLadderGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (DirUtils.isY(quadDir))
        {
            QuadModifier capMod = QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), LEG_DEPTH));

            capMod.derive().apply(Modifiers.cut(dir.getClockWise(), LEG_DEPTH))
                    .export(quadMap, quadDir);

            capMod.apply(Modifiers.cut(dir.getCounterClockWise(), LEG_DEPTH))
                    .export(quadMap, quadDir);

            QuadModifier rungMod = QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, 1F - RUNG_OFFSET))
                    .apply(Modifiers.cut(dir.getOpposite(), RUNG_DEPTH + RUNG_OFFSET))
                    .apply(Modifiers.cut(dir.getClockWise(), 1F - LEG_DEPTH))
                    .apply(Modifiers.cut(dir.getCounterClockWise(), 1F - LEG_DEPTH));

            for (int i = 0; i < 4; i++)
            {
                float height = quad.direction() == Direction.DOWN ? 1F - RUNGS[i] : RUNGS[i] + RUNG_DEPTH;
                rungMod.derive().apply(Modifiers.setPosition(height))
                        .export(quadMap, null);
            }

            rungMod.discard();
        }
        else if (quadDir.getAxis() == dir.getAxis())
        {
            boolean opposite = quadDir == dir.getOpposite();

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getClockWise(), LEG_DEPTH))
                    .applyIf(Modifiers.setPosition(LEG_DEPTH), opposite)
                    .export(quadMap, opposite ? null : quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getCounterClockWise(), LEG_DEPTH))
                    .applyIf(Modifiers.setPosition(LEG_DEPTH), opposite)
                    .export(quadMap, opposite ? null : quadDir);

            float pos = quad.direction() == dir ? (1F - RUNG_OFFSET) : (RUNG_DEPTH + RUNG_OFFSET);

            for (int i = 0; i < 4; i++)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSide(LEG_DEPTH, RUNGS[i], 1F - LEG_DEPTH, RUNGS[i] + RUNG_DEPTH))
                        .apply(Modifiers.setPosition(pos))
                        .export(quadMap, null);
            }
        }
        else
        {
            QuadModifier mod = QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), RUNG_DEPTH * 2F));

            mod.derive().apply(Modifiers.setPosition(RUNG_DEPTH * 2F))
                    .export(quadMap, null);

            mod.export(quadMap, quadDir);
        }
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }
}
