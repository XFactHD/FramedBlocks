package xfacthd.framedblocks.common.compat.buildinggadgets;

import com.direwolf20.buildinggadgets.common.tainted.building.tilesupport.*;
import com.direwolf20.buildinggadgets.common.tainted.registry.TopologicalRegistryBuilder;
import com.direwolf20.buildinggadgets.common.util.ref.Reference;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;

import java.util.function.Supplier;

public final class BuildingGadgetsCompat
{
    public static void init()
    {
        if (ModList.get().isLoaded("buildinggadgets"))
        {
            /* Safeguard against potential API changes in Building Gadgets
             * Providing a config option is not possible because the integration requires registering
             * a custom registry object which happens before configs load
             */

            try
            {
                GuardedAccess.init();
            }
            catch (Throwable e)
            {
                FramedBlocks.LOGGER.warn("An error occured while initializing Building Gadgets integration!", e);
            }
        }
    }

    static final class GuardedAccess
    {
        private static final DeferredRegister<ITileDataSerializer> SERIALIZERS = DeferredRegister.create(
                Reference.TileDataSerializerReference.REGISTRY_ID_TILE_DATA_SERIALIZER,
                FramedConstants.MOD_ID
        );
        static final RegistryObject<ITileDataSerializer> FRAMED_SERIALIZER = SERIALIZERS.register(
                "framed_serializer", FramedBlockEntityDataSerializer::new
        );

        public static void init()
        {
            SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
            FMLJavaModLoadingContext.get().getModEventBus().addListener(GuardedAccess::sendCompatImc);
        }

        private static void sendCompatImc(@SuppressWarnings("unused") final InterModEnqueueEvent event)
        {
            InterModComms.sendTo("buildinggadgets", "imc_tile_data_factory", GuardedAccess::createDataFactory);
        }

        private static Supplier<TopologicalRegistryBuilder<ITileDataFactory>> createDataFactory()
        {
            return () ->
            {
                TopologicalRegistryBuilder<ITileDataFactory> factory = TopologicalRegistryBuilder.create();
                factory.addValue(
                        Utils.rl("framed_block_data_factory"),
                        te -> te instanceof FramedBlockEntity ? new FramedBlockEntityData((FramedBlockEntity) te) : null
                );
                return factory;
            };
        }



        private GuardedAccess() { }
    }



    private BuildingGadgetsCompat() { }
}
