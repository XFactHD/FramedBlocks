package xfacthd.framedblocks.client.model.stairs;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;

public class FramedLadderGeometry implements Geometry
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
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            QuadModifier capMod = QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), LEG_DEPTH));

            capMod.derive().apply(Modifiers.cutTopBottom(dir.getClockWise(), LEG_DEPTH))
                    .export(quadMap.get(quadDir));

            capMod.apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), LEG_DEPTH))
                    .export(quadMap.get(quadDir));

            QuadModifier rungMod = QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir, 1F - RUNG_OFFSET))
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), RUNG_DEPTH + RUNG_OFFSET))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), 1F - LEG_DEPTH))
                    .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), 1F - LEG_DEPTH));

            for (int i = 0; i < 4; i++)
            {
                float height = quad.getDirection() == Direction.DOWN ? 1F - RUNGS[i] : RUNGS[i] + RUNG_DEPTH;
                rungMod.derive().apply(Modifiers.setPosition(height))
                        .export(quadMap.get(null));
            }
        }
        else if (quadDir.getAxis() == dir.getAxis())
        {
            boolean opposite = quadDir == dir.getOpposite();

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), LEG_DEPTH))
                    .applyIf(Modifiers.setPosition(LEG_DEPTH), opposite)
                    .export(quadMap.get(opposite ? null : quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), LEG_DEPTH))
                    .applyIf(Modifiers.setPosition(LEG_DEPTH), opposite)
                    .export(quadMap.get(opposite ? null : quadDir));

            float pos = quad.getDirection() == dir ? (1F - RUNG_OFFSET) : (RUNG_DEPTH + RUNG_OFFSET);

            for (int i = 0; i < 4; i++)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSide(LEG_DEPTH, RUNGS[i], 1F - LEG_DEPTH, RUNGS[i] + RUNG_DEPTH))
                        .apply(Modifiers.setPosition(pos))
                        .export(quadMap.get(null));
            }
        }
        else
        {
            QuadModifier mod = QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), RUNG_DEPTH * 2F));

            mod.export(quadMap.get(quadDir));

            mod.derive().apply(Modifiers.setPosition(RUNG_DEPTH * 2F))
                    .export(quadMap.get(null));
        }
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }
}