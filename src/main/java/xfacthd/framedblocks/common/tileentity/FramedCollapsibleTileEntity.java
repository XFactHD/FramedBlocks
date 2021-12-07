package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.Constants;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.CollapseFace;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.MathUtils;
import xfacthd.framedblocks.common.util.Utils;

import java.util.Arrays;

public class FramedCollapsibleTileEntity extends FramedTileEntity
{
    public static final ModelProperty<Integer> OFFSETS = new ModelProperty<>();
    public static final ModelProperty<Direction> COLLAPSED_FACE = new ModelProperty<>();

    private Direction collapsedFace = null;
    private byte[] vertexOffsets = new byte[4];

    public FramedCollapsibleTileEntity() { super(FBContent.blockEntityTypeFramedCollapsibleBlock.get()); }

    public void handleDeform(PlayerEntity player)
    {
        RayTraceResult hit = player.pick(10D, 0, false);
        if (!(hit instanceof BlockRayTraceResult)) { return; }

        BlockRayTraceResult blockHit = (BlockRayTraceResult) hit;
        Direction faceHit = blockHit.getFace();
        Vector3d hitLoc = Utils.fraction(hit.getHitVec());

        if (collapsedFace != null && faceHit != collapsedFace) { return; }

        int vert = vertexFromHit(faceHit, hitLoc);
        if (player.isSneaking() && collapsedFace != null && vertexOffsets[vert] > 0)
        {
            byte target = (byte) (vertexOffsets[vert] - 1);

            applyDeformation(vert, target, faceHit);
            deformNeighbors(faceHit, hitLoc, target);
        }
        else if (!player.isSneaking() && vertexOffsets[vert] < 16)
        {
            byte target = (byte) (vertexOffsets[vert] + 1);

            applyDeformation(vert, target, faceHit);
            deformNeighbors(faceHit, hitLoc, target);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void applyDeformation(int vertex, byte offset, Direction faceHit)
    {
        offset = (byte) MathHelper.clamp(offset, (byte) 0, (byte) 16);

        if (offset == vertexOffsets[vertex]) { return; }

        vertexOffsets[vertex] = offset;

        if (offset == 0)
        {
            boolean noOffsets = true;
            for (int i = 0; i < 4; i++)
            {
                if (vertexOffsets[i] > 0)
                {
                    noOffsets = false;
                    break;
                }
            }

            if (noOffsets)
            {
                collapsedFace = null;
                world.setBlockState(pos, getBlockState().with(PropertyHolder.COLLAPSED_FACE, CollapseFace.NONE), Constants.BlockFlags.DEFAULT);
            }
            else
            {
                world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
            }
        }
        else if (collapsedFace == null)
        {
            collapsedFace = faceHit;
            world.setBlockState(pos, getBlockState().with(PropertyHolder.COLLAPSED_FACE, CollapseFace.fromDirection(collapsedFace)), Constants.BlockFlags.DEFAULT);
        }
        else
        {
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
        }

        markDirty();
    }

    private void deformNeighbors(Direction faceHit, Vector3d hitLoc, byte offset)
    {
        BlockPos[] neighbors = new BlockPos[3];
        Vector3d[] hitVecs = new Vector3d[3];
        if (faceHit.getAxis() == Direction.Axis.Y)
        {
            Direction dirX = hitLoc.x > .5 ? Direction.EAST : Direction.WEST;
            Direction dirZ = hitLoc.z > .5 ? Direction.SOUTH : Direction.NORTH;

            neighbors[0] = pos.offset(dirX);
            neighbors[1] = pos.offset(dirZ);
            neighbors[2] = neighbors[0].offset(dirZ);

            hitVecs[0] = hitLoc.add(dirX.getXOffset() * .5, 0, 0);
            hitVecs[1] = hitLoc.add(0, 0, dirZ.getZOffset() * .5);
            hitVecs[2] = hitVecs[0].add(0, 0, dirZ.getZOffset() * .5);
        }
        else
        {
            Direction dirY = hitLoc.y > .5 ? Direction.UP : Direction.DOWN;
            Direction dirXZ;

            if (faceHit.getAxis() == Direction.Axis.X)
            {
                dirXZ = hitLoc.z > .5 ? Direction.SOUTH : Direction.NORTH;
            }
            else
            {
                dirXZ = hitLoc.x > .5 ? Direction.EAST : Direction.WEST;
            }

            neighbors[0] = pos.offset(dirY);
            neighbors[1] = pos.offset(dirXZ);
            neighbors[2] = neighbors[0].offset(dirXZ);

            hitVecs[0] = hitLoc.add(0, dirY.getYOffset() * .5, 0);
            hitVecs[1] = hitLoc.add(dirXZ.getXOffset() * .5, 0, dirXZ.getZOffset() * .5);
            hitVecs[2] = hitVecs[0].add(dirXZ.getXOffset() * .5, 0, dirXZ.getZOffset() * .5);
        }

        for (int i = 0; i < 3; i++)
        {
            //noinspection ConstantConditions
            TileEntity te = world.getTileEntity(neighbors[i]);
            if (te instanceof FramedCollapsibleTileEntity)
            {
                int vert = vertexFromHit(faceHit, MathUtils.wrapVector(hitVecs[i], 0, 1));
                ((FramedCollapsibleTileEntity) te).applyDeformation(vert, offset, faceHit);
            }
        }
    }

    private static int vertexFromHit(Direction faceHit, Vector3d loc)
    {
        if (faceHit.getAxis() == Direction.Axis.Y)
        {
            if ((loc.z < .5F) == (faceHit == Direction.UP))
            {
                return loc.x < .5F ? 0 : 3;
            }
            else
            {
                return loc.x < .5F ? 1 : 2;
            }
        }
        else
        {
            boolean positive = faceHit == Direction.SOUTH || faceHit == Direction.WEST;
            double xz = faceHit.getAxis() == Direction.Axis.X ? loc.z : loc.x;
            if (loc.y < .5F)
            {
                return (xz < .5F) == positive ? 1 : 2;
            }
            else
            {
                return (xz < .5F) == positive ? 0 : 3;
            }
        }
    }

    public byte[] getVertexOffsets() { return vertexOffsets; }

    public int getPackedOffsets() { return packOffsets(vertexOffsets); }

    @Override
    protected void writeToDataPacket(CompoundNBT nbt)
    {
        super.writeToDataPacket(nbt);
        nbt.putInt("offsets", packOffsets(vertexOffsets));
        nbt.putInt("face", collapsedFace == null ? -1 : collapsedFace.getIndex());
    }

    @Override
    protected boolean readFromDataPacket(CompoundNBT nbt)
    {
        boolean needUpdate = super.readFromDataPacket(nbt);

        int packed = nbt.getInt("offsets");
        byte[] offsets = unpackOffsets(packed);
        if (!Arrays.equals(offsets, vertexOffsets))
        {
            vertexOffsets = offsets;
            getModelData().setData(OFFSETS, packed);

            needUpdate = true;
        }

        int faceIdx = nbt.getInt("face");
        Direction face = faceIdx == -1 ? null : Direction.byIndex(faceIdx);
        if (collapsedFace != face)
        {
            collapsedFace = face;
            getModelData().setData(COLLAPSED_FACE, collapsedFace);

            needUpdate = true;
        }

        return needUpdate;
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        CompoundNBT nbt = super.getUpdateTag();
        nbt.putInt("offsets", packOffsets(vertexOffsets));
        nbt.putInt("face", collapsedFace == null ? -1 : collapsedFace.getIndex());
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt)
    {
        super.handleUpdateTag(state, nbt);

        int packed = nbt.getInt("offsets");
        vertexOffsets = unpackOffsets(packed);
        getModelData().setData(OFFSETS, packed);

        int face = nbt.getInt("face");
        collapsedFace = face == -1 ? null : Direction.byIndex(face);
        getModelData().setData(COLLAPSED_FACE, collapsedFace);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("offsets", packOffsets(vertexOffsets));
        nbt.putInt("face", collapsedFace == null ? -1 : collapsedFace.getIndex());
        return super.write(nbt);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        vertexOffsets = unpackOffsets(nbt.getInt("offsets"));
        int face = nbt.getInt("face");
        collapsedFace = face == -1 ? null : Direction.byIndex(face);
    }



    public static int packOffsets(byte[] offsets)
    {
        int result = 0;

        for (int i = 0; i < 4; i++)
        {
            result |= (offsets[i] << (i * 5));
        }

        return result;
    }

    public static byte[] unpackOffsets(int packed)
    {
        byte[] offsets = new byte[4];

        for (int i = 0; i < 4; i++)
        {
            offsets[i] = (byte) (packed >> (i * 5) & 0x1F);
        }

        return offsets;
    }
}