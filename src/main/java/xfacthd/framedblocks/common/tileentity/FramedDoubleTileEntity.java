package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraftforge.client.model.data.*;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.FramedBlockData;
import xfacthd.framedblocks.common.util.DoubleBlockSoundType;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

import java.util.List;

public abstract class FramedDoubleTileEntity extends FramedTileEntity
{
    public static final ModelProperty<IModelData> DATA_LEFT = new ModelProperty<>();
    public static final ModelProperty<IModelData> DATA_RIGHT = new ModelProperty<>();

    private final IModelData multiModelData = new ModelDataMap.Builder().build();
    private final FramedBlockData modelData = new FramedBlockData(true);
    private final DoubleBlockSoundType soundType = new DoubleBlockSoundType(this);
    private ItemStack camoStack = ItemStack.EMPTY;
    private BlockState camoState = Blocks.AIR.getDefaultState();

    public FramedDoubleTileEntity(TileEntityType<?> type) { super(type); }

    @Override
    public void setCamo(ItemStack camoStack, BlockState camoState, boolean secondary)
    {
        if (secondary)
        {
            int light = getLightValue();

            this.camoStack = camoStack;
            this.camoState = camoState;

            markDirty();
            if (getLightValue() != light)
            {
                doLightUpdate();
            }
            //noinspection ConstantConditions
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
        }
        else
        {
            super.setCamo(camoStack, camoState, false);
        }
    }

    @Override
    protected void applyCamo(ItemStack camoStack, BlockState camoState, BlockRayTraceResult hit)
    {
        if (hitSecondary(hit))
        {
            this.camoStack = camoStack;
            this.camoState = camoState;
        }
        else
        {
            super.applyCamo(camoStack, camoState, hit);
        }
    }

    public BlockState getCamoStateTwo() { return camoState; }

    public ItemStack getCamoStackTwo() { return camoStack; }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightValue() { return Math.max(camoState.getLightValue(), super.getLightValue()); }

    @Override
    public void addCamoDrops(List<ItemStack> drops)
    {
        super.addCamoDrops(drops);
        if (!camoStack.isEmpty())
        {
            drops.add(camoStack);
        }
    }

    @Override
    protected BlockState getCamoState(BlockRayTraceResult hit)
    {
        return hitSecondary(hit) ? getCamoStateTwo() : getCamoState();
    }

    @Override
    protected ItemStack getCamoStack(BlockRayTraceResult hit)
    {
        return hitSecondary(hit) ? getCamoStackTwo() : getCamoStack();
    }

    @Override
    public float getCamoBlastResistance(Explosion explosion)
    {
        return Math.max(
                getCamoState().getExplosionResistance(world, pos, explosion),
                getCamoStateTwo().getExplosionResistance(world, pos, explosion)
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isCamoFlammable(Direction face)
    {
        BlockState camo = getCamoState(face);
        if (camo.isAir() && (!getCamoState().isAir() || !getCamoStateTwo().isAir()))
        {
            return (getCamoState().isAir() || getCamoState().isFlammable(world, pos, face)) &&
                   (getCamoStateTwo().isAir() || getCamoStateTwo().isFlammable(world, pos, face));
        }
        else if (!camo.isAir())
        {
            return camo.isFlammable(world, pos, face);
        }
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getCamoFlammability(Direction face)
    {
        int flammabilityOne = super.getCamoFlammability(face);
        int flammabilityTwo = getCamoStateTwo().isAir() ? -1 : getCamoStateTwo().getFlammability(world, pos, face);

        if (flammabilityOne == -1) { return flammabilityTwo; }
        if (flammabilityTwo == -1) { return flammabilityOne; }
        return Math.min(flammabilityOne, flammabilityTwo);
    }

    public final SoundType getSoundType() { return soundType; }

    protected abstract boolean hitSecondary(BlockRayTraceResult hit);

    public abstract DoubleSoundMode getSoundMode();

    /*
     * Sync
     */

    @Override
    protected void writeToDataPacket(CompoundNBT nbt)
    {
        super.writeToDataPacket(nbt);

        nbt.put("camo_stack_two", camoStack.write(new CompoundNBT()));
        nbt.put("camo_state_two", NBTUtil.writeBlockState(camoState));
    }

    @Override
    protected boolean readFromDataPacket(CompoundNBT nbt)
    {
        camoStack = ItemStack.read(nbt.getCompound("camo_stack_two"));

        boolean needUpdate = false;
        BlockState newState = NBTUtil.readBlockState(nbt.getCompound("camo_state_two"));
        if (newState != camoState)
        {
            camoState = newState;

            modelData.setWorld(world);
            modelData.setPos(pos);
            modelData.setCamoState(camoState);

            needUpdate = true;
        }

        return super.readFromDataPacket(nbt) || needUpdate;
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        CompoundNBT nbt = super.getUpdateTag();

        nbt.put("camo_stack_two", camoStack.write(new CompoundNBT()));
        nbt.put("camo_state_two", NBTUtil.writeBlockState(camoState));

        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt)
    {
        super.handleUpdateTag(state, nbt);

        camoStack = ItemStack.read(nbt.getCompound("camo_stack_two"));

        BlockState newState = NBTUtil.readBlockState(nbt.getCompound("camo_state_two"));
        if (newState != camoState)
        {
            camoState = newState;

            modelData.setWorld(world);
            modelData.setPos(pos);
            modelData.setCamoState(camoState);
        }
    }

    /*
     * Model data
     */

    @Override
    public IModelData getModelData()
    {
        multiModelData.setData(DATA_LEFT, super.getModelData());
        multiModelData.setData(DATA_RIGHT, modelData);
        return multiModelData;
    }

    /*
     * NBT stuff
     */

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.put("camo_stack_two", camoStack.write(new CompoundNBT()));
        nbt.put("camo_state_two", NBTUtil.writeBlockState(camoState));

        return super.write(nbt);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);

        BlockState camoState = NBTUtil.readBlockState(nbt.getCompound("camo_state_two"));
        //noinspection deprecation
        if (camoState.isAir() || isValidBlock(camoState, null))
        {
            this.camoState = camoState;
            camoStack = ItemStack.read(nbt.getCompound("camo_stack_two"));
        }
        else
        {
            FramedBlocks.LOGGER.warn(
                    "Framed Block of type \"{}\" at position {} contains an invalid camo of type \"{}\", removing camo! This might be caused by a config or tag change!",
                    state.getBlock().getRegistryName(),
                    pos,
                    camoState.getBlock().getRegistryName()
            );
        }
    }
}