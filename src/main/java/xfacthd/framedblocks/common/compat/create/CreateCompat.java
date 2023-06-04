package xfacthd.framedblocks.common.compat.create;

import com.simibubi.create.AllInteractionBehaviours;
import com.simibubi.create.content.contraptions.BlockMovementChecks;
import com.simibubi.create.content.contraptions.behaviour.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;

public class CreateCompat
{
    public static void init()
    {
        if (ModList.get().isLoaded("create"))
        {
            // Safeguard against potential changes in Create since the interaction behaviours are not exposed as API
            try
            {
                GuardedAccess.init();
            }
            catch (Throwable e)
            {
                FramedBlocks.LOGGER.warn("An error occured while initializing Create integration!", e);
            }
        }
    }

    private static final class GuardedAccess
    {
        public static void init()
        {
            registerInteractionBehaviour(FBContent.blockFramedLever, new LeverMovingInteraction());

            BlockMovementChecks.registerAllChecks(new FramedBlockMovementChecks());
        }

        private static void registerInteractionBehaviour(RegistryObject<Block> block, MovingInteractionBehaviour behaviour)
        {
            AllInteractionBehaviours.registerBehaviour(block.get(), behaviour);
        }
    }
}
