package io.github.xfacthd.framedblocks.client.render.special;

import io.github.xfacthd.framedblocks.api.render.Quaternions;
import io.github.xfacthd.framedblocks.api.render.outline.OutlineRenderer;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.client.render.util.FramedRenderTypes;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import io.github.xfacthd.framedblocks.common.data.collapsible.HammerTarget;
import io.github.xfacthd.framedblocks.common.data.collapsible.TargetCalculator;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;

import java.util.Objects;

public final class CollapsibleBlockIndicatorRenderer
{
    private static final int LINE_COLOR = ARGB.color(153, 255, 0, 0);

    public static void onRenderBlockHighlight(ExtractBlockOutlineRenderStateEvent event)
    {
        Player player = Objects.requireNonNull(Minecraft.getInstance().player);
        if (!player.getMainHandItem().is(FBContent.ITEM_FRAMED_HAMMER)) return;

        Level level = Objects.requireNonNull(Minecraft.getInstance().level);
        BlockHitResult hit = event.getHitResult();
        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK)) return;
        if (!(level.getBlockEntity(pos) instanceof FramedCollapsibleBlockEntity be)) return;

        HammerTarget target = TargetCalculator.computeTarget(be, player, hit, true, 1F);
        if (target == null) return;

        Vec3 offset = Vec3.atLowerCornerOf(pos).subtract(event.getCamera().position());
        float[] vY = TargetCalculator.getVertexHeights(be, be.getCollapsedFace());

        event.addCustomRenderer((_, buffer, poseStack, translucentPass, _) ->
        {
            if (translucentPass)
            {
                poseStack.pushPose();
                poseStack.translate(offset.x + .5, offset.y + .5, offset.z + .5);
                if (target.face() == Direction.DOWN)
                {
                    poseStack.mulPose(Quaternions.XP_180);
                }
                else if (target.face() != Direction.UP)
                {
                    poseStack.mulPose(OutlineRenderer.YN_DIR[target.face().get2DDataValue()]);
                    poseStack.mulPose(Quaternions.XP_90);
                }
                poseStack.translate(-.5, -.5, -.5);

                OutlineRenderer.LineDrawer drawer = new BlockOutlineRenderer.DefaultLineDrawer(
                        poseStack.last(), buffer.getBuffer(FramedRenderTypes.LINES_NO_DEPTH), LINE_COLOR
                );
                drawSectionOverlay(drawer, vY);
                drawCornerMarkers(drawer, target.face(), target.pos(), vY);

                poseStack.popPose();
            }
            return false;
        });
    }

    private static void drawSectionOverlay(OutlineRenderer.LineDrawer drawer, float[] vY)
    {
        float cenx = Mth.lerp(.5F, vY[0], vY[1]); // center edge negative X
        float cepx = Mth.lerp(.5F, vY[3], vY[2]); // center edge positive X
        float cenz = Mth.lerp(.5F, vY[0], vY[3]); // center edge negative Z
        float cepz = Mth.lerp(.5F, vY[1], vY[2]); // center edge positive Z

        float cinx = Mth.lerp(.25F, cenx, cepx); // center inset negative X
        float cipx = Mth.lerp(.75F, cenx, cepx); // center inset positive X
        float cinz = Mth.lerp(.25F, cenz, cepz); // center inset negative Z
        float cipz = Mth.lerp(.75F, cenz, cepz); // center inset positive Z

        drawer.drawLine( .5F, cenz,   0F,  .5F, cinz, .25F);
        drawer.drawLine( .5F, cipz, .75F,  .5F, cepz,   1F);
        drawer.drawLine(  0F, cenx,  .5F, .25F, cinx,  .5F);
        drawer.drawLine(.75F, cipx,  .5F,   1F, cepx,  .5F);

        drawer.drawLine(.5F, cinz, .25F, .25F, cinx, .5F);
        drawer.drawLine(.5F, cinz, .25F, .75F, cipx, .5F);
        drawer.drawLine(.5F, cipz, .75F, .25F, cinx, .5F);
        drawer.drawLine(.5F, cipz, .75F, .75F, cipx, .5F);
    }

    private static void drawCornerMarkers(OutlineRenderer.LineDrawer drawer, Direction faceDir, Vec3 hitLocation, float[] vY)
    {
        int vert = FramedCollapsibleBlockEntity.vertexFromHit(faceDir, MathUtils.fraction(hitLocation));
        if (vert == 0 || vert == 4)
        {
            drawCubeFrame(drawer,  0.25F/16F,  0.25F/16F, vY[0]);
        }
        if (vert == 1 || vert == 4)
        {
            drawCubeFrame(drawer,  0.25F/16F, 15.75F/16F, vY[1]);
        }
        if (vert == 2 || vert == 4)
        {
            drawCubeFrame(drawer, 15.75F/16F, 15.75F/16F, vY[2]);
        }
        if (vert == 3 || vert == 4)
        {
            drawCubeFrame(drawer, 15.75F/16F,  0.25F/16F, vY[3]);
        }
    }

    private static void drawCubeFrame(OutlineRenderer.LineDrawer drawer, float x, float z, float vY)
    {
        float minX = x - .5F/16F;
        float maxX = x + .5F/16F;
        float minZ = z - .5F/16F;
        float maxZ = z + .5F/16F;

        float minY = vY - .75F/16F;
        float maxY = vY + .25F/16F;

        // Bottom
        drawer.drawLine(minX, minY, minZ, minX, minY, maxZ);
        drawer.drawLine(minX, minY, minZ, maxX, minY, minZ);
        drawer.drawLine(maxX, minY, minZ, maxX, minY, maxZ);
        drawer.drawLine(minX, minY, maxZ, maxX, minY, maxZ);

        // Top
        drawer.drawLine(minX, maxY, minZ, minX, maxY, maxZ);
        drawer.drawLine(minX, maxY, minZ, maxX, maxY, minZ);
        drawer.drawLine(maxX, maxY, minZ, maxX, maxY, maxZ);
        drawer.drawLine(minX, maxY, maxZ, maxX, maxY, maxZ);

        // Vertical
        drawer.drawLine(minX, minY, minZ, minX, maxY, minZ);
        drawer.drawLine(minX, minY, maxZ, minX, maxY, maxZ);
        drawer.drawLine(maxX, minY, minZ, maxX, maxY, minZ);
        drawer.drawLine(maxX, minY, maxZ, maxX, maxY, maxZ);
    }

    private CollapsibleBlockIndicatorRenderer() { }
}
