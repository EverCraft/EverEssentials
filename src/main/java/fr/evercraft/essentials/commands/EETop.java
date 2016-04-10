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
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
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

public class EETop extends ECommand<EverEssentials> {
	
	public EETop(final EverEssentials plugin) {
        super(plugin, "top");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("TOP"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("TOP_DESCRIPTION");
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
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandTop(final EPlayer player) {
		if(teleport(player)) {
			player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
					.append(this.plugin.getMessages().getMessage("TOP_TELEPORT"))
					.replace("<position>", getButtonPosition(player.getLocation()))
					.build());
			return true;
		} else {
			player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("TOP_TELEPORT_ERROR")));
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
		return EChat.of(this.plugin.getMessages().getMessage("TOP_POSITION")).toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("TOP_POSITION_HOVER")
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getBlockZ())))))
					.build();
	}
}