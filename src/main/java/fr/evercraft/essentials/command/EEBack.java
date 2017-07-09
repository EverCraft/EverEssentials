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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEBack extends ECommand<EverEssentials> {
	
	public EEBack(final EverEssentials plugin) {
        super(plugin, "back", "return");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.BACK.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.BACK_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
				.onClick(TextActions.suggestCommand("/" + this.getName()))
				.color(TextColors.RED)
				.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Nombre d'argument correct
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandBack((EPlayer) source);
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
	
	private CompletableFuture<Boolean> commandBack(final EPlayer player){
		final Optional<Transform<World>> back = player.getBack();
		
		// Le joueur a une position de retour
		if (!back.isPresent()) {
			EEMessages.BACK_INCONNU.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// Le joueur n'a pas la permission d'aller dans le monde
		if (!this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, back.get().getExtent())) {
			EAMessages.NO_PERMISSION_WORLD_OTHERS.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
			
		// La position n'est pas Safe
		if (!(this.plugin.getEverAPI().getManagerUtils().getLocation().isPositionSafe(back.get()) || player.isGod() || player.isCreative())) {
			EEMessages.BACK_ERROR_LOCATION.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// Delais de téléportation
		long delay = this.plugin.getConfigs().getTeleportDelay(player);
		if (delay > 0) {
			EEMessages.BACK_DELAY.sender()
				.replace("<delay>", () -> this.plugin.getEverAPI().getManagerUtils().getDate().diff(delay))
				.sendTo(player);
		}
		
		// Téléportation
		player.setTeleport(delay, () -> this.teleport(player, back.get()), player.hasPermission(EEPermissions.TELEPORT_BYPASS_MOVE.get()));
		return CompletableFuture.completedFuture(true);
	}
	
	private void teleport(final EPlayer player, final Transform<World> teleport) {
		// Le joueur n'est pas en ligne
		if (!player.isOnline()) {
			return;
		}
			
		// La position n'est pas Safe
		if (!player.teleportSafe(teleport, true)) {
			EEMessages.BACK_ERROR_LOCATION.sendTo(player);
			return;
		}
		
		EEMessages.BACK_TELEPORT.sender()
			.replace("<back>", () -> this.getButtonLocation(teleport.getLocation()))
			.sendTo(player);
	}
	
	private Text getButtonLocation(final Location<World> location){
		return EEMessages.BACK_NAME.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.BACK_NAME_HOVER.getFormat().toText(
							"<world>", location.getExtent().getName(),
							"<x>", String.valueOf(location.getBlockX()),
							"<y>", String.valueOf(location.getBlockY()),
							"<z>", String.valueOf(location.getBlockZ()))))
					.build();
	}
}
