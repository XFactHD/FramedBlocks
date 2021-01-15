package xfacthd.framedblocks.common.tileentity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import xfacthd.framedblocks.common.FBContent;

import java.util.function.Function;

public class FramedSignTileEntity extends FramedTileEntity
{
    private final ITextComponent[] lines = new ITextComponent[4];
    private final String[] renderLines = new String[4];
    private DyeColor textColor = DyeColor.BLACK;
    private PlayerEntity editingPlayer;

    public FramedSignTileEntity()
    {
        super(FBContent.tileTypeFramedSign);
        for (int i = 0; i < 4; i++) { lines[i] = new StringTextComponent(""); }
    }

    public void setLine(int line, ITextComponent text)
    {
        lines[line] = text;
        renderLines[line] = null;
    }

    public ITextComponent getLine(int line) { return lines[line]; }

    public String getRenderedLine(int line, Function<ITextComponent, String> converter)
    {
        if (lines[line] != null && renderLines[line] == null)
        {
            renderLines[line] = converter.apply(lines[line]);
        }
        return renderLines[line];
    }

    public boolean executeCommand(ServerPlayerEntity player)
    {
        for(ITextComponent line : this.lines)
        {
            Style style = line == null ? null : line.getStyle();
            if (style != null && style.getClickEvent() != null)
            {
                ClickEvent clickevent = style.getClickEvent();
                if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND)
                {
                    //noinspection ConstantConditions
                    player.getServer().getCommandManager().handleCommand(getCommandSource(player), clickevent.getValue());
                }
            }
        }

        return true;
    }

    private CommandSource getCommandSource(ServerPlayerEntity player)
    {
        String nameString = player == null ? "Sign" : player.getName().getString();
        ITextComponent name = player == null ? new StringTextComponent("Sign") : player.getDisplayName();
        Vec3d posVec = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

        //noinspection ConstantConditions
        return new CommandSource(ICommandSource.DUMMY, posVec, Vec2f.ZERO, (ServerWorld)world, 2, nameString, name, world.getServer(), player);
    }



    public boolean setTextColor(DyeColor color)
    {
        if (textColor != color)
        {
            this.textColor = color;

            markDirty();
            //noinspection ConstantConditions
            world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), Constants.BlockFlags.DEFAULT);

            return true;
        }
        return false;
    }

    public DyeColor getTextColor() { return textColor; }

    public PlayerEntity getEditingPlayer() { return editingPlayer; }

    public void setEditingPlayer(PlayerEntity player) { this.editingPlayer = player; }

    @Override
    public boolean onlyOpsCanSetNbt() { return true; }



    @Override
    protected void writeToDataPacket(CompoundNBT nbt)
    {
        super.writeToDataPacket(nbt);
        writeToNbt(nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        super.onDataPacket(net, pkt);
        readFromNbt(pkt.getNbtCompound());
    }

    public CompoundNBT getUpdateTag()
    {
        CompoundNBT nbt = super.getUpdateTag();
        writeToNbt(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundNBT nbt)
    {
        super.handleUpdateTag(nbt);
        readFromNbt(nbt);
    }

    private void writeToNbt(CompoundNBT nbt)
    {
        for(int i = 0; i < 4; i++)
        {
            nbt.putString("text" + i, ITextComponent.Serializer.toJson(lines[i]));
        }

        nbt.putString("color", textColor.getTranslationKey());
    }

    private void readFromNbt(CompoundNBT nbt)
    {
        for(int i = 0; i < 4; i++)
        {
            String s = nbt.getString("text" + i);
            ITextComponent line = ITextComponent.Serializer.fromJson(s.isEmpty() ? "\"\"" : s);
            if (world instanceof ServerWorld && line != null)
            {
                try
                {
                    lines[i] = TextComponentUtils.updateForEntity(getCommandSource(null), line, null, 0);
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

        textColor = DyeColor.byTranslationKey(nbt.getString("color"), DyeColor.BLACK);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        writeToNbt(nbt);
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        readFromNbt(nbt);
    }
}