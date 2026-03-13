package io.github.xfacthd.framedblocks.api.block.blockentity;

import com.mojang.logging.LogUtils;
import io.github.xfacthd.framedblocks.api.FramedBlocksAPI;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.cache.StateCache;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.block.render.CullingHelper;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.camo.applicator.CamoApplicator;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.component.FrameConfig;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.serdes.FramedCodecs;
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
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public non-sealed class FramedBlockEntity extends BlockEntity implements IFramedBlockEntity
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String CAMO_NBT_KEY = "camo";
    public static final String CAMO_DIR_NBT_KEY = "camo_dir";
    public static final String OVERLAY_NBT_KEY = "overlay";
    /**
     * {@link InteractionResult} marker instance to consume the interaction and communicate a failed camo interaction
     */
    public static final InteractionResult CONSUME_CAMO_FAILED = new InteractionResult.Success(InteractionResult.SwingSource.NONE, new InteractionResult.ItemContext(true, null));
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int DATA_VERSION = 3;
    protected static final int FLAG_GLOWING = 1;
    protected static final int FLAG_INTANGIBLE = 1 << 1;
    protected static final int FLAG_REINFORCED = 1 << 2;
    protected static final int FLAG_EMISSIVE = 1 << 3;

    private final boolean[] culledFaces = new boolean[6];
    private StateCache stateCache;
    private CamoContainer<?, ?> camoContainer = EmptyCamoContainer.EMPTY;
    /** Holds the horizontal orientation this block had when the camo was applied ("applications" for the purpose of updating an existing camo don't update this) */
    @Nullable
    private Direction camoOrientation = null;
    @Nullable
    private Holder<BlockOverlay> overlay = null;
    private boolean glowing = false;
    private boolean intangible = false;
    private boolean reinforced = false;
    private boolean emissive = false;
    private boolean recheckStates = false;
    private boolean forceLightUpdate = false;
    private boolean cullStateDirty = false;

    public FramedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.stateCache = state.framedblocks$getCache();
    }

    @Override
    public final InteractionResult handleInteraction(Player player, InteractionHand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(hand);
        boolean secondary = hitSecondary(hit, player);
        CamoContainer<?, ?> camo = getCamo(secondary);

        if (camo.isEmpty())
        {
            CamoApplicator applicator;
            if ((applicator = stack.getCapability(CamoApplicator.CAPABILITY)) != null)
            {
                boolean success = applicator.apply(this, player, hand, makeApplicatorCamoHandler(player, secondary), this::tryApplyModifierFromApplicator);
                // Return fail to fully consume the interaction, preventing any UIs from opening to ensure ability to quickly apply camos to lots of blocks
                return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }

            CamoContainerFactory<?> camoFactory;
            if ((camoFactory = CamoContainerHelper.findCamoFactory(stack)) != null)
            {
                return setCamo(player, ItemAccess.forPlayerInteraction(player, hand), camoFactory, secondary);
            }
        }
        else
        {
            if (CamoContainerHelper.isValidRemovalTool(camo, stack))
            {
                return clearCamo(player, ItemAccess.forPlayerInteraction(player, hand), camo, secondary);
            }
            if (!player.isShiftKeyDown() && Utils.isConfigurationTool(stack))
            {
                return rotateCamo(camo, secondary);
            }
        }

        InteractionResult applyResult = tryApplyModifier(ItemAccess.forPlayerInteraction(player, hand));
        if (applyResult != InteractionResult.PASS)
        {
            return applyResult;
        }

        InteractionResult removeResult = tryRemoveModifier(player, stack, hand);
        if (removeResult != InteractionResult.PASS)
        {
            return removeResult;
        }

        CamoContainer<?, ?> newCamo = CamoContainerHelper.handleCamoInteraction(level(), worldPosition, player, camo, stack, hand);
        if (camo != newCamo)
        {
            if (!level().isClientSide())
            {
                setCamo(newCamo, secondary);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    @ApiStatus.Internal
    public final void tryApplyCamoImmediately(Player player)
    {
        ItemStack stack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (stack.isEmpty()) return;

        CamoApplicator applicator = stack.getCapability(CamoApplicator.CAPABILITY);
        if (applicator != null)
        {
            applicator.apply(this, player, InteractionHand.OFF_HAND, makeApplicatorCamoHandler(player, false), this::tryApplyModifierFromApplicator);
            return;
        }

        CamoContainerFactory<?> factory = CamoContainerHelper.findCamoFactory(stack);
        if (factory != null)
        {
            if (canAutoApplyCamoOnPlacement() && camoContainer.isEmpty())
            {
                setCamo(player, ItemAccess.forPlayerInteraction(player, InteractionHand.OFF_HAND), factory, false);
            }
        }
        else
        {
            tryApplyModifier(ItemAccess.forPlayerInteraction(player, InteractionHand.OFF_HAND));
        }
    }

    private CamoApplicator.CamoHandler makeApplicatorCamoHandler(Player player, boolean secondary)
    {
        return (factory, itemAccess) -> setCamo(player, itemAccess, factory, secondary) == InteractionResult.SUCCESS;
    }

    private boolean tryApplyModifierFromApplicator(ItemAccess itemAccess)
    {
        return tryApplyModifier(itemAccess) == InteractionResult.SUCCESS;
    }

    private InteractionResult tryApplyModifier(ItemAccess itemAccess)
    {
        ItemResource resource = itemAccess.getResource();
        if (!glowing && FrameModifier.GLOWING.matches(resource))
        {
            return applyGlowstone(itemAccess);
        }
        if (!intangible && canMakeIntangible(resource))
        {
            return applyIntangibility(itemAccess);
        }
        if (!reinforced && FrameModifier.REINFORCED.matches(resource))
        {
            return applyReinforcement(itemAccess);
        }
        if (!emissive && FrameModifier.EMISSIVE.matches(resource))
        {
            return applyEmissivity(itemAccess);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult tryRemoveModifier(Player player, ItemStack stack, InteractionHand hand)
    {
        if (glowing && stack.is(Items.BRUSH))
        {
            return removeGlowstone(player);
        }
        if (intangible && player.isShiftKeyDown() && Utils.isConfigurationTool(stack))
        {
            return removeIntangibility(player);
        }
        if (reinforced && stack.isCorrectToolForDrops(Blocks.OBSIDIAN.defaultBlockState()))
        {
            return removeReinforcement(player, stack, hand);
        }
        if (emissive && stack.canPerformAction(ItemAbilities.AXE_SCRAPE))
        {
            return removeEmissivity(player);
        }
        return InteractionResult.FAIL;
    }

    private boolean canMakeIntangible(ItemResource resource)
    {
        if (!ConfigView.Server.INSTANCE.enableIntangibility())
        {
            return false;
        }
        return FrameModifier.INTANGIBLE.matches(resource) && getBlockType().allowMakingIntangible();
    }

    private InteractionResult setCamo(Player player, ItemAccess itemAccess, CamoContainerFactory<?> factory, boolean secondary)
    {
        CamoContainer<?, ?> camo = factory.applyCamo(level(), worldPosition, player, itemAccess);
        if (camo != null)
        {
            if (!level().isClientSide())
            {
                setCamo(camo, secondary);
            }
            return InteractionResult.SUCCESS;
        }
        // Abuse a specific InteractionResult instance to communicate failed camo application to the caller
        return CONSUME_CAMO_FAILED;
    }

    private InteractionResult clearCamo(Player player, ItemAccess itemAccess, CamoContainer<?, ?> camo, boolean secondary)
    {
        if (CamoContainerHelper.removeCamo(camo, level(), worldPosition, player, itemAccess))
        {
            if (!level().isClientSide())
            {
                setCamo(EmptyCamoContainer.EMPTY, secondary);
            }
            return InteractionResult.SUCCESS;
        }
        // Abuse a specific InteractionResult instance to communicate failed camo removal to the caller
        return CONSUME_CAMO_FAILED;
    }

    private InteractionResult rotateCamo(CamoContainer<?, ?> camo, boolean secondary)
    {
        if (camo.canRotateCamo())
        {
            if (!level().isClientSide())
            {
                CamoContainer<?, ?> newCamo = camo.rotateCamo();
                Objects.requireNonNull(newCamo, "CamoContainer#rotateCamo() must not return null if CamoContainer#canRotateCamo() returns true");
                setCamo(newCamo, secondary);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult applyGlowstone(ItemAccess itemAccess)
    {
        if (!Utils.extractOneFromItemAccess(itemAccess, !level().isClientSide()))
        {
            return InteractionResult.FAIL;
        }
        if (!level().isClientSide())
        {
            setGlowing(true);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult removeGlowstone(Player player)
    {
        if (!level().isClientSide())
        {
            setGlowing(false);

            Utils.giveToPlayer(player, FrameModifier.GLOWING.getDefaultStack(), true);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult applyIntangibility(ItemAccess itemAccess)
    {
        if (!Utils.extractOneFromItemAccess(itemAccess, !level().isClientSide()))
        {
            return InteractionResult.FAIL;
        }
        if (!level().isClientSide())
        {
            setIntangible(true);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult removeIntangibility(Player player)
    {
        if (!level().isClientSide())
        {
            setIntangible(false);

            Utils.giveToPlayer(player, FrameModifier.INTANGIBLE.getDefaultStack(), true);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult applyReinforcement(ItemAccess itemAccess)
    {
        if (!Utils.extractOneFromItemAccess(itemAccess, !level().isClientSide()))
        {
            return InteractionResult.FAIL;
        }
        if (!level().isClientSide())
        {
            setReinforced(true);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult removeReinforcement(Player player, ItemStack stack, InteractionHand hand)
    {
        if (!level().isClientSide())
        {
            setReinforced(false);

            if (!player.isCreative())
            {
                stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
            }

            Utils.giveToPlayer(player, FrameModifier.REINFORCED.getDefaultStack(), true);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult applyEmissivity(ItemAccess itemAccess)
    {
        if (!Utils.extractOneFromItemAccess(itemAccess, !level().isClientSide()))
        {
            return InteractionResult.FAIL;
        }
        if (!level().isClientSide())
        {
            setEmissive(true);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult removeEmissivity(Player player)
    {
        if (!level().isClientSide())
        {
            setEmissive(false);

            Utils.giveToPlayer(player, FrameModifier.EMISSIVE.getDefaultStack(), true);
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Check which part of a double block was hit if this is a double block
     * @param hit The result of the raycast against this block
     * @param player The player from which the raycast originated
     */
    protected final boolean hitSecondary(BlockHitResult hit, Player player)
    {
        return hitSecondary(hit, player.getLookAngle(), player.getEyePosition());
    }

    /**
     * Check which part of a double block was hit if this is a double block
     * @param hit The result of the raycast against this block
     * @param lookVec The look vector used for the raycast (usually {@link Player#getLookAngle()})
     * @param eyePos The eye position from which the raycast originated (usually {@link Player#getEyePosition()})
     */
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
    {
        return false;
    }

    /**
     * Update the camo of this block. Whether the primary or secondary camo will be replaced depends
     * on the given {@link BlockHitResult} and {@link Player}
     */
    @Override
    public final void setCamo(CamoContainer<?, ?> camo, BlockHitResult hit, Player player)
    {
        setCamo(camo, hitSecondary(hit, player));
    }

    @Override
    public final void setCamo(CamoContainer<?, ?> camo, boolean secondary)
    {
        setCamo(camo, secondary, CamoOrientation.UNKNOWN);
    }

    @Override
    public final void setCamo(CamoContainer<?, ?> camo, boolean secondary, CamoOrientation orientation)
    {
        int light = getLightValue();

        boolean forceOrientation = orientation != CamoOrientation.UNKNOWN;
        Direction camoOrientation = orientation.resolve(this);
        setCamoInternal(camo, secondary, camoOrientation, forceOrientation);

        setChangedWithoutSignalUpdate();
        if (getLightValue() != light)
        {
            doLightUpdate();
        }

        if (!updateDynamicStates(true, true, true))
        {
            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    void setCamoNoUpdate(CamoContainer<?, ?> camo, boolean secondary)
    {
        camoContainer = camo;
    }

    void setCamoInternal(CamoContainer<?, ?> camo, boolean secondary, @Nullable Direction orientation, boolean forceOrientation)
    {
        camoOrientation = updateOrientation(camo, orientation, false, forceOrientation);
        camoContainer = camo;
    }

    @Nullable
    Direction updateOrientation(CamoContainer<?, ?> newCamo, @Nullable Direction newDir, boolean second, boolean force)
    {
        if (newCamo.isEmpty()) return null;

        CamoContainer<?, ?> oldCamo = getCamo(second);
        if (oldCamo.isEmpty() || force) return newDir;

        // Keep previous orientation
        return getCamoOrientation(second);
    }

    /**
     * Returns the camo for the given {@link BlockState}. Used for cases where different double blocks
     * with the same underlying shape(s) don't use the same side to return the camo for a given "sub-state".
     */
    @Override
    public CamoContainer<?, ?> getCamo(BlockState state)
    {
        return camoContainer;
    }

    /**
     * Returns the camo for the given edge of the given side or for the full face if a null edge is provided
     */
    @Override
    public CamoContainer<?, ?> getCamo(Direction side, @Nullable Direction edge)
    {
        return camoContainer;
    }

    /**
     * Used to return a different camo depending on the exact interaction location
     * @param hit The result of the raycast against this block
     * @param player The player from which the raycast originated
     */
    @Override
    public final CamoContainer<?, ?> getCamo(BlockHitResult hit, Player player)
    {
        return getCamo(hitSecondary(hit, player));
    }

    /**
     * Used to return a different camo depending on the exact interaction location
     * @param hit The result of the raycast against this block
     * @param lookVec The look vector used for the raycast (usually {@link Player#getLookAngle()})
     * @param eyePos The eye position from which the raycast originated (usually {@link Player#getEyePosition()})
     */
    @Override
    public final CamoContainer<?, ?> getCamo(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
    {
        return getCamo(hitSecondary(hit, lookVec, eyePos));
    }

    CamoContainer<?, ?> getCamo(boolean secondary)
    {
        return camoContainer;
    }

    /**
     * Returns the horizontal orientation this block had when the camo was applied.
     *
     * @param secondary Whether the orientation of the first or second camo should be returned
     */
    @Override
    @Nullable
    public Direction getCamoOrientation(boolean secondary)
    {
        return camoOrientation;
    }

    @Override
    public final CamoContainer<?, ?> getCamo()
    {
        return camoContainer;
    }

    protected boolean isCamoSolid()
    {
        return camoContainer.getContent().isSolid();
    }

    protected boolean doesCamoPropagateSkylightDown()
    {
        return camoContainer.getContent().propagatesSkylightDown();
    }

    protected final boolean updateDynamicStates(boolean updateSolid, boolean updateLight, boolean updateSkylight)
    {
        BlockState state = getBlockState();
        boolean changed = false;

        if (updateSolid && getBlock().getBlockType().canOccludeWithSolidCamo())
        {
            boolean wasSolid = getBlockState().getValue(FramedProperties.SOLID);
            boolean solid = !intangible && isCamoSolid();

            if (solid != wasSolid)
            {
                state = state.setValue(FramedProperties.SOLID, solid);
                changed = true;
            }
        }

        if (updateLight)
        {
            boolean isGlowing = getLightValue() > 0;

            if (isGlowing != state.getValue(FramedProperties.GLOWING))
            {
                state = state.setValue(FramedProperties.GLOWING, isGlowing);
                changed = true;
            }
        }

        if (updateSkylight)
        {
            boolean propagatesSkylight = doesCamoPropagateSkylightDown();

            if (propagatesSkylight != state.getValue(FramedProperties.PROPAGATES_SKYLIGHT))
            {
                state = state.setValue(FramedProperties.PROPAGATES_SKYLIGHT, propagatesSkylight);
                changed = true;
            }
        }

        if (changed)
        {
            level().setBlock(worldPosition, state, Block.UPDATE_ALL);
        }
        return changed;
    }

    void markCullStateDirty()
    {
        cullStateDirty = true;
    }

    @Override
    public final void updateCulling(boolean neighbors, boolean rerender)
    {
        boolean changed = false;
        for (Direction dir : DIRECTIONS)
        {
            BlockState state = getBlockState();
            changed |= updateCulling(dir, state, false);
            if (neighbors && level().getBlockEntity(worldPosition.relative(dir)) instanceof IFramedBlockEntity be)
            {
                be.unwrap().updateCulling(dir.getOpposite(), be.getBlockState(), true);
            }
        }

        if (changed)
        {
            requestModelDataUpdate();
            if (rerender)
            {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    protected boolean updateCulling(Direction side, BlockState state, boolean rerender)
    {
        return updateCulling(culledFaces, state, side, rerender);
    }

    protected final boolean updateCulling(boolean[] culledFaces, BlockState testState, Direction side, boolean rerender)
    {
        boolean wasHidden = culledFaces[side.ordinal()];
        boolean hidden = CullingHelper.isSideHidden(level(), worldPosition, testState, side);
        if (wasHidden != hidden)
        {
            culledFaces[side.ordinal()] = hidden;
            requestModelDataUpdate();
            if (rerender)
            {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            return true;
        }
        return false;
    }

    @Override
    public float getCamoExplosionResistance(Explosion explosion)
    {
        float camoRes = camoContainer.getContent().getExplosionResistance(level(), worldPosition, explosion);
        if (reinforced)
        {
            camoRes = Math.max(camoRes, Blocks.OBSIDIAN.getExplosionResistance());
        }
        return camoRes;
    }

    @Override
    public boolean isCamoFlammable(Direction face)
    {
        if (reinforced)
        {
            return false;
        }
        return camoContainer.isEmpty() || camoContainer.getContent().isFlammable(level(), worldPosition, face);
    }

    @Override
    public int getCamoFlammability(Direction face)
    {
        if (reinforced)
        {
            return 0;
        }
        return camoContainer.isEmpty() ? -1 : camoContainer.getContent().getFlammability(level(), worldPosition, face);
    }

    @Override
    public int getCamoFireSpreadSpeed(Direction face)
    {
        if (reinforced)
        {
            return 0;
        }
        return camoContainer.isEmpty() ? -1 : camoContainer.getContent().getFireSpreadSpeed(level(), worldPosition, face);
    }

    @Override
    public boolean isCamoIgnitedByLava(Direction face)
    {
        return !reinforced && camoContainer.getContent().isIgnitedByLava(level(), worldPosition, face);
    }

    @Override
    public boolean hasOverlay()
    {
        return overlay != null;
    }

    @Override
    @Nullable
    public Holder<BlockOverlay> getOverlay()
    {
        return overlay;
    }

    @Override
    public void setOverlay(@Nullable Holder<BlockOverlay> overlay)
    {
        if (overlay != this.overlay)
        {
            this.overlay = overlay;
            setChangedWithoutSignalUpdate();
            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public final void setGlowing(boolean glowing)
    {
        if (this.glowing != glowing)
        {
            int oldLight = getLightValue();
            this.glowing = glowing;
            if (oldLight != getLightValue())
            {
                doLightUpdate();
            }

            setChangedWithoutSignalUpdate();
            if (!updateDynamicStates(false, true, false))
            {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public final boolean isGlowing()
    {
        return glowing;
    }

    protected int getLightValue()
    {
        int baseLight = glowing ? ConfigView.Server.INSTANCE.getGlowstoneLightLevel() : 0;
        return Math.max(baseLight, camoContainer.getContent().getLightEmission());
    }

    @Override
    public final void setIntangible(boolean intangible)
    {
        if (this.intangible != intangible)
        {
            this.intangible = intangible;

            setChangedWithoutSignalUpdate();

            if (!updateDynamicStates(true, false, false))
            {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    /**
     * Returns whether this block is marked as intangible.
     * <p>
     * If this method returns {@code true}, an entity interacting with this block may still behave as if it
     * returned {@code false} depending on the context.
     *
     * @return whether this block is marked as intangible
     */
    @Override
    public final boolean isMarkedIntangible()
    {
        return intangible;
    }

    @Override
    public final boolean isIntangible(@Nullable CollisionContext ctx)
    {
        if (!ConfigView.Server.INSTANCE.enableIntangibility() || !intangible)
        {
            return false;
        }

        if (ctx instanceof EntityCollisionContext ectx && ectx.getEntity() instanceof Player player)
        {
            ItemStack mainItem = player.getMainHandItem();
            if (mainItem.isEmpty())
            {
                return true;
            }
            if (mainItem.is(Utils.DISABLE_INTANGIBLE) || Utils.isWrenchRotationTool(mainItem) || Utils.isConfigurationTool(mainItem))
            {
                return false;
            }
            if (mainItem.getCapability(CamoApplicator.CAPABILITY) != null)
            {
                return false;
            }
            return !isValidRemovalToolForAnyCamo(mainItem);
        }

        return true;
    }

    /**
     * {@return whether any of the camos applied to this block can be removed with the given item}
     */
    protected boolean isValidRemovalToolForAnyCamo(ItemStack stack)
    {
        return CamoContainerHelper.isValidRemovalTool(camoContainer, stack);
    }

    @Override
    public final void setReinforced(boolean reinforced)
    {
        if (this.reinforced != reinforced)
        {
            this.reinforced = reinforced;

            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            setChangedWithoutSignalUpdate();
        }
    }

    @Override
    public final boolean isReinforced()
    {
        return reinforced;
    }

    @Override
    public final void setEmissive(boolean emissive)
    {
        if (this.emissive != emissive)
        {
            this.emissive = emissive;

            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            setChangedWithoutSignalUpdate();
        }
    }

    @Override
    public final boolean isEmissive()
    {
        return emissive;
    }

    final void doLightUpdate()
    {
        AuxiliaryLightManager lightManager = level().getAuxLightManager(worldPosition);
        if (lightManager != null)
        {
            lightManager.setLightAt(worldPosition, getLightValue());
        }
    }

    @Override
    public IFramedBlock getBlock()
    {
        return (IFramedBlock) getBlockState().getBlock();
    }

    @Override
    public final IBlockType getBlockType()
    {
        return getBlock().getBlockType();
    }

    public final Level level()
    {
        return Objects.requireNonNull(level, "BlockEntity#level accessed before it was set");
    }

    protected final void setChangedWithoutSignalUpdate()
    {
        level().blockEntityChanged(worldPosition);
    }

    protected StateCache getStateCache()
    {
        return stateCache;
    }

    protected boolean canAutoApplyCamoOnPlacement()
    {
        return true;
    }

    /**
     * {@return whether all camos applied to this block can be trivially converted to {@link ItemStack}s for dropping}
     */
    @Override
    public boolean canTriviallyDropAllCamos()
    {
        return camoContainer.canTriviallyConvertToItemStack();
    }

    /**
     * Add additional drops to the list of items being dropped
     * @param drops The list of items being dropped
     * @param dropCamo Whether the camo item should be dropped
     */
    @Override
    public void addAdditionalDrops(Consumer<ItemStack> drops, boolean dropCamo)
    {
        if (dropCamo && canTriviallyDropAllCamos())
        {
            addCamoDrops(drops);
        }
        for (FrameModifier modifier : FrameModifier.MODIFIERS)
        {
            if (modifier.isActive(this))
            {
                drops.accept(modifier.getDefaultStack());
            }
        }
        if (overlay != null)
        {
            drops.accept(overlay.value().sourceItem().value().getDefaultInstance());
        }
    }

    void addCamoDrops(Consumer<ItemStack> drops)
    {
        dropCamo(drops, camoContainer);
    }

    static void dropCamo(Consumer<ItemStack> drops, CamoContainer<?, ?> camo)
    {
        if (!camo.isEmpty())
        {
            ItemStack stack = CamoContainerHelper.dropCamo(camo);
            if (!stack.isEmpty())
            {
                drops.accept(stack);
            }
        }
    }

    @Override
    @Nullable
    public MapColor getMapColor()
    {
        return camoContainer.getMapColor(level(), worldPosition);
    }

    @Override
    @Nullable
    public Integer getCamoBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        return camoContainer.getBeaconColorMultiplier(level, pos, beaconPos);
    }

    @Override
    public boolean shouldCamoDisplayFluidOverlay(BlockAndTintGetter level, BlockPos pos, FluidState fluid)
    {
        return camoContainer.getContent().shouldDisplayFluidOverlay(level, pos, fluid);
    }

    @Override
    public float getCamoFriction(BlockState state, @Nullable Entity entity, float frameFriction)
    {
        return camoContainer.getContent().getFriction(level(), worldPosition, entity, frameFriction);
    }

    @Override
    public TriState canCamoSustainPlant(BlockGetter level, Direction side, BlockState plant)
    {
        return camoContainer.getContent().canSustainPlant(level, worldPosition, side, plant);
    }

    @Override
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canEntityDestroyCamo(Entity entity)
    {
        if (reinforced && !Blocks.OBSIDIAN.defaultBlockState().canEntityDestroy(level(), worldPosition, entity))
        {
            return false;
        }
        return camoContainer.getContent().canEntityDestroy(level(), worldPosition, entity);
    }

    @Override
    public void onLoad()
    {
        onLoadInternal();
        super.onLoad();
    }

    void onLoadInternal()
    {
        if (!level().isClientSide())
        {
            if (recheckStates)
            {
                updateDynamicStates(true, true, true);
            }
            if (forceLightUpdate)
            {
                // Ensure blocks placed by exactly copying BlockState and BlockEntity correctly store their light emission
                doLightUpdate();
            }
        }
    }

    @Override
    public void setBlockState(BlockState state)
    {
        BlockState oldState = getBlockState();
        super.setBlockState(state);
        this.stateCache = state.framedblocks$getCache();
        if (level != null && level.isClientSide() && needsModelDataUpdateAfterStateChange(oldState))
        {
            requestModelDataUpdate();
        }
    }

    protected boolean needsModelDataUpdateAfterStateChange(BlockState oldState)
    {
        IFramedBlock block = getBlock();
        Direction oldOrientation = block.getHorizontalOrientation(oldState);
        Direction newOrientation = block.getHorizontalOrientation(getBlockState());
        return oldOrientation != newOrientation;
    }

    @Override
    public final FramedBlockEntity unwrap()
    {
        return this;
    }

    /*
     * Sync
     */

    @Override
    public final CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        return appendUpdateTag(new CompoundTag(), registries);
    }

    final CompoundTag appendUpdateTag(CompoundTag tag, HolderLookup.Provider registries)
    {
        TagValueOutput valueOutput = new TagValueOutput(ProblemReporter.DISCARDING, registries.createSerializationContext(NbtOps.INSTANCE), tag);
        writeToDataPacket(valueOutput);
        return valueOutput.buildResult();
    }

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public final void handleUpdateTag(ValueInput valueInput)
    {
        NetworkValueInput.handleUpdateTag(this, valueInput);
    }

    @Override
    public final void onDataPacket(Connection net, ValueInput valueInput)
    {
        NetworkValueInput.handleUpdatePacket(this, valueInput);
    }

    protected void writeToDataPacket(ValueOutput valueOutput)
    {
        CamoContainerHelper.writeToNetwork(valueOutput.child(CAMO_NBT_KEY), camoContainer);
        valueOutput.storeNullable(CAMO_DIR_NBT_KEY, FramedCodecs.DIRECTION_BY_INT, camoOrientation);
        valueOutput.storeNullable(OVERLAY_NBT_KEY, BlockOverlay.CODEC, overlay);
        valueOutput.putByte("flags", writeFlags());
    }

    protected void readFromDataPacket(NetworkValueInput input)
    {
        camoContainer = input.readCamo(CAMO_NBT_KEY, false);

        Direction newOrientation = input.read(CAMO_DIR_NBT_KEY, FramedCodecs.DIRECTION_BY_INT).orElse(null);
        if (newOrientation != camoOrientation)
        {
            camoOrientation = newOrientation;
            input.requestRenderUpdate();
        }

        Holder<BlockOverlay> newOverlay = input.read(OVERLAY_NBT_KEY, BlockOverlay.CODEC).orElse(null);
        if (newOverlay != overlay)
        {
            overlay = newOverlay;
            input.requestRenderUpdate();
        }

        byte flags = input.getByteOr("flags", (byte) 0);

        boolean newGlow = readFlag(flags, FLAG_GLOWING);
        if (newGlow != glowing)
        {
            glowing = newGlow;
            input.requestRenderUpdate();
            input.requestLightUpdate();
        }

        boolean newIntangible = readFlag(flags, FLAG_INTANGIBLE);
        if (newIntangible != intangible)
        {
            intangible = newIntangible;
            input.requestRenderUpdate();
            input.requestCullingUpdate();
        }

        boolean newReinforced = readFlag(flags, FLAG_REINFORCED);
        if (newReinforced != reinforced)
        {
            reinforced = newReinforced;
            input.requestRenderUpdate();
        }

        boolean newEmissive = readFlag(flags, FLAG_EMISSIVE);
        if (newEmissive != emissive)
        {
            emissive = newEmissive;
            input.requestRenderUpdate();
        }
    }

    private byte writeFlags()
    {
        byte flags = 0;
        if (glowing) flags |= FLAG_GLOWING;
        if (intangible) flags |= FLAG_INTANGIBLE;
        if (reinforced) flags |= FLAG_REINFORCED;
        if (emissive) flags |= FLAG_EMISSIVE;
        return flags;
    }

    protected static boolean readFlag(byte flags, int flag)
    {
        return (flags & flag) != 0;
    }

    /*
     * Model data
     */

    @Override
    public final ModelData getModelData()
    {
        if (cullStateDirty)
        {
            updateCulling(false, false);
            cullStateDirty = false;
        }
        return getModelData(true, getBlockState());
    }

    /**
     * @param includeCullInfo Whether culling data should be included
     * @param state           The {@link BlockState} with which the model data is used for rendering (usually {@link #getBlockState()})
     */
    @Override
    public final ModelData getModelData(boolean includeCullInfo, BlockState state)
    {
        AbstractFramedBlockData modelData = computeBlockData(state, includeCullInfo);
        ModelData.Builder builder = ModelData.builder().with(AbstractFramedBlockData.PROPERTY, modelData);
        attachAdditionalModelData(builder);
        return builder.build();
    }

    /**
     * @param state           The {@link BlockState} with which the model data is used for rendering (usually {@link #getBlockState()})
     * @param includeCullInfo Whether culling data should be included
     */
    AbstractFramedBlockData computeBlockData(BlockState state, boolean includeCullInfo)
    {
        boolean[] cullData = includeCullInfo ? culledFaces : FramedBlockData.NO_CULLED_FACES;
        return makeBlockData(state, camoContainer, cullData, false);
    }

    final FramedBlockData makeBlockData(BlockState state, CamoContainer<?, ?> camo, boolean[] cullData, boolean secondPart)
    {
        // The view-blocking value is never resolved from the second part, no point in computing it twice
        TriState viewBlocking = secondPart ? TriState.DEFAULT : Utils.toTriState(state.isSuffocating(level(), worldPosition));
        CamoContainer<?, ?> adjustedCamo = adjustCamoOrientation(camo, state, secondPart);
        return new FramedBlockData(state, adjustedCamo, cullData, secondPart, reinforced, emissive, viewBlocking, overlay);
    }

    private CamoContainer<?, ?> adjustCamoOrientation(CamoContainer<?, ?> camo, BlockState state, boolean secondPart)
    {
        Direction camoOrientation = getCamoOrientation(secondPart);
        if (camoOrientation == null) return camo;

        Direction blockOrientation = getBlock().getHorizontalOrientation(state);
        if (blockOrientation == null) return camo;

        Rotation rotation = DirUtils.getRotationBetween(camoOrientation, blockOrientation);
        return camo.adjustForCarrierRotation(rotation);
    }

    protected void attachAdditionalModelData(ModelData.Builder builder) { }

    /*
     * Blueprint handling
     */

    @Override
    public final BlueprintData writeToBlueprint()
    {
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

    CamoList collectCamosForBlueprint()
    {
        return CamoList.of(camoContainer);
    }

    protected BlueprintData appendCustomBlueprintData(BlueprintData blueprintData)
    {
        return blueprintData;
    }

    @Override
    public final void applyBlueprintData(BlueprintData blueprintData)
    {
        applyCamosFromBlueprint(blueprintData);
        setOverlay(blueprintData.overlay().orElse(null));
        setGlowing(blueprintData.glowing());
        setIntangible(blueprintData.intangible());
        setReinforced(blueprintData.reinforced());
        setEmissive(blueprintData.emissive());
        blueprintData.customData().ifPresent(this::applyCustomDataFromBlueprint);
    }

    void applyCamosFromBlueprint(BlueprintData blueprintData)
    {
        setCamo(blueprintData.camos().getCamo(0), false);
    }

    protected void applyCustomDataFromBlueprint(TypedDataComponent<?> auxData) { }

    /*
     * DataComponent handling
     */

    @Override
    public void removeComponentsFromTag(ValueOutput valueOutput)
    {
        valueOutput.discard(CAMO_NBT_KEY);
        valueOutput.discard(CAMO_DIR_NBT_KEY);
        valueOutput.discard(OVERLAY_NBT_KEY);
        valueOutput.discard("glowing");
        valueOutput.discard("intangible");
        valueOutput.discard("reinforced");
        valueOutput.discard("emissive");
        valueOutput.discard("updated");
    }

    @Override
    protected final void collectImplicitComponents(DataComponentMap.Builder builder)
    {
        collectCamoComponents(builder);
        collectMiscComponents(builder);

        FrameConfig.collect(builder, this);

        if (overlay != null)
        {
            builder.set(Utils.DC_TYPE_BLOCK_OVERLAY, overlay);
        }
    }

    protected void collectCamoComponents(DataComponentMap.Builder builder)
    {
        builder.set(Utils.DC_TYPE_CAMO_LIST, CamoList.of(camoContainer));
    }

    protected void collectMiscComponents(DataComponentMap.Builder builder) { }

    @Override
    protected final void applyImplicitComponents(DataComponentGetter input)
    {
        applyCamoComponents(input);
        applyMiscComponents(input);

        input.getOrDefault(Utils.DC_TYPE_FRAME_CONFIG, FrameConfig.DEFAULT).apply(this);
        overlay = input.get(Utils.DC_TYPE_BLOCK_OVERLAY);
    }

    protected void applyCamoComponents(DataComponentGetter input)
    {
        setCamo(input.getOrDefault(Utils.DC_TYPE_CAMO_LIST, CamoList.EMPTY).getCamo(0), false);
    }

    protected void applyMiscComponents(DataComponentGetter input) { }

    /*
     * NBT stuff
     */

    @Override
    public void saveAdditional(ValueOutput valueOutput)
    {
        super.saveAdditional(valueOutput);
        saveAdditionalInternal(valueOutput);
    }

    void saveAdditionalInternal(ValueOutput valueOutput)
    {
        valueOutput.store(CAMO_NBT_KEY, CamoContainerHelper.CODEC, camoContainer);
        valueOutput.storeNullable(CAMO_DIR_NBT_KEY, Direction.CODEC, camoOrientation);
        valueOutput.storeNullable(OVERLAY_NBT_KEY, BlockOverlay.CODEC, overlay);
        valueOutput.putBoolean("glowing", glowing);
        valueOutput.putBoolean("intangible", intangible);
        valueOutput.putBoolean("reinforced", reinforced);
        valueOutput.putBoolean("emissive", emissive);
        valueOutput.putByte("updated", (byte) DATA_VERSION);
    }

    @Override
    public void loadAdditional(ValueInput valueInput)
    {
        super.loadAdditional(valueInput);
        loadAdditionalInternal(valueInput);
    }

    void loadAdditionalInternal(ValueInput valueInput)
    {
        camoContainer = loadAndValidateCamo(valueInput, CAMO_NBT_KEY);
        camoOrientation = valueInput.read(CAMO_DIR_NBT_KEY, Direction.CODEC).orElse(null);
        overlay = valueInput.read(OVERLAY_NBT_KEY, BlockOverlay.CODEC).orElse(null);
        glowing = valueInput.getBooleanOr("glowing", false);
        intangible = valueInput.getBooleanOr("intangible", false);
        reinforced = valueInput.getBooleanOr("reinforced", false);
        emissive = valueInput.getBooleanOr("emissive", false);
        recheckStates |= valueInput.getByteOr("updated", (byte) 0) < DATA_VERSION;

        if (glowing)
        {
            recheckStates = forceLightUpdate = true;
        }
    }

    final CamoContainer<?, ?> loadAndValidateCamo(ValueInput valueInput, String key)
    {
        CamoContainer<?, ?> camo = valueInput.read(key, CamoContainerHelper.CODEC).orElse(EmptyCamoContainer.EMPTY);
        if (!CamoContainerHelper.validateCamo(camo))
        {
            recheckStates = true;
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
}
