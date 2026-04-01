package io.github.xfacthd.framedblocks.client.render.util;

import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;

import java.util.stream.IntStream;

public enum ExtFaceInfo {
    DOWN(Direction.DOWN),
    UP(Direction.UP),
    NORTH(Direction.NORTH),
    SOUTH(Direction.SOUTH),
    WEST(Direction.WEST),
    EAST(Direction.EAST);

    private static final ExtFaceInfo[] BY_FACE = Util.make(() -> {
        ExtFaceInfo[] values = values();
        ExtFaceInfo[] byFace = new ExtFaceInfo[values.length];
        for (ExtFaceInfo value : values) {
            byFace[value.face.ordinal()] = value;
        }
        return byFace;
    });

    private final Direction face;
    private final ExtVertexInfo[] vertices;

    ExtFaceInfo(Direction face) {
        this.face = face;
        FaceInfo faceInfo = FaceInfo.fromFacing(face);
        this.vertices = IntStream.range(0, 4)
                .mapToObj(vert -> ExtVertexInfo.of(vert, faceInfo.getVertexInfo(vert)))
                .toArray(ExtVertexInfo[]::new);
    }

    public ExtVertexInfo vertex(int vert) {
        return vertices[vert];
    }

    public static ExtFaceInfo of(Direction face) {
        return BY_FACE[face.ordinal()];
    }

    public enum TexExtent {
        MIN_U,
        MIN_V,
        MAX_U,
        MAX_V;

        public float select(float minU, float minV, float maxU, float maxV) {
            return switch (this) {
                case MIN_U -> minU;
                case MIN_V -> minV;
                case MAX_U -> maxU;
                case MAX_V -> maxV;
            };
        }
    }

    public record ExtVertexInfo(
            FaceInfo.Extent xFace,
            FaceInfo.Extent yFace,
            FaceInfo.Extent zFace,
            TexExtent uFace,
            TexExtent vFace
    ) {
        private static ExtVertexInfo of(int vertex, FaceInfo.VertexInfo vertInfo) {
            TexExtent uFace = (vertex == 0 || vertex == 1) ? TexExtent.MIN_U : TexExtent.MAX_U;
            TexExtent vFace = (vertex == 0 || vertex == 3) ? TexExtent.MIN_V : TexExtent.MAX_V;
            return new ExtVertexInfo(vertInfo.xFace(), vertInfo.yFace(), vertInfo.zFace(), uFace, vFace);
        }
    }
}
