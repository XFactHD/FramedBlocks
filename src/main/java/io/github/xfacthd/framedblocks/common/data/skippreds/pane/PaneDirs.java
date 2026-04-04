package io.github.xfacthd.framedblocks.common.data.skippreds.pane;

import io.github.xfacthd.framedblocks.common.block.pane.FramedPartialBoardBlock;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CornerDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.HalfDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.TriangleDir;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;

public final class PaneDirs {
    public static final class Board {
        private static final int[] EDGE_MASKS = Util.make(new int[3], arr -> {
            for (Direction.Axis axis : Direction.Axis.values()) {
                int mask = (1 << axis.getPositive().ordinal()) | (1 << axis.getNegative().ordinal());
                arr[axis.ordinal()] = ~mask & 0b00111111;
            }
        });
        // Special edge mask to use when the side whose edges are queried is blocked, will never match against a valid edge mask
        private static final int INVALID_EDGE_MASK = 0b01000000;

        public static boolean testEarlyExit(int faces, Direction side) {
            return (faces & (1 << side.ordinal())) != 0;
        }

        public static int getEdgeMaskDir(int faces, Direction side) {
            if (testEarlyExit(faces, side)) {
                return INVALID_EDGE_MASK;
            }
            return faces & EDGE_MASKS[side.getAxis().ordinal()];
        }

        public static HalfDir getSingleEdgeDir(int faces, Direction side) {
            if (testEarlyExit(faces, side)) {
                return HalfDir.NULL;
            }
            int edgeMask = getEdgeMaskDir(faces, side);
            if (Integer.bitCount(edgeMask) == 1) {
                int face = Integer.numberOfTrailingZeros(edgeMask);
                return HalfDir.fromDirections(side, Direction.from3DDataValue(face));
            }
            return HalfDir.NULL;
        }

        private Board() { }
    }

    public static final class HalfBoard {
        public static HalfDir getEdgeDir(CompoundDirection cmpDir, Direction side) {
            if (side == cmpDir.orientation()) {
                return HalfDir.fromDirections(side, cmpDir.direction());
            }
            return HalfDir.NULL;
        }

        public static CornerDir getHalfEdgeDir(CompoundDirection cmpDir, Direction side) {
            if (side.getAxis() != cmpDir.direction().getAxis() && side.getAxis() != cmpDir.orientation().getAxis()) {
                return CornerDir.fromDirections(side, cmpDir.direction(), cmpDir.orientation());
            }
            return CornerDir.NULL;
        }

        public static HalfDir getHalfDir(CompoundDirection cmpDir, Direction side) {
            if (side == cmpDir.direction()) {
                return HalfDir.fromDirections(side, cmpDir.orientation());
            }
            return HalfDir.NULL;
        }

        private HalfBoard() { }
    }

    public static final class CornerBoard {
        public static CornerDir getHalfEdgeDir(CompoundDirection cmpDir, Direction side) {
            Direction dirOne = cmpDir.orientation();
            Direction dirTwo = FramedPartialBoardBlock.getCornerDirTwo(cmpDir);
            if (side == dirOne) {
                return CornerDir.fromDirections(side, cmpDir.direction(), dirTwo);
            }
            if (side == dirTwo) {
                return CornerDir.fromDirections(side, cmpDir.direction(), dirOne);
            }
            return CornerDir.NULL;
        }

        public static CornerDir getCornerDir(CompoundDirection cmpDir, Direction side) {
            if (side == cmpDir.direction()) {
                Direction dirOne = cmpDir.orientation();
                Direction dirTwo = FramedPartialBoardBlock.getCornerDirTwo(cmpDir);
                return CornerDir.fromDirections(side, dirOne, dirTwo);
            }
            return CornerDir.NULL;
        }

        private CornerBoard() { }
    }

    public static final class InnerCornerBoard {
        public static HalfDir getEdgeDir(CompoundDirection cmpDir, Direction side) {
            if (side == cmpDir.orientation() || side == FramedPartialBoardBlock.getCornerDirTwo(cmpDir)) {
                return HalfDir.fromDirections(side, cmpDir.direction());
            }
            return HalfDir.NULL;
        }

        public static CornerDir getHalfEdgeDir(CompoundDirection cmpDir, Direction side) {
            Direction dirOne = cmpDir.orientation();
            Direction dirTwo = FramedPartialBoardBlock.getCornerDirTwo(cmpDir);
            if (side == dirOne.getOpposite()) {
                return CornerDir.fromDirections(side, cmpDir.direction(), dirTwo);
            }
            if (side == dirTwo.getOpposite()) {
                return CornerDir.fromDirections(side, cmpDir.direction(), dirOne);
            }
            return CornerDir.NULL;
        }

        public static TriangleDir getStairDir(CompoundDirection cmpDir, Direction side) {
            if (side == cmpDir.direction()) {
                Direction dirOne = cmpDir.orientation();
                Direction dirTwo = FramedPartialBoardBlock.getCornerDirTwo(cmpDir);
                return TriangleDir.fromDirections(dirOne, dirTwo);
            }
            return TriangleDir.NULL;
        }

        private InnerCornerBoard() { }
    }

    public static final class CornerStrip {
        public static HalfDir getEdgeDir(Direction dir, SlopeType type, Direction side) {
            Direction dirTwo = switch (type) {
                case TOP -> Direction.UP;
                case BOTTOM -> Direction.DOWN;
                case HORIZONTAL -> dir.getCounterClockWise();
            };
            if (side == dir) {
                return HalfDir.fromDirections(side, dirTwo);
            }
            if (side == dirTwo) {
                return HalfDir.fromDirections(side, dir);
            }
            return HalfDir.NULL;
        }

        public static CornerDir getCornerDir(Direction dir, SlopeType type, Direction side) {
            Direction dirTwo = switch (type) {
                case TOP -> Direction.UP;
                case BOTTOM -> Direction.DOWN;
                case HORIZONTAL -> dir.getCounterClockWise();
            };
            if (side.getAxis() != dir.getAxis() && side.getAxis() != dirTwo.getAxis()) {
                return CornerDir.fromDirections(side, dir, dirTwo);
            }
            return CornerDir.NULL;
        }

        private CornerStrip() { }
    }

    private PaneDirs() { }
}
