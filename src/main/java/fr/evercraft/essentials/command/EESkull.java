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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsItemStack;

public class EESkull extends ECommand<EverEssentials> {

	public EESkull(final EverEssentials plugin) {
		super(plugin, "skull");
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SKULL.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.SKULL_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.SKULL_OTHERS.get())) {
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.getString() + "]")
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
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1 && source.hasPermission(EEPermissions.SKULL_OTHERS.get())) {
			suggests.addAll(this.getAllUsers());
		}
		return suggests;
	}

	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandSkull((EPlayer) source);
				// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			// On connais le joueur
		} else if (args.size() == 1) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.SKULL_OTHERS.get())) {
				resultat = this.commandSkullOthers((EPlayer) source, args.get(0));
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
		
		return resultat;
	}

	private boolean commandSkull(final EPlayer player) {
		player.giveItemAndDrop(UtilsItemStack.createPlayerHead(player.getProfile()));
		EEMessages.SKULL_MY_HEAD.sendTo(player);
		return true;
	}

	private boolean commandSkullOthers(final EPlayer player, final String name) {
		CompletableFuture<GameProfile> future = this.plugin.getEServer().getGameProfileFuture(name);
		future.exceptionally(e -> null).thenApplyAsync((profile) -> {
			if (player.isOnline()) {
				if (profile!= null && profile.getName().isPresent()) {
					try {
						GameProfile profile_skin = this.plugin.getEServer().getGameProfileManager().fill(profile, true, false).get();
						player.giveItemAndDrop(UtilsItemStack.createPlayerHead(profile_skin));
						EEMessages.SKULL_OTHERS.sender()
							.replace("<player>", profile_skin.getName().get())
							.sendTo(player);
					} catch (Exception e) {
						EAMessages.PLAYER_NOT_FOUND.sender()
							.prefix(EEMessages.PREFIX)
							.sendTo(player);
					}
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(player);
				}
			}
			return profile;
		}, this.plugin.getGame().getScheduler().createAsyncExecutor(this.plugin));
		return true;
	}
}