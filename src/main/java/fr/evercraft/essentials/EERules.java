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
package fr.evercraft.essentials;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.text.Text;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.file.EConfig;

public class EERules extends EConfig {

	public EERules(EverEssentials plugin, String name) {
		super(plugin, name);
	}

	@Override
	public void loadDefault() {
		addDefault("rules.title", "&aRègles d'Evercraft");
		List<String> list = Arrays.asList(
			"   &c&l[1] &7Il est interdit de tenir des propos injurieux, politiques, racistes, nuisant envers les autres joueurs ou à un membre du staff.",
			"   &c&l[2] &7Vous devez adopter une façon d'écrire dans le chat propre, lisible, et correct, penser à vous relire.",
			"   &c&l[3] &7Le spam, le flood, et l'écriture en majuscule sont interdit.",
			"   &c&l[4] &7La pub pour votre serveur ou un autre serveur est interdit.",
			"   &c&l[5] &7L'utilisation de mode de triche est interdit.",
			"   &c&l[6] &7L'exploitation de bugs majeurs est interdit notamment pour la duplication et la facilité du jeu : bug de Minecraft ou d'un plugin.",
			"   &c&l[7] &7Menacer un joueur d'une attaque DDOS est interdit.",
			"   &c&l[8] &7Les packs de textures permettant de tricher sont interdit.");
		addDefault("rules.list", list);
		
	}
	
	public Text getTitle() {
		return EChat.of(this.get("rules.title").getString(""));
	}
	
	public List<Text> getList() {
		return EChat.of(this.getListString("rules.list"));
	}
}
