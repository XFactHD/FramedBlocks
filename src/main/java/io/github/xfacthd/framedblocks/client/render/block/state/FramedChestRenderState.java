package io.github.xfacthd.framedblocks.client.render.block.state;

import com.mojang.math.Axis;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public final class FramedChestRenderState extends BlockEntityRenderState {
    public final BlockModelRenderState modelRenderState = new BlockModelRenderState();
    public float rotOriginX;
    public float rotOriginZ;
    public Axis lidHingeAxis = Axis.XN;
    public float lidAngle = 0F;
}
