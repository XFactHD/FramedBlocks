package xfacthd.framedblocks.client.model.door;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedTrapDoorGeometry extends Geometry
{
    private static final float DEPTH = 3F/16F;
    private static final Vector3f ZERO = new Vector3f();

    private final Direction dir;
    private final boolean top;
    private final boolean open;
    private final boolean rotate;
    private final Direction.Axis rotAxis;
    private final Vector3f rotOrigin;
    private final int rotAngle;

    public FramedTrapDoorGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.top = ctx.state().getValue(BlockStateProperties.HALF) == Half.TOP;
        this.open = ctx.state().getValue(BlockStateProperties.OPEN);
        this.rotate = ctx.state().getValue(PropertyHolder.ROTATE_TEXTURE);
        if (rotate)
        {
            this.rotAxis = dir.getClockWise().getAxis();
            boolean positive = Utils.isPositive(dir);
            float xzOrigin = positive ? 1.5F/16F : 14.5F/16F;
            this.rotOrigin = new Vector3f(xzOrigin, top ? 14.5F/16F : 1.5F/16F, xzOrigin);
            this.rotAngle = (positive ^ Utils.isZ(dir)) == top ? -90 : 90;
        }
        else
        {
            this.rotAxis = Direction.Axis.X;
            this.rotOrigin = ZERO;
            this.rotAngle = 0;
        }
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (open && !rotate)
        {
            if (quadDir == dir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.setPosition(DEPTH))
                        .export(quadMap.get(null));
            }
            else if (Utils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir, DEPTH))
                        .export(quadMap.get(quadDir));
            }
            else
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(dir, DEPTH))
                        .export(quadMap.get(quadDir));
            }
        }
        else
        {
            Direction topFace = top ? Direction.UP : Direction.DOWN;
            if (quadDir == topFace.getOpposite())
            {
                Direction exportDir = open ? dir.getOpposite() : null;
                QuadModifier.of(quad)
                        .apply(Modifiers.setPosition(DEPTH))
                        .applyIf(Modifiers.rotate(rotAxis, rotOrigin, rotAngle, false), open)
                        .export(quadMap.get(exportDir));
            }
            else if (open /*&& rotate*/ && quadDir == topFace)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.rotate(rotAxis, rotOrigin, rotAngle, false))
                        .export(quadMap.get(null));
            }
            else if (!Utils.isY(quadDir))
            {
                Direction exportDir = quadDir;
                if (open && quadDir.getAxis() == dir.getAxis())
                {
                    boolean up = (quadDir == dir) != top;
                    exportDir = up ? Direction.UP : Direction.DOWN;
                }
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(top, DEPTH))
                        .applyIf(Modifiers.rotate(rotAxis, rotOrigin, rotAngle, false), open)
                        .export(quadMap.get(exportDir));
            }
        }
    }
}
