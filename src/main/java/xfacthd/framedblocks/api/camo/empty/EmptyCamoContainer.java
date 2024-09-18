package xfacthd.framedblocks.api.camo.empty;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.registries.DeferredHolder;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;

public final class EmptyCamoContainer extends CamoContainer<EmptyCamoContent, EmptyCamoContainer>
{
    public static final EmptyCamoContainer EMPTY = new EmptyCamoContainer();
    public static final MutableComponent CAMO_NAME = Utils.translate("desc", "camo.empty").withStyle(ChatFormatting.ITALIC);
    private static final DeferredHolder<CamoContainerFactory<?>, CamoContainerFactory<EmptyCamoContainer>> FACTORY =
            DeferredHolder.create(FramedConstants.CAMO_CONTAINER_FACTORY_REGISTRY_KEY, Utils.rl("empty"));

    private EmptyCamoContainer()
    {
        super(EmptyCamoContent.EMPTY);
    }

    @Override
    public boolean canRotateCamo()
    {
        return false;
    }

    @Override
    public EmptyCamoContainer rotateCamo()
    {
        return null;
    }

    @Override
    public int hashCode()
    {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj == this;
    }

    @Override
    public String toString()
    {
        return "EmptyCamoContainer{}";
    }

    @Override
    public CamoContainerFactory<EmptyCamoContainer> getFactory()
    {
        return FACTORY.value();
    }
}
