package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
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
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class FramedMiniCubeGeometry extends Geometry
{
    private static final Vector3f ORIGIN_BOTTOM = new Vector3f(.5F, 0F, .5F);
    private static final Vector3f ORIGIN_TOP = new Vector3f(.5F, 1F, .5F);

    private final float rotAngle;
    private final Direction bottomFace;
    private final Vector3f origin;

    public FramedMiniCubeGeometry(GeometryFactory.Context ctx)
    {
        int rot = ctx.state().getValue(BlockStateProperties.ROTATION_16);
        this.rotAngle = (4 - (rot % 4)) * 22.5F;
        boolean top = ctx.state().getValue(FramedProperties.TOP);
        this.bottomFace = top ? Direction.UP : Direction.DOWN;
        this.origin = top ? ORIGIN_TOP : ORIGIN_BOTTOM;
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        QuadModifier.of(quad)
                .apply(Modifiers.scaleFace(.5F, origin))
                .applyIf(Modifiers.setPosition(.5F), quadDir == bottomFace.getOpposite())
                .applyIf(Modifiers.setPosition(.75F), !DirUtils.isY(quadDir))
                .apply(Modifiers.rotate(Direction.Axis.Y, origin, rotAngle, false))
                .export(quadMap, quadDir == bottomFace ? quadDir : null);
    }
}
