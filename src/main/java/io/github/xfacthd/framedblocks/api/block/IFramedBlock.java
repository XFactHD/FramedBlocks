package io.github.xfacthd.framedblocks.api.block;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.cache.StateCache;
import io.github.xfacthd.framedblocks.api.block.item.FramedBlockItem;
import io.github.xfacthd.framedblocks.api.block.render.CullingHelper;
import io.github.xfacthd.framedblocks.api.block.render.ParticleHelper;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.api.shapes.ShapeLookup;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.api.util.sound.SoundUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
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

public interface IFramedBlock extends EntityBlock, IBlockExtension
{
    String LOCK_MESSAGE = Utils.translationKey("msg", "lock_state");
    Component STATE_LOCKED = Utils.translate("msg", "lock_state.locked").withStyle(ChatFormatting.RED);
    Component STATE_UNLOCKED = Utils.translate("msg", "lock_state.unlocked").withStyle(ChatFormatting.GREEN);
    String CAMO_LABEL = Utils.translationKey("desc", "block.stored_camo");
    String CAMO_LABEL_MULTI = Utils.translationKey("desc", "block.stored_camo_multi");
    Identifier DYNAMIC_DROPS = Utils.id("dynamic_drops");

    IBlockType getBlockType();

    static Block.Properties applyDefaultProperties(BlockBehaviour.Properties props, IBlockType type)
    {
        props.mapColor(MapColor.WOOD)
                .ignitedByLava()
                .instrument(NoteBlockInstrument.BASS)
                .strength(2F)
                .sound(SoundType.WOOD)
                .emissiveRendering(FramedBlockInternals::isEmissiveRendering)
                .isViewBlocking(FramedBlockInternals::isViewBlocking)
                .isSuffocating(FramedBlockInternals::isSuffocating);

        if (!type.canOccludeWithSolidCamo())
        {
            props.noOcclusion();
        }

        return props;
    }

    default BlockItem createBlockItem(Item.Properties props)
    {
        return new FramedBlockItem((Block) this, props);
    }

    @ApiStatus.OverrideOnly
    default StateCache initCache(BlockState state)
    {
        return new StateCache(state, getBlockType());
    }

    default StateCache getCache(BlockState state)
    {
        return state.framedblocks$getCache();
    }

    default void tryApplyCamoImmediately(Level level, BlockPos pos, @Nullable LivingEntity placer, ItemStack stack)
    {
        if (level.isClientSide())
        {
            return;
        }

        //noinspection ConstantConditions
        if (stack.get(DataComponents.BLOCK_ENTITY_DATA) != null)
        {
            if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
            {
                be.checkCamoSolid();
            }
            return;
        }

        if (placer instanceof Player player && player.getMainHandItem() == stack && level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            be.tryApplyCamoImmediately(player);
        }
    }

    default InteractionResult handleUse(
            BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        ItemStack heldItem = player.getItemInHand(hand);
        if (getBlockType().canLockState() && hand == InteractionHand.MAIN_HAND && lockState(level, pos, player, heldItem))
        {
            return InteractionResult.SUCCESS;
        }

        if (Utils.isWrenchRotationTool(heldItem))
        {
            WrenchRotationMode mode = heldItem.getOrDefault(Utils.DC_TYPE_WRENCH_MODE, WrenchRotationMode.PRIMARY);
            BlockState newState = rotate(state, RotationDirection.of(player.isShiftKeyDown()), mode);
            if (newState != state)
            {
                if (!level.isClientSide())
                {
                    level.setBlockAndUpdate(pos, newState);
                }
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.FAIL;
        }

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.handleInteraction(player, hand, hit);
        }
        return InteractionResult.FAIL;
    }

    @Override
    default boolean hasDynamicLightEmission(BlockState state)
    {
        return state.getValue(FramedProperties.GLOWING);
    }

    @Override
    default int getLightEmission(BlockState state, BlockGetter level, BlockPos pos)
    {
        if (!state.getValue(FramedProperties.GLOWING))
        {
            return 0;
        }
        AuxiliaryLightManager lightManager = level.getAuxLightManager(pos);
        if (lightManager != null)
        {
            return lightManager.getLightAt(pos);
        }
        return 0;
    }

