package xfacthd.framedblocks.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.*;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, value = Dist.CLIENT)
public class ClientEventHandler
{
    @SubscribeEvent
    public static void onRenderBlockHighlight(final DrawSelectionEvent.HighlightBlock event)
    {
        if (!ClientConfig.fancyHitboxes) { return; }

        BlockHitResult result = event.getTarget();
        //noinspection ConstantConditions
        BlockState state = Minecraft.getInstance().level.getBlockState(result.getBlockPos());
        if (!(state.getBlock() instanceof IFramedBlock block)) { return; }

        IBlockType type = block.getBlockType();
        if (type.hasSpecialHitbox() && type instanceof BlockType blockType)
        {
            PoseStack mstack = event.getMatrix();
            Vec3 offset = Vec3.atLowerCornerOf(result.getBlockPos()).subtract(event.getInfo().getPosition());
            VertexConsumer builder = event.getBuffers().getBuffer(RenderType.lines());

            Direction dir = state.getValue(PropertyHolder.FACING_HOR);

            mstack.pushPose();
            mstack.translate(offset.x, offset.y, offset.z);
            mstack.translate(.5, .5, .5);
            mstack.mulPose(Vector3f.YP.rotationDegrees(-dir.toYRot()));
            mstack.translate(-.5, -.5, -.5);

            switch (blockType) //TODO: defer to the IBlockType to allow drawing outlines for blocks from add-ons
            {
                case FRAMED_SLOPE, FRAMED_RAIL_SLOPE -> drawSlopeBox(state, mstack, builder);
                case FRAMED_CORNER_SLOPE -> drawCornerSlopeBox(state, mstack, builder);
                case FRAMED_INNER_CORNER_SLOPE -> drawInnerCornerSlopeBox(state, mstack, builder);
                case FRAMED_PRISM_CORNER -> drawPrismCornerBox(state, mstack, builder);
                case FRAMED_INNER_PRISM_CORNER -> drawInnerPrismCornerBox(state, mstack, builder);
                case FRAMED_THREEWAY_CORNER -> drawThreewayCornerBox(state, mstack, builder);
                case FRAMED_INNER_THREEWAY_CORNER -> drawInnerThreewayCornerBox(state, mstack, builder);
            }

            mstack.popPose();

            event.setCanceled(true);
        }
    }

