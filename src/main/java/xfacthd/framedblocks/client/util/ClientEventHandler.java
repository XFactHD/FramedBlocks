package xfacthd.framedblocks.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.Utils;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, value = Dist.CLIENT)
public class ClientEventHandler
{
    @SubscribeEvent
    public static void onRenderBlockHighlight(final DrawHighlightEvent.HighlightBlock event)
    {
        if (!ClientConfig.fancyHitboxes) { return; }

        BlockRayTraceResult result = event.getTarget();
        //noinspection ConstantConditions
        BlockState state = Minecraft.getInstance().world.getBlockState(result.getPos());
        if (!(state.getBlock() instanceof IFramedBlock)) { return; }

        BlockType type = ((IFramedBlock) state.getBlock()).getBlockType();
        if (type.hasSpecialHitbox())
        {
            MatrixStack mstack = event.getMatrix();
            Vector3d offset = Vector3d.copy(result.getPos()).subtract(event.getInfo().getProjectedView());
            IVertexBuilder builder = event.getBuffers().getBuffer(RenderType.getLines());

            Direction dir = Utils.getBlockFacing(state);

            mstack.push();
            mstack.translate(offset.x, offset.y, offset.z);
            mstack.translate(.5, .5, .5);
            mstack.rotate(Vector3f.YP.rotationDegrees(-dir.getHorizontalAngle()));
            mstack.translate(-.5, -.5, -.5);

            switch (type)
            {
                case FRAMED_SLOPE:
                case FRAMED_RAIL_SLOPE:
                    drawSlopeBox(state, mstack, builder);
                    break;
                case FRAMED_CORNER_SLOPE:
                    drawCornerSlopeBox(state, mstack, builder);
                    break;
                case FRAMED_INNER_CORNER_SLOPE:
                    drawInnerCornerSlopeBox(state, mstack, builder);
                    break;
                case FRAMED_PRISM_CORNER:
                    drawPrismCornerBox(state, mstack, builder);
                    break;
                case FRAMED_INNER_PRISM_CORNER:
                    drawInnerPrismCornerBox(state, mstack, builder);
                    break;
                case FRAMED_THREEWAY_CORNER:
                    drawThreewayCornerBox(state, mstack, builder);
                    break;
                case FRAMED_INNER_THREEWAY_CORNER:
                    drawInnerThreewayCornerBox(state, mstack, builder);
                    break;
            }

            mstack.pop();

            event.setCanceled(true);
        }
    }

    private static void drawSlopeBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        SlopeType type = state.get(PropertyHolder.SLOPE_TYPE);

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

    private static void drawCornerSlopeBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);
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

    private static void drawInnerCornerSlopeBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);

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

            //Right face
            drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
            drawLine(builder, mstack, 0, 1, 0, 0, 1, 1);
            drawLine(builder, mstack, 0, 0, 0, 0, 1, 0);

            //Bottom face
            drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
            drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);

            //Slope edges
            drawLine(builder, mstack, 1, 0, 0, 1, 1, 1);
            drawLine(builder, mstack, 1, 0, 0, 0, 1, 0);
            drawLine(builder, mstack, 1, 0, 0, 0, 1, 1);
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

    private static void drawPrismCornerBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        boolean top = state.get(PropertyHolder.TOP);

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

    private static void drawInnerPrismCornerBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        boolean top = state.get(PropertyHolder.TOP);

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

    private static void drawThreewayCornerBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        boolean top = state.get(PropertyHolder.TOP);

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

    private static void drawInnerThreewayCornerBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        boolean top = state.get(PropertyHolder.TOP);

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

        //Right face
        drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
        drawLine(builder, mstack, 0, 1, 0, 0, 1, 1);
        drawLine(builder, mstack, 0, 0, 0, 0, 1, 0);

        //Slope edges
        drawLine(builder, mstack, 1, 0, 0, 1, 1, 1);
        drawLine(builder, mstack, 1, 0, 0, 0, 1, 0);
        drawLine(builder, mstack, 0, 1, 0, 1, 1, 1);

        //Cross
        drawLine(builder, mstack, 1, 0, 0, .5, .5, .5);
        drawLine(builder, mstack, .5, .5, .5, 1, 1, 1);
        drawLine(builder, mstack, 0, 1, 0, .5, .5, .5);
    }

    private static void drawLine(IVertexBuilder builder, MatrixStack mstack, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        builder.pos(mstack.getLast().getMatrix(), (float)x1, (float)y1, (float)z1).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builder.pos(mstack.getLast().getMatrix(), (float)x2, (float)y2, (float)z2).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
    }
}