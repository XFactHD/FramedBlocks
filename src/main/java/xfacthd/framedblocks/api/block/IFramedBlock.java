package xfacthd.framedblocks.api.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.cache.IStateCacheAccessor;
import xfacthd.framedblocks.api.block.cache.StateCache;
import xfacthd.framedblocks.api.block.render.*;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.internal.InternalAPI;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings({ "deprecation", "unused" })
public interface IFramedBlock extends EntityBlock, IBlockExtension
{
    String LOCK_MESSAGE = Utils.translationKey("msg", "lock_state");
    Component STATE_LOCKED = Utils.translate("msg", "lock_state.locked").withStyle(ChatFormatting.RED);
    Component STATE_UNLOCKED = Utils.translate("msg", "lock_state.unlocked").withStyle(ChatFormatting.GREEN);

    IBlockType getBlockType();

    static Block.Properties createProperties(IBlockType type)
    {
        Block.Properties props = Block.Properties.of()
                .mapColor(MapColor.WOOD)
                .ignitedByLava()
                .instrument(NoteBlockInstrument.BASS)
                .strength(2F)
                .sound(SoundType.WOOD)
                .isViewBlocking(IFramedBlock::isBlockSuffocating)
                .isSuffocating(IFramedBlock::isBlockSuffocating);

        if (!type.canOccludeWithSolidCamo())
        {
            props.noOcclusion();
        }

        return props;
    }

    private static boolean isBlockSuffocating(BlockState state, BlockGetter level, BlockPos pos)
    {
        return ((IFramedBlock) state.getBlock()).isSuffocating(state, level, pos);
    }

    default BlockItem createBlockItem()
    {
        return new BlockItem((Block) this, new Item.Properties());
    }

    @ApiStatus.OverrideOnly
    default StateCache initCache(BlockState state)
    {
        return new StateCache(state, getBlockType());
    }

    default StateCache getCache(BlockState state)
    {
        return ((IStateCacheAccessor) state).framedblocks$getCache();
    }

