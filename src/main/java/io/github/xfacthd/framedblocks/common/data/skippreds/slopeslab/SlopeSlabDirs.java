package io.github.xfacthd.framedblocks.common.data.skippreds.slopeslab;

import io.github.xfacthd.framedblocks.common.data.skippreds.HalfDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.HalfTriangleDir;
import net.minecraft.core.Direction;

@SuppressWarnings("JavaExistingMethodCanBeUsed")
public final class SlopeSlabDirs {
    public static final class SlopeSlab {
        public static HalfTriangleDir getTriDir(Direction dir, boolean topHalf, boolean top, Direction side) {
            if (side.getAxis() == dir.getClockWise().getAxis()) {
                Direction longEdge = top ? Direction.UP : Direction.DOWN;
                return HalfTriangleDir.fromDirections(longEdge, dir, topHalf == top);
            }
            return HalfTriangleDir.NULL;
        }

        public static HalfDir getHalfDir(Direction dir, boolean topHalf, Direction side) {
            if (side == dir) {
                return HalfDir.fromDirections(
                        side,
                        topHalf ? Direction.UP : Direction.DOWN
                );
            }
            return HalfDir.NULL;
        }

        private SlopeSlab() { }
    }

    public static final class ElevatedSlopeSlab {
        public static HalfTriangleDir getTriDir(Direction dir, boolean top, Direction side) {
            if (side.getAxis() == dir.getClockWise().getAxis()) {
                Direction longEdge = top ? Direction.UP : Direction.DOWN;
                return HalfTriangleDir.fromDirections(longEdge, dir, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static HalfDir getHalfDir(Direction dir, boolean top, Direction side) {
            if (side == dir.getOpposite()) {
                return HalfDir.fromDirections(
                        side,
                        top ? Direction.UP : Direction.DOWN
                );
            }
            return HalfDir.NULL;
        }

        private ElevatedSlopeSlab() { }
    }

    public static final class CompoundSlopeSlab {
        public static HalfTriangleDir getTriDir(Direction dir, Direction side) {
            if (side.getAxis() == dir.getClockWise().getAxis()) {
                return HalfTriangleDir.fromDirections(Direction.DOWN, dir, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static HalfDir getHalfDir(Direction dir, Direction side) {
            if (side == dir) {
                return HalfDir.fromDirections(side, Direction.UP);
            }
            if (side == dir.getOpposite()) {
                return HalfDir.fromDirections(side, Direction.DOWN);
            }
            return HalfDir.NULL;
        }

        private CompoundSlopeSlab() { }
    }

    public static final class FlatSlopeSlabCorner {
        public static HalfTriangleDir getTriDir(Direction dir, boolean topHalf, boolean top, Direction side) {
            if (side == dir || side == dir.getCounterClockWise()) {
                Direction longEdge = top ? Direction.UP : Direction.DOWN;
                Direction shortEdge = side == dir ? dir.getCounterClockWise() : dir;
                return HalfTriangleDir.fromDirections(longEdge, shortEdge, topHalf == top);
            }
            return HalfTriangleDir.NULL;
        }

        private FlatSlopeSlabCorner() { }
    }

    public static final class FlatInnerSlopeSlabCorner {
        public static HalfTriangleDir getTriDir(Direction dir, boolean topHalf, boolean top, Direction side) {
            if (side == dir.getOpposite() || side == dir.getClockWise()) {
                Direction longEdge = top ? Direction.UP : Direction.DOWN;
                Direction shortEdge = side == dir.getOpposite() ? dir.getCounterClockWise() : dir;
                return HalfTriangleDir.fromDirections(longEdge, shortEdge, topHalf == top);
            }
            return HalfTriangleDir.NULL;
        }

        public static HalfDir getHalfDir(Direction dir, boolean topHalf, Direction side) {
            if (side == dir || side == dir.getCounterClockWise()) {
                return HalfDir.fromDirections(
                        side,
                        topHalf ? Direction.UP : Direction.DOWN
                );
            }
            return HalfDir.NULL;
        }

        private FlatInnerSlopeSlabCorner() { }
    }

    public static final class FlatElevatedSlopeSlabCorner {
        public static HalfTriangleDir getTriDir(Direction dir, boolean top, Direction side) {
            if (side == dir || side == dir.getCounterClockWise()) {
                Direction longEdge = top ? Direction.UP : Direction.DOWN;
                Direction shortEdge = side == dir ? dir.getCounterClockWise() : dir;
                return HalfTriangleDir.fromDirections(longEdge, shortEdge, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static HalfDir getHalfDir(Direction dir, boolean top, Direction side) {
            if (side == dir.getOpposite() || side == dir.getClockWise()) {
                return HalfDir.fromDirections(
                        side,
                        top ? Direction.UP : Direction.DOWN
                );
            }
            return HalfDir.NULL;
        }

        private FlatElevatedSlopeSlabCorner() { }
    }

    public static final class FlatElevatedInnerSlopeSlabCorner {
        public static HalfTriangleDir getTriDir(Direction dir, boolean top, Direction side) {
            if (side == dir.getOpposite() || side == dir.getClockWise()) {
                Direction longEdge = top ? Direction.UP : Direction.DOWN;
                Direction shortEdge = side == dir.getOpposite() ? dir.getCounterClockWise() : dir;
                return HalfTriangleDir.fromDirections(longEdge, shortEdge, false);
            }
            return HalfTriangleDir.NULL;
        }

        private FlatElevatedInnerSlopeSlabCorner() { }
    }
}
