package xfacthd.framedblocks.common.data.camo.fluid;

import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.common.FBContent;

public final class FluidCamoContainer extends CamoContainer<FluidCamoContent, FluidCamoContainer>
{
    public FluidCamoContainer(Fluid fluid)
    {
        super(new FluidCamoContent(fluid));
    }

    public Fluid getFluid()
    {
        return content.getFluid();
    }

    @Override
    public boolean canRotateCamo()
    {
        return false;
    }

    @Override
    @Nullable
    public FluidCamoContainer rotateCamo()
    {
        return null;
    }

    @Override
    public int hashCode()
    {
        return content.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != FluidCamoContainer.class) return false;
        return content.equals(((FluidCamoContainer) obj).content);
    }

    @Override
    public CamoContainerFactory<FluidCamoContainer> getFactory()
    {
        return FBContent.FACTORY_FLUID.value();
    }
}
