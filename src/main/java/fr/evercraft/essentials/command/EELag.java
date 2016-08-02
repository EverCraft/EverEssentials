/*
 * This file is part of EverEssentials.
 *
 * EverEssentials is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EverEssentials is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EverEssentials.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.evercraft.essentials.command;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.World;

import com.google.common.collect.Iterables;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.java.UtilsDouble;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.text.ETextBuilder;

public class EELag extends ECommand<EverEssentials> {
	
	private static final double HISTORY_LENGTH = 15;
	private static final int TPS_LENGTH = 2;
	
	private Task scheduler;
	private List<Double> historys;
	
	public EELag(final EverEssentials plugin) {
        super(plugin, "lag", "gc", "tps");

        this.historys = new ArrayList<Double>();
        
        start();
    }
	
	private void start() {
		this.historys = new ArrayList<Double>();
		this.scheduler = this.plugin.getGame().getScheduler().createTaskBuilder().execute(new Runnable() {
			    public void run() {
			    	historys.add(getTPS());
			    	if(historys.size() > HISTORY_LENGTH) {
			    		historys.remove(0);
			    	}
			    }
			}).interval(1, TimeUnit.MINUTES).submit(this.plugin);
	}
	
	private void stop() {
		this.scheduler.cancel();
	}
	
	public void reload() {
		this.stop();
		this.start();
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.LAG.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.LAG_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		ManagementFactory.getRuntimeMXBean().getStartTime();
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 0) {
			resultat = commandLag(source);
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandLag(final CommandSource player) {
		Double tps = getTPS();
		
		List<Text> list = new ArrayList<Text>();
		
		list.add(EChat.of(EEMessages.LAG_TIME.get()
				.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()))));
		list.add(ETextBuilder.toBuilder(EEMessages.LAG_TPS.get())
				.replace("<tps>", Text.builder(tps.toString()).color(getColorTPS(tps)).build())
				.build());
		list.add(ETextBuilder.toBuilder(EEMessages.LAG_HISTORY_TPS.get())
					.replace("<tps>", getHistoryTPS()).build());
		list.add(EChat.of(EEMessages.LAG_MEMORY.get()
				.replaceAll("<usage>", String.valueOf(Runtime.getRuntime().totalMemory()/1024/1024))
				.replaceAll("<total>", String.valueOf(Runtime.getRuntime().maxMemory()/1024/1024))));
		list.add(EChat.of(EEMessages.LAG_WORLDS.get()));
		for(World world : this.plugin.getEServer().getWorlds()) {
			list.add(EChat.of(EEMessages.LAG_WORLDS_LINE.get().replaceAll("<world>", world.getName())).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.LAG_WORLDS_LINE_HOVER.get()
							.replaceAll("<entities>", String.valueOf(world.getEntities().size()))
							.replaceAll("<tiles>", String.valueOf(world.getTileEntities().size()))
							.replaceAll("<chunks>", String.valueOf(Iterables.size(world.getLoadedChunks()))))))
					.build());
		}
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EChat.of(EEMessages.LAG_TITLE.get()).toBuilder()
				.onClick(TextActions.runCommand("/lag")).build(), list, player);
		return true;
	}
	
	public Text getHistoryTPS() {
		Builder resultat = Text.builder();
		for(int cpt = 0; cpt < this.historys.size(); cpt++) {
			double tps = this.historys.get(this.historys.size() - cpt - 1);
			resultat.append(getHistoryTPSIcon(tps, cpt + 1));
		}
		return resultat.build();
	}
	
	public Text getHistoryTPSIcon(final double tps, final int num) {
		Builder resultat = null;
		if(tps >= 20) {
			resultat = Text.builder("▇").color(TextColors.GREEN);
		} else if(tps >= 18) {
			resultat = Text.builder("▆").color(TextColors.GREEN);
		} else if(tps >= 14) {
			resultat = Text.builder("▅").color(TextColors.YELLOW);
		} else if(tps >= 10) {
			resultat = Text.builder("▃").color(TextColors.YELLOW);
		} else if(tps >= 5) {
			resultat = Text.builder("▂").color(TextColors.RED);
		} else {
			resultat = Text.builder("▁").color(TextColors.RED);
		}
		return resultat.style(TextStyles.BOLD)
				.onHover(TextActions.showText(ETextBuilder.toBuilder(EEMessages.LAG_HISTORY_TPS_HOVER.get()
						.replaceAll("<num>", String.valueOf(num)))
							.replace("<tps>", Text.builder(String.valueOf(tps)).color(getColorTPS(tps)).build())
							.build()))
				.build();
	}
	
	public TextColor getColorTPS(final double tps) {
		TextColor color;
		if(tps >= 18) {
			color = TextColors.GREEN;
		} else if(tps >= 15) {
			color = TextColors.YELLOW;
		} else {
			color = TextColors.RED;
		}
		return color;
	}
	
	public double getTPS(){
		return UtilsDouble.round(plugin.getEServer().getTicksPerSecond(), TPS_LENGTH);
	}
}
