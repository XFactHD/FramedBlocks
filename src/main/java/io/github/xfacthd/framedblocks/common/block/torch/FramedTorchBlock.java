package io.github.xfacthd.framedblocks.common.block.torch;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.IFramedBlockInternal;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.item.block.FramedStandingAndWallBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class FramedTorchBlock extends TorchBlock implements IFramedBlockInternal {
    private final BlockType type;

    private FramedTorchBlock(BlockType type, SimpleParticleType particle, Properties props) {
        this.type = type;
        super(particle, props
                .pushReaction(PushReaction.DESTROY)
                .noCollision()
                .strength(0.5F)
                .sound(SoundType.WOOD)
                .lightLevel(_ -> 14)
                .noOcclusion()
        );
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
    @SuppressWarnings("deprecation")
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return Math.max(state.getLightEmission(), super.getLightEmission(state, level, pos));
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return super.getDrops(state, getCamoDrops(builder));
    }

    @Override
    public BlockType getBlockType() {
        return type;
    }

    @Override
    public IFramedBlockItem createBlockItem(Item.Properties props) {
        record BlockPair(Holder<Block> standing, Holder<Block> wall) {}

        BlockPair blocks = switch (type) {
            case FRAMED_TORCH -> new BlockPair(FBContent.BLOCK_FRAMED_TORCH, FBContent.BLOCK_FRAMED_WALL_TORCH);
            case FRAMED_SOUL_TORCH -> new BlockPair(FBContent.BLOCK_FRAMED_SOUL_TORCH, FBContent.BLOCK_FRAMED_SOUL_WALL_TORCH);
            case FRAMED_COPPER_TORCH -> new BlockPair(FBContent.BLOCK_FRAMED_COPPER_TORCH, FBContent.BLOCK_FRAMED_COPPER_WALL_TORCH);
            default -> throw new IllegalArgumentException("Invalid BlockType for FramedTorchBlock: " + type);
        };
        return new FramedStandingAndWallBlockItem(blocks.standing.value(), blocks.wall.value(), Direction.DOWN, props);
    }

    @Override
    public @Nullable BlockState getItemModelSource() {
        return null;
    }

    @Override
    public Class<? extends Block> getJadeTargetClass() {
        return FramedTorchBlock.class;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState();
    }

    @Override
    public float getJadeRenderScale(BlockState state) {
        return 2F;
    }

    public static FramedTorchBlock normal(Properties props) {
        return new FramedTorchBlock(BlockType.FRAMED_TORCH, ParticleTypes.FLAME, props);
    }

    public static FramedTorchBlock soul(Properties props) {
        return new FramedTorchBlock(BlockType.FRAMED_SOUL_TORCH, ParticleTypes.SOUL_FIRE_FLAME, props);
    }

    public static FramedTorchBlock copper(Properties props) {
        return new FramedTorchBlock(BlockType.FRAMED_COPPER_TORCH, ParticleTypes.COPPER_FIRE_FLAME, props);
    }
}
