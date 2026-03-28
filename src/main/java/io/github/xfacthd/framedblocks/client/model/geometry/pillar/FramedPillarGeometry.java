package io.github.xfacthd.framedblocks.client.model.geometry.pillar;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedPillarGeometry extends Geometry {
    private final Direction.Axis axis;
    private final float capStart;
    private final float capEnd;
    private final float sideCut;
    private final boolean useSolidBase;

    public FramedPillarGeometry(GeometryFactory.Context ctx) {
        this.axis = ctx.state().getValue(BlockStateProperties.AXIS);
        boolean post = ((IFramedBlock) ctx.state().getBlock()).getBlockType() == BlockType.FRAMED_POST;
        this.capStart = post ? (6F / 16F) : (4F / 16F);
        this.capEnd = this.sideCut = post ? (10F / 16F) : (12F / 16F);
        this.useSolidBase = post;
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction quadDir = quad.direction();
        createPillarQuad(quad, axis, capStart, capEnd, sideCut)
                .export(quadMap, quadDir.getAxis() == axis ? quadDir : null);
    }

    public static QuadModifier createPillarQuad(BakedQuad quad, Direction.Axis axis, float capStart, float capEnd, float sideCut) {
        Direction quadDir = quad.direction();
        if (quadDir.getAxis() == axis) {
            if (axis == Direction.Axis.Y) {
                return QuadModifier.of(quad).apply(Modifiers.cutTopBottom(capStart, capStart, capEnd, capEnd));
            } else {
                return QuadModifier.of(quad).apply(Modifiers.cutSide(capStart, capStart, capEnd, capEnd));
            }
        } else {
            if (axis == Direction.Axis.Y) {
                return QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadDir.getClockWise(), sideCut))
                        .apply(Modifiers.cut(quadDir.getCounterClockWise(), sideCut))
                        .apply(Modifiers.setPosition(sideCut));
            } else if (DirUtils.isY(quadDir)) {
                return QuadModifier.of(quad)
                        .apply(Modifiers.cut(axisToDir(axis, true).getClockWise(), sideCut))
                        .apply(Modifiers.cut(axisToDir(axis, false).getClockWise(), sideCut))
                        .apply(Modifiers.setPosition(sideCut));
            } else {
                return QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.DOWN, sideCut))
                        .apply(Modifiers.cut(Direction.UP, sideCut))
                        .apply(Modifiers.setPosition(sideCut));
            }
        }
    }

    @Override
    public boolean useSolidNoCamoModel() {
        return useSolidBase;
    }

    private static Direction axisToDir(Direction.Axis axis, boolean positive) {
        return switch (axis) {
            case X -> positive ? Direction.EAST : Direction.WEST;
            case Y -> positive ? Direction.UP : Direction.DOWN;
            case Z -> positive ? Direction.SOUTH : Direction.NORTH;
        };
    }
}
