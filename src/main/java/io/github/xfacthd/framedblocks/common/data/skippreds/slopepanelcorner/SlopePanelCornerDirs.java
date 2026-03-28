package io.github.xfacthd.framedblocks.common.data.skippreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.skippreds.CornerDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.HalfTriangleDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.QuarterTriangleDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.TriangleDir;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

@SuppressWarnings("JavaExistingMethodCanBeUsed")
public final class SlopePanelCornerDirs {
    public static final class SmallCornerSlopePanel {
        public static HalfTriangleDir getTriDir(Direction dir, boolean top, Direction side) {
            if (side == dir) {
                return HalfTriangleDir.fromDirections(
                        dir.getCounterClockWise(),
                        top ? Direction.UP : Direction.DOWN,
                        true
                );
            } else if (side == dir.getCounterClockWise()) {
                return HalfTriangleDir.fromDirections(
                        dir,
                        top ? Direction.UP : Direction.DOWN,
                        true
                );
            }
            return HalfTriangleDir.NULL;
        }

        public static CornerDir getCornerDir(Direction dir, boolean top, Direction side) {
            if ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
                return CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
            }
            return CornerDir.NULL;
        }

        private SmallCornerSlopePanel() { }
    }

    public static final class SmallCornerSlopePanelWall {
        public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
            if (side == rotDir) {
                return HalfTriangleDir.fromDirections(perpRotDir, dir, true);
            } else if (side == perpRotDir) {
                return HalfTriangleDir.fromDirections(rotDir, dir, true);
            }
            return HalfTriangleDir.NULL;
        }

        public static CornerDir getCornerDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == dir) {
                Direction rotDir = rot.withFacing(dir);
                Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
                return CornerDir.fromDirections(dir, rotDir, perpRotDir);
            }
            return CornerDir.NULL;
        }

        private SmallCornerSlopePanelWall() { }
    }

    public static final class LargeCornerSlopePanel {
        public static HalfTriangleDir getTriDir(Direction dir, boolean top, Direction side) {
            if (side == dir) {
                return HalfTriangleDir.fromDirections(
                        dir.getCounterClockWise(),
                        top ? Direction.UP : Direction.DOWN,
                        false
                );
            } else if (side == dir.getCounterClockWise()) {
                return HalfTriangleDir.fromDirections(
                        dir,
                        top ? Direction.UP : Direction.DOWN,
                        false
                );
            }
            return HalfTriangleDir.NULL;
        }

        public static TriangleDir getStairDir(Direction dir, boolean top, Direction side) {
            if ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
                return TriangleDir.fromDirections(
                        dir.getClockWise(),
                        dir.getOpposite()
                );
            }
            return TriangleDir.NULL;
        }

        private LargeCornerSlopePanel() { }
    }

    public static final class LargeCornerSlopePanelWall {
        public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
            if (side == rotDir) {
                return HalfTriangleDir.fromDirections(perpRotDir, dir, false);
            } else if (side == perpRotDir) {
                return HalfTriangleDir.fromDirections(rotDir, dir, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static TriangleDir getStairDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == dir) {
                Direction rotDir = rot.withFacing(dir);
                Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
                return TriangleDir.fromDirections(rotDir.getOpposite(), perpRotDir.getOpposite());
            }
            return TriangleDir.NULL;
        }

        private LargeCornerSlopePanelWall() { }
    }

    public static final class SmallInnerCornerSlopePanel {
        public static HalfTriangleDir getTriDir(Direction dir, boolean top, Direction side) {
            if (side == dir) {
                return HalfTriangleDir.fromDirections(
                        dir.getClockWise(),
                        top ? Direction.UP : Direction.DOWN,
                        false
                );
            } else if (side == dir.getCounterClockWise()) {
                return HalfTriangleDir.fromDirections(
                        dir.getOpposite(),
                        top ? Direction.UP : Direction.DOWN,
                        false
                );
            }
            return HalfTriangleDir.NULL;
        }

        public static CornerDir getCornerDir(Direction dir, boolean top, Direction side) {
            if ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
                return CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
            }
            return CornerDir.NULL;
        }

        private SmallInnerCornerSlopePanel() { }
    }

    public static final class SmallInnerCornerSlopePanelWall {
        public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
            if (side == rotDir) {
                return HalfTriangleDir.fromDirections(perpRotDir.getOpposite(), dir, false);
            } else if (side == perpRotDir) {
                return HalfTriangleDir.fromDirections(rotDir.getOpposite(), dir, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static CornerDir getCornerDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == dir) {
                Direction rotDir = rot.withFacing(dir);
                Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
                return CornerDir.fromDirections(dir, rotDir, perpRotDir);
            }
            return CornerDir.NULL;
        }

        private SmallInnerCornerSlopePanelWall() { }
    }

    public static final class LargeInnerCornerSlopePanel {
        public static HalfTriangleDir getTriDir(Direction dir, boolean top, Direction side) {
            if (side == dir) {
                return HalfTriangleDir.fromDirections(
                        dir.getClockWise(),
                        top ? Direction.UP : Direction.DOWN,
                        true
                );
            } else if (side == dir.getCounterClockWise()) {
                return HalfTriangleDir.fromDirections(
                        dir.getOpposite(),
                        top ? Direction.UP : Direction.DOWN,
                        true
                );
            }
            return HalfTriangleDir.NULL;
        }

        public static TriangleDir getStairDir(Direction dir, boolean top, Direction side) {
            if ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
                return TriangleDir.fromDirections(
                        dir.getClockWise(),
                        dir.getOpposite()
                );
            }
            return TriangleDir.NULL;
        }

        private LargeInnerCornerSlopePanel() { }
    }

    public static final class LargeInnerCornerSlopePanelWall {
        public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
            if (side == rotDir) {
                return HalfTriangleDir.fromDirections(perpRotDir.getOpposite(), dir, true);
            } else if (side == perpRotDir) {
                return HalfTriangleDir.fromDirections(rotDir.getOpposite(), dir, true);
            }
            return HalfTriangleDir.NULL;
        }

        public static TriangleDir getStairDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == dir) {
                Direction rotDir = rot.withFacing(dir);
                Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
                return TriangleDir.fromDirections(rotDir.getOpposite(), perpRotDir.getOpposite());
            }
            return TriangleDir.NULL;
        }

        private LargeInnerCornerSlopePanelWall() { }
    }

    public static final class ExtendedCornerSlopePanel {
        public static HalfTriangleDir getTriDir(Direction dir, boolean top, Direction side) {
            Direction dirTwo = top ? Direction.UP : Direction.DOWN;
            if (side == dir) {
                return HalfTriangleDir.fromDirections(dir.getCounterClockWise(), dirTwo, false);
            } else if (side == dir.getCounterClockWise()) {
                return HalfTriangleDir.fromDirections(dir, dirTwo, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static CornerDir getCornerDir(Direction dir, boolean top, Direction side) {
            if ((!top && side == Direction.UP) || (top && side == Direction.DOWN)) {
                return CornerDir.fromDirections(side, dir, dir.getCounterClockWise());
            }
            return CornerDir.NULL;
        }

        private ExtendedCornerSlopePanel() { }
    }

    public static final class ExtendedCornerSlopePanelWall {
        public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
            if (side == rotDir) {
                return HalfTriangleDir.fromDirections(perpRotDir, dir, false);
            } else if (side == perpRotDir) {
                return HalfTriangleDir.fromDirections(rotDir, dir, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static CornerDir getCornerDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == dir.getOpposite()) {
                Direction rotDir = rot.withFacing(dir);
                Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
                return CornerDir.fromDirections(dir.getOpposite(), rotDir, perpRotDir);
            }
            return CornerDir.NULL;
        }

        private ExtendedCornerSlopePanelWall() { }
    }

    public static final class ExtendedInnerCornerSlopePanel {
        public static HalfTriangleDir getTriDir(Direction dir, boolean top, Direction side) {
            Direction dirTwo = top ? Direction.UP : Direction.DOWN;
            if (side == dir) {
                return HalfTriangleDir.fromDirections(dir.getClockWise(), dirTwo, false);
            } else if (side == dir.getCounterClockWise()) {
                return HalfTriangleDir.fromDirections(dir.getOpposite(), dirTwo, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static TriangleDir getStairDir(Direction dir, boolean top, Direction side) {
            if ((!top && side == Direction.UP) || (top && side == Direction.DOWN)) {
                return TriangleDir.fromDirections(dir.getOpposite(), dir.getClockWise());
            }
            return TriangleDir.NULL;
        }

        private ExtendedInnerCornerSlopePanel() { }
    }

    public static final class ExtendedInnerCornerSlopePanelWall {
        public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
            if (side == rotDir) {
                return HalfTriangleDir.fromDirections(perpRotDir.getOpposite(), dir, false);
            } else if (side == perpRotDir) {
                return HalfTriangleDir.fromDirections(rotDir.getOpposite(), dir, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static TriangleDir getStairDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == dir.getOpposite()) {
                Direction rotDir = rot.withFacing(dir);
                Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
                return TriangleDir.fromDirections(rotDir.getOpposite(), perpRotDir.getOpposite());
            }
            return TriangleDir.NULL;
        }

        private ExtendedInnerCornerSlopePanelWall() { }
    }

    public static final class SmallPrismCornerSlopePanel {
        public static HalfTriangleDir getSideTriDir(Direction dir, boolean top, Direction side) {
            Direction down = top ? Direction.UP : Direction.DOWN;
            if (side == dir) {
                return HalfTriangleDir.fromDirections(dir.getCounterClockWise(), down, true);
            }
            if (side == dir.getCounterClockWise()) {
                return HalfTriangleDir.fromDirections(dir, down, true);
            }
            return HalfTriangleDir.NULL;
        }

        public static QuarterTriangleDir getBottomTriDir(Direction dir, boolean top, Direction side) {
            Direction down = top ? Direction.UP : Direction.DOWN;
            if (side == down) {
                return QuarterTriangleDir.fromDirections(dir, dir.getCounterClockWise(), false);
            }
            return QuarterTriangleDir.NULL;
        }

        private SmallPrismCornerSlopePanel() { }
    }

    public static final class SmallPrismCornerSlopePanelWall {
        public static HalfTriangleDir getSideTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
            if (side == rotDir) {
                return HalfTriangleDir.fromDirections(perpRotDir, dir, true);
            }
            if (side == perpRotDir) {
                return HalfTriangleDir.fromDirections(rotDir, dir, true);
            }
            return HalfTriangleDir.NULL;
        }

        public static QuarterTriangleDir getBackTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == dir) {
                return QuarterTriangleDir.fromDirections(
                        rot.withFacing(dir),
                        rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir),
                        false
                );
            }
            return QuarterTriangleDir.NULL;
        }

        private SmallPrismCornerSlopePanelWall() { }
    }

    public static final class LargePrismCornerSlopePanel {
        public static HalfTriangleDir getSideTriDir(Direction dir, boolean top, Direction side) {
            Direction down = top ? Direction.UP : Direction.DOWN;
            if (side == dir) {
                return HalfTriangleDir.fromDirections(dir.getCounterClockWise(), down, false);
            }
            if (side == dir.getCounterClockWise()) {
                return HalfTriangleDir.fromDirections(dir, down, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static TriangleDir getBottomTriDir(Direction dir, boolean top, Direction side) {
            Direction down = top ? Direction.UP : Direction.DOWN;
            if (side == down) {
                return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
            }
            return TriangleDir.NULL;
        }

        public static QuarterTriangleDir getTopTriDir(Direction dir, boolean top, Direction side) {
            Direction up = top ? Direction.DOWN : Direction.UP;
            if (side == up) {
                return QuarterTriangleDir.fromDirections(dir, dir.getCounterClockWise(), false);
            }
            return QuarterTriangleDir.NULL;
        }

        private LargePrismCornerSlopePanel() { }
    }

    public static final class LargePrismCornerSlopePanelWall {
        public static HalfTriangleDir getSideTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
            if (side == rotDir) {
                return HalfTriangleDir.fromDirections(perpRotDir, dir, false);
            }
            if (side == perpRotDir) {
                return HalfTriangleDir.fromDirections(rotDir, dir, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static TriangleDir getBackTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == dir) {
                return TriangleDir.fromDirections(
                        rot.withFacing(dir),
                        rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir)
                );
            }
            return TriangleDir.NULL;
        }

        public static QuarterTriangleDir getFrontTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == dir.getOpposite()) {
                return QuarterTriangleDir.fromDirections(
                        rot.withFacing(dir),
                        rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir),
                        false
                );
            }
            return QuarterTriangleDir.NULL;
        }

        private LargePrismCornerSlopePanelWall() { }
    }

    public static final class SmallInnerPrismCornerSlopePanel {
        public static HalfTriangleDir getSideTriDir(Direction dir, boolean top, Direction side) {
            Direction down = top ? Direction.UP : Direction.DOWN;
            if (side == dir.getOpposite()) {
                return HalfTriangleDir.fromDirections(dir.getCounterClockWise(), down, false);
            }
            if (side == dir.getClockWise()) {
                return HalfTriangleDir.fromDirections(dir, down, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static TriangleDir getTopTriDir(Direction dir, boolean top, Direction side) {
            Direction up = top ? Direction.DOWN : Direction.UP;
            if (side == up) {
                return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
            }
            return TriangleDir.NULL;
        }

        private SmallInnerPrismCornerSlopePanel() { }
    }

    public static final class SmallInnerPrismCornerSlopePanelWall {
        public static HalfTriangleDir getSideTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
            if (side == rotDir.getOpposite()) {
                return HalfTriangleDir.fromDirections(perpRotDir, dir, false);
            }
            if (side == perpRotDir.getOpposite()) {
                return HalfTriangleDir.fromDirections(rotDir, dir, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static TriangleDir getFrontTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == dir.getOpposite()) {
                return TriangleDir.fromDirections(
                        rot.withFacing(dir),
                        rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir)
                );
            }
            return TriangleDir.NULL;
        }

        private SmallInnerPrismCornerSlopePanelWall() { }
    }

    public static final class LargeInnerPrismCornerSlopePanel {
        public static HalfTriangleDir getSideTriDir(Direction dir, boolean top, Direction side) {
            Direction down = top ? Direction.UP : Direction.DOWN;
            if (side == dir.getOpposite()) {
                return HalfTriangleDir.fromDirections(dir.getCounterClockWise(), down, true);
            }
            if (side == dir.getClockWise()) {
                return HalfTriangleDir.fromDirections(dir, down, true);
            }
            return HalfTriangleDir.NULL;
        }

        public static TriangleDir getBottomTriDir(Direction dir, boolean top, Direction side) {
            Direction down = top ? Direction.UP : Direction.DOWN;
            if (side == down) {
                return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
            }
            return TriangleDir.NULL;
        }

        public static TriangleDir getTopTriDir(Direction dir, boolean top, Direction side) {
            Direction up = top ? Direction.DOWN : Direction.UP;
            if (side == up) {
                return TriangleDir.fromDirections(dir, dir.getCounterClockWise());
            }
            return TriangleDir.NULL;
        }

        private LargeInnerPrismCornerSlopePanel() { }
    }

    public static final class LargeInnerPrismCornerSlopePanelWall {
        public static HalfTriangleDir getSideTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
            if (side == rotDir.getOpposite()) {
                return HalfTriangleDir.fromDirections(perpRotDir, dir, true);
            }
            if (side == perpRotDir.getOpposite()) {
                return HalfTriangleDir.fromDirections(rotDir, dir, true);
            }
            return HalfTriangleDir.NULL;
        }

        public static TriangleDir getBackTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == dir) {
                return TriangleDir.fromDirections(
                        rot.withFacing(dir),
                        rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir)
                );
            }
            return TriangleDir.NULL;
        }

        public static TriangleDir getFrontTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == dir.getOpposite()) {
                return TriangleDir.fromDirections(
                        rot.withFacing(dir),
                        rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir)
                );
            }
            return TriangleDir.NULL;
        }

        private LargeInnerPrismCornerSlopePanelWall() { }
    }
}
