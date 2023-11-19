package xfacthd.framedblocks.common.compat.create;

//import com.simibubi.create.AllInteractionBehaviours;
//import com.simibubi.create.content.contraptions.BlockMovementChecks;
//import com.simibubi.create.content.contraptions.behaviour.*;
//import com.simibubi.create.foundation.block.connected.CTModel;
//import com.simibubi.create.foundation.utility.NBTProcessors;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.data.ConTexDataHandler;
import xfacthd.framedblocks.common.FBContent;

public final class CreateCompat
{
    public static void init()
    {
        if (ModList.get().isLoaded("create"))
        {
            // Safeguard against potential changes in Create since the ct context property is not exposed as API
            try
            {
                if (FMLEnvironment.dist.isClient())
                {
                    //GuardedClientAccess.init();
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
                //GuardedAccess.init();
            }
            catch (Throwable e)
            {
                FramedBlocks.LOGGER.warn("An error occured while initializing Create integration!", e);
            }
        }
    }

    /*private static final class GuardedAccess
    {
        public static void init()
        {
            registerInteractionBehaviour(FBContent.BLOCK_FRAMED_LEVER, new LeverMovingInteraction());
            registerInteractionBehaviour(FBContent.BLOCK_FRAMED_DOOR, new DoorMovingInteraction());
            registerInteractionBehaviour(FBContent.BLOCK_FRAMED_TRAP_DOOR, new TrapdoorMovingInteraction());

            BlockMovementChecks.registerAllChecks(new FramedBlockMovementChecks());

            NBTProcessors.addProcessor(FBContent.BE_TYPE_FRAMED_SIGN.get(), tag ->
            {
                for (int i = 0; i < 4; ++i)
                {
                    String key = "Text" + (i + 1);
                    if (NBTProcessors.textComponentHasClickEvent(tag.getString(key)))
                    {
                        tag.remove(key);
                    }
                }
                return tag;
            });
        }

        private static void registerInteractionBehaviour(RegistryObject<Block> block, MovingInteractionBehaviour behaviour)
        {
            AllInteractionBehaviours.registerBehaviour(block.get(), behaviour);
        }
    }

    private static final class GuardedClientAccess
    {

        public static void init()
        {
            ModelProperty<?> ctProperty = Utils.getPrivateValue(CTModel.class, null, "CT_PROPERTY");
            ConTexDataHandler.addConTexProperty(ctProperty);
        }
    }*/



    private CreateCompat() { }
}
