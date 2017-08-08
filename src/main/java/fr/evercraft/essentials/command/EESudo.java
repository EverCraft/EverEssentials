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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EESudo extends ECommand<EverEssentials> {
	
	public EESudo(final EverEssentials plugin) {
        super(plugin ,"sudo");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SUDO.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.SUDO_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.SUDO_CONSOLE.get())){
			return Text.builder("/" + this.getName() + " {" + EAMessages.ARGS_PLAYER.getString() + "|console} {" + EAMessages.ARGS_COMMAND.getString() + "}")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
		}
		return Text.builder("/" + this.getName() + " {" + EAMessages.ARGS_PLAYER.getString() + "} {" + EAMessages.ARGS_COMMAND.getString() + "}")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			List<String> suggests = new ArrayList<String>();
			suggests.add("console");
			for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				suggests.add(player.getName());
			}
			return suggests;
		}
		return Arrays.asList();
	}
	
	@Override
	protected List<String> getArg(final String arg) {
		List<String> args = super.getArg(arg);
		// Le message est transformer en un seul argument
		if (args.size() > 2) {
			List<String> args_send = new ArrayList<String>();
			args_send.add(args.get(0));
			args_send.add(Pattern.compile("^[ \"]*" + args.get(0) + "[ \"][ ]*").matcher(arg).replaceAll(""));
			return args_send;
		}
		return args;
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si on ne connait pas le joueur
		if (args.size() == 2) {
			
			if (!args.get(0).equalsIgnoreCase("console")) {
				Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (player.isPresent()){
					return this.commandSudo(source, player.get(), args.get(1));
				// Le joueur est introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{player}", args.get(0))
						.sendTo(source);
				} 
			} else {
				// Si il a la permission
				if (source.hasPermission(EEPermissions.SUDO_CONSOLE.get())){
					return this.commandSudoConsole(source, args.get(1));
				// Il n'a pas la permission
				} else {
					EAMessages.NO_PERMISSION.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandSudo(final CommandSource staff, final EPlayer player, final String command) {
		// Le joueur a la permission bypass
		if (player.hasPermission(EEPermissions.SUDO_BYPASS.get())) {
			EEMessages.SUDO_BYPASS.sender()
				.replace("{player}", player.getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
			
		this.plugin.getGame().getCommandManager().process(player.get(), command);
		EEMessages.SUDO_PLAYER.sender()
			.replace("{player}", player.getName())
			.replace("{command}", this.getButtonCommand(command))
			.sendTo(staff);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandSudoConsole(final CommandSource staff, final String command) {			
		this.plugin.getGame().getCommandManager().process(this.plugin.getGame().getServer().getConsole(), command);
		EEMessages.SUDO_CONSOLE.sender()
			.replace("{command}", this.getButtonCommand(command))
			.sendTo(staff);
		return CompletableFuture.completedFuture(true);
	}
	
	private Text getButtonCommand(final String command){
		return EEMessages.SUDO_COMMAND.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.SUDO_COMMAND_HOVER.getFormat()
							.toText("{command}", "/" + command)))
					.build();
	}
}
