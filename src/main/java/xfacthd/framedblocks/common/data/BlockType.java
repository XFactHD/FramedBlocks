package xfacthd.framedblocks.common.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import xfacthd.framedblocks.common.block.*;

import java.util.function.Function;

public enum BlockType
{
    FRAMED_CUBE(false, VoxelShapes.fullCube()),
    FRAMED_SLOPE(true, FramedSlopeBlock::generateShapes),
    FRAMED_CORNER_SLOPE(true, FramedCornerSlopeBlock::generateCornerShapes),
    FRAMED_INNER_CORNER_SLOPE(true, FramedCornerSlopeBlock::generateInnerCornerShapes),
    FRAMED_PRISM_CORNER(true, FramedThreewayCornerBlock::generatePrismShapes),
    FRAMED_INNER_PRISM_CORNER(true, FramedThreewayCornerBlock::generateInnerPrismShapes),
    FRAMED_THREEWAY_CORNER(true, FramedThreewayCornerBlock::generateThreewayShapes),
    FRAMED_INNER_THREEWAY_CORNER(true, FramedThreewayCornerBlock::generateInnerThreewayShapes),
    FRAMED_SLAB(false, FramedSlabBlock::generateShapes),
    FRAMED_PANEL(false, FramedPanelBlock::generateShapes),
    FRAMED_CORNER_PILLAR(false, FramedCornerPillarBlock::generateShapes),
    FRAMED_STAIRS(false),
    FRAMED_WALL(false),
    FRAMED_FENCE(false),
    FRAMED_GATE(false),
    FRAMED_DOOR(false),
    FRAMED_TRAPDOOR(false),
    FRAMED_PRESSURE_PLATE(false),
    FRAMED_LADDER(false),
    FRAMED_BUTTON(false),
    FRAMED_LEVER(false),
    FRAMED_SIGN(false, (states) -> null),
    FRAMED_WALL_SIGN(false, FramedWallSignBlock::generateShapes),
    FRAMED_COLLAPSIBLE_BLOCK(true);

    private final boolean specialHitbox;
    private final Function<ImmutableList<BlockState>, ImmutableMap<BlockState, VoxelShape>> shapeGen;

    BlockType(boolean specialHitbox)
    {
        this(specialHitbox, states -> ImmutableMap.<BlockState, VoxelShape>builder().build());
    }

    BlockType(boolean specialHitbox, VoxelShape shape)
    {
        this(specialHitbox, states ->
        {
            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
            states.forEach(state -> builder.put(state, shape));
            return builder.build();
        });
    }

    BlockType(boolean specialHitbox, Function<ImmutableList<BlockState>, ImmutableMap<BlockState, VoxelShape>> shapeGen)
    {
        this.specialHitbox = specialHitbox;
        this.shapeGen = shapeGen;
    }

    public boolean hasSpecialHitbox() { return specialHitbox; }

    public ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        return shapeGen.apply(states);
    }
}