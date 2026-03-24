package io.github.xfacthd.framedblocks.api.shapes;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ShapeGenerator
{
    ShapeGenerator EMPTY = _ -> EmptyShapeContainer.INSTANCE;

    ShapeContainer generatePrimary(List<BlockState> states);

    default ShapeContainer generateOcclusion(List<BlockState> states)
    {
        return ShapeContainer.EMPTY;
    }

    static ShapeGenerator singleShape(VoxelShape shape, @Nullable VoxelShape occlusionShape)
    {
        return new SingleShapeGenerator(shape, occlusionShape);
    }
}
