package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import xfacthd.framedblocks.common.data.CollapseFace;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedCollapsibleTileEntity;

public class CollapsibleBlockOutlineRenderer implements OutlineRender
{
    private static final Quaternion ROTATION = Vector3f.YN.rotationDegrees(180);

    @Override
    public void rotateMatrix(MatrixStack mstack, BlockState state) { mstack.rotate(ROTATION); }

    @Override
    public void draw(BlockState state, World world, BlockPos pos, MatrixStack mstack, IVertexBuilder builder)
    {
        CollapseFace face = state.get(PropertyHolder.COLLAPSED_FACE);
        if (face == CollapseFace.NONE)
        {
            VoxelShapes.fullCube().forEachEdge((pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ) -> OutlineRender.drawLine(builder, mstack, pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ));
        }
        else
        {
            TileEntity te = world.getTileEntity(pos);
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
            OutlineRender.drawLine(builder, mstack, 0, 1D - (offets[2] / 16D), 0, 0, 1D - (offets[3] / 16D), 1);
            OutlineRender.drawLine(builder, mstack, 0, 1D - (offets[2] / 16D), 0, 1, 1D - (offets[1] / 16D), 0);
            OutlineRender.drawLine(builder, mstack, 1, 1D - (offets[1] / 16D), 0, 1, 1D - (offets[0] / 16D), 1);
            OutlineRender.drawLine(builder, mstack, 0, 1D - (offets[3] / 16D), 1, 1, 1D - (offets[0] / 16D), 1);

            //Bottom
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);

            //Vertical
            OutlineRender.drawLine(builder, mstack, 1, 0, 1, 1, 1D - (offets[0] / 16D), 1);
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 1D - (offets[1] / 16D), 0);
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 1D - (offets[2] / 16D), 0);
            OutlineRender.drawLine(builder, mstack, 0, 0, 1, 0, 1D - (offets[3] / 16D), 1);

            mstack.pop();
        }
    }

    @Override
    public void draw(BlockState state, MatrixStack poseStack, IVertexBuilder builder)
    {
        throw new UnsupportedOperationException();
    }
}
