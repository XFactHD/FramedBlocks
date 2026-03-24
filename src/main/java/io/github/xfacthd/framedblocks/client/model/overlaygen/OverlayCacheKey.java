package io.github.xfacthd.framedblocks.client.model.overlaygen;

import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.joml.Vector3fc;

record OverlayCacheKey(
        Direction face,
        Vector3fc pos0,
        Vector3fc pos1,
        Vector3fc pos2,
        Vector3fc pos3,
        BakedNormals normals,
        Material.Baked material,
        boolean forceTranslucent,
        boolean forceEmissive
) implements OverlayQuadGenerator.VertexCoordProvider
{
    public OverlayCacheKey(BakedQuad quad, Material.Baked material, boolean forceTranslucent, boolean forceEmissive)
    {
        this(quad.direction(), quad.position0(), quad.position1(), quad.position2(), quad.position3(), quad.bakedNormals(), material, forceTranslucent, forceEmissive);
    }

    @Override
    public Vector3fc pos(int vert)
    {
        return switch (vert)
        {
            case 0 -> pos0;
            case 1 -> pos1;
            case 2 -> pos2;
            case 3 -> pos3;
            default -> throw new IndexOutOfBoundsException(vert);
        };
    }
}
