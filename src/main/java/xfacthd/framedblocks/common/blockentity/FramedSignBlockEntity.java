package xfacthd.framedblocks.common.blockentity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Constants;
import xfacthd.framedblocks.common.FBContent;

import java.util.function.Function;

public class FramedSignBlockEntity extends FramedBlockEntity
{
    private final Component[] lines = new Component[4];
    private final FormattedCharSequence[] renderLines = new FormattedCharSequence[4];
    private DyeColor textColor = DyeColor.BLACK;
    private Player editingPlayer;

    public FramedSignBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedSign.get(), pos, state);
        for (int i = 0; i < 4; i++) { lines[i] = new TextComponent(""); }
    }

    public void setLine(int line, Component text)
    {
        lines[line] = text;
        renderLines[line] = null;
    }

    public Component getLine(int line) { return lines[line]; }

    public FormattedCharSequence getRenderedLine(int line, Function<Component, FormattedCharSequence> converter)
    {
        if (lines[line] != null && renderLines[line] == null)
        {
            renderLines[line] = converter.apply(lines[line]);
        }
        return renderLines[line];
    }

    public boolean executeCommand(ServerPlayer player)
    {
        for(Component line : this.lines)
        {
            Style style = line == null ? null : line.getStyle();
            if (style != null && style.getClickEvent() != null)
            {
                ClickEvent clickevent = style.getClickEvent();
                if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND)
                {
                    //noinspection ConstantConditions
                    player.getServer().getCommands().performCommand(getCommandSource(player), clickevent.getValue());
                }
            }
        }

        return true;
    }

    private CommandSourceStack getCommandSource(ServerPlayer player)
    {
        String nameString = player == null ? "Sign" : player.getName().getString();
        Component name = player == null ? new TextComponent("Sign") : player.getDisplayName();
        Vec3 posVec = new Vec3(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D);

        //noinspection ConstantConditions
        return new CommandSourceStack(CommandSource.NULL, posVec, Vec2.ZERO, (ServerLevel)level, 2, nameString, name, level.getServer(), player);
    }



    public boolean setTextColor(DyeColor color)
    {
        if (textColor != color)
        {
            this.textColor = color;

            setChanged();
            //noinspection ConstantConditions
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.DEFAULT);

            return true;
        }
        return false;
    }

    public DyeColor getTextColor() { return textColor; }

    public Player getEditingPlayer() { return editingPlayer; }

    public void setEditingPlayer(Player player) { this.editingPlayer = player; }

    @Override
    public boolean onlyOpCanSetNbt() { return true; }



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
        for(int i = 0; i < 4; i++)
        {
            nbt.putString("text" + i, Component.Serializer.toJson(lines[i]));
        }

        nbt.putString("color", textColor.getName());
    }

    private void readFromNbt(CompoundTag nbt)
    {
        for(int i = 0; i < 4; i++)
        {
            String s = nbt.getString("text" + i);
            Component line = Component.Serializer.fromJson(s.isEmpty() ? "\"\"" : s);
            if (level instanceof ServerLevel && line != null)
            {
                try
                {
                    lines[i] = ComponentUtils.updateForEntity(getCommandSource(null), line, null, 0);
                }
                catch (CommandSyntaxException e)
                {
                    lines[i] = line;
                }
            }
            else
            {
                lines[i] = line;
            }

            renderLines[i] = null;
        }

        textColor = DyeColor.byName(nbt.getString("color"), DyeColor.BLACK);
    }

    @Override //Prevent writing sign data
    public CompoundTag writeToBlueprint() { return super.save(new CompoundTag()); }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        writeToNbt(nbt);
        return super.save(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        readFromNbt(nbt);
    }
}