package io.github.xfacthd.framedblocks.common.data.property;

import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.text.Printable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

import java.util.Locale;
import java.util.Objects;

public enum CornerTubeOrientation implements StringRepresentable, Printable {
    UP_NORTH(Direction.UP, Direction.NORTH),
    UP_EAST(Direction.UP, Direction.EAST),
    UP_SOUTH(Direction.UP, Direction.SOUTH),
    UP_WEST(Direction.UP, Direction.WEST),

    DOWN_NORTH(Direction.DOWN, Direction.NORTH),
    DOWN_EAST(Direction.DOWN, Direction.EAST),
    DOWN_SOUTH(Direction.DOWN, Direction.SOUTH),
    DOWN_WEST(Direction.DOWN, Direction.WEST),

    NORTH_EAST(Direction.NORTH, Direction.EAST),
    EAST_SOUTH(Direction.EAST, Direction.SOUTH),
    SOUTH_WEST(Direction.SOUTH, Direction.WEST),
    WEST_NORTH(Direction.WEST, Direction.NORTH);

    private static final CornerTubeOrientation[][] FROM_DIRS = makeDirTable();
    public static final int COUNT = values().length;

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final Component displayName = Utils.translate("value", "corner_tube_orientation." + name);
    private final Direction primDir;
    private final Direction secDir;

    CornerTubeOrientation(Direction primDir, Direction secDir) {
        this.primDir = primDir;
        this.secDir = secDir;
    }

    public Direction getPrimaryDir() {
        return primDir;
    }

    public Direction getSecondaryDir() {
        return secDir;
    }

    public boolean isVertical() {
        return DirUtils.isY(primDir);
    }

    public boolean isSideOpen(Direction side) {
        return side == primDir || side == secDir;
    }

    public CornerTubeOrientation rotate(Rotation rot) {
        if (rot == Rotation.NONE) {
            return this;
        }
        return of(rot.rotate(primDir), rot.rotate(secDir));
    }

    public CornerTubeOrientation mirror(Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return this;
        }
        return of(mirror.mirror(primDir), mirror.mirror(secDir));
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    @Override
    public Component print(ChatFormatting defaultColor) {
        return displayName.copy().withStyle(defaultColor);
    }

    public static CornerTubeOrientation of(Direction dirOne, Direction dirTwo) {
        CornerTubeOrientation orientationOne = FROM_DIRS[dirOne.ordinal()][dirTwo.ordinal()];
        CornerTubeOrientation orientationTwo = FROM_DIRS[dirTwo.ordinal()][dirOne.ordinal()];
        if (orientationOne == null && orientationTwo == null) {
            throw new IllegalArgumentException(
                    "Invalid direction pair! Primary dir: " + dirOne + ", Secondary dir: " + dirTwo
            );
        }
        return Objects.requireNonNullElse(orientationOne, orientationTwo);
    }

    private static CornerTubeOrientation[][] makeDirTable() {
        CornerTubeOrientation[][] table = new CornerTubeOrientation[6][6];
        for (CornerTubeOrientation orientation : CornerTubeOrientation.values()) {
            Direction primDir = orientation.primDir;
            Direction secDir = orientation.secDir;
            table[primDir.ordinal()][secDir.ordinal()] = orientation;
        }
        return table;
    }
}
