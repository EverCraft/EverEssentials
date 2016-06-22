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
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEWarpSet extends ECommand<EverEssentials> {
	
	public EEWarpSet(final EverEssentials plugin) {
        super(plugin, "setwarp", "setwarps");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SETWARP.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.SETWARP_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/setwarp <name>").onClick(TextActions.suggestCommand("/setwarp "))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException, ServerDisableException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if(args.size() == 1) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandSetWarp((EPlayer) source, args.get(0)); 
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(getHelp(source).get());
		}
		return resultat;
	}
	
	public boolean commandSetWarp(final EPlayer player, final String warp_name) throws ServerDisableException {
		String name = EChat.fixLength(warp_name, this.plugin.getEverAPI().getConfigs().get("maxCaractere").getInt(16));
		Optional<Transform<World>> warp = this.plugin.getManagerServices().getWarp().getWarp(name);
		if(warp.isPresent()) {
			if(this.plugin.getManagerServices().getWarp().removeWarp(name) && this.plugin.getManagerServices().getWarp().addWarp(name, player.getTransform())) {
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.SETWARP_REPLACE.get())
						.replace("<warp>", getButtonWarp(name, player.getLocation()))
						.build());
				return true;
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EAMessages.COMMAND_ERROR.get());
			}
		} else {
			if(this.plugin.getManagerServices().getWarp().addWarp(name, player.getTransform())) {
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.SETWARP_NEW.get())
						.replace("<warp>", getButtonWarp(name, player.getLocation()))
						.build());
				return true;
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EAMessages.COMMAND_ERROR.get());
			}
		}
		return false;
	}

	public Text getButtonWarp(final String name, final Location<World> location){
		return EChat.of(EEMessages.SETWARP_NAME.get().replaceAll("<name>", name)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.SETWARP_NAME_HOVER.get()
							.replaceAll("<warp>", name)
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getBlockZ())))))
					.build();
	}
}
