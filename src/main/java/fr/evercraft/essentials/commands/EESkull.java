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
package fr.evercraft.essentials.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
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

public class EESkull extends ECommand<EverEssentials> {

	public EESkull(final EverEssentials plugin) {
		super(plugin, "skull");
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SKULL.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.SKULL_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/skull").onClick(TextActions.suggestCommand("/skull")).color(TextColors.RED).build();
	}

	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source.hasPermission(EEPermissions.PING_OTHERS.get())) {
			return null;
		}
		return new ArrayList<String>();
	}

	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandSkull((EPlayer) source);
				// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
			// On connais le joueur
		} else if (args.size() == 1) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.SKULL_OTHERS.get())) {
				resultat = commandSkullOthers((EPlayer) source, args.get(0));
				// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
			// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}

	public boolean commandSkull(final EPlayer player) {
		player.giveItemAndDrop(createPlayerHead(player.getProfile()));
		player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SKULL_MY_HEAD.get());
		return true;
	}

	public boolean commandSkullOthers(final EPlayer player, final String pseudo) throws CommandException {
		CompletableFuture<GameProfile> future = this.plugin.getEServer().getGameProfileManager().get(pseudo);
		future.exceptionally(e -> null).thenApplyAsync((profile) -> {
			if (player.isOnline()) {
				if (profile!= null && profile.getName().isPresent()) {
					try {
						GameProfile profile_skin = this.plugin.getEServer().getGameProfileManager().fill(profile, true, false).get();
						player.giveItemAndDrop(createPlayerHead(profile_skin));
						player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SKULL_OTHERS.get().replaceAll("<player>", profile_skin.getName().get()));
					} catch (Exception e) {
						player.sendMessage(EEMessages.PREFIX.get() + EAMessages.PLAYER_NOT_FOUND.get());
					}
				} else {
					player.sendMessage(EEMessages.PREFIX.get() + EAMessages.PLAYER_NOT_FOUND.get());
				}
			}
			return profile;
		}, this.plugin.getGame().getScheduler().createAsyncExecutor(this.plugin));
		return false;
	}
	
	public ItemStack createPlayerHead(GameProfile profile) {
		ItemStack skull = ItemStack.of(ItemTypes.SKULL, 1);
		skull.offer(Keys.SKULL_TYPE, SkullTypes.PLAYER);
		skull.offer(Keys.REPRESENTED_PLAYER, profile);
		return skull;
	}
}