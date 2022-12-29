package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
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

    protected FramedWeightedPressurePlateBlock(BlockType type, int maxWeight, Properties props)
    {
        super(maxWeight, props);
        this.type = type;
    }

    @Override
    public final InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return handleUse(level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState)
    {
        onStateChange(level, pos, oldState, newState);
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (player.getMainHandItem().is(FBContent.itemFramedHammer.get()))
        {
            if (!level.isClientSide())
            {
                Utils.wrapInStateCopy(level, pos, false, () ->
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
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) { return getLight(state, level, pos); }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity)
    {
        return getCamoSound(state, level, pos);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return getCamoExplosionResistance(state, level, pos, explosion);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity)
    {
        return getCamoSlipperiness(state, level, pos, entity);
    }

    @Override
    public MaterialColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MaterialColor defaultColor)
    {
        return getCamoMapColor(level, pos, defaultColor);
    }

    @Override
    public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        return getCamoBeaconColorMultiplier(level, pos, beaconPos);
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FramedBlockEntity(pos, state); }

    @Override
    public BlockType getBlockType() { return type; }



    // Merge states with power > 0 to avoid unnecessary model duplication
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
        return state;
    }

    public static FramedWeightedPressurePlateBlock gold()
    {
        return new FramedWeightedPressurePlateBlock(
                BlockType.FRAMED_GOLD_PRESSURE_PLATE,
                15,
                IFramedBlock.createProperties(BlockType.FRAMED_GOLD_PRESSURE_PLATE)
                        .noCollission()
                        .strength(0.5F)
        );
    }

    public static FramedWeightedPressurePlateBlock goldWaterloggable()
    {
        return new FramedWaterloggableWeightedPressurePlateBlock(
                BlockType.FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE,
                15,
                IFramedBlock.createProperties(BlockType.FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE)
                        .noCollission()
                        .strength(0.5F)
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
                        .strength(0.5F)
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
                        .strength(0.5F)
        );
    }
}