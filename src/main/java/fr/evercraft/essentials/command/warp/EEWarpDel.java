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
import fr.evercraft.everapi.text.ETextBuilder;

public class EEWarpDel extends ECommand<EverEssentials> {
	
	public EEWarpDel(final EverEssentials plugin) {
        super(plugin, "delwarp", "delwarps");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.DELWARP.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.DELWARP_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_WARP.get() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			for(String warp : this.plugin.getManagerServices().getWarp().getAll().keySet()){
				suggests.add(warp);
			}
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
			commandDeleteWarp((EPlayer) source, args.get(0));
		} else if(args.size() == 2 && args.get(1).equalsIgnoreCase("confirmation")) {
			commandDeleteWarpConfirmation((EPlayer) source, args.get(0));
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(getHelp(source).get());
		}
		return resultat;
	}
	
	public boolean commandDeleteWarp(final EPlayer player, final String warp_name) {
		String name = EChat.fixLength(warp_name, this.plugin.getEverAPI().getConfigs().get("maxCaractere").getInt(16));
		Optional<Transform<World>> warp = this.plugin.getManagerServices().getWarp().get(name);
		// Le serveur a un warp qui porte ce nom
		if(warp.isPresent()) {
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.DELWARP_CONFIRMATION.get())
					.replace("<warp>", getButtonWarp(name, warp.get()))
					.replace("<confirmation>", getButtonConfirmation(name))
					.build());
		// Le serveur n'a pas de warp qui porte ce nom
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.DELWARP_INCONNU.get().replaceAll("<warp>", name));
		}
		return false;
	}
	
	public boolean commandDeleteWarpConfirmation(final EPlayer player, final String warp_name) throws ServerDisableException {
		String name = EChat.fixLength(warp_name, this.plugin.getEverAPI().getConfigs().get("maxCaractere").getInt(16));
		Optional<Transform<World>> warp = this.plugin.getManagerServices().getWarp().get(name);
		// Le serveur a un warp qui porte ce nom
		if(warp.isPresent()) {
			// Si le warp a bien été supprimer
			if(this.plugin.getManagerServices().getWarp().remove(name)) {
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.DELWARP_DELETE.get())
						.replace("<warp>", getButtonWarp(name, warp.get()))
						.build());
				return true;
			// Le warp n'a pas été supprimer
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EAMessages.COMMAND_ERROR.get());
			}
			// Le serveur n'a pas de warp qui porte ce nom
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.DELWARP_INCONNU.get().replaceAll("<warp>", name));
		}
		return false;
	}
	
	public Text getButtonWarp(final String name, final Transform<World> location){
		return EChat.of(EEMessages.DELWARP_NAME.get().replaceAll("<name>", name)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.DELWARP_NAME_HOVER.get()
							.replaceAll("<warp>", name)
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getLocation().getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getLocation().getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getLocation().getBlockZ())))))
					.build();
	}
	
	public Text getButtonConfirmation(final String name){
		return EEMessages.DELWARP_CONFIRMATION_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.DELWARP_CONFIRMATION_VALID_HOVER.get()
							.replaceAll("<warp>", name))))
					.onClick(TextActions.runCommand("/delwarp \"" + name + "\" confirmation"))
					.build();
	}
}
