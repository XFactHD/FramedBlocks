package io.github.xfacthd.framedblocks.api.block;

import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.cache.StateCache;
import io.github.xfacthd.framedblocks.api.block.item.FramedBlockItem;
import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.block.render.CullingHelper;
import io.github.xfacthd.framedblocks.api.block.render.ParticleHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.api.shapes.ShapeLookup;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.api.util.sound.SoundUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.UnaryOperator;

/// Top-level interface providing all generic block functionality of framed blocks.
/// Must be implemented by all framed blocks.
public interface IFramedBlock extends EntityBlock, IBlockExtension {
    /// ID of the dynamic drop provider used for appending camos to the block's loot.
    Identifier DYNAMIC_DROPS = Utils.id("dynamic_drops");

    /// {@return the block type describing this block}
    ///
    ///  @implNote The value returned by this method must either be constant or originate from a field that is initialized before super
    IBlockType getBlockType();

    /// Apply the default block properties to the given properties of the given block type.
    ///
    /// @param props The properties to modify
    /// @param type  The type of the block the properties will be used for
    /// @return the modified block properties
    static Block.Properties applyDefaultProperties(BlockBehaviour.Properties props, IBlockType type) {
        return applyDefaultProperties(props, type, UnaryOperator.identity());
    }

    /// Apply the default block properties to the given properties of the given block type
    /// and apply the given modifier to the resulting properties.
    ///
    /// @param props         The properties to modify
    /// @param type          The type of the block the properties will be used for
    /// @param propsModifier The modifier to apply after setting the default properties
    /// @return the modified block properties
    static Block.Properties applyDefaultProperties(BlockBehaviour.Properties props, IBlockType type, UnaryOperator<BlockBehaviour.Properties> propsModifier) {
        props.mapColor(MapColor.WOOD)
                .ignitedByLava()
                .instrument(NoteBlockInstrument.BASS)
                .strength(2F)
                .sound(SoundType.WOOD)
                .emissiveRendering(FramedBlockInternals::isEmissiveRendering)
                .isViewBlocking(FramedBlockInternals::isViewBlocking)
                .isSuffocating(FramedBlockInternals::isSuffocating);

        if (!type.canOccludeWithSolidCamo()) {
            props.noOcclusion();
        }

        return propsModifier.apply(props);
    }

    /// Create a [BlockItem] for this block. Must extend [BlockItem] and [IFramedBlockItem].
    ///
    /// @param props The [Item.Properties] to construct the item with
    ///
    /// @return the block item used for placing this block
    default IFramedBlockItem createBlockItem(Item.Properties props) {
        return new FramedBlockItem((Block) this, props);
    }

