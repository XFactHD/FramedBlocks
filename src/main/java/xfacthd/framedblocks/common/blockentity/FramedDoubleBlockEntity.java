package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.data.*;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.FramedBlockData;
import xfacthd.framedblocks.api.util.client.ClientUtils;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
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
    private ItemStack camoStack = ItemStack.EMPTY;
    private BlockState camoState = Blocks.AIR.defaultBlockState();

    public FramedDoubleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        blockPair = AbstractFramedDoubleBlock.getStatePair(state);
    }

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
    public BlockState getCamoState(BlockState state)
    {
        if (state == blockPair.getA())
        {
            return getCamoState();
        }
        if (state == blockPair.getB())
        {
            return getCamoStateTwo();
        }
        return Blocks.AIR.defaultBlockState();
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
    public void addCamoDrops(List<ItemStack> drops)
    {
        super.addCamoDrops(drops);
        if (!camoStack.isEmpty())
        {
            drops.add(camoStack);
        }
    }

    @Override
    public MaterialColor getMapColor()
    {
        if (!camoState.isAir())
        {
            //noinspection ConstantConditions
            return camoState.getMapColor(level, worldPosition);
        }
        return super.getMapColor();
    }

    @Override
    public float[] getCamoBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        float[] superMult = super.getCamoBeaconColorMultiplier(level, pos, beaconPos);
        float[] localMult = camoState.isAir() ? null : camoState.getBeaconColorMultiplier(level, pos, beaconPos);

        if (superMult == null)
        {
            return localMult;
        }
        if (localMult == null)
        {
            return superMult;
        }

        return new float[] {
                (superMult[0] + localMult[0]) / 2F,
                (superMult[1] + localMult[1]) / 2F,
                (superMult[2] + localMult[2]) / 2F
        };
    }

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
    public boolean shouldCamoDisplayFluidOverlay(BlockAndTintGetter level, BlockPos pos, FluidState fluid)
    {
        if (camoState.isAir() || camoState.shouldDisplayFluidOverlay(level, pos, fluid))
        {
            return true;
        }
        return super.shouldCamoDisplayFluidOverlay(level, pos, fluid);
    }

    @Override
    protected boolean isCamoSolid()
    {
        //noinspection ConstantConditions
        return super.isCamoSolid() && !camoState.isAir() && camoState.isSolidRender(level, worldPosition);
    }

    @Override
    public float getCamoExplosionResistance(Explosion explosion)
    {
        return Math.max(
                super.getCamoExplosionResistance(explosion),
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

    @Override
    public int getCamoFireSpreadSpeed(Direction face)
    {
        int spreadSpeedOne = super.getCamoFireSpreadSpeed(face);
        int spreadSpeedTwo = getCamoStateTwo().isAir() ? -1 : getCamoStateTwo().getFireSpreadSpeed(level, worldPosition, face);

        if (spreadSpeedOne == -1) { return spreadSpeedOne; }
        if (spreadSpeedTwo == -1) { return spreadSpeedTwo; }
        return Math.min(spreadSpeedOne, spreadSpeedTwo);
    }

    public final DoubleBlockSoundType getSoundType() { return soundType; }

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
        BlockState oldState = getBlockState();
        super.setBlockState(state);
        if (state != oldState)
        {
            blockPair = AbstractFramedDoubleBlock.getStatePair(state);
        }
    }

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

            modelData.setCamoState(camoState);

            needUpdate = true;
            updateCulling(true, false);
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

            modelData.setCamoState(camoState);

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

    @Override
    protected void initModelData()
    {
        super.initModelData();
        modelData.setCamoState(camoState);
    }

    /*
     * NBT stuff
     */

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.put("camo_stack_two", camoStack.save(new CompoundTag()));
        nbt.put("camo_state_two", NbtUtils.writeBlockState(camoState));

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        BlockState state = NbtUtils.readBlockState(nbt.getCompound("camo_state_two"));
        if (state.isAir() || isValidBlock(state, null))
        {
            camoStack = ItemStack.of(nbt.getCompound("camo_stack_two"));
            camoState = state;
        }
        else
        {
            FramedBlocks.LOGGER.warn(
                    "Framed Block of type \"{}\" at position {} contains an invalid camo of type \"{}\", removing camo! This might be caused by a config or tag change!",
                    getBlockState().getBlock().getRegistryName(),
                    worldPosition,
                    state.getBlock().getRegistryName()
            );
        }
    }
}