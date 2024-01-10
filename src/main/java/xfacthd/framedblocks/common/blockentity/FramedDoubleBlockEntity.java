package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.model.data.*;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.camo.EmptyCamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.internal.InternalAPI;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.api.util.TestProperties;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockStateCache;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockSoundType;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

import java.util.List;

public abstract class FramedDoubleBlockEntity extends FramedBlockEntity
{
    public static final ModelProperty<ModelData> DATA_LEFT = new ModelProperty<>();
    public static final ModelProperty<ModelData> DATA_RIGHT = new ModelProperty<>();

    private final FramedBlockData modelData = new FramedBlockData();
    private final DoubleBlockSoundType soundType = new DoubleBlockSoundType(this);
    private DoubleBlockStateCache stateCache;
    private CamoContainer camoContainer = EmptyCamoContainer.EMPTY;

    public FramedDoubleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.stateCache = getBlock().getCache(state);
        this.modelData.setUseAltModel(true);
    }

    @Override
    public void setCamoInternal(CamoContainer camo, boolean secondary)
    {
        if (secondary)
        {
            this.camoContainer = camo;
        }
        else
        {
            super.setCamoInternal(camo, false);
        }
    }

    @Override
    public CamoContainer getCamo(BlockState state)
    {
        if (state == stateCache.getBlockPair().getA())
        {
            return getCamo();
        }
        if (state == stateCache.getBlockPair().getB())
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
    public IFramedDoubleBlock getBlock()
    {
        return (IFramedDoubleBlock) super.getBlock();
    }

    @Override
    public boolean canAutoApplyCamoOnPlacement()
    {
        return false;
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean dropCamo)
    {
        super.addAdditionalDrops(drops, dropCamo);
        if (dropCamo && !camoContainer.isEmpty())
        {
            drops.add(camoContainer.toItemStack(ItemStack.EMPTY));
        }
    }

    @Override
    public MapColor getMapColor()
    {
        return switch (stateCache.getTopInteractionMode())
        {
            case FIRST -> super.getMapColor();
            case SECOND -> camoContainer.getMapColor(level, worldPosition);
            case EITHER ->
            {
                MapColor color = super.getMapColor();
                if (color != null)
                {
                    yield color;
                }
                yield camoContainer.getMapColor(level, worldPosition);
            }
        };
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
    public float getCamoFriction(BlockState state, @Nullable Entity entity)
    {
        return switch (stateCache.getTopInteractionMode())
        {
            case FIRST -> getFriction(this, getCamo(), state, entity);
            case SECOND -> getFriction(this, getCamoTwo(), state, entity);
            case EITHER -> Math.max(
                    getFriction(this, getCamo(), state, entity),
                    getFriction(this, getCamoTwo(), state, entity)
            );
        };
    }

    @Override
    public boolean canCamoSustainPlant(Direction side, IPlantable plant)
    {
        return stateCache.getSolidityCheck(side).canSustainPlant(this, side, plant);
    }

    @Override
    public boolean doesCamoPreventDestructionByEntity(Entity entity)
    {
        if (super.doesCamoPreventDestructionByEntity(entity))
        {
            return true;
        }
        return doesCamoPreventDestructionByEntity(this, camoContainer, entity);
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
    protected boolean doesCamoPropagateSkylightDown()
    {
        //noinspection ConstantConditions
        if (!camoContainer.getState().propagatesSkylightDown(level, worldPosition))
        {
            return false;
        }
        return super.doesCamoPropagateSkylightDown();
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

    @Override
    public float getCamoShadeBrightness(float ownShade)
    {
        if (!getCamo().isEmpty())
        {
            //noinspection ConstantConditions
            ownShade = Math.max(ownShade, getCamo().getState().getShadeBrightness(level, worldPosition));
        }
        if (!camoContainer.isEmpty())
        {
            //noinspection ConstantConditions
            ownShade = Math.max(ownShade, camoContainer.getState().getShadeBrightness(level, worldPosition));
        }
        return ownShade;
    }

    public final DoubleBlockSoundType getSoundType()
    {
        return soundType;
    }

    @Override
    protected abstract boolean hitSecondary(BlockHitResult hit);

    public final DoubleBlockTopInteractionMode getTopInteractionMode()
    {
        return stateCache.getTopInteractionMode();
    }

    @Override
    public final CamoContainer getCamo(Direction side)
    {
        return getCamo(side, null);
    }

    @Override
    public final CamoContainer getCamo(Direction side, @Nullable Direction edge)
    {
        return stateCache.getCamoGetter(side, edge).getCamo(this);
    }

    @Override
    public final boolean isSolidSide(Direction side)
    {
        return stateCache.getSolidityCheck(side).isSolid(this);
    }

    @Override
    public boolean updateCulling(Direction side, boolean rerender)
    {
        boolean changed = updateCulling(getModelDataInternal(), stateCache.getBlockPair().getA(), side, rerender);
        changed |= updateCulling(modelData, stateCache.getBlockPair().getB(), side, rerender);
        return changed;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setBlockState(BlockState state)
    {
        super.setBlockState(state);
        stateCache = getBlock().getCache(state);
    }

    /*
     * Debug rendering
     */

    @Override
    public boolean hasCustomOutlineRendering(Player player)
    {
        return !FMLEnvironment.production && TestProperties.ENABLE_DOUBLE_BLOCK_PART_HIT_DEBUG_RENDERER;
    }

    public final Tuple<BlockState, BlockState> getBlockPair()
    {
        return stateCache.getBlockPair();
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

        InternalAPI.INSTANCE.updateCamoNbt(nbt, "camo_state_two", "camo_stack_two", "camo_two");

        CamoContainer camo = CamoContainer.load(nbt.getCompound("camo_two"));
        if (camo.isEmpty() || isValidBlock(camo.getState(), null))
        {
            camoContainer = camo;
        }
        else
        {
            FramedBlocks.LOGGER.warn(
                    "Framed Block of type \"{}\" at position {} contains an invalid camo of type \"{}\", removing camo! This might be caused by a config or tag change!",
                    BuiltInRegistries.BLOCK.getKey(getBlockState().getBlock()),
                    worldPosition,
                    BuiltInRegistries.BLOCK.getKey(camo.getState().getBlock())
            );
        }
    }
}