package io.github.xfacthd.framedblocks.common.data.skippreds.stairs;

import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CornerDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.HalfDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.TriangleDir;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;

@SuppressWarnings("JavaExistingMethodCanBeUsed")
public final class StairsDirs {
    public static final class Stairs {
        public static TriangleDir getStairDir(Direction dir, StairsShape shape, Half half, Direction side) {
            Direction dirTwo = half == Half.TOP ? Direction.UP : Direction.DOWN;
            return switch (shape) {
                case STRAIGHT -> {
                    if (side == dir.getClockWise() || side == dir.getCounterClockWise()) {
                        yield TriangleDir.fromDirections(dir, dirTwo);
                    }
                    yield TriangleDir.NULL;
                }
                case INNER_LEFT -> {
                    if (side == dir.getOpposite()) {
                        yield TriangleDir.fromDirections(dir.getCounterClockWise(), dirTwo);
                    }
                    if (side == dir.getClockWise()) {
                        yield TriangleDir.fromDirections(dir, dirTwo);
                    }
                    if (side == dirTwo) {
                        yield TriangleDir.fromDirections(dir, dir.getCounterClockWise());
                    }
                    yield TriangleDir.NULL;
                }
                case INNER_RIGHT -> {
                    if (side == dir.getOpposite()) {
                        yield TriangleDir.fromDirections(dir.getClockWise(), dirTwo);
                    }
                    if (side == dir.getCounterClockWise()) {
                        yield TriangleDir.fromDirections(dir, dirTwo);
                    }
                    if (side == dirTwo) {
                        yield TriangleDir.fromDirections(dir, dir.getClockWise());
                    }
                    yield TriangleDir.NULL;
                }
                case OUTER_LEFT -> {
                    if (side == dir) {
                        yield TriangleDir.fromDirections(dir.getCounterClockWise(), dirTwo);
                    }
                    if (side == dir.getCounterClockWise()) {
                        yield TriangleDir.fromDirections(dir, dirTwo);
                    }
                    yield TriangleDir.NULL;
                }
                case OUTER_RIGHT -> {
                    if (side == dir) {
                        yield TriangleDir.fromDirections(dir.getClockWise(), dirTwo);
                    }
                    if (side == dir.getClockWise()) {
                        yield TriangleDir.fromDirections(dir, dirTwo);
                    }
                    yield TriangleDir.NULL;
                }
            };
        }

        public static HalfDir getHalfDir(Direction dir, StairsShape shape, Half half, Direction side) {
            Direction edge = half == Half.TOP ? Direction.UP : Direction.DOWN;
            return switch (shape) {
                case INNER_LEFT, INNER_RIGHT -> HalfDir.NULL;
                case STRAIGHT -> {
                    if (side == dir.getOpposite()) {
                        yield HalfDir.fromDirections(side, edge);
                    } else if (side == edge.getOpposite()) {
                        yield HalfDir.fromDirections(side, dir);
                    }
                    yield HalfDir.NULL;
                }
                case OUTER_LEFT -> {
                    if (side == dir.getOpposite() || side == dir.getClockWise()) {
                        yield HalfDir.fromDirections(side, edge);
                    }
                    yield HalfDir.NULL;
                }
                case OUTER_RIGHT -> {
                    if (side == dir.getOpposite() || side == dir.getCounterClockWise()) {
                        yield HalfDir.fromDirections(side, edge);
                    }
                    yield HalfDir.NULL;
                }
            };
        }

        public static CornerDir getCornerDir(Direction dir, StairsShape shape, Half half, Direction side) {
            Direction normal = half == Half.TOP ? Direction.DOWN : Direction.UP;
            if (side != normal) {
                return CornerDir.NULL;
            }

            return switch (shape) {
                case STRAIGHT, INNER_LEFT, INNER_RIGHT -> CornerDir.NULL;
                case OUTER_LEFT -> CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
                case OUTER_RIGHT -> CornerDir.fromDirections(side, dir, dir.getClockWise());
            };
        }

        private Stairs() { }
    }

    public static final class HalfStairs {
        public static TriangleDir getStairDir(Direction dir, boolean top, boolean right, Direction side) {
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

        public static CornerDir getCornerDir(Direction dir, boolean top, boolean right, Direction side) {
            if (side == dir.getOpposite()) {
                return CornerDir.fromDirections(
                        side,
                        top ? Direction.UP : Direction.DOWN,
                        right ? dir.getClockWise() : dir.getCounterClockWise()
                );
            } else if ((!top && side == Direction.UP) || (top && side == Direction.DOWN)) {
                return CornerDir.fromDirections(
                        side,
                        dir,
                        right ? dir.getClockWise() : dir.getCounterClockWise()
                );
            }
            return CornerDir.NULL;
        }

        private HalfStairs() { }
    }

