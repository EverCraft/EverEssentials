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
package fr.evercraft.everessentials.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.collect.Collections2;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEHelp extends ECommand<EverEssentials> {
		
	public EEHelp(final EverEssentials plugin) {
        super(plugin, "help", "?");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission("minecraft.command.help");
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.HELP_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_COMMAND.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			TreeSet<String> commands = new TreeSet<String>();
			for (CommandMapping command : Collections2.filter(this.plugin.getGame().getCommandManager().getAll().values(), input -> input.getCallable().testPermission(source))) {
				commands.add(command.getPrimaryAlias());
			}
			return commands;
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			
			return this.commandHelp(source);
			
		// On connais le joueur
		} else if (args.size() == 1) {
			
			return this.commandHelp(source, args.get(0));
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandHelp(final CommandSource source) {
		Text title = EEMessages.HELP_TITLE.getText().toBuilder()
						.onClick(TextActions.runCommand("/help"))
						.color(TextColors.RED)
						.build();
		
		TreeSet<CommandMapping> commands = new TreeSet<CommandMapping>((o1, o2) -> o1.getPrimaryAlias().compareTo(o2.getPrimaryAlias()));
        commands.addAll(this.plugin.getGame().getCommandManager().getAll().values());
		
		this.plugin.getEverAPI().getManagerService().getEPagination().helpCommands(commands, title, source);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandHelp(final CommandSource source, final String alias) {
		Optional<? extends CommandMapping> command = this.plugin.getGame().getCommandManager().get(alias);
		if (command.isPresent()) {
			if (command.get().getCallable().testPermission(source)) {
				source.sendMessage(command.get().getCallable().getHelp(source).orElse(Text.of("/" + command.get().getPrimaryAlias())));
				return CompletableFuture.completedFuture(true);
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		} else {
			Text title = EEMessages.HELP_SEARCH_TITLE.getFormat().toText("{command}", alias).toBuilder()
					.onClick(TextActions.runCommand("/help " + alias))
					.color(TextColors.RED)
					.build();
			
			TreeSet<CommandMapping> commands = new TreeSet<CommandMapping>((o1, o2) -> o1.getPrimaryAlias().compareTo(o2.getPrimaryAlias()));
			for (CommandMapping mapping : this.plugin.getGame().getCommandManager().getAll().values()) {
				if (mapping.getPrimaryAlias().contains(alias)) {
					commands.add(mapping);
				}
			}
	
			this.plugin.getEverAPI().getManagerService().getEPagination().helpCommands(commands, title, source);
		}
		return CompletableFuture.completedFuture(false);
	}
}
