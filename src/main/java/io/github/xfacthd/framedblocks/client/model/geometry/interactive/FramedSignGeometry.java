package io.github.xfacthd.framedblocks.client.model.geometry.interactive;

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

public class FramedSignGeometry extends Geometry
{
    private static final float Y_OFF = 1.75F/16F;
    private static final float POS = 9F/16F;
    private final Direction dir;
    private final float rotDegrees;

    public FramedSignGeometry(GeometryFactory.Context ctx)
    {
        int rotation = ctx.state().getValue(BlockStateProperties.ROTATION_16);
        this.dir = Direction.from2DDataValue(rotation / 4);
        this.rotDegrees = (float)(rotation % 4) * -22.5F;
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (quadDir.getAxis() == dir.getAxis())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.DOWN, .5F))
                    .apply(Modifiers.setPosition(POS))
                    .apply(Modifiers.offset(Direction.UP, Y_OFF))
                    .apply(Modifiers.rotateCentered(Direction.Axis.Y, rotDegrees, false))
                    .export(quadMap, null);
        }
        else if (DirUtils.isY(quadDir))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getAxis(), 9F/16F))
                    .applyIf(Modifiers.setPosition(.5F), quadDir == Direction.DOWN)
                    .apply(Modifiers.offset(Direction.UP, Y_OFF))
                    .apply(Modifiers.rotateCentered(Direction.Axis.Y, rotDegrees, false))
                    .export(quadMap, null);
        }
        else
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(7F/16F, .5F, 9F/16F, 1F))
                    .apply(Modifiers.offset(Direction.UP, Y_OFF))
                    .apply(Modifiers.rotateCentered(Direction.Axis.Y, rotDegrees, false))
                    .export(quadMap, null);
        }

        if (!DirUtils.isY(quadDir))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(7F/16F, 0F, 9F/16F, 9.75F/16F))
                    .apply(Modifiers.setPosition(POS))
                    .apply(Modifiers.rotateCentered(Direction.Axis.Y, rotDegrees, false))
                    .export(quadMap, null);
        }
        else if (quadDir == Direction.DOWN)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(7F/16F, 7F/16F, 9F/16F, 9F/16F))
                    .apply(Modifiers.rotateCentered(Direction.Axis.Y, rotDegrees, false))
                    .export(quadMap, Direction.DOWN);
        }
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }
}
