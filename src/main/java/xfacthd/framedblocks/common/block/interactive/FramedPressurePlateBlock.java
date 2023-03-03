package xfacthd.framedblocks.common.block.interactive;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class FramedPressurePlateBlock extends PressurePlateBlock implements IFramedBlock
{
    private static final Map<BlockType, BlockType> WATERLOGGING_SWITCH = Map.of(
            BlockType.FRAMED_PRESSURE_PLATE, BlockType.FRAMED_WATERLOGGABLE_PRESSURE_PLATE,
            BlockType.FRAMED_WATERLOGGABLE_PRESSURE_PLATE, BlockType.FRAMED_PRESSURE_PLATE,
            BlockType.FRAMED_STONE_PRESSURE_PLATE, BlockType.FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE,
            BlockType.FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE, BlockType.FRAMED_STONE_PRESSURE_PLATE,
            BlockType.FRAMED_OBSIDIAN_PRESSURE_PLATE, BlockType.FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE,
            BlockType.FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE, BlockType.FRAMED_OBSIDIAN_PRESSURE_PLATE
    );

    private final BlockType type;

    @SuppressWarnings("ConstantConditions")
    protected FramedPressurePlateBlock(BlockType type, Sensitivity sensitivity, Properties props, SoundEvent soundOff, SoundEvent soundOn)
    {
        super(sensitivity, props, soundOff, soundOn);
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
    protected int getSignalStrength(Level level, BlockPos pos)
    {
        //noinspection ConstantConditions
        if (sensitivity == null)
        {
            List<Player> players = level.getEntitiesOfClass(Player.class, TOUCH_AABB.move(pos));
            if (!players.isEmpty())
            {
                for(Player player : players)
                {
                    if (!player.isIgnoringBlockTriggers())
                    {
                        return 15;
                    }
                }
            }

            return 0;
        }
        return super.getSignalStrength(level, pos);
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FramedBlockEntity(pos, state); }

    @Override
    public BlockType getBlockType() { return type; }



    public static FramedPressurePlateBlock wood()
    {
        return new FramedPressurePlateBlock(
                BlockType.FRAMED_PRESSURE_PLATE,
                Sensitivity.EVERYTHING,
                IFramedBlock.createProperties(BlockType.FRAMED_PRESSURE_PLATE)
                        .noCollission()
                        .strength(0.5F),
                SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF,
                SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON
        );
    }

    public static FramedPressurePlateBlock woodWaterloggable()
    {
        return new FramedWaterloggablePressurePlateBlock(
                BlockType.FRAMED_WATERLOGGABLE_PRESSURE_PLATE,
                Sensitivity.EVERYTHING,
                IFramedBlock.createProperties(BlockType.FRAMED_WATERLOGGABLE_PRESSURE_PLATE)
                        .noCollission()
                        .strength(0.5F),
                SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF,
                SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON
        );
    }

    public static FramedPressurePlateBlock stone()
    {
        return new FramedPressurePlateBlock(
                BlockType.FRAMED_STONE_PRESSURE_PLATE,
                Sensitivity.MOBS,
                IFramedBlock.createProperties(BlockType.FRAMED_STONE_PRESSURE_PLATE)
                        .requiresCorrectToolForDrops()
                        .noCollission()
                        .strength(0.5F),
                SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF,
                SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON
        );
    }

    public static FramedPressurePlateBlock stoneWaterloggable()
    {
        return new FramedWaterloggablePressurePlateBlock(
                BlockType.FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE,
                Sensitivity.MOBS,
                IFramedBlock.createProperties(BlockType.FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE)
                        .requiresCorrectToolForDrops()
                        .noCollission()
                        .strength(0.5F),
                SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF,
                SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON
        );
    }

    public static FramedPressurePlateBlock obsidian() // Player-only
    {
        return new FramedPressurePlateBlock(
                BlockType.FRAMED_OBSIDIAN_PRESSURE_PLATE,
                null, //Abuse null for player-only sensitivity
                IFramedBlock.createProperties(BlockType.FRAMED_OBSIDIAN_PRESSURE_PLATE)
                        .requiresCorrectToolForDrops()
                        .noCollission()
                        .strength(0.5F),
                SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF,
                SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON
        );
    }

    public static FramedPressurePlateBlock obsidianWaterloggable() // Player-only
    {
        return new FramedWaterloggablePressurePlateBlock(
                BlockType.FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE,
                null, //Abuse null for player-only sensitivity
                IFramedBlock.createProperties(BlockType.FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE)
                        .requiresCorrectToolForDrops()
                        .noCollission()
                        .strength(0.5F),
                SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF,
                SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON
        );
    }
}