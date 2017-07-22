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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
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
import fr.evercraft.everapi.server.EServer;
import fr.evercraft.everapi.server.player.EPlayer;

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
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_USER.getString() + "]")
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
		if (args.size() == 1 && source.hasPermission(EEPermissions.UUID_OTHERS.get())){
			return this.getAllGameProfile();
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandUUIDPlayerUUID((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// On connais le joueur
		} else if (args.size() == 1) {
			
			// Si il a la permission
			if (source.hasPermission(EEPermissions.UUID_OTHERS.get())) {
				return this.commandUUID(source, args.get(0));
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
	
	private CompletableFuture<Boolean> commandUUIDPlayerName(final EPlayer player) {
		EEMessages.UUID_PLAYER_NAME.sender()
			.replace("<uuid>", this.getButtonUUID(player.getUniqueId()))
			.replace("<name>", this.getButtonName(player.getName()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandUUID(final CommandSource source, String name) {
		try {
			CompletableFuture<GameProfile> future = this.plugin.getEServer().getGameProfileFuture(name);
			future.exceptionally(e -> null).thenApplyAsync(profile -> {
				if (profile != null && profile.isFilled() && profile.getName().isPresent()) {
					if (name.length() == EServer.UUID_LENGTH) {
						this.commandUUIDOthersName(source, profile);
					} else {
						this.commandUUIDOthersUUID(source, profile);
					}
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.replace("<player>", name)
						.sendTo(source);
				}
				return profile;
			}, this.plugin.getGame().getScheduler().createAsyncExecutor(this.plugin));
			return CompletableFuture.completedFuture(true);
		} catch (IllegalArgumentException e) {
			EAMessages.PLAYER_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.replace("<player>", name)
				.sendTo(source);
			return CompletableFuture.completedFuture(false);
		}
	}
	
	private CompletableFuture<Boolean> commandUUIDPlayerUUID(final EPlayer player) {
		EEMessages.UUID_PLAYER_UUID.sender()
			.replace("<uuid>", this.getButtonUUID(player.getUniqueId()))
			.replace("<name>", this.getButtonName(player.getName()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandUUIDOthersName(final CommandSource staff, final GameProfile profile) {
		// La source et le joueur sont identique
		if (staff instanceof EPlayer && profile.getUniqueId().equals(((EPlayer) staff).getUniqueId())) {
			return this.commandUUIDPlayerName((EPlayer) staff);
		}
		
		// Joueur introuvable
		if(!profile.getName().isPresent()) {
			EAMessages.PLAYER_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.replace("<player>", profile.getUniqueId().toString())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.UUID_OTHERS_PLAYER_NAME.sender()
			.replace("<uuid>", this.getButtonUUID(profile.getUniqueId()))
			.replace("<name>", this.getButtonName(profile.getName().get()))
			.sendTo(staff);				
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandUUIDOthersUUID(final CommandSource staff, final GameProfile profile) {
		// La source et le joueur sont identique
		if (staff instanceof EPlayer && profile.getUniqueId().equals(((EPlayer) staff).getUniqueId())) {
			return this.commandUUIDPlayerUUID((EPlayer) staff);
		}
		
		EEMessages.UUID_OTHERS_PLAYER_UUID.sender()
			.replace("<player>", profile.getName().orElse(profile.getUniqueId().toString()))
			.replace("<uuid>", this.getButtonUUID(profile.getUniqueId()))
			.replace("<name>", this.getButtonName(profile.getName().get()))
			.sendTo(staff);				
		return CompletableFuture.completedFuture(true);
	}
	
	private Text getButtonUUID(final UUID uuid){
		return EEMessages.UUID_UUID.getFormat().toText("<uuid>", uuid.toString()).toBuilder()
				.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
					.onClick(TextActions.suggestCommand(uuid.toString()))
					.onShiftClick(TextActions.insertText(uuid.toString()))
				.build();
	}
	
	private Text getButtonName(final String name){
		return EEMessages.UUID_NAME.getFormat().toText("<name>", name.toString()).toBuilder()
				.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
					.onClick(TextActions.suggestCommand(name))
					.onShiftClick(TextActions.insertText(name))
				.build();
	}
}

