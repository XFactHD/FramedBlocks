package xfacthd.framedblocks.common.compat.create;

import com.simibubi.create.AllInteractionBehaviours;
import com.simibubi.create.content.contraptions.components.structureMovement.BlockMovementChecks;
import com.simibubi.create.content.contraptions.components.structureMovement.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.interaction.*;
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
            AllInteractionBehaviours.registerBehaviour(block.get().delegate, behaviour);
        }
    }
}
