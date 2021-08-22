package xfacthd.framedblocks.common.compat.buildinggadgets;

import com.direwolf20.buildinggadgets.common.tainted.building.tilesupport.*;
import com.direwolf20.buildinggadgets.common.tainted.registry.TopologicalRegistryBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

import java.util.function.Supplier;

public class BuildingGadgetsCompat
{
    private static final DeferredRegister<ITileDataSerializer> SERIALIZERS = DeferredRegister.create(ITileDataSerializer.class, FramedBlocks.MODID);
    static final RegistryObject<ITileDataSerializer> FRAMED_SERIALIZER = SERIALIZERS.register("framed_serializer", FramedTileEntityDataSerializer::new);

    public static void init()
    {
        SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BuildingGadgetsCompat::sendCompatImc);
    }

    private static void sendCompatImc(@SuppressWarnings("unused") final InterModEnqueueEvent event)
    {
        InterModComms.sendTo("buildinggadgets", "imc_tile_data_factory", BuildingGadgetsCompat::createDataFactory);
    }

    private static Supplier<TopologicalRegistryBuilder<ITileDataFactory>> createDataFactory()
    {
        return () ->
        {
            TopologicalRegistryBuilder<ITileDataFactory> factory = TopologicalRegistryBuilder.create();
            factory.addValue(
                    new ResourceLocation(FramedBlocks.MODID, "framed_block_data_factory"),
                    te -> te instanceof FramedTileEntity ? new FramedTileEntityData((FramedTileEntity) te) : null
            );
            return factory;
        };
    }

}