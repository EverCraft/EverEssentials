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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

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
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_MESSAGE.get() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	@Override
	protected List<String> getArg(final String arg){
		if (!arg.isEmpty()) {
			return Arrays.asList(arg);
		}
		return Arrays.asList();
	}

	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandSayPlayer((EPlayer) source, args.get(0));
			} else if(source instanceof CommandBlockSource) {
				resultat = this.commandSayCommandBlock(source, args.get(0));
			// La source n'est pas un joueur
			} else {
				resultat = this.commandSayConsole(source, args.get(0));
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	private boolean commandSayPlayer(final EPlayer player, String message) {
		this.plugin.getEServer().getBroadcastChannel().send(EChat.of(EEMessages.SAY_PLAYER.get()
				.replaceAll("<player>", player.getName())
				.replaceAll("<message>", message)));
		return true;
	}
	
	private boolean commandSayCommandBlock(final CommandSource player, String message) {
		this.plugin.getEServer().getBroadcastChannel().send(EChat.of(EEMessages.SAY_COMMANDBLOCK.get()
				.replaceAll("<message>", message)));
		return true;
	}
	
	private boolean commandSayConsole(final CommandSource player, String message) {
		this.plugin.getEServer().getBroadcastChannel().send(EChat.of(EEMessages.SAY_CONSOLE.get()
				.replaceAll("<message>", message)));
		return true;
	}
}