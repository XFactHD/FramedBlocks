package xfacthd.framedblocks.api.block;

import com.google.common.base.Preconditions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * The {@link team.chisel.ctm.api.IFacade} interface is not implemented directly and is instead hacked on
 * via a mixin to allow CTM to be an optional dependency :/
 */
@SuppressWarnings({ "deprecation", "unused" })
public interface IFramedBlock extends EntityBlock//, IFacade
{
    String LOCK_MESSAGE = "msg." + FramedConstants.MOD_ID + ".lock_state";
    Component STATE_LOCKED = Utils.translate("msg", "lock_state.locked").withStyle(ChatFormatting.RED);
    Component STATE_UNLOCKED = Utils.translate("msg", "lock_state.unlocked").withStyle(ChatFormatting.GREEN);

    IBlockType getBlockType();

    /** @deprecated Use overload with {@link IBlockType} parameter instead */
    @Deprecated(forRemoval = true)
    static Block.Properties createProperties()
    {
        return Block.Properties.of(Material.WOOD)
                .noOcclusion()
                .strength(2F)
                .sound(SoundType.WOOD)
                .isViewBlocking(IFramedBlock::isBlockSuffocating)
                .isSuffocating(IFramedBlock::isBlockSuffocating);
    }

    static Block.Properties createProperties(IBlockType type)
    {
        return createProperties(type, Material.WOOD);
    }

    static Block.Properties createProperties(IBlockType type, Material material)
    {
        Block.Properties props = Block.Properties.of(material)
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

    default BlockItem createItemBlock()
    {
        Block block = (Block)this;
        BlockItem item = new BlockItem(block, new Item.Properties().tab(FramedBlocksAPI.getInstance().defaultCreativeTab()));
        //noinspection ConstantConditions
        item.setRegistryName(block.getRegistryName());
        return item;
    }

    default void tryApplyCamoImmediately(Level level, BlockPos pos, @Nullable LivingEntity placer, ItemStack stack)
    {
        if (level.isClientSide()) { return; }

        //noinspection ConstantConditions
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag"))
        {
            if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
            {
                be.checkCamoSolid();
            }
            return;
        }

        if (placer instanceof Player player)
        {
            if (player.getMainHandItem() != stack) { return; }

            ItemStack otherStack = player.getOffhandItem();
            if (otherStack.getItem() instanceof BlockItem item && !(item.getBlock() instanceof IFramedBlock))
            {
                if (level.getBlockEntity(pos) instanceof FramedBlockEntity be && !FramedBlocksAPI.getInstance().isFramedDoubleBlockEntity(be))
                {
                    Vec3 hitVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());
                    be.handleInteraction(player, InteractionHand.OFF_HAND, new BlockHitResult(hitVec, Direction.UP, pos, false));
                }
            }
        }
    }

    default InteractionResult handleUse(Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (getBlockType().canLockState() && hand == InteractionHand.MAIN_HAND && lockState(level, pos, player, player.getItemInHand(hand)))
        {
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        if (player.getItemInHand(hand).is(Utils.WRENCH))
        {
            BlockState state = level.getBlockState(pos);
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

    default int getLight(BlockGetter level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.getLightValue();
        }
        return 0;
    }

    default SoundType getCamoSound(BlockState state, LevelReader level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            BlockState camoState = be.getCamoState();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }
        }
        return ((Block)this).getSoundType(state);
    }

    default List<ItemStack> getCamoDrops(List<ItemStack> drops, LootContext.Builder builder)
    {
        if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof FramedBlockEntity be)
        {
            be.addCamoDrops(drops);
        }

