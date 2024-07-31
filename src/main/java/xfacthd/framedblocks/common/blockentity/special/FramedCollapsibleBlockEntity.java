package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentMap;
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
import xfacthd.framedblocks.api.blueprint.AuxBlueprintData;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.component.CollapsibleBlockData;
import xfacthd.framedblocks.common.data.property.NullableDirection;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.Optional;

public class FramedCollapsibleBlockEntity extends FramedBlockEntity implements ICollapsibleBlockEntity
{
    public static final ModelProperty<Integer> OFFSETS = new ModelProperty<>();
    private static final int DIRECTIONS = Direction.values().length;
    private static final int VERTEX_COUNT = 4;
    private static final int BIT_PER_VERTEX = 5;
    private static final int VERTEX_MASK = ~(-1 << BIT_PER_VERTEX);
    private static final NeighborVertex[][] VERTEX_MAPPINGS = makeVertexMapping();

    @Nullable
    private Direction collapsedFace = null;
    private int packedOffsets = 0;

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
        int offset = getVertexOffset(vert);
        if (player.isShiftKeyDown() && collapsedFace != null && offset > 0)
        {
            int target = offset - 1;

            applyDeformation(vert, target, faceHit);
            deformNeighbors(faceHit, vert, target);
        }
        else if (!player.isShiftKeyDown() && offset < 16)
        {
            int target = offset + 1;

            applyDeformation(vert, target, faceHit);
            deformNeighbors(faceHit, vert, target);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void applyDeformation(int vertex, int offset, Direction faceHit)
    {
        offset = Mth.clamp(offset, 0, 16);

        if (offset == getVertexOffset(vertex))
        {
            return;
        }

        setVertexOffset(vertex, offset);

        if (offset == 0)
        {
            boolean noOffsets = true;
            for (int i = 0; i < 4; i++)
            {
                if (getVertexOffset(i) > 0)
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

    private void deformNeighbors(Direction faceHit, int srcVert, int offset)
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

    private void setVertexOffset(int vertex, int offset)
    {
        int idx = vertex * BIT_PER_VERTEX;
        int mask = VERTEX_MASK << idx;
        packedOffsets = (packedOffsets & ~mask) | (offset << idx);
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

    public int getVertexOffset(int vertex)
    {
        return packedOffsets >> (vertex * 5) & 0x1F;
    }

    @Override
    public int getVertexOffset(BlockState state, int vertex)
    {
        return getVertexOffset(vertex);
    }

    @Override
    public int getPackedOffsets(BlockState state)
    {
        return packedOffsets;
    }

    @Override
    protected void attachAdditionalModelData(ModelData.Builder builder)
    {
        builder.with(OFFSETS, packedOffsets);
    }

    @Override
    protected void writeToDataPacket(CompoundTag nbt, HolderLookup.Provider lookupProvider)
    {
        super.writeToDataPacket(nbt, lookupProvider);
        nbt.putInt("offsets", packedOffsets);
        nbt.putByte("face", (byte) (collapsedFace == null ? -1 : collapsedFace.get3DDataValue()));
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt, HolderLookup.Provider lookupProvider)
    {
        boolean needUpdate = super.readFromDataPacket(nbt, lookupProvider);

        int packed = nbt.getInt("offsets");
        if (packed != packedOffsets)
        {
            packedOffsets = packed;

            needUpdate = true;
            updateCulling(true, false);
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
    public CompoundTag getUpdateTag(HolderLookup.Provider provider)
    {
        CompoundTag nbt = super.getUpdateTag(provider);
        nbt.putInt("offsets", packedOffsets);
        nbt.putByte("face", (byte) (collapsedFace == null ? -1 : collapsedFace.get3DDataValue()));
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt, HolderLookup.Provider provider)
    {
        packedOffsets = nbt.getInt("offsets");

        int face = nbt.getByte("face");
        collapsedFace = face == -1 ? null : Direction.from3DDataValue(face);

        super.handleUpdateTag(nbt, provider);
    }

    @Override
    protected Optional<AuxBlueprintData<?>> collectAuxBlueprintData()
    {
        return Optional.of(new CollapsibleBlockData(collapsedFace, packedOffsets));
    }

    @Override
    protected void applyAuxDataFromBlueprint(AuxBlueprintData<?> auxData)
    {
        if (auxData instanceof CollapsibleBlockData blockData)
        {
            collapsedFace = blockData.collapsedFace().toDirection();
            packedOffsets = blockData.offsets();
        }
    }

    @Override
    protected void collectMiscComponents(DataComponentMap.Builder builder)
    {
        builder.set(FBContent.DC_TYPE_COLLAPSIBLE_BLOCK_DATA, new CollapsibleBlockData(collapsedFace, packedOffsets));
    }

    @Override
    protected void applyMiscComponents(DataComponentInput input)
    {
        CollapsibleBlockData blockData = input.get(FBContent.DC_TYPE_COLLAPSIBLE_BLOCK_DATA);
        if (blockData != null)
        {
            collapsedFace = blockData.collapsedFace().toDirection();
            packedOffsets = blockData.offsets();
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.saveAdditional(nbt, provider);
        nbt.putInt("offsets", packedOffsets);
        nbt.putInt("face", collapsedFace == null ? -1 : collapsedFace.get3DDataValue());
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        packedOffsets = nbt.getInt("offsets");
        int face = nbt.getInt("face");
        collapsedFace = face == -1 ? null : Direction.from3DDataValue(face);
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
