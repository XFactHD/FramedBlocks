package xfacthd.framedblocks.common.block.interactive;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class FramedWeightedPressurePlateBlock extends WeightedPressurePlateBlock implements IFramedBlock
{
    private static final Map<BlockType, BlockType> WATERLOGGING_SWITCH = Map.of(
            BlockType.FRAMED_GOLD_PRESSURE_PLATE, BlockType.FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE,
            BlockType.FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE, BlockType.FRAMED_GOLD_PRESSURE_PLATE,
            BlockType.FRAMED_IRON_PRESSURE_PLATE, BlockType.FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE,
            BlockType.FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE, BlockType.FRAMED_IRON_PRESSURE_PLATE
    );

    private final BlockType type;

    protected FramedWeightedPressurePlateBlock(BlockType type, int maxWeight, Properties props, SoundEvent soundOff, SoundEvent soundOn)
    {
        super(maxWeight, props, soundOff, soundOn);
        this.type = type;
        registerDefaultState(defaultBlockState().setValue(FramedProperties.GLOWING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.GLOWING);
    }

    @Override
    public final InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return handleUse(state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (player.getMainHandItem().is(FBContent.itemFramedHammer.get()))
        {
            if (!level.isClientSide())
            {
                Utils.wrapInStateCopy(level, pos, player, ItemStack.EMPTY, false, false, () ->
                {
                    BlockState newState = FBContent.byType(WATERLOGGING_SWITCH.get(type)).defaultBlockState();
                    level.setBlockAndUpdate(pos, newState);
                });
            }
            return true;
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public BlockType getBlockType() { return type; }



    // Merge states with power > 0 and ignore waterlogging and glowing to avoid unnecessary model duplication
    public static BlockState mergeWeightedState(BlockState state)
    {
        if (state.hasProperty(BlockStateProperties.WATERLOGGED))
        {
            state = state.setValue(BlockStateProperties.WATERLOGGED, false);
        }

        if (state.getValue(WeightedPressurePlateBlock.POWER) > 1)
        {
            return state.setValue(WeightedPressurePlateBlock.POWER, 1);
        }
        return state.setValue(FramedProperties.GLOWING, false);
    }

    public static FramedWeightedPressurePlateBlock gold()
    {
        return new FramedWeightedPressurePlateBlock(
                BlockType.FRAMED_GOLD_PRESSURE_PLATE,
                15,
                IFramedBlock.createProperties(BlockType.FRAMED_GOLD_PRESSURE_PLATE)
                        .noCollission()
                        .strength(0.5F),
                SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
                SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON
        );
    }

    public static FramedWeightedPressurePlateBlock goldWaterloggable()
    {
        return new FramedWaterloggableWeightedPressurePlateBlock(
                BlockType.FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE,
                15,
                IFramedBlock.createProperties(BlockType.FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE)
                        .noCollission()
                        .strength(0.5F),
                SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
                SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON
        );
    }

    public static FramedWeightedPressurePlateBlock iron()
    {
        return new FramedWeightedPressurePlateBlock(
                BlockType.FRAMED_IRON_PRESSURE_PLATE,
                150,
                IFramedBlock.createProperties(BlockType.FRAMED_IRON_PRESSURE_PLATE)
                        .requiresCorrectToolForDrops()
                        .noCollission()
                        .strength(0.5F),
                SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
                SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON
        );
    }

    public static FramedWeightedPressurePlateBlock ironWaterloggable()
    {
        return new FramedWaterloggableWeightedPressurePlateBlock(
                BlockType.FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE,
                150,
                IFramedBlock.createProperties(BlockType.FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE)
                        .requiresCorrectToolForDrops()
                        .noCollission()
                        .strength(0.5F),
                SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
                SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON
        );
    }
}