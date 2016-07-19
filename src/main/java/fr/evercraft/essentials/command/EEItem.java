package fr.evercraft.essentials.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.EReloadCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsItemTypes;

public class EEItem extends EReloadCommand<EverEssentials> {
	/*
	private Collection<ItemType> items;
	private Collection<ItemType> items_with_blacklist;
	private Collection<ItemType> blacklist; 
	*/
	public EEItem(final EverEssentials plugin) {
        super(plugin, "item");
        reload();
    }
	
	
	@Override
	public void reload() {
		
	}
	/*@Override
	public void reload() {
		this.items = this.plugin.getGame().getRegistry().getAllOf(ItemType.class);
		for(String name : this.plugin.getConfigs().getListString("blacklist")){
			for(ItemType type : this.items){
				if(type.getName().equals(addMinecraft(name))){
					this.blacklist.add(ItemTypes.);
				}
			}
		}
		this.blacklist
		
		
		this.items_with_blacklist = this.items.r
		
	}
	*/

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.ITEM.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.ITEM_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/item <objet> [quantité] [type]").onClick(TextActions.suggestCommand("/item"))
					.color(TextColors.RED).build();
	}
	
	@SuppressWarnings("null")
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = null;
		if(args.size() == 1){
			for(ItemType type : UtilsItemTypes.getItems()){
				suggests.add(type.getName().replaceAll("minecraft:", ""));
			}
		} else if(args.size() == 2){
			suggests.add("1");
			Optional<ItemType> optItem = UtilsItemTypes.getItemType(args.get(0));
			if(optItem.isPresent()){
				suggests.add(String.valueOf(optItem.get().getMaxStackQuantity()));
			}
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandPing((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// On connais le joueur
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(EEPermissions.PING_OTHERS.get())){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandPingOthers(source, optPlayer.get());
				// Le joueur est introuvable
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
	
	public boolean commandPing(final EPlayer player) {
		player.sendMessage(EEMessages.PREFIX.get() + EEMessages.PING_PLAYER.get()
				.replaceAll("<ping>", String.valueOf(player.getConnection().getLatency())));
		return true;
	}
	
	public boolean commandPingOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont différent
		if(!player.equals(staff)){
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.PING_OTHERS.get()
					.replaceAll("<player>", player.getName())
					.replaceAll("<ping>", String.valueOf(player.getConnection().getLatency()))));
			return true;
		// La source et le joueur sont identique
		} else {
			return execute(staff, new ArrayList<String>());
		}
	}
	/*
	private String addMinecraft(String name){
		if(!name.contains("minecraft:")){
			name = "minecraft:" + name;
		}
		return name;
	}
	*/
}