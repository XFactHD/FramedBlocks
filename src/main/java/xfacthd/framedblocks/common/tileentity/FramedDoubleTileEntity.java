package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.client.model.data.*;
import xfacthd.framedblocks.client.util.FramedBlockData;

public abstract class FramedDoubleTileEntity extends FramedTileEntity
{
    public static final ModelProperty<IModelData> DATA_LEFT = new ModelProperty<>();
    public static final ModelProperty<IModelData> DATA_RIGHT = new ModelProperty<>();

    private final IModelData multiModelData = new ModelDataMap.Builder().build();
    private final FramedBlockData modelData = new FramedBlockData();
    protected ItemStack camoStack = ItemStack.EMPTY;
    protected BlockState camoState = Blocks.AIR.defaultBlockState();

    public FramedDoubleTileEntity(TileEntityType<?> type) { super(type); }

    @Override
    public void setCamo(ItemStack camoStack, BlockState camoState, boolean secondary)
    {
        if (secondary)
        {
            int light = getLightValue();

            this.camoStack = camoStack;
            this.camoState = camoState;

            setChanged();
            if (getLightValue() != light)
            {
                doLightUpdate();
            }
            //noinspection ConstantConditions
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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
    public int getLightValue() { return Math.max(camoState.getLightEmission(), super.getLightValue()); }

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

    protected abstract boolean hitSecondary(BlockRayTraceResult hit);

    /*
     * Sync
     */

    @Override
    protected void writeToDataPacket(CompoundNBT nbt)
    {
        super.writeToDataPacket(nbt);

        nbt.put("camo_stack_two", camoStack.save(new CompoundNBT()));
        nbt.put("camo_state_two", NBTUtil.writeBlockState(camoState));
    }

    @Override
    protected boolean readFromDataPacket(CompoundNBT nbt)
    {
        camoStack = ItemStack.of(nbt.getCompound("camo_stack_two"));

        boolean needUpdate = false;
        BlockState newState = NBTUtil.readBlockState(nbt.getCompound("camo_state_two"));
        if (newState != camoState)
        {
            camoState = newState;

            modelData.setWorld(level);
            modelData.setPos(worldPosition);
            modelData.setCamoState(camoState);

            needUpdate = true;
        }

        return super.readFromDataPacket(nbt) || needUpdate;
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        CompoundNBT nbt = super.getUpdateTag();

        nbt.put("camo_stack_two", camoStack.save(new CompoundNBT()));
        nbt.put("camo_state_two", NBTUtil.writeBlockState(camoState));

        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt)
    {
        super.handleUpdateTag(state, nbt);

        camoStack = ItemStack.of(nbt.getCompound("camo_stack_two"));

        BlockState newState = NBTUtil.readBlockState(nbt.getCompound("camo_state_two"));
        if (newState != camoState)
        {
            camoState = newState;

            modelData.setWorld(level);
            modelData.setPos(worldPosition);
            modelData.setCamoState(camoState);
            requestModelDataUpdate();
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
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.put("camo_stack_two", camoStack.save(new CompoundNBT()));
        nbt.put("camo_state_two", NBTUtil.writeBlockState(camoState));

        return super.save(nbt);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);

        camoStack = ItemStack.of(nbt.getCompound("camo_stack_two"));
        camoState = NBTUtil.readBlockState(nbt.getCompound("camo_state_two"));
    }
}