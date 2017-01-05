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
package fr.evercraft.essentials.command.ignore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEIgnoreList extends ESubCommand<EverEssentials> {
	public EEIgnoreList(final EverEssentials plugin, final EEIgnore command) {
        super(plugin, command, "list");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return true;
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.IGNORE_LIST_DESCRIPTION.get());
	}
	
	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.IGNORE_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.get() + "]")
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
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1 && source.hasPermission(EEPermissions.IGNORE_OTHERS.get())){
			suggests.addAll(this.getAllUsers());
		}
		return suggests;
	}
	
	@Override
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandIgnoreList((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 1) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.IGNORE_OTHERS.get())){
				Optional<EUser> user = this.plugin.getEServer().getEUser(args.get(0));
				// Le joueur existe
				if (user.isPresent()){
					resultat = this.commandIgnoreList(source, user.get());
				// Le joueur est introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}

	private boolean commandIgnoreList(final EPlayer player) {
		List<Text> lists = new ArrayList<Text>();
		
		for (UUID uuid : player.getIgnores()) {
			Optional<EUser> user = this.plugin.getEServer().getEUser(uuid);
			if (user.isPresent()) {
				lists.add(ETextBuilder.toBuilder(EEMessages.IGNORE_LIST_LINE_DELETE.get()
						.replaceAll("<player>", user.get().getName()))
					.replace("<delete>", getButtonDelete(user.get().getName(),  user.get().getUniqueId()))
					.build());
			}
		}
			
		if (lists.isEmpty()) {
			lists.add(EEMessages.IGNORE_LIST_EMPTY.getText());
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EChat.of(EEMessages.IGNORE_LIST_PLAYER_TITLE.get().replaceAll("<player>", player.getName())).toBuilder()
					.onClick(TextActions.runCommand("/" + this.getName())).build(), 
				lists, player);
		return true;
	}
	
	private boolean commandIgnoreList(final CommandSource staff, final EUser player) {
		if (staff instanceof EPlayer && player.getIdentifier().equals(staff.getIdentifier())) {
			return this.commandIgnoreList((EPlayer) staff);
		} else {
			List<Text> lists = new ArrayList<Text>();
			
			for (UUID uuid : player.getIgnores()) {
				Optional<EUser> user = this.plugin.getEServer().getEUser(uuid);
				if (user.isPresent()) {
					lists.add(EChat.of(EEMessages.IGNORE_LIST_LINE.get()
							.replaceAll("<player>", user.get().getName())));
				}
			}
				
			if (lists.isEmpty()) {
				lists.add(EEMessages.IGNORE_LIST_EMPTY.getText());
			}
			
			this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
					EChat.of(EEMessages.IGNORE_LIST_OTHERS_TITLE.get().replaceAll("<player>", player.getName())).toBuilder()
						.onClick(TextActions.runCommand("/" + this.getName() + " \"" + player.getUniqueId() + "\"")).build(), 
					lists, staff);
			return true;
		}
	}
	
	private Text getButtonDelete(final String name, final UUID uuid){
		return EEMessages.IGNORE_LIST_REMOVE.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.IGNORE_LIST_REMOVE_HOVER.get()
							.replaceAll("<player>", name))))
					.onClick(TextActions.runCommand("/" + this.getParentName() + " remove " + uuid.toString()))
					.build();
	}
}
