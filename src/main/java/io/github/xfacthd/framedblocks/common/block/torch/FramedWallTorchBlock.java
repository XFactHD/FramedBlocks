package io.github.xfacthd.framedblocks.common.block.torch;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.IFramedBlockInternal;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class FramedWallTorchBlock extends WallTorchBlock implements IFramedBlockInternal
{
    private final BlockType type;

    private FramedWallTorchBlock(BlockType type, SimpleParticleType particle, Properties props)
    {
        super(particle, props
                .pushReaction(PushReaction.DESTROY)
                .noCollision()
                .strength(0.5F)
                .sound(SoundType.WOOD)
                .lightLevel(state -> 14)
                .noOcclusion()
        );
        this.type = type;
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
    @SuppressWarnings("deprecation")
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos)
    {
        return Math.max(state.getLightEmission(), super.getLightEmission(state, level, pos));
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return super.getDrops(state, getCamoDrops(builder));
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        //Not rotatable by wrench
        return state;
    }

    @Override
    public BlockType getBlockType()
    {
        return type;
    }

    @Override
    @Nullable
    public BlockState getItemModelSource()
    {
        return null;
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        return state.getValue(FACING);
    }

    @Override
    public Class<? extends Block> getJadeTargetClass()
    {
        return FramedWallTorchBlock.class;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return ((IFramedBlock) FBContent.BLOCK_FRAMED_TORCH.value()).getJadeRenderState(state);
    }

    @Override
    public float getJadeRenderScale(BlockState state)
    {
        return ((IFramedBlock) FBContent.BLOCK_FRAMED_TORCH.value()).getJadeRenderScale(state);
    }

    public static FramedWallTorchBlock normal(Properties props)
    {
        return new FramedWallTorchBlock(BlockType.FRAMED_WALL_TORCH, ParticleTypes.FLAME, props);
    }

    public static FramedWallTorchBlock soul(Properties props)
    {
        return new FramedWallTorchBlock(BlockType.FRAMED_SOUL_WALL_TORCH, ParticleTypes.SOUL_FIRE_FLAME, props);
    }

    public static FramedWallTorchBlock copper(Properties props)
    {
        return new FramedWallTorchBlock(BlockType.FRAMED_COPPER_WALL_TORCH, ParticleTypes.COPPER_FIRE_FLAME, props);
    }
}
