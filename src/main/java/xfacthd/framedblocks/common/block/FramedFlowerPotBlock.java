package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.common.blockentity.FramedFlowerPotBlockEntity;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.Map;
import java.util.function.Supplier;

public class FramedFlowerPotBlock extends FramedBlock
{
    public FramedFlowerPotBlock()
    {
        super(BlockType.FRAMED_FLOWER_POT);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.HANGING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.HANGING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        if (state != null && SupplementariesCompat.isLoaded())
        {
            state = state.setValue(
                    PropertyHolder.HANGING,
                    context.getClickedFace() == Direction.DOWN
            );
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction side, BlockState sideState, LevelAccessor level, BlockPos pos, BlockPos sidePos)
    {
        if (state.getValue(PropertyHolder.HANGING) && side == Direction.UP && !state.canSurvive(level, pos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, side, sideState, level, pos, sidePos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        if (state.getValue(PropertyHolder.HANGING))
        {
            return SupplementariesCompat.canSurviveHanging(level, pos.relative(Direction.UP));
        }
        return true;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        InteractionResult result = super.use(state, level, pos, player, hand, hit);
        if (result != InteractionResult.PASS) { return result; }

        if (level.getBlockEntity(pos) instanceof FramedFlowerPotBlockEntity be)
        {
            ItemStack stack = player.getItemInHand(hand);
            boolean isFlower = stack.getItem() instanceof BlockItem item && !getFlowerPotState(item.getBlock()).isAir();

            if (isFlower != be.hasFlowerBlock())
            {
                if (!level.isClientSide())
                {
                    if (isFlower && !be.hasFlowerBlock())
                    {
                        be.setFlowerBlock(((BlockItem) stack.getItem()).getBlock());

                        player.awardStat(Stats.POT_FLOWER);
                        if (!player.getAbilities().instabuild)
                        {
                            stack.shrink(1);
                        }
                    }
                    else
                    {
                        ItemStack flowerStack = new ItemStack(be.getFlowerBlock());
                        if (stack.isEmpty())
                        {
                            player.setItemInHand(hand, flowerStack);
                        }
                        else if (!player.addItem(flowerStack))
                        {
                            player.drop(flowerStack, false);
                        }

                        be.setFlowerBlock(Blocks.AIR);
                    }
                }

                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            else
            {
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type)
    {
        return false;
    }

    @Override
    protected boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        //It technically does occlude the beam, but it looks stupid, so we disable it :D
        return false;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FramedFlowerPotBlockEntity(pos, state); }



    public static BlockState getFlowerPotState(Block flower)
    {
        Map<ResourceLocation, Supplier<? extends Block>> fullPots = ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView();
        return fullPots.getOrDefault(
                ForgeRegistries.BLOCKS.getKey(flower),
                ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.AIR)
        ).get().defaultBlockState();
    }
}