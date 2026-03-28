package io.github.xfacthd.framedblocks.common.data.skippreds.pillar;

import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.skippreds.CornerDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.HalfDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.TriangleDir;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WallSide;

@SuppressWarnings("JavaExistingMethodCanBeUsed")
public final class PillarDirs {
    public static final class CornerPillar {
        public static HalfDir getHalfDir(Direction dir, Direction side) {
            if (side == dir) {
                return HalfDir.fromDirections(side, dir.getCounterClockWise());
            }
            if (side == dir.getCounterClockWise()) {
                return HalfDir.fromDirections(side, dir);
            }
            return HalfDir.NULL;
        }

        public static CornerDir getCornerDir(Direction dir, Direction side) {
            if (DirUtils.isY(side)) {
                return CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
            }
            return CornerDir.NULL;
        }

        private CornerPillar() { }
    }

    public static final class ThreewayCornerPillar {
        public static TriangleDir getStairDir(Direction dir, boolean top, Direction side) {
            if (side == dir) {
                return TriangleDir.fromDirections(dir.getCounterClockWise(), top ? Direction.UP : Direction.DOWN);
            }
            if (side == dir.getCounterClockWise()) {
                return TriangleDir.fromDirections(dir, top ? Direction.UP : Direction.DOWN);
            }
            if ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
                return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
            }
            return TriangleDir.NULL;
        }

        public static CornerDir getCornerDir(Direction dir, boolean top, Direction side) {
            if ((!top && side == Direction.UP) || (top && side == Direction.DOWN)) {
                return CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
            }
            if (side == dir.getClockWise()) {
                return CornerDir.fromDirections(side, dir, top ? Direction.UP : Direction.DOWN);
            }
            if (side == dir.getOpposite()) {
                return CornerDir.fromDirections(side, dir.getCounterClockWise(), top ? Direction.UP : Direction.DOWN);
            }
            return CornerDir.NULL;
        }

        private ThreewayCornerPillar() { }
    }

    public static final class Wall {
        public static boolean testWallArmDir(BlockState state, BlockState adjState, Direction side) {
            if (!DirUtils.isY(side)) {
                WallSide wallSide = getWallSide(state, side);
                WallSide adjWallSide = getWallSide(adjState, side.getOpposite());
                return wallSide != WallSide.NONE && wallSide == adjWallSide;
            }
            return false;
        }

        public static WallSide getWallSide(BlockState state, Direction side) {
            return switch (side) {
                case NORTH -> state.getValue(WallBlock.NORTH);
                case EAST -> state.getValue(WallBlock.EAST);
                case SOUTH -> state.getValue(WallBlock.SOUTH);
                case WEST -> state.getValue(WallBlock.WEST);
                default -> throw new IllegalArgumentException("Invalid wall arm direction");
            };
        }

        public static boolean isPillarDir(boolean up, Direction side) {
            return up && DirUtils.isY(side);
        }

        private Wall() { }
    }

    public static final class Fence {
        public static boolean testFenceArmDir(BlockState state, BlockState adjState, Direction side) {
            return !DirUtils.isY(side) && hasFenceArm(state, side) && hasFenceArm(adjState, side.getOpposite());
        }

        public static boolean testFenceArmToGateDir(BlockState state, BlockState adjState, Direction side) {
            if (!DirUtils.isY(side) && hasFenceArm(state, side)) {
                Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
                return adjDir.getClockWise().getAxis() == side.getAxis();
            }
            return false;
        }

        private static boolean hasFenceArm(BlockState state, Direction side) {
            return switch (side) {
                case NORTH -> state.getValue(FenceBlock.NORTH);
                case EAST -> state.getValue(FenceBlock.EAST);
                case SOUTH -> state.getValue(FenceBlock.SOUTH);
                case WEST -> state.getValue(FenceBlock.WEST);
                default -> throw new IllegalArgumentException("Invalid fence arm side: " + side);
            };
        }

        public static boolean isPostDir(Direction side) {
            return DirUtils.isY(side);
        }

        private Fence() { }
    }

    public static final class Lattice {
        public static boolean testEarlyExit(boolean xAxis, boolean yAxis, boolean zAxis, Direction side) {
            return switch (side.getAxis()) {
                case X -> !xAxis;
                case Y -> !yAxis;
                case Z -> !zAxis;
            };
        }

        public static boolean isPostDir(boolean xAxis, boolean yAxis, boolean zAxis, Direction side) {
            return switch (side.getAxis()) {
                case X -> xAxis;
                case Y -> yAxis;
                case Z -> zAxis;
            };
        }

        private Lattice() { }
    }

    public static final class ThickLattice {
        public static boolean testEarlyExit(boolean xAxis, boolean yAxis, boolean zAxis, Direction side) {
            return switch (side.getAxis()) {
                case X -> !xAxis;
                case Y -> !yAxis;
                case Z -> !zAxis;
            };
        }

        public static boolean isPillarDir(boolean xAxis, boolean yAxis, boolean zAxis, Direction side) {
            return switch (side.getAxis()) {
                case X -> xAxis;
                case Y -> yAxis;
                case Z -> zAxis;
            };
        }

        private ThickLattice() { }
    }

    public static final class Pillar {
        public static boolean testEarlyExit(Direction.Axis axis, Direction side) {
            return side.getAxis() != axis;
        }

        public static boolean isPillarDir(Direction.Axis axis, Direction side) {
            return side.getAxis() == axis;
        }

        private Pillar() { }
    }

    public static final class HalfPillar {
        public static boolean testEarlyExit(Direction dir, Direction side) {
            return side != dir;
        }

        public static boolean isPillarDir(Direction dir, Direction side) {
            return side == dir;
        }

        private HalfPillar() { }
    }

    public static final class PillarSocket {
        public static HalfDir getHalfDir(Direction dir, Direction side) {
            if (side.getAxis() != dir.getAxis()) {
                return HalfDir.fromDirections(side, dir);
            }
            return HalfDir.NULL;
        }

        public static boolean isPillarDir(Direction dir, Direction side) {
            return side == dir.getOpposite();
        }

        private PillarSocket() { }
    }

    public static final class Post {
        public static boolean testEarlyExit(Direction.Axis axis, Direction side) {
            return side.getAxis() != axis;
        }

        public static boolean isPostDir(Direction.Axis axis, Direction side) {
            return side.getAxis() == axis;
        }

        private Post() { }
    }
}
