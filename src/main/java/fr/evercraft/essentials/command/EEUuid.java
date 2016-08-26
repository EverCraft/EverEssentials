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
import java.util.UUID;

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
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.EServer;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEUuid extends ECommand<EverEssentials> {
	
	public EEUuid(final EverEssentials plugin) {
        super(plugin, "uuid");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.UUID.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.UUID_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.UUID_OTHERS.get())){
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
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1 && source.hasPermission(EEPermissions.UUID_OTHERS.get())){
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
				resultat = this.commandUUIDPlayerUUID((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
		// On connais le joueur
		} else if (args.size() == 1) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.UUID_OTHERS.get())) {
				Optional<GameProfile> profile = this.plugin.getEServer().getGameProfile(args.get(0));
				// Le joueur existe
				if (profile.isPresent()) {
					if (args.get(0).length() == EServer.UUID_LENGTH) {
						resultat = this.commandUUIDOthersName(source, profile.get());
					} else {
						resultat = this.commandUUIDOthersUUID(source, profile.get());
					}
				// Le joueur est introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	public boolean commandUUIDPlayerName(final EPlayer player) {
		player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
				.append(EEMessages.UUID_PLAYER_NAME.get())
				.replace("<uuid>", this.getButtonUUID(player.getUniqueId()))
				.replace("<name>", this.getButtonName(player.getName()))
				.build());
		return true;
	}
	
	public boolean commandUUIDPlayerUUID(final EPlayer player) {
		player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
				.append(EEMessages.UUID_PLAYER_UUID.get())
				.replace("<uuid>", this.getButtonUUID(player.getUniqueId()))
				.replace("<name>", this.getButtonName(player.getName()))
				.build());
		return true;
	}
	
	public boolean commandUUIDOthersName(final CommandSource staff, final GameProfile profile) throws CommandException {
		// La source et le joueur sont identique
		if (staff instanceof EPlayer && profile.getUniqueId().equals(((EPlayer) staff).getUniqueId())) {
			return this.commandUUIDPlayerName((EPlayer) staff);
		// La source et le joueur sont différent
		} else {
			if(profile.getName().isPresent()) {
				staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.UUID_OTHERS_PLAYER_NAME.get()
								.replaceAll("<player>", profile.getName().get()))
						.replace("<uuid>", this.getButtonUUID(profile.getUniqueId()))
						.replace("<name>", this.getButtonName(profile.getName().get()))
						.build());				
				return true;
			} else {
				staff.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
			}
		}
		return false;
	}
	
	public boolean commandUUIDOthersUUID(final CommandSource staff, final GameProfile profile) throws CommandException {
		// La source et le joueur sont identique
		if (staff instanceof EPlayer && profile.getUniqueId().equals(((EPlayer) staff).getUniqueId())) {
			return this.commandUUIDPlayerUUID((EPlayer) staff);
		// La source et le joueur sont différent
		} else {
			staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.UUID_OTHERS_PLAYER_UUID.get()
							.replaceAll("<player>", profile.getName().orElse(profile.getUniqueId().toString())))
					.replace("<uuid>", this.getButtonUUID(profile.getUniqueId()))
					.replace("<name>", this.getButtonName(profile.getName().get()))
					.build());				
			return true;
		}
	}
	
	public Text getButtonUUID(final UUID uuid){
		return EChat.of(EEMessages.UUID_PLAYER_UUID.get().replace("<uuid>", uuid.toString())).toBuilder()
				.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
					.onClick(TextActions.suggestCommand(uuid.toString()))
					.onShiftClick(TextActions.insertText(uuid.toString()))
					.build();
	}
	
	public Text getButtonName(final String name){
		return EChat.of(EEMessages.UUID_PLAYER_NAME.get().replace("<name>", name)).toBuilder()
				.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
					.onClick(TextActions.suggestCommand(name))
					.onShiftClick(TextActions.insertText(name))
					.build();
	}
}

