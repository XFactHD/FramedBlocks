package io.github.xfacthd.framedblocks.client.model.geometry.door;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class FramedTrapDoorGeometry extends Geometry
{
    private static final float DEPTH = 3F/16F;
    private static final Vector3f ZERO = new Vector3f();

    private final Direction dir;
    private final boolean top;
    private final boolean open;
    private final boolean rotate;
    private final boolean iron;
    private final Direction.Axis rotAxis;
    private final Vector3f rotOrigin;
    private final int rotAngle;

    private FramedTrapDoorGeometry(GeometryFactory.Context ctx, boolean iron)
    {
        this.dir = ctx.state().getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.top = ctx.state().getValue(BlockStateProperties.HALF) == Half.TOP;
        this.rotate = ctx.state().getValue(PropertyHolder.ROTATE_TEXTURE);
        this.open = ctx.state().getValue(BlockStateProperties.OPEN);
        this.iron = iron;
        if (rotate)
        {
            this.rotAxis = dir.getClockWise().getAxis();
            boolean positive = DirUtils.isPositive(dir);
            float xzOrigin = positive ? 1.5F/16F : 14.5F/16F;
            this.rotOrigin = new Vector3f(xzOrigin, top ? 14.5F/16F : 1.5F/16F, xzOrigin);
            this.rotAngle = (positive ^ DirUtils.isZ(dir)) == top ? -90 : 90;
        }
        else
        {
            this.rotAxis = Direction.Axis.X;
            this.rotOrigin = ZERO;
            this.rotAngle = 0;
        }
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (open && !rotate)
        {
            if (quadDir == dir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.setPosition(DEPTH))
                        .export(quadMap, null);
            }
            else if (DirUtils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir, DEPTH))
                        .export(quadMap, quadDir);
            }
            else
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir, DEPTH))
                        .export(quadMap, quadDir);
            }
        }
        else
        {
            Direction topFace = top ? Direction.UP : Direction.DOWN;
            if (quadDir == topFace.getOpposite())
            {
                Direction exportDir = open ? dir.getOpposite() : quadDir;
                QuadModifier.of(quad)
                        .apply(Modifiers.setPosition(DEPTH))
                        .applyIf(Modifiers.rotate(rotAxis, rotOrigin, rotAngle, false), open)
                        .export(quadMap, exportDir);
            }
            else if (open /*&& rotate*/ && quadDir == topFace)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.rotate(rotAxis, rotOrigin, rotAngle, false))
                        .export(quadMap, null);
            }
            else if (!DirUtils.isY(quadDir))
            {
                Direction exportDir = quadDir;
                if (open && quadDir.getAxis() == dir.getAxis())
                {
                    boolean up = (quadDir == dir) != top;
                    exportDir = up ? Direction.UP : Direction.DOWN;
                }
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, DEPTH))
                        .applyIf(Modifiers.rotate(rotAxis, rotOrigin, rotAngle, false), open)
                        .export(quadMap, exportDir);
            }
        }
    }

    @Override
    public boolean useBaseModel()
    {
        return iron;
    }

    public static FramedTrapDoorGeometry wood(GeometryFactory.Context ctx)
    {
        return new FramedTrapDoorGeometry(ctx, false);
    }

    public static FramedTrapDoorGeometry iron(GeometryFactory.Context ctx)
    {
        return new FramedTrapDoorGeometry(ctx, true);
    }
}