        return drops;
    }

    default CtmPredicate getCtmPredicate() { return getBlockType().getCtmPredicate(); }

    /**
     * This method is overriden from {@link team.chisel.ctm.api.IFacade}. To allow CTM to be an optional dependency,
     * the interface is not implemented directly and is instead hacked on via a mixin :/
     */
    @Nonnull
    //@Override
    @SuppressWarnings("unused")
    @Deprecated
    default BlockState getFacade(@Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nullable Direction side)
    {
        return Blocks.AIR.defaultBlockState();
    }

    /**
     * This method is overriden from {@link team.chisel.ctm.api.IFacade}. To allow CTM to be an optional dependency,
     * the interface is not implemented directly and is instead hacked on via a mixin :/
     */
    @Nonnull
    //@Override
    @SuppressWarnings("unused")
    default BlockState getFacade(@Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nullable Direction side, @Nonnull BlockPos connection)
    {
        BlockState state = level.getBlockState(pos);
        if (getCtmPredicate().test(state, side) && level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.getCamoState();
        }
        return Blocks.AIR.defaultBlockState();
    }

    default boolean isSideHidden(BlockGetter level, BlockPos pos, BlockState state, Direction side)
    {
        if (level == null) { return false; } //Block had no camo when loaded => level in data not set

        BlockPos neighborPos = pos.relative(side);
        BlockState neighborState = level.getBlockState(neighborPos);

        if (FramedBlocksAPI.getInstance().enableIntangibility() && !isIntangible(state, level, pos, null))
        {
            if (neighborState.getBlock() instanceof IFramedBlock block && block.getBlockType().allowMakingIntangible())
            {
                if (block.isIntangible(neighborState, level, neighborPos, null))
                {
                    return false;
                }
            }
        }

        //Let the game handle culling against solid surfaces automatically
        if (neighborState.isSolidRender(level, neighborPos))
        {
            return false;
        }

        SideSkipPredicate pred = FramedBlocksAPI.getInstance().detailedCullingEnabled() ? getBlockType().getSideSkipPredicate() : SideSkipPredicate.CTM;
        return pred.test(level, pos, state, neighborState, side);
    }

    default float getCamoSlipperiness(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            BlockState camoState = be.getCamoState(Direction.UP);
            if (!camoState.isAir())
            {
                return camoState.getFriction(level, pos, entity);
            }
        }
        return state.getBlock().getFriction();
    }

    default float getCamoExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion)
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

    default boolean isCamoFlammable(BlockGetter level, BlockPos pos, Direction face)
    {
        if (FramedBlocksAPI.getInstance().areBlocksFireproof()) { return false; }

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.isCamoFlammable(face);
        }
        return true;
    }

    default int getCamoFlammability(BlockGetter level, BlockPos pos, Direction face)
    {
        if (FramedBlocksAPI.getInstance().areBlocksFireproof()) { return 0; }

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

    default boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player) { return false; }

    default boolean isIntangible(BlockState state, BlockGetter level, BlockPos pos, @Nullable CollisionContext ctx)
    {
        if (!FramedBlocksAPI.getInstance().enableIntangibility() || !getBlockType().allowMakingIntangible()) { return false; }
        return level.getBlockEntity(pos) instanceof FramedBlockEntity be && be.isIntangible(ctx);
    }

    default boolean isSuffocating(BlockState state, BlockGetter level, BlockPos pos)
    {
        if (FramedBlocksAPI.getInstance().enableIntangibility() && getBlockType().allowMakingIntangible())
        {
            // The given BlockPos may be a neighboring block due to how Entity#isInWall() calls this
            BlockState stateAtPos = level.getBlockState(pos);
            if (state != stateAtPos || isIntangible(state, level, pos, null))
            {
                return false;
            }
        }

        // Copy of the default suffocation check
        return state.getMaterial().blocksMotion() && state.isCollisionShapeFullBlock(level, pos);
    }

    default boolean useCamoOcclusionShapeForLightOcclusion(BlockState state)
    {
        if (getBlockType() != null && !getBlockType().canOccludeWithSolidCamo()) { return false; }

        return state.hasProperty(FramedProperties.SOLID) && state.getValue(FramedProperties.SOLID) && !state.getValue(FramedProperties.GLOWING);
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

    default boolean doesHideNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir)
    {
        if (!FramedBlocksAPI.getInstance().canHideNeighborFaceInLevel(level)) { return false; }
        if (neighborState.getBlock() instanceof IFramedBlock) { return false; }

        if (level.getExistingBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            if (neighborState.getBlock() instanceof HalfTransparentBlock && SideSkipPredicate.CTM.test(level, pos, state, neighborState, dir))
            {
                return true;
            }
            return be.isSolidSide(dir) && !be.isIntangible(null);
        }
        return false;
    }

    default void onStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState)
    {
        if (level.isClientSide() && level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            onStateChangeClient(level, pos, oldState, newState, be);
        }
    }

    default void onStateChangeClient(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState, FramedBlockEntity be)
    {
        be.setBlockState(newState);
        if (needCullingUpdateAfterStateChange(level, oldState, newState))
        {
            be.updateCulling(true, false);
        }
    }

    default void updateCulling(LevelAccessor level, BlockPos pos, @Nullable BlockState neighborState, @Nullable Direction side, boolean rerender)
    {
        Preconditions.checkArgument(side == null || neighborState != null, "Neighbor BlockState cannot be null when a side is provided");
        if (level.isClientSide() && (side == null || !(neighborState.getBlock() instanceof IFramedBlock)) && level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            if (side != null)
            {
                be.updateCulling(side, rerender);
            }
            else
            {
                be.updateCulling(true, rerender);
            }
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    default boolean needCullingUpdateAfterStateChange(LevelReader level, BlockState oldState, BlockState newState)
    {
        if (!level.isClientSide() || oldState.getBlock() != newState.getBlock() || oldState == newState) { return false; }

        // Camo-based BlockState property changes should not update culling because the BlockEntity handles these data changes
        if (getBlockType().canOccludeWithSolidCamo())
        {
            if (oldState.setValue(FramedProperties.SOLID, !oldState.getValue(FramedProperties.SOLID)) == newState)
            {
                return false;
            }

            if (oldState.setValue(FramedProperties.GLOWING, !oldState.getValue(FramedProperties.GLOWING)) == newState)
            {
                return false;
            }
        }

        return true;
    }

    default boolean lockState(Level level, BlockPos pos, Player player, ItemStack stack)
    {
        if (stack.getItem() != Utils.FRAMED_KEY.get()) { return false; }

        if (!level.isClientSide())
        {
            BlockState state = level.getBlockState(pos);
            boolean locked = state.getValue(FramedProperties.STATE_LOCKED);
            player.displayClientMessage(new TranslatableComponent(LOCK_MESSAGE, locked ? STATE_UNLOCKED : STATE_LOCKED), true);

            level.setBlockAndUpdate(pos, state.cycle(FramedProperties.STATE_LOCKED));
        }
        return true;
    }

    default BlockState updateShapeLockable(BlockState state, LevelAccessor level, BlockPos pos, Supplier<BlockState> updateShape)
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

    default BlockState rotate(BlockState state, Direction face, Rotation rot) { return state.rotate(rot); }

    default MaterialColor getCamoMapColor(BlockGetter level, BlockPos pos, MaterialColor defaultColor)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            MaterialColor color = be.getMapColor();
            if (color != null)
            {
                return color;
            }
        }
        return defaultColor;
    }

    default float[] getCamoBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.getCamoBeaconColorMultiplier(level, pos, beaconPos);
        }
        return null;
    }

    default Optional<MutableComponent> printCamoBlock(CompoundTag beTag)
    {
        BlockState camoState = NbtUtils.readBlockState(beTag.getCompound("camo_state"));

        if (camoState.isAir()) { return Optional.empty(); }
        return Optional.of(camoState.getBlock().getName().withStyle(ChatFormatting.WHITE));
    }
}