    private static void drawSlopeBox(BlockState state, PoseStack mstack, VertexConsumer builder)
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);

        if (type != SlopeType.HORIZONTAL)
        {
            if (type == SlopeType.TOP)
            {
                mstack.translate(.5, .5, .5);
                mstack.scale(1, -1, 1);
                mstack.translate(-.5, -.5, -.5);
            }

            //Back edges
            drawLine(builder, mstack, 0, 0, 1, 0, 1, 1);
            drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

            //Bottom face
            drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
            drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);
            drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
            drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);

            //Top edge
            drawLine(builder, mstack, 0, 1, 1, 1, 1, 1);

            //Slope
            drawLine(builder, mstack, 0, 0, 0, 0, 1, 1);
            drawLine(builder, mstack, 1, 0, 0, 1, 1, 1);
        }
        else
        {
            //Back
            drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);
            drawLine(builder, mstack, 0, 1, 1, 1, 1, 1);
            drawLine(builder, mstack, 0, 0, 1, 0, 1, 1);
            drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

            //Left side
            drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
            drawLine(builder, mstack, 1, 1, 0, 1, 1, 1);
            drawLine(builder, mstack, 1, 0, 0, 1, 1, 0);

            //Slope
            drawLine(builder, mstack, 1, 0, 0, 0, 0, 1);
            drawLine(builder, mstack, 1, 1, 0, 0, 1, 1);
        }
    }

    private static void drawCornerSlopeBox(BlockState state, PoseStack mstack, VertexConsumer builder)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (!type.isHorizontal())
        {
            if (type.isTop())
            {
                mstack.translate(.5, .5, .5);
                mstack.scale(1, -1, 1);
                mstack.translate(-.5, -.5, -.5);
            }

            //Back edge
            drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

            //Bottom face
            drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
            drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);
            drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
            drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);

            //Slope
            drawLine(builder, mstack, 0, 0, 0, 1, 1, 1);
            drawLine(builder, mstack, 1, 0, 0, 1, 1, 1);
            drawLine(builder, mstack, 0, 0, 1, 1, 1, 1);
        }
        else
        {
            mstack.translate(.5, .5, .5);
            if (!type.isRight()) { mstack.scale(-1, 1, 1); }
            if (type.isTop()) { mstack.scale(1, -1, 1); }
            mstack.translate(-.5, -.5, -.5);

            //Back face
            drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);
            drawLine(builder, mstack, 0, 1, 1, 1, 1, 1);
            drawLine(builder, mstack, 0, 0, 1, 0, 1, 1);
            drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

            //Back edge
            drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);

            //Center slope edge
            drawLine(builder, mstack, 0, 0, 0, 1, 1, 1);

            //Side slope edges
            drawLine(builder, mstack, 0, 0, 0, 0, 1, 1);
            drawLine(builder, mstack, 0, 0, 0, 1, 0, 1);
        }
    }

    private static void drawInnerCornerSlopeBox(BlockState state, PoseStack mstack, VertexConsumer builder)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal())
        {
            mstack.translate(.5, .5, .5);
            if (type.isTop()) { mstack.scale(1, -1, 1); }
            mstack.translate(-.5, -.5, -.5);

            //Back face
            drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);
            drawLine(builder, mstack, 0, 1, 1, 1, 1, 1);
            drawLine(builder, mstack, 0, 0, 1, 0, 1, 1);
            drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

            //Left face
            drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
            drawLine(builder, mstack, 1, 1, 0, 1, 1, 1);
            drawLine(builder, mstack, 1, 0, 0, 1, 1, 0);

            //Bottom face
            drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
            drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);

            //Slope edges
            drawLine(builder, mstack, 0, 0, 0, 0, 1, 1);
            drawLine(builder, mstack, 0, 0, 0, 1, 1, 0);
            drawLine(builder, mstack, 0, 0, 0, 1, 1, 1);
        }
        else
        {
            mstack.translate(.5, .5, .5);
            if (!type.isRight()) { mstack.scale(-1, 1, 1); }
            if (type.isTop()) { mstack.scale(1, -1, 1); }
            mstack.translate(-.5, -.5, -.5);

            //Top face
            drawLine(builder, mstack, 0, 1, 1, 1, 1, 1);
            drawLine(builder, mstack, 0, 1, 0, 0, 1, 1);
            drawLine(builder, mstack, 0, 1, 0, 1, 1, 1);

            //Bottom face
            drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);
            drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);
            drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
            drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);

            //Right face
            drawLine(builder, mstack, 0, 0, 0, 0, 1, 0);
            drawLine(builder, mstack, 0, 0, 1, 0, 1, 1);

            //Left face
            drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);
            drawLine(builder, mstack, 1, 0, 0, 1, 1, 1);

            //Slope edge
            drawLine(builder, mstack, 0, 0, 0, 1, 1, 1);
        }
    }

    private static void drawPrismCornerBox(BlockState state, PoseStack mstack, VertexConsumer builder)
    {
        boolean top = state.getValue(PropertyHolder.TOP);

        mstack.translate(.5, .5, .5);
        if (top) { mstack.scale(1, -1, 1); }
        mstack.translate(-.5, -.5, -.5);

        //Back edge
        drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

        //Bottom edges
        drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
        drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);

        //Front edge
        drawLine(builder, mstack, 1, 0, 0, 0, 0, 1);

        //Slope edges
        drawLine(builder, mstack, 1, 0, 0, 1, 1, 1);
        drawLine(builder, mstack, 0, 0, 1, 1, 1, 1);
    }

    private static void drawInnerPrismCornerBox(BlockState state, PoseStack mstack, VertexConsumer builder)
    {
        boolean top = state.getValue(PropertyHolder.TOP);

        mstack.translate(.5, .5, .5);
        if (top) { mstack.scale(1, -1, 1); }
        mstack.translate(-.5, -.5, -.5);

        //Bottom face
        drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
        drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);
        drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
        drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);

        //Back face
        drawLine(builder, mstack, 1, 0, 0, 1, 1, 0);
        drawLine(builder, mstack, 0, 0, 1, 0, 1, 1);
        drawLine(builder, mstack, 1, 1, 0, 1, 1, 1);

        //Right face
        drawLine(builder, mstack, 0, 1, 1, 1, 1, 1);
        drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

        //Slope edges
        drawLine(builder, mstack, 1, 1, 0, 0, 1, 1);
        drawLine(builder, mstack, 0, 0, 0, 1, 1, 0);
        drawLine(builder, mstack, 0, 0, 0, 0, 1, 1);
    }

    private static void drawThreewayCornerBox(BlockState state, PoseStack mstack, VertexConsumer builder)
    {
        boolean top = state.getValue(PropertyHolder.TOP);

        mstack.translate(.5, .5, .5);
        if (top) { mstack.scale(1, -1, 1); }
        mstack.translate(-.5, -.5, -.5);

        //Back edges
        drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);
        drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
        drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);

        //Slope edges
        drawLine(builder, mstack, 1, 0, 0, 1, 1, 1);
        drawLine(builder, mstack, 1, 0, 0, 0, 0, 1);
        drawLine(builder, mstack, 0, 0, 1, 1, 1, 1);

        //Cross
        drawLine(builder, mstack, 1, 0, 0, .5, .5, .5);
        drawLine(builder, mstack, .5, .5, .5, 1, 1, 1);
        drawLine(builder, mstack, 0, 0, 1, .5, .5, .5);
    }

    private static void drawInnerThreewayCornerBox(BlockState state, PoseStack mstack, VertexConsumer builder)
    {
        boolean top = state.getValue(PropertyHolder.TOP);

        mstack.translate(.5, .5, .5);
        if (top) { mstack.scale(1, -1, 1); }
        mstack.translate(-.5, -.5, -.5);

        //Bottom face
        drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
        drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);
        drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
        drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);

        //Back face
        drawLine(builder, mstack, 0, 1, 1, 1, 1, 1);
        drawLine(builder, mstack, 0, 0, 1, 0, 1, 1);
        drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

        //Left face
        drawLine(builder, mstack, 1, 1, 0, 1, 1, 1);
        drawLine(builder, mstack, 1, 0, 0, 1, 1, 0);

        //Slope edges
        drawLine(builder, mstack, 0, 0, 0, 0, 1, 1);
        drawLine(builder, mstack, 0, 0, 0, 1, 1, 0);
        drawLine(builder, mstack, 0, 1, 1, 1, 1, 0);

        //Cross
        drawLine(builder, mstack, 0, 0, 0, .5, .5, .5);
        drawLine(builder, mstack, .5, .5, .5, 0, 1, 1);
        drawLine(builder, mstack, 1, 1, 0, .5, .5, .5);
    }

    //TODO: move to API to allow usage by add-ons
    private static void drawLine(VertexConsumer builder, PoseStack mstack, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        float nX = (float)(x2 - x1);
        float nY = (float)(y2 - y1);
        float nZ = (float)(z2 - z1);
        float nLen = Mth.sqrt(nX * nX + nY * nY + nZ * nZ);

        nX = nX / nLen;
        nY = nY / nLen;
        nZ = nZ / nLen;

        builder.vertex(mstack.last().pose(), (float)x1, (float)y1, (float)z1).color(0.0F, 0.0F, 0.0F, 0.4F).normal(mstack.last().normal(), nX, nY, nZ).endVertex();
        builder.vertex(mstack.last().pose(), (float)x2, (float)y2, (float)z2).color(0.0F, 0.0F, 0.0F, 0.4F).normal(mstack.last().normal(), nX, nY, nZ).endVertex();
    }
}