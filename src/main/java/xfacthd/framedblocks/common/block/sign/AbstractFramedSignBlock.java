package xfacthd.framedblocks.common.block.sign;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.*;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ToolActions;
import net.neoforged.neoforge.network.PacketDistributor;
import xfacthd.framedblocks.api.model.wrapping.WrapHelper;
import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;
import xfacthd.framedblocks.common.net.payload.OpenSignScreenPayload;

import java.util.*;

public abstract class AbstractFramedSignBlock extends FramedBlock
{
    private static final Vec3 HITBOX_CENTER = new Vec3(.5, .5, .5);

    protected AbstractFramedSignBlock(BlockType type, Properties props)
    {
        super(type, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    // TODO check sign editing
    @Override
    public ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        //Makes sure the block can have a camo applied, even when the sign can execute a command
        ItemInteractionResult result = super.useItemOn(stack, state, level, pos, player, hand, hit);
        if (result != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION || preventUse(state, level, pos, player, hand, hit))
        {
            return result;
        }

        SignInteraction interaction = SignInteraction.from(stack);
        boolean canInteract = interaction != null && player.getAbilities().mayBuild;

        if (level.getBlockEntity(pos) instanceof FramedSignBlockEntity sign)
        {
            if (level.isClientSide())
            {
                return canInteract || sign.isWaxed() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.CONSUME;
            }

            boolean front = sign.isFacingFrontText(player);
            if (sign.isWaxed() && interaction != SignInteraction.REMOVE_WAX)
            {
                if (sign.canExecuteCommands(front, player) && sign.tryExecuteCommands(player, level, pos, front))
                {
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            else if (canInteract && notBlockedByOtherPlayer(player, sign) && interaction.interact(level, pos, player, stack, front, sign))
            {
                if (!player.isCreative())
                {
                    stack.shrink(1);
                    player.getInventory().setChanged();
                }
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));

                return ItemInteractionResult.SUCCESS;
            }
            else if (notBlockedByOtherPlayer(player, sign) && canEdit(player, sign, front))
            {
                openEditScreen(player, sign, front);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    protected boolean preventUse(
            BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        return false;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        if (state.getValue(BlockStateProperties.WATERLOGGED))
        {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, dir, facingState, level, pos, facingPos);
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type)
    {
        return type != PathComputationType.WATER || state.getFluidState().is(FluidTags.WATER);
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState state)
    {
        return true;
    }

    public abstract float getYRotationDegrees(BlockState state);

    public Vec3 getSignHitboxCenterPosition(BlockState state)
    {
        return HITBOX_CENTER;
    }

    public int getTextLineHeight()
    {
        return 10;
    }

    public int getMaxTextLineWidth()
    {
        return 90;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return FramedSignBlockEntity.normalSign(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return Utils.createBlockEntityTicker(type, FBContent.BE_TYPE_FRAMED_SIGN.value(), FramedSignBlockEntity::tick);
    }



    private static boolean notBlockedByOtherPlayer(Player player, FramedSignBlockEntity sign)
    {
        UUID uuid = sign.getEditingPlayer();
        return uuid == null || uuid.equals(player.getUUID());
    }

    private static boolean canEdit(Player player, FramedSignBlockEntity sign, boolean frontText)
    {
        SignText text = sign.getText(frontText);
        return Arrays.stream(text.getMessages(player.isTextFilteringEnabled())).allMatch(line ->
                line.equals(CommonComponents.EMPTY) || line.getContents() instanceof PlainTextContents
        );
    }

    public static void openEditScreen(Player player, FramedSignBlockEntity sign, boolean frontText)
    {
        sign.setEditingPlayer(player.getUUID());
        PacketDistributor.sendToPlayer((ServerPlayer)player, new OpenSignScreenPayload(sign.getBlockPos(), frontText));
    }



    private enum SignInteraction
    {
        APPLY_DYE((level, pos, player, stack, front, sign) ->
        {
            level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return sign.updateText(text -> text.setColor(((DyeItem) stack.getItem()).getDyeColor()), front);
        }),
        APPLY_INK((level, pos, player, stack, front, sign) ->
        {
            level.playSound(null, pos, SoundEvents.INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return sign.updateText(text -> text.setHasGlowingText(false), front);
        }),
        APPLY_GLOW_INK((level, pos, player, stack, front, sign) ->
        {
            level.playSound(null, pos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (player instanceof ServerPlayer)
            {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, stack);
            }
            return sign.updateText(text -> text.setHasGlowingText(true), front);
        }),
        APPLY_WAX((level, pos, player, stack, front, sign) ->
        {
            if (sign.setWaxed(true))
            {
                level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_WAX_ON, sign.getBlockPos(), 0);
                return true;
            }
            return false;
        }),
        REMOVE_WAX((level, pos, player, stack, front, sign) ->
        {
            if (sign.setWaxed(false))
            {
                level.levelEvent(LevelEvent.PARTICLES_WAX_OFF, sign.getBlockPos(), 0);
                return true;
            }
            return false;
        });

        private final Action action;

        SignInteraction(Action action)
        {
            this.action = action;
        }

        public boolean interact(
                Level level, BlockPos pos, Player player, ItemStack stack, boolean front, FramedSignBlockEntity sign
        )
        {
            return action.apply(level, pos, player, stack, front, sign);
        }



        public static SignInteraction from(ItemStack stack)
        {
            if (stack.getItem() instanceof DyeItem)
            {
                return APPLY_DYE;
            }
            else if (stack.is(Items.INK_SAC))
            {
                return APPLY_INK;
            }
            else if (stack.is(Items.GLOW_INK_SAC))
            {
                return APPLY_GLOW_INK;
            }
            else if (stack.is(Items.HONEYCOMB))
            {
                return APPLY_WAX;
            }
            else if (stack.canPerformAction(ToolActions.AXE_WAX_OFF))
            {
                return REMOVE_WAX;
            }
            return null;
        }
    }

    private interface Action
    {
        boolean apply(Level level, BlockPos pos, Player player, ItemStack stack, boolean front, FramedSignBlockEntity sign);
    }

    public static final class RotatingSignStateMerger implements StateMerger
    {
        public static final RotatingSignStateMerger INSTANCE = new RotatingSignStateMerger();

        private final StateMerger ignoringMerger = StateMerger.ignoring(WrapHelper.IGNORE_WATERLOGGED);

        private RotatingSignStateMerger() { }

        @Override
        public BlockState apply(BlockState state)
        {
            state = ignoringMerger.apply(state);
            int rot = state.getValue(BlockStateProperties.ROTATION_16);
            if (rot > 7)
            {
                state = state.setValue(BlockStateProperties.ROTATION_16, rot - 8);
            }
            return state;
        }

        @Override
        public Set<Property<?>> getHandledProperties(Holder<Block> block)
        {
            return Utils.concat(
                    ignoringMerger.getHandledProperties(block),
                    Set.of(BlockStateProperties.ROTATION_16)
            );
        }
    }
}
