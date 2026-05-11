package io.github.xfacthd.framedblocks.common.data.camo.fluid;

import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.resource.ResourceCamoContainer;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public final class FluidCamoContainer extends ResourceCamoContainer<FluidResource, FluidCamoContent, FluidCamoContainer> {
    private static final Direction[] DIRECTIONS = Direction.values();

    public FluidCamoContainer(FluidResource fluid, Direction flowDirection) {
        super(new FluidCamoContent(fluid, flowDirection));
    }

    public Fluid getFluid() {
        return content.getFluid();
    }

    public Direction getFlowDirection() {
        return content.getFlowDirection();
    }

    @Override
    public boolean canRotateCamo() {
        return true;
    }

    @Override
    public FluidCamoContainer rotateCamo() {
        int nextIdx = (getFlowDirection().ordinal() + 1) % DIRECTIONS.length;
        return new FluidCamoContainer(getResource(), DIRECTIONS[nextIdx]);
    }

    @Override
    public FluidCamoContainer adjustForCarrierRotation(Mirror mirror, Rotation rotation) {
        Direction flowDir = getFlowDirection();
        Direction newFlowDir = rotation.rotate(mirror.mirror(flowDir));
        return flowDir != newFlowDir ? new FluidCamoContainer(getResource(), newFlowDir) : this;
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return obj instanceof FluidCamoContainer fluidContainer && content.equals(fluidContainer.content);
    }

    @Override
    public String toString() {
        return "FluidCamoContainer{content=" + content + "}";
    }

    @Override
    public CamoContainerFactory<FluidCamoContainer> getFactory() {
        return FBContent.FACTORY_FLUID.value();
    }
}
