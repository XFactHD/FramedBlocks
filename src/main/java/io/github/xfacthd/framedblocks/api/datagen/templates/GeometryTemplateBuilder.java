package io.github.xfacthd.framedblocks.api.datagen.templates;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;

import java.util.EnumSet;

/// Builder for creating geometry templates during datagen.
@ApiStatus.NonExtendable
public abstract class GeometryTemplateBuilder {
    /// Add a cube with the given extents and the given visible face.
    ///
    /// @param minX The minimum X coordinate of the cube
    /// @param minY The minimum Y coordinate of the cube
    /// @param minZ The minimum Z coordinate of the cube
    /// @param maxX The maximum X coordinate of the cube
    /// @param maxY The maximum Y coordinate of the cube
    /// @param maxZ The maximum Z coordinate of the cube
    /// @param face The visible face
    /// @return this builder
    public final GeometryTemplateBuilder cube(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Direction face) {
        return cube(minX, minY, minZ, maxX, maxY, maxZ, EnumSet.of(face));
    }

    /// Add a cube with the given extents and the given visible faces.
    ///
    /// @param minX  The minimum X coordinate of the cube
    /// @param minY  The minimum Y coordinate of the cube
    /// @param minZ  The minimum Z coordinate of the cube
    /// @param maxX  The maximum X coordinate of the cube
    /// @param maxY  The maximum Y coordinate of the cube
    /// @param maxZ  The maximum Z coordinate of the cube
    /// @param faces The visible faces
    /// @return this builder
    public abstract GeometryTemplateBuilder cube(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, EnumSet<Direction> faces);

    /// Add a cube with the given horizontal visible face. The face is shaped as a horizontal band with the given width, horizontally
    /// centered, given min and max Y coordinates at the given depth. A depth of 0 puts the band fully "in" the block, a depth of
    /// 16 puts the band at the block volume's outer perimeter.
    ///
    /// @param face  The active face
    /// @param width The width of the band
    /// @param depth The depth to place the band at
    /// @param minY  The minimum Y coordinate of the band
    /// @param maxY  The maximum Y coordinate of the band
    /// @return this builder
    public final GeometryTemplateBuilder singleFaceHorizontalBand(Direction face, float width, float depth, float minY, float maxY) {
        Preconditions.checkArgument(!DirUtils.isY(face), "Face must be horizontal");

        Direction.Axis perp = face.getClockWise().getAxis();
        float parCenterOff = 8F - depth;
        float perpCenterOff = width / 2F;
        float minX = 8F - (parCenterOff * face.getStepX()) + (perpCenterOff * perp.getNegative().getStepX());
        float minZ = 8F - (parCenterOff * face.getStepZ()) + (perpCenterOff * perp.getNegative().getStepZ());
        float maxX = 8F - (parCenterOff * face.getStepX()) + (perpCenterOff * perp.getPositive().getStepX());
        float maxZ = 8F - (parCenterOff * face.getStepZ()) + (perpCenterOff * perp.getPositive().getStepZ());
        return cube(minX, minY, minZ, maxX, maxY, maxZ, face);
    }

    protected abstract JsonElement toJson();
}
