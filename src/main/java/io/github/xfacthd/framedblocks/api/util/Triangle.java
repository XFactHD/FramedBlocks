package io.github.xfacthd.framedblocks.api.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record Triangle(Vec3 vertex0, Vec3 vertex1, Vec3 vertex2, Vec3 edge1, Vec3 edge2) {
    public Triangle(Vec3 vertex0, Vec3 vertex1, Vec3 vertex2) {
        this(vertex0, vertex1, vertex2, vertex1.subtract(vertex0), vertex2.subtract(vertex0));
    }

    public boolean intersects(Vec3 rayOrigin, Vec3 rayVector) {
        return !Mth.equal(0, computeIntersectDistance(rayOrigin, rayVector));
    }

    public @Nullable Vec3 clip(Vec3 rayOrigin, Vec3 rayVector) {
        double t = computeIntersectDistance(rayOrigin, rayVector);
        return Mth.equal(t, 0) ? null : rayOrigin.add(rayVector.scale(t));
    }

    /**
     * Ray-triangle intersection via the Möller-Trumbore algorithm
     * <p>
     * <a href=https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm>Wikipedia</a>
     * <br>
     * <a href=https://www.scratchapixel.com/lessons/3d-basic-rendering/ray-tracing-rendering-a-triangle/moller-trumbore-ray-triangle-intersection.html>Scratchapixel</a>
     */
    private double computeIntersectDistance(Vec3 rayOrigin, Vec3 rayVector) {
        Vec3 pvec = rayVector.cross(edge2);
        double det = edge1.dot(pvec);
        if (Mth.equal(det, 0D)) {
            return 0D; // Ray is parallel to triangle
        }

        double invDet = 1D / det;
        Vec3 tvec = rayOrigin.subtract(vertex0);
        double u = invDet * tvec.dot(pvec);
        if (u < 0D || u > 1D) {
            return 0D;
        }

        Vec3 qvec = tvec.cross(edge1);
        double v = invDet * rayVector.dot(qvec);
        if (v < 0D || u + v > 1D) {
            return 0D;
        }

        // Compute t to find the intersection point along the ray
        return invDet * edge2.dot(qvec);
    }
}
