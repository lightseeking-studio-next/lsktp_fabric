package lsk.lsktp;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import java.util.HashSet;

public class Lsktp implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("lsktp")
                    .then(CommandManager.argument("x", DoubleArgumentType.doubleArg())
                            .then(CommandManager.argument("y", DoubleArgumentType.doubleArg())
                                    .then(CommandManager.argument("z", DoubleArgumentType.doubleArg())
                                            //   .executes(context -> teleportPlayer(context))
                                            .executes(context ->execute_command(context.getSource(),context))
                                    )
                            )
                    )
                    .requires(source -> source.hasPermissionLevel(0)) // 设置权限等级
            );
        });
    }
    static int execute_command(ServerCommandSource Source, CommandContext<ServerCommandSource> context){
        Entity entity =  Source.getPlayer() ;

        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");

        entity.teleport(Source.getWorld(),x , y, z, new HashSet<>(), entity.getYaw(), entity.getPitch());
//      return execute_command(Source, Radius, Player, Origin.getPos());
        context.getSource().sendFeedback(() -> Text.literal("传送至 " + x + ", " + y + ", " + z), false);

        return 1;
    }
}
