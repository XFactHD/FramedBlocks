package io.github.xfacthd.framedblocks.api.block.blockentity;

import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.cache.DoubleBlockStateCache;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.util.ColorUtils;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.ValueMerger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.util.ARGB;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/// Extended [IFramedBlockEntity] implementation for blocks with two camos.
public class FramedDoubleBlockEntity extends FramedBlockEntity {
    public static final String CAMO_TWO_NBT_KEY = "camo_two";
    private static final ValueMerger<MapColor> MAP_COLOR_MERGER = new ValueMerger<>(ColorUtils::average);
    private static final ValueMerger<Integer> BEACON_MULT_MERGER = new ValueMerger<>(ARGB::average);
    private static final ValueMerger<Integer> FLAMMABILITY_MERGER = new ValueMerger<>(i -> i == -1, Math::min);

    private CamoContainer<?, ?> camoContainer = EmptyCamoContainer.EMPTY;

    public FramedDoubleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    void setCamoNoUpdate(CamoContainer<?, ?> camo, boolean secondary) {
        if (secondary) {
            camoContainer = camo;
        } else {
            super.setCamoNoUpdate(camo, false);
        }
    }

    @Override
    void setCamoInternal(CamoContainer<?, ?> camo, boolean secondary) {
        if (secondary) {
            camoContainer = camo;
        } else {
            super.setCamoInternal(camo, false);
        }
    }

    @Override
    public CamoContainer<?, ?> getCamo(BlockState state) {
        DoubleBlockParts parts = getStateCache().getParts();
        if (state == parts.stateOne()) {
            return getCamo();
        }
        if (state == parts.stateTwo()) {
            return getCamoTwo();
        }
        return EmptyCamoContainer.EMPTY;
    }

    @Override
    CamoContainer<?, ?> getCamo(boolean secondary) {
        return secondary ? camoContainer : getCamo();
    }

    /// {@return the second camo applied to this block}
    public final CamoContainer<?, ?> getCamoTwo() {
        return camoContainer;
    }

    @Override
    protected int getLightValue() {
        return Math.max(camoContainer.getContent().getLightEmission(), super.getLightValue());
    }

    @Override
    protected final boolean isValidRemovalToolForAnyCamo(ItemStack stack) {
        return super.isValidRemovalToolForAnyCamo(stack) || CamoContainerHelper.isValidRemovalTool(camoContainer, stack);
    }

    @Override
    public final IFramedDoubleBlock getBlock() {
        return (IFramedDoubleBlock) super.getBlock();
    }

    @Override
    public final DoubleBlockStateCache getStateCache() {
        return (DoubleBlockStateCache) super.getStateCache();
    }

    @Override
    protected final boolean canAutoApplyCamoOnPlacement() {
        return false;
    }

    @Override
    public final boolean canTriviallyDropAllCamos() {
        return super.canTriviallyDropAllCamos() && camoContainer.canTriviallyConvertToItemStack();
    }

    @Override
    void addCamoDrops(Consumer<ItemStack> drops) {
        super.addCamoDrops(drops);
        dropCamo(drops, camoContainer);
    }

    @Override
    public @Nullable MapColor getMapColor() {
        return switch (getStateCache().getTopInteractionMode()) {
            case FIRST -> super.getMapColor();
            case SECOND -> camoContainer.getMapColor(level(), worldPosition);
            case BOTH -> MAP_COLOR_MERGER.apply(super.getMapColor(), camoContainer.getMapColor(level(), worldPosition));
        };
    }

