package io.github.xfacthd.framedblocks.api.block.blockentity;

import com.mojang.logging.LogUtils;
import io.github.xfacthd.framedblocks.api.FramedBlocksAPI;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.cache.StateCache;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.camo.applicator.CamoApplicator;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.component.FrameConfig;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/// Base [IFramedBlockEntity] implementation. Holds all general non-camo metadata and one camo.
public non-sealed class FramedBlockEntity extends BlockEntity implements IFramedBlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String CAMO_NBT_KEY = "camo";
    public static final String OVERLAY_NBT_KEY = "overlay";
    /// [InteractionResult] marker instance to consume the interaction and communicate a failed camo interaction. Must be compared by reference.
    public static final InteractionResult CONSUME_CAMO_FAILED = new InteractionResult.Success(InteractionResult.SwingSource.NONE, new InteractionResult.ItemContext(true, null));
    protected static final int FLAG_GLOWING = 1;
    protected static final int FLAG_INTANGIBLE = 1 << 1;
    protected static final int FLAG_REINFORCED = 1 << 2;
    protected static final int FLAG_EMISSIVE = 1 << 3;

    final ClientData<?> clientData;
    private StateCache stateCache;
    private CamoContainer<?, ?> camoContainer = EmptyCamoContainer.EMPTY;
    @Nullable
    private Holder<BlockOverlay> overlay = null;
    private boolean glowing = false;
    private boolean intangible = false;
    private boolean reinforced = false;
    private boolean emissive = false;
    private boolean forceLightUpdate = false;

    public FramedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.clientData = ClientData.create(this);
        this.stateCache = state.framedblocks$getCache();
    }

    @Override
    public final InteractionResult handleInteraction(Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        boolean secondary = hitSecondary(hit, player);
        CamoContainer<?, ?> camo = getCamo(secondary);

        if (camo.isEmpty()) {
            CamoApplicator applicator;
            if ((applicator = stack.getCapability(CamoApplicator.CAPABILITY)) != null) {
                boolean success = applicator.apply(this, player, hand, makeApplicatorCamoHandler(player, secondary), this::tryApplyModifierFromApplicator);
                // Return fail to fully consume the interaction, preventing any UIs from opening to ensure ability to quickly apply camos to lots of blocks
                return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }

            CamoContainerFactory<?> camoFactory;
            if ((camoFactory = CamoContainerHelper.findCamoFactory(stack)) != null) {
                return setCamo(player, ItemAccess.forPlayerInteraction(player, hand), camoFactory, secondary);
            }
        } else {
            if (CamoContainerHelper.isValidRemovalTool(camo, stack)) {
                return clearCamo(player, ItemAccess.forPlayerInteraction(player, hand), camo, secondary);
            }
            if (!player.isShiftKeyDown() && Utils.isConfigurationTool(stack)) {
                return rotateCamo(camo, secondary);
            }
        }

        InteractionResult applyResult = tryApplyModifier(ItemAccess.forPlayerInteraction(player, hand));
        if (applyResult != InteractionResult.PASS) {
            return applyResult;
        }

        InteractionResult removeResult = tryRemoveModifier(player, stack, hand);
        if (removeResult != InteractionResult.PASS) {
            return removeResult;
        }

        CamoContainer<?, ?> newCamo = CamoContainerHelper.handleCamoInteraction(level(), worldPosition, player, camo, stack, hand);
        if (camo != newCamo) {
            if (!level().isClientSide()) {
                setCamo(newCamo, secondary);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    @ApiStatus.Internal
    public final void tryApplyCamoImmediately(Player player) {
        ItemStack stack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (stack.isEmpty()) {
            return;
        }

        CamoApplicator applicator = stack.getCapability(CamoApplicator.CAPABILITY);
        if (applicator != null) {
            applicator.apply(this, player, InteractionHand.OFF_HAND, makeApplicatorCamoHandler(player, false), this::tryApplyModifierFromApplicator);
            return;
        }

        CamoContainerFactory<?> factory = CamoContainerHelper.findCamoFactory(stack);
        if (factory != null) {
            if (canAutoApplyCamoOnPlacement() && camoContainer.isEmpty()) {
                setCamo(player, ItemAccess.forPlayerInteraction(player, InteractionHand.OFF_HAND), factory, false);
            }
        } else {
            tryApplyModifier(ItemAccess.forPlayerInteraction(player, InteractionHand.OFF_HAND));
        }
    }

    private CamoApplicator.CamoHandler makeApplicatorCamoHandler(Player player, boolean secondary) {
        return (factory, itemAccess) -> setCamo(player, itemAccess, factory, secondary) == InteractionResult.SUCCESS;
    }

    private boolean tryApplyModifierFromApplicator(ItemAccess itemAccess) {
        return tryApplyModifier(itemAccess) == InteractionResult.SUCCESS;
    }

    private InteractionResult tryApplyModifier(ItemAccess itemAccess) {
        ItemResource resource = itemAccess.getResource();
        if (!glowing && FrameModifier.GLOWING.matches(resource)) {
            return applyGlowstone(itemAccess);
        }
        if (!intangible && canMakeIntangible(resource)) {
            return applyIntangibility(itemAccess);
        }
        if (!reinforced && FrameModifier.REINFORCED.matches(resource)) {
            return applyReinforcement(itemAccess);
        }
        if (!emissive && FrameModifier.EMISSIVE.matches(resource)) {
            return applyEmissivity(itemAccess);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult tryRemoveModifier(Player player, ItemStack stack, InteractionHand hand) {
        if (glowing && stack.is(Items.BRUSH)) {
            return removeGlowstone(player);
        }
        if (intangible && player.isShiftKeyDown() && Utils.isConfigurationTool(stack)) {
            return removeIntangibility(player);
        }
        if (reinforced && stack.isCorrectToolForDrops(Blocks.OBSIDIAN.defaultBlockState())) {
            return removeReinforcement(player, stack, hand);
        }
        if (emissive && stack.canPerformAction(ItemAbilities.AXE_SCRAPE)) {
            return removeEmissivity(player);
        }
        return InteractionResult.PASS;
    }

    private boolean canMakeIntangible(ItemResource resource) {
        if (!ConfigView.Server.INSTANCE.enableIntangibility()) {
            return false;
        }
        return FrameModifier.INTANGIBLE.matches(resource) && getBlockType().allowMakingIntangible();
    }

    private InteractionResult setCamo(Player player, ItemAccess itemAccess, CamoContainerFactory<?> factory, boolean secondary) {
        CamoContainer<?, ?> camo = factory.applyCamo(level(), worldPosition, player, itemAccess);
        if (camo != null) {
            if (!level().isClientSide()) {
                setCamo(camo, secondary);
            }
            return InteractionResult.SUCCESS;
        }
        // Abuse a specific InteractionResult instance to communicate failed camo application to the caller
        return CONSUME_CAMO_FAILED;
    }

    private InteractionResult clearCamo(Player player, ItemAccess itemAccess, CamoContainer<?, ?> camo, boolean secondary) {
        if (CamoContainerHelper.removeCamo(camo, level(), worldPosition, player, itemAccess)) {
            if (!level().isClientSide()) {
                setCamo(EmptyCamoContainer.EMPTY, secondary);
            }
            return InteractionResult.SUCCESS;
        }
        // Abuse a specific InteractionResult instance to communicate failed camo removal to the caller
        return CONSUME_CAMO_FAILED;
    }

    private InteractionResult rotateCamo(CamoContainer<?, ?> camo, boolean secondary) {
        if (camo.canRotateCamo()) {
            if (!level().isClientSide()) {
                CamoContainer<?, ?> newCamo = camo.rotateCamo();
                Objects.requireNonNull(newCamo, "CamoContainer#rotateCamo() must not return null if CamoContainer#canRotateCamo() returns true");
                setCamo(newCamo, secondary);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult applyGlowstone(ItemAccess itemAccess) {
        if (!Utils.extractOneFromItemAccess(itemAccess, !level().isClientSide())) {
            return InteractionResult.FAIL;
        }
        if (!level().isClientSide()) {
            setGlowing(true);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult removeGlowstone(Player player) {
        if (!level().isClientSide()) {
            setGlowing(false);

            Utils.giveToPlayer(player, FrameModifier.GLOWING.getDefaultStack());
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult applyIntangibility(ItemAccess itemAccess) {
        if (!Utils.extractOneFromItemAccess(itemAccess, !level().isClientSide())) {
            return InteractionResult.FAIL;
        }
        if (!level().isClientSide()) {
            setIntangible(true);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult removeIntangibility(Player player) {
        if (!level().isClientSide()) {
            setIntangible(false);

            Utils.giveToPlayer(player, FrameModifier.INTANGIBLE.getDefaultStack());
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult applyReinforcement(ItemAccess itemAccess) {
        if (!Utils.extractOneFromItemAccess(itemAccess, !level().isClientSide())) {
            return InteractionResult.FAIL;
        }
        if (!level().isClientSide()) {
            setReinforced(true);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult removeReinforcement(Player player, ItemStack stack, InteractionHand hand) {
        if (!level().isClientSide()) {
            setReinforced(false);

            if (!player.isCreative()) {
                stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
            }

            Utils.giveToPlayer(player, FrameModifier.REINFORCED.getDefaultStack());
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult applyEmissivity(ItemAccess itemAccess) {
        if (!Utils.extractOneFromItemAccess(itemAccess, !level().isClientSide())) {
            return InteractionResult.FAIL;
        }
        if (!level().isClientSide()) {
            setEmissive(true);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult removeEmissivity(Player player) {
        if (!level().isClientSide()) {
            setEmissive(false);

            Utils.giveToPlayer(player, FrameModifier.EMISSIVE.getDefaultStack());
        }
        return InteractionResult.SUCCESS;
    }

    /// Check which part of a double block was hit if this is a double block
    /// @param hit The result of the raycast against this block
    /// @param player The player from which the raycast originated
    protected final boolean hitSecondary(BlockHitResult hit, Player player) {
        return hitSecondary(hit, player.getLookAngle(), player.getEyePosition());
    }

    /// Check which part of a double block was hit if this is a double block
    /// @param hit The result of the raycast against this block
    /// @param lookVec The look vector used for the raycast (usually [Player#getLookAngle()])
    /// @param eyePos The eye position from which the raycast originated (usually [Player#getEyePosition()])
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos) {
        return false;
    }

    @Override
    public final void setCamo(CamoContainer<?, ?> camo, BlockHitResult hit, Player player) {
        setCamo(camo, hitSecondary(hit, player));
    }

    @Override
    public final void setCamo(CamoContainer<?, ?> camo, boolean secondary) {
        int light = getLightValue();

        setCamoInternal(camo, secondary);

        setChangedWithoutSignalUpdate();
        if (getLightValue() != light) {
            doLightUpdate();
        }

        if (!updateDynamicStates(true, true, true)) {
            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    void setCamoNoUpdate(CamoContainer<?, ?> camo, boolean secondary) {
        camoContainer = camo;
    }

    void setCamoInternal(CamoContainer<?, ?> camo, boolean secondary) {
        camoContainer = camo;
    }

    @Override
    public CamoContainer<?, ?> getCamo(BlockState state) {
        return camoContainer;
    }

    @Override
    public CamoContainer<?, ?> getCamo(Direction side, @Nullable Direction edge) {
        return camoContainer;
    }

    @Override
    public final CamoContainer<?, ?> getCamo(BlockHitResult hit, Player player) {
        return getCamo(hitSecondary(hit, player));
    }

    @Override
    public final CamoContainer<?, ?> getCamo(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos) {
        return getCamo(hitSecondary(hit, lookVec, eyePos));
    }

    /// {@return the camo applied to the "slot" indicated by the given `secondary` flag}
    ///
    /// @param secondary Whether the first or second camo should be returned
    CamoContainer<?, ?> getCamo(boolean secondary) {
        return camoContainer;
    }

    @Override
    public final CamoContainer<?, ?> getCamo() {
        return camoContainer;
    }

    /// {@return whether all camos applied to this block are solid}
    protected boolean isCamoSolid() {
        return camoContainer.getContent().isSolid();
    }

    /// {@return whether all camos applied to this block propagate skylight}
    protected boolean doesCamoPropagateSkylightDown() {
        return camoContainer.getContent().propagatesSkylightDown();
    }

    /// Update the camo-based [BlockState] properties of this block.
    ///
    /// @param updateSolid    Whether to update solidity ([FramedProperties#SOLID])
    /// @param updateLight    Whether to update light emission ([FramedProperties#GLOWING])
    /// @param updateSkylight Whether to update skylight propagation ([FramedProperties#PROPAGATES_SKYLIGHT])
    /// @return Whether any property changed
    protected final boolean updateDynamicStates(boolean updateSolid, boolean updateLight, boolean updateSkylight) {
        BlockState state = getBlockState();
        boolean changed = false;

        if (updateSolid && getBlock().getBlockType().canOccludeWithSolidCamo()) {
            boolean wasSolid = getBlockState().getValue(FramedProperties.SOLID);
            boolean solid = !intangible && isCamoSolid();

            if (solid != wasSolid) {
                state = state.setValue(FramedProperties.SOLID, solid);
                changed = true;
            }
        }

        if (updateLight) {
            boolean isGlowing = getLightValue() > 0;

            if (isGlowing != state.getValue(FramedProperties.GLOWING)) {
                state = state.setValue(FramedProperties.GLOWING, isGlowing);
                changed = true;
            }
        }

        if (updateSkylight) {
            boolean propagatesSkylight = doesCamoPropagateSkylightDown();

            if (propagatesSkylight != state.getValue(FramedProperties.PROPAGATES_SKYLIGHT)) {
                state = state.setValue(FramedProperties.PROPAGATES_SKYLIGHT, propagatesSkylight);
                changed = true;
            }
        }

        if (changed) {
            level().setBlock(worldPosition, state, Block.UPDATE_ALL);
        }
        return changed;
    }

    @Override
    public final void updateCulling(boolean neighbors, boolean rerender) {
        if (Utils.CLIENT_DIST) {
            clientData.updateCulling(neighbors, rerender);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getCamoExplosionResistance(Explosion explosion) {
        float camoRes = camoContainer.getContent().getExplosionResistance(level(), worldPosition, explosion);
        if (reinforced) {
            camoRes = Math.max(camoRes, Blocks.OBSIDIAN.getExplosionResistance());
        }
        return camoRes;
    }

    @Override
    public boolean isCamoFlammable(Direction face) {
        return !reinforced && (camoContainer.isEmpty() || camoContainer.getContent().isFlammable(level(), worldPosition, face));
    }

    @Override
    public int getCamoFlammability(Direction face) {
        if (reinforced) {
            return 0;
        }
        if (camoContainer.isEmpty()) {
            return -1;
        }
        return camoContainer.getContent().getFlammability(level(), worldPosition, face);
    }

    @Override
    public int getCamoFireSpreadSpeed(Direction face) {
        if (reinforced) {
            return 0;
        }
        if (camoContainer.isEmpty()) {
            return -1;
        }
        return camoContainer.getContent().getFireSpreadSpeed(level(), worldPosition, face);
    }

    @Override
    public boolean isCamoIgnitedByLava(Direction face) {
        return !reinforced && camoContainer.getContent().isIgnitedByLava(level(), worldPosition, face);
    }

    @Override
    public boolean hasOverlay() {
        return overlay != null;
    }

    @Override
    public @Nullable Holder<BlockOverlay> getOverlay() {
        return overlay;
    }

    @Override
    public void setOverlay(@Nullable Holder<BlockOverlay> overlay) {
        if (overlay != this.overlay) {
            this.overlay = overlay;
            setChangedWithoutSignalUpdate();
            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public final void setGlowing(boolean glowing) {
        if (this.glowing != glowing) {
            int oldLight = getLightValue();
            this.glowing = glowing;
            if (oldLight != getLightValue()) {
                doLightUpdate();
            }

            setChangedWithoutSignalUpdate();
            if (!updateDynamicStates(false, true, false)) {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public final boolean isGlowing() {
        return glowing;
    }

    /// {@return the light value emitted by this block based on camos and the [#glowing] flag}
    protected int getLightValue() {
        int baseLight = glowing ? ConfigView.Server.INSTANCE.getGlowstoneLightLevel() : 0;
        return Math.max(baseLight, camoContainer.getContent().getLightEmission());
    }

    @Override
    public final void setIntangible(boolean intangible) {
        if (this.intangible != intangible) {
            this.intangible = intangible;

            setChangedWithoutSignalUpdate();

            if (!updateDynamicStates(true, false, false)) {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public final boolean isMarkedIntangible() {
        return intangible;
    }

    @Override
    public final boolean isIntangible(@Nullable CollisionContext ctx) {
        if (!ConfigView.Server.INSTANCE.enableIntangibility() || !intangible) {
            return false;
        }

        if (ctx instanceof EntityCollisionContext ectx && ectx.getEntity() instanceof Player player) {
            ItemStack mainItem = player.getMainHandItem();
            if (mainItem.isEmpty()) {
                return true;
            }
            if (mainItem.is(FramedConstants.Tags.DISABLE_INTANGIBLE) || Utils.isWrenchRotationTool(mainItem) || Utils.isConfigurationTool(mainItem)) {
                return false;
            }
            if (mainItem.getCapability(CamoApplicator.CAPABILITY) != null) {
                return false;
            }
            return !isValidRemovalToolForAnyCamo(mainItem);
        }

        return true;
    }

    /// {@return whether any of the camos applied to this block can be removed with the given item}
    ///
    /// @param stack The item attempted to be used for camo removal
    protected boolean isValidRemovalToolForAnyCamo(ItemStack stack) {
        return CamoContainerHelper.isValidRemovalTool(camoContainer, stack);
    }

    @Override
    public final void setReinforced(boolean reinforced) {
        if (this.reinforced != reinforced) {
            this.reinforced = reinforced;

            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            setChangedWithoutSignalUpdate();
        }
    }

    @Override
    public final boolean isReinforced() {
        return reinforced;
    }

    @Override
    public final void setEmissive(boolean emissive) {
        if (this.emissive != emissive) {
            this.emissive = emissive;

            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            setChangedWithoutSignalUpdate();
        }
    }

    @Override
    public final boolean isEmissive() {
        return emissive;
    }

    /// Update the "published" dynamic light emission value.
    final void doLightUpdate() {
        AuxiliaryLightManager lightManager = level().getAuxLightManager(worldPosition);
        if (lightManager != null) {
            lightManager.setLightAt(worldPosition, getLightValue());
        }
    }

    @Override
    public IFramedBlock getBlock() {
        return (IFramedBlock) getBlockState().getBlock();
    }

    @Override
    public final IBlockType getBlockType() {
        return getBlock().getBlockType();
    }

    public final Level level() {
        return Objects.requireNonNull(level, "BlockEntity#level accessed before it was set");
    }

    /// Mark the owning chunk for saving without triggering a comparator update.
    protected final void setChangedWithoutSignalUpdate() {
        level().blockEntityChanged(worldPosition);
    }

    /// {@return the [StateCache] associated with the current [BlockState] of this BE}
    protected StateCache getStateCache() {
        return stateCache;
    }

    /// {@return whether a camo can automatically be applied to this block during placement from a suitable item or a Camo applicator in the player's off-hand}
    protected boolean canAutoApplyCamoOnPlacement() {
        return true;
    }

    @Override
    public boolean canTriviallyDropAllCamos() {
        return camoContainer.canTriviallyConvertToItemStack();
    }

    @Override
    public void addAdditionalDrops(Consumer<ItemStack> drops, boolean dropCamo) {
        if (dropCamo && canTriviallyDropAllCamos()) {
            addCamoDrops(drops);
        }
        for (FrameModifier modifier : FrameModifier.MODIFIERS) {
            if (modifier.isActive(this)) {
                drops.accept(modifier.getDefaultStack());
            }
        }
        if (overlay != null) {
            drops.accept(overlay.value().sourceItem().value().getDefaultInstance());
        }
    }

    void addCamoDrops(Consumer<ItemStack> drops) {
        dropCamo(drops, camoContainer);
    }

    static void dropCamo(Consumer<ItemStack> drops, CamoContainer<?, ?> camo) {
        if (!camo.isEmpty()) {
            ItemStack stack = CamoContainerHelper.dropCamo(camo);
            if (!stack.isEmpty()) {
                drops.accept(stack);
            }
        }
    }

    @Override
    public @Nullable MapColor getMapColor() {
        return camoContainer.getMapColor(level(), worldPosition);
    }

    @Override
    public @Nullable Integer getCamoBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return camoContainer.getBeaconColorMultiplier(level, pos, beaconPos);
    }

    @Override
    public boolean shouldCamoDisplayFluidOverlay(BlockAndLightGetter level, BlockPos pos, FluidState fluid) {
        return camoContainer.getContent().shouldDisplayFluidOverlay(level, pos, fluid);
    }

    @Override
    public float getCamoFriction(BlockState state, @Nullable Entity entity, float frameFriction) {
        return camoContainer.getContent().getFriction(level(), worldPosition, entity, frameFriction);
    }

    @Override
    public TriState canCamoSustainPlant(BlockGetter level, Direction side, BlockState plant) {
        return camoContainer.getContent().canSustainPlant(level, worldPosition, side, plant);
    }

    @Override
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canEntityDestroyCamo(Entity entity) {
        if (reinforced && !Blocks.OBSIDIAN.defaultBlockState().canEntityDestroy(level(), worldPosition, entity)) {
            return false;
        }
        return camoContainer.getContent().canEntityDestroy(level(), worldPosition, entity);
    }

    @Override
    public float getCamoBounceRestitution(Entity entity) {
        return getCamoBounceRestitution(camoContainer, entity);
    }

    final float getCamoBounceRestitution(CamoContainer<?, ?> camo, Entity entity) {
        return camo.getContent().getBounceRestitution(level(), worldPosition, entity);
    }

    @Override
    public final void applyStructureRotation(Mirror mirror, Rotation rotation) {
        applyExternalRotation(mirror, rotation, RotationSource.STRUCTURE);
    }

    @Override
    public final void applyWrenchRotation(Rotation rotation, boolean stateChanged) {
        if (applyExternalRotation(Mirror.NONE, rotation, RotationSource.WRENCH)) {
            setChangedWithoutSignalUpdate();
            if (!stateChanged) {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    /// React to this block being rotated by the given [RotationSource].
    ///
    /// @param mirror   The mirror applied to this block
    /// @param rotation The rotation applied to this block
    /// @param source   The source triggering the rotation
    protected boolean applyExternalRotation(Mirror mirror, Rotation rotation, RotationSource source) {
        CamoContainer<?, ?> prevCamo = camoContainer;
        camoContainer = camoContainer.adjustForCarrierRotation(mirror, rotation);
        return camoContainer != prevCamo;
    }

    @Override
    public void onLoad() {
        onLoadInternal();
        super.onLoad();
    }

    void onLoadInternal() {
        if (!level().isClientSide()) {
            // Unconditionally recompute these flags to work around issues with tools exactly copying blockstates without copying BE data
            updateDynamicStates(true, true, true);
            if (forceLightUpdate) {
                // Ensure blocks placed by exactly copying BlockState and BlockEntity correctly store their light emission
                doLightUpdate();
            }
        }
    }

    @Override
    public final void requestModelDataUpdate() {
        requestModelDataUpdateDirect();
        clientData.notifyUpdateRequested();
    }

    final void requestModelDataUpdateDirect() {
        super.requestModelDataUpdate();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setBlockState(BlockState state) {
        BlockState oldState = getBlockState();
        super.setBlockState(state);
        this.stateCache = state.framedblocks$getCache();
        if (level != null && level.isClientSide() && needsModelDataUpdateAfterStateChange(oldState)) {
            requestModelDataUpdate();
        }
    }

    /// {@return whether a model data update should be triggered after changing the [BlockState] from the provided one}
    protected boolean needsModelDataUpdateAfterStateChange(BlockState oldState) {
        return false;
    }

    @Override
    public final FramedBlockEntity unwrap() {
        return this;
    }

    /*
     * Sync
     */

    @Override
    public final CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return appendUpdateTag(new CompoundTag(), registries);
    }

    final CompoundTag appendUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        TagValueOutput valueOutput = new TagValueOutput(ProblemReporter.DISCARDING, registries.createSerializationContext(NbtOps.INSTANCE), tag);
        writeToDataPacket(valueOutput);
        return valueOutput.buildResult();
    }

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public final void handleUpdateTag(ValueInput valueInput) {
        NetworkValueInput.handleUpdateTag(this, valueInput);
    }

    @Override
    public final void onDataPacket(Connection net, ValueInput valueInput) {
        NetworkValueInput.handleUpdatePacket(this, valueInput);
    }

    /// Serialize this BE for network synchronization.
    ///
    /// @param valueOutput The output to write the BE data into
    protected void writeToDataPacket(ValueOutput valueOutput) {
        CamoContainerHelper.writeToNetwork(valueOutput.child(CAMO_NBT_KEY), camoContainer);
        valueOutput.storeNullable(OVERLAY_NBT_KEY, BlockOverlay.CODEC, overlay);
        valueOutput.putByte("flags", writeFlags());
    }

    /// Deserialize this BE from a network packet.
    ///
    /// @param input The input to read the BE data from
    protected void readFromDataPacket(NetworkValueInput input) {
        camoContainer = input.readCamo(CAMO_NBT_KEY, false);

        Holder<BlockOverlay> newOverlay = input.read(OVERLAY_NBT_KEY, BlockOverlay.CODEC).orElse(null);
        if (newOverlay != overlay) {
            overlay = newOverlay;
            input.requestRenderUpdate();
        }

        byte flags = input.getByteOr("flags", (byte) 0);

        boolean newGlow = readFlag(flags, FLAG_GLOWING);
        if (newGlow != glowing) {
            glowing = newGlow;
            input.requestRenderUpdate();
            input.requestLightUpdate();
        }

        boolean newIntangible = readFlag(flags, FLAG_INTANGIBLE);
        if (newIntangible != intangible) {
            intangible = newIntangible;
            input.requestRenderUpdate();
            input.requestCullingUpdate();
        }

        boolean newReinforced = readFlag(flags, FLAG_REINFORCED);
        if (newReinforced != reinforced) {
            reinforced = newReinforced;
            input.requestRenderUpdate();
        }

        boolean newEmissive = readFlag(flags, FLAG_EMISSIVE);
        if (newEmissive != emissive) {
            emissive = newEmissive;
            input.requestRenderUpdate();
        }
    }

    private byte writeFlags() {
        byte flags = 0;
        if (glowing) flags |= FLAG_GLOWING;
        if (intangible) flags |= FLAG_INTANGIBLE;
        if (reinforced) flags |= FLAG_REINFORCED;
        if (emissive) flags |= FLAG_EMISSIVE;
        return flags;
    }

    /// Read the given flag bit from the packed flags.
    ///
    /// @param flags The packed flags
    /// @param flag  The flag blit to read
    /// @return Whether the given flag bit is set
    protected static boolean readFlag(byte flags, @StateFlag int flag) {
        return (flags & flag) != 0;
    }

    /*
     * Model data
     */

    @Override
    public final ModelData getModelData() {
        return clientData.getModelData();
    }

    @Override
    public final ModelData getModelData(boolean includeCullInfo, BlockState state) {
        return clientData.getModelData(includeCullInfo, state);
    }

    /// Attach additional [ModelProperty]s to the given builder for non-camo data required by this block's model.
    protected void attachAdditionalModelData(ModelData.Builder builder) { }

    /*
     * Blueprint handling
     */

    @Override
    public final BlueprintData writeToBlueprint() {
        return appendCustomBlueprintData(new BlueprintData(
                getBlockState().getBlock(),
                collectCamosForBlueprint(),
                Optional.ofNullable(overlay),
                glowing,
                intangible,
                reinforced,
                emissive,
                BlockItemStateProperties.EMPTY,
                Optional.empty()
        ));
    }

    CamoList collectCamosForBlueprint() {
        return CamoList.of(camoContainer);
    }

    /// Append additional data to the given [BlueprintData].
    ///
    /// @param blueprintData The data to attach to
    /// @return the modified data
    protected BlueprintData appendCustomBlueprintData(BlueprintData blueprintData) {
        return blueprintData;
    }

    @Override
    public final void applyBlueprintData(BlueprintData blueprintData) {
        applyCamosFromBlueprint(blueprintData);
        setOverlay(blueprintData.overlay().orElse(null));
        setGlowing(blueprintData.glowing());
        setIntangible(blueprintData.intangible());
        setReinforced(blueprintData.reinforced());
        setEmissive(blueprintData.emissive());
        blueprintData.customData().ifPresent(this::applyCustomDataFromBlueprint);
    }

    void applyCamosFromBlueprint(BlueprintData blueprintData) {
        setCamo(blueprintData.camos().getCamo(0), false);
    }

    /// Apply the custom data from the [BlueprintData] applied to this block.
    ///
    /// @param auxData The additional data read from the [BlueprintData]
    protected void applyCustomDataFromBlueprint(TypedDataComponent<?> auxData) { }

    /*
     * DataComponent handling
     */

    @Override
    @SuppressWarnings("deprecation")
    public void removeComponentsFromTag(ValueOutput valueOutput) {
        valueOutput.discard(CAMO_NBT_KEY);
        valueOutput.discard(OVERLAY_NBT_KEY);
        valueOutput.discard("glowing");
        valueOutput.discard("intangible");
        valueOutput.discard("reinforced");
        valueOutput.discard("emissive");
        valueOutput.discard("updated");
    }

    @Override
    protected final void collectImplicitComponents(DataComponentMap.Builder builder) {
        collectCamoComponents(builder);
        collectMiscComponents(builder);

        FrameConfig.collect(builder, this);

        if (overlay != null) {
            builder.set(FramedConstants.Objects.DC_TYPE_BLOCK_OVERLAY, overlay);
        }
    }

    /// Collect all camos applied to this block.
    ///
    /// @param builder The builder to add the components to
    protected void collectCamoComponents(DataComponentMap.Builder builder) {
        builder.set(FramedConstants.Objects.DC_TYPE_CAMO_LIST, CamoList.of(camoContainer));
    }

    /// Collect additional non-camo data components.
    ///
    /// @param builder The builder to add the components to
    protected void collectMiscComponents(DataComponentMap.Builder builder) { }

    @Override
    protected final void applyImplicitComponents(DataComponentGetter input) {
        applyCamoComponents(input);
        applyMiscComponents(input);

        input.getOrDefault(FramedConstants.Objects.DC_TYPE_FRAME_CONFIG, FrameConfig.DEFAULT).apply(this);
        overlay = input.get(FramedConstants.Objects.DC_TYPE_BLOCK_OVERLAY);
    }

    /// Apply camos from a stack's data components.
    ///
    /// @param input The input to read the components from
    protected void applyCamoComponents(DataComponentGetter input) {
        setCamo(input.getOrDefault(FramedConstants.Objects.DC_TYPE_CAMO_LIST, CamoList.EMPTY).getCamo(0), false);
    }

    /// Apply additional non-camo data components from a stack.
    ///
    /// @param input The input to read the components from
    protected void applyMiscComponents(DataComponentGetter input) { }

    /*
     * NBT stuff
     */

    @Override
    public void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        saveAdditionalInternal(valueOutput);
    }

    void saveAdditionalInternal(ValueOutput valueOutput) {
        valueOutput.store(CAMO_NBT_KEY, CamoContainerHelper.CODEC, camoContainer);
        valueOutput.storeNullable(OVERLAY_NBT_KEY, BlockOverlay.CODEC, overlay);
        valueOutput.putBoolean("glowing", glowing);
        valueOutput.putBoolean("intangible", intangible);
        valueOutput.putBoolean("reinforced", reinforced);
        valueOutput.putBoolean("emissive", emissive);
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        loadAdditionalInternal(valueInput);
    }

    void loadAdditionalInternal(ValueInput valueInput) {
        camoContainer = loadAndValidateCamo(valueInput, CAMO_NBT_KEY);
        overlay = valueInput.read(OVERLAY_NBT_KEY, BlockOverlay.CODEC).orElse(null);
        glowing = valueInput.getBooleanOr("glowing", false);
        intangible = valueInput.getBooleanOr("intangible", false);
        reinforced = valueInput.getBooleanOr("reinforced", false);
        emissive = valueInput.getBooleanOr("emissive", false);

        if (glowing) {
            forceLightUpdate = true;
        }
    }

    final CamoContainer<?, ?> loadAndValidateCamo(ValueInput valueInput, String key) {
        CamoContainer<?, ?> camo = valueInput.read(key, CamoContainerHelper.CODEC).orElse(EmptyCamoContainer.EMPTY);
        if (!CamoContainerHelper.validateCamo(camo)) {
            LOGGER.warn(
                    "Framed Block of type \"{}\" at position {} contains an invalid camo of type \"{}\" containing \"{}\", removing camo! This might be caused by a config or tag change!",
                    BuiltInRegistries.BLOCK.getKey(getBlockState().getBlock()),
                    worldPosition,
                    FramedBlocksAPI.INSTANCE.getCamoContainerFactoryRegistry().getKey(camo.getFactory()),
                    camo.getContent().getCamoId()
            );
            return EmptyCamoContainer.EMPTY;
        }
        forceLightUpdate |= camo.getContent().getLightEmission() > 0;
        return camo;
    }

    @Retention(RetentionPolicy.CLASS)
    @Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
    @MagicConstant(intValues = { FLAG_GLOWING, FLAG_INTANGIBLE, FLAG_REINFORCED, FLAG_EMISSIVE })
    public @interface StateFlag {}
}
