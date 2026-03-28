package io.github.xfacthd.framedblocks.common.data.skippreds.slope;

import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import io.github.xfacthd.framedblocks.common.data.property.PillarConnection;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.data.skippreds.HalfDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.TriangleDir;
import net.minecraft.core.Direction;

public final class SlopeDirs {
    public static final class Slope {
        public static TriangleDir getTriDir(Direction dir, SlopeType type, Direction side) {
            if (type == SlopeType.HORIZONTAL) {
                if (DirUtils.isY(side)) {
                    return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
                }
            } else {
                if (side.getAxis() == dir.getClockWise().getAxis()) {
                    return TriangleDir.fromDirections(
                            dir,
                            type == SlopeType.TOP ? Direction.UP : Direction.DOWN
                    );
                }
            }
            return TriangleDir.NULL;
        }

        private Slope() { }
    }

    public static final class HalfSlope {
        public static TriangleDir getTriDir(Direction dir, boolean top, boolean right, Direction side) {
            if ((!right && side == dir.getCounterClockWise()) || (right && side == dir.getClockWise())) {
                return TriangleDir.fromDirections(
                        dir,
                        top ? Direction.UP : Direction.DOWN
                );
            }
            return TriangleDir.NULL;
        }

        public static HalfDir getHalfDir(Direction dir, boolean top, boolean right, Direction side) {
            if (side == dir || (!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
                return HalfDir.fromDirections(
                        side,
                        right ? dir.getClockWise() : dir.getCounterClockWise()
                );
            }
            return HalfDir.NULL;
        }

        private HalfSlope() { }
    }

    public static final class VerticalHalfSlope {
        public static TriangleDir getTriDir(Direction dir, boolean top, Direction side) {
            if ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
                return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
            }
            return TriangleDir.NULL;
        }

        public static HalfDir getHalfDir(Direction dir, boolean top, Direction side) {
            if (side == dir || side == dir.getCounterClockWise()) {
                return HalfDir.fromDirections(
                        side,
                        top ? Direction.UP : Direction.DOWN
                );
            }
            return HalfDir.NULL;
        }

        private VerticalHalfSlope() { }
    }

    public static final class Corner {
        public static TriangleDir getTriDir(Direction dir, CornerType type, Direction side) {
            if (type.isHorizontal()) {
                boolean top = type.isTop();
                boolean right = type.isRight();

                if ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
                    return TriangleDir.fromDirections(
                            dir,
                            right ? dir.getClockWise() : dir.getCounterClockWise()
                    );
                } else if ((!right && side == dir.getCounterClockWise()) || (right && side == dir.getClockWise())) {
                    return TriangleDir.fromDirections(
                            dir,
                            top ? Direction.UP : Direction.DOWN
                    );
                }
            } else if (side == dir) {
                return TriangleDir.fromDirections(
                        dir.getCounterClockWise(),
                        type == CornerType.TOP ? Direction.UP : Direction.DOWN
                );
            } else if (side == dir.getCounterClockWise()) {
                return TriangleDir.fromDirections(
                        dir,
                        type == CornerType.TOP ? Direction.UP : Direction.DOWN
                );
            }
            return TriangleDir.NULL;
        }

        private Corner() { }
    }

    public static final class InnerCorner {
        public static TriangleDir getTriDir(Direction dir, CornerType type, Direction side) {
            if (type.isHorizontal()) {
                boolean top = type.isTop();
                boolean right = type.isRight();

                if ((!top && side == Direction.UP) || (top && side == Direction.DOWN)) {
                    return TriangleDir.fromDirections(
                            dir,
                            right ? dir.getClockWise() : dir.getCounterClockWise()
                    );
                } else if ((!right && side == dir.getClockWise()) || (right && side == dir.getCounterClockWise())) {
                    return TriangleDir.fromDirections(
                            dir,
                            top ? Direction.UP : Direction.DOWN
                    );
                }
            } else if (side == dir.getOpposite()) {
                return TriangleDir.fromDirections(
                        dir.getCounterClockWise(),
                        type == CornerType.TOP ? Direction.UP : Direction.DOWN
                );
            } else if (side == dir.getClockWise()) {
                return TriangleDir.fromDirections(
                        dir,
                        type == CornerType.TOP ? Direction.UP : Direction.DOWN
                );
            }
            return TriangleDir.NULL;
        }

        private InnerCorner() { }
    }

    public static final class ThreewayCorner {
        public static TriangleDir getTriDir(Direction dir, boolean top, Direction side) {
            if ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
                return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
            } else if (side == dir) {
                return TriangleDir.fromDirections(
                        dir.getCounterClockWise(),
                        top ? Direction.UP : Direction.DOWN
                );
            } else if (side == dir.getCounterClockWise()) {
                return TriangleDir.fromDirections(
                        dir,
                        top ? Direction.UP : Direction.DOWN
                );
            }
            return TriangleDir.NULL;
        }

        private ThreewayCorner() { }
    }

    public static final class InnerThreewayCorner {
        public static TriangleDir getTriDir(Direction dir, boolean top, Direction side) {
            if ((!top && side == Direction.UP) || (top && side == Direction.DOWN)) {
                return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
            } else if (side == dir.getOpposite()) {
                return TriangleDir.fromDirections(
                        dir.getCounterClockWise(),
                        top ? Direction.UP : Direction.DOWN
                );
            } else if (side == dir.getClockWise()) {
                return TriangleDir.fromDirections(
                        dir,
                        top ? Direction.UP : Direction.DOWN
                );
            }
            return TriangleDir.NULL;
        }

        private InnerThreewayCorner() { }
    }

    public static final class Pyramid {
        public static boolean testEarlyExit(Direction dir, PillarConnection connection, Direction side) {
            return side != dir || connection == PillarConnection.NONE;
        }

        public static boolean isPostDir(Direction dir, PillarConnection connection, Direction side) {
            return side == dir && connection == PillarConnection.POST;
        }

        public static boolean isPillarDir(Direction dir, PillarConnection connection, Direction side) {
            return side == dir && connection == PillarConnection.PILLAR;
        }

        private Pyramid() { }
    }

    public static final class ElevatedPyramidSlab {
        public static boolean isPostDir(Direction dir, PillarConnection connection, Direction side) {
            return side == dir && connection == PillarConnection.POST;
        }

        public static boolean isPillarDir(Direction dir, PillarConnection connection, Direction side) {
            return side == dir && connection == PillarConnection.PILLAR;
        }

        public static HalfDir getHalfDir(Direction dir, Direction side) {
            if (side.getAxis() != dir.getAxis()) {
                return HalfDir.fromDirections(side, dir.getOpposite());
            }
            return HalfDir.NULL;
        }

        private ElevatedPyramidSlab() { }
    }

    public static final class UpperPyramidSlab {
        public static boolean testEarlyExit(Direction dir, PillarConnection connection, Direction side) {
            return side != dir || connection == PillarConnection.NONE;
        }

        public static boolean isPostDir(Direction dir, PillarConnection connection, Direction side) {
            return side == dir && connection == PillarConnection.POST;
        }

        public static boolean isPillarDir(Direction dir, PillarConnection connection, Direction side) {
            return side == dir && connection == PillarConnection.PILLAR;
        }

        private UpperPyramidSlab() { }
    }

    public static final class RailSlope {
        public static TriangleDir getTriDir(Direction dir, Direction side) {
            return Slope.getTriDir(dir, SlopeType.BOTTOM, side);
        }

        private RailSlope() { }
    }
}
