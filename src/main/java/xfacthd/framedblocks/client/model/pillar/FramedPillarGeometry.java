package xfacthd.framedblocks.client.model.pillar;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedPillarGeometry implements Geometry
{
    private final Direction.Axis axis;
    private final float capStart;
    private final float capEnd;
    private final float sideCut;

    public FramedPillarGeometry(GeometryFactory.Context ctx)
    {
        this.axis = ctx.state().getValue(BlockStateProperties.AXIS);

        boolean post = ((IFramedBlock) ctx.state().getBlock()).getBlockType() == BlockType.FRAMED_POST;
        this.capStart = post ? (6F / 16F) : (4F / 16F);
        this.capEnd = this.sideCut = post ? (10F / 16F) : (12F / 16F);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        createPillarQuad(quad, axis, capStart, capEnd, sideCut)
                .export(quadMap.get(quadDir.getAxis() == axis ? quadDir : null));
    }

    public static QuadModifier createPillarQuad(
            BakedQuad quad, Direction.Axis axis, float capStart, float capEnd, float sideCut
    )
    {
        Direction quadDir = quad.getDirection();
        if (quadDir.getAxis() == axis)
        {
            if (axis == Direction.Axis.Y)
            {
                return QuadModifier.geometry(quad).apply(Modifiers.cutTopBottom(capStart, capStart, capEnd, capEnd));
            }
            else
            {
                return QuadModifier.geometry(quad).apply(Modifiers.cutSide(capStart, capStart, capEnd, capEnd));
            }
        }
        else
        {
            if (axis == Direction.Axis.Y)
            {
                return QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(quadDir.getClockWise(), sideCut))
                        .apply(Modifiers.cutSideLeftRight(quadDir.getCounterClockWise(), sideCut))
                        .apply(Modifiers.setPosition(sideCut));
            }
            else if (Utils.isY(quadDir))
            {
                return QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(axisToDir(axis, true).getClockWise(), sideCut))
                        .apply(Modifiers.cutTopBottom(axisToDir(axis, false).getClockWise(), sideCut))
                        .apply(Modifiers.setPosition(sideCut));
            }
            else
            {
                return QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(true, sideCut))
                        .apply(Modifiers.cutSideUpDown(false, sideCut))
                        .apply(Modifiers.setPosition(sideCut));
            }
        }
    }

    private static Direction axisToDir(Direction.Axis axis, boolean positive)
    {
        return switch (axis)
        {
            case X -> positive ? Direction.EAST : Direction.WEST;
            case Y -> positive ? Direction.UP : Direction.DOWN;
            case Z -> positive ? Direction.SOUTH : Direction.NORTH;
        };
    }
}