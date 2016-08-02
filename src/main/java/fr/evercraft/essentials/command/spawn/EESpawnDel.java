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
package fr.evercraft.essentials.command.spawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EESpawnDel extends ECommand<EverEssentials> {
	
	public EESpawnDel(final EverEssentials plugin) {
        super(plugin, "delspawn");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.DELSPAWN.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.DELSPAWN_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_GROUP.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1 && source instanceof Player){
			suggests.addAll(this.plugin.getManagerServices().getSpawn().getAll().keySet());
		} else if(args.size() == 2){
			suggests.add("confirmation");
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException, ServerDisableException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 1) {
			commandDeleteSpawn((EPlayer) source, args.get(0));
		} else if(args.size() == 2 && args.get(1).equalsIgnoreCase("confirmation")) {
			commandDeleteSpawnConfirmation((EPlayer) source, args.get(0));
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(getHelp(source).get());
		}
		return resultat;
	}
	
	public boolean commandDeleteSpawn(final EPlayer player, final String spawn_name) {
		Optional<Transform<World>> spawn = this.plugin.getManagerServices().getSpawn().get(spawn_name);
		// Le serveur a un spawn qui porte ce nom
		if(spawn.isPresent()) {
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.DELSPAWN_CONFIRMATION.get())
					.replace("<spawn>", getButtonSpawn(spawn_name, spawn.get()))
					.replace("<confirmation>", getButtonConfirmation(spawn_name))
					.build());
		// Le serveur n'a pas de spawn qui porte ce nom
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.DELSPAWN_INCONNU.get().replaceAll("<name>", spawn_name));
		}
		return false;
	}
	
	public boolean commandDeleteSpawnConfirmation(final EPlayer player, final String spawn_name) throws ServerDisableException {
		Optional<Transform<World>> spawn = this.plugin.getManagerServices().getSpawn().get(spawn_name);
		// Le serveur a un spawn qui porte ce nom
		if(spawn.isPresent()) {
			// Si le spawn a bien été supprimer
			if(this.plugin.getManagerServices().getSpawn().remove(spawn_name)) {
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.DELSPAWN_DELETE.get())
						.replace("<spawn>", getButtonSpawn(spawn_name, spawn.get()))
						.build());
				return true;
			// Le spawn n'a pas été supprimer
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EAMessages.COMMAND_ERROR.get());
			}
		// Le serveur n'a pas de spawn qui porte ce nom
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.DELSPAWN_INCONNU.get().replaceAll("<name>", spawn_name));
		}
		return false;
	}
	
	public Text getButtonSpawn(final String name, final Transform<World> location){
		return EChat.of(EEMessages.DELSPAWN_NAME.get().replaceAll("<name>", name)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.DELSPAWN_NAME_HOVER.get()
							.replaceAll("<name>", name)
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getLocation().getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getLocation().getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getLocation().getBlockZ())))))
					.build();
	}
	
	public Text getButtonConfirmation(final String name){
		return EEMessages.DELSPAWN_CONFIRMATION_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.DELSPAWN_CONFIRMATION_VALID_HOVER.get()
							.replaceAll("<name>", name))))
					.onClick(TextActions.runCommand("/delspawn \"" + name + "\" confirmation"))
					.build();
	}
}
