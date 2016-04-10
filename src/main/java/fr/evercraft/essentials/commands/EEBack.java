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

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEBack extends ECommand<EverEssentials> {
	
	public EEBack(final EverEssentials plugin) {
        super(plugin, "back", "return");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("BACK"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("BACK_DESCRIPTION");
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
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
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
			player.setBack(player.getTransform());
			// Le joueur a bien été téléporter
			player.setTransform(back.get());
			player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
					.append(this.plugin.getMessages().getMessage("BACK_TELEPORT"))
					.replace("<back>", getButtonLocation(back.get().getLocation()))
					.build());
			return true;
		// Le joueur n'a pas de position de retour
		} else {
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("BACK_INCONNU"));
		}
		return false;
	}
	
	public Text getButtonLocation(final Location<World> location){
		return EChat.of(this.plugin.getMessages().getMessage("BACK_NAME")).toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("BACK_NAME_HOVER")
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getX()))
							.replaceAll("<y>", String.valueOf(location.getY()))
							.replaceAll("<z>", String.valueOf(location.getZ())))))
					.build();
	}
}
