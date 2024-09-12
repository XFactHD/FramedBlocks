package xfacthd.framedblocks.client.model.pillar;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.quad.*;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.pillar.FramedChainBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FramedChainGeometry extends Geometry
{
    private static final Vector3f ROT_ORIGIN = new Vector3f(.5F, .5F, .5F);
    private static final CutterFactory CUTTER_SIDE_UD = (dir, len) -> Modifiers.cutSideUpDown(dir == Direction.DOWN, len);

    private final Direction.Axis axis;

    public FramedChainGeometry(GeometryFactory.Context ctx)
    {
        this.axis = ctx.state().getValue(FramedChainBlock.AXIS);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        Direction.Axis quadPerpAxis = Utils.nextAxisNotEqualTo(quadDir.getAxis(), axis);
        if (axis == Direction.Axis.Y)
        {
            if (!Utils.isY(quadDir))
            {
                createChainEdgeParts(quadMap, quad, quadDir, quadPerpAxis, Utils::isX, CUTTER_SIDE_UD, Modifiers::cutSideLeftRight);
                createChainCenterParts(quadMap, quad, CUTTER_SIDE_UD, Modifiers::cutSideLeftRight);
            }
        }
        else
        {
            if (Utils.isY(quadDir))
            {
                Direction.Axis perpAxis = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;

                createChainEdgeParts(quadMap, quad, quadDir, quadPerpAxis, dir -> axis == Direction.Axis.Z, Modifiers::cutTopBottom, Modifiers::cutTopBottom);
                createChainCenterParts(quadMap, quad, Modifiers::cutTopBottom, len -> Modifiers.cutTopBottom(perpAxis, len));
            }
            else if (quadDir.getAxis() != axis)
            {
                createChainEdgeParts(quadMap, quad, quadDir, quadPerpAxis, dir -> axis == Direction.Axis.X, Modifiers::cutSideLeftRight, CUTTER_SIDE_UD);
                createChainCenterParts(quadMap, quad, Modifiers::cutSideLeftRight, Modifiers::cutSideUpDown);
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
