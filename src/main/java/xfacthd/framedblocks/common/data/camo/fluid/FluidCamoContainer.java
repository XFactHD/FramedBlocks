package xfacthd.framedblocks.common.data.camo.fluid;

import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.common.FBContent;

public final class FluidCamoContainer extends CamoContainer<FluidCamoContent, FluidCamoContainer>
{
    static final Direction[] DIRECTIONS = Direction.values();

    public FluidCamoContainer(Fluid fluid, Direction flowDirection)
    {
        super(new FluidCamoContent(fluid, flowDirection));
    }

    public Fluid getFluid()
    {
        return content.getFluid();
    }

    public Direction getFlowDirection()
    {
        return content.getFlowDirection();
    }

    @Override
    public boolean canRotateCamo()
    {
        return true;
    }

    @Override
    public FluidCamoContainer rotateCamo()
    {
        int nextIdx = (getFlowDirection().ordinal() + 1) % DIRECTIONS.length;
        return new FluidCamoContainer(getFluid(), DIRECTIONS[nextIdx]);
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
    public String toString()
    {
        return "FluidCamoContainer{content=" + content + "}";
    }

    @Override
    public CamoContainerFactory<FluidCamoContainer> getFactory()
    {
        return FBContent.FACTORY_FLUID.value();
    }
}
