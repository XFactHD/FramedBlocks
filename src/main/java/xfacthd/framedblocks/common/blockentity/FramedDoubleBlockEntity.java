package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.client.model.data.*;
import xfacthd.framedblocks.client.util.FramedBlockData;

public abstract class FramedDoubleBlockEntity extends FramedBlockEntity
{
    public static final ModelProperty<IModelData> DATA_LEFT = new ModelProperty<>();
    public static final ModelProperty<IModelData> DATA_RIGHT = new ModelProperty<>();

    private final IModelData multiModelData = new ModelDataMap.Builder().build();
    private final FramedBlockData modelData = new FramedBlockData();
    protected ItemStack camoStack = ItemStack.EMPTY;
    protected BlockState camoState = Blocks.AIR.defaultBlockState();

    public FramedDoubleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

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
    protected void applyCamo(ItemStack camoStack, BlockState camoState, BlockHitResult hit)
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
    protected BlockState getCamoState(BlockHitResult hit)
    {
        return hitSecondary(hit) ? getCamoStateTwo() : getCamoState();
    }

    @Override
    protected ItemStack getCamoStack(BlockHitResult hit)
    {
        return hitSecondary(hit) ? getCamoStackTwo() : getCamoStack();
    }

    @Override
    public float getCamoExplosionResistance(Explosion explosion)
    {
        return Math.max(
                getCamoState().getExplosionResistance(level, worldPosition, explosion),
                getCamoStateTwo().getExplosionResistance(level, worldPosition, explosion)
        );
    }

    @Override
    public boolean isCamoFlammable(Direction face)
    {
        BlockState camo = getCamoState(face);
        if (camo.isAir() && (!getCamoState().isAir() || !getCamoStateTwo().isAir()))
        {
            return (getCamoState().isAir() || getCamoState().isFlammable(level, worldPosition, face)) &&
                   (getCamoStateTwo().isAir() || getCamoStateTwo().isFlammable(level, worldPosition, face));
        }
        else if (!camo.isAir())
        {
            return camo.isFlammable(level, worldPosition, face);
        }
        return true;
    }

    @Override
    public int getCamoFlammability(Direction face)
    {
        int flammabilityOne = super.getCamoFlammability(face);
        int flammabilityTwo = getCamoStateTwo().isAir() ? -1 : getCamoStateTwo().getFlammability(level, worldPosition, face);

        if (flammabilityOne == -1) { return flammabilityTwo; }
        if (flammabilityTwo == -1) { return flammabilityOne; }
        return Math.min(flammabilityOne, flammabilityTwo);
    }

    protected abstract boolean hitSecondary(BlockHitResult hit);

    /*
     * Sync
     */

    @Override
    protected void writeToDataPacket(CompoundTag nbt)
    {
        super.writeToDataPacket(nbt);

        nbt.put("camo_stack_two", camoStack.save(new CompoundTag()));
        nbt.put("camo_state_two", NbtUtils.writeBlockState(camoState));
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        camoStack = ItemStack.of(nbt.getCompound("camo_stack_two"));

        boolean needUpdate = false;
        BlockState newState = NbtUtils.readBlockState(nbt.getCompound("camo_state_two"));
        if (newState != camoState)
        {
            camoState = newState;

            modelData.setLevel(level);
            modelData.setPos(worldPosition);
            modelData.setCamoState(camoState);

            needUpdate = true;
        }

        return super.readFromDataPacket(nbt) || needUpdate;
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag nbt = super.getUpdateTag();

        nbt.put("camo_stack_two", camoStack.save(new CompoundTag()));
        nbt.put("camo_state_two", NbtUtils.writeBlockState(camoState));

        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt)
    {
        super.handleUpdateTag(nbt);

        camoStack = ItemStack.of(nbt.getCompound("camo_stack_two"));

        BlockState newState = NbtUtils.readBlockState(nbt.getCompound("camo_state_two"));
        if (newState != camoState)
        {
            camoState = newState;

            modelData.setLevel(level);
            modelData.setPos(worldPosition);
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
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.put("camo_stack_two", camoStack.save(new CompoundTag()));
        nbt.put("camo_state_two", NbtUtils.writeBlockState(camoState));

        return super.save(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        camoStack = ItemStack.of(nbt.getCompound("camo_stack_two"));
        camoState = NbtUtils.readBlockState(nbt.getCompound("camo_state_two"));
    }
}