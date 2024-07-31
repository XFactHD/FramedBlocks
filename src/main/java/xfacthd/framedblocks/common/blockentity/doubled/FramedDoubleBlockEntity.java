package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.util.FastColor;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.data.*;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.block.blockentity.IFramedDoubleBlockEntity;
import xfacthd.framedblocks.api.blueprint.BlueprintData;
import xfacthd.framedblocks.api.camo.*;
import xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.config.DevToolsConfig;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockStateCache;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockSoundType;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

import java.util.List;

public class FramedDoubleBlockEntity extends FramedBlockEntity implements IFramedDoubleBlockEntity
{
    public static final ModelProperty<ModelData> DATA_LEFT = new ModelProperty<>();
    public static final ModelProperty<ModelData> DATA_RIGHT = new ModelProperty<>();

    private final boolean[] culledFaces = new boolean[6];
    private final DoubleBlockSoundType soundType = new DoubleBlockSoundType(this);
    private CamoContainer<?, ?> camoContainer = EmptyCamoContainer.EMPTY;

    public FramedDoubleBlockEntity(BlockPos pos, BlockState state)
    {
        this(FBContent.BE_TYPE_FRAMED_DOUBLE_BLOCK.value(), pos, state);
    }

    protected FramedDoubleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void setCamoInternal(CamoContainer<?, ?> camo, boolean secondary)
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
    public CamoContainer<?, ?> getCamo(BlockState state)
    {
        Tuple<BlockState, BlockState> blockPair = getStateCache().getBlockPair();
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
    protected CamoContainer<?, ?> getCamo(boolean secondary)
    {
        return secondary ? camoContainer : getCamo();
    }

    @Override
    public final CamoContainer<?, ?> getCamoTwo()
    {
        return camoContainer;
    }

    @Override
    protected int getLightValue()
    {
        return Math.max(camoContainer.getContent().getLightEmission(), super.getLightValue());
    }

    @Override
    public IFramedDoubleBlock getBlock()
    {
        return (IFramedDoubleBlock) super.getBlock();
    }

    @Override
    public DoubleBlockStateCache getStateCache()
    {
        return (DoubleBlockStateCache) super.getStateCache();
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
            ItemStack stack = CamoContainerHelper.dropCamo(camoContainer);
            if (!stack.isEmpty())
            {
                drops.add(stack);
            }
        }
    }

    @Override
    public MapColor getMapColor()
    {
        return switch (getStateCache().getTopInteractionMode())
        {
            case FIRST -> super.getMapColor();
            case SECOND -> camoContainer.getMapColor(level(), worldPosition);
            case EITHER ->
            {
                MapColor color = super.getMapColor();
                if (color != null)
                {
                    yield color;
                }
                yield camoContainer.getMapColor(level(), worldPosition);
            }
        };
    }

    @Override
    public Integer getCamoBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        Integer superMult = super.getCamoBeaconColorMultiplier(level, pos, beaconPos);
        Integer localMult = camoContainer.getBeaconColorMultiplier(level, pos, beaconPos);

