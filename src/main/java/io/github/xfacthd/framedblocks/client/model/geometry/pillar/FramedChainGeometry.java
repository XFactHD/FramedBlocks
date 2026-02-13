package io.github.xfacthd.framedblocks.client.model.geometry.pillar;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.MultiQuadModifier;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.block.pillar.FramedChainBlock;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FramedChainGeometry extends Geometry
{
    private static final Vector3f ROT_ORIGIN = new Vector3f(.5F, .5F, .5F);

    private final Direction.Axis axis;

    public FramedChainGeometry(GeometryFactory.Context ctx)
    {
        this.axis = ctx.state().getValue(FramedChainBlock.AXIS);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (quadDir.getAxis() == axis) return;

        Direction.Axis quadPerpAxis = Utils.getPerpendicularAxis(quadDir.getAxis(), axis);
        if (axis == Direction.Axis.Y)
        {
            createChainEdgeParts(quadMap, quad, quadDir, quadPerpAxis, Utils::isX, Modifiers::cut, Modifiers::cut);
            createChainCenterParts(quadMap, quad, Modifiers::cut, length -> Modifiers.cut(quadPerpAxis, length));
        }
        else
        {
            if (Utils.isY(quadDir))
            {
                Direction.Axis perpAxis = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;

                createChainEdgeParts(quadMap, quad, quadDir, quadPerpAxis, dir -> axis == Direction.Axis.Z, Modifiers::cut, Modifiers::cut);
                createChainCenterParts(quadMap, quad, Modifiers::cut, len -> Modifiers.cut(perpAxis, len));
            }
            else
            {
                createChainEdgeParts(quadMap, quad, quadDir, quadPerpAxis, dir -> axis == Direction.Axis.X, Modifiers::cut, Modifiers::cut);
                createChainCenterParts(quadMap, quad, Modifiers::cut, length -> Modifiers.cut(Direction.Axis.Y, length));
            }
        }
    }

    private void createChainEdgeParts(
            QuadMap quadMap,
            BakedQuad quad,
            Direction quadDir,
            Direction.Axis quadPerpAxis,
            Predicate<Direction> fourSectionPred,
            CutterFactory vertCutterFactory,
            CutterFactory horCutterFactory
    )
    {
        Direction dirUp = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        Direction dirDown = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
        Direction dirNeg = Direction.fromAxisAndDirection(quadPerpAxis, Direction.AxisDirection.NEGATIVE);
        Direction dirPos = Direction.fromAxisAndDirection(quadPerpAxis, Direction.AxisDirection.POSITIVE);

        List<MultiQuadModifier> modifiers = new ArrayList<>();

        MultiQuadModifier baseMod = new MultiQuadModifier(
                QuadModifier.of(quad)
                        .apply(horCutterFactory.create(dirNeg, 10F/16F))
                        .apply(horCutterFactory.create(dirPos,  7F/16F))
                        .apply(Modifiers.offset(dirPos, .5F/16F)),
                QuadModifier.of(quad)
                        .apply(horCutterFactory.create(dirNeg,  7F/16F))
                        .apply(horCutterFactory.create(dirPos, 10F/16F))
                        .apply(Modifiers.offset(dirNeg, .5F/16F))
        );

        if (fourSectionPred.test(quadDir))
        {
            modifiers.add(baseMod.derive()
                    .apply(vertCutterFactory.create(dirUp, 2F/16F))
            );
            modifiers.add(baseMod.derive()
                    .apply(vertCutterFactory.create(dirUp, 7F/16F))
                    .apply(vertCutterFactory.create(dirDown, 13F/16F))
            );
            modifiers.add(baseMod.derive()
                    .apply(vertCutterFactory.create(dirDown, 7F/16F))
                    .apply(vertCutterFactory.create(dirUp, 13F/16F))
            );
            modifiers.add(baseMod.derive()
                    .apply(vertCutterFactory.create(dirDown, 2F/16F))
            );
        }
        else
        {
            modifiers.add(baseMod.derive()
                    .apply(vertCutterFactory.create(dirDown, 4F/16F))
                    .apply(vertCutterFactory.create(dirUp, 15F/16F))
            );
            modifiers.add(baseMod.derive()
                    .apply(vertCutterFactory.create(dirDown, 10F/16F))
                    .apply(vertCutterFactory.create(dirUp, 10F/16F))
            );
            modifiers.add(baseMod.derive()
                    .apply(vertCutterFactory.create(dirUp, 4F/16F))
                    .apply(vertCutterFactory.create(dirDown, 15F/16F))
            );
        }

        for (MultiQuadModifier mod : modifiers)
        {
            mod.apply(Modifiers.setPosition(.5F))
                    .apply(Modifiers.rotate(axis, ROT_ORIGIN, 45, false))
                    .export(quadMap.get(null));
        }
    }

    private void createChainCenterParts(
            QuadMap quadMap,
            BakedQuad quad,
            CutterFactory vertCutterFactory,
            BidirectionalCutterFactory horCutterFactory
    )
    {
        Direction dirUp = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        Direction dirDown = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);

        for (int i = 0; i < 6; i++)
        {
            float height = switch (i)
            {
                case 0 -> 2F;
                case 5 -> 15F;
                default -> 3F * i + 1F;
            };
            QuadModifier.of(quad)
                    .apply(horCutterFactory.create(8.5F/16F))
                    .apply(vertCutterFactory.create(dirDown, height / 16F))
                    .apply(vertCutterFactory.create(dirUp, (16F - height + 1F) / 16F))
                    .apply(Modifiers.setPosition(.5F))
                    .apply(Modifiers.rotate(axis, ROT_ORIGIN, 45, false))
                    .export(quadMap.get(null));
        }
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }



    @FunctionalInterface
    private interface CutterFactory
    {
        QuadModifier.Modifier create(Direction dir, float length);
    }

    @FunctionalInterface
    private interface BidirectionalCutterFactory
    {
        QuadModifier.Modifier create(float length);
    }
}
