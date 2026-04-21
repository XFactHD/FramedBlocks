package io.github.xfacthd.framedblocks.client.render.block.state;

import com.mojang.math.Transformation;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class FramedBannerRenderState extends BlockEntityRenderState {
    private static final Matrix4fc IDENTITY = new Matrix4f();

    public final BlockModelRenderState modelRenderState = new BlockModelRenderState();
    @UnknownNullability
    public BannerModel bannerModel = null;
    public Transformation bannerTransform = Transformation.IDENTITY;
    public Matrix4fc flagTransform = IDENTITY;
    public float swing;
}
