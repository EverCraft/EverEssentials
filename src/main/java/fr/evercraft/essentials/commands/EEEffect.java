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

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEffect;

public class EEEffect extends ECommand<EverEssentials> {

	public EEEffect(final EverEssentials plugin) {
		super(plugin, "effect", "effects");
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.EFFECT.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.EFFECT_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/effect <effet> [amplification] [durée]").onClick(TextActions.suggestCommand("/effect"))
				.color(TextColors.RED).build();
	}

	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (source instanceof Player) {
			if (args.size() == 1) {
				// Effet
				suggests = UtilsEffect.getEffects();
			} else if (args.size() == 2) {
				// Amplification
				if (UtilsEffect.getEffect(args.get(0)).isPresent()) {
					UtilsEffect effect = UtilsEffect.getEffect(args.get(0)).get();
					for (int cpt = effect.getMinAmplifier(); cpt <= effect.getMaxAmplifier(); cpt++) {
						suggests.add(String.valueOf(cpt));
					}
				}
			} else if (args.size() == 3) {
				suggests.add("30");
				suggests.add("60");
				suggests.add("600");
			}
		}
		return suggests;
	}

	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if (source instanceof EPlayer) {
			EPlayer player = (EPlayer) source;
			// Affichage de l'aide
			if (args.size() == 0) {
				player.sendMessage(help(source));
			// Ajout de l'effect avec amplifier et durée par défaut
			} else if (args.size() == 1) {
				commandEffect(player, args.get(0));
			// Ajout de l'effect avec durée par défaut et amplifier personnalisé
			} else if (args.size() == 2) {
				try {
					int amplification = Integer.valueOf(args.get(1));
					commandEffect(player, args.get(0), amplification);
					// Nombre invalide
				} catch (NumberFormatException e) {
					player.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.NUMBER_INVALID.getText()));
				}
			// Ajout de l'effect avec durée et amplifier personnalisé
			} else if (args.size() == 3) {
				try {
					int amplification = Integer.valueOf(args.get(1));
					int duration = Integer.valueOf(args.get(2)) * 20;
					commandEffect(player, args.get(0), amplification, duration);
				} catch (NumberFormatException e) {
					player.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.NUMBER_INVALID.getText()));
				}
			} else {
				source.sendMessage(help(source));
			}
		} else {
			source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
		}
		return resultat;
	}

	public boolean commandEffect(final EPlayer player, final String effect) {
		if (UtilsEffect.getEffect(effect).isPresent()) {
			this.plugin.getEServer().broadcast("test 1");
			PotionEffect potion = createPotionEffect(UtilsEffect.getEffect(effect).get().getType(), getDefaultAmplifier(), getDefaultDuration());
			this.plugin.getEServer().broadcast("" + potion);
			player.addPotion(potion);
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.EFFECT_ERROR_NAME.getText()));
			return false;
		}
	}

	public boolean commandEffect(final EPlayer player, final String effect, final int amplifier) {
		if (UtilsEffect.getEffect(effect).isPresent()) {
			UtilsEffect utils = UtilsEffect.getEffect(effect).get();
			if (utils.getMinAmplifier() <= amplifier && amplifier <= utils.getMaxAmplifier()) {
				player.addPotion(createPotionEffect(utils.getType(), amplifier - 1, getDefaultDuration()));
			} else {
				player.sendMessage(EEMessages.PREFIX.get()
					+ EEMessages.EFFECT_ERROR_AMPLIFIER.get()
						.replaceAll("<min>", String.valueOf(utils.getMinAmplifier()))
						.replaceAll("<max>", String.valueOf(utils.getMaxAmplifier())));
			}
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.get() 
				+ EEMessages.EFFECT_ERROR_NAME.get());
			return false;
		}
	}

	public boolean commandEffect(final EPlayer player, final String effect, final int amplifier, final int duration) {
		if (UtilsEffect.getEffect(effect).isPresent()) {
			UtilsEffect utils = UtilsEffect.getEffect(effect).get();
			if (utils.getMinAmplifier() <= amplifier && amplifier <= utils.getMaxAmplifier()) {
				if (duration > 0 && duration <= getMaxDefaultDuration()) {
					player.addPotion(createPotionEffect(utils.getType(), amplifier - 1, duration));
				} else {
					player.sendMessage(EEMessages.PREFIX.get() 
						+ EEMessages.EFFECT_ERROR_DURATION.get()
							.replaceAll("<min>", String.valueOf(1))
							.replaceAll("<max>", String.valueOf(getMaxDefaultDuration())));
				}
			} else {
				player.sendMessage(EEMessages.PREFIX.get()
						+ EEMessages.EFFECT_ERROR_AMPLIFIER.get()
							.replaceAll("<min>", String.valueOf(utils.getMinAmplifier()))
							.replaceAll("<max>", String.valueOf(utils.getMaxAmplifier() / 20)));
			}
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.get() 
					+ EEMessages.EFFECT_ERROR_NAME.get());
			return false;
		}
	}

	private PotionEffect createPotionEffect(PotionEffectType type, int amplifier, int duration) {
		return PotionEffect.builder()
				.potionType(type)
				.amplifier(amplifier)
				.particles(true)
				.duration(duration).build();
	}

	private int getDefaultDuration() {
		return this.plugin.getConfigs().get("effect-default-duration").getInt() * 20;
	}
	
	private int getMaxDefaultDuration() {
		return this.plugin.getConfigs().get("effect-default-max-duration").getInt() * 20;
	}

	private int getDefaultAmplifier() {
		return this.plugin.getConfigs().get("effect-default-amplifier").getInt();
	}
}
