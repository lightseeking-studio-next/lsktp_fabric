package lsk.lsktp;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Lsktp implements ModInitializer {

    private static final SimpleCommandExceptionType INVALID_COORD = new SimpleCommandExceptionType(Text.literal("无效的坐标格式"));

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("lsktp")
                    .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                            .executes(context -> execute_command(context.getSource(), context))
                    )
            );
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("lsktp")
                    .then(CommandManager.argument("d", DimensionArgumentType.dimension())
                            .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                                    .executes(context -> execute(context.getSource(), context))
                            )
                    )
            );
        });
    }

    private static int findSafeY(ServerWorld world, double x, double z) {
        BlockPos blockPos = new BlockPos((int) x, 0, (int) z);
        int maxY = world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, blockPos);
        int minY = world.getBottomY();

        // 从高处向下搜索第一个安全的 Y 坐标
        for (int y = maxY; y >= minY; y--) {
            BlockPos pos = new BlockPos((int) x, y, (int) z);
            BlockState above = world.getBlockState(pos.up());
            BlockState at = world.getBlockState(pos);
            BlockState below = world.getBlockState(pos.down());

            // 检查上方是空气，当前位置是固体或流体，下方是固体
            boolean aboveAir = above.isAir();
            boolean atSolidOrFluid = !at.isAir() || at.getBlock() instanceof FluidBlock;
            boolean belowSolid = !below.isAir();

            if (aboveAir && atSolidOrFluid && belowSolid) {
                return y + 1; // 返回安全的传送位置（在固体方块上方）
            }
        }
        return minY; // 如果找不到安全位置，返回最低点
    }
    //2026.2.10 21:38更新：原版y轴的～竟然不是安全坐标！而是原坐标！

    static int execute_command(ServerCommandSource source, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Entity entity = source.getPlayer();
        if (entity == null) {
            source.sendError(Text.literal("需玩家执行！"));
            return 0;
        }

        Vec3d pos = Vec3ArgumentType.getVec3(context, "pos");

        double x = pos.x;
        double z = pos.z;
        double y = pos.y;

        ServerWorld world = source.getWorld();
/*
        // 检查 y 是否使用 ~ 符号（通过检查原始输入）
        String input = context.getInput();
        String[] parts = input.split(" ");
        if (parts.length >= 3 && parts[2].equals("~")) {
            y = findSafeY(world, x, z);
        } else {
            y = pos.y;
        }
*/
        entity.teleport(world, x, y, z, new java.util.HashSet<>(), entity.getYaw(), entity.getPitch(), false);
        source.sendFeedback(() -> Text.literal("传送至 " + String.format("%.2f", x) + ", " + String.format("%.2f", y) + ", " + String.format("%.2f", z)), false);

        return 1;
    }

    private static int execute(ServerCommandSource source, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Entity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("需玩家执行！"));
            return 0;
        }

        Vec3d pos = Vec3ArgumentType.getVec3(context, "pos");

        double x = pos.x;
        double z = pos.z;
        double y = pos.y;

        ServerWorld dimension = DimensionArgumentType.getDimensionArgument(context, "d");
/*
        // 检查 y 是否使用 ~ 符号（通过检查原始输入）
        String input = context.getInput();
        String[] parts = input.split(" ");
        if (parts.length >= 4 && parts[3].equals("~")) {
            y = findSafeY(dimension, x, z);
        } else {
            y = pos.y;
        }
        */


        player.teleport(dimension, x, y, z, new java.util.HashSet<>(), player.getYaw(), player.getPitch(), false);
        source.sendFeedback(() -> Text.literal("传送至 " + String.format("%.2f", x) + ", " + String.format("%.2f", y) + ", " + String.format("%.2f", z)), false);

        return 1;
    }
}