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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.EReloadCommand;

public class EELag extends EReloadCommand<EverEssentials> {
	
	private static final double HISTORY_LENGTH = 15;
	private static final int TPS_LENGTH = 2;
	
	private Task scheduler;
	private final List<Double> historys;
	
	public EELag(final EverEssentials plugin) {
        super(plugin, "lag", "gc", "tps");

        this.historys = new ArrayList<Double>();
        this.reload();
    }
	
	@Override
	public void reload() {
		if (this.scheduler != null) {
			this.scheduler.cancel();
			this.scheduler = null;
		}
		
		this.scheduler = this.plugin.getGame().getScheduler().createTaskBuilder().execute(() -> {
		    	this.historys.add(getTPS());
		    	if (this.historys.size() > HISTORY_LENGTH) {
		    		this.historys.remove(0);
		    	}
			}).interval(1, TimeUnit.MINUTES).submit(this.plugin);
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.LAG.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.LAG_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return Arrays.asList();
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		ManagementFactory.getRuntimeMXBean().getStartTime();
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 0) {
			resultat = this.commandLag(source);
		} else {
			source.sendMessage(help(source));
		}
		
		return resultat;
	}
	
	private boolean commandLag(final CommandSource player) {
		Double tps = this.getTPS();
		List<Text> list = new ArrayList<Text>();
		
		list.add(EEMessages.LAG_TIME.getFormat()
				.toText("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().formatDate(ManagementFactory.getRuntimeMXBean().getStartTime())));
		list.add(EEMessages.LAG_TPS.getFormat()
				.toText("<tps>", Text.builder(tps.toString()).color(getColorTPS(tps)).build()));
		list.add(EEMessages.LAG_HISTORY_TPS.getFormat()
				.toText("<tps>", getHistoryTPS()));
		list.add(EEMessages.LAG_MEMORY.getFormat()
				.toText("<usage>", String.valueOf(Runtime.getRuntime().totalMemory()/1024/1024),
						"<total>", String.valueOf(Runtime.getRuntime().maxMemory()/1024/1024)));
		
		List<Text> worlds = new ArrayList<Text>();
		for (World world : this.plugin.getEServer().getWorlds()) {
			Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
			replaces.put("<world>", EReplace.of(world.getName()));
			replaces.put("<entities>", EReplace.of(String.valueOf(world.getEntities().size())));
			replaces.put("<tiles>", EReplace.of(String.valueOf(world.getTileEntities().size())));
			replaces.put("<chunks>", EReplace.of(String.valueOf(Iterables.size(world.getLoadedChunks()))));
			
			Text text = EEMessages.LAG_WORLDS_WORLD.getFormat().toText(replaces);
			if (!text.getHoverAction().isPresent() && EEMessages.LAG_WORLDS_WORLD_HOVER.getMessage().getChat().isPresent()) {
				text = text.toBuilder()
						.onHover(TextActions.showText(EEMessages.LAG_WORLDS_WORLD_HOVER.getFormat().toText(replaces)))
						.build();
			}
			worlds.add(text);
		}
		
		list.add(EEMessages.LAG_WORLDS.getFormat().toText("<worlds>", Text.joinWith(EEMessages.LAG_WORLDS_SEPARATOR.getText(), worlds)));
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EEMessages.LAG_TITLE.getText().toBuilder()
					.onClick(TextActions.runCommand("/lag"))
					.build(), 
				list, 
				player);
		return true;
	}
	
	private Text getHistoryTPS() {
		Builder resultat = Text.builder();
		for (int cpt = 0; cpt < this.historys.size(); cpt++) {
			double tps = this.historys.get(this.historys.size() - cpt - 1);
			resultat.append(this.getHistoryTPSIcon(tps, cpt + 1));
		}
		return resultat.build();
	}
	
	private Text getHistoryTPSIcon(final double tps, final int num) {
		Builder resultat = null;
		if (tps >= 20) {
			resultat = Text.builder("▇").color(TextColors.GREEN);
		} else if (tps >= 18) {
			resultat = Text.builder("▆").color(TextColors.GREEN);
		} else if (tps >= 14) {
			resultat = Text.builder("▅").color(TextColors.YELLOW);
		} else if (tps >= 10) {
			resultat = Text.builder("▃").color(TextColors.YELLOW);
		} else if (tps >= 5) {
			resultat = Text.builder("▂").color(TextColors.RED);
		} else {
			resultat = Text.builder("▁").color(TextColors.RED);
		}
		return resultat.style(TextStyles.BOLD)
				.onHover(TextActions.showText(EEMessages.LAG_HISTORY_TPS_HOVER.getFormat().toText(
							"<num>", String.valueOf(num),
							"<tps>", Text.builder(String.valueOf(tps)).color(getColorTPS(tps)).build())))
				.build();
	}
	
	private TextColor getColorTPS(final double tps) {
		TextColor color;
		if (tps >= 18) {
			color = TextColors.GREEN;
		} else if (tps >= 15) {
			color = TextColors.YELLOW;
		} else {
			color = TextColors.RED;
		}
		return color;
	}
	
	private double getTPS(){
		return UtilsDouble.round(plugin.getEServer().getTicksPerSecond(), TPS_LENGTH);
	}
}
