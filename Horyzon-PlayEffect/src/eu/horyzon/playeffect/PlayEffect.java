package eu.horyzon.playeffect;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class PlayEffect extends JavaPlugin implements CommandExecutor {
	private Map<UUID, PlayerEffect> effects;

	public void onEnable() {
		this.effects = new HashMap<UUID, PlayerEffect>();

		getLogger().info("TEST");

		getCommand("playeffect").setExecutor(this);
		getLogger().info(getName() + " correctly enabled!");
	}

	public void onDisable() {
		this.effects = null;
		getLogger().info(getName() + " correctly disabled!");
	}

	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (!(s instanceof Player)) {
			s.sendMessage("You must use this commands in game!");
			return true;
		}
		Player p = (Player) s;
		if (args.length == 1) {
			if ((args[0].equalsIgnoreCase("play")) || (args[0].equalsIgnoreCase("replay"))) {
				if (this.effects.containsKey(p.getUniqueId())) {
					((PlayerEffect) this.effects.get(p.getUniqueId())).play(p.getLocation());
				} else {
					p.sendMessage(ChatColor.RED + "Please load or create effect before to play it!");
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				TreeSet<String> effectList = new TreeSet<String>();
				Effect[] arrayOfEffect;
				int j = (arrayOfEffect = Effect.values()).length;
				for (int i = 0; i < j; i++) {
					Effect effect = arrayOfEffect[i];
					effectList.add(effect.name());
				}
				StringBuilder build = new StringBuilder();
				int i = 0;
				int size = effectList.size();
				for (String effect : effectList) {
					build.append(" " + effect);
					i++;
					if (i < size) {
						build.append(',');
					}
				}
				p.sendMessage(ChatColor.GOLD + "Effects available (" + size + "):" + build);
			} else {
				p.sendMessage(ChatColor.RED + "Please use /" + label + " <play|replay|list>");
			}
			return true;
		}
		if (args.length == 2) {
			if (!args[0].equalsIgnoreCase("save")) {
				if (!args[0].equalsIgnoreCase("load")) {
					p.sendMessage(ChatColor.RED + "Please use /" + label + " <save|load> <name>");
				}
			}
			return true;
		}
		if (args.length == 9) {
			try {
				PlayerEffect pe = new PlayerEffect(Effect.valueOf(args[0].toUpperCase()), Integer.parseInt(args[1]),
						Integer.parseInt(args[2]), Float.parseFloat(args[3]), Float.parseFloat(args[4]),
						Float.parseFloat(args[5]), Float.parseFloat(args[6]), Integer.parseInt(args[7]),
						Integer.parseInt(args[8]));

				pe.play(p.getLocation());
				this.effects.put(p.getUniqueId(), pe);

				return true;
			} catch (IllegalArgumentException localIllegalArgumentException) {
			}
		}
		p.sendMessage(ChatColor.RED + "Please use /" + label
				+ " <effect> <id> <data> <offsetX> <offsetY> <offsetZ> <speed> <particleCount> <radius>");
		return false;
	}

	public class PlayerEffect {
		private Effect effect;
		private int id;
		private int data;
		private int count;
		private int radius;
		private float offsetX;
		private float offsetY;
		private float offsetZ;
		private float speed;

		public PlayerEffect(Effect effect, int id, int data, float offsetX, float offsetY, float offsetZ, float speed,
				int count, int radius) {
			this.effect = effect;
			this.id = id;
			this.data = data;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.offsetZ = offsetZ;
			this.speed = speed;
			this.count = count;
			this.radius = radius;
		}

		public void play(Location loc) {
			loc.getWorld().spigot().playEffect(loc.add(loc.getDirection().clone().setY(0).normalize().multiply(3)),
					this.effect, this.id, this.data, this.offsetX, this.offsetY, this.offsetZ, this.speed, this.count,
					this.radius);
		}
	}
}
