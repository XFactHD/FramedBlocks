package io.github.xfacthd.framedblocks.common.data.skippreds.slab;

import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.skippreds.CornerDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.DiagCornerDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.HalfDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.TriangleDir;
import net.minecraft.core.Direction;

public final class SlabDirs {
    public static final class Slab {
        public static HalfDir getHalfDir(boolean top, Direction side) {
            if (!DirUtils.isY(side)) {
                return HalfDir.fromDirections(
                        side,
                        top ? Direction.UP : Direction.DOWN
                );
            }
            return HalfDir.NULL;
        }

        private Slab() { }
    }

    public static final class SlabEdge {
        public static HalfDir getHalfDir(Direction dir, boolean top, Direction side) {
            if (side == dir) {
                return HalfDir.fromDirections(dir, top ? Direction.UP : Direction.DOWN);
            }
            if ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
                return HalfDir.fromDirections(side, dir);
            }
            return HalfDir.NULL;
        }

        public static CornerDir getCornerDir(Direction dir, boolean top, Direction side) {
            if (side == dir.getCounterClockWise() || side == dir.getClockWise()) {
                return CornerDir.fromDirections(
                        side,
                        dir,
                        top ? Direction.UP : Direction.DOWN
                );
            }
            return CornerDir.NULL;
        }

        private SlabEdge() { }
    }

    public static final class SlabCorner {
        public static CornerDir getCornerDir(Direction dir, boolean top, Direction side) {
            if ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
                return CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
            }
            if (side == dir) {
                return CornerDir.fromDirections(
                        side,
                        dir.getCounterClockWise(),
                        top ? Direction.UP : Direction.DOWN
                );
            }
            if (side == dir.getCounterClockWise()) {
                return CornerDir.fromDirections(
                        side,
                        dir,
                        top ? Direction.UP : Direction.DOWN
                );
            }
            return CornerDir.NULL;
        }

        private SlabCorner() { }
    }

    public static final class Panel {
        public static HalfDir getHalfDir(Direction dir, Direction side) {
            if (side.getAxis() != dir.getAxis()) {
                return HalfDir.fromDirections(side, dir);
            }
            return HalfDir.NULL;
        }

        private Panel() { }
    }

    public static final class MasonryCornerSegment {
        public static HalfDir getHalfDir(Direction dir, boolean top, Direction side) {
            return switch (side) {
                case DOWN -> HalfDir.fromDirections(side, top ? dir.getClockWise() : dir.getOpposite());
                case UP -> HalfDir.fromDirections(side, top ? dir.getOpposite() : dir.getClockWise());
                default -> HalfDir.NULL;
            };
        }

        public static CornerDir getCornerDir(Direction dir, boolean top, Direction side) {
            if (side == dir) {
                return CornerDir.fromDirections(side, top ? Direction.DOWN : Direction.UP, dir.getClockWise());
            }
            if (side == dir.getCounterClockWise()) {
                return CornerDir.fromDirections(side, top ? Direction.UP : Direction.DOWN, dir.getOpposite());
            }
            return CornerDir.NULL;
        }

        public static TriangleDir getStairDir(Direction dir, boolean top, Direction side) {
            if (side == dir.getClockWise()) {
                return TriangleDir.fromDirections(top ? Direction.DOWN : Direction.UP, dir.getOpposite());
            }
            if (side == dir.getOpposite()) {
                return TriangleDir.fromDirections(top ? Direction.UP : Direction.DOWN, dir.getClockWise());
            }
            return TriangleDir.NULL;
        }

        private MasonryCornerSegment() { }
    }

    public static final class CheckeredCubeSegment {
        public static DiagCornerDir getDiagCornerDir(boolean second, Direction side) {
            return switch (side) {
                case DOWN -> second ? DiagCornerDir.DOWN_NE_SW : DiagCornerDir.DOWN_NW_SE;
                case UP -> second ? DiagCornerDir.UP_NW_SE : DiagCornerDir.UP_NE_SW;
                case NORTH -> second ? DiagCornerDir.NORTH_UW_DE : DiagCornerDir.NORTH_UE_DW;
                case SOUTH -> second ? DiagCornerDir.SOUTH_UE_DW : DiagCornerDir.SOUTH_UW_DE;
                case WEST -> second ? DiagCornerDir.WEST_UN_DS : DiagCornerDir.WEST_US_DN;
                case EAST -> second ? DiagCornerDir.EAST_US_DN : DiagCornerDir.EAST_UN_DS;
            };
        }

        private CheckeredCubeSegment() { }
    }

    public static final class CheckeredSlabSegment {
        public static DiagCornerDir getDiagCornerDir(boolean top, boolean second, Direction side) {
            if (top && side == Direction.UP) {
                return second ? DiagCornerDir.UP_NW_SE : DiagCornerDir.UP_NE_SW;
            }
            if (!top && side == Direction.DOWN) {
                return second ? DiagCornerDir.DOWN_NE_SW : DiagCornerDir.DOWN_NW_SE;
            }
            return DiagCornerDir.NULL;
        }

        public static CornerDir getCornerDir(boolean top, boolean second, Direction side) {
            if (!DirUtils.isY(side)) {
                boolean x = DirUtils.isX(side);
                return CornerDir.fromDirections(
                        side,
                        top ? Direction.UP : Direction.DOWN,
                        second == x ? side.getCounterClockWise() : side.getClockWise()
                );
            }
            return CornerDir.NULL;
        }

        private CheckeredSlabSegment() { }
    }

    public static final class CheckeredPanelSegment {
        public static DiagCornerDir getDiagCornerDir(Direction dir, boolean second, Direction side) {
            if (side == dir) {
                return switch (side) {
                    case NORTH -> second ? DiagCornerDir.NORTH_UW_DE : DiagCornerDir.NORTH_UE_DW;
                    case SOUTH -> second ? DiagCornerDir.SOUTH_UE_DW : DiagCornerDir.SOUTH_UW_DE;
                    case WEST -> second ? DiagCornerDir.WEST_UN_DS : DiagCornerDir.WEST_US_DN;
                    case EAST -> second ? DiagCornerDir.EAST_US_DN : DiagCornerDir.EAST_UN_DS;
                    default -> DiagCornerDir.NULL;
                };
            }
            return DiagCornerDir.NULL;
        }

        public static CornerDir getCornerDir(Direction dir, boolean second, Direction side) {
            if (DirUtils.isY(side)) {
                boolean x = DirUtils.isX(dir);
                boolean up = side == Direction.UP;
                return CornerDir.fromDirections(
                        side,
                        dir,
                        (second == up) ^ x ? dir.getCounterClockWise() : dir.getClockWise()
                );
            }
            if (side.getAxis() != dir.getAxis()) {
                boolean x = DirUtils.isX(dir);
                boolean cw = side == dir.getClockWise();
                return CornerDir.fromDirections(
                        side,
                        dir,
                        (second == cw) ^ x ? Direction.DOWN : Direction.UP
                );
            }
            return CornerDir.NULL;
        }

        private CheckeredPanelSegment() { }
    }
}
