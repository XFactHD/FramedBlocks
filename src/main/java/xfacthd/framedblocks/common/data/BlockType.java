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
    FRAMED_CUBE(false, true, VoxelShapes.fullCube()),
    FRAMED_SLOPE(true, false, FramedSlopeBlock::generateShapes),
    FRAMED_CORNER_SLOPE(true, false, FramedCornerSlopeBlock::generateCornerShapes),
    FRAMED_INNER_CORNER_SLOPE(true, false, FramedCornerSlopeBlock::generateInnerCornerShapes),
    FRAMED_PRISM_CORNER(true, false, FramedThreewayCornerBlock::generatePrismShapes),
    FRAMED_INNER_PRISM_CORNER(true, false, FramedThreewayCornerBlock::generateInnerPrismShapes),
    FRAMED_THREEWAY_CORNER(true, false, FramedThreewayCornerBlock::generateThreewayShapes),
    FRAMED_INNER_THREEWAY_CORNER(true, false, FramedThreewayCornerBlock::generateInnerThreewayShapes),
    FRAMED_SLAB(false, true, FramedSlabBlock::generateShapes),
    FRAMED_PANEL(false, true, FramedPanelBlock::generateShapes),
    //FRAMED_CORNER_PILLAR(false, true, FramedCornerPillarBlock::generateShapes),
    FRAMED_STAIRS(false, true),
    FRAMED_WALL(false, false),
    FRAMED_FENCE(false, false),
    FRAMED_GATE(false, false),
    FRAMED_DOOR(false, true),
    FRAMED_TRAPDOOR(false, true),
    FRAMED_PRESSURE_PLATE(false, false),
    FRAMED_LADDER(false, false),
    FRAMED_BUTTON(false, false),
    FRAMED_LEVER(false, false),
    FRAMED_SIGN(false, false, (states) -> null),
    FRAMED_WALL_SIGN(false, false, FramedWallSignBlock::generateShapes),
    FRAMED_COLLAPSIBLE_BLOCK(true, false);

    private final boolean specialHitbox;
    private final boolean supportCTM;
    private final Function<ImmutableList<BlockState>, ImmutableMap<BlockState, VoxelShape>> shapeGen;

    BlockType(boolean specialHitbox, boolean supportCTM)
    {
        this(specialHitbox, supportCTM, states -> ImmutableMap.<BlockState, VoxelShape>builder().build());
    }

    BlockType(boolean specialHitbox, boolean supportCTM, VoxelShape shape)
    {
        this(specialHitbox, supportCTM, states ->
        {
            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
            states.forEach(state -> builder.put(state, shape));
            return builder.build();
        });
    }

    BlockType(boolean specialHitbox, boolean supportCTM, Function<ImmutableList<BlockState>, ImmutableMap<BlockState, VoxelShape>> shapeGen)
    {
        this.specialHitbox = specialHitbox;
        this.supportCTM = supportCTM;
        this.shapeGen = shapeGen;
    }

    public boolean hasSpecialHitbox() { return specialHitbox; }

    public ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        return shapeGen.apply(states);
    }

    public boolean supportsCTM() { return supportCTM; }
}