package io.github.xfacthd.framedblocks.api.shapes;

import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public final class ReloadableShapeLookup implements ShapeLookup {
    private final ShapeGenerator generator;
    private final List<BlockState> states;
    private ShapeLookup wrapped;

    static ShapeLookup of(ShapeGenerator generator, List<BlockState> states) {
        return Utils.PRODUCTION ? buildLookup(generator, states) : new ReloadableShapeLookup(generator, states);
    }

    private static ShapeLookup buildLookup(ShapeGenerator generator, List<BlockState> states) {
        ShapeContainer shapes = generator.generatePrimary(states);
        ShapeContainer occlusionShapes = generator.generateOcclusion(states);
        return switch (shapes) {
            case EmptyShapeContainer ignored -> {
                if (occlusionShapes != SingleShapeContainer.EMPTY) {
                    throw new IllegalStateException("Cannot use non-empty occlusion shape container with empty primary shape container");
                }
                yield EMPTY;
            }
            case SingleShapeContainer single -> {
                SingleShapeContainer singleOcclusion = SingleShapeContainer.unwrap(occlusionShapes);
                yield new SingleShapeLookup(single, singleOcclusion);
            }
            case MapBackedShapeContainer mapBacked -> {
                MapBackedShapeContainer mapBackedOcclusion = MapBackedShapeContainer.unwrap(occlusionShapes);
                yield new MapBackedShapeLookup(mapBacked, mapBackedOcclusion);
            }
        };
    }

    private ReloadableShapeLookup(ShapeGenerator generator, List<BlockState> states) {
        this.generator = generator;
        this.states = states;
        reload();
        InternalAPI.INSTANCE.registerReloadableShapeLookup(this);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        return wrapped.getShape(state);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state) {
        return wrapped.getOcclusionShape(state);
    }

    @Override
    public boolean hasSeparateOcclusionShapes() {
        return wrapped.hasSeparateOcclusionShapes();
    }

    @Override
    public boolean occludesBeaconBeam(BlockState state) {
        return wrapped.occludesBeaconBeam(state);
    }

    public void reload() {
        wrapped = buildLookup(generator, states);
    }

    public List<BlockState> getStates() {
        return states;
    }
}
