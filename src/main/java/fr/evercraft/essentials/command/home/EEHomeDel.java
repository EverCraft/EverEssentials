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
package fr.evercraft.essentials.command.home;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.service.subject.EUserSubject;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.location.VirtualTransform;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.SubjectUserEssentials;

public class EEHomeDel extends ECommand<EverEssentials> {
	
	public EEHomeDel(final EverEssentials plugin) {
        super(plugin, "delhome", "delresidence");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.DELHOME.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.DELHOME_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " {" + EAMessages.ARGS_HOME.getString() + "}")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source instanceof Player) {
			Optional<SubjectUserEssentials> player = this.plugin.getManagerServices().getEssentials().get(((Player) source).getUniqueId());
			// Le joueur existe
			if (player.isPresent()) {
				List<String> suggests = new ArrayList<String>();
				for (String home : player.get().getHomes().keySet()){
					suggests.add(home);
				}
				return suggests;
			}
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si on ne connait pas le joueur
		if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandDeleteHome((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 2 && args.get(1).equalsIgnoreCase("confirmation")) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandDeleteHomeConfirmation((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandDeleteHome(final EPlayer player, final String home_name) {
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());
		
		Optional<EUserSubject> subject = this.plugin.getManagerServices().getEssentials().getSubject(player.getUniqueId());
		if (!subject.isPresent()) {
			EAMessages.PLAYER_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{player}", player.getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
			
		Optional<VirtualTransform> home = subject.get().getHomeLocation(name);
		// Le n'a pas de home qui porte ce nom
		if (!home.isPresent()) {
			EEMessages.DELHOME_INCONNU.sender()
				.replace("{home}", name)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.DELHOME_CONFIRMATION.sender()
			.replace("{home}", this.getButtonHome(name, home.get()))
			.replace("{confirmation}", this.getButtonConfirmation(name))
			.sendTo(player);
		return CompletableFuture.completedFuture(false);
	}
	
	
	private CompletableFuture<Boolean> commandDeleteHomeConfirmation(final EPlayer player, final String home_name) {
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());
		
		Optional<EUserSubject> subject = this.plugin.getManagerServices().getEssentials().getSubject(player.getUniqueId());
		if (!subject.isPresent()) {
			EAMessages.PLAYER_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{player}", player.getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
			
		Optional<VirtualTransform> home = subject.get().getHomeLocation(name);
		// Le joueur a bien un home qui porte ce nom
		if (!home.isPresent()) {
			EEMessages.DELHOME_INCONNU.sender()
				.replace("{home}", name)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// Le home n'a pas été supprimer
		if (!player.removeHome(name)) {
			EAMessages.COMMAND_ERROR.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.DELHOME_DELETE.sender()
			.replace("{home}", this.getButtonHome(name, home.get()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private Text getButtonHome(final String name, final VirtualTransform location){
		return EEMessages.HOME_NAME.getFormat().toText("{name}", name).toBuilder()
					.onHover(TextActions.showText(EEMessages.HOME_NAME_HOVER.getFormat().toText(
								"{home}", name,
								"{world}", location.getWorldName(),
								"{x}", String.valueOf(location.getPosition().getFloorX()),
								"{y}",String.valueOf(location.getPosition().getFloorY()),
								"{z}", String.valueOf(location.getPosition().getFloorZ()))))
					.build();
	}
	
	private Text getButtonConfirmation(final String name){
		return EEMessages.DELHOME_CONFIRMATION_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.DELHOME_CONFIRMATION_VALID_HOVER.getFormat()
							.toText("{home}", name)))
					.onClick(TextActions.runCommand("/delhome \"" + name + "\" confirmation"))
					.build();
	}
}