        if (superMult == null)
        {
            return localMult;
        }
        if (localMult == null)
        {
            return superMult;
        }
        return FastColor.ARGB32.multiply(superMult, localMult);
    }

    @Override
    public boolean shouldCamoDisplayFluidOverlay(BlockAndTintGetter level, BlockPos pos, FluidState fluid)
    {
        if (camoContainer.getContent().shouldDisplayFluidOverlay(level, pos, fluid))
        {
            return true;
        }
        return super.shouldCamoDisplayFluidOverlay(level, pos, fluid);
    }

    @Override
    public float getCamoFriction(BlockState state, @Nullable Entity entity, float frameFriction)
    {
        return switch (getStateCache().getTopInteractionMode())
        {
            case FIRST -> getCamo().getContent().getFriction(level, worldPosition, entity, frameFriction);
            case SECOND -> getCamoTwo().getContent().getFriction(level, worldPosition, entity, frameFriction);
            case EITHER -> Math.max(
                    getCamo().getContent().getFriction(level, worldPosition, entity, frameFriction),
                    getCamoTwo().getContent().getFriction(level, worldPosition, entity, frameFriction)
            );
        };
    }

    @Override
    public TriState canCamoSustainPlant(Direction side, BlockState plant)
    {
        return getStateCache().getSolidityCheck(side).canSustainPlant(this, side, plant);
    }

    @Override
    public boolean canEntityDestroyCamo(Entity entity)
    {
        if (super.canEntityDestroyCamo(entity))
        {
            return true;
        }
        return camoContainer.getContent().canEntityDestroy(level(), worldPosition, entity);
    }

    @Override
    protected boolean isCamoSolid()
    {
        return super.isCamoSolid() && camoContainer.getContent().isSolid(level(), worldPosition);
    }

    @Override
    protected boolean doesCamoPropagateSkylightDown()
    {
        if (!camoContainer.getContent().propagatesSkylightDown(level(), worldPosition))
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
                camoContainer.getContent().getExplosionResistance(level(), worldPosition, explosion)
        );
    }

    @Override
    public boolean isCamoFlammable(Direction face)
    {
        CamoContainer<?, ?> camo = getCamo(face);
        if (camo.isEmpty() && (!getCamo().isEmpty() || !camoContainer.isEmpty()))
        {
            return (getCamo().isEmpty() || getCamo().getContent().isFlammable(level(), worldPosition, face)) &&
                   (camoContainer.isEmpty() || camoContainer.getContent().isFlammable(level(), worldPosition, face));
        }
        else if (!camo.isEmpty())
        {
            return camo.getContent().isFlammable(level(), worldPosition, face);
        }
        return true;
    }

    @Override
    public int getCamoFlammability(Direction face)
    {
        int flammabilityOne = super.getCamoFlammability(face);
        int flammabilityTwo = camoContainer.getContent().getFlammability(level(), worldPosition, face);

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
        int spreadSpeedTwo = camoContainer.getContent().getFireSpreadSpeed(level(), worldPosition, face);

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
        return Math.max(
                super.getCamoShadeBrightness(ownShade),
                camoContainer.getContent().getShadeBrightness(level(), worldPosition, ownShade)
        );
    }

    public final DoubleBlockSoundType getSoundType()
    {
        return soundType;
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
    {
        lookVec = lookVec.normalize().multiply(1D/16D, 1D/16D, 1D/16D);
        Vec3 vecStart = hit.getLocation().subtract(lookVec);
        Vec3 vecEnd = hit.getLocation().add(lookVec);

        VoxelShape shapeSec = getBlockPair().getB().getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        BlockHitResult clipSec = shapeSec.clip(vecStart, vecEnd, worldPosition);
        if (clipSec == null)
        {
            return false;
        }

        VoxelShape shapePri = getBlockPair().getA().getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        BlockHitResult clipPri = shapePri.clip(vecStart, vecEnd, worldPosition);
        if (clipPri == null)
        {
            return true;
        }

        return eyePos.distanceToSqr(clipSec.getLocation()) < eyePos.distanceToSqr(clipPri.getLocation());
    }

    public final DoubleBlockTopInteractionMode getTopInteractionMode()
    {
        return getStateCache().getTopInteractionMode();
    }

    @Override
    public final CamoContainer<?, ?> getCamo(Direction side)
    {
        return getCamo(side, null);
    }

    @Override
    public final CamoContainer<?, ?> getCamo(Direction side, @Nullable Direction edge)
    {
        return getStateCache().getCamoGetter(side, edge).getCamo(this);
    }

    @Override
    public final boolean isSolidSide(Direction side)
    {
        return getStateCache().getSolidityCheck(side).isSolid(this);
    }

    @Override
    protected boolean updateCulling(Direction side, BlockState state, boolean rerender)
    {
        Tuple<BlockState, BlockState> blockPair = getStateCache().getBlockPair();
        boolean changed = super.updateCulling(side, blockPair.getA(), rerender);
        changed |= updateCulling(culledFaces, blockPair.getB(), side, rerender);
        return changed;
    }

    /*
     * Debug rendering
     */

    @Override
    public boolean hasCustomOutlineRendering(Player player)
    {
        return !FMLEnvironment.production && DevToolsConfig.VIEW.isDoubleBlockPartHitDebugRendererEnabled();
    }

    public final Tuple<BlockState, BlockState> getBlockPair()
    {
        return getStateCache().getBlockPair();
    }

    public final boolean debugHitSecondary(BlockHitResult hit, Player player)
    {
        return hitSecondary(hit, player);
    }

    /*
     * Sync
     */

    @Override
    protected void writeToDataPacket(CompoundTag nbt, HolderLookup.Provider lookupProvider)
    {
        super.writeToDataPacket(nbt, lookupProvider);

        nbt.put("camo_two", CamoContainerHelper.writeToNetwork(camoContainer));
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt, HolderLookup.Provider lookupProvider)
    {
        boolean needUpdate = false;
        CamoContainer<?, ?> newCamo = CamoContainerHelper.readFromNetwork(nbt.getCompound("camo_two"));
        if (!newCamo.equals(camoContainer))
        {
            int oldLight = getLightValue();
            camoContainer = newCamo;
            if (oldLight != getLightValue())
            {
                doLightUpdate();
            }

            needUpdate = true;
            updateCulling(true, false);
        }

        return super.readFromDataPacket(nbt, lookupProvider) || needUpdate;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider)
    {
        CompoundTag nbt = super.getUpdateTag(provider);

        nbt.put("camo_two", CamoContainerHelper.writeToNetwork(camoContainer));

        return nbt;
    }

    @Override
    protected boolean readCamoFromUpdateTag(CompoundTag nbt, HolderLookup.Provider provider)
    {
        boolean changed = super.readCamoFromUpdateTag(nbt, provider);
        CamoContainer<?, ?> newCamo = CamoContainerHelper.readFromNetwork(nbt.getCompound("camo_two"));
        if (!newCamo.equals(camoContainer))
        {
            camoContainer = newCamo;
            changed = true;
        }
        return changed;
    }

    /*
     * Model data
     */

    @Override
    public ModelData getModelData(boolean includeCullInfo)
    {
        boolean[] cullData = includeCullInfo ? culledFaces : FramedBlockData.NO_CULLED_FACES;
        FramedBlockData modelData = new FramedBlockData(camoContainer.getContent(), cullData, true, isReinforced());
        return ModelData.builder()
                .with(DATA_LEFT, super.getModelData(includeCullInfo))
                .with(DATA_RIGHT, ModelData.builder().with(FramedBlockData.PROPERTY, modelData).build())
                .build();
    }

    /*
     * Blueprint handling
     */

    @Override
    protected CamoList collectCamosForBlueprint()
    {
        return CamoList.of(getCamo(), camoContainer);
    }

    @Override
    protected void applyCamosFromBlueprint(BlueprintData blueprintData)
    {
        super.applyCamosFromBlueprint(blueprintData);
        setCamo(blueprintData.camos().getCamo(1), true);
    }

    /*
     * DataComponent handling
     */

    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        super.removeComponentsFromTag(tag);
        tag.remove("camo_two");
    }

    @Override
    protected void collectCamoComponents(DataComponentMap.Builder builder)
    {
        builder.set(FBContent.DC_TYPE_CAMO_LIST, CamoList.of(getCamo(), camoContainer));
    }

    @Override
    protected void applyCamoComponents(DataComponentInput input)
    {
        super.applyCamoComponents(input);
        setCamo(input.getOrDefault(Utils.DC_TYPE_CAMO_LIST, CamoList.EMPTY).getCamo(1), true);
    }

    /*
     * NBT stuff
     */

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.put("camo_two", CamoContainerHelper.writeToDisk(camoContainer));

        super.saveAdditional(nbt, provider);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        camoContainer = loadAndValidateCamo(nbt, "camo_two");
    }
}
