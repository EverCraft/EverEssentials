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
package fr.evercraft.essentials.command.repair;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EERepair extends ECommand<EverEssentials> {

	public EERepair(final EverEssentials plugin) {
		super(plugin, "repair", "fix");
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.REPAIR.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.REPAIR_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " ").onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.append(Text.of("<"))
					.append(Text.builder("all").onClick(TextActions.suggestCommand("/" + this.getName() + " all")).build())
					.append(Text.of("|"))
					.append(Text.builder("hand").onClick(TextActions.suggestCommand("/" + this.getName() + " hand")).build())
					.append(Text.of("|"))
					.append(Text.builder("hotbar").onClick(TextActions.suggestCommand("/" + this.getName() + " hotbar")).build())
					.append(Text.of(">"))
					.color(TextColors.RED)
					.build();
	}

	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return Arrays.asList("all", "hand", "hotbar");
		}
		return Arrays.asList();
	}

	@Override
	public CompletableFuture<Boolean> execute(CommandSource source, final List<String> args) throws CommandException {
		// Erreur : Context 
		if(source instanceof EPlayer) {
			source = ((EPlayer) source).get();
		}
		
		if (args.size() == 1) {
			if (args.get(0).equalsIgnoreCase("all")) {
				return this.commandRepairAll(source);
			} else if (args.get(0).equalsIgnoreCase("hand")) {
				return this.commandRepairHand(source);
			} else if (args.get(0).equalsIgnoreCase("hotbar")) {
				return this.commandRepairHotbar(source);
			} else {
				source.sendMessage(this.help(source));
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandRepairAll(final CommandSource player) {
		this.plugin.getGame().getCommandManager().process(player, "repairall");
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandRepairHand(final CommandSource player) {
		this.plugin.getGame().getCommandManager().process(player, "repairhand");
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandRepairHotbar(final CommandSource player) {
		this.plugin.getGame().getCommandManager().process(player, "repairhotbar");
		return CompletableFuture.completedFuture(false);
	}
}
