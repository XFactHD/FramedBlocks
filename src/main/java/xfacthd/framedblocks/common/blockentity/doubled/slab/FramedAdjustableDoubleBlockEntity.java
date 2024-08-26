package xfacthd.framedblocks.common.blockentity.doubled.slab;

import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import xfacthd.framedblocks.api.blueprint.AuxBlueprintData;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.slab.FramedAdjustableDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.special.*;
import xfacthd.framedblocks.common.data.component.AdjustableDoubleBlockData;

import java.util.Objects;
import java.util.Optional;

public class FramedAdjustableDoubleBlockEntity extends FramedDoubleBlockEntity implements ICollapsibleBlockEntity, ICollapsibleCopycatBlockEntity
{
    private static final int MIN_PART_HEIGHT = 1;
    private static final int MAX_PART_HEIGHT = 15;
    public static final int CENTER_PART_HEIGHT = 8;

    private final ModelProperty<Integer> offsetProperty;
    private final OffsetPacker offsetPacker;
    private int firstHeight = CENTER_PART_HEIGHT;

    private FramedAdjustableDoubleBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            ModelProperty<Integer> offsetProperty,
            OffsetPacker offsetPacker
    )
    {
        super(type, pos, state);
        this.offsetProperty = offsetProperty;
        this.offsetPacker = offsetPacker;
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
    {
        Direction facing = getFacing(getBlockState());
        Direction face = hit.getDirection();
        if (face == facing.getOpposite()) return false;
        if (face == facing) return true;

        int y = (int)(Utils.fractionInDir(hit.getLocation(), facing) * 16F);
        return y > firstHeight;
    }

    public boolean handleDeform(Player player)
    {
        HitResult hit = player.pick(10D, 1F, false);
        if (!(hit instanceof BlockHitResult blockHit))
        {
            return false;
        }

        Direction facing = getFacing(getBlockState());
        Direction faceHit = blockHit.getDirection();
        if (faceHit.getAxis() != facing.getAxis())
        {
            return false;
        }

        if (!level().isClientSide())
        {
            boolean upwards = faceHit == facing.getOpposite() ^ player.isShiftKeyDown();
            boolean changed = false;
            if (!upwards && firstHeight > MIN_PART_HEIGHT)
            {
                firstHeight--;
                changed = true;
            }
            else if (upwards && firstHeight < MAX_PART_HEIGHT)
            {
                firstHeight++;
                changed = true;
            }
            if (changed)
            {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                setChangedWithoutSignalUpdate();
            }
        }
        return true;
    }

    @Override
    public int getVertexOffset(BlockState state, int vertex)
    {
        Tuple<BlockState, BlockState> blockPair = getBlockPair();
        if (state == blockPair.getA())
        {
            return 16 - firstHeight;
        }
        if (state == blockPair.getB())
        {
            return firstHeight;
        }
        return 0;
    }

    @Override
    public int getFaceOffset(BlockState state, Direction side)
    {
        Direction facing = getFacing(getBlockState());
        Tuple<BlockState, BlockState> blockPair = getBlockPair();
        if (state == blockPair.getA() && side == facing)
        {
            return 16 - firstHeight;
        }
        if (state == blockPair.getB() && side == facing.getOpposite())
        {
            return firstHeight;
        }
        return 0;
    }

    @Override
    public int getPackedOffsets(BlockState state)
    {
        Tuple<BlockState, BlockState> blockPair = getBlockPair();
        if (state == blockPair.getA())
        {
            return offsetPacker.pack(getBlockState(), firstHeight, false);
        }
        if (state == blockPair.getB())
        {
            return offsetPacker.pack(getBlockState(), firstHeight, true);
        }
        return 0;
    }

    @Override
    public ModelData getModelData(boolean includeCullInfo)
    {
        BlockState state = getBlockState();
        ModelData data = super.getModelData(includeCullInfo);
        ModelData dataLeft = Objects.requireNonNull(data.get(DATA_ONE))
                .derive()
                .with(offsetProperty, offsetPacker.pack(state, firstHeight, false))
                .build();
        ModelData dataRight = Objects.requireNonNull(data.get(DATA_TWO))
                .derive()
                .with(offsetProperty, offsetPacker.pack(state, firstHeight, true))
                .build();
        return data.derive().with(DATA_ONE, dataLeft).with(DATA_TWO, dataRight).build();
    }

    private static Direction getFacing(BlockState state)
    {
        return ((FramedAdjustableDoubleBlock) state.getBlock()).getFacing(state);
    }

    @Override
    protected void writeToDataPacket(CompoundTag nbt, HolderLookup.Provider lookupProvider)
    {
        super.writeToDataPacket(nbt, lookupProvider);
        nbt.putInt("first_height", firstHeight);
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt, HolderLookup.Provider lookupProvider)
    {
        boolean needUpdate = super.readFromDataPacket(nbt, lookupProvider);

        int height = nbt.getInt("first_height");
        if (height != firstHeight)
        {
            firstHeight = height;

            needUpdate = true;
            updateCulling(true, false);
        }

        return needUpdate;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider)
    {
        CompoundTag nbt = super.getUpdateTag(provider);
        nbt.putInt("first_height", firstHeight);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.handleUpdateTag(nbt, provider);
        firstHeight = nbt.getInt("first_height");
    }

    @Override
    protected Optional<AuxBlueprintData<?>> collectAuxBlueprintData()
    {
        return Optional.of(new AdjustableDoubleBlockData(firstHeight));
    }

    @Override
    protected void applyAuxDataFromBlueprint(AuxBlueprintData<?> auxData)
    {
        if (auxData instanceof AdjustableDoubleBlockData blockData)
        {
            firstHeight = blockData.firstHeight();
        }
    }

    @Override
    protected void collectMiscComponents(DataComponentMap.Builder builder)
    {
        builder.set(FBContent.DC_TYPE_ADJ_DOUBLE_BLOCK_DATA, new AdjustableDoubleBlockData(firstHeight));
    }

    @Override
    protected void applyMiscComponents(DataComponentInput input)
    {
        AdjustableDoubleBlockData blockData = input.get(FBContent.DC_TYPE_ADJ_DOUBLE_BLOCK_DATA);
        if (blockData != null)
        {
            firstHeight = blockData.firstHeight();
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.saveAdditional(nbt, provider);
        nbt.putInt("first_height", firstHeight);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        firstHeight = nbt.getInt("first_height");
    }



    public static FramedAdjustableDoubleBlockEntity standard(BlockPos pos, BlockState state)
    {
        return new FramedAdjustableDoubleBlockEntity(
                FBContent.BE_TYPE_FRAMED_ADJ_DOUBLE_BLOCK.value(),
                pos,
                state,
                FramedCollapsibleBlockEntity.OFFSETS,
                FramedAdjustableDoubleBlockEntity::getPackedOffsetsStandard
        );
    }

    public static FramedAdjustableDoubleBlockEntity copycat(BlockPos pos, BlockState state)
    {
        return new FramedAdjustableDoubleBlockEntity(
                FBContent.BE_TYPE_FRAMED_ADJ_DOUBLE_COPYCAT_BLOCK.value(),
                pos,
                state,
                FramedCollapsibleCopycatBlockEntity.OFFSETS,
                FramedAdjustableDoubleBlockEntity::getPackedOffsetsCopycat
        );
    }

    public static int getPackedOffsetsStandard(BlockState state, int firstHeight, boolean right)
    {
        if (!right)
        {
            firstHeight = 16 - firstHeight;
        }

        int result = 0;
        for (int i = 0; i < 4; i++)
        {
            result |= (firstHeight << (i * 5));
        }
        return result;
    }

    public static int getPackedOffsetsCopycat(BlockState state, int firstHeight, boolean right)
    {
        Direction facing = getFacing(state);
        if (right)
        {
            facing = facing.getOpposite();
        }
        else
        {
            firstHeight = 16 - firstHeight;
        }
        return firstHeight << (facing.ordinal() * 4);
    }

    public interface OffsetPacker
    {
        int pack(BlockState state, int firstHeight, boolean right);
    }
}
