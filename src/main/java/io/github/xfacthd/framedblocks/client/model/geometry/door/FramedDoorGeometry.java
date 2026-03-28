package io.github.xfacthd.framedblocks.client.model.geometry.door;

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
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import org.jspecify.annotations.Nullable;

public class FramedDoorGeometry extends Geometry {
    private final Direction dir;
    private final boolean hingeRight;
    private final boolean open;
    private final boolean iron;

    private FramedDoorGeometry(GeometryFactory.Context ctx, boolean iron) {
        this.dir = ctx.state().getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.hingeRight = ctx.state().getValue(BlockStateProperties.DOOR_HINGE) == DoorHingeSide.RIGHT;
        this.open = ctx.state().getValue(BlockStateProperties.OPEN);
        this.iron = iron;
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction faceDir = dir;
        if (open) {
            faceDir = hingeRight ? faceDir.getCounterClockWise() : faceDir.getClockWise();
        }

        Direction quadDir = quad.direction();
        if (DirUtils.isY(quadDir)) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(faceDir, 3F/16F))
                    .export(quadMap, quadDir);
        } else {
            if (quadDir == faceDir) {
                QuadModifier.of(quad)
                        .apply(Modifiers.setPosition(3F/16F))
                        .export(quadMap, null);
            } else {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(faceDir, 3F/16F))
                        .export(quadMap, quadDir);
            }
        }
    }

    @Override
    public boolean useBaseModel() {
        return iron;
    }

    public static FramedDoorGeometry wood(GeometryFactory.Context ctx) {
        return new FramedDoorGeometry(ctx, false);
    }

    public static FramedDoorGeometry iron(GeometryFactory.Context ctx) {
        return new FramedDoorGeometry(ctx, true);
    }
}
