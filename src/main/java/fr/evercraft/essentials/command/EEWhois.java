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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
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
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everapi.sponge.UtilsNetwork;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEWhois extends ECommand<EverEssentials> {
	
	public EEWhois(final EverEssentials plugin) {
        super(plugin, "whois");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WHOIS.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.WHOIS_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.WHOIS_OTHERS.get())) {
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
		}
		return Text.builder("/" + this.getName())
				.onClick(TextActions.suggestCommand("/" + this.getName()))
				.color(TextColors.RED)
				.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1 && source.hasPermission(EEPermissions.WHOIS_OTHERS.get())){
			suggests.addAll(this.getAllUsers());
		}
		return suggests;
	}
	
	@Override
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
		} else if (args.size() == 1) {
			
			// Si il a la permission
			if (source.hasPermission(EEPermissions.WHOIS_OTHERS.get())) {
				Optional<EUser> user = this.plugin.getEServer().getEUser(args.get(0));
				// Le joueur existe
				if (user.isPresent()) {
					if(user.get() instanceof EPlayer) {
						resultat = this.commandWhoisPlayer(source, (EPlayer) user.get());
					} else {
						resultat = this.commandWhoisPlayer(source, user.get());
					}
				// Joueur introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		
		return resultat;
	}
	
	private boolean commandWhoisPlayer(final CommandSource staff, final EPlayer player) {
		List<Text> lists = new ArrayList<Text>();

		lists.add(this.getUUID(player));
		lists.add(this.getIP(player));
		lists.add(this.getPing(player));
		lists.add(this.getHeal(player));
		lists.add(this.getFood(player));
		lists.add(this.getExp());
		lists.add(this.getExpLevel(player));
		lists.add(this.getExpPoint(player));
		lists.add(this.getSpeed());
		lists.add(this.getSpeedWalk(player));
		lists.add(this.getSpeedFly(player));
		lists.add(this.getLocation(player));
		if (this.plugin.getEverAPI().getManagerService().getEconomy().isPresent()) {
			lists.add(this.getBalance(player));
		}
		lists.add(this.getGameMode(player));
		lists.add(this.getFly(player));
		lists.add(this.getGod(player));
		lists.add(this.getVanish(player));
		lists.add(this.getFreeze(player));
		lists.add(this.getAFK(player));
		lists.add(this.getFirstDatePlayed(player));
		lists.add(this.getLastDatePlayed(player));
		lists.add(this.getChatVisibility(player));
		lists.add(this.getViewDistance(player));
		lists.add(this.getChatColor(player));
		lists.add(this.getLocale(player));
		lists.add(this.getToggle(player));
		lists.add(this.getTotalTimePlayed(player));
		
		String title;
		if(player.getIdentifier().equals(staff.getIdentifier())) {
			title = EEMessages.WHOIS_TITLE_EQUALS.get();
		} else {
			title = EEMessages.WHOIS_TITLE_OTHERS.get();
		}
		
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EChat.of(title.replace("<player>", player.getName())).toBuilder()
					.onClick(TextActions.runCommand("/whois \"" + player.getName() + "\""))
					.build(), 
				lists, staff);
		return false;
	}
	
	private boolean commandWhoisPlayer(final CommandSource staff, final EUser user) {
		List<Text> lists = new ArrayList<Text>();

		lists.add(this.getUUID(user));
		if(user.getLastIP().isPresent()) {
			lists.add(this.getLastIp(user, user.getLastIP().get()));
		}
		lists.add(this.getHeal(user));
		lists.add(this.getFood(user));
		lists.add(this.getExp());
		lists.add(this.getExpLevel(user));
		lists.add(this.getExpPoint(user));
		lists.add(this.getSpeed());
		lists.add(this.getSpeedWalk(user));
		lists.add(this.getSpeedFly(user));
		if (this.plugin.getEverAPI().getManagerService().getEconomy().isPresent()) {
			lists.add(this.getBalance(user));
		}
		lists.add(this.getGameMode(user));
		lists.add(this.getFly(user));
		lists.add(this.getGod(user));
		lists.add(this.getVanish(user));
		lists.add(this.getFirstDatePlayed(user));
		lists.add(this.getLastDatePlayed(user));
		lists.add(this.getToggle(user));
		lists.add(this.getTotalTimePlayed(user));
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EChat.of(EEMessages.WHOIS_TITLE_OTHERS.get().replace("<player>", user.getName())).toBuilder()
					.onClick(TextActions.runCommand("/whois \"" + user.getName() + "\""))
					.build(), 
				lists, staff);
		return false;
	}
	
	private Text getUUID(final EUser player){
		return ETextBuilder.toBuilder(EEMessages.WHOIS_UUID.get())
				.replace("<uuid>", getButtomUUID(player))
				.build();
	}
	
	private Text getButtomUUID(final EUser player){
		return EChat.of(EEMessages.WHOIS_UUID_STYLE.get()
						.replaceAll("<uuid>", player.getUniqueId().toString())).toBuilder()
					.onHover(TextActions.showText(EChat.of(EAMessages.HOVER_COPY.get())))
					.onClick(TextActions.suggestCommand(player.getUniqueId().toString()))
					.onShiftClick(TextActions.insertText(player.getUniqueId().toString()))
					.build();
	}
	
	private Text getIP(final EPlayer player){
		return ETextBuilder.toBuilder(EEMessages.WHOIS_IP.get())
				.replace("<ip>", getButtomIP(UtilsNetwork.getHostString(player.getConnection().getAddress().getAddress())))
				.build();
	}
	
	private Text getLastIp(final User user, final InetAddress address) {
		return ETextBuilder.toBuilder(EEMessages.WHOIS_LAST_IP.get())
				.replace("<ip>", this.getButtomIP(UtilsNetwork.getHostString(address)))
				.build();
	}
	
	private Text getButtomIP(String address){
		return EChat.of(EEMessages.WHOIS_IP_STYLE.get()
				.replaceAll("<ip>", address)).toBuilder()
			.onHover(TextActions.showText(EChat.of(EAMessages.HOVER_COPY.get())))
			.onClick(TextActions.suggestCommand(address))
			.onShiftClick(TextActions.insertText(address))
			.build();
	}
	
	private Text getPing(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_PING.get()
				.replaceAll("<ping>", String.valueOf(player.getConnection().getLatency())));
	}
	
	private Text getHeal(final EUser player){
		return EChat.of(EEMessages.WHOIS_HEAL.get()
				.replaceAll("<heal>", String.valueOf((int) Math.ceil(player.getHealth())))
				.replaceAll("<max_heal>", String.valueOf((int) Math.ceil(player.getMaxHealth()))));
	}
	
	private Text getFood(final EUser player) {
		int saturation = (int) Math.ceil(player.getSaturation());
		if (saturation > 0) {
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
	
	private Text getExp(){
		return EChat.of(EEMessages.WHOIS_EXP.get());
	}
	
	private Text getExpLevel(final EUser player){
		return EChat.of(EEMessages.WHOIS_EXP_LEVEL.get()
				.replaceAll("<level>", String.valueOf(player.getLevel())));
	}
	
	private Text getExpPoint(final EUser player){
		return EChat.of(EEMessages.WHOIS_EXP_POINT.get()
				.replaceAll("<point>", String.valueOf(player.getLevel())));
	}
	
	private Text getSpeed(){
		return EChat.of(EEMessages.WHOIS_SPEED.get());
	}
	
	private Text getSpeedFly(final EUser player){
		return EChat.of(EEMessages.WHOIS_SPEED_FLY.get()
				.replaceAll("<speed>", String.valueOf(UtilsDouble.round(player.getFlySpeed() / EPlayer.CONVERSION_FLY, 3))));
	}
	
	private Text getSpeedWalk(final EUser player){
		return EChat.of(EEMessages.WHOIS_SPEED_WALK.get()
				.replaceAll("<speed>", String.valueOf( UtilsDouble.round(player.getWalkSpeed() / EPlayer.CONVERSION_WALF, 3))));
	}
	
	private Text getLocation(final EPlayer player){
		return ETextBuilder.toBuilder(EEMessages.WHOIS_LOCATION.get())
				.replace("<position>", getButtonLocation(player))
				.build();
	}
	
	private Text getButtonLocation(final EPlayer player){
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
	
	private Text getBalance(final EUser player) {
		return EChat.of(EEMessages.WHOIS_BALANCE.get()
				.replaceAll("<money>", this.plugin.getEverAPI().getManagerService().getEconomy().get().getDefaultCurrency().format(player.getBalance()).toPlain()));
	}
	
	private Text getGameMode(final EUser player){
		return EChat.of(EEMessages.WHOIS_GAMEMODE.get()
				.replaceAll("<gamemode>", this.plugin.getEverAPI().getManagerUtils().getGameMode().getName(player.getGameMode())));
	}
	
	private Text getGod(final EUser player){
		if (player.isGod()) {
			return EChat.of(EEMessages.WHOIS_GOD_ENABLE.get());
		} else {
			return EChat.of(EEMessages.WHOIS_GOD_DISABLE.get());
		}
	}
	
	private Text getFly(final EUser player){
		if (player.getAllowFlight()) {
			if (player.isFlying()) {
				return EChat.of(EEMessages.WHOIS_FLY_ENABLE_FLY.get());
			} else {
				return EChat.of(EEMessages.WHOIS_FLY_ENABLE_WALK.get());
			}
		} else {
			return EChat.of(EEMessages.WHOIS_FLY_DISABLE.get());
		}
	}
	
	@SuppressWarnings("unused")
	private Text getMute(final EPlayer player) {
		// TODO EverSanctions
		return EChat.of(EEMessages.WHOIS_MUTE_DISABLE.get());
		/*
		if (player.isMute()) {
			return EChat.of(EEMessages.WHOIS_MUTE_ENABLE.get());
		} else {
			return EChat.of(EEMessages.WHOIS_MUTE_DISABLE.get());
		}*/
	}
	
	private Text getVanish(final EUser player){
		if (player.isVanish()) {
			return EChat.of(EEMessages.WHOIS_VANISH_ENABLE.get());
		} else {
			return EChat.of(EEMessages.WHOIS_VANISH_DISABLE.get());
		}
	}
	
	private Text getFreeze(final EPlayer player){
		if (player.isFreeze()) {
			return EChat.of(EEMessages.WHOIS_FREEZE_ENABLE.get());
		} else {
			return EChat.of(EEMessages.WHOIS_FREEZE_DISABLE.get());
		}
	}
	
	private Text getAFK(final EPlayer player){
		if (player.isAfk()) {
			return EChat.of(EEMessages.WHOIS_AFK_ENABLE.get());
		} else {
			return EChat.of(EEMessages.WHOIS_AFK_DISABLE.get());
		}
	}
	
	private Text getFirstDatePlayed(final EUser player){
		return EChat.of(EEMessages.WHOIS_FIRST_DATE_PLAYED.get()
				.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(player.getFirstDatePlayed())));
	}
	
	private Text getLastDatePlayed(final EPlayer player) {
		return EChat.of(EEMessages.WHOIS_LAST_DATE_PLAYED_ONLINE.get()
				.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().formatDateDiff(player.getLastDatePlayed(), 3)));
	}
	
	private Text getLastDatePlayed(final EUser player) {
		return EChat.of(EEMessages.WHOIS_LAST_DATE_PLAYED_OFFLINE.get()
				.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().formatDateDiff(player.getLastDatePlayed(), 3)));
	}
	
	private Text getTotalTimePlayed(final EUser player){
		return EChat.of(EEMessages.WHOIS_TOTAL_TIME_PLAYED.get()
				.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().diff(player.getTotalTimePlayed())));
	}
	
	private Text getChatVisibility(final EPlayer player){
		ChatVisibility chat = player.getChatVisibility();
		if (chat.equals(ChatVisibilities.FULL)){
			return EEMessages.WHOIS_CHAT_FULL.getText();
		} else if (chat.equals(ChatVisibilities.SYSTEM)){
			return EEMessages.WHOIS_CHAT_SYSTEM.getText();
		} else {
			return EEMessages.WHOIS_CHAT_HIDDEN.getText();
		}
	}
	
	private Text getViewDistance(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_VIEW_DISTANCE.get()
				.replaceAll("<amount>", String.valueOf(player.getViewDistance())));
	}
	
	private Text getChatColor(final EPlayer player){
		if (player.isChatColorsEnabled()){
			return EEMessages.WHOIS_CHATCOLOR_ON.getText();
		} else {
			return EEMessages.WHOIS_CHATCOLOR_OFF.getText();
		}
	}
	
	private Text getLocale(final EPlayer player){
		return EChat.of(EEMessages.WHOIS_LANGUAGE.get()
				.replaceAll("<langue>", StringUtils.capitalize(player.getLocale().getDisplayLanguage())));
	}
	
	private Text getToggle(final EUser player){
		if (player.isToggle()) {
			return EChat.of(EEMessages.WHOIS_TOGGLE_ENABLE.get());
		} else {
			return EChat.of(EEMessages.WHOIS_TOGGLE_DISABLE.get());
		}
	}
}