    default LootParams.Builder getCamoDrops(LootParams.Builder builder)
    {
        if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof FramedBlockEntity be)
        {
            builder.withDynamicDrop(DYNAMIC_DROPS, consumer ->
                    be.addAdditionalDrops(consumer, ConfigView.Server.INSTANCE.shouldConsumeCamoItem())
            );
        }
        return builder;
    }

    /**
     * Called on the occluding block to determine which {@link BlockState} should be used to retrieve the camo from its
     * {@link FramedBlockEntity} if the given {@link SideSkipPredicate} of the block being occluded succeeds
     * @param pred The skip predicate of the block being occluded
     * @param level The level the blocks are in
     * @param pos The position of the block being occluded
     * @param state The block being occluded
     * @param adjState The occluding block (the block this method is being called on)
     * @param side The side being occluded of the block that is being occluded
     * @return The state used for camo lookup on the occluding block if the given predicate succeeds, else null
     */
    @Nullable
    default BlockState runOcclusionTestAndGetLookupState(
            SideSkipPredicate pred, BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side
    )
    {
        if (pred.test(level, pos, state, adjState, side))
        {
            return adjState;
        }
        return null;
    }

    @Override
    default BlockState getAppearance(
            BlockState state,
            BlockAndTintGetter level,
            BlockPos pos,
            Direction side,
            @Nullable BlockState queryState,
            @Nullable BlockPos queryPos
    )
    {
        return InternalAPI.INSTANCE.getAppearance(this, state, level, pos, side, queryState, queryPos);
    }

    /**
     * Get a double block's component located at the given edge on the given side or covering the full face if the
     * given edge is null. Only relevant for double blocks
     */
    @Nullable
    default BlockState getComponentAtEdge(
            BlockGetter level, BlockPos pos, BlockState state, Direction side, @Nullable Direction edge
    )
    {
        return state;
    }

    /**
     * Get a double block's component which is occluded by the given neighbor state on the given side.
     * Only relevant for double blocks
     */
    @Nullable
    default BlockState getComponentBySkipPredicate(
            BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction side
    )
    {
        return state;
    }

    default boolean canOccludeNeighbor(BlockGetter level, BlockPos pos, BlockState state, BlockPos adjPos, BlockState adjState)
    {
        if (!ConfigView.Server.INSTANCE.enableIntangibility())
        {
            return true;
        }
        if (adjState.getBlock() instanceof IFramedBlock adjBlock && adjBlock.isIntangible(adjState, level, adjPos, null))
        {
            return true;
        }
        return !isIntangible(state, level, pos, null);
    }

    @Override
    default float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.getCamoFriction(state, entity, state.getBlock().getFriction());
        }
        return state.getBlock().getFriction();
    }

    @Override
    @SuppressWarnings("deprecation")
    default float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            float resistance = be.getCamoExplosionResistance(explosion);
            if (resistance > 0F)
            {
                return resistance;
            }
        }
        return state.getBlock().getExplosionResistance();
    }

    @Override
    default boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction face)
    {
        if (ConfigView.Server.INSTANCE.areBlocksFireproof())
        {
            return false;
        }

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.isCamoFlammable(face);
        }
        return true;
    }

    @Override
    default int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face)
    {
        if (ConfigView.Server.INSTANCE.areBlocksFireproof())
        {
            return 0;
        }

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            int flammability = be.getCamoFlammability(face);
            if (flammability > -1)
            {
                return flammability;
            }
        }
        return 20;
    }

    @Override
    default int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face)
    {
        if (ConfigView.Server.INSTANCE.areBlocksFireproof())
        {
            return 0;
        }

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            int spreadSpeed = be.getCamoFireSpreadSpeed(face);
            if (spreadSpeed > -1)
            {
                return spreadSpeed;
            }
        }
        return 5;
    }

    @Override
    default boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction side)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            CamoContent<?> camo = be.getCamo(side).getContent();
            return !camo.isEmpty() && camo.getAsBlockState().is(level.dimensionType().infiniburn());
        }
        return false;
    }

    @Override
    default boolean ignitedByLava(BlockState state, BlockGetter level, BlockPos pos, Direction side)
    {
        if (ConfigView.Server.INSTANCE.areBlocksFireproof())
        {
            return false;
        }

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.isCamoIgnitedByLava(side);
        }
        return IBlockExtension.super.ignitedByLava(state, level, pos, side);
    }

    default boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return false;
    }

    default boolean isIntangible(@SuppressWarnings("unused") BlockState state, BlockGetter level, BlockPos pos, @Nullable CollisionContext ctx)
    {
        if (!ConfigView.Server.INSTANCE.enableIntangibility() || !getBlockType().allowMakingIntangible())
        {
            return false;
        }
        return level.getBlockEntity(pos) instanceof FramedBlockEntity be && be.isIntangible(ctx);
    }

    default boolean useCamoOcclusionShapeForLightOcclusion(BlockState state)
    {
        //noinspection ConstantValue
        if (getBlockType() != null && !getBlockType().canOccludeWithSolidCamo())
        {
            return false;
        }
        return state.getValueOrElse(FramedProperties.SOLID, false) && !state.getValue(FramedProperties.GLOWING);
    }

    /**
     * {@return the shape to use for occlusion checks}
     * @param state This block's state
     * @param occlusionShapes The {@link ShapeLookup} to get the shape from if this block uses separate main and occlusion shapes
     */
    default VoxelShape getCamoOcclusionShape(BlockState state, @Nullable ShapeLookup occlusionShapes)
    {
        if (getBlockType().canOccludeWithSolidCamo() && !state.getValue(FramedProperties.SOLID))
        {
            return Shapes.empty();
        }
        if (occlusionShapes != null && occlusionShapes.hasSeparateOcclusionShapes())
        {
            return occlusionShapes.getOcclusionShape(state);
        }
        return state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
    }

    default VoxelShape getCamoVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        if (getBlockType().canOccludeWithSolidCamo() && !state.getValue(FramedProperties.SOLID))
        {
            return Shapes.empty();
        }
        return state.getCollisionShape(level, pos, ctx);
    }

    default float getCamoShadeBrightness(@SuppressWarnings("unused") BlockState state, BlockGetter level, BlockPos pos, float ownShade)
    {
        AbstractFramedBlockData fbData = level.getModelData(pos).get(AbstractFramedBlockData.PROPERTY);
        return fbData != null ? fbData.getCamoShadeBrightness(level, pos, ownShade) : ownShade;
    }

    @Override
    default boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            ParticleHelper.spawnRunningParticles(be.getCamo(), be.getOverlay(), state, level, pos, entity);
            return true;
        }
        return false;
    }

    @Override
    default boolean addLandingEffects(BlockState state, ServerLevel level, BlockPos pos, BlockState sameState, LivingEntity entity, int count)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            ParticleHelper.spawnLandingParticles(be.getCamo(), be.getOverlay(), state, level, pos, entity, count);
            return true;
        }
        return false;
    }

    @Override
    default void playStepSound(BlockState state, Level level, BlockPos pos, Entity entity, float volumeMult, float pitchMult)
    {
        CamoContainer<?, ?> camo = level.getBlockEntity(pos) instanceof FramedBlockEntity be ? be.getCamo() : EmptyCamoContainer.EMPTY;
        SoundUtils.playStepSound(entity, camo.getContent().getSoundType(), volumeMult, pitchMult);
    }

    @Override
    default void playFallSound(BlockState state, Level level, BlockPos pos, LivingEntity entity)
    {
        CamoContainer<?, ?> camo = level.getBlockEntity(pos) instanceof FramedBlockEntity be ? be.getCamo() : EmptyCamoContainer.EMPTY;
        SoundUtils.playFallSound(entity, camo.getContent().getSoundType());
    }

    @Override
    default boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        return CullingHelper.hidesNeighborFace(this, level, pos, state, adjState, side);
    }

    default CamoContainer<?, ?> getCamo(BlockGetter level, BlockPos pos, BlockState state, Direction side)
    {
        AbstractFramedBlockData fbData = level.getModelData(pos).get(AbstractFramedBlockData.PROPERTY);
        return fbData != null ? fbData.unwrap(false).getCamoContainer() : EmptyCamoContainer.EMPTY;
    }

    default boolean isSolidSide(BlockGetter level, BlockPos pos, BlockState state, Direction side)
    {
        if (state.framedblocks$getCache().isFullFace(side))
        {
            AbstractFramedBlockData fbData = level.getModelData(pos).get(AbstractFramedBlockData.PROPERTY);
            return fbData != null && fbData.unwrap(false).getCamoContent().isSolid();
        }
        return false;
    }

    @Override
    default void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState)
    {
        if (level.isClientSide())
        {
            if (oldState.getBlock() == newState.getBlock())
            {
                updateCulling(level, pos);
            }
            if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
            {
                be.setBlockState(newState);
            }
        }
    }

    default void updateCulling(LevelReader level, BlockPos pos)
    {
        if (!level.isClientSide() && level instanceof Level realLevel)
        {
            InternalAPI.INSTANCE.enqueueCullingUpdate(realLevel, pos);
        }
        else if (level.isClientSide() && level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            be.updateCulling(true, false);
        }
    }

    default boolean lockState(Level level, BlockPos pos, Player player, ItemStack stack)
    {
        if (stack.getItem() != Utils.FRAMED_KEY.value())
        {
            return false;
        }

        if (!level.isClientSide())
        {
            BlockState state = level.getBlockState(pos);
            boolean locked = state.getValue(FramedProperties.STATE_LOCKED);
            player.displayClientMessage(Component.translatable(LOCK_MESSAGE, locked ? STATE_UNLOCKED : STATE_LOCKED), true);

            level.setBlockAndUpdate(pos, state.cycle(FramedProperties.STATE_LOCKED));
        }
        return true;
    }

    default BlockState updateShapeLockable(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess tickAccess,
            BlockPos pos,
            Direction side,
            BlockPos adjPos,
            BlockState adjState,
            RandomSource random,
            UpdateShapeHandler updateShape
    )
    {
        if (!state.getValue(FramedProperties.STATE_LOCKED))
        {
            return updateShape.handle(state, level, tickAccess, pos, side, adjPos, adjState, random);
        }
        if (getBlockType().supportsWaterLogging() && state.getValue(BlockStateProperties.WATERLOGGED))
        {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state;
    }

    @SuppressWarnings("deprecation")
    default BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        return state.rotate(direction.toVanillaRotation());
    }

    @Override
    default MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            MapColor color = be.getMapColor();
            if (color != null)
            {
                return color;
            }
        }
        return defaultColor;
    }

    @Override
    @Nullable
    default Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        if (!doesBlockOccludeBeaconBeam(state, level, pos))
        {
            return null;
        }
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.getCamoBeaconColorMultiplier(level, pos, beaconPos);
        }
        return null;
    }

    default boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        return false;
    }

    @Override
    default TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction side, BlockState plant)
    {
        if (state.isFaceSturdy(level, pos, side, SupportType.FULL) && level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.canCamoSustainPlant(level, side, plant);
        }
        return TriState.DEFAULT;
    }

    @Override
    default boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluid)
    {
        if (!getBlockType().canOccludeWithSolidCamo())
        {
            return false;
        }
        if (!state.getValue(FramedProperties.SOLID) && level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.shouldCamoDisplayFluidOverlay(level, pos, fluid);
        }
        return false;
    }

    @Override
    default boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be && !be.canEntityDestroyCamo(entity))
        {
            return false;
        }
        return IBlockExtension.super.canEntityDestroy(state, level, pos, entity);
    }

    @Override
    FramedBlockEntity newBlockEntity(BlockPos pos, BlockState state);

    default CamoList getCamosFromBlueprint(BlueprintData blueprintData)
    {
        return blueprintData.camos();
    }

    static boolean toggleYSlope(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (player.getMainHandItem().getItem() == Utils.FRAMED_WRENCH.value())
        {
            level.setBlockAndUpdate(pos, state.setValue(FramedProperties.Y_SLOPE, !state.getValue(FramedProperties.Y_SLOPE)));
            return true;
        }
        return false;
    }

    /**
     * {@return the state whose block model to reuse for the item or null if the loaded item model should be used}
     */
    @Nullable
    BlockState getItemModelSource();

    /**
     * {@return the horizontal orientation of the given state to adjust the camo rotation to the block's rotation or {@code null} if not applicable}
     * <p>
     * This method should either always or never return {@code null}. Special cases:
     * <ul>
     *     <li>Blocks with a {@link Direction.Axis} property as primary orientation should return {@link Utils#getHorizontalDirection(Direction.Axis)}</li>
     *     <li>Blocks with a {@link Direction} property including vertical directions should return {@link Direction#NORTH} for vertical directions</li>
     *     <li>Blocks which have a conditional orientation (i.e. one-way window) should always return {@code null}</li>
     * </ul>
     */
    @Nullable
    Direction getHorizontalOrientation(BlockState state);

    /**
     * {@return the class under which this block should be registered to the Jade BlockComponentProvider to prevent
     * duplicate provider attachment for blocks which extend a class that is instantiated for other blocks}
     * @apiNote This is only relevant for blocks which do not extend {@link AbstractFramedBlock}
     */
    default Class<? extends Block> getJadeTargetClass()
    {
        return ((Block) this).getClass();
    }

    /**
     * {@return whether this block should be rendered as a block or as the item on the Jade tooltip}
     */
    default boolean shouldRenderAsBlockInJadeTooltip()
    {
        return true;
    }

    /**
     * {@return the state which should be drawn on the Jade tooltip for the given in-world state}
     */
    BlockState getJadeRenderState(BlockState state);

    /**
     * {@return the scale value at which this block should be drawn on the Jade tooltip}
     */
    default float getJadeRenderScale(BlockState state)
    {
        return 1F;
    }

    @FunctionalInterface
    interface UpdateShapeHandler
    {
        BlockState handle(
                BlockState state,
                LevelReader level,
                ScheduledTickAccess tickAccess,
                BlockPos pos,
                Direction side,
                BlockPos adjPos,
                BlockState adjState,
                RandomSource random
        );
    }
}
