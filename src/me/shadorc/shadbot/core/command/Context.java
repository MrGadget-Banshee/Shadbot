package me.shadorc.shadbot.core.command;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import discord4j.core.DiscordClient;
import discord4j.core.object.entity.ApplicationInfo;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.Snowflake;
import me.shadorc.shadbot.exception.MissingArgumentException;
import me.shadorc.shadbot.utils.StringUtils;
import me.shadorc.shadbot.utils.Utils;
import reactor.core.publisher.Mono;

public class Context {

	private final Optional<Snowflake> guildId;
	private final Message message;
	private final String prefix;
	private final String cmdName;
	private final Optional<String> arg;

	/**
	 * The message is stored because it does not need to be reactive, a command is received and executed, it does not need to adapt to message
	 * modifications
	 */
	public Context(@Nullable Snowflake guildId, Message message, String prefix) {
		this.guildId = Optional.ofNullable(guildId);
		this.message = message;
		this.prefix = prefix;

		List<String> splittedMsg = StringUtils.split(this.getContent(), 2);
		this.cmdName = splittedMsg.get(0).substring(prefix.length()).toLowerCase();
		this.arg = Optional.ofNullable(splittedMsg.size() > 1 ? splittedMsg.get(1) : null);
	}

	public Message getMessage() {
		return message;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getCommandName() {
		return cmdName;
	}

	public Optional<String> getArg() {
		return arg;
	}

	public DiscordClient getClient() {
		return this.getMessage().getClient();
	}

	public Mono<User> getSelf() {
		return this.getClient().getSelf();
	}

	public String getContent() {
		return this.getMessage().getContent().get();
	}

	public Optional<Snowflake> getGuildId() {
		return guildId;
	}

	public Mono<Guild> getGuild() {
		return this.getMessage().getGuild();
	}

	public Snowflake getChannelId() {
		return this.getMessage().getChannelId();
	}

	public Mono<MessageChannel> getChannel() {
		return this.getMessage().getChannel();
	}

	public Snowflake getAuthorId() {
		return this.getMessage().getAuthorId().get();
	}

	public Mono<User> getAuthor() {
		return this.getMessage().getAuthor();
	}

	public Mono<CommandPermission> getAuthorPermission() {
		// Is owner
		Mono<CommandPermission> ownerPerm = this.getClient().getApplicationInfo()
				.map(ApplicationInfo::getOwnerId)
				.filter(this.getAuthorId()::equals)
				.flatMap(bool -> {
					return Mono.just(CommandPermission.OWNER);
				});

		// Private message, the author is considered as admin
		Mono<CommandPermission> dmPerm = Mono.just(this.getGuildId())
				.filter(guildId -> !guildId.isPresent())
				.flatMap(guildId -> {
					return Mono.just(CommandPermission.ADMIN);
				});

		// The member is an administrator
		Mono<CommandPermission> adminperm = Utils.hasPermissions(this.getAuthor(), this.getGuildId().get(), Permission.ADMINISTRATOR)
				.map(bool -> {
					return CommandPermission.ADMIN;
				});

		// By default, the member is considered as a basic user
		Mono<CommandPermission> userPerm = Mono.just(CommandPermission.USER);

		return ownerPerm.switchIfEmpty(dmPerm.switchIfEmpty(adminperm.switchIfEmpty(userPerm)));
	}

	public Mono<Boolean> isChannelNsfw() {
		return this.getChannel().map(TextChannel.class::cast).map(TextChannel::isNsfw);
	}

	public void requireArg() throws MissingArgumentException {
		if(!this.getArg().isPresent()) {
			throw new MissingArgumentException();
		}
	}

}
