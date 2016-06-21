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
package fr.evercraft.essentials.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.command.ECommand;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EETop extends ECommand<EverEssentials> {
	
	public EETop(final EverEssentials plugin) {
        super(plugin, "top");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TOP.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.TOP_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/top").onClick(TextActions.suggestCommand("/top"))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si connait que la location ou aussi peut être le monde
		if(args.size() == 0) {
			// Si la source est bien un joueur
			if(source instanceof EPlayer) {
				resultat = commandTop((EPlayer) source);
			// Si la source est une console ou un commande block
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandTop(final EPlayer player) {
		if(teleport(player)) {
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.TOP_TELEPORT.get())
					.replace("<position>", getButtonPosition(player.getLocation()))
					.build());
			return true;
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TOP_TELEPORT_ERROR.get()));
		}
		return false;
	}
	public boolean teleport(EPlayer player) {
		Optional<Transform<World>> transform = this.plugin.getEverAPI().getManagerUtils().getLocation().getMaxBlock(
														player.getTransform(), 
														!(player.isGod() || player.getGameMode().equals(GameModes.CREATIVE)));
		if(transform.isPresent()) {
			player.setBack();
			player.setTransform(transform.get());
			return true;
		}
		return false;
	}
	
	public Text getButtonPosition(final Location<World> location){
		return EEMessages.TOP_POSITION.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.TOP_POSITION_HOVER.get()
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getBlockZ())))))
					.build();
	}
}