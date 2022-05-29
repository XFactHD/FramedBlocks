package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.client.model.data.*;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.data.EmptyCamoContainer;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.util.FramedBlockData;
import xfacthd.framedblocks.api.util.client.ClientUtils;
import xfacthd.framedblocks.common.util.DoubleBlockSoundType;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

import java.util.List;

public abstract class FramedDoubleBlockEntity extends FramedBlockEntity
{
    public static final ModelProperty<IModelData> DATA_LEFT = new ModelProperty<>();
    public static final ModelProperty<IModelData> DATA_RIGHT = new ModelProperty<>();

    private final IModelData multiModelData = new ModelDataMap.Builder().build();
    private final FramedBlockData modelData = new FramedBlockData(false);
    private final DoubleBlockSoundType soundType = new DoubleBlockSoundType(this);
    private Tuple<BlockState, BlockState> blockPair;
    private CamoContainer camoContainer = EmptyCamoContainer.EMPTY;

    public FramedDoubleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        blockPair = getBlockPair(state);
    }

    @Override
    public void setCamo(CamoContainer camo, boolean secondary)
    {
        if (secondary)
        {
            int light = getLightValue();

            this.camoContainer = camo;

            setChanged();
            if (getLightValue() != light)
            {
                doLightUpdate();
            }

            if (!updateDynamicStates(true, true))
            {
                //noinspection ConstantConditions
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
        else
        {
            super.setCamo(camo, false);
        }
    }

    @Override
    protected CamoContainer getCamo(boolean secondary) { return secondary ? camoContainer : getCamo(); }

    public final CamoContainer getCamoTwo() { return camoContainer; }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightValue() { return Math.max(camoContainer.getState().getLightEmission(), super.getLightValue()); }

    @Override
    public void addCamoDrops(List<ItemStack> drops)
    {
        super.addCamoDrops(drops);
        if (!camoContainer.isEmpty())
        {
            drops.add(camoContainer.toItemStack(ItemStack.EMPTY));
        }
    }

    @Override
    public MaterialColor getMapColor()
    {
        if (!camoContainer.isEmpty())
        {
            //noinspection ConstantConditions
            return camoContainer.getState().getMapColor(level, worldPosition);
        }
        return super.getMapColor();
    }

    @Override
    protected boolean isCamoSolid()
    {
        if (camoContainer.isEmpty()) { return false; }

        //noinspection ConstantConditions
        return super.isCamoSolid() && camoContainer.getState().isSolidRender(level, worldPosition);
    }

    @Override
    public float getCamoExplosionResistance(Explosion explosion)
    {
        return Math.max(
                getCamo().getState().getExplosionResistance(level, worldPosition, explosion),
                camoContainer.getState().getExplosionResistance(level, worldPosition, explosion)
        );
    }

    @Override
    public boolean isCamoFlammable(Direction face)
    {
        CamoContainer camo = getCamo(face);
        if (camo.isEmpty() && (!getCamo().isEmpty() || !camoContainer.isEmpty()))
        {
            return (getCamo().isEmpty() || getCamo().getState().isFlammable(level, worldPosition, face)) &&
                   (camoContainer.isEmpty() || camoContainer.getState().isFlammable(level, worldPosition, face));
        }
        else if (!camo.isEmpty())
        {
            return camo.getState().isFlammable(level, worldPosition, face);
        }
        return true;
    }

    @Override
    public int getCamoFlammability(Direction face)
    {
        int flammabilityOne = super.getCamoFlammability(face);
        int flammabilityTwo = camoContainer.isEmpty() ? -1 : camoContainer.getState().getFlammability(level, worldPosition, face);

        if (flammabilityOne == -1) { return flammabilityTwo; }
        if (flammabilityTwo == -1) { return flammabilityOne; }
        return Math.min(flammabilityOne, flammabilityTwo);
    }

    public final DoubleBlockSoundType getSoundType() { return soundType; }

    protected abstract Tuple<BlockState, BlockState> getBlockPair(BlockState state);

    @Override
    protected abstract boolean hitSecondary(BlockHitResult hit);

    public abstract DoubleSoundMode getSoundMode();

    @Override
    public boolean updateCulling(Direction side, boolean rerender)
    {
        boolean changed = updateCulling(getModelDataInternal(), blockPair.getA(), side, rerender);
        changed |= updateCulling(modelData, blockPair.getB(), side, rerender);
        return changed;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setBlockState(BlockState state)
    {
        super.setBlockState(state);
        blockPair = getBlockPair(state);
    }

    /*
     * Sync
     */

    @Override
    protected void writeToDataPacket(CompoundTag nbt)
    {
        super.writeToDataPacket(nbt);

        nbt.put("camo_two", CamoContainer.save(camoContainer));
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        boolean needUpdate = false;
        CamoContainer newCamo = CamoContainer.load(nbt.getCompound("camo_two"));
        if (!newCamo.equals(camoContainer))
        {
            int oldLight = getLightValue();
            camoContainer = newCamo;
            if (oldLight != getLightValue()) { doLightUpdate(); }

            modelData.setCamoState(camoContainer.getState());

            needUpdate = true;
            updateCulling(true, false);
        }

        return super.readFromDataPacket(nbt) || needUpdate;
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag nbt = super.getUpdateTag();

        nbt.put("camo_two", CamoContainer.save(camoContainer));

        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt)
    {
        super.handleUpdateTag(nbt);

        CamoContainer newCamo = CamoContainer.load(nbt.getCompound("camo_two"));
        if (!newCamo.equals(camoContainer))
        {
            camoContainer = newCamo;

            modelData.setCamoState(camoContainer.getState());

            ClientUtils.enqueueClientTask(() -> updateCulling(true, true));
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
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.put("camo_two", CamoContainer.save(camoContainer));

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        CamoContainer camo = CamoContainer.load(nbt.getCompound("camo_two"));
        if (camo.isEmpty() || isValidBlock(camo.getState(), null))
        {
            camoContainer = camo;
        }
        else
        {
            FramedBlocks.LOGGER.warn(
                    "Framed Block of type \"{}\" at position {} contains an invalid camo of type \"{}\", removing camo! This might be caused by a config or tag change!",
                    getBlockState().getBlock().getRegistryName(),
                    worldPosition,
                    camo.getState().getBlock().getRegistryName()
            );
        }
    }
}