package io.github.xfacthd.framedblocks.common.block.interactive.pressureplate;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.IFramedBlockInternal;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class FramedPressurePlateBlock extends PressurePlateBlock implements IFramedBlockInternal
{
    private static final Map<BlockType, BlockType> WATERLOGGING_SWITCH = Map.of(
            BlockType.FRAMED_PRESSURE_PLATE, BlockType.FRAMED_WATERLOGGABLE_PRESSURE_PLATE,
            BlockType.FRAMED_WATERLOGGABLE_PRESSURE_PLATE, BlockType.FRAMED_PRESSURE_PLATE,
            BlockType.FRAMED_STONE_PRESSURE_PLATE, BlockType.FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE,
            BlockType.FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE, BlockType.FRAMED_STONE_PRESSURE_PLATE,
            BlockType.FRAMED_OBSIDIAN_PRESSURE_PLATE, BlockType.FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE,
            BlockType.FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE, BlockType.FRAMED_OBSIDIAN_PRESSURE_PLATE
    );
    private static final BlockSetType OBSIDIAN = BlockSetType.register(new BlockSetType(
            FramedConstants.MOD_ID + ":obsidian",
            true,
            true,
            false,
            BlockSetType.PressurePlateSensitivity.MOBS,
            SoundType.STONE,
            SoundEvents.IRON_DOOR_CLOSE,
            SoundEvents.IRON_DOOR_OPEN,
            SoundEvents.IRON_TRAPDOOR_CLOSE,
            SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF,
            SoundEvents.STONE_BUTTON_CLICK_ON
    ));

    private final BlockType blockType;

    protected FramedPressurePlateBlock(BlockType type, BlockSetType blockSet, Properties props)
    {
        super(blockSet, props);
        this.blockType = type;
        BlockUtils.configureStandardProperties(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        BlockUtils.addRequiredProperties(builder);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
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
        if (player.getMainHandItem().is(FBContent.ITEM_FRAMED_HAMMER.value()))
        {
            if (!level.isClientSide())
            {
                BlockUtils.wrapInStateCopy(level, pos, player, ItemStack.EMPTY, false, false, () ->
                {
                    BlockState newState = getCounterpart().defaultBlockState();
                    level.setBlockAndUpdate(pos, newState);
                });
            }
            return true;
        }
        return false;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state)
    {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return super.getDrops(state, getCamoDrops(builder));
    }

    @Override
    protected int getSignalStrength(Level level, BlockPos pos)
    {
        if (type == OBSIDIAN)
        {
            return getEntityCount(level, TOUCH_AABB.move(pos), Player.class) > 0 ? 15 : 0;
        }
        return super.getSignalStrength(level, pos);
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        return true;
    }

    @Override
    public BlockType getBlockType()
    {
        return blockType;
    }

    protected final Block getCounterpart()
    {
        return FBContent.byType(WATERLOGGING_SWITCH.get(blockType));
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    @Nullable
    public Direction getHorizontalOrientation(BlockState state)
    {
        return null;
    }

    @Override
    public Class<? extends Block> getJadeTargetClass()
    {
        return FramedPressurePlateBlock.class;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }



    public static FramedPressurePlateBlock wood(Properties props)
    {
        return new FramedPressurePlateBlock(
                BlockType.FRAMED_PRESSURE_PLATE,
                BlockSetType.OAK,
                IFramedBlock.applyDefaultProperties(props, BlockType.FRAMED_PRESSURE_PLATE)
                        .noCollision()
                        .strength(0.5F)
        );
    }

    public static FramedPressurePlateBlock woodWaterloggable(Properties props)
    {
        return new FramedWaterloggablePressurePlateBlock(
                BlockType.FRAMED_WATERLOGGABLE_PRESSURE_PLATE,
                BlockSetType.OAK,
                IFramedBlock.applyDefaultProperties(props, BlockType.FRAMED_WATERLOGGABLE_PRESSURE_PLATE)
                        .noCollision()
                        .strength(0.5F)
        );
    }

    public static FramedPressurePlateBlock stone(Properties props)
    {
        return new FramedPressurePlateBlock(
                BlockType.FRAMED_STONE_PRESSURE_PLATE,
                BlockSetType.STONE,
                IFramedBlock.applyDefaultProperties(props, BlockType.FRAMED_STONE_PRESSURE_PLATE)
                        .requiresCorrectToolForDrops()
                        .noCollision()
                        .strength(0.5F)
        );
    }

    public static FramedPressurePlateBlock stoneWaterloggable(Properties props)
    {
        return new FramedWaterloggablePressurePlateBlock(
                BlockType.FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE,
                BlockSetType.STONE,
                IFramedBlock.applyDefaultProperties(props, BlockType.FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE)
                        .requiresCorrectToolForDrops()
                        .noCollision()
                        .strength(0.5F)
        );
    }

    public static FramedPressurePlateBlock obsidian(Properties props) // Player-only
    {
        return new FramedPressurePlateBlock(
                BlockType.FRAMED_OBSIDIAN_PRESSURE_PLATE,
                OBSIDIAN,
                IFramedBlock.applyDefaultProperties(props, BlockType.FRAMED_OBSIDIAN_PRESSURE_PLATE)
                        .requiresCorrectToolForDrops()
                        .noCollision()
                        .strength(0.5F)
        );
    }

    public static FramedPressurePlateBlock obsidianWaterloggable(Properties props) // Player-only
    {
        return new FramedWaterloggablePressurePlateBlock(
                BlockType.FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE,
                OBSIDIAN,
                IFramedBlock.applyDefaultProperties(props, BlockType.FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE)
                        .requiresCorrectToolForDrops()
                        .noCollision()
                        .strength(0.5F)
        );
    }
}
