package io.github.xfacthd.framedblocks.common.data.skippreds.misc;

import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.property.CornerTubeOrientation;
import io.github.xfacthd.framedblocks.common.data.skippreds.HalfDir;
import io.github.xfacthd.framedblocks.common.data.skippreds.TubeOpening;
import net.minecraft.core.Direction;

public final class MiscDirs
{
    public static final class Tube
    {
        public static boolean testEarlyExit(Direction.Axis axis, Direction side)
        {
            return side.getAxis() != axis;
        }

        public static TubeOpening getOpeningDir(Direction.Axis axis, boolean thick, Direction side)
        {
            if (side.getAxis() == axis)
            {
                return thick ? TubeOpening.THICK : TubeOpening.THIN;
            }
            return TubeOpening.NONE;
        }

        private Tube() { }
    }

    public static final class CornerTube
    {
        public static boolean testEarlyExit(CornerTubeOrientation orientation, Direction side)
        {
            return !orientation.isSideOpen(side);
        }

        public static TubeOpening getOpeningDir(CornerTubeOrientation orientation, boolean thick, Direction side)
        {
            if (orientation.isSideOpen(side))
            {
                return thick ? TubeOpening.THICK : TubeOpening.THIN;
            }
            return TubeOpening.NONE;
        }

        private CornerTube() { }
    }

    public static final class Hopper
    {
        public static boolean isHopperSideDir(Direction side)
        {
            return !DirUtils.isY(side);
        }

        public static TubeOpening getOpeningDir(Direction side)
        {
            return side == Direction.UP ? TubeOpening.THIN : TubeOpening.NONE;
        }

        private Hopper() { }
    }

    public static final class LayeredCube
    {
        public static int getLayersDir(Direction dir, int layers, Direction side)
        {
            if (side.getAxis() != dir.getAxis())
            {
                return dir.ordinal() << 3 | (layers - 1);
            }
            return 0;
        }

        public static HalfDir getHalfDir(Direction dir, int layers, Direction side)
        {
            if (side.getAxis() != dir.getAxis() && layers == 4)
            {
                return HalfDir.fromDirections(side, dir.getOpposite());
            }
            return HalfDir.NULL;
        }

        private LayeredCube() { }
    }
}
