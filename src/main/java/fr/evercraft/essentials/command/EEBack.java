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

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEBack extends ECommand<EverEssentials> {
	
	public EEBack(final EverEssentials plugin) {
        super(plugin, "back", "return");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.BACK.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.BACK_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/back").onClick(TextActions.suggestCommand("/back"))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Nom du home inconnu
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandBack((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandBack(final EPlayer player){
		Optional<Transform<World>> back = player.getBack();
		// Le joueur a une position de retour
		if(back.isPresent()){
			if(this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, back.get().getExtent())){
				player.setBack(player.getTransform());
				// Le joueur a bien été téléporter
				player.setTransform(back.get());
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
						.append(EEMessages.BACK_TELEPORT.get())
						.replace("<back>", getButtonLocation(back.get().getLocation()))
						.build());
				return true;
			} else {
				player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.NO_PERMISSION_WORLD_OTHERS.get()));
			}
		// Le joueur n'a pas de position de retour
		} else {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.BACK_INCONNU.getText()));
		}
		return false;
	}
	
	public Text getButtonLocation(final Location<World> location){
		return EEMessages.BACK_NAME.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.BACK_NAME_HOVER.get()
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getX()))
							.replaceAll("<y>", String.valueOf(location.getY()))
							.replaceAll("<z>", String.valueOf(location.getZ())))))
					.build();
	}
}
