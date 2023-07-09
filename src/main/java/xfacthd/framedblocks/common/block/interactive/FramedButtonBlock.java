package xfacthd.framedblocks.common.block.interactive;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.render.FramedBlockRenderProperties;
import xfacthd.framedblocks.common.data.BlockType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class FramedButtonBlock extends ButtonBlock implements IFramedBlock
{
    private final BlockType type;

    protected FramedButtonBlock(BlockType type, int pressTime, boolean arrowsCanPress, BlockSetType blockSet)
    {
        super(Properties.of()
                .pushReaction(PushReaction.DESTROY)
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.WOOD)
                .noOcclusion(),
                blockSet,
                pressTime,
                arrowsCanPress
        );
        this.type = type;
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.GLOWING, false)
                .setValue(FramedProperties.PROPAGATES_SKYLIGHT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    public final InteractionResult use(
            BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        InteractionResult result = handleUse(state, level, pos, player, hand, hit);
        return result.consumesAction() ? result : super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (state.getValue(FACE) != AttachFace.WALL)
        {
            return rotate(state, rot);
        }
        return state;
    }

    @Override
    public BlockType getBlockType()
    {
        return type;
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        consumer.accept(FramedBlockRenderProperties.INSTANCE);
    }



    public static FramedButtonBlock wood()
    {
        return new FramedButtonBlock(
                BlockType.FRAMED_BUTTON,
                30,
                true,
                BlockSetType.OAK
        );
    }

    public static FramedButtonBlock stone()
    {
        return new FramedButtonBlock(
                BlockType.FRAMED_STONE_BUTTON,
                20,
                false,
                BlockSetType.STONE
        );
    }
}