    /// Create the [StateCycleSpec] to use for cycling through this block's states.
    ///
    /// Blocks which do not have an item or are not the primary block of the [BlockItem] used to place
    /// them should return [StateCycleSpec#UNSUPPORTED].
    ///
    /// @return the state cycle spec for this block
    default StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.NOT_IMPLEMENTED;
    }

    /// Create the [StateCache] holding static metadata of the given state of this block.
    ///
    /// @param state The state to compute the state cache for
    /// @return the state cache for the given state
    @ApiStatus.OverrideOnly
    default StateCache initCache(BlockState state) {
        return new StateCache(state, getBlockType());
    }

    /// {@return the state cache of the given state}
    default StateCache getCache(BlockState state) {
        return state.framedblocks$getCache();
    }

    /// Try applying a camo from the player's off-hand to this block immediately after placement.
    ///
    /// @param level  The level this block is in
    /// @param pos    The position of this block
    /// @param placer The entity which placed this block
    /// @param stack  The stack this block was placed with
    default void tryApplyCamoImmediately(Level level, BlockPos pos, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide() && placer instanceof Player player && player.getMainHandItem() == stack && level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            be.tryApplyCamoImmediately(player);
        }
    }

    /// Handle right-click interactions with this block. Automatically handles shape locking, wrench rotation,
    /// camo application/removal and modifier application/removal.
    ///
    /// @param state  The state of this block
    /// @param level  The level this block is in
    /// @param pos    The position of this block
    /// @param player The player interacting with this blocks
    /// @param hand   The hand used for the interaction
    /// @param hit    The exact location at which this block is being interacted with
    /// @return the result of the interaction
    default InteractionResult handleUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (hand == InteractionHand.MAIN_HAND && this instanceof ShapeLockableBlock block && block.lockState(level, pos, player, heldItem)) {
            return InteractionResult.SUCCESS;
        }

        if (!(level.getBlockEntity(pos) instanceof IFramedBlockEntity be)) {
            return InteractionResult.FAIL;
        }

        if (Utils.isWrenchRotationTool(heldItem)) {
            WrenchRotationMode mode = heldItem.getOrDefault(FramedConstants.Objects.DC_TYPE_WRENCH_MODE, WrenchRotationMode.PRIMARY);
            RotationDirection direction = RotationDirection.of(player.isShiftKeyDown());
            BlockState newState = rotate(state, direction, mode);
            TriState notifyBlockEntity = shouldNotifyBlockEntityOfWrenchRotation(mode, state, newState);
            if (newState != state || notifyBlockEntity == TriState.TRUE) {
                if (!level.isClientSide()) {
                    if (newState != state) {
                        level.setBlockAndUpdate(pos, newState);
                    }
                    if (notifyBlockEntity != TriState.FALSE) {
                        be.applyWrenchRotation(direction.toVanillaRotation(), newState != state);
                    }
                }
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.FAIL;
        }

        return be.handleInteraction(player, hand, hit);
    }

    @Override
    default boolean hasDynamicLightEmission(BlockState state) {
        return state.getValue(FramedProperties.GLOWING);
    }

    @Override
    default int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        if (!state.getValue(FramedProperties.GLOWING)) {
            return 0;
        }
        AuxiliaryLightManager lightManager = level.getAuxLightManager(pos);
        return lightManager != null ? lightManager.getLightAt(pos) : 0;
    }

    /// Append the dynamic drops (camos, modifiers, etc.) of this block to the given loot param builder.
    ///
    /// @param builder The loot param builder to append to
    /// @return the loot param builder
    default LootParams.Builder getCamoDrops(LootParams.Builder builder) {
        if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof IFramedBlockEntity be) {
            boolean dropCamo = ConfigView.Server.INSTANCE.shouldConsumeCamoItem() &&
                    !builder.getParameter(LootContextParams.TOOL).has(FramedConstants.Objects.DC_TYPE_RETAIN_CAMO);
            builder.withDynamicDrop(DYNAMIC_DROPS, consumer ->
                    be.addAdditionalDrops(consumer, dropCamo)
            );
        }
        return builder;
    }

    /// Determine which [BlockState] should be used to retrieve the camo from this block's [IFramedBlockEntity] if the
    /// given [SideSkipPredicate] of the block being occluded succeeds against this block (the occluding block).
    ///
    /// @param predicate      The skip predicate of the block being occluded
    /// @param level          The level the blocks are in
    /// @param pos            The position of the block being occluded
    /// @param occludedState  The block being occluded
    /// @param occludingState The occluding block (the block this method is being called on)
    /// @param side           The side being occluded of the block that is being occluded
    /// @return The state used for camo lookup on the occluding block if the given predicate succeeds, else null
    default @Nullable BlockState runOcclusionTestAndGetLookupState(
            SideSkipPredicate predicate, BlockGetter level, BlockPos pos, BlockState occludedState, BlockState occludingState, Direction side
    ) {
        return predicate.test(level, pos, occludedState, occludingState, side) ? occludingState : null;
    }

    @Override
    default BlockState getAppearance(
            BlockState state,
            BlockAndLightGetter level,
            BlockPos pos,
            Direction side,
            @Nullable BlockState queryState,
            @Nullable BlockPos queryPos
    ) {
        return InternalAPI.INSTANCE.getAppearance(this, state, level, pos, side, queryState, queryPos);
    }

    /// {@return a double block's component located at the given edge on the given side or covering the full face if the
    /// given edge is null}
    ///
    /// @param level The level this block is in
    /// @param pos   The position of this block
    /// @param state The state of this block
    /// @param side  The side being queried
    /// @param edge  The edge being edge or null for the full face
    /// @apiNote Only relevant for double blocks.
    default @Nullable BlockState getComponentAtEdge(
            BlockGetter level, BlockPos pos, BlockState state, Direction side, @Nullable Direction edge
    ) {
        return state;
    }

    /// {@return a double block's component which is occluded by the given neighbor state on the given side}
    ///
    /// @param level    The level the blocks are in
    /// @param pos      The position of this block
    /// @param state    The state of this block
    /// @param adjState The state occluding the given side of this block
    /// @param side     The side being queried
    /// @apiNote Only relevant for double blocks.
    /// @apiNote Only relevant for double blocks.
    default @Nullable BlockState getComponentBySkipPredicate(
            BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side
    ) {
        return state;
    }

    /// {@return whether this block can occlude the given neighbor block}
    ///
    /// @param level    The level the blocks are in
    /// @param pos      The position of this block
    /// @param state    The state of this block
    /// @param adjPos   The position of the block being occluded
    /// @param adjState The state of the block being occluded
    default boolean canOccludeNeighbor(BlockGetter level, BlockPos pos, BlockState state, BlockPos adjPos, BlockState adjState) {
        if (!ConfigView.Server.INSTANCE.enableIntangibility()) {
            return true;
        }
        if (adjState.getBlock() instanceof IFramedBlock adjBlock && adjBlock.isIntangible(adjState, level, adjPos, null)) {
            return true;
        }
        return !isIntangible(state, level, pos, null);
    }

    @Override
    default float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            return be.getCamoFriction(state, entity, state.getBlock().getFriction());
        }
        return state.getBlock().getFriction();
    }

    @Override
    @SuppressWarnings("deprecation")
    default float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            float resistance = be.getCamoExplosionResistance(explosion);
            if (resistance > 0F) {
                return resistance;
            }
        }
        return state.getBlock().getExplosionResistance();
    }

    @Override
    default boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        if (ConfigView.Server.INSTANCE.areBlocksFireproof()) {
            return false;
        }

        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            return be.isCamoFlammable(face);
        }
        return true;
    }

    @Override
    default int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        if (ConfigView.Server.INSTANCE.areBlocksFireproof()) {
            return 0;
        }

        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            int flammability = be.getCamoFlammability(face);
            if (flammability > -1) {
                return flammability;
            }
        }
        return 20;
    }

    @Override
    default int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        if (ConfigView.Server.INSTANCE.areBlocksFireproof()) {
            return 0;
        }

        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            int spreadSpeed = be.getCamoFireSpreadSpeed(face);
            if (spreadSpeed > -1) {
                return spreadSpeed;
            }
        }
        return 5;
    }

    @Override
    default boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction side) {
        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            CamoContent<?> camo = be.getCamo(side, null).getContent();
            return !camo.isEmpty() && camo.getAsBlockState().is(level.dimensionType().infiniburn());
        }
        return false;
    }

    @Override
    default boolean ignitedByLava(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        if (ConfigView.Server.INSTANCE.areBlocksFireproof()) {
            return false;
        }

        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            return be.isCamoIgnitedByLava(side);
        }
        return IBlockExtension.super.ignitedByLava(state, level, pos, side);
    }

    /// Handle left-click interactions with this block. Automatically handles slope face toggling.
    ///
    /// @param state  The state of this block
    /// @param level  The level this block is in
    /// @param pos    The position of this block
    /// @param player The player interacting with this block
    /// @return whether the interaction succeeded
    default boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player) {
        if (this instanceof SlopeToggleBlock) {
            return SlopeToggleBlock.toggleAltSlope(state, level, pos, player);
        }
        if (this instanceof CopycatStyleBlock.StateDependent block) {
            return block.toggleCopycatStyle(state, level, pos, player);
        }
        return false;
    }

    /// {@return whether this block is intangible in the given context}
    ///
    /// @param state The state of this block
    /// @param level The level this block is in
    /// @param pos   The position of this block
    /// @param ctx   The collision context the intangibility is queried in
    default boolean isIntangible(@SuppressWarnings("unused") BlockState state, BlockGetter level, BlockPos pos, @Nullable CollisionContext ctx) {
        if (!ConfigView.Server.INSTANCE.enableIntangibility() || !getBlockType().allowMakingIntangible()) {
            return false;
        }
        return level.getBlockEntity(pos) instanceof IFramedBlockEntity be && be.isIntangible(ctx);
    }

    /// {@return whether this block's occlusion shape should be used for light occlusion}
    ///
    /// @param state The state of this block
    default boolean useCamoOcclusionShapeForLightOcclusion(BlockState state) {
        if (!getBlockType().canOccludeWithSolidCamo()) {
            return false;
        }
        return state.getValueOrElse(FramedProperties.SOLID, false) && !state.getValue(FramedProperties.GLOWING);
    }

    /// {@return the shape to use for occlusion checks against the given state of this block}
    ///
    /// @param state           This state of this block
    /// @param occlusionShapes The [ShapeLookup] to get the shape from if this block uses separate main and occlusion shapes
    default VoxelShape getCamoOcclusionShape(BlockState state, @Nullable ShapeLookup occlusionShapes) {
        if (getBlockType().canOccludeWithSolidCamo() && !state.getValue(FramedProperties.SOLID)) {
            return Shapes.empty();
        }
        if (occlusionShapes != null && occlusionShapes.hasSeparateOcclusionShapes()) {
            return occlusionShapes.getOcclusionShape(state);
        }
        return state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
    }

    /// {@return the visual shape of the given state of this block}
    ///
    /// @param state This state of this block
    /// @param level The level this block is in
    /// @param pos   The position of this block
    /// @param ctx   The context the shape is being queried in
    default VoxelShape getCamoVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (getBlockType().canOccludeWithSolidCamo() && !state.getValue(FramedProperties.SOLID)) {
            return Shapes.empty();
        }
        return state.getCollisionShape(level, pos, ctx);
    }

    /// {@return the shade brightness of the given state of this block}
    ///
    /// @param state    The state of this block
    /// @param level    The level this block is in
    /// @param pos      The position of this block
    /// @param ownShade The shade of this block with no camo applied
    default float getCamoShadeBrightness(@SuppressWarnings("unused") BlockState state, BlockGetter level, BlockPos pos, float ownShade) {
        AbstractFramedBlockData fbData = level.getModelData(pos).get(AbstractFramedBlockData.PROPERTY);
        return fbData != null ? fbData.getCamoShadeBrightness(level, pos, ownShade) : ownShade;
    }

    @Override
    default boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            ParticleHelper.spawnRunningParticles(be.getCamo(), be.getOverlay(), state, level, pos, entity);
            return true;
        }
        return false;
    }

    @Override
    default boolean addLandingEffects(BlockState state, ServerLevel level, BlockPos pos, BlockState sameState, LivingEntity entity, int count) {
        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            ParticleHelper.spawnLandingParticles(be.getCamo(), be.getOverlay(), state, level, pos, entity, count);
            return true;
        }
        return false;
    }

    @Override
    default void playStepSound(BlockState state, Level level, BlockPos pos, Entity entity, float volumeMult, float pitchMult) {
        CamoContainer<?, ?> camo = level.getBlockEntity(pos) instanceof IFramedBlockEntity be ? be.getCamo() : EmptyCamoContainer.EMPTY;
        SoundUtils.playStepSound(entity, camo.getContent().getSoundType(), volumeMult, pitchMult);
    }

    @Override
    default void playFallSound(BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        CamoContainer<?, ?> camo = level.getBlockEntity(pos) instanceof IFramedBlockEntity be ? be.getCamo() : EmptyCamoContainer.EMPTY;
        SoundUtils.playFallSound(entity, camo.getContent().getSoundType());
    }

    @Override
    default boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        return CullingHelper.hidesNeighborFace(this, level, pos, state, adjState, side);
    }

    /// {@return the camo applied to the given side of this block}
    ///
    /// @param state This state of this block
    /// @param level The level this block is in
    /// @param pos   The position of this block
    /// @param side  The side being queried
    default CamoContainer<?, ?> getCamo(BlockGetter level, BlockPos pos, BlockState state, Direction side) {
        AbstractFramedBlockData fbData = level.getModelData(pos).get(AbstractFramedBlockData.PROPERTY);
        return fbData != null ? fbData.unwrap(false).getCamoContainer() : EmptyCamoContainer.EMPTY;
    }

    /// {@return whether the given side of this block covers the full face at the outer perimeter of the block and is fully opaque}
    ///
    /// @param state This state of this block
    /// @param level The level this block is in
    /// @param pos   The position of this block
    /// @param side  The side being queried
    default boolean isSolidSide(BlockGetter level, BlockPos pos, BlockState state, Direction side) {
        if (state.framedblocks$getCache().isFullFace(side)) {
            AbstractFramedBlockData fbData = level.getModelData(pos).get(AbstractFramedBlockData.PROPERTY);
            return fbData != null && fbData.unwrap(false).getCamoContent().isSolid();
        }
        return false;
    }

    @Override
    default void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (level.isClientSide()) {
            if (oldState.getBlock() == newState.getBlock()) {
                updateCulling(level, pos);
            }
            if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
                be.setBlockState(newState);
            }
        }
    }

    /// Trigger an occlusion update on this block.
    ///
    /// @param level The level this block is in
    /// @param pos   The position of this block
    default void updateCulling(LevelReader level, BlockPos pos) {
        if (!level.isClientSide() && level instanceof Level realLevel) {
            InternalAPI.INSTANCE.enqueueCullingUpdate(realLevel, pos);
        } else if (level.isClientSide() && level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            be.updateCulling(true, false);
        }
    }

    /// Handle rotation of this block with a wrench.
    ///
    /// @param state     The state of this block
    /// @param direction The direction this block is being rotated in
    /// @param mode      The rotation mode of the wrench
    /// @return the rotated state of this block
    @SuppressWarnings("deprecation")
    default BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return state.rotate(direction.toVanillaRotation());
    }

    /// Returns whether the rotation applied with the given mode is a rotation around the Y axis, requiring the [BlockEntity]
    /// to be notified of the rotation to adjust the applied camo(s).
    /// - [TriState#FALSE]: [BlockEntity] is not informed of the rotation, regardless of success
    /// - [TriState#DEFAULT]: [BlockEntity] is informed of successful rotations
    /// - [TriState#TRUE]: [BlockEntity] is informed of every rotation attempt, regardless of success
    ///
    /// @param mode     The mode of the wrench this block was rotated with
    /// @param oldState The [BlockState] before rotation
    /// @param newState The [BlockState] after rotation
    /// @return whether the BlockEntity should react to rotating the block with a wrench in the given mode.
    @ApiStatus.OverrideOnly
    default TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        return TriState.FALSE;
    }

    @Override
    default MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor) {
        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            MapColor color = be.getMapColor();
            if (color != null) {
                return color;
            }
        }
        return defaultColor;
    }

    @Override
    default @Nullable Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        if (!doesBlockOccludeBeaconBeam(state, level, pos)) {
            return null;
        }
        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            return be.getCamoBeaconColorMultiplier(level, pos, beaconPos);
        }
        return null;
    }

    /// {@return whether the given [BlockState] occludes the full area of the beacon beam and
    /// can therefore tint the beam}
    ///
    /// @param state The state attempting to occlude the beacon beam
    /// @param level The level this block is in
    /// @param pos   The position of this block
    default boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos) {
        return false;
    }

    @Override
    default TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction side, BlockState plant) {
        if (state.isFaceSturdy(level, pos, side, SupportType.FULL) && level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            return be.canCamoSustainPlant(level, side, plant);
        }
        return TriState.DEFAULT;
    }

    @Override
    default boolean shouldDisplayFluidOverlay(BlockState state, BlockAndLightGetter level, BlockPos pos, FluidState fluid) {
        if (!getBlockType().canOccludeWithSolidCamo()) {
            return false;
        }
        if (!state.getValue(FramedProperties.SOLID) && level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            return be.shouldCamoDisplayFluidOverlay(level, pos, fluid);
        }
        return false;
    }

    @Override
    default boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be && !be.canEntityDestroyCamo(entity)) {
            return false;
        }
        return IBlockExtension.super.canEntityDestroy(state, level, pos, entity);
    }

    /// Create a new [BlockEntity] for this block. BEs returned from this method must implement [IFramedBlockEntity]
    @Override
    BlockEntity newBlockEntity(BlockPos pos, BlockState state);

    /// {@return the state whose block model to reuse for the item or null if the loaded item model should be used}
    @Nullable BlockState getItemModelSource();

    /// {@return the class under which this block should be registered to the Jade BlockComponentProvider to prevent
    ///  duplicate provider attachment for blocks which extend a class that is instantiated for other blocks}
    ///
    /// @apiNote This is only relevant for blocks which do not extend [AbstractFramedBlock]
    default Class<? extends Block> getJadeTargetClass() {
        return ((Block) this).getClass();
    }

    /// {@return whether this block should be rendered as a block or as the item on the Jade tooltip}
    default boolean shouldRenderAsBlockInJadeTooltip() {
        return true;
    }

    /// {@return the state which should be drawn on the Jade tooltip for the given in-world state}
    ///
    /// @param state The in-world state being looked at
    BlockState getJadeRenderState(BlockState state);

    /// {@return the scale value at which this block should be drawn on the Jade tooltip}
    ///
    /// @param state The in-world state being looked at
    default float getJadeRenderScale(BlockState state) {
        return 1F;
    }
}
