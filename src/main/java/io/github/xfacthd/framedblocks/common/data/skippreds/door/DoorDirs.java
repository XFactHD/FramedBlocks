package io.github.xfacthd.framedblocks.common.data.skippreds.door;

import io.github.xfacthd.framedblocks.common.data.skippreds.HalfDir;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.Half;

@SuppressWarnings("JavaExistingMethodCanBeUsed")
public final class DoorDirs {
    public static final class Door {
        public static HalfDir getDoorEdgeDir(Direction dir, DoorHingeSide hinge, boolean open, Direction side) {
            Direction face = !open ? dir.getOpposite() : switch (hinge) {
                case LEFT -> dir.getCounterClockWise();
                case RIGHT -> dir.getClockWise();
            };
            if (side.getAxis() != face.getAxis()) {
                return HalfDir.fromDirections(side, face);
            }
            return HalfDir.NULL;
        }

        private Door() { }
    }

    public static final class Trapdoor {
        public static HalfDir getDoorEdgeDir(Direction dir, Half half, boolean open, Direction side) {
            Direction face = open ? dir.getOpposite() : switch (half) {
                case TOP -> Direction.UP;
                case BOTTOM -> Direction.DOWN;
            };
            if (side.getAxis() != face.getAxis()) {
                return HalfDir.fromDirections(side, face);
            }
            return HalfDir.NULL;
        }

        private Trapdoor() { }
    }

    public static final class Gate {
        public static HalfDir getDoorEdgeDir(Direction dir, DoorHingeSide hinge, boolean open, Direction side) {
            Direction face = !open ? dir.getOpposite() : switch (hinge) {
                case LEFT -> dir.getCounterClockWise();
                case RIGHT -> dir.getClockWise();
            };
            if (side.getAxis() != face.getAxis()) {
                return HalfDir.fromDirections(side, face);
            }
            return HalfDir.NULL;
        }

        private Gate() { }
    }
}
