package io.github.xfacthd.framedblocks.common.capability.fluid;

import io.github.xfacthd.framedblocks.common.blockentity.special.FramedTankBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public final class TankFluidResourceHandler extends FluidStacksResourceHandler {
    public static final int CAPACITY = 16 * FluidType.BUCKET_VOLUME;
    public static final String FLUID_NBT_KEY = "fluid";

    private final FramedTankBlockEntity blockEntity;

    public TankFluidResourceHandler(FramedTankBlockEntity blockEntity) {
        super(1, CAPACITY);
        this.blockEntity = blockEntity;
    }

    public void setContent(SimpleFluidContent content) {
        set(0, FluidResource.of(content.copy()), content.getAmount());
    }

    @Override
    public void serialize(ValueOutput output) {
        output.store(FLUID_NBT_KEY, FluidStack.OPTIONAL_CODEC, stacks.getFirst());
    }

    @Override
    public void deserialize(ValueInput input) {
        input.read(FLUID_NBT_KEY, FluidStack.OPTIONAL_CODEC).ifPresent(stack -> stacks.set(0, stack));
    }

    @Override
    protected void onContentsChanged(int index, FluidStack previousContents) {
        blockEntity.onTankContentsChanged();
    }
}
