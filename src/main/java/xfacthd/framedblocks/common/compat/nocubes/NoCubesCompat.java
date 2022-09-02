package xfacthd.framedblocks.common.compat.nocubes;

import io.github.cadiboo.nocubes.NoCubes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;

public final class NoCubesCompat
{
    private static boolean noCubesLoaded = false;

    public static void init() { noCubesLoaded = ModList.get().isLoaded("nocubes"); }

    public static boolean mayCullNextTo(BlockState state)
    {
        if (noCubesLoaded)
        {
            return GuardedAccess.mayCull(state);
        }
        return true;
    }

    private static final class GuardedAccess
    {
        public static boolean mayCull(BlockState state)
        {
            return !NoCubes.isSmoothable(state);
        }
    }



    private NoCubesCompat() { }
}
