package io.github.xfacthd.framedblocks.common.data.skippreds.prism;

import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import io.github.xfacthd.framedblocks.common.data.property.DirectionAxis;
import io.github.xfacthd.framedblocks.common.data.skippreds.HalfDir;
import net.minecraft.core.Direction;

@SuppressWarnings("JavaExistingMethodCanBeUsed")
public final class PrismDirs {
    public static final class Prism {
        public static boolean testEarlyExit(DirectionAxis dirAxis, Direction side) {
            return side.getAxis() != dirAxis.axis();
        }

        public static HalfDir getTriDir(DirectionAxis dirAxis, Direction side) {
            Direction dir = dirAxis.direction();
            Direction.Axis axis = dirAxis.axis();
            if (dir.getAxis() != axis && side.getAxis() == axis) {
                return HalfDir.fromDirections(side, dir);
            }
            return HalfDir.NULL;
        }

        private Prism() { }
    }

    public static final class ElevatedInnerPrism {
        public static boolean testEarlyExit(DirectionAxis dirAxis, Direction side) {
            return side.getAxis() != dirAxis.axis();
        }

        public static HalfDir getTriDir(DirectionAxis dirAxis, Direction side) {
            Direction dir = dirAxis.direction();
            Direction.Axis axis = dirAxis.axis();
            if (dir.getAxis() != axis && side.getAxis() == axis) {
                return HalfDir.fromDirections(side, dir);
            }
            return HalfDir.NULL;
        }

        private ElevatedInnerPrism() { }
    }

    public static final class SlopedPrism {
        public static boolean testEarlyExit(CompoundDirection cmpDir, Direction side) {
            return side != cmpDir.orientation();
        }

        public static HalfDir getTriDir(CompoundDirection cmpDir, Direction side) {
            Direction dir = cmpDir.direction();
            Direction orientation = cmpDir.orientation();
            if (dir.getAxis() != orientation.getAxis() && side == orientation) {
                return HalfDir.fromDirections(side, dir);
            }
            return HalfDir.NULL;
        }

        private SlopedPrism() { }
    }

    public static final class ElevatedInnerSlopedPrism {
        public static boolean testEarlyExit(CompoundDirection cmpDir, Direction side) {
            return side != cmpDir.orientation();
        }

        public static HalfDir getTriDir(CompoundDirection cmpDir, Direction side) {
            Direction dir = cmpDir.direction();
            Direction orientation = cmpDir.orientation();
            if (dir.getAxis() != orientation.getAxis() && side == orientation) {
                return HalfDir.fromDirections(side, dir);
            }
            return HalfDir.NULL;
        }

        private ElevatedInnerSlopedPrism() { }
    }
}
