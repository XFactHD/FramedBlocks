package xfacthd.framedblocks.common.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import xfacthd.framedblocks.common.block.*;

import java.util.function.BiPredicate;
import java.util.function.Function;

public enum BlockType
{
    FRAMED_CUBE                 (false, (state, dir) -> true, VoxelShapes.fullCube()),
    FRAMED_SLOPE                (true,  FramedSlopeBlock.CTM_PREDICATE, FramedSlopeBlock::generateShapes),
    FRAMED_CORNER_SLOPE         (true,  FramedCornerSlopeBlock.CTM_PREDICATE, FramedCornerSlopeBlock::generateCornerShapes),
    FRAMED_INNER_CORNER_SLOPE   (true,  FramedCornerSlopeBlock.CTM_PREDICATE_INNER, FramedCornerSlopeBlock::generateInnerCornerShapes),
    FRAMED_PRISM_CORNER         (true,  FramedThreewayCornerBlock::generatePrismShapes),
    FRAMED_INNER_PRISM_CORNER   (true,  FramedThreewayCornerBlock.CTM_PREDICATE, FramedThreewayCornerBlock::generateInnerPrismShapes),
    FRAMED_THREEWAY_CORNER      (true,  FramedThreewayCornerBlock::generateThreewayShapes),
    FRAMED_INNER_THREEWAY_CORNER(true,  FramedThreewayCornerBlock.CTM_PREDICATE, FramedThreewayCornerBlock::generateInnerThreewayShapes),
    FRAMED_SLAB                 (false, FramedSlabBlock.CTM_PREDICATE, FramedSlabBlock::generateShapes),
    FRAMED_PANEL                (false, FramedPanelBlock.CTM_PREDICATE, FramedPanelBlock::generateShapes),
    FRAMED_CORNER_PILLAR        (false, FramedCornerPillarBlock::generateShapes),
    FRAMED_STAIRS               (false, FramedStairsBlock.CTM_PREDICATE),
    FRAMED_WALL                 (false),
    FRAMED_FENCE                (false),
    FRAMED_GATE                 (false),
    FRAMED_DOOR                 (false, FramedDoorBlock.CTM_PREDICATE),
    FRAMED_TRAPDOOR             (false, FramedTrapDoorBlock.CTM_PREDICATE),
    FRAMED_PRESSURE_PLATE       (false),
    FRAMED_LADDER               (false),
    FRAMED_BUTTON               (false),
    FRAMED_LEVER                (false),
    FRAMED_SIGN                 (false, (states) -> null),
    FRAMED_WALL_SIGN            (false, FramedWallSignBlock::generateShapes),
    FRAMED_COLLAPSIBLE_BLOCK    (true);

    private final boolean specialHitbox;
    private final BiPredicate<BlockState, Direction> ctmPredicate;
    private final Function<ImmutableList<BlockState>, ImmutableMap<BlockState, VoxelShape>> shapeGen;

    BlockType(boolean specialHitbox)
    {
        this (specialHitbox, (state, dir) -> false);
    }

    BlockType(boolean specialHitbox, BiPredicate<BlockState, Direction> ctmPredicate)
    {
        this(specialHitbox, ctmPredicate, states -> ImmutableMap.<BlockState, VoxelShape>builder().build());
    }

    BlockType(boolean specialHitbox, Function<ImmutableList<BlockState>, ImmutableMap<BlockState, VoxelShape>> shapeGen)
    {
        this(specialHitbox, (state, dir) -> false, shapeGen);
    }

    BlockType(boolean specialHitbox, BiPredicate<BlockState, Direction> ctmPredicate, VoxelShape shape)
    {
        this(specialHitbox, ctmPredicate, states ->
        {
            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
            states.forEach(state -> builder.put(state, shape));
            return builder.build();
        });
    }

    BlockType(boolean specialHitbox, BiPredicate<BlockState, Direction> ctmPredicate, Function<ImmutableList<BlockState>, ImmutableMap<BlockState, VoxelShape>> shapeGen)
    {
        this.specialHitbox = specialHitbox;
        this.ctmPredicate = ctmPredicate;
        this.shapeGen = shapeGen;
    }

    public boolean hasSpecialHitbox() { return specialHitbox; }

    public BiPredicate<BlockState, Direction> getCtmPredicate() { return ctmPredicate; }

    public ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        return shapeGen.apply(states);
    }
}