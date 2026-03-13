package io.github.xfacthd.framedblocks.common.blockentity.special;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.xfacthd.framedblocks.FramedBlocks;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.NetworkValueInput;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.sign.AbstractFramedSignBlock;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class FramedSignBlockEntity extends FramedBlockEntity
{
    @Nullable
    private UUID editingPlayer;
    private SignText frontText = new SignText();
    private SignText backText = new SignText();
    private boolean waxed;

    private FramedSignBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public boolean isFacingFrontText(Player player)
    {
        if (getBlockState().getBlock() instanceof AbstractFramedSignBlock signblock)
        {
            Vec3 center = signblock.getSignHitboxCenterPosition(getBlockState());
            double dX = player.getX() - ((double) getBlockPos().getX() + center.x);
            double dY = player.getZ() - ((double) getBlockPos().getZ() + center.z);
            float blockAngle = signblock.getYRotationDegrees(getBlockState());
            float playerAngle = (float)(Mth.atan2(dY, dX) * (double)(180F / (float) Math.PI)) - 90.0F;
            return Mth.degreesDifferenceAbs(blockAngle, playerAngle) <= 90.0F;
        }
        return false;
    }

    public SignText getText(boolean front)
    {
        return front ? frontText : backText;
    }

    public void updateTextFromPacket(Player player, boolean front, List<FilteredText> filteredText)
    {
        if (level == null)
        {
            return;
        }

        if (isWaxed() || !player.getUUID().equals(editingPlayer))
        {
            FramedBlocks.LOGGER.warn(
                    "Player {} just tried to change non-editable sign at {}", player.getName().getString(), worldPosition
            );
            return;
        }

        updateText(text ->
        {
            boolean filter = player.isTextFilteringEnabled();
            for (int idx = 0; idx < filteredText.size(); idx++)
            {
                FilteredText filteredtext = filteredText.get(idx);
                Style style = text.getMessage(idx, filter).getStyle();
                Component filteredLine = Component.literal(filteredtext.filteredOrEmpty()).setStyle(style);
                Component line = filter ? filteredLine : Component.literal(filteredtext.raw()).setStyle(style);
                text = text.setMessage(idx, line, filteredLine);
            }

            return text;
        }, front);
        setEditingPlayer(null);
    }

    public boolean updateText(UnaryOperator<SignText> modifier, boolean front)
    {
        return setText(modifier.apply(getText(front)), front);
    }

    public boolean setText(SignText text, boolean front)
    {
        if (text != getText(front))
        {
            if (front) frontText = text;
            else backText = text;

            markDirty();
            return true;
        }
        return false;
    }

    public boolean isWaxed()
    {
        return waxed;
    }

    public boolean setWaxed(boolean waxed)
    {
        if (this.waxed != waxed)
        {
            this.waxed = waxed;

            markDirty();
            return true;
        }
        return false;
    }

    public boolean cannotExecuteCommands(boolean front, Player pPlayer)
    {
        return !waxed || !getText(front).hasAnyClickCommands(pPlayer);
    }

    public boolean tryExecuteCommands(Player player, Level level, BlockPos pos, boolean front)
    {
        boolean executed = false;

        for (Component line : getText(front).getMessages(player.isTextFilteringEnabled()))
        {
            ClickEvent event = line.getStyle().getClickEvent();
            if (event instanceof ClickEvent.RunCommand(String cmd))
            {
                //noinspection ConstantConditions
                player.level().getServer().getCommands().performPrefixedCommand(
                        getCommandSource((ServerPlayer) player, (ServerLevel) level, pos),
                        cmd
                );
                executed = true;
            }
        }

        return executed;
    }

    private static CommandSourceStack getCommandSource(@Nullable ServerPlayer player, ServerLevel level, BlockPos pos)
    {
        String nameString = player == null ? "Sign" : player.getName().getString();
        Component name = player == null ? Component.literal("Sign") : player.getDisplayName();
        Vec3 posVec = Vec3.atCenterOf(pos);

        return new CommandSourceStack(CommandSource.NULL, posVec, Vec2.ZERO, level, LevelBasedPermissionSet.GAMEMASTER, nameString, name, level.getServer(), player);
    }

    @Nullable
    public UUID getEditingPlayer()
    {
        return editingPlayer;
    }

    public void setEditingPlayer(@Nullable UUID player)
    {
        this.editingPlayer = player;
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, FramedSignBlockEntity be)
    {
        if (be.editingPlayer != null)
        {
            Player player = level.getPlayerByUUID(be.editingPlayer);
            if (be.isTooFarAwayToEdit(player))
            {
                be.editingPlayer = null;
            }
        }
    }

    public boolean isTooFarAwayToEdit(@Nullable Player player)
    {
        return player == null || player.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) > 64.0D;
    }

    private void markDirty()
    {
        level().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        setChangedWithoutSignalUpdate();
    }

    @Override
    protected void writeToDataPacket(ValueOutput valueOutput)
    {
        super.writeToDataPacket(valueOutput);
        writeToNbt(valueOutput);
    }

    @Override
    protected void readFromDataPacket(NetworkValueInput input)
    {
        super.readFromDataPacket(input);
        readFromNbt(input);
    }

    private void writeToNbt(ValueOutput valueOutput)
    {
        valueOutput.store("front_text", SignText.DIRECT_CODEC, frontText);
        valueOutput.store("back_text", SignText.DIRECT_CODEC, backText);
        valueOutput.putBoolean("waxed", waxed);
    }

    private void readFromNbt(ValueInput valueInput)
    {
        frontText = valueInput.read("front_text", SignText.DIRECT_CODEC).map(lines -> loadLines(level, worldPosition, lines)).orElseGet(SignText::new);
        backText = valueInput.read("back_text", SignText.DIRECT_CODEC).map(lines -> loadLines(level, worldPosition, lines)).orElseGet(SignText::new);
        waxed = valueInput.getBooleanOr("waxed", false);
    }

    private static SignText loadLines(@Nullable Level level, BlockPos pos, SignText text)
    {
        for (int i = 0; i < 4; ++i)
        {
            Component line = loadLine(level, pos, text.getMessage(i, false));
            Component lineFiltered = loadLine(level, pos, text.getMessage(i, true));
            text = text.setMessage(i, line, lineFiltered);
        }

        return text;
    }

    private static Component loadLine(@Nullable Level level, BlockPos pos, Component line)
    {
        if (level instanceof ServerLevel serverlevel)
        {
            try
            {
                return ComponentUtils.updateForEntity(getCommandSource(null, serverlevel, pos), line, null, 0);
            }
            catch (CommandSyntaxException ignored) { }
        }

        return line;
    }

    @Override
    public void saveAdditional(ValueOutput valueOutput)
    {
        writeToNbt(valueOutput);
        super.saveAdditional(valueOutput);
    }

    @Override
    public void loadAdditional(ValueInput valueInput)
    {
        super.loadAdditional(valueInput);
        readFromNbt(valueInput);
    }



    public static FramedSignBlockEntity normalSign(BlockPos pos, BlockState state)
    {
        return new FramedSignBlockEntity(FBContent.BE_TYPE_FRAMED_SIGN.value(), pos, state);
    }

    public static FramedSignBlockEntity hangingSign(BlockPos pos, BlockState state)
    {
        return new FramedSignBlockEntity(FBContent.BE_TYPE_FRAMED_HANGING_SIGN.value(), pos, state);
    }
}
