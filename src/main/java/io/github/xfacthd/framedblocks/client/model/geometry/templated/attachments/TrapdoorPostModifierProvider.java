package io.github.xfacthd.framedblocks.client.model.geometry.templated.attachments;

import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.template.PostModifierProvider;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public final class TrapdoorPostModifierProvider implements PostModifierProvider {
    private static final PostModifierProvider[] PROVIDERS = Util.make(() -> {
        PostModifierProvider[] providers = new PostModifierProvider[8];
        for (Half half : Half.values()) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                providers[index(dir, half)] = new TrapdoorPostModifierProvider(dir, half == Half.TOP);
            }
        }
        return providers;
    });

    private final Direction dir;
    private final Direction topFace;
    private final QuadModifier.Modifier modifier;

    private TrapdoorPostModifierProvider(Direction dir, boolean top) {
        this.dir = dir;
        this.topFace = top ? Direction.UP : Direction.DOWN;
        Direction.Axis rotAxis = dir.getClockWise().getAxis();
        boolean positive = DirUtils.isPositive(dir);
        float xzOrigin = positive ? 1.5F/16F : 14.5F/16F;
        Vector3f rotOrigin = new Vector3f(xzOrigin, top ? 14.5F/16F : 1.5F/16F, xzOrigin);
        float rotAngle = (positive ^ DirUtils.isZ(dir)) == top ? -90 : 90;
        this.modifier = Modifiers.rotate(rotAxis, rotOrigin, rotAngle, false);
    }

    @Override
    public void collectPostModifiers(Direction normal, boolean cullable, Consumer<QuadModifier.Modifier> collector) {
        collector.accept(modifier);
    }

    @Override
    public @Nullable Direction transformCullFace(Direction normal, @Nullable Direction cullface) {
        if (normal == topFace) {
            return null;
        }
        if (normal == topFace.getOpposite()) {
            return dir.getOpposite();
        }
        if (normal == dir) {
            return topFace.getOpposite();
        }
        if (normal == dir.getOpposite()) {
            return topFace;
        }
        return cullface;
    }

    public static PostModifierProvider get(BlockState state) {
        Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Half half = state.getValue(BlockStateProperties.HALF);
        return PROVIDERS[index(dir, half)];
    }

    private static int index(Direction dir, Half half) {
        return dir.get2DDataValue() + 4 * half.ordinal();
    }
}
