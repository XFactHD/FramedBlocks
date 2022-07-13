package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;

public class FramedFlowerPotBlockEntity extends FramedBlockEntity
{
    public static final ModelProperty<Block> FLOWER_BLOCK = new ModelProperty<>();

    private Block flowerBlock = Blocks.AIR;

    public FramedFlowerPotBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedFlowerPot.get(), pos, state);
    }

    public void setFlowerBlock(Block flowerBlock)
    {
        if (flowerBlock != this.flowerBlock)
        {
            this.flowerBlock = flowerBlock;

            setChanged();

            //noinspection ConstantConditions
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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
    protected void writeToDataPacket(CompoundTag nbt)
    {
        super.writeToDataPacket(nbt);
        //noinspection ConstantConditions
        nbt.putString("flower", flowerBlock.getRegistryName().toString());
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        Block flower = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("flower")));

        boolean update = flower != flowerBlock;
        if (update)
        {
            flowerBlock = flower;
            getModelDataInternal().setData(FLOWER_BLOCK, flower);
        }

        return super.readFromDataPacket(nbt) || update;
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag nbt = super.getUpdateTag();

        //noinspection ConstantConditions
        nbt.putString("flower", flowerBlock.getRegistryName().toString());

        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt)
    {
        super.handleUpdateTag(nbt);

        Block flower = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("flower")));
        if (flower != flowerBlock)
        {
            flowerBlock = flower;
            getModelDataInternal().setData(FLOWER_BLOCK, flower);
        }
    }

    @Override
    protected void initModelData()
    {
        super.initModelData();
        getModelDataInternal().setData(FLOWER_BLOCK, flowerBlock);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        //noinspection ConstantConditions
        nbt.putString("flower", flowerBlock.getRegistryName().toString());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        flowerBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("flower")));
    }
}