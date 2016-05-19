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
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEffect;

public class EEEffect extends ECommand<EverEssentials> {
	
	public EEEffect(final EverEssentials plugin) {
        super(plugin, "effect", "effects");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("EFFECT"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("EFFECT_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(this.plugin.getPermissions().get("EFFECT_OTHERS"))){
			return Text.builder("/effect [joueur] <effet> [amplification] [durée]").onClick(TextActions.suggestCommand("/effect "))
					.color(TextColors.RED).build();
		}
		return Text.builder("/ping").onClick(TextActions.suggestCommand("/effect <effet> [niveau] [durée]"))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> list = new ArrayList<String>();
		if (source instanceof Player){
			if(args.size() == 1){
				list = UtilsEffect.getEffects();
			} else if (args.size() == 2){
				if (source.hasPermission(this.plugin.getPermissions().get("EFFECT_OTHERS"))){
					list = UtilsEffect.getEffects();
				} else {
					if (UtilsEffect.getEffect(args.get(0)).isPresent()){
						UtilsEffect effect = UtilsEffect.getEffect(args.get(0)).get();
						for(int cpt = effect.getMinAmplifier(); cpt <= effect.getMaxAmplifier(); cpt++){
							list.add(String.valueOf(cpt));
						}
					}
				}
			} else if (args.size() == 3){
				if (source.hasPermission(this.plugin.getPermissions().get("EFFECT_OTHERS"))){
					if (UtilsEffect.getEffect(args.get(0)).isPresent()){
						UtilsEffect effect = UtilsEffect.getEffect(args.get(0)).get();
						for(int cpt = effect.getMinAmplifier(); cpt <= effect.getMaxAmplifier(); cpt++){
							list.add(String.valueOf(cpt));
						}
					}
				} else {
					list = UtilsEffect.getEffects();
				}
			} else if (args.size() == 3 && source.hasPermission(this.plugin.getPermissions().get("EFFECT_OTHERS"))){
				
			}
		}
		return list;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(source instanceof EPlayer) {
			EPlayer player = (EPlayer) source;
			// Affichage de l'aide
			if(args.size() == 0) {
				//source.sendMessage(help(source));
				this.plugin.getEServer().broadcast(PotionEffectTypes.ABSORPTION.getId() 
						+ " " + PotionEffectTypes.ABSORPTION.getName());
			// Ajout de l'effect avec amplifier et durée par défaut
			} else if(args.size() == 1) {
				commandEffect(player, args.get(0));
			} else if(args.size() == 2){
				if (player.hasPermission(this.plugin.getPermissions().get("EFFECT_OTHERS"))){
					Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
					if (optPlayer.isPresent()){
						EPlayer target = optPlayer.get();
						commandEffectOthers(player, target, args.get(0));
					} else {
						source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") 
								+ this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
					}
				} else {
					try {
						int amplification = Integer.valueOf(args.get(1));
						commandEffect(player, args.get(0), amplification);
					// Nombre invalide
					} catch(NumberFormatException e) {
						player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") 
								+ this.plugin.getEverAPI().getMessages().getMessage("NUMBER_INVALID"));
					}
				}
			
			} else {
				source.sendMessage(help(source));
			}
		} else {
			source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
		}
		return resultat;
	}
	
	public boolean commandEffect(final EPlayer player, final String effect) {
		if (UtilsEffect.getEffect(effect).isPresent()){
			player.addEffect(createPotionEffect(UtilsEffect.getEffect(effect).get().getType(), getDefaultAmplifier(), getDefaultDuration()));
			return true;
		} else {
			player.sendMessage(this.plugin.getMessages().getText("PREFIX") 
					+ this.plugin.getMessages().getMessage("EFFECT_ERROR_NAME"));	
			return false;
		}
	}
	
	public boolean commandEffect(final EPlayer player, final String effect, final int amplifier) {
		if (UtilsEffect.getEffect(effect).isPresent()){
			UtilsEffect utils = UtilsEffect.getEffect(effect).get();
			if (utils.getMinAmplifier() >= amplifier && amplifier >= utils.getMaxAmplifier()){
				player.addEffect(createPotionEffect(utils.getType(), amplifier, getDefaultDuration()));
			} else {
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") 
						+ this.plugin.getMessages().getMessage("EFFECT_ERROR_AMPLIFIER"));
			}
			return true;
		} else {
			player.sendMessage(this.plugin.getMessages().getText("PREFIX") 
					+ this.plugin.getMessages().getMessage("EFFECT_ERROR_NAME"));	
			return false;
		}
	}
	
	public boolean commandEffectOthers(final EPlayer player, final EPlayer target, final String effect) throws CommandException {
		// La source et le joueur sont différent
		if(!player.equals(target)){
			if (UtilsEffect.getEffect(effect).isPresent()){
				target.addEffect(createPotionEffect(UtilsEffect.getEffect(effect).get().getType(), getDefaultAmplifier(), getDefaultDuration()));
				return true;
			} else {
				player.sendMessage(this.plugin.getMessages().getText("PREFIX") 
						+ this.plugin.getMessages().getMessage("EFFECT_ERROR_NAME"));	
				return false;
			}
		// La source et le joueur sont identique
		} else {
			return execute(player, new ArrayList<String>());
		}
	}
	
	public boolean commandEffectOthers(final EPlayer player, final EPlayer target, final String effect, final int amplifier) throws CommandException {
		// La source et le joueur sont différent
		if(!player.equals(target)){
			if (UtilsEffect.getEffect(effect).isPresent()){
				target.addEffect(createPotionEffect(UtilsEffect.getEffect(effect).get().getType(), amplifier, getDefaultDuration()));
				return true;
			} else {
				player.sendMessage(this.plugin.getMessages().getText("PREFIX") 
						+ this.plugin.getMessages().getMessage("EFFECT_ERROR_NAME"));	
				return false;
			}
		// La source et le joueur sont identique
		} else {
			return execute(player, new ArrayList<String>());
		}
	}
	
	private PotionEffect createPotionEffect(PotionEffectType type, int amplifier, int duration){
		return PotionEffect
				.builder()
				.potionType(type)
			    .amplifier(amplifier)
			    .duration(duration)
			    .build();
	}
	
	private int getDefaultDuration(){
		return this.plugin.getConfigs().get("effect-default-duration").getInt();
	}
	
	private int getDefaultAmplifier(){
		return this.plugin.getConfigs().get("effect-default-amplifier").getInt();
	}
	
	/*
	private int getMaxDuration(){
		return this.plugin.getConfigs().get("effect-default-max-duration").getInt();
	}
	*/
}
