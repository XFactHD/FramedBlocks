package io.github.xfacthd.framedblocks.client.model.geometry.pane;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedBarsGeometry extends FramedPaneGeometry {
    public FramedBarsGeometry(GeometryFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction face = quad.direction();
        if (DirUtils.isY(face)) {
            createTopBottomCenterQuad(quadMap, quad, false);
            createTopBottomCenterQuad(quadMap, quad, true);

            if (north) {
                createTopBottomEdgeQuad(quadMap, quad, Direction.NORTH, false);
                createTopBottomEdgeQuad(quadMap, quad, Direction.NORTH, true);
            }
            if (east) {
                createTopBottomEdgeQuad(quadMap, quad, Direction.EAST, false);
                createTopBottomEdgeQuad(quadMap, quad, Direction.EAST, true);
            }
            if (south) {
                createTopBottomEdgeQuad(quadMap, quad, Direction.SOUTH, false);
                createTopBottomEdgeQuad(quadMap, quad, Direction.SOUTH, true);
            }
            if (west) {
                createTopBottomEdgeQuad(quadMap, quad, Direction.WEST, false);
                createTopBottomEdgeQuad(quadMap, quad, Direction.WEST, true);
            }
        } else {
            if (!isSideInset(face)) {
                createSideEdgeQuad(quadMap, quad, false, false);
            }
            if (!isSideInset(face.getOpposite())) {
                createSideEdgeQuad(quadMap, quad, false, true);
            }

            if (DirUtils.isX(face)) {
                createCenterPillarQuad(quadMap, quad, east, west, south, north);

                if (north) {
                    createPillarQuad(quadMap, quad, Direction.NORTH);
                    createBarQuads(quadMap, quad, Direction.NORTH);
                }
                if (south) {
                    createPillarQuad(quadMap, quad, Direction.SOUTH);
                    createBarQuads(quadMap, quad, Direction.SOUTH);
                }
            }

            if (DirUtils.isZ(face)) {
                createCenterPillarQuad(quadMap, quad, south, north, east, west);

                if (east) {
                    createPillarQuad(quadMap, quad, Direction.EAST);
                    createBarQuads(quadMap, quad, Direction.EAST);
                }
                if (west) {
                    createPillarQuad(quadMap, quad, Direction.WEST);
                    createBarQuads(quadMap, quad, Direction.WEST);
                }
            }
        }
    }

    @Override
    public boolean useSolidNoCamoModel() {
        return true;
    }

    /// @param perpNeg Connection state in the negative direction perpendicular to the quad
    /// @param perpPos Connection state in the positive direction perpendicular to the quad
    /// @param parNeg  Connection state in the negative direction in the same plane as the quad
    /// @param parPos  Connection state in the positive direction in the same plane as the quad
    private static void createCenterPillarQuad(QuadMapBuilder quadMap, BakedQuad quad, boolean perpNeg, boolean perpPos, boolean parNeg, boolean parPos) {
        if (perpNeg && perpPos && !parNeg && !parPos) {
            return;
        }

        boolean perpendicular = perpNeg || perpPos;
        boolean oneParallel = parNeg ^ parPos;

        float minXZ = perpendicular && oneParallel && !parPos ? 8F/16F : 7F/16F;
        float maxXZ = perpendicular && oneParallel && !parNeg ? 8F/16F : 9F/16F;

        float offset;
        if (parNeg || parPos) {
            offset = .5F;
        } else {
            offset = perpNeg ? 9F/16F : (perpPos ? 7F/16F : .5F);

            if (DirUtils.isPositive(quad.direction())) {
                offset = 1F - offset;
            }
        }

        QuadModifier.of(quad)
                .apply(Modifiers.cutSide(minXZ, 0, maxXZ, 1))
                .apply(Modifiers.setPosition(offset))
                .export(quadMap, null);
    }

    private static void createPillarQuad(QuadMapBuilder quadMap, BakedQuad quad, Direction dir) {
        if (DirUtils.isY(dir)) {
            throw new IllegalArgumentException(String.format("Invalid direction: %s!", dir));
        }

        boolean positive = DirUtils.isPositive(dir);
        float minXZ = positive ? 12F/16F : 2F/16F;
        float maxXZ = positive ? 14F/16F : 4F/16F;

        QuadModifier.of(quad)
                .apply(Modifiers.cutSide(minXZ, 0, maxXZ, 1))
                .apply(Modifiers.setPosition(.5F))
                .export(quadMap, null);
    }

    private static void createBarQuads(QuadMapBuilder quadMap, BakedQuad quad, Direction dir) {
        if (DirUtils.isY(dir)) {
            throw new IllegalArgumentException(String.format("Invalid direction: %s!", dir));
        }

        boolean positive = DirUtils.isPositive(dir);
        boolean northeast = dir == Direction.NORTH || dir == Direction.EAST;

        float minXZ = positive ?  9F/16F : 4F/16F;
        float maxXZ = positive ? 12F/16F : 7F/16F;
        float minY = northeast ? 2F/16F : 12F/16F;
        float maxY = northeast ? 4F/16F : 14F/16F;

        QuadModifier.of(quad)
                .apply(Modifiers.cutSide(minXZ, minY, maxXZ, maxY))
                .apply(Modifiers.setPosition(.5F))
                .export(quadMap, null);

        minXZ = positive ? 14F/16F : 0;
        maxXZ = positive ? 1 :  2F/16F;

        QuadModifier.of(quad)
                .apply(Modifiers.cutSide(minXZ, 7F/16F, maxXZ, 9F/16F))
                .apply(Modifiers.setPosition(.5F))
                .export(quadMap, null);
    }
}
