package xfacthd.framedblocks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedDoubleBlockDebugRenderer implements BlockEntityRenderer<FramedDoubleBlockEntity>
{
    private static final ModelData MODEL_DATA = Util.make(() ->
    {
        FramedBlockData data = new FramedBlockData();
        data.setCamoState(Blocks.STONE.defaultBlockState());
        return ModelData.EMPTY.derive().with(FramedBlockData.PROPERTY, data).build();
    });

    public FramedDoubleBlockDebugRenderer(@SuppressWarnings("unused") BlockEntityRendererProvider.Context ctx) { }

    @Override
    public void render(
            FramedDoubleBlockEntity be,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    )
    {
        HitResult hit = Minecraft.getInstance().hitResult;
        if (!(hit instanceof BlockHitResult blockHit) || !blockHit.getBlockPos().equals(be.getBlockPos()))
        {
            return;
        }

        Tuple<BlockState, BlockState> blockPair = be.getBlockPair();
        boolean secondary = be.debugHitSecondary(blockHit);
        BlockState state = secondary ? blockPair.getB() : blockPair.getA();
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);

        OutlineBufferSource outlineBuffer = Minecraft.getInstance().renderBuffers().outlineBufferSource();
        outlineBuffer.setColor(
                secondary ? 0x00 : 0xFF,
                secondary ? 0xFF : 0x00,
                0x00,
                0xFF
        );

        //noinspection deprecation
        VertexConsumer consumer = outlineBuffer.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                consumer,
                state,
                model,
                1F, 1F, 1F,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                MODEL_DATA,
                RenderType.solid()
        );
    }
}
