package io.github.xfacthd.framedblocks.client.render.block.state;

import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.jetbrains.annotations.UnknownNullability;

public final class FramedTankRenderState extends BlockEntityRenderState {
    @UnknownNullability
    public FluidModel fluidModel;
    public int fluidAmount;
    public boolean lighterThanAir;
    public boolean gaseous;
    public int tint;
    public int fluidLightEmission;
}
