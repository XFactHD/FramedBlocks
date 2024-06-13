package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.blueprint.AuxBlueprintData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.component.PottedFlower;

import java.util.List;
import java.util.Optional;

public class FramedFlowerPotBlockEntity extends FramedBlockEntity
{
    public static final ModelProperty<Block> FLOWER_BLOCK = new ModelProperty<>();

    private Block flowerBlock = Blocks.AIR;

    public FramedFlowerPotBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_FLOWER_POT.value(), pos, state);
    }

    public void setFlowerBlock(Block flowerBlock)
    {
        if (flowerBlock != this.flowerBlock)
        {
            this.flowerBlock = flowerBlock;

            setChangedWithoutSignalUpdate();
            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean hasFlowerBlock()
    {
        return flowerBlock != Blocks.AIR;
    }

    public Block getFlowerBlock()
    {
        return flowerBlock;
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean dropCamo)
    {
        super.addAdditionalDrops(drops, dropCamo);
        if (flowerBlock != Blocks.AIR)
        {
            drops.add(new ItemStack(flowerBlock));
        }
    }

    @Override
    protected void attachAdditionalModelData(ModelData.Builder builder)
    {
        builder.with(FLOWER_BLOCK, flowerBlock);
    }

    @Override
    protected void writeToDataPacket(CompoundTag nbt, HolderLookup.Provider lookupProvider)
    {
        super.writeToDataPacket(nbt, lookupProvider);
        nbt.putString("flower", BuiltInRegistries.BLOCK.getKey(flowerBlock).toString());
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt, HolderLookup.Provider lookupProvider)
    {
        Block flower = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(nbt.getString("flower")));

        boolean update = flower != flowerBlock;
        if (update)
        {
            flowerBlock = flower;
        }

        return super.readFromDataPacket(nbt, lookupProvider) || update;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider)
    {
        CompoundTag nbt = super.getUpdateTag(provider);

        nbt.putString("flower", BuiltInRegistries.BLOCK.getKey(flowerBlock).toString());

        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.handleUpdateTag(nbt, provider);

        Block flower = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(nbt.getString("flower")));
        if (flower != flowerBlock)
        {
            flowerBlock = flower;
        }
    }

    @Override
    protected Optional<AuxBlueprintData<?>> collectAuxBlueprintData()
    {
        return Optional.of(new PottedFlower(flowerBlock));
    }

    @Override
    protected void applyAuxDataFromBlueprint(AuxBlueprintData<?> auxData)
    {
        if (auxData instanceof PottedFlower flower && !flower.isEmpty())
        {
            flowerBlock = flower.flower();
        }
    }

    @Override
    protected void collectMiscComponents(DataComponentMap.Builder builder)
    {
        if (hasFlowerBlock())
        {
            builder.set(FBContent.DC_TYPE_POTTED_FLOWER, new PottedFlower(flowerBlock));
        }
    }

    @Override
    protected void applyMiscComponents(DataComponentInput input)
    {
        PottedFlower flower = input.getOrDefault(FBContent.DC_TYPE_POTTED_FLOWER, PottedFlower.EMPTY);
        if (!flower.isEmpty())
        {
            flowerBlock = flower.flower();
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putString("flower", BuiltInRegistries.BLOCK.getKey(flowerBlock).toString());
        super.saveAdditional(nbt, provider);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        flowerBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(nbt.getString("flower")));
    }
}
