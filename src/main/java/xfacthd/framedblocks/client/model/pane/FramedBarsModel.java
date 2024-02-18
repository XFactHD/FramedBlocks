package xfacthd.framedblocks.client.model.pane;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedBarsModel extends FramedPaneModel
{
    public FramedBarsModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        if (Utils.isY(face))
        {
            createTopBottomCenterQuad(quadMap, quad, false);
            createTopBottomCenterQuad(quadMap, quad, true);

            if (north)
            {
                createTopBottomEdgeQuad(quadMap, quad, Direction.NORTH, false);
                createTopBottomEdgeQuad(quadMap, quad, Direction.NORTH, true);
            }
            if (east)
            {
                createTopBottomEdgeQuad(quadMap, quad, Direction.EAST, false);
                createTopBottomEdgeQuad(quadMap, quad, Direction.EAST, true);
            }
            if (south)
            {
                createTopBottomEdgeQuad(quadMap, quad, Direction.SOUTH, false);
                createTopBottomEdgeQuad(quadMap, quad, Direction.SOUTH, true);
            }
            if (west)
            {
                createTopBottomEdgeQuad(quadMap, quad, Direction.WEST, false);
                createTopBottomEdgeQuad(quadMap, quad, Direction.WEST, true);
            }
        }
        else
        {
            if (!isSideInset(face))
            {
                createSideEdgeQuad(quadMap, quad, false, false);
            }
            if (!isSideInset(face.getOpposite()))
            {
                createSideEdgeQuad(quadMap, quad, false, true);
            }

            if (Utils.isX(face))
            {
                createCenterPillarQuad(quadMap.get(null), quad, east, west, south, north);

                if (north)
                {
                    createPillarQuad(quadMap.get(null), quad, Direction.NORTH);
                    createBarQuads(quadMap.get(null), quad, Direction.NORTH);
                }
                if (south)
                {
                    createPillarQuad(quadMap.get(null), quad, Direction.SOUTH);
                    createBarQuads(quadMap.get(null), quad, Direction.SOUTH);
                }
            }

            if (Utils.isZ(face))
            {
                createCenterPillarQuad(quadMap.get(null), quad, south, north, east, west);

                if (east)
                {
                    createPillarQuad(quadMap.get(null), quad, Direction.EAST);
                    createBarQuads(quadMap.get(null), quad, Direction.EAST);
                }
                if (west)
                {
                    createPillarQuad(quadMap.get(null), quad, Direction.WEST);
                    createBarQuads(quadMap.get(null), quad, Direction.WEST);
                }
            }
        }
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }

    /**
     * @param perpNeg Connection state in the negative direction perpendicular to the quad
     * @param perpPos Connection state in the positive direction perpendicular to the quad
     * @param parNeg Connection state in the negative direction in the same plane as the quad
     * @param parPos Connection state in the positive direction in the same plane as the quad
     */
    private static void createCenterPillarQuad(List<BakedQuad> quadList, BakedQuad quad, boolean perpNeg, boolean perpPos, boolean parNeg, boolean parPos)
    {
        if (perpNeg && perpPos && !parNeg && !parPos)
        {
            return;
        }

        boolean perpendicular = perpNeg || perpPos;
        boolean oneParallel = parNeg ^ parPos;

        float minXZ = perpendicular && oneParallel && !parPos ? 8F/16F : 7F/16F;
        float maxXZ = perpendicular && oneParallel && !parNeg ? 8F/16F : 9F/16F;

        float offset;
        if (parNeg || parPos)
        {
            offset = .5F;
        }
        else
        {
            offset = perpNeg ? 9F/16F : (perpPos ? 7F/16F : .5F);

            if (Utils.isPositive(quad.getDirection()))
            {
                offset = 1F - offset;
            }
        }

        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSide(minXZ, 0, maxXZ, 1))
                .apply(Modifiers.setPosition(offset))
                .export(quadList);
    }

    private static void createPillarQuad(List<BakedQuad> quadList, BakedQuad quad, Direction dir)
    {
        if (Utils.isY(dir))
        {
            throw new IllegalArgumentException(String.format("Invalid direction: %s!", dir));
        }

        boolean positive = Utils.isPositive(dir);
        float minXZ = positive ? 12F/16F : 2F/16F;
        float maxXZ = positive ? 14F/16F : 4F/16F;

        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSide(minXZ, 0, maxXZ, 1))
                .apply(Modifiers.setPosition(.5F))
                .export(quadList);
    }

    private static void createBarQuads(List<BakedQuad> quadList, BakedQuad quad, Direction dir)
    {
        if (Utils.isY(dir))
        {
            throw new IllegalArgumentException(String.format("Invalid direction: %s!", dir));
        }

        boolean positive = Utils.isPositive(dir);
        boolean northeast = dir == Direction.NORTH || dir == Direction.EAST;

        float minXZ = positive ?  9F/16F : 4F/16F;
        float maxXZ = positive ? 12F/16F : 7F/16F;
        float minY = northeast ? 2F/16F : 12F/16F;
        float maxY = northeast ? 4F/16F : 14F/16F;

        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSide(minXZ, minY, maxXZ, maxY))
                .apply(Modifiers.setPosition(.5F))
                .export(quadList);

        minXZ = positive ? 14F/16F : 0;
        maxXZ = positive ? 1 :  2F/16F;

        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSide(minXZ, 7F/16F, maxXZ, 9F/16F))
                .apply(Modifiers.setPosition(.5F))
                .export(quadList);
    }
}