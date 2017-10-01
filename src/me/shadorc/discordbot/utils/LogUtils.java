package me.shadorc.discordbot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.shadorc.discordbot.Shadbot;
import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.data.Config;
import me.shadorc.discordbot.utils.command.Emoji;
import sx.blah.discord.handle.obj.IGuild;

public class LogUtils {

	public static final Logger LOGGER = LoggerFactory.getLogger("Logger");

	public static void error(String msg, Exception err, Context context) {
		LOGGER.error(msg + " (Input: " + context.getMessage().getContent() + ")", err);
		BotUtils.sendMessage(Emoji.RED_FLAG + " " + msg, context.getChannel());
		LogUtils.sendLogs("**[ERROR]** {User warned} " + msg
				+ "\nError message: " + err.getMessage()
				+ "\nInput: " + context.getMessage().getContent());
	}

	public static void error(String msg, Exception err) {
		LOGGER.error(msg, err);
		LogUtils.sendLogs("**[ERROR]** " + msg
				+ "\nError message: " + err.getMessage());
	}

	public static void error(String msg) {
		LOGGER.error(msg);
		LogUtils.sendLogs("**[ERROR]** " + msg);
	}

	public static void warn(String msg) {
		LOGGER.warn(msg);
		LogUtils.sendLogs("**[WARN]** " + msg);
	}

	public static void info(String msg) {
		LOGGER.info(msg);
	}

	public static void debug(Class<?> underlyingClass, IGuild guild, String msg) {
		LogUtils.info("{DEBUG} {" + underlyingClass.getSimpleName() + "} {Guild ID: " + guild.getLongID() + "} " + msg);
	}

	private static void sendLogs(String msg) {
		BotUtils.sendMessage(msg, Shadbot.getClient().getChannelByID(Config.LOGS_CHANNEL_ID));
	}
}
