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
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EESay extends ECommand<EverEssentials> {
	
	public EESay(final EverEssentials plugin) {
        super(plugin ,"say");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SAY.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.SAY_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_MESSAGE.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return Arrays.asList();
	}
	
	@Override
	protected List<String> getArg(final String arg){
		if (!arg.isEmpty()) {
			return Arrays.asList(arg);
		}
		return Arrays.asList();
	}

	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandSayPlayer((EPlayer) source, args.get(0));
			} else if(source instanceof CommandBlockSource) {
				return this.commandSayCommandBlock(source, args.get(0));
			// La source n'est pas un joueur
			} else {
				return this.commandSayConsole(source, args.get(0));
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandSayPlayer(final EPlayer player, String message) {
		this.plugin.getEServer().getBroadcastChannel().send(EEMessages.SAY_PLAYER.getFormat().toText(
				"{player}", player.getName(),
				"{message}", message));
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandSayCommandBlock(final CommandSource player, String message) {
		this.plugin.getEServer().getBroadcastChannel().send(EEMessages.SAY_COMMANDBLOCK.getFormat().toText(
				"{message}", message));
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandSayConsole(final CommandSource player, String message) {
		this.plugin.getEServer().getBroadcastChannel().send(EEMessages.SAY_CONSOLE.getFormat().toText(
				"{message}", message));
		return CompletableFuture.completedFuture(true);
	}
}
