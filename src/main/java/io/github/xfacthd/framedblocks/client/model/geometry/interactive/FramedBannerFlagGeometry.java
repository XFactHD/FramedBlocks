package io.github.xfacthd.framedblocks.client.model.geometry.interactive;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMergers;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public final class FramedBannerFlagGeometry extends Geometry {
    public static final StateMerger STATE_MERGER = Util.make(() -> {
        Set<Property<?>> properties = new HashSet<>(StateMergers.IGNORED_PROPS);
        properties.remove(FramedProperties.GLOWING);
        return StateMergers.ignoring(properties);
    });

    private final Direction dir;
    private final boolean top;

    public FramedBannerFlagGeometry(GeometryFactory.Context ctx) {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        // Abuse glowing flag to indicate top half
        this.top = ctx.state().getValue(FramedProperties.GLOWING);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        Direction quadDir = quad.direction();
        if ((top && quadDir == Direction.DOWN) || (!top && quadDir == Direction.UP)) {
            return;
        }

        float yOff = top ? 1F : 2F;
        QuadModifier modifier = QuadModifier.of(quad)
                .apply(Modifiers.offset(Direction.DOWN, yOff));

        if (quadDir == dir) {
            modifier = modifier.apply(Modifiers.setPosition(1F / 16F));
        } else if (quadDir != dir.getOpposite()) {
            modifier = modifier.apply(Modifiers.cut(dir, 1F/16F));
        }

        // Rotate opposite the rotation applied in the BER to simplify transformations in the BER
        modifier.apply(Modifiers.rotateCentered(Direction.Axis.Y, dir.toYRot(), false))
                .export(quadMap, null);
    }

    @Override
    public boolean useSolidNoCamoModel() {
        return true;
    }
}
