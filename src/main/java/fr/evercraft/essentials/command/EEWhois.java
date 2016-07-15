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

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.chat.ChatVisibilities;
import org.spongepowered.api.text.chat.ChatVisibility;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.java.UtilsDouble;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEWhois extends ECommand<EverEssentials> {
	
	public EEWhois(final EverEssentials plugin) {
        super(plugin, "whois");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WHOIS.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.WHOIS_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/whois").onClick(TextActions.suggestCommand("/whois"))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if(args.size() == 1){
			return null;
		}
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Nom du home inconnu
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandWhoisPlayer(source, (EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// Nom du home connu
		} else if(args.size() == 1) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
			// Le joueur existe
			if(optPlayer.isPresent()){
				resultat = commandWhoisPlayer(source, optPlayer.get());
			// Joueur introuvable
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean commandWhoisPlayer(final CommandSource staff, final EPlayer player) {
		List<Text> lists = new ArrayList<Text>();

		lists.add(getUUID(player));
		lists.add(getIP(player));
		lists.add(getPing(player));
		lists.add(getHeal(player));
		lists.add(getFood(player));
		lists.add(getExp());
		lists.add(getExpLevel(player));
		lists.add(getExpPoint(player));
		lists.add(getSpeed());
		lists.add(getSpeedWalk(player));
		lists.add(getSpeedFly(player));
		lists.add(getLocation(player));
		if(this.plugin.getEverAPI().getManagerService().getEconomy().isPresent()) {
			lists.add(getBalance(player));
		}
		lists.add(getGameMode(player));
		lists.add(getFly(player));
		lists.add(getGod(player));
		lists.add(getMute(player));
		lists.add(getVanish(player));
		lists.add(getAFK(player));
		lists.add(getFirstDatePlayed(player));
		lists.add(getLastDatePlayed(player));
		lists.add(getChatVisibility(player));
		lists.add(getViewDistance(player));
		lists.add(ChatColorsEnabled(player));
		lists.add(getLocale(player));
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EChat.of(EEMessages.WHOIS_TITLE.get().replace("<player>", player.getName())).toBuilder()
					.onClick(TextActions.runCommand("/whois " + player.getName())).build(), 
				lists, staff);
		return false;
	}
	
	public Text getUUID(final EPlayer player){
		return ETextBuilder.toBuilder(EEMessages.WHOIS_UUID.get())
				.replace("<uuid>", getButtomUUID(player))
				.build();
	}
	
	public Text getButtomUUID(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_UUID_STYLE.get()
						.replaceAll("<uuid>", player.getUniqueId().toString())).toBuilder()
					.onHover(TextActions.showText(EChat.of(EAMessages.HOVER_COPY.get())))
					.onClick(TextActions.suggestCommand(player.getUniqueId().toString()))
					.onShiftClick(TextActions.insertText(player.getUniqueId().toString()))
					.build();
	}
	
	public Text getIP(final EPlayer player){
		return ETextBuilder.toBuilder(EEMessages.WHOIS_IP.get())
				.replace("<ip>", getButtomIP(player))
				.build();
	}
	
	public Text getButtomIP(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_IP_STYLE.get()
				.replaceAll("<ip>", player.getConnection().getAddress().getAddress().getHostAddress().toString())).toBuilder()
			.onHover(TextActions.showText(EChat.of(EAMessages.HOVER_COPY.get())))
			.onClick(TextActions.suggestCommand(player.getConnection().getAddress().getAddress().getHostAddress().toString()))
			.onShiftClick(TextActions.insertText(player.getConnection().getAddress().getAddress().getHostAddress().toString()))
			.build();
	}
	
	public Text getPing(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_PING.get()
				.replaceAll("<ping>", String.valueOf(player.getConnection().getLatency())));
	}
	
	public Text getHeal(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_HEAL.get()
				.replaceAll("<heal>", String.valueOf((int) Math.ceil(player.getHealth())))
				.replaceAll("<max_heal>", String.valueOf((int) Math.ceil(player.getMaxHealth()))));
	}
	
	public Text getFood(final EPlayer player) {
		int saturation = (int) Math.ceil(player.getSaturation());
		if(saturation > 0) {
			return EChat.of(EEMessages.WHOIS_FOOD_SATURATION.get()
				.replaceAll("<food>", String.valueOf(player.getFood()))
				.replaceAll("<max_food>", String.valueOf(EPlayer.MAX_FOOD))
				.replaceAll("<saturation>", String.valueOf(saturation)));
		} else {
			return EChat.of(EEMessages.WHOIS_FOOD.get()
				.replaceAll("<food>", String.valueOf(player.getFood()))
				.replaceAll("<max_food>", String.valueOf(EPlayer.MAX_FOOD)));
		}
	}
	
	public Text getExp(){
		return EChat.of(EEMessages.WHOIS_EXP.get());
	}
	
	public Text getExpLevel(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_EXP_LEVEL.get()
				.replaceAll("<level>", String.valueOf(player.getLevel())));
	}
	
	public Text getExpPoint(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_EXP_POINT.get()
				.replaceAll("<point>", String.valueOf(player.getLevel())));
	}
	
	public Text getSpeed(){
		return EChat.of(EEMessages.WHOIS_SPEED.get());
	}
	
	public Text getSpeedFly(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_SPEED_FLY.get()
				.replaceAll("<speed>", String.valueOf(UtilsDouble.round(player.getFlySpeed() / EPlayer.CONVERSION_FLY, 3))));
	}
	
	public Text getSpeedWalk(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_SPEED_WALK.get()
				.replaceAll("<speed>", String.valueOf( UtilsDouble.round(player.getWalkSpeed() / EPlayer.CONVERSION_WALF, 3))));
	}
	
	public Text getLocation(final EPlayer player){
		return ETextBuilder.toBuilder(EEMessages.WHOIS_LOCATION.get())
				.replace("<position>", getButtonLocation(player))
				.build();
	}
	
	public Text getButtonLocation(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_LOCATION_POSITION.get()
					.replaceAll("<x>", String.valueOf(player.getLocation().getBlockX()))
					.replaceAll("<y>", String.valueOf(player.getLocation().getBlockY()))
					.replaceAll("<z>", String.valueOf(player.getLocation().getBlockZ()))
					.replaceAll("<world>", player.getLocation().getExtent().getName())).toBuilder()
				.onHover(TextActions.showText(EChat.of(EEMessages.WHOIS_LOCATION_POSITION_HOVER.get()
						.replaceAll("<x>", String.valueOf(player.getLocation().getBlockX()))
						.replaceAll("<y>", String.valueOf(player.getLocation().getBlockY()))
						.replaceAll("<z>", String.valueOf(player.getLocation().getBlockZ()))
						.replaceAll("<world>", player.getLocation().getExtent().getName()))))
				.build();
	}
	
	public Text getBalance(final EPlayer player) {
		return EChat.of(EEMessages.WHOIS_BALANCE.get()
				.replaceAll("<money>", this.plugin.getEverAPI().getManagerService().getEconomy().get().getDefaultCurrency().format(player.getBalance()).toPlain()));
	}
	
	public Text getGameMode(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_GAMEMODE.get()
				.replaceAll("<gamemode>", this.plugin.getEverAPI().getManagerUtils().getGameMode().getName(player.getGameMode())));
	}
	
	public Text getGod(final EPlayer player){
		if(player.isGod()) {
			return EChat.of(EEMessages.WHOIS_GOD_ENABLE.get());
		} else {
			return EChat.of(EEMessages.WHOIS_GOD_DISABLE.get());
		}
	}
	
	public Text getFly(final EPlayer player){
		if(player.getAllowFlight()) {
			if(player.isFlying()) {
				return EChat.of(EEMessages.WHOIS_FLY_ENABLE_FLY.get());
			} else {
				return EChat.of(EEMessages.WHOIS_FLY_ENABLE_WALK.get());
			}
		} else {
			return EChat.of(EEMessages.WHOIS_FLY_DISABLE.get());
		}
	}
	
	public Text getMute(final EPlayer player) {
		// TODO EverSanctions
		return EChat.of(EEMessages.WHOIS_MUTE_ENABLE.get());
		/*
		if(player.isMute()) {
			return EChat.of(EEMessages.WHOIS_MUTE_ENABLE.get());
		} else {
			return EChat.of(EEMessages.WHOIS_MUTE_DISABLE.get());
		}*/
	}
	
	public Text getVanish(final EPlayer player){
		if(player.isVanish()) {
			return EChat.of(EEMessages.WHOIS_VANISH_ENABLE.get());
		} else {
			return EChat.of(EEMessages.WHOIS_VANISH_DISABLE.get());
		}
	}
	
	public Text getAFK(final EPlayer player){
		if(player.isAFK()) {
			return EChat.of(EEMessages.WHOIS_AFK_ENABLE.get());
		} else {
			return EChat.of(EEMessages.WHOIS_AFK_DISABLE.get());
		}
	}
	
	public Text getFirstDatePlayed(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_FIRST_DATE_PLAYED.get()
				.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(player.getFirstDatePlayed())));
	}
	
	public Text getLastDatePlayed(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_LAST_DATE_PLAYED.get()
				.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().formatDateDiff(player.getLastDatePlayed(), 3)));
	}
	
	public Text getChatVisibility(final EPlayer player){
		ChatVisibility chat = player.getChatVisibility();
		if (chat.equals(ChatVisibilities.FULL)){
			return EEMessages.WHOIS_CHAT_FULL.getText();
		} else if (chat.equals(ChatVisibilities.SYSTEM)){
			return EEMessages.WHOIS_CHAT_SYSTEM.getText();
		} else {
			return EEMessages.WHOIS_CHAT_HIDDEN.getText();
		}
	}
	
	public Text getViewDistance(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_VIEW_DISTANCE.get()
				.replaceAll("<amount>", String.valueOf(player.getViewDistance())));
	}
	
	public Text ChatColorsEnabled(final EPlayer player){
		if (player.isChatColorsEnabled()){
			return EEMessages.WHOIS_CHATCOLOR_ON.getText();
		} else {
			return EEMessages.WHOIS_CHATCOLOR_OFF.getText();
		}
	}
	
	public Text getLocale(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_LANGUAGE.get()
				.replaceAll("<langue>", StringUtils.capitalize(player.getLocale().getDisplayLanguage())));
	}
}
