package xfacthd.framedblocks.api.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.util.ServerConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * The {@link team.chisel.ctm.api.IFacade} interface is not implemented directly and is instead hacked on
 * via a mixin to allow CTM to be an optional dependency :/
 */
@SuppressWarnings({ "deprecation", "unused" })
public interface IFramedBlock extends EntityBlock//, IFacade
{
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
        Block.Properties props = Block.Properties.of(Material.WOOD)
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
                be.checkCamoSolidOnPlace();
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

        if (ServerConfig.enableIntangibleFeature && !isIntangible(state, level, pos, null))
        {
            if (neighborState.getBlock() instanceof IFramedBlock block && block.getBlockType().allowMakingIntangible())
            {
                if (block.isIntangible(neighborState, level, neighborPos, null))
                {
                    return false;
                }
            }
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
        if (!ServerConfig.enableIntangibleFeature || !getBlockType().allowMakingIntangible()) { return false; }
        return level.getBlockEntity(pos) instanceof FramedBlockEntity be && be.isIntangible(ctx);
    }

    default boolean isSuffocating(BlockState state, BlockGetter level, BlockPos pos)
    {
        if (ServerConfig.enableIntangibleFeature && getBlockType().allowMakingIntangible())
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

    default Optional<MutableComponent> printCamoBlock(CompoundTag beTag)
    {
        BlockState camoState = NbtUtils.readBlockState(beTag.getCompound("camo_state"));

        if (camoState.isAir()) { return Optional.empty(); }
        return Optional.of(camoState.getBlock().getName().withStyle(ChatFormatting.WHITE));
    }
}