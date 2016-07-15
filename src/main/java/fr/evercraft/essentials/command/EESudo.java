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
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
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
import fr.evercraft.everapi.text.ETextBuilder;

public class EESudo extends ECommand<EverEssentials> {
	
	public EESudo(final EverEssentials plugin) {
        super(plugin ,"sudo");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SUDO.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.SUDO_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(EEPermissions.SUDO_CONSOLE.get())){
			return Text.builder("/sudo <joueur|console> <commande>").onClick(TextActions.suggestCommand("/sudo "))
					.color(TextColors.RED).build();
		}
		return Text.builder("/sudo <joueur> <commande>").onClick(TextActions.suggestCommand("/sudo "))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1) {
			suggests.add("console");
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				suggests.add(player.getName());
			}
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() > 1) {
			if(!args.get(0).equalsIgnoreCase("console")) {
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandSudo(source, optPlayer.get(), getCommand(args));
				// Le joueur est introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				} 
			} else {
				// Si il a la permission
				if(source.hasPermission(EEPermissions.SUDO_CONSOLE.get())){
					resultat = commandSudoConsole(source, getCommand(args));
				// Il n'a pas la permission
				} else {
					source.sendMessage(EAMessages.NO_PERMISSION.getText());
				}
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandSudo(final CommandSource staff, final EPlayer player, final String command) {
		// Si le joueur n'a pas la permission bypass
		if(!player.hasPermission(EEPermissions.SUDO_BYPASS.get())) {
			if(player.getCommandSource().isPresent()) {
				this.plugin.getGame().getCommandManager().process(player.getCommandSource().get(), command);
				staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.SUDO_PLAYER.get()
								.replaceAll("<player>", player.getName()))
						.replace("<command>", getButtonCommand(command))
						.build());
				return true;
			} else {
				staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.COMMAND_ERROR.get()));
			}
		// Le joueur a la permission bypass
		} else {
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.SUDO_BYPASS.get()
					.replaceAll("<player>", player.getName())));
		}
		return true;
	}
	
	public boolean commandSudoConsole(final CommandSource staff, final String command) {
		if(this.plugin.getGame().getServer().getConsole().getCommandSource().isPresent()) {
			this.plugin.getGame().getCommandManager().process(this.plugin.getGame().getServer().getConsole().getCommandSource().get(), command);
			staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.SUDO_CONSOLE.get())
					.replace("<command>", getButtonCommand(command))
					.build());
		} else {
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.COMMAND_ERROR.get()));
		}
		return true;
	}
	
	public String getCommand(final List<String> args){
		args.remove(0);
		String command = args.get(0).replace("/", "");
		args.remove(0);
		for(String arg : args) {
			command += " " + arg;
		}
		return command;
	}
	
	public Text getButtonCommand(final String command){
		return EChat.of(EEMessages.SUDO_COMMAND.get()).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.SUDO_COMMAND_HOVER.get()
							.replaceAll("<command>", "/" + command))))
					.build();
	}
}