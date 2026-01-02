package com.yourmod.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.text.Text;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public class OreTagRenderer {

    private static final Set<Block> ORES = Set.of(
            Blocks.DIAMOND_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.IRON_ORE,
            Blocks.GOLD_ORE,
            Blocks.ANCIENT_DEBRIS
    );

    public static void init() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            render(context.matrixStack());
        });
    }

    private static void render(MatrixStack matrices) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        Camera camera = mc.gameRenderer.getCamera();
        Vec3d camPos = camera.getPos();
        TextRenderer textRenderer = mc.textRenderer;

        BlockPos playerPos = mc.player.getBlockPos();
        int range = 16; // render distance

        for (BlockPos pos : BlockPos.iterateOutwards(playerPos, range, range, range)) {
            Block block = mc.world.getBlockState(pos).getBlock();
            if (!ORES.contains(block)) continue;

            double dx = pos.getX() + 0.5 - camPos.x;
            double dy = pos.getY() + 1.2 - camPos.y;
            double dz = pos.getZ() + 0.5 - camPos.z;

            matrices.push();
            matrices.translate(dx, dy, dz);
            matrices.multiply(camera.getRotation());
            matrices.scale(-0.025f, -0.025f, 0.025f);

            String name = block.getName().getString();
            float width = textRenderer.getWidth(name) / 2f;

            VertexConsumerProvider.Immediate buffer =
                    mc.getBufferBuilders().getEntityVertexConsumers();

            textRenderer.draw(
                    Text.literal(name),
                    -width,
                    0,
                    0x00FFFF, // cyan
                    false,
                    matrices.peek().getPositionMatrix(),
                    buffer,
                    TextRenderer.TextLayerType.SEE_THROUGH,
                    0,
                    15728880
            );

            buffer.draw();
            matrices.pop();
        }
    }
}
