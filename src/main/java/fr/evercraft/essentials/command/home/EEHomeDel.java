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
import java.util.List;
import java.util.Optional;

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
import fr.evercraft.everapi.server.location.LocationSQL;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

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
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_HOME.get() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1 && source instanceof Player){
			Optional<EPlayer> player = this.plugin.getEServer().getEPlayer((Player) source);
			// Le joueur existe
			if (player.isPresent()) {
				for (String home : player.get().getHomes().keySet()){
					suggests.add(home);
				}
			}
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandDeleteHome((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
		} else if (args.size() == 2 && args.get(1).equalsIgnoreCase("confirmation")) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandDeleteHomeConfirmation((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	private boolean commandDeleteHome(final EPlayer player, final String home_name) {
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());
		
		Optional<EUserSubject> subject = this.plugin.getManagerServices().getEssentials().getSubject(player.getUniqueId());
		if (subject.isPresent()) {
			
			Optional<LocationSQL> home = subject.get().getHomeLocation(name);
			// Le joueur a bien un home qui porte ce nom
			if (home.isPresent()) {
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.DELHOME_CONFIRMATION.get())
						.replace("<home>", this.getButtonHome(name, home.get()))
						.replace("<confirmation>", this.getButtonConfirmation(name))
						.build());
			// Le n'a pas de home qui porte ce nom
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.DELHOME_INCONNU.get().replaceAll("<home>", name));
			}
			
		} else {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
		}
		return false;
	}
	
	
	private boolean commandDeleteHomeConfirmation(final EPlayer player, final String home_name) {
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());
		
		Optional<EUserSubject> subject = this.plugin.getManagerServices().getEssentials().getSubject(player.getUniqueId());
		if (subject.isPresent()) {
			
			Optional<LocationSQL> home = subject.get().getHomeLocation(name);
			// Le joueur a bien un home qui porte ce nom
			if (home.isPresent()) {
				
				// Si le home a bien été supprimer
				if (player.removeHome(name)) {
					player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
							.append(EEMessages.DELHOME_DELETE.get())
							.replace("<home>", this.getButtonHome(name, home.get()))
							.build());
					return true;
				// Le home n'a pas été supprimer
				} else {
					player.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR.getText()));
				}
				
			// Le n'a pas de home qui porte ce nom
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.DELHOME_INCONNU.get().replaceAll("<home>", name));
			}
			
		} else {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
		}
		return false;
	}
	
	private Text getButtonHome(final String name, final LocationSQL location){
		return EChat.of(EEMessages.HOME_NAME.get().replaceAll("<name>", name)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.HOME_NAME_HOVER.get()
							.replaceAll("<home>", name)
							.replaceAll("<world>", location.getWorldName())
							.replaceAll("<x>", location.getX().toString())
							.replaceAll("<y>", location.getY().toString())
							.replaceAll("<z>", location.getZ().toString()))))
					.build();
	}
	
	private Text getButtonConfirmation(final String name){
		return EEMessages.DELHOME_CONFIRMATION_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.DELHOME_CONFIRMATION_VALID_HOVER.get()
							.replaceAll("<home>", name))))
					.onClick(TextActions.runCommand("/delhome \"" + name + "\" confirmation"))
					.build();
	}
}
