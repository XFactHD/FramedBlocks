package xfacthd.framedblocks.common.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.blockentity.FramedSignBlockEntity;

import javax.annotation.Nullable;

public abstract class AbstractFramedSignBlock extends FramedBlock
{
    protected AbstractFramedSignBlock(BlockType type, Properties props) { super(type, props); }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        //Makes sure the block can have a camo applied, even when the sign can execute a command
        InteractionResult result = super.use(state, level, pos, player, hand, hit);
        if (result != InteractionResult.PASS) { return result; }

        ItemStack stack = player.getItemInHand(hand);
        boolean dye = stack.getItem() instanceof DyeItem;
        boolean glowInk = stack.is(Items.GLOW_INK_SAC);
        boolean inkSac = stack.is(Items.INK_SAC);
        boolean canInteract = (dye || glowInk || inkSac) && player.getAbilities().mayBuild;

        if (level.isClientSide())
        {
            return canInteract ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        if (level.getBlockEntity(pos) instanceof FramedSignBlockEntity sign)
        {
            if (canInteract)
            {
                boolean success = false;

                if (dye)
                {
                    level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    success = sign.setTextColor(((DyeItem) stack.getItem()).getDyeColor());
                }
                else if (glowInk && !sign.hasGlowingText())
                {
                    level.playSound(null, pos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    success = sign.setGlowingText(true);

                    if (player instanceof ServerPlayer)
                    {
                        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, stack);
                    }
                }
                else if (inkSac && sign.hasGlowingText())
                {
                    level.playSound(null, pos, SoundEvents.INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    success = sign.setGlowingText(false);
                }

                if (success)
                {
                    if (!player.isCreative())
                    {
                        stack.shrink(1);
                        player.getInventory().setChanged();
                    }
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));

                    return InteractionResult.SUCCESS;
                }
            }
            else if (sign.executeCommand((ServerPlayer) player))
            {
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type)
    {
        return type != PathComputationType.WATER || level.getFluidState(pos).is(FluidTags.WATER);
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedSignBlockEntity(pos, state);
    }

    @Override
    public boolean isPossibleToRespawnInThis() { return true; }
}