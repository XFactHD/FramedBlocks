package io.github.xfacthd.framedblocks.client.model.geometry.interactive;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedButtonGeometry extends Geometry {
    protected final Direction dir;
    protected final AttachFace face;
    protected final Direction facing;
    protected final boolean pressed;
    private final boolean useBaseModel;

    public FramedButtonGeometry(GeometryFactory.Context ctx) {
        this.dir = ctx.state().getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.face = ctx.state().getValue(BlockStateProperties.ATTACH_FACE);
        this.facing = getFacing(dir, face);
        this.pressed = ctx.state().getValue(BlockStateProperties.POWERED);
        this.useBaseModel = !ctx.state().is(FBContent.BLOCK_FRAMED_BUTTON);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction quadDir = quad.direction();
        if (DirUtils.isY(facing)) {
            generateVerticalButton(quadMap, quad, quadDir);
        } else {
            generateHorizontalButton(quadMap, quad, quadDir);
        }
    }

    private void generateVerticalButton(QuadMapBuilder quadMap, BakedQuad quad, Direction quadDir) {
        if (quadDir.getAxis() == facing.getAxis()) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getAxis(), 10F/16F))
                    .apply(Modifiers.cut(dir.getClockWise().getAxis(), 11F/16F))
                    .applyIf(Modifiers.setPosition(pressed ? 1F/16F : 2F/16F), quadDir == facing)
                    .export(quadMap, quadDir == facing ? null : quadDir);
        } else {
            boolean largeSide = dir.getAxis() == quadDir.getAxis();

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, pressed ? 1F/16F : 2F/16F))
                    .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), largeSide ? 11F/16F : 10F/16F))
                    .apply(Modifiers.setPosition(largeSide ? 10F/16F : 11F/16F))
                    .export(quadMap, null);
        }
    }

    private void generateHorizontalButton(QuadMapBuilder quadMap, BakedQuad quad, Direction quadDir) {
        float height = pressed ? 1F/16F : 2F/16F;
        if (quadDir.getAxis() == facing.getAxis()) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(5F/16F, 6F/16F, 11F/16F, 10F/16F))
                    .applyIf(Modifiers.setPosition(height), quadDir == facing)
                    .export(quadMap, quadDir == facing ? null : quadDir);
        } else if (DirUtils.isY(quadDir)) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, height))
                    .apply(Modifiers.cut(dir.getClockWise().getAxis(), 11F/16F))
                    .apply(Modifiers.setPosition(10F / 16F))
                    .export(quadMap, null);
        } else {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, height))
                    .apply(Modifiers.cut(Direction.Axis.Y, 10F/16F))
                    .apply(Modifiers.setPosition(11F / 16F))
                    .export(quadMap, null);
        }
    }

    @Override
    public boolean useBaseModel() {
        return useBaseModel;
    }

    private static Direction getFacing(Direction dir, AttachFace face) {
        return switch (face) {
            case FLOOR -> Direction.UP;
            case CEILING -> Direction.DOWN;
            case WALL -> dir;
        };
    }
}
