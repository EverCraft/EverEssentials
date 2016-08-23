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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ninja.leaping.configurate.ConfigurationNode;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.java.UtilsMap;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.EReloadCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EENear extends EReloadCommand<EverEssentials> {
	
	private List<Entry<String, Integer>> permissions;
	private int permission_default;
	
	public EENear(final EverEssentials plugin) {
        super(plugin, "near");
        reload();
    }
	
	public void reload(){
		Map<String, Integer> permissions = new HashMap<String, Integer>();
		this.permission_default = this.plugin.getConfigs().get("near-distance.default").getInt(1);
		for (Entry<Object, ? extends ConfigurationNode> key : this.plugin.getConfigs().get("near-distance").getChildrenMap().entrySet()) {
			if (key.getKey() instanceof String) {
				permissions.put((String) key.getKey(), key.getValue().getInt(this.permission_default));
			}
		}
		this.permissions = UtilsMap.valueDESC(permissions);
	}
	
	public int getValue(final EPlayer player) {
		int max = this.permission_default;
		int cpt = 0;
		while (cpt < this.permissions.size() && max == this.permission_default) {
			if (player.hasPermission(EEPermissions.NEAR.get() + "." + this.permissions.get(cpt).getKey())) {
				max = this.permissions.get(cpt).getValue();
			}
			cpt++;
		}
		return max;
	}
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.NEAR.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.NEAR_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandNear((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// Si on ne connait pas le joueur
		} else {
			source.sendMessage(getHelp(source).get());
		}
		return resultat;
	}
	
	public boolean commandNear(final EPlayer player) {
		Map <EPlayer, Integer> list = player.getEPlayers(getValue(player));		
		if (list.isEmpty()) {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.NEAR_NOPLAYER.get());
		} else {
			List<Text> lists = new ArrayList<Text>();
			for (Entry<EPlayer, Integer> position : UtilsMap.valueASC(list)){
				lists.add(EChat.of(EEMessages.NEAR_LIST_LINE.get()
						.replaceAll("<player>", position.getKey().getName())
						.replaceAll("<distance>", position.getValue().toString())));
			}
			this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.NEAR_LIST_TITLE.getText().toBuilder()
					.onClick(TextActions.runCommand("/near")).build(), lists, player);
			return true;
		}
		return false;
	}
}