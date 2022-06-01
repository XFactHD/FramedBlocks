package xfacthd.framedblocks.common.compat.flywheel;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.fml.ModList;

public final class FlywheelCompat
{
    private static boolean loaded = false;

    public static void init()
    {
        loaded = ModList.get().isLoaded("flywheel");
        if (loaded)
        {
            GuardedAccess.init();
        }
    }

    public static boolean isVirtualLevel(BlockGetter level)
    {
        if (loaded)
        {
            return GuardedAccess.isVirtualLevel(level);
        }
        return false;
    }

    private static final class GuardedAccess
    {
        public static void init()
        {
            //TODO: try to find a way to allow the camo to render while on a moving contraption
        }

        public static boolean isVirtualLevel(BlockGetter level)
        {
            return level instanceof VirtualRenderWorld;
        }
    }



    private FlywheelCompat() { }
}
