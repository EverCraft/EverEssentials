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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import ninja.leaping.configurate.ConfigurationNode;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.java.UtilsMap;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEList extends ECommand<EverEssentials> {
	
	private static final String GROUPS_DEFAULT = "*";
	private static final String HIDDEN = "hidden";

	public EEList(final EverEssentials plugin) {
        super(plugin, "list");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.LIST.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.LIST_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/list").onClick(TextActions.suggestCommand("/list"))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandList(source, this.plugin.getEServer().getOnlineEPlayers((EPlayer) source));
			// La source n'est pas un joueur
			} else {
				resultat = commandList(source, this.plugin.getEServer().getOnlineEPlayers());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean commandList(final CommandSource staff, final Collection<EPlayer> players) throws CommandException {
		// Liste des groupes avec les joueurs
		Map<String, TreeMap<String, EPlayer>> groups = new HashMap<String, TreeMap<String, EPlayer>>();
		for(EPlayer player : players) {
			Optional<Subject> group = player.getGroup();
			if(group.isPresent()) {
				if(!groups.containsKey(group.get().getIdentifier())) {
					groups.put(group.get().getIdentifier(), new TreeMap<String, EPlayer>());
				}
				groups.get(group.get().getIdentifier()).put(player.getName(), player);
			} else {
				if(!groups.containsKey(GROUPS_DEFAULT)) {
					groups.put(GROUPS_DEFAULT, new TreeMap<String, EPlayer>());
				}
				groups.get(GROUPS_DEFAULT).put(player.getName(), player);
			}
		}
		
		// Cache les groupes et redimmensionne
		for(Entry<Object, ? extends ConfigurationNode> config : this.plugin.getConfigs().getConfigList().getChildrenMap().entrySet()) {
			if(config.getKey() instanceof String && groups.containsKey((String) config.getKey())) {
				// Groupe caché
				if(config.getValue().getString("").equalsIgnoreCase(HIDDEN)) {
					groups.remove((String) config.getKey());
				// Redimensionne
				} else {
					int size = config.getValue().getInt(-1);
					if(size == 0) {
						groups.remove((String) config.getKey());
					} else if(size > 0 && groups.containsKey((String) config.getKey())) {
						TreeMap<String, EPlayer> group_players = groups.get((String) config.getKey());
						if(group_players.size() > size) {
							groups.put((String) config.getKey(), UtilsMap.split(group_players, size));
						}
					}
				}
			}
		}
		
		// Liste des groupes à afficher
		TreeMap<String, TreeMap<String, EPlayer>> groups_format = new TreeMap<String, TreeMap<String, EPlayer>>();
		for(Entry<Object, ? extends ConfigurationNode> config : this.plugin.getConfigs().getConfigList().getChildrenMap().entrySet()) {
			if(config.getKey() instanceof String) {
				Object value = config.getValue().getValue();
				if(value instanceof String && !((String) value).equalsIgnoreCase(HIDDEN)) {
					for(String group : ((String) value).split(" ")) {
						if(groups.containsKey(group)) {
							if(groups_format.containsKey(group)) {
								groups_format.get(group).putAll(groups.get(group));;
							} else {
								groups_format.put(group, groups.get(group));
							}
							groups.remove(group);
						}
					}
				}
			}
		}
		
		// Ajoute les groupes restant
		for(Entry<String, TreeMap<String, EPlayer>> group : groups.entrySet()) {
			if(group.getKey() != GROUPS_DEFAULT) {
				groups_format.put(group.getKey(), group.getValue());
			}
		}
		
		String style_player = EEMessages.LIST_PLAYER.get();
		String style_afk = EEMessages.LIST_TAG_AFK.get();
		String style_vanish = EEMessages.LIST_TAG_VANISH.get();
		
		String style_group = EEMessages.LIST_GROUP.get();
		Text style_separator = EEMessages.LIST_SEPARATOR.getText();
		
		List<Text> group_texts = new ArrayList<Text>();
		for(Entry<String, TreeMap<String, EPlayer>> group : groups_format.entrySet()) {
			List<Text> player_texts = new ArrayList<Text>();
			for(EPlayer player : group.getValue().values()) {
				String text = style_player;
				
				if(player.isAFK()) {
					text = text.replaceAll("<afk>", style_afk);
				} else {
					text = text.replaceAll("<afk>", "");
				}
				
				if(player.isVanish()) {
					text = text.replaceAll("<vanish>", style_vanish);
				} else {
					text = text.replaceAll("<vanish>", "");
				}
				
				player_texts.add(this.plugin.getChat().replaceFormat(player, this.plugin.getChat().replacePlayer(player, text)));
			}
			group_texts.add(ETextBuilder.toBuilder(style_group
								.replaceAll("<group>", group.getKey()))
							.replace("<players>", Text.joinWith(style_separator, player_texts))
							.build());
		}
		
		if(group_texts.isEmpty()) {
			group_texts.add(EEMessages.LIST_EMPTY.getText());
		}
		
		String title;
		Integer vanish = players.size() - this.plugin.getEServer().playerNotVanish();
		if(vanish == 0) {
			title = EEMessages.LIST_TITLE.get();
		} else {
			title = EEMessages.LIST_TITLE_VANISH.get();
			title = title.replaceAll("<vanish>", vanish.toString());
		}
		
		title = this.plugin.getChat().replaceGlobal(title);
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EChat.of(title).toBuilder()
					.onClick(TextActions.runCommand("/list"))
					.build(), 
				group_texts, staff);
		
		return true;
	}
}
