package io.github.xfacthd.framedblocks.common.block.interactive;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedFlowerPotBlockEntity;
import io.github.xfacthd.framedblocks.common.compat.amendments.AmendmentsCompat;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class FramedFlowerPotBlock extends FramedBlock {
    private static final Map<Block, BlockState> POT_STATE_BY_FLOWER_BLOCK = new Reference2ReferenceOpenHashMap<>();

    public FramedFlowerPotBlock(Properties props) {
        super(BlockType.FRAMED_FLOWER_POT, props);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.HANGING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.HANGING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        if (AmendmentsCompat.isLoaded()) {
            state = state.setValue(PropertyHolder.HANGING, context.getClickedFace() == Direction.DOWN);
        }
        return state;
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess tickAccess,
            BlockPos pos, Direction side,
            BlockPos adjPos,
            BlockState adjState,
            RandomSource random
    ) {
        if (state.getValue(PropertyHolder.HANGING) && side == Direction.UP && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, tickAccess, pos, side, adjPos, adjState, random);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(PropertyHolder.HANGING)) {
            return AmendmentsCompat.canSurviveHanging(level, pos.relative(Direction.UP));
        }
        return true;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = super.useItemOn(stack, state, level, pos, player, hand, hit);
        if (result.consumesAction() && result != FramedBlockEntity.CONSUME_CAMO_FAILED) {
            return result;
        }

        if (level.getBlockEntity(pos) instanceof FramedFlowerPotBlockEntity be) {
            boolean isFlower = stack.getItem() instanceof BlockItem item && !getFlowerPotState(item.getBlock()).isAir();

            if (isFlower != be.hasFlowerBlock()) {
                if (!level.isClientSide()) {
                    if (isFlower && !be.hasFlowerBlock()) {
                        be.setFlowerBlock(((BlockItem) stack.getItem()).getBlock());

                        player.awardStat(Stats.POT_FLOWER);
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                    } else {
                        ItemStack flowerStack = new ItemStack(be.getFlowerBlock());
                        Utils.giveToPlayer(player, flowerStack);

                        be.setFlowerBlock(Blocks.AIR);
                    }
                }

                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos) {
        //It technically does occlude the beam, but it looks stupid, so we disable it :D
        return false;
    }

    @Override
    public FramedBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedFlowerPotBlockEntity(pos, state);
    }

    @Override
    public @Nullable BlockState getItemModelSource() {
        return null;
    }

    @Override
    public @Nullable Direction getHorizontalOrientation(BlockState state) {
        return null;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return state;
    }

    public static BlockState getFlowerPotState(Block flower) {
        return POT_STATE_BY_FLOWER_BLOCK.getOrDefault(flower, Blocks.AIR.defaultBlockState());
    }

    public static void initPotMapping() {
        POT_STATE_BY_FLOWER_BLOCK.clear();
        ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().forEach((flowerId, potSupplier) -> {
            Block flower = BuiltInRegistries.BLOCK.getValue(flowerId);
            if (!flower.defaultBlockState().isAir()) {
                POT_STATE_BY_FLOWER_BLOCK.put(flower, potSupplier.get().defaultBlockState());
            }
        });
    }
}
