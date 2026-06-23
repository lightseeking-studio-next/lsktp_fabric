package lsk.lsktp.lsktp;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class Lsktp implements ModInitializer {
	public static final String MOD_ID = "lsktp";


	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(
					Commands.literal("lsktp")
							.then(
									Commands.argument("pos", Vec3Argument.vec3())
											.executes(ctx -> execute_command(ctx.getSource(), ctx))
							)
							.then(
									Commands.argument("dimension", DimensionArgument.dimension())
											.then(
													Commands.argument("pos", Vec3Argument.vec3())
															.executes(ctx -> execute_command_d(ctx.getSource(), ctx))
											)
							)
			);
		});

	};



	static  int execute_command(CommandSourceStack source, CommandContext<CommandSourceStack> content)
	{
		 Entity entity  =source.getEntity();
		if (entity == null){
			source.sendFailure(Component.literal("需玩家执行"));
			return 0;
		}
		Vec3 pos =  Vec3Argument.getVec3(content,"pos");
		double x =pos.x;
		double y = pos.y;
		double z = pos.z;
		entity.teleportTo(x,y,z);
		source.sendSuccess(() -> Component.literal("传送至"+ String.format("%.2f", x) + ", " + String.format("%.2f", y) + ", " + String.format("%.2f", z)),false);
		return 1;
    }
	static  int execute_command_d(CommandSourceStack source, CommandContext<CommandSourceStack> content) throws CommandSyntaxException {
		Entity entity  =source.getEntity();
		if (entity == null){
			source.sendFailure(Component.literal("需玩家执行"));
			return 0;
		}
		Vec3 pos =  Vec3Argument.getVec3(content,"pos");
		double x =pos.x;
		double y = pos.y;
		double z = pos.z;

		ServerLevel level = DimensionArgument.getDimension(content,"dimension");
		entity.teleportTo(level,x,y,z, Collections.<Relative>emptySet(), 0f, 0f, false);
		source.sendSuccess(() -> Component.literal("传送至"+ String.format("%.2f", x) + ", " + String.format("%.2f", y) + ", " + String.format("%.2f", z)),false);
		return 1;
	}
}
