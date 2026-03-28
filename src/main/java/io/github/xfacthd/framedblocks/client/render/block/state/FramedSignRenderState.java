package io.github.xfacthd.framedblocks.client.render.block.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.SignText;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector3f;

public final class FramedSignRenderState extends BlockEntityRenderState {
    public boolean standing;
    public float yRot;
    @UnknownNullability
    public SignText frontText;
    @UnknownNullability
    public SignText backText;
    @UnknownNullability
    public Vector3f textOffset;
    public int lineHeight;
    public int lineWidth;
    public boolean outline;
}
