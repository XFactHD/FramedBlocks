package xfacthd.framedblocks.api.camo.empty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.camo.TriggerRegistrar;

public final class EmptyCamoContainerFactory extends CamoContainerFactory<EmptyCamoContainer>
{
    private static final MapCodec<EmptyCamoContainer> CODEC = MapCodec.unit(EmptyCamoContainer.EMPTY);

    @Override
    protected void writeToDisk(CompoundTag tag, EmptyCamoContainer container) { }

    @Override
    protected EmptyCamoContainer readFromDisk(CompoundTag tag)
    {
        return EmptyCamoContainer.EMPTY;
    }

    @Override
    protected void writeToNetwork(CompoundTag tag, EmptyCamoContainer container) { }

    @Override
    protected EmptyCamoContainer readFromNetwork(CompoundTag tag)
    {
        return EmptyCamoContainer.EMPTY;
    }

    @Override
    public EmptyCamoContainer applyCamo(Level level, BlockPos pos, Player player, ItemStack stack)
    {
        throw new UnsupportedOperationException("Empty camo container cannot be created from an ItemStack");
    }

    @Override
    public boolean removeCamo(Level level, BlockPos pos, Player player, ItemStack stack, EmptyCamoContainer container)
    {
        throw new UnsupportedOperationException("Empty camo container cannot be removed");
    }

    @Override
    public boolean canTriviallyConvertToItemStack()
    {
        return true;
    }

    @Override
    public ItemStack dropCamo(EmptyCamoContainer container)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean validateCamo(EmptyCamoContainer container)
    {
        return true;
    }

    @Override
    public MapCodec<EmptyCamoContainer> codec()
    {
        return CODEC;
    }

    @Override
    public void registerTriggerItems(TriggerRegistrar registrar) { }
}
