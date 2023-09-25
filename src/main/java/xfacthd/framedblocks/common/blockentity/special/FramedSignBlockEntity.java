package xfacthd.framedblocks.common.blockentity.special;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.sign.AbstractFramedSignBlock;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

public final class FramedSignBlockEntity extends FramedBlockEntity
{
    @Nullable
    private UUID editingPlayer;
    private SignText frontText = new SignText();
    private SignText backText = new SignText();
    private boolean waxed;
    private AABB renderBounds;

    private FramedSignBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.renderBounds = state.getShape(EmptyBlockGetter.INSTANCE, pos).bounds().move(pos);
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

    public SignText getFrontText()
    {
        return frontText;
    }

    public SignText getBackText()
    {
        return backText;
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
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public boolean updateText(UnaryOperator<SignText> modifier, boolean front)
    {
        return setText(modifier.apply(getText(front)), front);
    }

    public boolean setText(SignText text, boolean front)
    {
        return front ? setFrontText(text) : setBackText(text);
    }

    public boolean setFrontText(SignText text)
    {
        if (text != this.frontText) {
            frontText = text;

            //noinspection ConstantConditions
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
            setChanged();

            return true;
        }
        return false;
    }

    public boolean setBackText(SignText text)
    {
        if (text != backText)
        {
            backText = text;

            //noinspection ConstantConditions
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
            setChanged();

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

            //noinspection ConstantConditions
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
            setChanged();

            return true;
        }
        return false;
    }

    public boolean canExecuteCommands(boolean front, Player pPlayer)
    {
        return isWaxed() && getText(front).hasAnyClickCommands(pPlayer);
    }

    public boolean tryExecuteCommands(Player player, Level level, BlockPos pos, boolean front)
    {
        boolean executed = false;

        for (Component line : getText(front).getMessages(player.isTextFilteringEnabled()))
        {
            ClickEvent event = line.getStyle().getClickEvent();
            if (event != null && event.getAction() == ClickEvent.Action.RUN_COMMAND)
            {
                //noinspection ConstantConditions
                player.getServer().getCommands().performPrefixedCommand(
                        getCommandSource((ServerPlayer) player, (ServerLevel) level, pos),
                        event.getValue()
                );
                executed = true;
            }
        }

        return executed;
    }

    private static CommandSourceStack getCommandSource(ServerPlayer player, ServerLevel level, BlockPos pos)
    {
        String nameString = player == null ? "Sign" : player.getName().getString();
        Component name = player == null ? Component.literal("Sign") : player.getDisplayName();
        Vec3 posVec = Vec3.atCenterOf(pos);

        //noinspection ConstantConditions
        return new CommandSourceStack(CommandSource.NULL, posVec, Vec2.ZERO, level, 2, nameString, name, level.getServer(), player);
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

    @Override
    public boolean onlyOpCanSetNbt()
    {
        return true;
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, FramedSignBlockEntity be)
    {
        if (be.editingPlayer != null)
        {
            //noinspection ConstantConditions
            Player player = level.getPlayerByUUID(be.editingPlayer);
            if (be.isTooFarAwayToEdit(player))
            {
                be.editingPlayer = null;
            }
        }
    }

    public boolean isTooFarAwayToEdit(Player player)
    {
        return player == null || player.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) > 64.0D;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setBlockState(BlockState state)
    {
        super.setBlockState(state);
        //noinspection ConstantConditions
        renderBounds = state.getShape(level, worldPosition).bounds().move(worldPosition);
    }

    @Override
    public AABB getRenderBoundingBox()
    {
        return renderBounds;
    }

    @Override
    protected void writeToDataPacket(CompoundTag nbt)
    {
        super.writeToDataPacket(nbt);
        writeToNbt(nbt);
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        readFromNbt(nbt);
        return super.readFromDataPacket(nbt);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag nbt = super.getUpdateTag();
        writeToNbt(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt)
    {
        super.handleUpdateTag(nbt);
        readFromNbt(nbt);
    }

    private void writeToNbt(CompoundTag nbt)
    {
        SignText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, frontText)
                .resultOrPartial(FramedBlocks.LOGGER::error)
                .ifPresent(tag -> nbt.put("front_text", tag));
        SignText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, backText)
                .resultOrPartial(FramedBlocks.LOGGER::error)
                .ifPresent(tag -> nbt.put("back_text", tag));

        nbt.putBoolean("waxed", waxed);
    }

    private void readFromNbt(CompoundTag nbt)
    {
        if (nbt.contains("front_text"))
        {
            SignText.DIRECT_CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("front_text"))
                    .resultOrPartial(FramedBlocks.LOGGER::error)
                    .ifPresent(tag -> frontText = loadLines(level, worldPosition, tag));
        }

        if (nbt.contains("back_text"))
        {
            SignText.DIRECT_CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("back_text"))
                    .resultOrPartial(FramedBlocks.LOGGER::error)
                    .ifPresent(tag -> backText = loadLines(level, worldPosition, tag));
        }

        waxed = nbt.getBoolean("waxed");
    }

    private static SignText loadLines(Level level, BlockPos pos, SignText text)
    {
        for (int i = 0; i < 4; ++i)
        {
            Component line = loadLine(level, pos, text.getMessage(i, false));
            Component lineFiltered = loadLine(level, pos, text.getMessage(i, true));
            text = text.setMessage(i, line, lineFiltered);
        }

        return text;
    }

    private static Component loadLine(Level level, BlockPos pos, Component line)
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

    @Override //Prevent writing sign data
    public CompoundTag writeToBlueprint()
    {
        CompoundTag tag = saveWithoutMetadata();
        tag.remove("front_text");
        tag.remove("back_text");
        tag.remove("waxed");
        return tag;
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        writeToNbt(nbt);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        // TODO: remove in the next breaking update
        if (nbt.contains("text0") || nbt.contains("text1") || nbt.contains("text2") || nbt.contains("text3") || nbt.contains("glowingText") || nbt.contains("color"))
        {
            for (int i = 0; i < 4; i++)
            {
                String text = nbt.getString("text" + i);
                Component line = Component.Serializer.fromJson(text.isEmpty() ? "\"\"" : text);
                if (line != null)
                {
                    frontText.setMessage(i, line);
                }
            }

            frontText.setHasGlowingText(nbt.getBoolean("glowingText"));
            frontText.setColor(DyeColor.byName(nbt.getString("color"), DyeColor.BLACK));
            return;
        }
        readFromNbt(nbt);
    }



    public static FramedSignBlockEntity normalSign(BlockPos pos, BlockState state)
    {
        return new FramedSignBlockEntity(FBContent.BE_TYPE_FRAMED_SIGN.get(), pos, state);
    }

    public static FramedSignBlockEntity hangingSign(BlockPos pos, BlockState state)
    {
        return new FramedSignBlockEntity(FBContent.BE_TYPE_FRAMED_HANGING_SIGN.get(), pos, state);
    }
}
