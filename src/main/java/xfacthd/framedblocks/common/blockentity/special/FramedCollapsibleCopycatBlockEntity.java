package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedCollapsibleCopycatBlockEntity extends FramedBlockEntity implements ICollapsibleCopycatBlockEntity
{
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Direction[] HORIZONTAL_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
    private static final int MAX_OFFSET_BEACON_OCCLUSION = 5;
    public static final ModelProperty<Integer> OFFSETS = new ModelProperty<>();

    private int packedOffsets = 0;
    private boolean occludesBeacon = true;

    public FramedCollapsibleCopycatBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK.get(), pos, state);
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
        int offset = getFaceOffset(faceHit);
        if (sneak && offset > 0)
        {
            setFaceOffset(faceHit, offset - 1);
            changed = true;
        }
        else if (!sneak && offset < 15 - getFaceOffset(faceHit.getOpposite()))
        {
            setFaceOffset(faceHit, offset + 1);
            changed = true;
        }
        if (changed)
        {
            updateBeaconOcclusion();
            if (!updateFaceSolidity())
            {
                //noinspection ConstantConditions
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            setChanged();
        }
    }


    private void setFaceOffset(Direction side, int offset)
    {
        int idx = side.ordinal() * 4;
        int mask = 0x0F << idx;
        packedOffsets = (packedOffsets & ~mask) | (offset << idx);
    }

    public int getFaceOffset(Direction side)
    {
        return (byte) (packedOffsets >> (side.ordinal() * 4) & 0xF);
    }

    @Override
    public int getFaceOffset(BlockState state, Direction side)
    {
        return getFaceOffset(side);
    }

    @Override
    public int getPackedOffsets(BlockState state)
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
            //noinspection ConstantConditions
            level.setBlockAndUpdate(worldPosition, state.setValue(PropertyHolder.SOLID_FACES, solid));
            return true;
        }
        return false;
    }

    private void updateBeaconOcclusion()
    {
        occludesBeacon = true;
        for (Direction face : HORIZONTAL_DIRECTIONS)
        {
            if (getFaceOffset(face) > MAX_OFFSET_BEACON_OCCLUSION)
            {
                occludesBeacon = false;
                break;
            }
        }
    }

    @Override
    public ModelData getModelData()
    {
        return ModelData.builder()
                .with(FramedBlockData.PROPERTY, getModelDataInternal())
                .with(OFFSETS, getPackedOffsets(getBlockState()))
                .build();
    }

    @Override
    protected void writeToDataPacket(CompoundTag nbt)
    {
        super.writeToDataPacket(nbt);
        nbt.putInt("offsets", packedOffsets);
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
        updateBeaconOcclusion();
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