    default void tryApplyCamoImmediately(Level level, BlockPos pos, @Nullable LivingEntity placer, ItemStack stack)
    {
        if (level.isClientSide())
        {
            return;
        }

        //noinspection ConstantConditions
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag"))
        {
            if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
            {
                be.checkCamoSolid();
            }
            return;
        }

        if (placer instanceof Player player && player.getMainHandItem() == stack)
        {
            ItemStack offhandStack = player.getOffhandItem();
            if (offhandStack.getItem() instanceof BlockItem item)
            {
                if (item.getBlock() instanceof IFramedBlock)
                {
                    return;
                }
            }
            else if (!offhandStack.is(Tags.Items.DUSTS_GLOWSTONE))
            {
                return;
            }

            if (level.getBlockEntity(pos) instanceof FramedBlockEntity be && be.canAutoApplyCamoOnPlacement())
            {
                Vec3 hitVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());
                be.handleInteraction(player, InteractionHand.OFF_HAND, new BlockHitResult(hitVec, Direction.UP, pos, false));
            }
        }
    }

    default InteractionResult handleUse(
            BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        if (getBlockType().canLockState() && hand == InteractionHand.MAIN_HAND && lockState(level, pos, player, player.getItemInHand(hand)))
        {
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        if (player.getItemInHand(hand).is(Utils.WRENCH))
        {
            Rotation rot = player.isShiftKeyDown() ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;
            BlockState newState = rotate(state, hit, rot);
            if (newState != state)
            {
                if (!level.isClientSide())
                {
                    level.setBlockAndUpdate(pos, newState);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
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
    default int getLightEmission(BlockState state, BlockGetter level, BlockPos pos)
    {
        if (!state.getValue(FramedProperties.GLOWING))
        {
            return 0;
        }
        if (InternalAPI.INSTANCE.getBlockEntityForLight(level, pos) instanceof FramedBlockEntity be)
        {
            return be.getLightValue();
        }
        return 0;
    }

    @Override
    default SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            CamoContainer camo = be.getCamo();
            if (!camo.isEmpty())
            {
                return camo.getSoundType();
            }
        }
        return ((Block) this).getSoundType(state);
    }

    default List<ItemStack> getCamoDrops(List<ItemStack> drops, LootParams.Builder builder)
    {
        if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof FramedBlockEntity be)
        {
            be.addAdditionalDrops(drops, ConfigView.Server.INSTANCE.shouldConsumeCamoItem());
        }
        return drops;
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

    default boolean shouldPreventNeighborCulling(
            BlockGetter level, BlockPos pos, BlockState state, BlockPos adjPos, BlockState adjState
    )
    {
        if (!ConfigView.Server.INSTANCE.enableIntangibility() || isIntangible(adjState, level, adjPos, null))
        {
            return false;
        }

        if (getBlockType().allowMakingIntangible())
        {
            return isIntangible(state, level, pos, null);
        }
        return false;
    }

    @Override
    default float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.getCamoFriction(state, entity);
        }
        return state.getBlock().getFriction();
    }

    @Override
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
        if (ConfigView.Common.INSTANCE.areBlocksFireproof())
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
        if (ConfigView.Common.INSTANCE.areBlocksFireproof())
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
        if (ConfigView.Common.INSTANCE.areBlocksFireproof())
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

    default boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return false;
    }

    default boolean isIntangible(BlockState state, BlockGetter level, BlockPos pos, @Nullable CollisionContext ctx)
    {
        if (!ConfigView.Server.INSTANCE.enableIntangibility() || !getBlockType().allowMakingIntangible())
        {
            return false;
        }
        return level.getBlockEntity(pos) instanceof FramedBlockEntity be && be.isIntangible(ctx);
    }

    default boolean isSuffocating(BlockState state, BlockGetter level, BlockPos pos)
    {
        if (ConfigView.Server.INSTANCE.enableIntangibility() && getBlockType().allowMakingIntangible())
        {
            // The given BlockPos may be a neighboring block due to how Entity#isInWall() calls this
            BlockState stateAtPos = level.getBlockState(pos);
            if (state != stateAtPos || isIntangible(state, level, pos, null))
            {
                return false;
            }
        }

        // Copy of the default suffocation check
        return state.blocksMotion() && state.isCollisionShapeFullBlock(level, pos);
    }

    default boolean useCamoOcclusionShapeForLightOcclusion(BlockState state)
    {
        if (getBlockType() != null && !getBlockType().canOccludeWithSolidCamo())
        {
            return false;
        }
        return Utils.tryGetValue(state, FramedProperties.SOLID, false) && !state.getValue(FramedProperties.GLOWING);
    }

    default VoxelShape getCamoOcclusionShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        if (getBlockType().canOccludeWithSolidCamo() && !state.getValue(FramedProperties.SOLID))
        {
            return Shapes.empty();
        }
        return state.getShape(level, pos);
    }

    default VoxelShape getCamoVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        if (getBlockType().canOccludeWithSolidCamo() && !state.getValue(FramedProperties.SOLID))
        {
            return Shapes.empty();
        }
        return state.getCollisionShape(level, pos, ctx);
    }

    default float getCamoShadeBrightness(BlockState state, BlockGetter level, BlockPos pos, float ownShade)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.getCamoShadeBrightness(ownShade);
        }
        return ownShade;
    }

    @Override
    default boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            ParticleHelper.spawnRunningParticles(be.getCamo().getState(), level, pos, entity);
            return true;
        }
        return false;
    }

    @Override
    default boolean addLandingEffects(
            BlockState state, ServerLevel level, BlockPos pos, BlockState sameState, LivingEntity entity, int count
    )
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            ParticleHelper.spawnLandingParticles(be.getCamo().getState(), level, pos, entity, count);
            return true;
        }
        return false;
    }

    @Override
    default boolean hidesNeighborFace(
            BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side
    )
    {
        return CullingHelper.hidesNeighborFace(this, level, pos, state, adjState, side);
    }

    @Override
    default void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState)
    {
        if (needCullingUpdateAfterStateChange(level, oldState, newState))
        {
            updateCulling(level, pos);
        }
        if (level.isClientSide() && level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            be.setBlockState(newState);
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

    @SuppressWarnings("RedundantIfStatement")
    default boolean needCullingUpdateAfterStateChange(LevelReader level, BlockState oldState, BlockState newState)
    {
        if (!level.isClientSide() || oldState.getBlock() != newState.getBlock())
        {
            return false;
        }

        // Camo-based BlockState property changes should not update culling because the BlockEntity handles these data changes
        if (getBlockType().canOccludeWithSolidCamo())
        {
            if (oldState.setValue(FramedProperties.SOLID, !oldState.getValue(FramedProperties.SOLID)) == newState)
            {
                return false;
            }
        }

        if (oldState.setValue(FramedProperties.GLOWING, !oldState.getValue(FramedProperties.GLOWING)) == newState)
        {
            return false;
        }

        return true;
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
            BlockState state, LevelAccessor level, BlockPos pos, Supplier<BlockState> updateShape
    )
    {
        if (!state.getValue(FramedProperties.STATE_LOCKED))
        {
            return updateShape.get();
        }

        if (getBlockType().supportsWaterLogging() && state.getValue(BlockStateProperties.WATERLOGGED))
        {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state;
    }

    default BlockState rotate(BlockState state, BlockHitResult hit, Rotation rot)
    {
        return rotate(state, hit.getDirection(), rot);
    }

    default BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        return state.rotate(rot);
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
    default float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos)
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

    // Can't be replaced with an override for the IForgeBlock method due to that being overridden in a Block patch
    default boolean canCamoSustainPlant(
            BlockState state, BlockGetter level, BlockPos pos, Direction side, IPlantable plant
    )
    {
        if (state.isFaceSturdy(level, pos, side, SupportType.FULL) && level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.canCamoSustainPlant(side, plant);
        }
        return false;
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
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            if (be.doesCamoPreventDestructionByEntity(entity))
            {
                return false;
            }
        }
        return IBlockExtension.super.canEntityDestroy(state, level, pos, entity);
    }

    @Override
    default BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedBlockEntity(pos, state);
    }

    default Optional<MutableComponent> printCamoBlock(CompoundTag beTag)
    {
        BlockState camoState = CamoContainer.load(beTag.getCompound("camo")).getState();

        if (camoState.isAir())
        {
            return Optional.empty();
        }
        return Optional.of(camoState.getBlock().getName().withStyle(ChatFormatting.WHITE));
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
}
