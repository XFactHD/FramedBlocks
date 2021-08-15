package xfacthd.framedblocks.common.block;

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
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.common.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public interface IFramedBlock extends EntityBlock//, IFacade
{
    BlockType getBlockType();

    static Block.Properties createProperties()
    {
        return Block.Properties.of(Material.WOOD)
                .noOcclusion()
                .strength(2F)
                .sound(SoundType.WOOD);
    }

    default BlockItem createItemBlock()
    {
        Block block = (Block)this;
        BlockItem item = new BlockItem(block, new Item.Properties().tab(FramedBlocks.FRAMED_TAB));
        //noinspection ConstantConditions
        item.setRegistryName(block.getRegistryName());
        return item;
    }

    default void tryApplyCamoImmediately(Level level, BlockPos pos, @Nullable LivingEntity placer, ItemStack stack)
    {
        if (!level.isClientSide() && placer instanceof Player player)
        {
            if (player.getMainHandItem() != stack) { return; }

            ItemStack otherStack = player.getOffhandItem();
            if (otherStack.getItem() instanceof BlockItem item && !(item.getBlock() instanceof IFramedBlock))
            {
                if (level.getBlockEntity(pos) instanceof FramedBlockEntity be && !(be instanceof FramedDoubleBlockEntity))
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
            ItemStack camo = be.getCamoStack();
            if (!camo.isEmpty())
            {
                drops.add(camo);
            }

            if (be instanceof FramedDoubleBlockEntity dbe)
            {
                camo = dbe.getCamoStackTwo();
                if (!camo.isEmpty())
                {
                    drops.add(camo);
                }
            }
        }

        return drops;
    }

    default CtmPredicate getCtmPredicate() { return getBlockType().getCtmPredicate(); }

    @Nonnull
    //@Override //TODO: reactivate when CTM is out
    @Deprecated
    default BlockState getFacadeDisabled(@Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nullable Direction side)
    {
        return Blocks.AIR.defaultBlockState();
    }

    @Nonnull
    //@Override //TODO: reactivate when CTM is out
    default BlockState getFacadeDisabled(@Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nullable Direction side, @Nonnull BlockPos connection)
    {
        BlockState state = level.getBlockState(pos);
        if (getCtmPredicate().test(state, side))
        {
            if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
            {
                return be.getCamoState();
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    default boolean isSideHidden(BlockGetter level, BlockPos pos, BlockState state, Direction side)
    {
        if (level == null) { return false; } //Block had no camo when loaded => level in data not set

        SideSkipPredicate pred = ClientConfig.detailedCulling ? getBlockType().getSideSkipPredicate() : SideSkipPredicate.CTM;
        return pred.test(level, pos, state, level.getBlockState(pos.relative(side)), side);
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
        if (CommonConfig.fireproofBlocks) { return false; }

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.isCamoFlammable(face);
        }
        return true;
    }

    default int getCamoFlammability(BlockGetter level, BlockPos pos, Direction face)
    {
        if (CommonConfig.fireproofBlocks) { return 0; }

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

    default MutableComponent printCamoBlock(CompoundTag beTag)
    {
        BlockState camoState = NbtUtils.readBlockState(beTag.getCompound("camo_state"));
        return camoState.isAir() ? FramedBlueprintItem.BLOCK_NONE : camoState.getBlock().getName().withStyle(ChatFormatting.WHITE);
    }
}