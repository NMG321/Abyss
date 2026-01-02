package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.impl.Render3DEvent;
import me.alpha432.oyvey.event.system.Subscribe;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.render.RenderUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.Set;

public class OreESP extends Module {

    public Setting<Color> color = color("Color", 0, 255, 255, 255);
    public Setting<Integer> range = num("Range", 16, 4, 64);

    private static final Set<Block> ORES = Set.of(
            Blocks.DIAMOND_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.IRON_ORE,
            Blocks.GOLD_ORE,
            Blocks.ANCIENT_DEBRIS,
            Blocks.EMERALD_ORE,
            Blocks.REDSTONE_ORE,
            Blocks.LAPIS_ORE
    );

    public OreESP() {
        super("OreESP", "Shows ore names with distance", Category.RENDER);
    }

    @Subscribe
    public void onRender3D(Render3DEvent event) {
        if (mc.player == null || mc.level == null) return;

        BlockPos playerPos = mc.player.blockPosition();
        Vec3 playerEye = mc.player.getEyePosition(1.0f);

        int r = range.getValue();

        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-r, -r, -r),
                playerPos.offset(r, r, r)
        )) {
            Block block = mc.level.getBlockState(pos).getBlock();
            if (!ORES.contains(block)) continue;

            Vec3 blockCenter = Vec3.atCenterOf(pos);

            double distance = Math.sqrt(playerEye.distanceToSqr(blockCenter));

            String text = block.getName().getString() +
                    String.format(" [%.1fm]", distance);

            RenderUtil.drawText3D(
                    event.getMatrix(),
                    text,
                    blockCenter.x,
                    blockCenter.y + 1.1,
                    blockCenter.z,
                    color.getValue().getRGB()
            );
        }
    }
}
