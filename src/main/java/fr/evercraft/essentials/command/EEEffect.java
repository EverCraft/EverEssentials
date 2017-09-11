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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEffect;

public class EEEffect extends ECommand<EverEssentials> {
	
	private int default_duration;
	private int max_duration;
	private int default_amplifier;
	private boolean unsafe;
	
	public EEEffect(final EverEssentials plugin) {
		super(plugin, "effect", "effects");
		this.reload();
	}

	@Override
	public void reload() {
		super.reload();
		
		this.default_duration = this.plugin.getConfigs().getEffectDurationDefault();
		this.max_duration = this.plugin.getConfigs().getEffectDurationMax();
		this.default_amplifier = this.plugin.getConfigs().getEffectAmplifierDefault();
		this.unsafe = this.plugin.getConfigs().isEffectUnsafe();
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.EFFECT.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.EFFECT_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_EFFECT.getString() + "> "
				+ "[" + EAMessages.ARGS_AMPLIFICATION.getString() + "] [" + EAMessages.ARGS_SECONDS.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}

	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (source instanceof Player) {
			
			// Effet
			if (args.size() == 1) {
				return UtilsEffect.getEffects();
				
			// Amplification
			} else if (args.size() == 2) {
				List<String> suggests = new ArrayList<String>();
				Optional<UtilsEffect> effect = UtilsEffect.getEffect(args.get(0));
				if (effect.isPresent()) {
					for (int cpt = 1; cpt <= effect.get().getMaxAmplifier(); cpt++) {
						suggests.add(String.valueOf(cpt));
					}
				}
				return suggests;
				
			// Duration
			} else if (args.size() == 3) {
				return Arrays.asList("30", "60", "600");
			}
			
		}
		return Arrays.asList();
	}

	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (source instanceof EPlayer) {
			EPlayer player = (EPlayer) source;
			// Affichage de l'aide
			if (args.size() == 0) {
				
				player.sendMessage(this.help(source));
				
			// Ajout de l'effect avec amplifier et durée par défaut
			} else if (args.size() == 1) {
				
				return this.commandEffect(player, args.get(0));
				
			// Ajout de l'effect avec durée par défaut et amplifier personnalisé
			} else if (args.size() == 2) {
				
				try {
					return this.commandEffect(player, args.get(0), Integer.valueOf(args.get(1)));
					// Nombre invalide
				} catch (NumberFormatException e) {
					EAMessages.IS_NOT_NUMBER.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{number}", args.get(1))
						.sendTo(player);
				}
				
			// Ajout de l'effect avec durée et amplifier personnalisé
			} else if (args.size() == 3) {
				
				try {
					int amplification = Integer.valueOf(args.get(1));
					try {
						int duration = Integer.valueOf(args.get(2)) * 20;
						return this.commandEffect(player, args.get(0), amplification, duration);
					} catch (NumberFormatException e) {
						EAMessages.IS_NOT_NUMBER.sender()
							.prefix(EEMessages.PREFIX)
							.replace("{number}", args.get(2))
							.sendTo(player);
					}
				} catch (NumberFormatException e) {
					EAMessages.IS_NOT_NUMBER.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{number}", args.get(1))
						.sendTo(player);
				}
				
			} else {
				source.sendMessage(this.help(source));
			}
		} else {
			EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(source);
		}
		
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandEffect(final EPlayer player, final String name_effect) {
		Optional<UtilsEffect> effect = UtilsEffect.getEffect(name_effect);
		
		// L'effet n'existe pas
		if (!effect.isPresent()) {
			EEMessages.EFFECT_ERROR_NAME.sender()
				.replace("{effect}", name_effect)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		player.addPotion(this.createPotionEffect(effect.get().getType(), this.default_amplifier, this.default_duration));
		return CompletableFuture.completedFuture(true);
	}

	private CompletableFuture<Boolean> commandEffect(final EPlayer player, final String name_effect, final int amplifier) {
		Optional<UtilsEffect> effect = UtilsEffect.getEffect(name_effect);
		
		// L'effet n'existe pas
		if (!effect.isPresent()) {
			EEMessages.EFFECT_ERROR_NAME.sender()
				.replace("{effect}", name_effect)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// La valeur de l'amplifieur n'est pas correcte
		if (amplifier < 1 || (amplifier > effect.get().getMaxAmplifier() && !this.unsafe)) {
			EEMessages.EFFECT_ERROR_AMPLIFIER.sender()
				.replace("{min}", "1")
				.replace("{max}", String.valueOf(effect.get().getMaxAmplifier()))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		player.addPotion(this.createPotionEffect(effect.get().getType(), amplifier - 1, this.default_duration));
		return CompletableFuture.completedFuture(true);
	}

	private CompletableFuture<Boolean> commandEffect(final EPlayer player, final String name_effect, final int amplifier, final int duration) {
		Optional<UtilsEffect> effect = UtilsEffect.getEffect(name_effect);
		
		// L'effet n'existe pas
		if (!effect.isPresent()) {
			EEMessages.EFFECT_ERROR_NAME.sender()
				.replace("{effect}", name_effect)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// La valeur de l'amplifieur n'est pas correcte
		if (amplifier < 1 || (amplifier > effect.get().getMaxAmplifier() && !this.unsafe)) {
			EEMessages.EFFECT_ERROR_AMPLIFIER.sender()
				.replace("{min}", "1")
				.replace("{max}", String.valueOf(effect.get().getMaxAmplifier()))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// La durée n'est pas correcte
		if (duration < 0 || duration > this.max_duration) {
			EEMessages.EFFECT_ERROR_DURATION.sender()
				.replace("{min}", "1")
				.replace("{max}", String.valueOf(this.max_duration))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		player.addPotion(createPotionEffect(effect.get().getType(), amplifier - 1, duration));
		return CompletableFuture.completedFuture(true);
	}

	private PotionEffect createPotionEffect(PotionEffectType type, int amplifier, int duration) {
		return PotionEffect.builder()
				.potionType(type)
				.amplifier(amplifier)
				.particles(true)
				.duration(duration).build();
	}
}
