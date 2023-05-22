package xfacthd.framedblocks.common.compat.create;

import com.simibubi.create.AllInteractionBehaviours;
import com.simibubi.create.content.contraptions.components.structureMovement.BlockMovementChecks;
import com.simibubi.create.content.contraptions.components.structureMovement.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.interaction.*;
import com.simibubi.create.foundation.block.connected.CTModel;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;

public final class CreateCompat
{
    private static boolean loadedClient = false;

    public static void init()
    {
        if (ModList.get().isLoaded("create"))
        {
            // Safeguard against potential changes in Create since the ct context property is not exposed as API
            try
            {
                if (FMLEnvironment.dist.isClient())
                {
                    GuardedClientAccess.init();
                    loadedClient = true;
                }
            }
            catch (Throwable e)
            {
                FramedBlocks.LOGGER.warn("An error occured while initializing client-only Create integration!", e);
            }
        }
    }

    public static void commonSetup()
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

    public static Object tryGetCTContext(ModelData data)
    {
        if (loadedClient)
        {
            return GuardedClientAccess.tryGetCTContext(data);
        }
        return null;
    }

    private static final class GuardedAccess
    {
        public static void init()
        {
            registerInteractionBehaviour(FBContent.blockFramedLever, new LeverMovingInteraction());
            registerInteractionBehaviour(FBContent.blockFramedDoor, new DoorMovingInteraction());
            registerInteractionBehaviour(FBContent.blockFramedTrapDoor, new TrapdoorMovingInteraction());

            BlockMovementChecks.registerAllChecks(new FramedBlockMovementChecks());
        }

        private static void registerInteractionBehaviour(RegistryObject<Block> block, MovingInteractionBehaviour behaviour)
        {
            AllInteractionBehaviours.registerBehaviour(block.get(), behaviour);
        }
    }

    private static final class GuardedClientAccess
    {
        private static ModelProperty<?> CREATE_CT_PROPERTY;

        public static void init()
        {
            CREATE_CT_PROPERTY = ObfuscationReflectionHelper.getPrivateValue(CTModel.class, null, "CT_PROPERTY");
        }

        public static Object tryGetCTContext(ModelData data)
        {
            return data.get(CREATE_CT_PROPERTY);
        }
    }



    private CreateCompat() { }
}
