package io.github.xfacthd.framedblocks.api.shapes;

import it.unimi.dsi.fastutil.objects.Reference2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Map;

final class MapBackedShapeLookup implements ShapeLookup
{
    private final Map<BlockState, VoxelShape> shapes;
    private final Map<BlockState, VoxelShape> occlusionShapes;
    private final Reference2BooleanFunction<BlockState> occludesBeaconBeam;

    MapBackedShapeLookup(MapBackedShapeContainer shapes, @Nullable MapBackedShapeContainer occlusionShapes)
    {
        this.shapes = shapes.shapes();
        this.occlusionShapes = occlusionShapes != null ? occlusionShapes.shapes() : this.shapes;
        this.occludesBeaconBeam = computeBeaconBeamOcclusion(shapes);
    }

    @Override
    public VoxelShape getShape(BlockState state)
    {
        return shapes.get(state);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state)
    {
        return occlusionShapes.get(state);
    }

    @Override
    public boolean hasSeparateOcclusionShapes()
    {
        return shapes != occlusionShapes;
    }

    @Override
    public boolean occludesBeaconBeam(BlockState state)
    {
        return occludesBeaconBeam.getBoolean(state);
    }

    private static Reference2BooleanFunction<BlockState> computeBeaconBeamOcclusion(ShapeContainer shapes)
    {
        Reference2BooleanMap<BlockState> beamColorMasking = new Reference2BooleanOpenHashMap<>();
        Reference2BooleanMap<VoxelShape> encounteredShapes = new Reference2BooleanOpenHashMap<>();

        boolean[] hasFalse = new boolean[1];
        boolean[] hasTrue = new boolean[1];
        shapes.forEach((state, shape) ->
        {
            boolean occludes = encounteredShapes.computeIfAbsent(shape, ShapeUtils::occludesBeaconBeam);
            hasFalse[0] |= !occludes;
            hasTrue[0] |= occludes;
            beamColorMasking.put(state, occludes);
        });
        if (!hasFalse[0]) return _ -> true;
        if (!hasTrue[0]) return _ -> false;
        return beamColorMasking;
    }
}
