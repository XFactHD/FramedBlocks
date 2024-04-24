package xfacthd.framedblocks.common.block.interactive;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.*;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.blockentity.special.FramedChiseledBookshelfBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.Optional;

@SuppressWarnings("deprecation")
public class FramedChiseledBookshelfBlock extends FramedBlock
{
    public FramedChiseledBookshelfBlock()
    {
        super(BlockType.FRAMED_CHISELED_BOOKSHELF);
        BlockState state = defaultBlockState();
        for (BooleanProperty prop : ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES)
        {
            state = state.setValue(prop, false);
        }
        registerDefaultState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, FramedProperties.SOLID);
        ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.forEach(builder::add);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx).withHorizontalFacing(true).build();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.setPlacedBy(level, pos, state, placer, stack);

        //noinspection ConstantConditions
        if (level.isClientSide() || stack.get(DataComponents.BLOCK_ENTITY_DATA) == null)
        {
            return;
        }

        if (level.getBlockEntity(pos) instanceof FramedChiseledBookshelfBlockEntity be)
        {
            be.forceStateUpdate();
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        InteractionResult result = super.use(state, level, pos, player, hand, hit);
        if (result != InteractionResult.PASS)
        {
            return result;
        }

        if (level.getBlockEntity(pos) instanceof FramedChiseledBookshelfBlockEntity be)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            Optional<Vec2> optional = ChiseledBookShelfBlock.getRelativeHitCoordinatesForBlockFace(hit, dir);
            if (optional.isEmpty())
            {
                return InteractionResult.PASS;
            }

            int slot = ChiseledBookShelfBlock.getHitSlot(optional.get());
            if (state.getValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(slot)))
            {
                takeBook(level, pos, player, be, slot);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }

            ItemStack stack = player.getItemInHand(hand);
            if (!stack.is(ItemTags.BOOKSHELF_BOOKS))
            {
                return InteractionResult.CONSUME;
            }

            placeBook(level, pos, player, be, stack, slot);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    private static void placeBook(
            Level level, BlockPos pos, Player player, FramedChiseledBookshelfBlockEntity be, ItemStack stack, int slot
    )
    {
        if (level.isClientSide())
        {
            return;
        }

        be.placeBook(stack.split(1), slot);
        if (player.isCreative())
        {
            stack.grow(1);
        }

        SoundEvent sound = stack.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_INSERT_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_INSERT;
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1F, 1F);

        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    private static void takeBook(Level level, BlockPos pos, Player player, FramedChiseledBookshelfBlockEntity be, int slot)
    {
        if (level.isClientSide())
        {
            return;
        }

        ItemStack stack = be.takeBook(slot);
        if (!player.getInventory().add(stack))
        {
            player.drop(stack, false);
        }

        SoundEvent sound = stack.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_PICKUP;
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);

        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (newState.getBlock() != state.getBlock() && level.getBlockEntity(pos) instanceof FramedChiseledBookshelfBlockEntity be)
        {
            be.getDrops().forEach(stack -> popResource(level, pos, stack));
            be.clearContents();
            level.updateNeighbourForOutputSignal(pos, this);
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof FramedChiseledBookshelfBlockEntity be)
        {
            return be.getAnalogOutputSignal();
        }
        return 0;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, mirror);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedChiseledBookshelfBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return state.setValue(FramedProperties.FACING_HOR, Direction.NORTH);
    }
}
