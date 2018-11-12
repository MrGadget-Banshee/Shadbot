package me.shadorc.shadbot.listener;

import java.time.Duration;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.GatewayLifecycleEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import me.shadorc.shadbot.Shadbot;
import me.shadorc.shadbot.utils.DiscordUtils;
import me.shadorc.shadbot.utils.embed.log.LogUtils;
import reactor.core.publisher.Flux;

public class GatewayLifecycleListener {

	public static void onGatewayLifecycleEvent(GatewayLifecycleEvent event) {
		if(Shadbot.isReady()) {
			LogUtils.info("{Shard %d} %s",
					event.getClient().getConfig().getShardIndex(),
					// Add space before uppercase letters
					event.getClass().getSimpleName().replaceAll("([^_])([A-Z])", "$1 $2"));
		}
	}

	public static void onReadyEvent(ReadyEvent event) {
		final DiscordClient client = event.getClient();

		Flux.interval(Duration.ofHours(2), Duration.ofHours(2))
				.flatMap(ignored -> DiscordUtils.postStats(client))
				.doOnError(err -> LogUtils.error(client, err, "An error occurred while posting statistics."))
				.subscribe();

		Flux.interval(Duration.ZERO, Duration.ofMinutes(30))
				.flatMap(ignored -> DiscordUtils.updatePresence(client))
				.doOnError(err -> LogUtils.error(client, err, "An error occurred while updating presence."))
				.subscribe();
	}

}