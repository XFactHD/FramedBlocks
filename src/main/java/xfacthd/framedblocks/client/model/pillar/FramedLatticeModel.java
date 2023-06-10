package xfacthd.framedblocks.client.model.pillar;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedLatticeModel extends FramedBlockModel
{
    private static final float MIN_COORD = 6F/16F;
    private static final float MAX_COORD = 10F/16F;

    private final boolean xAxis;
    private final boolean yAxis;
    private final boolean zAxis;

    public FramedLatticeModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        xAxis = state.getValue(FramedProperties.X_AXIS);
        yAxis = state.getValue(FramedProperties.Y_AXIS);
        zAxis = state.getValue(FramedProperties.Z_AXIS);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(MIN_COORD, MIN_COORD, MAX_COORD, MAX_COORD))
                    .applyIf(Modifiers.setPosition(MAX_COORD), !yAxis)
                    .export(quadMap.get(yAxis ? quadDir : null));

            if (xAxis)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(0F, MIN_COORD, MIN_COORD, MAX_COORD))
                        .apply(Modifiers.setPosition(MAX_COORD))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(MAX_COORD, MIN_COORD, 1F, MAX_COORD))
                        .apply(Modifiers.setPosition(MAX_COORD))
                        .export(quadMap.get(null));
            }

            if (zAxis)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(MIN_COORD, 0F, MAX_COORD, MIN_COORD))
                        .apply(Modifiers.setPosition(MAX_COORD))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(MIN_COORD, MAX_COORD, MAX_COORD, 1F))
                        .apply(Modifiers.setPosition(MAX_COORD))
                        .export(quadMap.get(null));
            }
        }
        else if (Utils.isX(quadDir))
        {
            createHorizontalStrutSideQuads(quadMap, quad, xAxis, zAxis);
        }
        else if (Utils.isZ(quadDir))
        {
            createHorizontalStrutSideQuads(quadMap, quad, zAxis, xAxis);
        }

        if (!Utils.isY(quadDir) && yAxis)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(MIN_COORD, 0F, MAX_COORD, MIN_COORD))
                    .apply(Modifiers.setPosition(MAX_COORD))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(MIN_COORD, MAX_COORD, MAX_COORD, 1F))
                    .apply(Modifiers.setPosition(MAX_COORD))
                    .export(quadMap.get(null));
        }
    }

    private static void createHorizontalStrutSideQuads(
            Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, boolean frontAxis, boolean sideAxis
    )
    {
        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSide(MIN_COORD, MIN_COORD, MAX_COORD, MAX_COORD))
                .applyIf(Modifiers.setPosition(MAX_COORD), !frontAxis)
                .export(quadMap.get(frontAxis ? quad.getDirection() : null));

        if (sideAxis)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(0F, MIN_COORD, MIN_COORD, MAX_COORD))
                    .apply(Modifiers.setPosition(MAX_COORD))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(MAX_COORD, MIN_COORD, 1F, MAX_COORD))
                    .apply(Modifiers.setPosition(MAX_COORD))
                    .export(quadMap.get(null));
        }
    }
}