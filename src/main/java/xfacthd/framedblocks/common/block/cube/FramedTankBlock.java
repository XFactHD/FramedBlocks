package xfacthd.framedblocks.common.block.cube;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.blockentity.special.FramedTankBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.List;

public class FramedTankBlock extends FramedBlock
{
    public static final String TANK_CONTENTS = Utils.translationKey("desc", "block.fluid_tank.contents");
    public static final Component EMPTY_FLUID = Utils.translate("desc", "block.fluid_tank.contents.empty").withStyle(ChatFormatting.ITALIC);

    public FramedTankBlock()
    {
        super(BlockType.FRAMED_TANK);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.SOLID, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        ItemInteractionResult result = super.useItemOn(stack, state, level, pos, player, hand, hit);
        if (result.consumesAction() && result != ItemInteractionResult.CONSUME_PARTIAL)
        {
            return result;
        }
        if (level.getBlockEntity(pos) instanceof FramedTankBlockEntity be)
        {
            return be.handleTankInteraction(player, hand);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof FramedTankBlockEntity be)
        {
            return be.getAnalogSignal();
        }
        return 0;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedTankBlockEntity(pos, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> lines, TooltipFlag flag)
    {
        super.appendHoverText(stack, ctx, lines, flag);

        Component name = EMPTY_FLUID;
        SimpleFluidContent content = stack.getOrDefault(FBContent.DC_TYPE_TANK_CONTENTS, SimpleFluidContent.EMPTY);
        if (!content.isEmpty())
        {
            name = content.getFluid().getFluidType().getDescription().copy().withStyle(ChatFormatting.WHITE);
        }
        lines.add(Component.translatable(TANK_CONTENTS, name).withStyle(ChatFormatting.GOLD));
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }
}
