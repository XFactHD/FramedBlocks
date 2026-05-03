package io.github.xfacthd.framedblocks.common.blockentity.special;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.NetworkValueInput;
import io.github.xfacthd.framedblocks.api.block.blockentity.RotationSource;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.PackedCollapsibleBlockOffsets;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.collapsible.HammerTarget;
import io.github.xfacthd.framedblocks.common.data.collapsible.NeighborVertex;
import io.github.xfacthd.framedblocks.common.data.collapsible.TargetCalculator;
import io.github.xfacthd.framedblocks.common.data.collapsible.VertexMappings;
import io.github.xfacthd.framedblocks.common.data.component.CollapsibleBlockData;
import io.github.xfacthd.framedblocks.common.data.property.NullableDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

public class FramedCollapsibleBlockEntity extends FramedBlockEntity implements CollapsibleBlockEntity {
    public static final int DIRECTIONS = Direction.values().length;
    private static final Direction[] HORIZONTAL_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
    public static final int VERTEX_COUNT = 4;
    private static final int BIT_PER_VERTEX = 5;
    private static final int VERTEX_MASK = ~(-1 << BIT_PER_VERTEX);

    private int packedOffsets = 0;

    public FramedCollapsibleBlockEntity(BlockPos pos, BlockState state) {
        super(FBContent.BE_TYPE_FRAMED_COLLAPSIBLE_BLOCK.value(), pos, state);
    }

    public void handleDeform(Player player) {
        HitResult hit = player.pick(10D, 1F, false);
        if (!(hit instanceof BlockHitResult blockHit)) {
            return;
        }

        HammerTarget target = TargetCalculator.computeTarget(this, player, blockHit, true, 1F);
        if (target == null) {
            return;
        }

        int vert = vertexFromHit(target.face(), target.pos());
        if (vert == 4) {
            for (int i = 0; i < 4; i++) {
                handleDeformOfVertex(player, target, i);
            }
        } else {
            handleDeformOfVertex(player, target, vert);
        }
    }

    private void handleDeformOfVertex(Player player, HammerTarget target, int vert) {
        Direction oldFace = target.oldFace();
        Direction faceHit = target.face();
        int offset = getVertexOffset(vert);
        if (player.isShiftKeyDown() && oldFace != null && offset > 0) {
            int newOffset = offset - 1;

            applyDeformation(vert, newOffset, faceHit, oldFace);
            deformNeighbors(faceHit, vert, newOffset);
        } else if (!player.isShiftKeyDown() && offset < 16) {
            int newOffset = offset + 1;

            applyDeformation(vert, newOffset, faceHit, oldFace);
            deformNeighbors(faceHit, vert, newOffset);
        }
    }

    private void applyDeformation(int vertex, int offset, Direction faceHit, @Nullable Direction oldFace) {
        offset = Mth.clamp(offset, 0, 16);

        if (offset == getVertexOffset(vertex)) {
            return;
        }

        setVertexOffset(vertex, offset);

        if (offset == 0) {
            boolean noOffsets = true;
            for (int i = 0; i < 4; i++) {
                if (getVertexOffset(i) > 0) {
                    noOffsets = false;
                    break;
                }
            }

            updateStateAndSync(oldFace, noOffsets ? null : oldFace);
        } else {
            updateStateAndSync(oldFace, faceHit);
        }

        setChangedWithoutSignalUpdate();
    }

