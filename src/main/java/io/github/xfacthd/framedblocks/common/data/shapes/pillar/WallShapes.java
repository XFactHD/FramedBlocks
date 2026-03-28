package io.github.xfacthd.framedblocks.common.data.shapes.pillar;

import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class WallShapes implements ShapeGenerator {
    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generateShapes(states, 14F, 16F);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        // Misuse separate occlusion shape handling for collision shapes
        return generateShapes(states, 24F, 24F);
    }

    private static ShapeContainer generateShapes(List<BlockState> states, float lowHeight, float tallHeight) {
        boolean sameHeight = lowHeight == tallHeight;

        VoxelShape centerLowShape = Block.box(5F, 0F, 5F, 11F, lowHeight, 11F);
        VoxelShape centerTallShape = sameHeight ? centerLowShape : Block.box(5F, 0F, 5F, 11F, tallHeight, 11F);
        VoxelShape postShape = Block.box(4F, 0F, 4F, 12F, tallHeight, 12F);
        VoxelShape wallLowShape = Block.box(5F, 0F, 0F, 11F, lowHeight, 5F);
        VoxelShape wallTallShape = sameHeight ? wallLowShape : Block.box(5F, 0F, 0F, 11F, tallHeight, 5F);

        VoxelShape[] wallLowShapes = ShapeUtils.makeHorizontalRotations(wallLowShape, Direction.NORTH);
        VoxelShape[] wallTallShapes = sameHeight ? wallLowShapes : ShapeUtils.makeHorizontalRotations(wallTallShape, Direction.NORTH);

        VoxelShape[] shapes = new VoxelShape[512];
        for (WallSide north : WallBlock.NORTH.getPossibleValues()) {
            for (WallSide east : WallBlock.EAST.getPossibleValues()) {
                for (WallSide south : WallBlock.SOUTH.getPossibleValues()) {
                    for (WallSide west : WallBlock.WEST.getPossibleValues()) {
                        int noUpKey = makeShapeKey(false, north, east, south, west);
                        int upKey = makeShapeKey(true, north, east, south, west);

                        boolean anyTall = north == WallSide.TALL || east == WallSide.TALL || south == WallSide.TALL || west == WallSide.TALL;
                        shapes[noUpKey] = ShapeUtils.or(
                                anyTall ? centerTallShape : centerLowShape,
                                getWallShape(north, Direction.NORTH, wallLowShapes, wallTallShapes),
                                getWallShape(east, Direction.EAST, wallLowShapes, wallTallShapes),
                                getWallShape(south, Direction.SOUTH, wallLowShapes, wallTallShapes),
                                getWallShape(west, Direction.WEST, wallLowShapes, wallTallShapes)
                        );

                        shapes[upKey] = ShapeUtils.or(
                                postShape,
                                getWallShape(north, Direction.NORTH, wallLowShapes, wallTallShapes),
                                getWallShape(east, Direction.EAST, wallLowShapes, wallTallShapes),
                                getWallShape(south, Direction.SOUTH, wallLowShapes, wallTallShapes),
                                getWallShape(west, Direction.WEST, wallLowShapes, wallTallShapes)
                        );
                    }
                }
            }
        }
        shapes[0] = Shapes.block();

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>();
        for (BlockState state : states) {
            int key = makeShapeKey(
                    state.getValue(WallBlock.UP),
                    state.getValue(WallBlock.NORTH),
                    state.getValue(WallBlock.EAST),
                    state.getValue(WallBlock.SOUTH),
                    state.getValue(WallBlock.WEST)
            );
            map.put(state, shapes[key]);
        }
        return ShapeContainer.of(map);
    }

    private static VoxelShape getWallShape(WallSide side, Direction dir, VoxelShape[] lowShapes, VoxelShape[] tallShapes) {
        return switch (side) {
            case NONE -> Shapes.empty();
            case LOW -> lowShapes[dir.get2DDataValue()];
            case TALL -> tallShapes[dir.get2DDataValue()];
        };
    }

    private static int makeShapeKey(boolean up, WallSide north, WallSide east, WallSide south, WallSide west) {
        return ((up ? 1 : 0) << 8) | (north.ordinal() << 6) | (east.ordinal() << 4) | (south.ordinal() << 2) | west.ordinal();
    }
}
