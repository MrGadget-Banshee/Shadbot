package me.shadorc.discordbot.data;

import org.json.JSONObject;

import me.shadorc.discordbot.utils.LogUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class Player {

	private final IGuild guild;
	private final IUser user;

	private int coins;

	public Player(IGuild guild, IUser user, JSONObject userObj) {
		this.guild = guild;
		this.user = user;
		this.coins = userObj == null ? 0 : userObj.getInt("coins");
	}

	public IGuild getGuild() {
		return guild;
	}

	public IUser getUser() {
		return user;
	}

	public int getCoins() {
		return coins;
	}

	public void addCoins(int gains) {
		try {
			this.coins = Math.max(0, Math.addExact(coins, gains));
		} catch (ArithmeticException err) {
			this.coins = Integer.MAX_VALUE;
			LogUtils.warn("User (ID: " + user.getLongID() + ") exceeded the maximum coins value.");
		}
		this.save();
	}

	public JSONObject toJSON() {
		JSONObject userJson = new JSONObject();
		userJson.put("coins", coins);
		return userJson;
	}

	private void save() {
		Storage.savePlayer(this);
	}
}