    private void updateStateAndSync(@Nullable Direction oldFace, @Nullable Direction newFace) {
        if (oldFace != newFace) {
            NullableDirection face = NullableDirection.fromDirection(newFace);
            BlockState newState = getBlockState().setValue(PropertyHolder.NULLABLE_FACE, face);
            level().setBlock(worldPosition, newState, Block.UPDATE_ALL);
        } else {
            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void deformNeighbors(Direction faceHit, int srcVert, int offset) {
        NeighborVertex[] verts = VertexMappings.getNeighbors(faceHit, srcVert);
        for (int i = 0; i < 3; i++) {
            NeighborVertex vert = verts[i];
            BlockPos pos = worldPosition.offset(vert.offset());
            if (level().getBlockEntity(pos) instanceof FramedCollapsibleBlockEntity be) {
                Direction oldFace = be.getCollapsedFace();
                if (oldFace == null || oldFace == faceHit) {
                    be.applyDeformation(vert.targetVert(), offset, faceHit, oldFace);
                }
            }
        }
    }

    private void setVertexOffset(int vertex, int offset) {
        int idx = vertex * BIT_PER_VERTEX;
        int mask = VERTEX_MASK << idx;
        packedOffsets = (packedOffsets & ~mask) | (offset << idx);
    }

    public static int vertexFromHit(Direction faceHit, Vec3 loc) {
        if (DirUtils.isY(faceHit)) {
            double ax = Math.abs((loc.x - .5) * 4D);
            double az = Math.abs((loc.z - .5) * 4D);
            if (ax >= 0D && ax <= 1D && az >= 0D && az <= 1D && az <= (1D - ax)) {
                return 4;
            }

            if ((loc.z < .5F) == (faceHit == Direction.UP)) {
                return loc.x < .5F ? 0 : 3;
            } else {
                return loc.x < .5F ? 1 : 2;
            }
        } else {
            double xz = DirUtils.isX(faceHit) ? loc.z : loc.x;
            double axz = Math.abs((xz - .5) * 4D);
            double ay = Math.abs((loc.y - .5D) * 4D);
            if (axz >= 0D && axz <= 1D && ay >= 0D && ay <= 1D && ay <= (1D - axz)) {
                return 4;
            }

            boolean positive = faceHit == Direction.SOUTH || faceHit == Direction.WEST;
            if (loc.y < .5F) {
                return (xz < .5F) == positive ? 1 : 2;
            } else {
                return (xz < .5F) == positive ? 0 : 3;
            }
        }
    }

    public static int vertexFromCorner(Direction faceHit, Direction dirOne, Direction dirTwo) {
        if (DirUtils.isY(faceHit)) {
            Direction dirX = select(Direction.Axis.X, dirOne, dirTwo);
            Direction dirZ = select(Direction.Axis.Z, dirOne, dirTwo);
            if ((dirZ == Direction.NORTH) == (faceHit == Direction.UP)) {
                return dirX == Direction.WEST ? 0 : 3;
            } else {
                return dirX == Direction.WEST ? 1 : 2;
            }
        } else {
            Direction dirY = select(Direction.Axis.Y, dirOne, dirTwo);
            Direction dirXZ = select(DirUtils.isX(faceHit) ? Direction.Axis.Z : Direction.Axis.X, dirOne, dirTwo);
            boolean positive = faceHit == Direction.SOUTH || faceHit == Direction.WEST;
            if (dirY == Direction.DOWN) {
                return DirUtils.isPositive(dirXZ) != positive ? 1 : 2;
            } else {
                return DirUtils.isPositive(dirXZ) != positive ? 0 : 3;
            }
        }
    }

    private static Direction select(Direction.Axis axis, Direction dirOne, Direction dirTwo) {
        if (dirOne.getAxis() == axis) {
            return dirOne;
        } else if (dirTwo.getAxis() == axis) {
            return dirTwo;
        } else {
            throw new IllegalArgumentException("Neither %s nor %s fit axis %s".formatted(dirOne, dirTwo, axis));
        }
    }

    public @Nullable Direction getCollapsedFace() {
        return getBlockState().getValue(PropertyHolder.NULLABLE_FACE).toNullableDirection();
    }

    public int getVertexOffset(int vertex) {
        return packedOffsets >> (vertex * 5) & 0x1F;
    }

    @Override
    public int getVertexOffset(BlockState state, int vertex) {
        return getVertexOffset(vertex);
    }

    @Override
    public int getPackedOffsets(BlockState state) {
        return packedOffsets;
    }

    @Override
    protected boolean applyExternalRotation(Mirror mirror, Rotation rotation, RotationSource source) {
        int prevOffsets = packedOffsets;
        NullableDirection nullableDir = getBlockState().getValue(PropertyHolder.NULLABLE_FACE);
        if (nullableDir != NullableDirection.NONE && (mirror != Mirror.NONE || rotation != Rotation.NONE)) {
            Direction dir = nullableDir.toDirection();
            int[] offsets = new int[4];
            for (int i = 0; i < 4; i++) {
                offsets[i] = getVertexOffset(i);
            }

            if (mirror != Mirror.NONE) {
                Direction face;
                Direction.Axis mirrorAxis;
                Direction.Axis perpAxis;
                if (DirUtils.isY(dir)) {
                    face = dir;
                    mirrorAxis = DirUtils.getMirrorAxis(mirror);
                    perpAxis = DirUtils.getPerpendicularAxis(mirrorAxis, Direction.Axis.Y);
                } else {
                    NullableDirection unmirrored = nullableDir.rotate(DirUtils.getOppositeRotation(rotation)).mirror(mirror);
                    face = unmirrored.toDirection();
                    mirrorAxis = face.getClockWise().getAxis();
                    perpAxis = Direction.Axis.Y;
                }

                Direction mirrorNeg = mirrorAxis.getNegative();
                Direction mirrorPos = mirrorAxis.getPositive();
                Direction perpNeg = perpAxis.getNegative();
                Direction perpPos = perpAxis.getPositive();
                MathUtils.swap(offsets, vertexFromCorner(face, mirrorNeg, perpNeg), vertexFromCorner(face, mirrorPos, perpNeg));
                MathUtils.swap(offsets, vertexFromCorner(face, mirrorNeg, perpPos), vertexFromCorner(face, mirrorPos, perpPos));
            }

            if (DirUtils.isY(dir) && rotation != Rotation.NONE) {
                if (dir == Direction.UP) {
                    rotation = DirUtils.getOppositeRotation(rotation);
                }
                int[] offsetsCopy = Arrays.copyOf(offsets, offsets.length);
                for (Direction horDir : HORIZONTAL_DIRECTIONS) {
                    Direction outDir = rotation.rotate(horDir);
                    offsets[outDir.get2DDataValue()] = offsetsCopy[horDir.get2DDataValue()];
                }
            }

            for (int i = 0; i < 4; i++) {
                setVertexOffset(i, offsets[i]);
            }
        }
        if (source == RotationSource.STRUCTURE || packedOffsets != prevOffsets) {
            super.applyExternalRotation(mirror, rotation, source);
            return true;
        }
        return false;
    }

    @Override
    protected void attachAdditionalModelData(ModelData.Builder builder) {
        builder.with(PackedCollapsibleBlockOffsets.PROPERTY, new PackedCollapsibleBlockOffsets.Single(packedOffsets));
    }

    @Override
    protected void writeToDataPacket(ValueOutput valueOutput) {
        super.writeToDataPacket(valueOutput);
        valueOutput.putInt("offsets", packedOffsets);
    }

    @Override
    protected void readFromDataPacket(NetworkValueInput input) {
        super.readFromDataPacket(input);

        int packed = input.getIntOr("offsets", 0);
        if (packed != packedOffsets) {
            packedOffsets = packed;

            input.requestRenderUpdate();
            input.requestCullingUpdate();
        }
    }

    @Override
    protected BlueprintData appendCustomBlueprintData(BlueprintData blueprintData) {
        return blueprintData.withCustomData(FBContent.DC_TYPE_COLLAPSIBLE_BLOCK_DATA, new CollapsibleBlockData(packedOffsets));
    }

    @Override
    protected void applyCustomDataFromBlueprint(TypedDataComponent<?> auxData) {
        if (auxData.value() instanceof CollapsibleBlockData(int offsets)) {
            packedOffsets = offsets;
        }
    }

    @Override
    public void removeComponentsFromTag(ValueOutput valueOutput) {
        super.removeComponentsFromTag(valueOutput);
        valueOutput.discard("offsets");
        valueOutput.discard("face");
    }

    @Override
    protected void collectMiscComponents(DataComponentMap.Builder builder) {
        builder.set(FBContent.DC_TYPE_COLLAPSIBLE_BLOCK_DATA, new CollapsibleBlockData(packedOffsets));

        BlockState state = getBlockState();
        BlockItemStateProperties stateProperties = BlockItemStateProperties.EMPTY
                .with(PropertyHolder.NULLABLE_FACE, state)
                .with(PropertyHolder.ROTATE_SPLIT_LINE, state);
        builder.set(DataComponents.BLOCK_STATE, stateProperties);
    }

    @Override
    protected void applyMiscComponents(DataComponentGetter input) {
        CollapsibleBlockData blockData = input.get(FBContent.DC_TYPE_COLLAPSIBLE_BLOCK_DATA);
        if (blockData != null) {
            packedOffsets = blockData.offsets();
        }
    }

    @Override
    public void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.putInt("offsets", packedOffsets);
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        packedOffsets = valueInput.getIntOr("offsets", 0);
    }

    public static byte[] unpackOffsets(int packed) {
        byte[] offsets = new byte[4];
        for (int i = 0; i < 4; i++) {
            offsets[i] = (byte) (packed >> (i * 5) & 0x1F);
        }
        return offsets;
    }
}
