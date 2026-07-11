package io.github.xfacthd.framedblocks.client.model.template;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.github.xfacthd.framedblocks.api.datagen.templates.GeometryTemplateBuilder;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public final class GeometryTemplateBuilderImpl extends GeometryTemplateBuilder {
    private final List<GeometryTemplate.Cube> cubes = new ArrayList<>();

    @Override
    public GeometryTemplateBuilder cube(float fromX, float fromY, float fromZ, float toX, float toY, float toZ, EnumSet<Direction> faces) {
        Vector3fc from = new Vector3f(fromX, fromY, fromZ);
        Vector3fc to = new Vector3f(toX, toY, toZ);

        Preconditions.checkArgument(fromX <= toX && fromY <= toY && fromZ <= toZ, "Min must be smaller than max (min: %s max: %s)", from, to);
        Preconditions.checkArgument(!faces.isEmpty(), "At least one face must be specified");

        Map<Direction, Boolean> faceMap = new EnumMap<>(Direction.class);
        for (Direction face : faces) {
            assertNonZeroArea(from, to, face);
            faceMap.put(face, isCullable(from, to, face));
        }
        cubes.add(new GeometryTemplate.Cube(from, to, faceMap));
        return this;
    }

    private static void assertNonZeroArea(Vector3fc from, Vector3fc to, Direction face) {
        for (Direction.Axis axis : Direction.Axis.VALUES) {
            if (axis == face.getAxis()) {
                continue;
            }

            float min = select(axis, from);
            float max = select(axis, to);
            if (max - min <= 0F) {
                throw new IllegalArgumentException("Face " + face + " is invisible");
            }
        }
    }

    private static boolean isCullable(Vector3fc from, Vector3fc to, Direction face) {
        boolean positive = DirUtils.isPositive(face);
        Vector3fc vec = positive ? to : from;
        return Mth.equal(select(face.getAxis(), vec), positive ? 16 : 0);
    }

    private static float select(Direction.Axis axis, Vector3fc vec) {
        return switch (axis) {
            case X -> vec.x();
            case Y -> vec.y();
            case Z -> vec.z();
        };
    }

    @Override
    protected JsonElement toJson() {
        GeometryTemplate template = new GeometryTemplate(cubes);
        return GeometryTemplate.CODEC.encodeStart(JsonOps.INSTANCE, template).getOrThrow();
    }
}
