package xfacthd.framedblocks.api.camo.empty;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.internal.InternalAPI;
import xfacthd.framedblocks.api.util.Utils;

public final class EmptyCamoContainer extends CamoContainer<EmptyCamoContent, EmptyCamoContainer>
{
    public static final EmptyCamoContainer EMPTY = new EmptyCamoContainer();
    public static final MutableComponent CAMO_NAME = Utils.translate("desc", "camo.empty").withStyle(ChatFormatting.ITALIC);

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
    public CamoContainerFactory<EmptyCamoContainer> getFactory()
    {
        return InternalAPI.INSTANCE.getEmptyCamoContainerFactory();
    }
}
