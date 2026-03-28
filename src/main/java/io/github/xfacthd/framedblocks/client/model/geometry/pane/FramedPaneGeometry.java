package io.github.xfacthd.framedblocks.client.model.geometry.pane;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedPaneGeometry extends Geometry {
    protected final boolean north;
    protected final boolean east;
    protected final boolean south;
    protected final boolean west;

    public FramedPaneGeometry(GeometryFactory.Context ctx) {
        this.north = ctx.state().getValue(BlockStateProperties.NORTH);
        this.east = ctx.state().getValue(BlockStateProperties.EAST);
        this.south = ctx.state().getValue(BlockStateProperties.SOUTH);
        this.west = ctx.state().getValue(BlockStateProperties.WEST);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction face = quad.direction();
        if (DirUtils.isY(face)) {
            if (isPillarVisible()) {
                createTopBottomCenterQuad(quadMap, quad, false);
            }

            if (north) {
                createTopBottomEdgeQuad(quadMap, quad, Direction.NORTH, false);
            }
            if (east) {
                createTopBottomEdgeQuad(quadMap, quad, Direction.EAST, false);
            }
            if (south) {
                createTopBottomEdgeQuad(quadMap, quad, Direction.SOUTH, false);
            }
            if (west) {
                createTopBottomEdgeQuad(quadMap, quad, Direction.WEST, false);
            }
        } else {
            boolean inset = isSideInset(face);
            if (!inset || isPillarVisible()) {
                createSideEdgeQuad(quadMap, quad, inset, false);
            }

            if (DirUtils.isX(face)) {
                if (north) {
                    createSideQuad(quadMap, quad, Direction.NORTH);
                }
                if (south) {
                    createSideQuad(quadMap, quad, Direction.SOUTH);
                }
            }

            if (DirUtils.isZ(face)) {
                if (east) {
                    createSideQuad(quadMap, quad, Direction.EAST);
                }
                if (west) {
                    createSideQuad(quadMap, quad, Direction.WEST);
                }
            }
        }
    }

    protected boolean isPillarVisible() {
        return true;
    }

    protected static void createTopBottomCenterQuad(QuadMapBuilder quadMap, BakedQuad quad, boolean mirrored) {
        QuadModifier.of(quad)
                .apply(Modifiers.cutTopBottom(7F/16F, 7F/16F, 9F/16F, 9F/16F))
                .applyIf(Modifiers.setPosition(.001F), mirrored)
                .export(quadMap, mirrored ? null : quad.direction());
    }

    protected static void createTopBottomEdgeQuad(QuadMapBuilder quadMap, BakedQuad quad, Direction dir, boolean mirrored) {
        Preconditions.checkArgument(!DirUtils.isY(dir), String.format("Invalid direction: %s!", dir));

        QuadModifier.of(quad)
                .apply(Modifiers.cut(dir.getOpposite(), 7F/16F))
                .apply(Modifiers.cut(dir.getClockWise().getAxis(), 9F/16F))
                .applyIf(Modifiers.setPosition(.001F), mirrored)
                .export(quadMap, mirrored ? null : quad.direction());
    }

    protected static void createSideEdgeQuad(QuadMapBuilder quadMap, BakedQuad quad, boolean inset, boolean mirrored) {
        Preconditions.checkArgument(!inset || !mirrored, "Quad can't be mirrored and inset!");

        Direction quadDir = quad.direction();
        Direction exportSide = inset ? null : (mirrored ? quadDir.getOpposite() : quadDir);

        QuadModifier.of(quad)
                .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 9F/16F))
                .applyIf(Modifiers.setPosition(9F/16F), inset)
                .applyIf(Modifiers.setPosition(.001F), !inset && mirrored)
                .export(quadMap, exportSide);
    }

    private static void createSideQuad(QuadMapBuilder quadMap, BakedQuad quad, Direction dir) {
        QuadModifier.of(quad)
                .apply(Modifiers.cut(dir.getOpposite(), 7F/16F))
                .apply(Modifiers.setPosition(9F/16F))
                .export(quadMap, null);
    }

    protected boolean isSideInset(Direction face) {
        return switch (face) {
            case NORTH -> !north;
            case EAST -> !east;
            case SOUTH -> !south;
            case WEST -> !west;
            default -> throw new IllegalArgumentException(String.format("Invalid face: %s!", face));
        };
    }
}
