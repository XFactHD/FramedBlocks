package io.github.xfacthd.framedblocks.common.data.skippreds.slopepanel;

import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.skippreds.HalfDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.HalfTriangleDir;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

@SuppressWarnings("JavaExistingMethodCanBeUsed")
public final class SlopePanelDirs {
    public static final class SlopePanel {
        public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, boolean front, Direction side) {
            Direction perpRotDir = rot.rotate(Rotation.CLOCKWISE_90).withFacing(dir);
            if (side.getAxis() == perpRotDir.getAxis()) {
                Direction shortEdge = rot.getOpposite().withFacing(dir);
                return HalfTriangleDir.fromDirections(dir, shortEdge, !front);
            }
            return HalfTriangleDir.NULL;
        }

        public static HalfDir getHalfDir(Direction dir, HorizontalRotation rot, boolean front, Direction side) {
            if (side == rot.withFacing(dir).getOpposite()) {
                return HalfDir.fromDirections(
                        side,
                        front ? dir.getOpposite() : dir
                );
            }
            return HalfDir.NULL;
        }

        private SlopePanel() { }
    }

    public static final class ExtendedSlopePanel {
        public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction perpRotDir = rot.rotate(Rotation.CLOCKWISE_90).withFacing(dir);
            if (side.getAxis() == perpRotDir.getAxis()) {
                Direction shortEdge = rot.getOpposite().withFacing(dir);
                return HalfTriangleDir.fromDirections(dir, shortEdge, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static HalfDir getHalfDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == rot.withFacing(dir)) {
                return HalfDir.fromDirections(side, dir);
            }
            return HalfDir.NULL;
        }

        private ExtendedSlopePanel() { }
    }

    public static final class CompoundSlopePanel {
        public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction perpRotDir = rot.rotate(Rotation.CLOCKWISE_90).withFacing(dir);
            if (side.getAxis() == perpRotDir.getAxis()) {
                Direction shortEdge = rot.getOpposite().withFacing(dir);
                return HalfTriangleDir.fromDirections(dir, shortEdge, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static HalfDir getHalfDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            if (side == rotDir) {
                return HalfDir.fromDirections(side, dir);
            }
            if (side == rotDir.getOpposite()) {
                return HalfDir.fromDirections(side, dir.getOpposite());
            }
            return HalfDir.NULL;
        }

        private CompoundSlopePanel() { }
    }

    public static final class FlatSlopePanelCorner {
        public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, boolean front, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
            if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite()) {
                Direction shortEdge = side == rotDir.getOpposite() ? perpRotDir.getOpposite() : rotDir.getOpposite();
                return HalfTriangleDir.fromDirections(dir, shortEdge, !front);
            }
            return HalfTriangleDir.NULL;
        }

        private FlatSlopePanelCorner() { }
    }

    public static final class FlatInnerSlopePanelCorner {
        public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, boolean front, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
            if (side == rotDir || side == perpRotDir) {
                Direction shortEdge = side == rotDir ? perpRotDir.getOpposite() : rotDir.getOpposite();
                return HalfTriangleDir.fromDirections(dir, shortEdge, !front);
            }
            return HalfTriangleDir.NULL;
        }

        public static HalfDir getHalfDir(Direction dir, HorizontalRotation rot, boolean front, Direction side) {
            if (side == rot.withFacing(dir).getOpposite() || side == rot.rotate(Rotation.CLOCKWISE_90).withFacing(dir)) {
                return HalfDir.fromDirections(
                        side,
                        front ? dir.getOpposite() : dir
                );
            }
            return HalfDir.NULL;
        }

        private FlatInnerSlopePanelCorner() { }
    }

    public static final class FlatExtendedSlopePanelCorner {
        public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
            if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite()) {
                Direction shortEdge = side == rotDir.getOpposite() ? perpRotDir.getOpposite() : rotDir.getOpposite();
                return HalfTriangleDir.fromDirections(dir, shortEdge, false);
            }
            return HalfTriangleDir.NULL;
        }

        public static HalfDir getHalfDir(Direction dir, HorizontalRotation rot, Direction side) {
            if (side == rot.withFacing(dir) || side == rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir)) {
                return HalfDir.fromDirections(side, dir);
            }
            return HalfDir.NULL;
        }

        private FlatExtendedSlopePanelCorner() { }
    }

    public static final class FlatExtendedInnerSlopePanelCorner {
        public static HalfTriangleDir getTriDir(Direction dir, HorizontalRotation rot, Direction side) {
            Direction rotDir = rot.withFacing(dir);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
            if (side == rotDir || side == perpRotDir) {
                Direction shortEdge = side == rotDir ? perpRotDir.getOpposite() : rotDir.getOpposite();
                return HalfTriangleDir.fromDirections(dir, shortEdge, false);
            }
            return HalfTriangleDir.NULL;
        }

        private FlatExtendedInnerSlopePanelCorner() { }
    }
}
