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
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEHeal extends ECommand<EverEssentials> {
	
	public EEHeal(final EverEssentials plugin) {
        super(plugin, "heal");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.HEAL.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.HEAL_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.HEAL_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.getString() + "|*]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
		}
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source.hasPermission(EEPermissions.HEAL_OTHERS.get())){
			return this.getAllPlayers(source, true);
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandHeal((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// On connais le joueur
		} else if (args.size() == 1) {
			
			// Si il a la permission
			if (source.hasPermission(EEPermissions.HEAL_OTHERS.get())){
				// Pour tous les joueurs
				if (args.get(0).equals("*")) {
					return this.commandHealAll(source);
				// Pour un joueur
				} else {
					Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.get(0));
					// Le joueur existe
					if (player.isPresent()){
						return this.commandHealOthers(source, player.get());
					// Le joueur est introuvable
					} else {
						EAMessages.PLAYER_NOT_FOUND.sender()
							.prefix(EEMessages.PREFIX)
							.replace("{player}", args.get(0))
							.sendTo(source);
					}
				}
			// Il n'a pas la permission
			} else {
				EAMessages.NO_PERMISSION.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandHeal(final EPlayer player) {
		player.heal();
		EEMessages.HEAL_PLAYER.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandHealOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont identique
		if (player.equals(staff)) {
			return this.commandHeal(player);
		}
			
		// Le joueur est mort
		if (player.isDead()) {
			EEMessages.HEAL_OTHERS_DEAD_STAFF.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		player.heal();
		
		EEMessages.HEAL_OTHERS_STAFF.sender()
			.replace("{player}", player.getName())
			.sendTo(staff);
		EEMessages.HEAL_OTHERS_PLAYER.sender()
			.replace("{staff}", staff.getName())
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandHealAll(final CommandSource staff) {
		// Pour tous les joueurs connecté
		this.plugin.getEServer().getOnlineEPlayers().forEach(player -> {
			// Si le joueur n'est pas mort
			if (!player.isDead()) {
				player.heal();
				
				// La source et le joueur sont différent
				if (!staff.equals(player)) {
					EEMessages.HEAL_OTHERS_PLAYER.sender()
						.replace("{staff}", staff.getName())
						.sendTo(player);
				}
			}
		});
		
		EEMessages.HEAL_ALL_STAFF.sendTo(staff);
		return CompletableFuture.completedFuture(true);
	}
}
