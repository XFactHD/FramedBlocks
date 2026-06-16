package io.github.xfacthd.framedblocks.api.block.blockentity;

import io.github.xfacthd.framedblocks.api.block.cache.DoubleBlockStateCache;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.block.render.CullingHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedDoubleBlockData;
import io.github.xfacthd.framedblocks.api.model.data.ModelDataEntry;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.TriState;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract sealed class ClientData<T extends FramedBlockEntity> {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int NEIGHBOR_MASK_NONE = 0b00000000;
    private static final int NEIGHBOR_MASK_ALL = 0b00111111;

    protected final T blockEntity;
    private final int sectionNeighborMask;
    private boolean cullStateDirty = false;
    @Nullable
    private ModelData lastModelData;

    static ClientData<?> create(FramedBlockEntity blockEntity) {
        if (blockEntity instanceof FramedDoubleBlockEntity doubleBlockEntity) {
            return new Double(doubleBlockEntity);
        }
        return new Single(blockEntity);
    }

    private ClientData(T blockEntity) {
        this.blockEntity = blockEntity;
        this.sectionNeighborMask = computeSectionNeighborMask(blockEntity.getBlockPos());
    }

    final void markCullStateDirty() {
        cullStateDirty = true;
    }

    final void notifyUpdateRequested() {
        lastModelData = null;
    }

    /// Marks dirty the "owning" chunk section, immediately adjacent ones if the block is on a section boundary and diagonally
    /// adjacent ones resulting from any two of these neighbors
    final void markSectionRangeDirty() {
        ClientAccess.setSectionsDirty(SectionPos.asLong(blockEntity.getBlockPos()), sectionNeighborMask, true, true);
    }

    final void updateCulling(boolean neighbors, boolean rerender) {
        updateCulling(neighbors ? NEIGHBOR_MASK_ALL : NEIGHBOR_MASK_NONE, rerender, true);
    }

    private CullingUpdateResult updateCulling(int blockNeighborMask, boolean rerender, boolean updateModelData) {
        Level level = blockEntity.level();
        BlockPos pos = blockEntity.getBlockPos();
        int sectionNeighborMask = this.sectionNeighborMask;
        ChunkAccess centerChunk = null;
        boolean changedSelf = false;
        int changedNeighbors = 0;
        boolean partial = false;

        for (Direction dir : DIRECTIONS) {
            changedSelf |= updateCulling(dir, false);

            int dirMask = 1 << dir.ordinal();
            if ((blockNeighborMask & dirMask) == 0) {
                continue;
            }

            ChunkAccess adjChunk;
            if ((sectionNeighborMask & dirMask) != 0 && !DirUtils.isY(dir)) {
                adjChunk = getChunk(level, pos, dir, false);
                if (adjChunk == null) {
                    partial = true;
                    continue;
                }
            } else {
                if (centerChunk == null) {
                    centerChunk = getChunk(level, pos, dir, true);
                }
                adjChunk = centerChunk;
            }

            if (adjChunk.getBlockEntity(pos.relative(dir)) instanceof IFramedBlockEntity be) {
                if (be.unwrap().clientData.updateCulling(dir.getOpposite(), true)) {
                    changedNeighbors |= dirMask;
                }
            }
        }

        if (updateModelData && changedSelf) {
            blockEntity.requestModelDataUpdate();
        }

        int changedNeighborSections = changedNeighbors & sectionNeighborMask;
        boolean includeCenter = rerender && (changedSelf || (changedNeighbors & ~sectionNeighborMask) != 0);
        ClientAccess.setSectionsDirty(SectionPos.asLong(pos), changedNeighborSections, includeCenter, false);

        return CullingUpdateResult.of(changedSelf, partial);
    }

    @Contract("_,_,_,true->!null")
    private static @Nullable ChunkAccess getChunk(Level level, BlockPos pos, Direction offset, boolean required) {
        int chunkX = SectionPos.blockToSectionCoord(pos.getX() + offset.getStepX());
        int chunkZ = SectionPos.blockToSectionCoord(pos.getZ() + offset.getStepZ());
        if (!required && (!ChunkPos.isValid(chunkX, chunkZ) || !level.isInsideBuildHeight(pos.getY() + offset.getStepY()))) {
            return null;
        }
        ChunkAccess chunk = level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, required);
        assert !required || chunk != null; // The getChunk() call already enforces this
        return chunk;
    }

    protected abstract boolean updateCulling(Direction side, boolean updateModelData);

    @SuppressWarnings("ClassEscapesDefinedScope")
    protected final boolean updateCulling(CullState culledFaces, BlockState testState, Direction side, boolean updateModelData) {
        boolean wasHidden = culledFaces.get(side);
        boolean hidden = CullingHelper.isSideHidden(blockEntity.level(), blockEntity.getBlockPos(), testState, blockEntity, side);
        if (wasHidden != hidden) {
            culledFaces.set(side, hidden);
            if (updateModelData) {
                blockEntity.requestModelDataUpdate();
            }
            return true;
        }
        return false;
    }

    final ModelData getModelData() {
        boolean retainData = false;
        if (cullStateDirty) {
            CullingUpdateResult result = updateCulling(sectionNeighborMask, false, false);
            if (result == CullingUpdateResult.SUCCESS) {
                cullStateDirty = false;
            } else {
                retainData = true;
            }
            if (result != CullingUpdateResult.UNCHANGED_PARTIAL) {
                lastModelData = null;
            }
        }
        ModelData modelData;
        if (retainData && lastModelData != null) {
            modelData = lastModelData;
        } else {
            modelData = getModelData(true, blockEntity.getBlockState());
        }
        if (retainData) {
            lastModelData = modelData;
            // Re-request a data update for the next time the BE's owning chunk section is queried for ModelData, hoping that the missing chunks are present by then.
            blockEntity.requestModelDataUpdateDirect();
        }
        return modelData;
    }

    final ModelData getModelData(boolean includeCullInfo, BlockState state) {
        AbstractFramedBlockData modelData = computeBlockData(state, includeCullInfo);
        ModelData.Builder builder = ModelData.builder().with(AbstractFramedBlockData.PROPERTY, modelData);
        blockEntity.attachAdditionalModelData(builder);
        return builder.build();
    }

    protected abstract AbstractFramedBlockData computeBlockData(BlockState state, boolean includeCullInfo);

    protected final FramedBlockData makeBlockData(BlockState state, CamoContainer<?, ?> camo, byte cullMask, boolean secondPart) {
        Level level = blockEntity.level();
        BlockPos pos = blockEntity.getBlockPos();
        boolean reinforced = blockEntity.isReinforced();
        boolean emissive = blockEntity.isEmissive();
        Holder<BlockOverlay> overlay = blockEntity.getOverlay();

        // The view-blocking value is never resolved from the second part, no point in computing it twice
        TriState viewBlocking = secondPart ? TriState.DEFAULT : Utils.toTriState(state.isSuffocating(level, pos));
        ModelDataEntry<?> queryData = camo.computeQueryData(level, pos);
        return new FramedBlockData(state, camo, cullMask, secondPart, reinforced, emissive, viewBlocking, overlay, queryData);
    }

    /// Computes the mask of directions towards neighbors in adjacent chunk sections
    private static int computeSectionNeighborMask(BlockPos pos) {
        int x = SectionPos.sectionRelative(pos.getX());
        int y = SectionPos.sectionRelative(pos.getY());
        int z = SectionPos.sectionRelative(pos.getZ());
        int mask = 0;
        if (x == 0) {
            mask |= 1 << Direction.WEST.ordinal();
        } else if (x == SectionPos.SECTION_MAX_INDEX) {
            mask |= 1 << Direction.EAST.ordinal();
        }
        if (y == 0) {
            mask |= 1 << Direction.DOWN.ordinal();
        } else if (y == SectionPos.SECTION_MAX_INDEX) {
            mask |= 1 << Direction.UP.ordinal();
        }
        if (z == 0) {
            mask |= 1 << Direction.NORTH.ordinal();
        } else if (z == SectionPos.SECTION_MAX_INDEX) {
            mask |= 1 << Direction.SOUTH.ordinal();
        }
        return mask;
    }

    private static final class Single extends ClientData<FramedBlockEntity> {
        private final CullState cullState = new CullState();

        private Single(FramedBlockEntity blockEntity) {
            super(blockEntity);
        }

        @Override
        protected boolean updateCulling(Direction side, boolean updateModelData) {
            return updateCulling(cullState, blockEntity.getBlockState(), side, updateModelData);
        }

        @Override
        protected AbstractFramedBlockData computeBlockData(BlockState state, boolean includeCullInfo) {
            return makeBlockData(state, blockEntity.getCamo(), cullState.computeMask(includeCullInfo, false), false);
        }
    }

    private static final class Double extends ClientData<FramedDoubleBlockEntity> {
        private final CullState cullStateOne = new CullState();
        private final CullState cullStateTwo = new CullState();

        private Double(FramedDoubleBlockEntity blockEntity) {
            super(blockEntity);
        }

        @Override
        protected boolean updateCulling(Direction side, boolean updateModelData) {
            DoubleBlockParts parts = blockEntity.getParts();
            boolean changedOne = updateCulling(cullStateOne, parts.stateOne(), side, updateModelData);
            boolean changedTwo = updateCulling(cullStateTwo, parts.stateTwo(), side, updateModelData);
            return changedOne || changedTwo;
        }

        @Override
        protected AbstractFramedBlockData computeBlockData(BlockState state, boolean includeCullInfo) {
            DoubleBlockStateCache stateCache = blockEntity.getBlock().getCache(state);
            DoubleBlockParts parts = stateCache.getParts();

            byte cullMaskOne = cullStateOne.computeMask(includeCullInfo, canCullNullFace(stateCache, false));
            FramedBlockData dataOne = makeBlockData(state, blockEntity.getCamo(), cullMaskOne, false);
            byte cullMaskTwo = cullStateTwo.computeMask(includeCullInfo, canCullNullFace(stateCache, true));
            FramedBlockData dataTwo = makeBlockData(state, blockEntity.getCamoTwo(), cullMaskTwo, true);
            return new FramedDoubleBlockData(parts, dataOne, dataTwo);
        }

        private boolean canCullNullFace(DoubleBlockStateCache stateCache, boolean secondPart) {
            // Cull-ability of one part checks against the solidity of the other part's camo
            return stateCache.mayCullNullFace(secondPart) && blockEntity.getCamo(!secondPart).getContent().isSolid();
        }
    }

    private static final class CullState {
        private byte mask = 0;

        private void set(Direction side, boolean occluded) {
            if (occluded) {
                mask |= (byte) (1 << side.ordinal());
            } else {
                mask &= (byte) ~(1 << side.ordinal());
            }
        }

        private boolean get(Direction side) {
            return (mask & (1 << side.ordinal())) != 0;
        }

        private byte computeMask(boolean includeCullInfo, boolean cullNullFace) {
            byte mask = 0;
            if (includeCullInfo) {
                mask = this.mask;
            }
            if (cullNullFace) {
                mask |= 0b01000000;
            }
            return mask;
        }
    }

    private enum CullingUpdateResult {
        /// Culling update was successful, all adjacent blocks were checked
        SUCCESS,
        /// Culling update detected changes but could not check all adjacent blocks (i.e. an adjacent chunk was inaccessible)
        CHANGED_PARTIAL,
        /// Culling update did not detect changes and could not check all adjacent blocks (i.e. an adjacent chunk was inaccessible)
        UNCHANGED_PARTIAL,
        ;

        private static CullingUpdateResult of(boolean changed, boolean partial) {
            if (partial) {
                return changed ? CHANGED_PARTIAL : UNCHANGED_PARTIAL;
            }
            return SUCCESS;
        }
    }

    private static final class ClientAccess {
        private static final int MARKER_INCLUDE_DIAGONALS = 0b01000000;
        private static final Neighbor[][] NEIGHBORS_BY_MASK = new NeighborTableBuilder().build();

        /// Marks all sections for re-rendering which could be affected by a change in the calling block.
        /// If `includeCenter` is `true` then `neighborMask` must be the block's full [sectionNeighborMask].
        private static void setSectionsDirty(long centerSection, int neighborMask, boolean includeCenter, boolean includeDiagonals) {
            LevelExtractor levelExtractor = Minecraft.getInstance().levelExtractor;
            int centerX = SectionPos.x(centerSection);
            int centerY = SectionPos.y(centerSection);
            int centerZ = SectionPos.z(centerSection);

            if (includeCenter) {
                setSectionDirty(levelExtractor, centerX, centerY, centerZ);
            }
            if (neighborMask != 0) {
                if (includeDiagonals) {
                    neighborMask |= MARKER_INCLUDE_DIAGONALS;
                }
                for (Neighbor neighbor : NEIGHBORS_BY_MASK[neighborMask]) {
                    neighbor.setDirty(levelExtractor, centerX, centerY, centerZ);
                }
            }
        }

        private static void setSectionDirty(LevelExtractor levelExtractor, int sectionX, int sectionY, int sectionZ) {
            levelExtractor.setSectionDirty(sectionX, sectionY, sectionZ);
        }

        private record Neighbor(int diffX, int diffY, int diffZ, int dirMask) {
            private Neighbor(Direction dir) {
                this(dir.getStepX(), dir.getStepY(), dir.getStepZ(), 1 << dir.ordinal());
            }

            private Neighbor(Direction dirOne, Direction dirTwo) {
                int diffX = dirOne.getStepX() + dirTwo.getStepX();
                int diffY = dirOne.getStepY() + dirTwo.getStepY();
                int diffZ = dirOne.getStepZ() + dirTwo.getStepZ();
                this(diffX, diffY, diffZ, MARKER_INCLUDE_DIAGONALS | (1 << dirOne.ordinal()) | (1 << dirTwo.ordinal()));
            }

            private void setDirty(LevelExtractor levelExtractor, int centerX, int centerY, int centerZ) {
                setSectionDirty(levelExtractor, centerX + diffX, centerY + diffY, centerZ + diffZ);
            }
        }

        private static final class NeighborTableBuilder {
            private final List<Neighbor> allNeighbors = new ArrayList<>();
            private final IntSet masks = new IntOpenHashSet();

            Neighbor[][] build() {
                for (Direction dir : DIRECTIONS) {
                    addNeighbor(new Neighbor(dir));
                    if (!DirUtils.isY(dir)) {
                        Neighbor cwNeighbor = addNeighbor(new Neighbor(dir, dir.getClockWise()));
                        Neighbor upNeighbor = addNeighbor(new Neighbor(dir, Direction.DOWN));
                        Neighbor downNeighbor = addNeighbor(new Neighbor(dir, Direction.UP));
                        addNeighborMask(cwNeighbor.dirMask | upNeighbor.dirMask);
                        addNeighborMask(cwNeighbor.dirMask | downNeighbor.dirMask);
                    }
                }

                Neighbor[][] byMask = new Neighbor[(NEIGHBOR_MASK_ALL + 1) * 2][];
                Arrays.fill(byMask, new Neighbor[0]);
                masks.forEach(mask -> {
                    List<Neighbor> maskNeighbors = new ArrayList<>();
                    for (Neighbor neighbor : allNeighbors) {
                        if ((mask & neighbor.dirMask) == neighbor.dirMask) {
                            maskNeighbors.add(neighbor);
                        }
                    }
                    if (!maskNeighbors.isEmpty()) {
                        byMask[mask] = maskNeighbors.toArray(Neighbor[]::new);
                    }
                });
                return byMask;
            }

            private Neighbor addNeighbor(Neighbor neighbor) {
                allNeighbors.add(neighbor);
                addNeighborMask(neighbor.dirMask);
                return neighbor;
            }

            private void addNeighborMask(int mask) {
                masks.add(mask & ~MARKER_INCLUDE_DIAGONALS);
                masks.add(mask | MARKER_INCLUDE_DIAGONALS);
            }
        }
    }
}