    @Override
    public @Nullable Integer getCamoBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        Integer superMult = super.getCamoBeaconColorMultiplier(level, pos, beaconPos);
        Integer localMult = camoContainer.getBeaconColorMultiplier(level, pos, beaconPos);
        return BEACON_MULT_MERGER.apply(superMult, localMult);
    }

    @Override
    public boolean shouldCamoDisplayFluidOverlay(BlockAndLightGetter level, BlockPos pos, FluidState fluid) {
        if (camoContainer.getContent().shouldDisplayFluidOverlay(level, pos, fluid)) {
            return true;
        }
        return super.shouldCamoDisplayFluidOverlay(level, pos, fluid);
    }

    @Override
    public float getCamoFriction(BlockState state, @Nullable Entity entity, float frameFriction) {
        return switch (getStateCache().getTopInteractionMode()) {
            case FIRST -> getCamo().getContent().getFriction(level(), worldPosition, entity, frameFriction);
            case SECOND -> getCamoTwo().getContent().getFriction(level(), worldPosition, entity, frameFriction);
            case BOTH -> Math.max(
                    getCamo().getContent().getFriction(level(), worldPosition, entity, frameFriction),
                    getCamoTwo().getContent().getFriction(level(), worldPosition, entity, frameFriction)
            );
        };
    }

    @Override
    public TriState canCamoSustainPlant(BlockGetter level, Direction side, BlockState plant) {
        return getStateCache().getSolidityCheck(side).canSustainPlant(this, level, side, plant);
    }

    @Override
    public boolean canEntityDestroyCamo(Entity entity) {
        return super.canEntityDestroyCamo(entity) && camoContainer.getContent().canEntityDestroy(level(), worldPosition, entity);
    }

    @Override
    protected boolean isCamoSolid() {
        return super.isCamoSolid() && camoContainer.getContent().isSolid();
    }

    @Override
    protected boolean doesCamoPropagateSkylightDown() {
        return camoContainer.getContent().propagatesSkylightDown() && super.doesCamoPropagateSkylightDown();
    }

    @Override
    public float getCamoExplosionResistance(Explosion explosion) {
        return Math.max(
                super.getCamoExplosionResistance(explosion),
                camoContainer.getContent().getExplosionResistance(level(), worldPosition, explosion)
        );
    }

    @Override
    public boolean isCamoFlammable(Direction face) {
        if (isReinforced()) {
            return false;
        }

        CamoContainer<?, ?> camo = getCamo(face, null);
        if (camo.isEmpty() && (!getCamo().isEmpty() || !camoContainer.isEmpty())) {
            return super.isCamoFlammable(face) && (camoContainer.isEmpty() || camoContainer.getContent().isFlammable(level(), worldPosition, face));
        } else if (!camo.isEmpty()) {
            return camo.getContent().isFlammable(level(), worldPosition, face);
        }
        return true;
    }

    @Override
    public int getCamoFlammability(Direction face) {
        if (isReinforced()) {
            return 0;
        }

        int flammabilityOne = super.getCamoFlammability(face);
        int flammabilityTwo = camoContainer.getContent().getFlammability(level(), worldPosition, face);
        return FLAMMABILITY_MERGER.apply(flammabilityOne, flammabilityTwo);
    }

    @Override
    public int getCamoFireSpreadSpeed(Direction face) {
        if (isReinforced()) {
            return 0;
        }

        int spreadSpeedOne = super.getCamoFireSpreadSpeed(face);
        int spreadSpeedTwo = camoContainer.getContent().getFireSpreadSpeed(level(), worldPosition, face);
        return FLAMMABILITY_MERGER.apply(spreadSpeedOne, spreadSpeedTwo);
    }

    @Override
    public boolean isCamoIgnitedByLava(Direction face) {
        if (isReinforced()) {
            return false;
        }

        CamoContainer<?, ?> camo = getCamo(face, null);
        if (camo.isEmpty() && (!getCamo().isEmpty() || !camoContainer.isEmpty())) {
            return super.isCamoIgnitedByLava(face) && (camoContainer.isEmpty() || camoContainer.getContent().isIgnitedByLava(level(), worldPosition, face));
        } else if (!camo.isEmpty()) {
            return camo.getContent().isIgnitedByLava(level(), worldPosition, face);
        }
        return true;
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos) {
        lookVec = lookVec.normalize().multiply(1D/16D, 1D/16D, 1D/16D);
        Vec3 vecStart = hit.getLocation().subtract(lookVec);
        Vec3 vecEnd = hit.getLocation().add(lookVec);
        DoubleBlockParts parts = getParts();

        VoxelShape shapeSec = parts.stateTwo().getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        BlockHitResult clipSec = shapeSec.clip(vecStart, vecEnd, worldPosition);
        if (clipSec == null) {
            return false;
        }

        VoxelShape shapePri = parts.stateOne().getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        BlockHitResult clipPri = shapePri.clip(vecStart, vecEnd, worldPosition);
        if (clipPri == null) {
            return true;
        }

        return eyePos.distanceToSqr(clipSec.getLocation()) < eyePos.distanceToSqr(clipPri.getLocation());
    }

    @Override
    public final CamoContainer<?, ?> getCamo(Direction side, @Nullable Direction edge) {
        return getStateCache().getCamoGetter(side, edge).getCamo(this);
    }

    @Override
    protected boolean needsModelDataUpdateAfterStateChange(BlockState oldState) {
        if (super.needsModelDataUpdateAfterStateChange(oldState)) {
            return true;
        }

        DoubleBlockStateCache oldCache = (DoubleBlockStateCache) oldState.framedblocks$getCache();
        return !getStateCache().getParts().equals(oldCache.getParts());
    }

    @Override
    public boolean applyExternalRotation(Mirror mirror, Rotation rotation, RotationSource source) {
        CamoContainer<?, ?> prevCamo = camoContainer;
        camoContainer = camoContainer.adjustForCarrierRotation(mirror, rotation);
        return super.applyExternalRotation(mirror, rotation, source) || prevCamo != camoContainer;
    }

    /*
     * Debug rendering
     */

    /// {@return the parts making up this double block}
    public final DoubleBlockParts getParts() {
        return getStateCache().getParts();
    }

    /// {@return whether the given raycast result by the given player points at the second part}
    ///
    /// @param hit    The exact position the player is looking at on this block
    /// @param player The player looking at this block
    public final boolean debugHitSecondary(BlockHitResult hit, Player player) {
        return hitSecondary(hit, player);
    }

    /*
     * Sync
     */

    @Override
    protected void writeToDataPacket(ValueOutput valueOutput) {
        super.writeToDataPacket(valueOutput);
        CamoContainerHelper.writeToNetwork(valueOutput.child(CAMO_TWO_NBT_KEY), camoContainer);
    }

    @Override
    protected void readFromDataPacket(NetworkValueInput input) {
        super.readFromDataPacket(input);
        camoContainer = input.readCamo(CAMO_TWO_NBT_KEY, true);
    }

    /*
     * Blueprint handling
     */

    @Override
    CamoList collectCamosForBlueprint() {
        return CamoList.of(getCamo(), camoContainer);
    }

    @Override
    void applyCamosFromBlueprint(BlueprintData blueprintData) {
        super.applyCamosFromBlueprint(blueprintData);
        setCamo(blueprintData.camos().getCamo(1), true);
    }

    /*
     * DataComponent handling
     */

    @Override
    public void removeComponentsFromTag(ValueOutput valueOutput) {
        super.removeComponentsFromTag(valueOutput);
        valueOutput.discard(CAMO_TWO_NBT_KEY);
    }

    @Override
    protected void collectCamoComponents(DataComponentMap.Builder builder) {
        builder.set(FramedConstants.Objects.DC_TYPE_CAMO_LIST, CamoList.of(getCamo(), camoContainer));
    }

    @Override
    protected void applyCamoComponents(DataComponentGetter input) {
        super.applyCamoComponents(input);
        setCamo(input.getOrDefault(FramedConstants.Objects.DC_TYPE_CAMO_LIST, CamoList.EMPTY).getCamo(1), true);
    }

    /*
     * NBT stuff
     */

    @Override
    public void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.store(CAMO_TWO_NBT_KEY, CamoContainerHelper.CODEC, camoContainer);
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        camoContainer = loadAndValidateCamo(valueInput, CAMO_TWO_NBT_KEY);
    }
}
