package fr.evercraft.essentials.commands;

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
			return Text.builder("/effect [joueur] <effet> [niveau] [durée]").onClick(TextActions.suggestCommand("/effect "))
					.color(TextColors.RED).build();
		}
		return Text.builder("/ping").onClick(TextActions.suggestCommand("/effect <effet> [niveau] [durée]"))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> list = new ArrayList<String>();
		if (source instanceof Player){
			if(args.size() == 1){
				if (source.hasPermission(this.plugin.getPermissions().get("EFFECT_OTHERS"))){
					list = null;
				} else {
					list = UtilsEffect.getEffects();
				}
			} else if (args.size() == 2){
				if (source.hasPermission(this.plugin.getPermissions().get("EFFECT_OTHERS"))){
					list = UtilsEffect.getEffects();
				} else {
					if (UtilsEffect.getEffect(args.get(0)).isPresent()){
						UtilsEffect effect = UtilsEffect.getEffect(args.get(0)).get();
						for(int cpt = effect.getMinAmplifier(); cpt <= effect.getMaxAmplifier(); cpt++){
							list.add(String.valueOf(cpt));
						}
					} else {
						// erreur nom effect
					}
				}
			} else if (args.size() == 3){
				if (source.hasPermission(this.plugin.getPermissions().get("EFFECT_OTHERS"))){
					if (UtilsEffect.getEffect(args.get(0)).isPresent()){
						UtilsEffect effect = UtilsEffect.getEffect(args.get(0)).get();
						for(int cpt = effect.getMinAmplifier(); cpt <= effect.getMaxAmplifier(); cpt++){
							list.add(String.valueOf(cpt));
						}
					} else {
						// erreur nom effect
					}
				} else {
					list = UtilsEffect.getEffects();
				}
			} else if (args.size() == 3 && source.hasPermission(this.plugin.getPermissions().get("EFFECT_OTHERS"))){
				
			}
		}
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(source instanceof EPlayer) {
			EPlayer player = (EPlayer) source;
			// Affichage de l'aide
			if(args.size() == 0) {
				source.sendMessage(help(source));
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
			player.addEffect(createPotionEffect(UtilsEffect.getEffect(effect).get().getType(), 30, 0));
			return true;
		} else {
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX"));	
			return false;
		}
	}
	
	public boolean commandEffectOthers(final EPlayer player, final EPlayer target, final String arg) throws CommandException {
		// La source et le joueur sont différent
		if(!player.equals(target)){
			if (UtilsEffect.getEffect(arg).isPresent()){
				target.addEffect(createPotionEffect(UtilsEffect.getEffect(arg).get().getType(), 30, 0));
				return true;
			} else {
				player.sendMessage(this.plugin.getMessages().getText("PREFIX"));	
				return false;
			}
		// La source et le joueur sont identique
		} else {
			return execute(player, new ArrayList<String>());
		}
	}
	
	private PotionEffect createPotionEffect(PotionEffectType type, int duration, int amplifier){
		return PotionEffect
				.builder()
				.potionType(type)
			    .duration(duration)
			    .amplifier(amplifier)
			    .build();
	}
}
