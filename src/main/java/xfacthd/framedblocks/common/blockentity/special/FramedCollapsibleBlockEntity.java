package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.property.NullableDirection;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedCollapsibleBlockEntity extends FramedBlockEntity
{
    public static final ModelProperty<Integer> OFFSETS = new ModelProperty<>();
    private static final int DIRECTIONS = Direction.values().length;
    private static final int VERTEX_COUNT = 4;
    private static final NeighborVertex[][] VERTEX_MAPPINGS = makeVertexMapping();

    @Nullable
    private Direction collapsedFace = null;
    private int packedOffsets = 0;
    private byte[] vertexOffsets = new byte[4];

    public FramedCollapsibleBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_COLLAPSIBLE_BLOCK.value(), pos, state);
    }

    public void handleDeform(Player player)
    {
        HitResult hit = player.pick(10D, 1F, false);
        if (!(hit instanceof BlockHitResult blockHit))
        {
            return;
        }

        Direction faceHit = blockHit.getDirection();
        Vec3 hitLoc = Utils.fraction(hit.getLocation());

        if (collapsedFace != null && faceHit != collapsedFace)
        {
            return;
        }

        int vert = vertexFromHit(faceHit, hitLoc);
        if (vert == 4)
        {
            for (int i = 0; i < 4; i++)
            {
                handleDeformOfVertex(player, faceHit, i);
            }
        }
        else
        {
            handleDeformOfVertex(player, faceHit, vert);
        }
    }

    private void handleDeformOfVertex(Player player, Direction faceHit, int vert)
    {
        if (player.isShiftKeyDown() && collapsedFace != null && vertexOffsets[vert] > 0)
        {
            byte target = (byte) (vertexOffsets[vert] - 1);

            applyDeformation(vert, target, faceHit);
            deformNeighbors(faceHit, vert, target);
        }
        else if (!player.isShiftKeyDown() && vertexOffsets[vert] < 16)
        {
            byte target = (byte) (vertexOffsets[vert] + 1);

            applyDeformation(vert, target, faceHit);
            deformNeighbors(faceHit, vert, target);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void applyDeformation(int vertex, byte offset, Direction faceHit)
    {
        offset = (byte) Mth.clamp(offset, (byte) 0, (byte) 16);

        if (offset == vertexOffsets[vertex])
        {
            return;
        }

        vertexOffsets[vertex] = offset;
        packedOffsets = packOffsets(vertexOffsets);

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
                level().setBlock(worldPosition, getBlockState().setValue(PropertyHolder.NULLABLE_FACE, NullableDirection.NONE), Block.UPDATE_ALL);
            }
            else
            {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
        else if (collapsedFace == null)
        {
            collapsedFace = faceHit;
            level().setBlock(worldPosition, getBlockState().setValue(PropertyHolder.NULLABLE_FACE, NullableDirection.fromDirection(collapsedFace)), Block.UPDATE_ALL);
        }
        else
        {
            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }

        setChangedWithoutSignalUpdate();
    }

    private void deformNeighbors(Direction faceHit, int srcVert, byte offset)
    {
        NeighborVertex[] verts = VERTEX_MAPPINGS[getMappingIndex(faceHit, srcVert)];
        for (int i = 0; i < 3; i++)
        {
            NeighborVertex vert = verts[i];
            BlockPos pos = worldPosition.offset(vert.offset);
            if (level().getBlockEntity(pos) instanceof FramedCollapsibleBlockEntity be)
            {
                if (be.collapsedFace == null || be.collapsedFace == faceHit)
                {
                    be.applyDeformation(vert.targetVert, offset, faceHit);
                }
            }
        }
    }

    public static int vertexFromHit(Direction faceHit, Vec3 loc)
    {
        if (Utils.isY(faceHit))
        {
            double ax = Math.abs((loc.x - .5) * 4D);
            double az = Math.abs((loc.z - .5) * 4D);
            if (ax >= 0D && ax <= 1D && az >= 0D && az <= 1D && az <= (1D - ax))
            {
                return 4;
            }

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
            double xz = Utils.isX(faceHit) ? loc.z : loc.x;
            double axz = Math.abs((xz - .5) * 4D);
            double ay = Math.abs((loc.y - .5D) * 4D);
            if (axz >= 0D && axz <= 1D && ay >= 0D && ay <= 1D && ay <= (1D - axz))
            {
                return 4;
            }

            boolean positive = faceHit == Direction.SOUTH || faceHit == Direction.WEST;
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

    @Nullable
    public Direction getCollapsedFace()
    {
        return collapsedFace;
    }

    public byte[] getVertexOffsets()
    {
        return vertexOffsets;
    }

    public int getPackedOffsets()
    {
        return packedOffsets;
    }

    @Override
    protected void attachAdditionalModelData(ModelData.Builder builder)
    {
        builder.with(OFFSETS, getPackedOffsets());
    }

    @Override
    protected void writeToDataPacket(CompoundTag nbt)
    {
        super.writeToDataPacket(nbt);
        nbt.putInt("offsets", packOffsets(vertexOffsets));
        nbt.putByte("face", (byte) (collapsedFace == null ? -1 : collapsedFace.get3DDataValue()));
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        boolean needUpdate = super.readFromDataPacket(nbt);

        int packed = nbt.getInt("offsets");
        if (packed != packedOffsets)
        {
            packedOffsets = packed;
            vertexOffsets = unpackOffsets(packedOffsets);

            needUpdate = true;
        }

        int faceIdx = nbt.getByte("face");
        Direction face = faceIdx == -1 ? null : Direction.from3DDataValue(faceIdx);
        if (collapsedFace != face)
        {
            collapsedFace = face;

            needUpdate = true;
        }

        return needUpdate;
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag nbt = super.getUpdateTag();
        nbt.putInt("offsets", packedOffsets);
        nbt.putByte("face", (byte) (collapsedFace == null ? -1 : collapsedFace.get3DDataValue()));
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt)
    {
        packedOffsets = nbt.getInt("offsets");
        vertexOffsets = unpackOffsets(packedOffsets);

        int face = nbt.getByte("face");
        collapsedFace = face == -1 ? null : Direction.from3DDataValue(face);

        super.handleUpdateTag(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putInt("offsets", packedOffsets);
        nbt.putInt("face", collapsedFace == null ? -1 : collapsedFace.get3DDataValue());
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        packedOffsets = nbt.getInt("offsets");
        vertexOffsets = unpackOffsets(packedOffsets);
        int face = nbt.getInt("face");
        collapsedFace = face == -1 ? null : Direction.from3DDataValue(face);
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

    private static int getMappingIndex(Direction face, int srcVert)
    {
        return face.ordinal() * VERTEX_COUNT + srcVert;
    }

    private static NeighborVertex[][] makeVertexMapping()
    {
        NeighborVertex[][] mappings = new NeighborVertex[DIRECTIONS * VERTEX_COUNT][];

        putMapping(mappings, Direction.UP, 0, new NeighborVertex(new Vec3i(-1, 0,  0), 3), new NeighborVertex(new Vec3i( 0, 0, -1), 1), new NeighborVertex(new Vec3i(-1, 0, -1), 2));
        putMapping(mappings, Direction.UP, 1, new NeighborVertex(new Vec3i(-1, 0,  0), 2), new NeighborVertex(new Vec3i( 0, 0,  1), 0), new NeighborVertex(new Vec3i(-1, 0,  1), 3));
        putMapping(mappings, Direction.UP, 2, new NeighborVertex(new Vec3i( 1, 0,  0), 1), new NeighborVertex(new Vec3i( 0, 0,  1), 3), new NeighborVertex(new Vec3i( 1, 0,  1), 0));
        putMapping(mappings, Direction.UP, 3, new NeighborVertex(new Vec3i( 1, 0,  0), 0), new NeighborVertex(new Vec3i( 0, 0, -1), 2), new NeighborVertex(new Vec3i( 1, 0, -1), 1));

        putMapping(mappings, Direction.DOWN, 0, new NeighborVertex(new Vec3i(-1, 0,  0), 3), new NeighborVertex(new Vec3i( 0, 0,  1), 1), new NeighborVertex(new Vec3i(-1, 0,  1), 2));
        putMapping(mappings, Direction.DOWN, 1, new NeighborVertex(new Vec3i(-1, 0,  0), 2), new NeighborVertex(new Vec3i( 0, 0, -1), 0), new NeighborVertex(new Vec3i(-1, 0, -1), 3));
        putMapping(mappings, Direction.DOWN, 2, new NeighborVertex(new Vec3i( 1, 0,  0), 1), new NeighborVertex(new Vec3i( 0, 0, -1), 3), new NeighborVertex(new Vec3i( 1, 0, -1), 0));
        putMapping(mappings, Direction.DOWN, 3, new NeighborVertex(new Vec3i( 1, 0,  0), 0), new NeighborVertex(new Vec3i( 0, 0,  1), 2), new NeighborVertex(new Vec3i( 1, 0,  1), 1));

        putMapping(mappings, Direction.NORTH, 0, new NeighborVertex(new Vec3i( 1,  0, 0), 3), new NeighborVertex(new Vec3i( 0,  1, 0), 1), new NeighborVertex(new Vec3i( 1,  1, 0), 2));
        putMapping(mappings, Direction.NORTH, 1, new NeighborVertex(new Vec3i( 1,  0, 0), 2), new NeighborVertex(new Vec3i( 0, -1, 0), 0), new NeighborVertex(new Vec3i( 1, -1, 0), 3));
        putMapping(mappings, Direction.NORTH, 2, new NeighborVertex(new Vec3i(-1,  0, 0), 1), new NeighborVertex(new Vec3i( 0, -1, 0), 3), new NeighborVertex(new Vec3i(-1, -1, 0), 0));
        putMapping(mappings, Direction.NORTH, 3, new NeighborVertex(new Vec3i(-1,  0, 0), 0), new NeighborVertex(new Vec3i( 0,  1, 0), 2), new NeighborVertex(new Vec3i(-1,  1, 0), 1));

        putMapping(mappings, Direction.SOUTH, 0, new NeighborVertex(new Vec3i(-1,  0, 0), 3), new NeighborVertex(new Vec3i( 0,  1, 0), 1), new NeighborVertex(new Vec3i(-1,  1, 0), 2));
        putMapping(mappings, Direction.SOUTH, 1, new NeighborVertex(new Vec3i(-1,  0, 0), 2), new NeighborVertex(new Vec3i( 0, -1, 0), 0), new NeighborVertex(new Vec3i(-1, -1, 0), 3));
        putMapping(mappings, Direction.SOUTH, 2, new NeighborVertex(new Vec3i( 1,  0, 0), 1), new NeighborVertex(new Vec3i( 0, -1, 0), 3), new NeighborVertex(new Vec3i( 1, -1, 0), 0));
        putMapping(mappings, Direction.SOUTH, 3, new NeighborVertex(new Vec3i( 1,  0, 0), 0), new NeighborVertex(new Vec3i( 0,  1, 0), 2), new NeighborVertex(new Vec3i( 1,  1, 0), 1));

        putMapping(mappings, Direction.EAST, 0, new NeighborVertex(new Vec3i(0,  0,  1), 3), new NeighborVertex(new Vec3i( 0,  1, 0), 1), new NeighborVertex(new Vec3i(0,  1,  1), 2));
        putMapping(mappings, Direction.EAST, 1, new NeighborVertex(new Vec3i(0,  0,  1), 2), new NeighborVertex(new Vec3i( 0, -1, 0), 0), new NeighborVertex(new Vec3i(0, -1,  1), 3));
        putMapping(mappings, Direction.EAST, 2, new NeighborVertex(new Vec3i(0,  0, -1), 1), new NeighborVertex(new Vec3i( 0, -1, 0), 3), new NeighborVertex(new Vec3i(0, -1, -1), 0));
        putMapping(mappings, Direction.EAST, 3, new NeighborVertex(new Vec3i(0,  0, -1), 0), new NeighborVertex(new Vec3i( 0,  1, 0), 2), new NeighborVertex(new Vec3i(0,  1, -1), 1));

        putMapping(mappings, Direction.WEST, 0, new NeighborVertex(new Vec3i(0,  0, -1), 3), new NeighborVertex(new Vec3i( 0,  1, 0), 1), new NeighborVertex(new Vec3i(0,  1, -1), 2));
        putMapping(mappings, Direction.WEST, 1, new NeighborVertex(new Vec3i(0,  0, -1), 2), new NeighborVertex(new Vec3i( 0, -1, 0), 0), new NeighborVertex(new Vec3i(0, -1, -1), 3));
        putMapping(mappings, Direction.WEST, 2, new NeighborVertex(new Vec3i(0,  0,  1), 1), new NeighborVertex(new Vec3i( 0, -1, 0), 3), new NeighborVertex(new Vec3i(0, -1,  1), 0));
        putMapping(mappings, Direction.WEST, 3, new NeighborVertex(new Vec3i(0,  0,  1), 0), new NeighborVertex(new Vec3i( 0,  1, 0), 2), new NeighborVertex(new Vec3i(0,  1,  1), 1));

        return mappings;
    }

    private static void putMapping(
            NeighborVertex[][] mappings,
            Direction face,
            int srcVert,
            NeighborVertex vertOne,
            NeighborVertex vertTwo,
            NeighborVertex vertBoth
    )
    {
        mappings[getMappingIndex(face, srcVert)] = new NeighborVertex[] { vertOne, vertTwo, vertBoth };
    }

    private record NeighborVertex(Vec3i offset, int targetVert) { }
}