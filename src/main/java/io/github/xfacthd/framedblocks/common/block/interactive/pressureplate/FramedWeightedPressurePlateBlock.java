package io.github.xfacthd.framedblocks.common.block.interactive.pressureplate;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.CopycatStyleBlock;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMergers;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.IFramedBlockInternal;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FramedWeightedPressurePlateBlock extends WeightedPressurePlateBlock implements IFramedBlockInternal, CopycatStyleBlock.StateDependent {
    public static final StateMerger STATE_MERGER = StateMergers.compound(StateMergers.DEFAULT, new WeightedStateMerger());
    private static final Map<BlockType, BlockType> WATERLOGGING_SWITCH = Map.of(
            BlockType.FRAMED_GOLD_PRESSURE_PLATE, BlockType.FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE,
            BlockType.FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE, BlockType.FRAMED_GOLD_PRESSURE_PLATE,
            BlockType.FRAMED_IRON_PRESSURE_PLATE, BlockType.FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE,
            BlockType.FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE, BlockType.FRAMED_IRON_PRESSURE_PLATE
    );

    private final BlockType type;

    protected FramedWeightedPressurePlateBlock(BlockType type, int maxWeight, BlockSetType blockSet, Properties props) {
        this.type = type;
        super(maxWeight, blockSet, props);
        BlockUtils.configureStandardProperties(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        BlockUtils.addStandardProperties(this, builder);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return handleUse(state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player) {
        if (player.getMainHandItem().is(FBContent.ITEM_FRAMED_HAMMER.value())) {
            if (!level.isClientSide()) {
                BlockUtils.wrapInStateCopy(level, pos, player, ItemStack.EMPTY, false, false, () -> {
                    BlockState newState = getCounterpart().defaultBlockState();
                    newState = copyProperty(state, newState, FramedProperties.COPYCAT_STYLE);
                    level.setBlockAndUpdate(pos, newState);
                });
            }
            return true;
        }
        return IFramedBlockInternal.super.handleBlockLeftClick(state, level, pos, player);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return super.getDrops(state, getCamoDrops(builder));
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public BlockType getBlockType() {
        return type;
    }

    protected final Block getCounterpart() {
        return FBContent.byType(WATERLOGGING_SWITCH.get(type));
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.UNSUPPORTED;
    }

    @Override
    public Class<? extends Block> getJadeTargetClass() {
        return FramedWeightedPressurePlateBlock.class;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState();
    }

    public static FramedWeightedPressurePlateBlock gold(Properties props) {
        return new FramedWeightedPressurePlateBlock(
                BlockType.FRAMED_GOLD_PRESSURE_PLATE,
                15,
                BlockSetType.GOLD,
                IFramedBlock.applyDefaultProperties(props, BlockType.FRAMED_GOLD_PRESSURE_PLATE)
                        .noCollision()
                        .strength(0.5F)
        );
    }

    public static FramedWeightedPressurePlateBlock goldWaterloggable(Properties props) {
        return new FramedWaterloggableWeightedPressurePlateBlock(
                BlockType.FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE,
                15,
                BlockSetType.GOLD,
                IFramedBlock.applyDefaultProperties(props, BlockType.FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE)
                        .noCollision()
                        .strength(0.5F)
        );
    }

    public static FramedWeightedPressurePlateBlock iron(Properties props) {
        return new FramedWeightedPressurePlateBlock(
                BlockType.FRAMED_IRON_PRESSURE_PLATE,
                150,
                BlockSetType.IRON,
                IFramedBlock.applyDefaultProperties(props, BlockType.FRAMED_IRON_PRESSURE_PLATE)
                        .requiresCorrectToolForDrops()
                        .noCollision()
                        .strength(0.5F)
        );
    }

    public static FramedWeightedPressurePlateBlock ironWaterloggable(Properties props) {
        return new FramedWaterloggableWeightedPressurePlateBlock(
                BlockType.FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE,
                150,
                BlockSetType.IRON,
                IFramedBlock.applyDefaultProperties(props, BlockType.FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE)
                        .requiresCorrectToolForDrops()
                        .noCollision()
                        .strength(0.5F)
        );
    }

    private static final class WeightedStateMerger extends StateMerger {
        private WeightedStateMerger() {
            super(Set.of(WeightedPressurePlateBlock.POWER));
        }

        @Override
        public BlockState apply(BlockState state) {
            if (state.getValue(WeightedPressurePlateBlock.POWER) > 1) {
                state = state.setValue(WeightedPressurePlateBlock.POWER, 1);
            }
            return state;
        }
    }
}
