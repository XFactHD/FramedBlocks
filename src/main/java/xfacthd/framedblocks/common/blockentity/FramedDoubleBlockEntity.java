package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.data.*;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.camo.EmptyCamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.util.DoubleBlockSoundType;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class FramedDoubleBlockEntity extends FramedBlockEntity
{
    public static final ModelProperty<ModelData> DATA_LEFT = new ModelProperty<>();
    public static final ModelProperty<ModelData> DATA_RIGHT = new ModelProperty<>();
    protected static final CamoGetter EMPTY_GETTER = () -> EmptyCamoContainer.EMPTY;

    private final FramedBlockData modelData = new FramedBlockData();
    private final DoubleBlockSoundType soundType = new DoubleBlockSoundType(this);
    private final CamoGetter[][] camoGetters = new CamoGetter[6][7];
    private Tuple<BlockState, BlockState> blockPair;
    private CamoContainer camoContainer = EmptyCamoContainer.EMPTY;

    public FramedDoubleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        blockPair = AbstractFramedDoubleBlock.getStatePair(state);
        modelData.setUseAltModel(true);
        collectCamoGetters();
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
    public CamoContainer getCamo(BlockState state)
    {
        if (state == blockPair.getA())
        {
            return getCamo();
        }
        if (state == blockPair.getB())
        {
            return getCamoTwo();
        }
        return EmptyCamoContainer.EMPTY;
    }

    @Override
    protected CamoContainer getCamo(boolean secondary)
    {
        return secondary ? camoContainer : getCamo();
    }

    public final CamoContainer getCamoTwo()
    {
        return camoContainer;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightValue()
    {
        return Math.max(camoContainer.getState().getLightEmission(), super.getLightValue());
    }

    @Override
    public boolean canAutoApplyCamoOnPlacement()
    {
        return false;
    }

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
    public MapColor getMapColor()
    {
        if (!camoContainer.isEmpty())
        {
            //noinspection ConstantConditions
            return camoContainer.getState().getMapColor(level, worldPosition);
        }
        return super.getMapColor();
    }

    @Override
    public float[] getCamoBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        float[] superMult = super.getCamoBeaconColorMultiplier(level, pos, beaconPos);
        float[] localMult = camoContainer.isEmpty() ? null : camoContainer.getBeaconColorMultiplier(level, pos, beaconPos);

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
    public boolean shouldCamoDisplayFluidOverlay(BlockAndTintGetter level, BlockPos pos, FluidState fluid)
    {
        if (camoContainer.isEmpty() || camoContainer.getState().shouldDisplayFluidOverlay(level, pos, fluid))
        {
            return true;
        }
        return super.shouldCamoDisplayFluidOverlay(level, pos, fluid);
    }

    @Override
    protected boolean isCamoSolid()
    {
        if (camoContainer.isEmpty())
        {
            return false;
        }

        //noinspection ConstantConditions
        return super.isCamoSolid() && camoContainer.getState().isSolidRender(level, worldPosition);
    }

    @Override
    public float getCamoExplosionResistance(Explosion explosion)
    {
        return Math.max(
                super.getCamoExplosionResistance(explosion),
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

        if (flammabilityOne == -1)
        {
            return flammabilityTwo;
        }
        if (flammabilityTwo == -1)
        {
            return flammabilityOne;
        }
        return Math.min(flammabilityOne, flammabilityTwo);
    }

    @Override
    public int getCamoFireSpreadSpeed(Direction face)
    {
        int spreadSpeedOne = super.getCamoFireSpreadSpeed(face);
        int spreadSpeedTwo = camoContainer.isEmpty() ? -1 : camoContainer.getState().getFireSpreadSpeed(level, worldPosition, face);

        if (spreadSpeedOne == -1)
        {
            return spreadSpeedOne;
        }
        if (spreadSpeedTwo == -1)
        {
            return spreadSpeedTwo;
        }
        return Math.min(spreadSpeedOne, spreadSpeedTwo);
    }

    public final DoubleBlockSoundType getSoundType()
    {
        return soundType;
    }

    @Override
    protected abstract boolean hitSecondary(BlockHitResult hit);

    public abstract DoubleSoundMode getSoundMode();

    @Override
    public final CamoContainer getCamo(Direction side)
    {
        return getCamo(side, null);
    }

    @Override
    public final CamoContainer getCamo(Direction side, @Nullable Direction edge)
    {
        return camoGetters[side.ordinal()][Utils.maskNullDirection(edge)].get();
    }

    public abstract CamoGetter getCamoGetter(Direction side, @Nullable Direction edge);

    @Override
    public abstract boolean isSolidSide(Direction side);

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
        blockPair = AbstractFramedDoubleBlock.getStatePair(state);
        collectCamoGetters();
    }

    private void collectCamoGetters()
    {
        Utils.forAllDirections(false, side -> Utils.forAllDirections(edge ->
        {
            CamoGetter getter = null;
            if (edge == null || edge.getAxis() != side.getAxis())
            {
                getter = getCamoGetter(side, edge);
            }
            camoGetters[side.ordinal()][Utils.maskNullDirection(edge)] = getter;
        }));
    }

    /*
     * Debug rendering
     */

    @Override
    public boolean hasCustomOutlineRendering(Player player)
    {
        return !FMLEnvironment.production;
    }

    public Tuple<BlockState, BlockState> getBlockPair()
    {
        return blockPair;
    }

    public final boolean debugHitSecondary(BlockHitResult hit)
    {
        return hitSecondary(hit);
    }

    /*
     * Sync
     */

    @Override
    protected void writeToDataPacket(CompoundTag nbt)
    {
        super.writeToDataPacket(nbt);

        nbt.put("camo_two", CamoContainer.writeToNetwork(camoContainer));
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        boolean needUpdate = false;
        CamoContainer newCamo = CamoContainer.readFromNetwork(nbt.getCompound("camo_two"));
        if (!newCamo.equals(camoContainer))
        {
            int oldLight = getLightValue();
            camoContainer = newCamo;
            if (oldLight != getLightValue()) { doLightUpdate(); }

            modelData.setCamoState(camoContainer.getState());

            needUpdate = true;
            updateCulling(true, false);
        }

        byte flags = nbt.getByte("flags");

        boolean newReinforced = readFlag(flags, FLAG_REINFORCED);
        if (isReinforced() != newReinforced)
        {
            modelData.setReinforced(newReinforced);
            needUpdate = true;
        }

        return super.readFromDataPacket(nbt) || needUpdate;
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag nbt = super.getUpdateTag();

        nbt.put("camo_two", CamoContainer.writeToNetwork(camoContainer));

        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt)
    {
        super.handleUpdateTag(nbt);

        CamoContainer newCamo = CamoContainer.readFromNetwork(nbt.getCompound("camo_two"));
        if (!newCamo.equals(camoContainer))
        {
            camoContainer = newCamo;

            modelData.setCamoState(camoContainer.getState());

            ClientUtils.enqueueClientTask(() -> updateCulling(true, true));
        }

        byte flags = nbt.getByte("flags");

        boolean newReinforced = readFlag(flags, FLAG_REINFORCED);
        if (isReinforced() != newReinforced)
        {
            modelData.setReinforced(newReinforced);
        }
    }

    /*
     * Model data
     */

    @Override
    public ModelData getModelData()
    {
        return ModelData.builder()
                .with(DATA_LEFT, super.getModelData())
                .with(DATA_RIGHT, ModelData.builder()
                        .with(FramedBlockData.PROPERTY, modelData)
                        .build()
                )
                .build();
    }

    @Override
    protected void initModelData()
    {
        super.initModelData();
        modelData.setCamoState(camoContainer.getState());
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
                    ForgeRegistries.BLOCKS.getKey(getBlockState().getBlock()),
                    worldPosition,
                    ForgeRegistries.BLOCKS.getKey(camo.getState().getBlock())
            );
        }
    }



    protected interface CamoGetter extends Supplier<CamoContainer> { }
}