    public static final class SlopedStairs {
        public static TriangleDir getTriDir(Direction dir, boolean top, Direction side) {
            if ((!top && side == Direction.UP) || (top && side == Direction.DOWN)) {
                return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
            }
            return TriangleDir.NULL;
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

        private SlopedStairs() { }
    }

    public static final class VerticalStairs {
        public static TriangleDir getStairDir(Direction dir, StairsType type, Direction side) {
            if (side == Direction.DOWN) {
                if (!type.isBottom()) {
                    return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
                }
            } else if (side == Direction.UP) {
                if (!type.isTop()) {
                    return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
                }
            } else if (side == dir && type != StairsType.VERTICAL) {
                if (type.isForward()) {
                    Direction dirTwo = type.isTop() ? Direction.DOWN : Direction.UP;
                    return TriangleDir.fromDirections(dir.getCounterClockWise(), dirTwo);
                }
            } else if (side == dir.getCounterClockWise()) {
                if (type.isCounterClockwise()) {
                    Direction dirTwo = type.isTop() ? Direction.DOWN : Direction.UP;
                    return TriangleDir.fromDirections(dir, dirTwo);
                }
            }
            return TriangleDir.NULL;
        }

        public static HalfDir getHalfDir(Direction dir, StairsType type, Direction side) {
            if (side == dir.getClockWise()) {
                if (!type.isForward()) {
                    return HalfDir.fromDirections(side, dir);
                }
            } else if (side == dir.getOpposite()) {
                if (!type.isCounterClockwise()) {
                    return HalfDir.fromDirections(side, dir.getCounterClockWise());
                }
            } else if (side == Direction.UP) {
                if (type.isTop()) {
                    return HalfDir.fromDirections(side, type.isForward() ? dir.getCounterClockWise() : dir);
                }
            } else if (side == Direction.DOWN) {
                if (type.isBottom()) {
                    return HalfDir.fromDirections(side, type.isForward() ? dir.getCounterClockWise() : dir);
                }
            }
            return HalfDir.NULL;
        }

        public static CornerDir getCornerDir(Direction dir, StairsType type, Direction side) {
            if (type == StairsType.VERTICAL) {
                return CornerDir.NULL;
            }

            Direction dirTwo = type.isTop() ? Direction.DOWN : Direction.UP;
            if (side == dirTwo.getOpposite()) {
                if (type.isForward() && type.isCounterClockwise()) {
                    return CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
                }
            } else if (side == dir.getOpposite()) {
                if (type.isCounterClockwise()) {
                    return CornerDir.fromDirections(side, dir.getCounterClockWise(), dirTwo);
                }
            } else if (side == dir.getClockWise()) {
                if (type.isForward()) {
                    return CornerDir.fromDirections(side, dir, dirTwo);
                }
            }
            return CornerDir.NULL;
        }

        private VerticalStairs() { }
    }

    public static final class VerticalHalfStairs {
        public static TriangleDir getStairDir(Direction dir, boolean top, Direction side) {
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

        public static CornerDir getCornerDir(Direction dir, boolean top, Direction side) {
            if (side == dir.getClockWise()) {
                return CornerDir.fromDirections(
                        side,
                        dir,
                        top ? Direction.UP : Direction.DOWN
                );
            }
            if (side == dir.getOpposite()) {
                return CornerDir.fromDirections(
                        side,
                        dir.getCounterClockWise(),
                        top ? Direction.UP : Direction.DOWN
                );
            }
            return CornerDir.NULL;
        }

        private VerticalHalfStairs() { }
    }

    public static final class VerticalSlopedStairs {
        public static TriangleDir getTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == dir.getOpposite()) {
                return TriangleDir.fromDirections(
                        rot.getOpposite().withFacing(dir),
                        rot.rotate(Rotation.CLOCKWISE_90).withFacing(dir)
                );
            }
            return TriangleDir.NULL;
        }

        public static HalfDir getHalfDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == rot.withFacing(dir) || side == rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir)) {
                return HalfDir.fromDirections(side, dir);
            }
            return HalfDir.NULL;
        }

        private VerticalSlopedStairs() { }
    }
}
