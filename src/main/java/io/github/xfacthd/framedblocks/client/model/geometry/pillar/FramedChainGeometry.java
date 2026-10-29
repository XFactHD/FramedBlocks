package io.github.xfacthd.framedblocks.client.model.geometry.pillar;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.MultiQuadModifier;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.block.pillar.FramedChainBlock;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FramedChainGeometry extends Geometry {
    private static final Vector3f ROT_ORIGIN = new Vector3f(.5F, .5F, .5F);

    private final Direction.Axis axis;

    public FramedChainGeometry(GeometryFactory.Context ctx) {
        this.axis = ctx.state().getValue(FramedChainBlock.AXIS);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction quadDir = quad.direction();
        if (quadDir.getAxis() == axis) {
            return;
        }

        Direction.Axis quadPerpAxis = DirUtils.getPerpendicularAxis(quadDir.getAxis(), axis);
        if (axis == Direction.Axis.Y) {
            createChainEdgeParts(quadMap, quad, quadDir, quadPerpAxis, DirUtils::isX);
            createChainCenterParts(quadMap, quad, quadPerpAxis);
        } else {
            if (DirUtils.isY(quadDir)) {
                Direction.Axis perpAxis = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;

                createChainEdgeParts(quadMap, quad, quadDir, quadPerpAxis, _ -> axis == Direction.Axis.Z);
                createChainCenterParts(quadMap, quad, perpAxis);
            } else {
                createChainEdgeParts(quadMap, quad, quadDir, quadPerpAxis, _ -> axis == Direction.Axis.X);
                createChainCenterParts(quadMap, quad, Direction.Axis.Y);
            }
        }
    }

    private void createChainEdgeParts(
            QuadMapBuilder quadMap,
            BakedQuad quad,
            Direction quadDir,
            Direction.Axis quadPerpAxis,
            Predicate<Direction> fourSectionPred
    ) {
        Direction dirUp = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        Direction dirDown = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
        Direction dirNeg = Direction.fromAxisAndDirection(quadPerpAxis, Direction.AxisDirection.NEGATIVE);
        Direction dirPos = Direction.fromAxisAndDirection(quadPerpAxis, Direction.AxisDirection.POSITIVE);

        List<MultiQuadModifier> modifiers = new ArrayList<>();

        MultiQuadModifier baseMod = new MultiQuadModifier(
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dirNeg, 10F/16F))
                        .apply(Modifiers.cut(dirPos,  7F/16F))
                        .apply(Modifiers.offset(dirPos, .5F/16F)),
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dirNeg,  7F/16F))
                        .apply(Modifiers.cut(dirPos, 10F/16F))
                        .apply(Modifiers.offset(dirNeg, .5F/16F))
        );

        if (fourSectionPred.test(quadDir)) {
            modifiers.add(baseMod.derive()
                    .apply(Modifiers.cut(dirUp, 2F/16F))
            );
            modifiers.add(baseMod.derive()
                    .apply(Modifiers.cut(dirUp, 7F/16F))
                    .apply(Modifiers.cut(dirDown, 13F/16F))
            );
            modifiers.add(baseMod.derive()
                    .apply(Modifiers.cut(dirDown, 7F/16F))
                    .apply(Modifiers.cut(dirUp, 13F/16F))
            );
            modifiers.add(baseMod.derive()
                    .apply(Modifiers.cut(dirDown, 2F/16F))
            );
        } else {
            modifiers.add(baseMod.derive()
                    .apply(Modifiers.cut(dirDown, 4F/16F))
                    .apply(Modifiers.cut(dirUp, 15F/16F))
            );
            modifiers.add(baseMod.derive()
                    .apply(Modifiers.cut(dirDown, 10F/16F))
                    .apply(Modifiers.cut(dirUp, 10F/16F))
            );
            modifiers.add(baseMod.derive()
                    .apply(Modifiers.cut(dirUp, 4F/16F))
                    .apply(Modifiers.cut(dirDown, 15F/16F))
            );
        }

        for (MultiQuadModifier mod : modifiers) {
            mod.apply(Modifiers.setPosition(.5F))
                    .apply(Modifiers.rotate(axis, ROT_ORIGIN, 45, false))
                    .export(quadMap, null);
        }

        baseMod.discard();
    }

    private void createChainCenterParts(QuadMapBuilder quadMap, BakedQuad quad, Direction.Axis horAxis) {
        Direction dirUp = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        Direction dirDown = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);

        for (int i = 0; i < 6; i++) {
            float height = switch (i) {
                case 0 -> 2F;
                case 5 -> 15F;
                default -> 3F * i + 1F;
            };
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(horAxis, 8.5F/16F))
                    .apply(Modifiers.cut(dirDown, height / 16F))
                    .apply(Modifiers.cut(dirUp, (16F - height + 1F) / 16F))
                    .apply(Modifiers.setPosition(.5F))
                    .apply(Modifiers.rotate(axis, ROT_ORIGIN, 45, false))
                    .export(quadMap, null);
        }
    }

    @Override
    public boolean useSolidNoCamoModel() {
        return true;
    }
}
