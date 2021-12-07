package xfacthd.framedblocks.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.tileentity.FramedCollapsibleTileEntity;
import xfacthd.framedblocks.common.util.Utils;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, value = Dist.CLIENT)
public class ClientEventHandler
{
    private static final Quaternion ROT_180 = Vector3f.YN.rotationDegrees(180);

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

            mstack.push();
            mstack.translate(offset.x, offset.y, offset.z);
            mstack.translate(.5, .5, .5);
            if (type != BlockType.FRAMED_COLLAPSIBLE_BLOCK)
            {
                Direction dir = Utils.getBlockFacing(state);
                mstack.rotate(Vector3f.YP.rotationDegrees(-dir.getHorizontalAngle()));
            }
            else
            {
                mstack.rotate(ROT_180);
            }
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
                case FRAMED_COLLAPSIBLE_BLOCK:
                    drawCollapsibleBlockBox(result.getPos(), state, mstack, builder);
                    break;
            }

            mstack.pop();

            event.setCanceled(true);
        }
    }

    private static void drawSlopeBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        SlopeType type = Utils.getSlopeType(state);

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

    public static void drawCollapsibleBlockBox(BlockPos pos, BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        CollapseFace face = state.get(PropertyHolder.COLLAPSED_FACE);
        if (face == CollapseFace.NONE)
        {
            VoxelShapes.fullCube().forEachEdge((pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ) -> drawLine(builder, mstack, pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ));
        }
        else
        {
            //noinspection ConstantConditions
            TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
            if (!(te instanceof FramedCollapsibleTileEntity)) { return; }

            byte[] offets = ((FramedCollapsibleTileEntity) te).getVertexOffsets();
            Direction faceDir = face.toDirection().getOpposite();

            mstack.push();
            mstack.translate(.5, .5, .5);
            if (faceDir == Direction.UP)
            {
                mstack.rotate(Vector3f.XP.rotationDegrees(180));
            }
            else if (faceDir != Direction.DOWN)
            {
                mstack.rotate(Vector3f.YN.rotationDegrees(faceDir.getHorizontalAngle() + 180F));
                mstack.rotate(Vector3f.XN.rotationDegrees(90));
            }
            mstack.translate(-.5, -.5, -.5);

            //Top
            drawLine(builder, mstack, 0, 1D - (offets[2] / 16D), 0, 0, 1D - (offets[3] / 16D), 1);
            drawLine(builder, mstack, 0, 1D - (offets[2] / 16D), 0, 1, 1D - (offets[1] / 16D), 0);
            drawLine(builder, mstack, 1, 1D - (offets[1] / 16D), 0, 1, 1D - (offets[0] / 16D), 1);
            drawLine(builder, mstack, 0, 1D - (offets[3] / 16D), 1, 1, 1D - (offets[0] / 16D), 1);

            //Bottom
            drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
            drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);
            drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
            drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);

            //Vertical
            drawLine(builder, mstack, 1, 0, 1, 1, 1D - (offets[0] / 16D), 1);
            drawLine(builder, mstack, 1, 0, 0, 1, 1D - (offets[1] / 16D), 0);
            drawLine(builder, mstack, 0, 0, 0, 0, 1D - (offets[2] / 16D), 0);
            drawLine(builder, mstack, 0, 0, 1, 0, 1D - (offets[3] / 16D), 1);

            mstack.pop();
        }
    }

    private static void drawLine(IVertexBuilder builder, MatrixStack mstack, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        builder.pos(mstack.getLast().getMatrix(), (float)x1, (float)y1, (float)z1).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builder.pos(mstack.getLast().getMatrix(), (float)x2, (float)y2, (float)z2).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
    }
}