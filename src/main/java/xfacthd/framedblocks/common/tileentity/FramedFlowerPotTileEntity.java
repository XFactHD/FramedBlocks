package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;

public class FramedFlowerPotTileEntity extends FramedTileEntity
{
    public static final ModelProperty<Block> FLOWER_BLOCK = new ModelProperty<>();

    private Block flowerBlock = Blocks.AIR;

    public FramedFlowerPotTileEntity() { super(FBContent.blockEntityTypeFramedFlowerPot.get()); }

    public void setFlowerBlock(Block flowerBlock)
    {
        if (flowerBlock != this.flowerBlock)
        {
            this.flowerBlock = flowerBlock;

            markDirty();

            //noinspection ConstantConditions
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean hasFlowerBlock() { return flowerBlock != Blocks.AIR; }

    public Block getFlowerBlock() { return flowerBlock; }

    @Override
    public void addCamoDrops(List<ItemStack> drops)
    {
        super.addCamoDrops(drops);
        if (flowerBlock != Blocks.AIR)
        {
            drops.add(new ItemStack(flowerBlock));
        }
    }

    @Override
    protected void writeToDataPacket(CompoundNBT nbt)
    {
        super.writeToDataPacket(nbt);
        //noinspection ConstantConditions
        nbt.putString("flower", flowerBlock.getRegistryName().toString());
    }

    @Override
    protected boolean readFromDataPacket(CompoundNBT nbt)
    {
        Block flower = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("flower")));

        boolean update = flower != flowerBlock;
        if (update)
        {
            flowerBlock = flower;
            getModelData().setData(FLOWER_BLOCK, flower);
        }

        return super.readFromDataPacket(nbt) || update;
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        CompoundNBT nbt = super.getUpdateTag();

        //noinspection ConstantConditions
        nbt.putString("flower", flowerBlock.getRegistryName().toString());

        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt)
    {
        super.handleUpdateTag(state, nbt);

        Block flower = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("flower")));
        if (flower != flowerBlock)
        {
            flowerBlock = flower;
            getModelData().setData(FLOWER_BLOCK, flower);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        //noinspection ConstantConditions
        nbt.putString("flower", flowerBlock.getRegistryName().toString());
        return super.write(nbt);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        flowerBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("flower")));
    }
}