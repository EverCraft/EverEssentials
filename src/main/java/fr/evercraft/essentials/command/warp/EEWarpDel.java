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
package fr.evercraft.essentials.command.warp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
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

public class EEWarpDel extends ECommand<EverEssentials> {
	
	public EEWarpDel(final EverEssentials plugin) {
        super(plugin, "delwarp", "delwarps");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.DELWARP.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.DELWARP_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_WARP.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			suggests.addAll(this.plugin.getManagerServices().getWarp().getAll().keySet());
		} else if (args.size() == 2){
			suggests.add("confirmation");
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException, ServerDisableException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if (args.size() == 1) {
			resultat = this.commandDeleteWarp((EPlayer) source, args.get(0));
		} else if (args.size() == 2 && args.get(1).equalsIgnoreCase("confirmation")) {
			resultat = this.commandDeleteWarpConfirmation((EPlayer) source, args.get(0));
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	private boolean commandDeleteWarp(final EPlayer player, final String warp_name) {
		String name = EChat.fixLength(warp_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());
		
		Optional<Transform<World>> warp = this.plugin.getManagerServices().getWarp().get(name);
		// Le serveur n'a pas de warp qui porte ce nom
		if (!warp.isPresent()) {
			EEMessages.DELWARP_INCONNU.sender()
				.replace("<warp>", name)
				.sendTo(player);
			return false;
		}
		
		EEMessages.DELWARP_CONFIRMATION.sender()
			.replace("<warp>", () -> this.getButtonWarp(name, warp.get()))
			.replace("<confirmation>", () -> this.getButtonConfirmation(name))
			.sendTo(player);
		return false;
	}
	
	private boolean commandDeleteWarpConfirmation(final EPlayer player, final String warp_name) throws ServerDisableException {
		String name = EChat.fixLength(warp_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());
		
		Optional<Transform<World>> warp = this.plugin.getManagerServices().getWarp().get(name);
		// Le serveur n'a pas de warp qui porte ce nom
		if (!warp.isPresent()) {
			EEMessages.DELWARP_INCONNU.sender()
				.replace("<warp>", name)
				.sendTo(player);
			return false;
		}
		
		// Le warp n'a pas été supprimer
		if (!this.plugin.getManagerServices().getWarp().remove(name)) {
			EEMessages.DELWARP_CANCEL.sender()
				.replace("<warp>", name)
				.sendTo(player);
			return false;
		}
		
		EEMessages.DELWARP_DELETE.sender()
			.replace("<warp>", () -> this.getButtonWarp(name, warp.get()))
			.sendTo(player);
		return true;
	}
	
	private Text getButtonWarp(final String name, final Transform<World> location){
		return EEMessages.DELWARP_NAME.getFormat().toText("<name>", name).toBuilder()
					.onHover(TextActions.showText(EEMessages.DELWARP_NAME_HOVER.getFormat().toText(
								"<warp>", name,
								"<world>", location.getExtent().getName(),
								"<x>", String.valueOf(location.getLocation().getBlockX()),
								"<y>", String.valueOf(location.getLocation().getBlockY()),
								"<z>", String.valueOf(location.getLocation().getBlockZ()))))
					.build();
	}
	
	private Text getButtonConfirmation(final String name){
		return EEMessages.DELWARP_CONFIRMATION_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.DELWARP_CONFIRMATION_VALID_HOVER.getFormat()
							.toText("<warp>", name)))
					.onClick(TextActions.runCommand("/delwarp \"" + name + "\" confirmation"))
					.build();
	}
}
