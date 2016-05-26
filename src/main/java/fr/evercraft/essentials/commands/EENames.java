/**
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.mojang.MojangService;
import fr.evercraft.everapi.services.mojang.namehistory.NameHistory;

public class EENames extends ECommand<EverEssentials> {
	
	public EENames(final EverEssentials plugin) {
        super(plugin, "names");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("NAMES"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("NAMES_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(this.plugin.getPermissions().get("NAMES_OTHERS"))){
			return Text.builder("/names [joueur]").onClick(TextActions.suggestCommand("/names "))
					.color(TextColors.RED).build();
		}
		return Text.builder("/names").onClick(TextActions.suggestCommand("/names"))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if(args.size() == 1 && source.hasPermission(this.plugin.getPermissions().get("NAMES_OTHERS"))){
			return null;
		}
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				this.plugin.getGame().getScheduler().createTaskBuilder()
						.async()
						.execute(() -> commandNames((EPlayer) source))
						.name("Command : Names").submit(this.plugin);
				resultat = true;
			// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		// On connais le joueur
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(this.plugin.getPermissions().get("NAMES_OTHERS"))){
				this.plugin.getGame().getScheduler().createTaskBuilder()
							.async()
							.execute(() -> commandNames(source, args.get(0)))
							.name("Command : Names").submit(this.plugin);
				resultat = true;
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

	private void commandNames(final EPlayer player) {
		Optional<MojangService> service = this.plugin.getEverAPI().getManagerService().getMojangService();
		if(service.isPresent()) {
			try {
				List<Text> lists = new ArrayList<Text>();
		
				for(NameHistory name : service.get().getNameHistory().get(player.getUniqueId())) {
					if(!name.getDate().isPresent()) {
						lists.add(EChat.of(this.plugin.getMessages().getMessage("NAMES_PLAYER_LINE_ORIGINAL")
								.replaceAll("<name>", name.getName())));
					} else {
						lists.add(EChat.of(this.plugin.getMessages().getMessage("NAMES_PLAYER_LINE_OTHERS")
								.replaceAll("<name>", name.getName())
								.replaceAll("<date>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(name.getDate().get()))
								.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(name.getDate().get()))
								.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(name.getDate().get()))));
					}
				}
				if(lists.size() <= 1) {
					lists.clear();
					lists.add(EChat.of(this.plugin.getMessages().getMessage("NAMES_PLAYER_EMPTY")
							.replaceAll("<player>", player.getName())));
				}
				
				this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
						EChat.of(this.plugin.getMessages().getMessage("NAMES_PLAYER_TITLE")
								.replaceAll("<player>", player.getName())).toBuilder()
							.onClick(TextActions.runCommand("/names ")).build(), 
						lists, player);
			} catch (ExecutionException e) {
				player.sendMessage(Text.of("erreur : " + e.getMessage()));
			}
		} else {
			player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getCommandError()));
		}
	}
	
	private void commandNames(final CommandSource player, String name) {
		try {
			CompletableFuture<GameProfile> future = null;
			if(name.length() == 36) {
				future = this.plugin.getEServer().getGameProfileManager().get(UUID.fromString(name));
			} else {
				future = this.plugin.getEServer().getGameProfileManager().get(name);
			}
			future.exceptionally(e -> null).thenApplyAsync(profile -> {
				if (profile != null && profile.isFilled() && profile.getName().isPresent()) {
					if(player instanceof EPlayer && ((EPlayer) player).getProfile().equals(profile)) {
						this.commandNames((EPlayer) player);
					} else {
						this.commandNames(player, profile);
					}
				} else {
					player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
				}
				return profile;
			}, this.plugin.getGame().getScheduler().createAsyncExecutor(this.plugin));
		} catch (IllegalArgumentException e) {
			player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
		}
	}
	
	private void commandNames(CommandSource staff, GameProfile gameprofile) {
		Optional<MojangService> service = this.plugin.getEverAPI().getManagerService().getMojangService();
		if(service.isPresent()) {
			try {
				List<Text> lists = new ArrayList<Text>();
		
				for(NameHistory name : service.get().getNameHistory().get(gameprofile.getUniqueId())) {
					if(!name.getDate().isPresent()) {
						lists.add(EChat.of(this.plugin.getMessages().getMessage("NAMES_OTHERS_LINE_ORIGINAL")
								.replaceAll("<name>", name.getName())));
					} else {
						lists.add(EChat.of(this.plugin.getMessages().getMessage("NAMES_OTHERS_LINE_OTHERS")
								.replaceAll("<name>", name.getName())
								.replaceAll("<date>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(name.getDate().get()))
								.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(name.getDate().get()))
								.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(name.getDate().get()))));
					}
				}
				
				if(lists.size() <= 1) {
					lists.clear();
					lists.add(EChat.of(this.plugin.getMessages().getMessage("NAMES_OTHERS_EMPTY")
							.replaceAll("<player>", gameprofile.getName().get())));
				}
				
				this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
						EChat.of(this.plugin.getMessages().getMessage("NAMES_OTHERS_TITLE")
								.replaceAll("<player>", gameprofile.getName().get())).toBuilder()
							.onClick(TextActions.runCommand("/names " + gameprofile.getUniqueId().toString())).build(), 
						lists, staff);
			} catch (ExecutionException e) {
				staff.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getCommandError()));
			}
		}
	}
}
