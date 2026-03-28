package io.github.xfacthd.framedblocks.client.render.block.state;

import io.github.xfacthd.framedblocks.api.render.Quaternions;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.joml.Quaternionfc;

public final class FramedChestRenderState extends BlockEntityRenderState {
    public final BlockModelRenderState modelRenderState = new BlockModelRenderState();
    public float rotOriginX;
    public float rotOriginZ;
    public Quaternionfc lidAngle = Quaternions.ONE;
}
