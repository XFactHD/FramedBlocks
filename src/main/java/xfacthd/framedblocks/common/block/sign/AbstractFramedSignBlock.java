package xfacthd.framedblocks.common.block.sign;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.*;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.*;
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
import net.minecraft.world.level.gameevent.GameEvent;
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
import xfacthd.framedblocks.common.net.payload.ClientboundOpenSignScreenPayload;

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

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        //Makes sure the block can have a camo applied, even when the sign can execute a command
        ItemInteractionResult result = super.useItemOn(stack, state, level, pos, player, hand, hit);
        if (result.consumesAction() || preventUse(state, level, pos, player, stack, hit))
        {
            return result;
        }

        SignInteraction interaction = SignInteraction.from(stack);
        boolean canInteract = interaction != null && player.mayBuild();

        if (level.getBlockEntity(pos) instanceof FramedSignBlockEntity sign)
        {
            if (level.isClientSide())
            {
                return canInteract || sign.isWaxed() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.CONSUME;
            }

            if (!canInteract || sign.isWaxed() || isBlockedByOtherPlayer(player, sign))
            {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            boolean front = sign.isFacingFrontText(player);
            if (interaction.interact(level, pos, player, stack, front, sign))
            {
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                level.gameEvent(GameEvent.BLOCK_CHANGE, sign.getBlockPos(), GameEvent.Context.of(player, sign.getBlockState()));
                if (!player.isCreative())
                {
                    stack.shrink(1);
                }

                return ItemInteractionResult.SUCCESS;
            }

            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit)
    {
        if (level.getBlockEntity(pos) instanceof FramedSignBlockEntity sign)
        {
            boolean front = sign.isFacingFrontText(player);
            if (sign.isWaxed())
            {
                if (sign.cannotExecuteCommands(front, player) || !sign.tryExecuteCommands(player, level, pos, front))
                {
                    level.playSound(null, pos, getSignInteractionFailedSoundEvent(), SoundSource.BLOCKS);
                }
                return InteractionResult.SUCCESS;
            }

            if (!isBlockedByOtherPlayer(player, sign) && canEdit(player, sign, front))
            {
                openEditScreen(player, sign, front);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    protected boolean preventUse(BlockState state, Level level, BlockPos pos, Player player, ItemStack stack, BlockHitResult hit)
    {
        return false;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        if (state.getValue(BlockStateProperties.WATERLOGGED))
        {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, dir, facingState, level, pos, facingPos);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type)
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

    protected SoundEvent getSignInteractionFailedSoundEvent()
    {
        return SoundEvents.WAXED_SIGN_INTERACT_FAIL;
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



    private static boolean isBlockedByOtherPlayer(Player player, FramedSignBlockEntity sign)
    {
        UUID uuid = sign.getEditingPlayer();
        return uuid != null && !uuid.equals(player.getUUID());
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
        PacketDistributor.sendToPlayer((ServerPlayer)player, new ClientboundOpenSignScreenPayload(sign.getBlockPos(), frontText));
    }



    private enum SignInteraction
    {
        APPLY_DYE(SignText::hasMessage, (level, pos, player, stack, front, sign) ->
        {
            level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return sign.updateText(text -> text.setColor(((DyeItem) stack.getItem()).getDyeColor()), front);
        }),
        APPLY_INK(SignText::hasMessage, (level, pos, player, stack, front, sign) ->
        {
            level.playSound(null, pos, SoundEvents.INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return sign.updateText(text -> text.setHasGlowingText(false), front);
        }),
        APPLY_GLOW_INK(SignText::hasMessage, (level, pos, player, stack, front, sign) ->
        {
            level.playSound(null, pos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (player instanceof ServerPlayer)
            {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, stack);
            }
            return sign.updateText(text -> text.setHasGlowingText(true), front);
        }),
        APPLY_WAX((text, player) -> true, (level, pos, player, stack, front, sign) ->
        {
            if (sign.setWaxed(true))
            {
                level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_WAX_ON, sign.getBlockPos(), 0);
                return true;
            }
            return false;
        }),
        REMOVE_WAX(SignText::hasMessage, (level, pos, player, stack, front, sign) ->
        {
            if (sign.setWaxed(false))
            {
                level.levelEvent(LevelEvent.PARTICLES_WAX_OFF, sign.getBlockPos(), 0);
                return true;
            }
            return false;
        });

        private final InteractionPredicate predicate;
        private final Action action;

        SignInteraction(InteractionPredicate predicate, Action action)
        {
            this.predicate = predicate;
            this.action = action;
        }

        public boolean interact(
                Level level, BlockPos pos, Player player, ItemStack stack, boolean front, FramedSignBlockEntity sign
        )
        {
            return predicate.canInteract(sign.getText(front), player) && action.apply(level, pos, player, stack, front, sign);
        }



        public static SignInteraction from(ItemStack stack)
        {
            return switch (stack.getItem())
            {
                case DyeItem ignored -> APPLY_DYE;
                case InkSacItem ignored -> APPLY_INK;
                case GlowInkSacItem ignored -> APPLY_GLOW_INK;
                case HoneycombItem ignored -> APPLY_WAX;
                default -> stack.canPerformAction(ToolActions.AXE_WAX_OFF) ? REMOVE_WAX : null;
            };
        }
    }

    private interface Action
    {
        boolean apply(Level level, BlockPos pos, Player player, ItemStack stack, boolean front, FramedSignBlockEntity sign);
    }

    private interface InteractionPredicate
    {
        boolean canInteract(SignText text, Player player);
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
