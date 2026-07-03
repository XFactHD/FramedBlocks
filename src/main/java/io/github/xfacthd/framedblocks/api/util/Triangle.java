package io.github.xfacthd.framedblocks.api.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/// Represents a triangle made up of the given vertices in counterclockwise order.
///
/// @param vertex0 The first vertex
/// @param vertex1 The second vertex
/// @param vertex2 The third vertex
/// @param edge1   The edge between the first and second vertex
/// @param edge2   The edge between the first and third vertex
public record Triangle(Vec3 vertex0, Vec3 vertex1, Vec3 vertex2, Vec3 edge1, Vec3 edge2) {
    /// @param vertex0 The first vertex
    /// @param vertex1 The second vertex
    /// @param vertex2 The third vertex
    public Triangle(Vec3 vertex0, Vec3 vertex1, Vec3 vertex2) {
        this(vertex0, vertex1, vertex2, vertex1.subtract(vertex0), vertex2.subtract(vertex0));
    }

    /// {@return whether the given ray starting at the given origin intersects this triangle}
    ///
    /// @param rayOrigin The origin of the ray
    /// @param rayVector The normalized direction of the vector
    public boolean intersects(Vec3 rayOrigin, Vec3 rayVector) {
        return !Mth.equal(0, computeIntersectDistance(rayOrigin, rayVector));
    }

    /// {@return the intersection point between the given ray from the given origin and this triangle}
    ///
    /// @param rayOrigin The origin of the ray
    /// @param rayVector The normalized direction of the vector
    public @Nullable Vec3 clip(Vec3 rayOrigin, Vec3 rayVector) {
        double t = computeIntersectDistance(rayOrigin, rayVector);
        return Mth.equal(t, 0) ? null : rayOrigin.add(rayVector.scale(t));
    }

    /// Perform a ray-triangle intersection via the Möller-Trumbore algorithm.
    ///
    /// [Wikipedia](https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm)
    /// [Scratchapixel](https://www.scratchapixel.com/lessons/3d-basic-rendering/ray-tracing-rendering-a-triangle/moller-trumbore-ray-triangle-intersection.html)
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
