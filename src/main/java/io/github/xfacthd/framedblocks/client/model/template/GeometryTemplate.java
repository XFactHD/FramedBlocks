package io.github.xfacthd.framedblocks.client.model.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

record GeometryTemplate(List<Cube> cubes) {
    static final Codec<GeometryTemplate> CODEC = Cube.CODEC.listOf()
            .fieldOf("elements")
            .xmap(GeometryTemplate::new, GeometryTemplate::cubes)
            .codec();
    static final GeometryTemplate SINGLE_CUBE = Util.make(() -> {
        Face[] faces = Arrays.stream(Direction.values()).map(Face::new).toArray(Face[]::new);
        Cube cube = new Cube(new Vector3f(0, 0, 0), new Vector3f(16, 16, 16), faces);
        return new GeometryTemplate(List.of(cube));
    });

    record Cube(Vector3fc from, Vector3fc to, Face[] faces) {
        private static final Codec<Cube> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ExtraCodecs.VECTOR3F.fieldOf("from").forGetter(Cube::from),
                ExtraCodecs.VECTOR3F.fieldOf("to").forGetter(Cube::to),
                Codec.withAlternative(
                        Codec.unboundedMap(Direction.CODEC, Codec.BOOL),
                        Codec.unboundedMap(Direction.CODEC, Direction.CODEC.optionalFieldOf("cullface").codec().xmap(Optional::isPresent, _ -> Optional.empty()))
                ).fieldOf("faces").forGetter(Cube::facesForSerialization)
        ).apply(inst, Cube::new));

        Cube(Vector3fc from, Vector3fc to, Map<Direction, Boolean> faces) {
            this(from, to, faces.entrySet().stream().map(Face::new).toArray(Face[]::new));
        }

        private Map<Direction, Boolean> facesForSerialization() {
            Map<Direction, Boolean> faceByDir = new HashMap<>();
            for (Face face : faces) {
                faceByDir.put(face.normal, face.cullable);
            }
            return faceByDir;
        }
    }

    record Face(Direction normal, boolean cullable) {
        private Face(Map.Entry<Direction, Boolean> entry) {
            this(entry.getKey(), entry.getValue());
        }

        private Face(Direction dir) {
            this(dir, true);
        }
    }
}
