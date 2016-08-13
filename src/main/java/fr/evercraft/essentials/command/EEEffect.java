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
import fr.evercraft.everapi.plugin.command.EReloadCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEffect;

public class EEEffect extends EReloadCommand<EverEssentials> {
	
	private int default_duration;
	private int max_duration;
	private int default_amplifier;
	private boolean unsafe;
	
	public EEEffect(final EverEssentials plugin) {
		super(plugin, "effect", "effects");
		this.reload();
	}

	public void reload() {
		this.default_duration = this.plugin.getConfigs().getEffectDurationDefault();
		this.max_duration = this.plugin.getConfigs().getEffectDurationMax();
		this.default_amplifier = this.plugin.getConfigs().getEffectAmplifierDefault();
		this.unsafe = this.plugin.getConfigs().isEffectUnsafe();
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.EFFECT.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.EFFECT_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_EFFECT.get() + "> [" + EAMessages.ARGS_AMPLIFICATION.get() + "] [" + EAMessages.ARGS_SECONDS.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}

	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (source instanceof Player) {
			// Effet
			if (args.size() == 1) {
				suggests = UtilsEffect.getEffects();
			// Amplification
			} else if (args.size() == 2) {
				Optional<UtilsEffect> effect = UtilsEffect.getEffect(args.get(0));
				if (effect.isPresent()) {
					for (int cpt = 1; cpt <= effect.get().getMaxAmplifier(); cpt++) {
						suggests.add(String.valueOf(cpt));
					}
				}
			// Duration
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
				resultat = this.commandEffect(player, args.get(0));
			// Ajout de l'effect avec durée par défaut et amplifier personnalisé
			} else if (args.size() == 2) {
				try {
					resultat = this.commandEffect(player, args.get(0), Integer.valueOf(args.get(1)));
					// Nombre invalide
				} catch (NumberFormatException e) {
					player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
							.replaceAll("<number>", args.get(1)));
				}
			// Ajout de l'effect avec durée et amplifier personnalisé
			} else if (args.size() == 3) {
				try {
					int amplification = Integer.valueOf(args.get(1));
					try {
						int duration = Integer.valueOf(args.get(2)) * 20;
						resultat = this.commandEffect(player, args.get(0), amplification, duration);
					} catch (NumberFormatException e) {
						player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
								.replaceAll("<number>", args.get(2)));
					}
				} catch (NumberFormatException e) {
					player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
							.replaceAll("<number>", args.get(1)));
				}
			} else {
				source.sendMessage(this.help(source));
			}
		} else {
			source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
		}
		
		return resultat;
	}

	private boolean commandEffect(final EPlayer player, final String name_effect) {
		Optional<UtilsEffect> effect = UtilsEffect.getEffect(name_effect);
		// Si l'effet existe
		if(effect.isPresent()) {
			player.addPotion(this.createPotionEffect(effect.get().getType(), this.default_amplifier, this.default_duration));
			return true;
		// L'effet n'existe pas
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.EFFECT_ERROR_NAME.get()
					.replaceAll("<effect>", name_effect));
			return false;
		}
	}

	private boolean commandEffect(final EPlayer player, final String name_effect, final int amplifier) {
		Optional<UtilsEffect> effect = UtilsEffect.getEffect(name_effect);
		// Si l'effet existe
		if (effect.isPresent()) {
			// Si la valeur de l'amplifieur est correcte
			if (1 <= amplifier && (this.unsafe || amplifier <= effect.get().getMaxAmplifier())) {
				player.addPotion(createPotionEffect(effect.get().getType(), amplifier - 1, this.default_duration));
			// La valeur de l'amplifieur n'est pas correcte
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.EFFECT_ERROR_AMPLIFIER.get()
						.replaceAll("<min>", "1")
						.replaceAll("<max>", String.valueOf(effect.get().getMaxAmplifier())));
			}
			return true;
		// L'effet n'existe pas
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.EFFECT_ERROR_NAME.get()
					.replaceAll("<effect>", name_effect));
			return false;
		}
	}

	private boolean commandEffect(final EPlayer player, final String name_effect, final int amplifier, final int duration) {
		Optional<UtilsEffect> effect = UtilsEffect.getEffect(name_effect);
		// Si l'effet existe
		if (effect.isPresent()) {
			// Si la valeur de l'amplifieur est correcte
			if (1 <= amplifier && (this.unsafe || amplifier <= effect.get().getMaxAmplifier())) {
				// Si la durée est correcte
				if (duration > 0 && duration <= this.max_duration) {
					player.addPotion(createPotionEffect(effect.get().getType(), amplifier - 1, duration));
				// La durée n'est pas correcte
				} else {
					player.sendMessage(EEMessages.PREFIX.get() 
						+ EEMessages.EFFECT_ERROR_DURATION.get()
							.replaceAll("<min>", "1")
							.replaceAll("<max>", String.valueOf(this.max_duration)));
				}
			// La valeur de l'amplifieur n'est pas correcte
			} else {
				player.sendMessage(EEMessages.PREFIX.get()
						+ EEMessages.EFFECT_ERROR_AMPLIFIER.get()
							.replaceAll("<min>", "1")
							.replaceAll("<max>", String.valueOf(effect.get().getMaxAmplifier() / 20)));
			}
			return true;
		// L'effet n'existe pas
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
}
