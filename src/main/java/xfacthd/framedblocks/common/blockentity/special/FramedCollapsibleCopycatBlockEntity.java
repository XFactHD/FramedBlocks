package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedCollapsibleCopycatBlockEntity extends FramedBlockEntity
{
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Direction[] HORIZONTAL_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
    private static final int MAX_OFFSET_BEACON_OCCLUSION = 5;
    public static final ModelProperty<Integer> OFFSETS = new ModelProperty<>();

    private int packedOffsets = 0;
    private byte[] faceOffsets = new byte[DIRECTIONS.length];
    private boolean occludesBeacon = true;

    public FramedCollapsibleCopycatBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK.value(), pos, state);
    }

    public void handleDeform(Player player)
    {
        HitResult hit = player.pick(10D, 1F, false);
        if (!(hit instanceof BlockHitResult blockHit))
        {
            return;
        }

        Direction faceHit = blockHit.getDirection();
        boolean sneak = player.isShiftKeyDown();
        boolean changed = false;
        if (sneak && faceOffsets[faceHit.ordinal()] > 0)
        {
            faceOffsets[faceHit.ordinal()]--;
            changed = true;
        }
        else if (!sneak && faceOffsets[faceHit.ordinal()] < 15 - faceOffsets[faceHit.getOpposite().ordinal()])
        {
            faceOffsets[faceHit.ordinal()]++;
            changed = true;
        }
        if (changed)
        {
            packedOffsets = packOffsets(faceOffsets);
            updateBeaconOcclusion();
            if (!updateFaceSolidity())
            {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            setChanged();
        }
    }

    public byte[] getFaceOffsets()
    {
        return faceOffsets;
    }

    public int getPackedOffsets()
    {
        return packedOffsets;
    }

    public boolean doesOccludeBeaconBeam()
    {
        return occludesBeacon;
    }

    public boolean updateFaceSolidity()
    {
        BlockState state = getBlockState();
        int solid = computeSolidFaces(packedOffsets);
        if (state.getValue(PropertyHolder.SOLID_FACES) != solid)
        {
            level().setBlockAndUpdate(worldPosition, state.setValue(PropertyHolder.SOLID_FACES, solid));
            return true;
        }
        return false;
    }

    private void updateBeaconOcclusion()
    {
        occludesBeacon = true;
        for (Direction face : HORIZONTAL_DIRECTIONS)
        {
            if (faceOffsets[face.ordinal()] > MAX_OFFSET_BEACON_OCCLUSION)
            {
                occludesBeacon = false;
                break;
            }
        }
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
        nbt.putInt("offsets", packOffsets(faceOffsets));
        nbt.putBoolean("occludesBeacon", occludesBeacon);
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        boolean needUpdate = super.readFromDataPacket(nbt);

        int packed = nbt.getInt("offsets");
        if (packed != packedOffsets)
        {
            packedOffsets = packed;
            faceOffsets = unpackOffsets(packedOffsets);

            needUpdate = true;
            updateCulling(true, false);
        }

        occludesBeacon = nbt.getBoolean("occludesBeacon");

        return needUpdate;
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag nbt = super.getUpdateTag();
        nbt.putInt("offsets", packedOffsets);
        nbt.putBoolean("occludesBeacon", occludesBeacon);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt)
    {
        packedOffsets = nbt.getInt("offsets");
        faceOffsets = unpackOffsets(packedOffsets);
        occludesBeacon = nbt.getBoolean("occludesBeacon");

        super.handleUpdateTag(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putInt("offsets", packedOffsets);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        packedOffsets = nbt.getInt("offsets");
        faceOffsets = unpackOffsets(packedOffsets);
        updateBeaconOcclusion();
    }



    public static int packOffsets(byte[] offsets)
    {
        int result = 0;

        for (int i = 0; i < DIRECTIONS.length; i++)
        {
            result |= (offsets[i] << (i * 4));
        }

        return result;
    }

    public static byte[] unpackOffsets(int packed)
    {
        byte[] offsets = new byte[DIRECTIONS.length];

        for (int i = 0; i < DIRECTIONS.length; i++)
        {
            offsets[i] = (byte) (packed >> (i * 4) & 0xF);
        }

        return offsets;
    }

    public static int computeSolidFaces(int packedOffsets)
    {
        int solid = 0;
        for (Direction face : DIRECTIONS)
        {
            if (((packedOffsets >> (face.ordinal() * 4)) & 0xF) == 0)
            {
                solid |= (1 << face.ordinal());
            }
        }
        return solid;
    }
}
