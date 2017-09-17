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
package fr.evercraft.everessentials;

import com.google.common.base.Preconditions;

import fr.evercraft.everapi.message.EMessageBuilder;
import fr.evercraft.everapi.message.EMessageFormat;
import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.message.replace.EReplacesPlayer;
import fr.evercraft.everapi.message.replace.EReplacesServer;
import fr.evercraft.everapi.plugin.file.EMessage;
import fr.evercraft.everapi.plugin.file.EnumMessage;

public class EEMessage extends EMessage<EverEssentials> {

	public EEMessage(EverEssentials plugin, String name) {
		super(plugin, EEMessages.values());
	}

	public enum EEMessages implements EnumMessage {
		PREFIX(							"[&4Ever&6&lEssentials&f] "),
		DESCRIPTION(					"Information sur EverEssentials"),
		
		AFK_DESCRIPTION(				"Permet de vous signaler AFK"),
		AFK_KICK(						"&cPour cause d'inactivité"),
		
		AFK_ON_DESCRIPTION(				"Rend le joueur invulnérable"),

		AFK_ON_PLAYER(					"&7Vous êtes désormais AFK."),
		AFK_ON_PLAYER_ERROR(			"&cVous êtes déjà AFK."),
		AFK_ON_PLAYER_CANCEL(			"&cImpossible de vous mettre AFK."),
		AFK_ON_ALL(						"&6" + EReplacesPlayer.DISPLAYNAME.getName() + " &7est désormais AFK.", "The message may be empty"),
		AFK_ON_OTHERS_PLAYER(			"&7Vous êtes désormais AFK à cause de &6{staff}&7."),
		AFK_ON_OTHERS_STAFF(			"&6{player} &7est désormais AFK à cause de &6{staff}&7."),
		AFK_ON_OTHERS_ERROR(			"&6{player} &cest déjà signalé AFK."),
		AFK_ON_OTHERS_CANCEL(			"&cImpossible de rendre &6{player} &cinvulnérable."),

		AFK_OFF_DESCRIPTION(			"Rend le joueur vulnérable"),

		AFK_OFF_PLAYER(					"&7Vous n'êtes plus AFK."),
		AFK_OFF_PLAYER_ERROR(			"&cVous n'êtes pas AFK."),
		AFK_OFF_PLAYER_CANCEL(			"&cImpossible de vous rendre vulnérable."),
		AFK_OFF_ALL(					"&6" + EReplacesPlayer.DISPLAYNAME.getName() + " &7n'est plus AFK.", "The message may be empty"),
		AFK_OFF_OTHERS_PLAYER(			"&7Vous n'êtes plus AFK à cause de &6{staff}&7."),
		AFK_OFF_OTHERS_STAFF(			"&6{player} &7n'est plus AFK à cause de &6{staff}&7."),
		AFK_OFF_OTHERS_ERROR(			"&6{player} &cn'est pas AFK."),
		AFK_OFF_OTHERS_CANCEL(			"&cImpossible de sortir &6{player} &cd'AFK."),
		
		AFK_STATUS_DESCRIPTION(			"Affiche si le joueur est AFK où pas"),
		AFK_STATUS_PLAYER_ON(			"&7Vous êtes AFK."),
		AFK_STATUS_PLAYER_OFF(			"&7Vous n'êtes pas AFK."),
		AFK_STATUS_OTHERS_ON(			"&6{player} &7est AFK."),
		AFK_STATUS_OTHERS_OFF(			"&6{player} &7n'est pas AFK."),
		
		BACK_DESCRIPTION(				"Retourne à la dernière position sauvegardé."),
		BACK_NAME(						"&6&lposition"),
		BACK_NAME_HOVER(				"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		BACK_TELEPORT(					"&7Vous avez été téléporté à votre dernière {back}&7."),
		BACK_INCONNU(					"&cVous n'avez aucune position sauvegardé."),
		BACK_DELAY(						"&7Votre téléportation commencera dans &6{delay}&7. Ne bougez pas."),
		BACK_ERROR_LOCATION(			"&cImpossible de trouver une position pour réaliser une téléportation."),

		BED_DESCRIPTION(				"Retourne à la dernière position ou vous avez dormi"),
		
		BROADCAST_DESCRIPTION(			"Envoie un message à tous les joueurs."),
		BROADCAST_MESSAGE(				"&7[&6&lBroadcast&7] {message}"),
		
		BOOK_DESCRIPTION(				"Permet de modifier un livre."),
		BOOK_WRITABLE(					""),
		BOOK_NO_WRITTEN(				""),
		
		BUTCHER_DESCRIPTION(			"Supprime les entités dans un monde ou dans un rayon."),
		BUTCHER_ALL_DESCRIPTION(		"Supprime toutes les entités dans un monde ou dans un rayon."),
		BUTCHER_ANIMAL_DESCRIPTION(		"Supprime toutes les animaux dans un monde ou dans un rayon."),
		BUTCHER_MONSTER_DESCRIPTION(	"Supprime toutes les monstres dans un monde ou dans un rayon."),
		BUTCHER_TYPE_DESCRIPTION(	  	"Supprime toutes les entité d'un type dans un monde ou dans un rayon."),
		
		
		BUTCHER_NOENTITY(				"&cIl y a aucune entité à supprimer."),
		BUTCHER_ENTITY_COLOR(			"&6"),
		BUTCHER_ANIMAL(					"&7Suppression de &6{count} animaux &7dans ce monde."),
		BUTCHER_ANIMAL_RADIUS(			"&7Suppression de &6{count} animaux &7dans un rayon de &6{radius} bloc(s)&7."),
		BUTCHER_MONSTER(				"&7Suppression de &6{count} monstre(s) &7dans ce monde."),
		BUTCHER_MONSTER_RADIUS(			"&7Suppression de &6{count} monstre(s) &7dans un rayon de &6{radius} bloc(s)&7."),
		BUTCHER_ALL(					"&7Suppression de &6{count} entité(s) &7dans ce monde."),
		BUTCHER_ALL_RADIUS(				"&7Suppression de &6{count} entité(s) &7dans un rayon de &6{radius} bloc(s)&7."),
		BUTCHER_TYPE(					"&7Suppression de &6{count} {entity}&6(s)&7 dans ce monde."),
		BUTCHER_TYPE_RADIUS(			"&7Suppression de &6{count} &6{entity}&6(s)&7 dans un rayon de &6{radius} bloc(s)&7."),
		
		CLEAREFFECT_DESCRIPTION(		"Supprime tous les effets de potions d'un joueur."),
		CLEAREFFECT_PLAYER(				"&7Tous vos effets de potions ont été supprimés."),
		CLEAREFFECT_NOEFFECT(			"&cErreur : Aucun effet de potion à supprimer."),
		CLEAREFFECT_OTHERS_PLAYER(		"&7Tous les effets de potions ont été supprimés par &6{staff}&7."),
		CLEAREFFECT_OTHERS_STAFF(		"&7Tous les effets de potions de &6{player} &7ont été supprimés."),
		
		CLEARINVENTORY_DESCRIPTION(		"Supprime tous les objets de l'inventaire d'un joueur."),
		CLEARINVENTORY_PLAYER(			"&7Vous venez de supprimer &6{amount} objet(s) &7de votre inventaire."),
		CLEARINVENTORY_NOITEM(			"&cErreur : Vous n'avez aucun objet dans l'inventaire."),
		CLEARINVENTORY_OTHERS_PLAYER(	"&6{staff} &7vient de supprimer &6{amount} objet(s) &7de votre inventaire."),
		CLEARINVENTORY_OTHERS_STAFF(	"&7Vous venez de supprimer &6{amount} objet(s) &7de l'inventaire de &6{player}&7."),
		CLEARINVENTORY_OTHERS_NOITEM(	"&cErreur : &6{player} &cn'a aucun objet dans l'inventaire."),
		
		COLOR_DESCRIPTION(				"Affiche les différentes couleurs dans Minecraft."),
		COLOR_LIST_TITLE(				"&l&4Liste des couleurs"), 
		COLOR_LIST_MESSAGE(				"{color}█ &0: {id}-{name}"), 
		
		EFFECT_DESCRIPTION(				"Ajoute un effet de potion sur un joueur."),
		EFFECT_ERROR_NAME(				"&cErreur : Nom de l'effet invalide."),
		EFFECT_ERROR_DURATION(			"&cErreur : La durée de l'effet doit être compris entre {min} et {max}."),
		EFFECT_ERROR_AMPLIFIER(			"&cErreur : L'amplification de l'effet doit être compris entre {min} et {max}."),
		
		ENCHANT_DESCRIPTION(			"Enchante l'objet dans votre main."),
		ENCHANT_NOT_FOUND(				"&cErreur : Cet enchantement n'existe pas."),
		ENCHANT_LEVEL_TOO_HIGHT(		"&cErreur : Le niveau de cet enchantement est trop élevé."),
		ENCHANT_LEVEL_TOO_LOW(			"&cErreur : Le niveau de cet enchantement est trop faible."),
		ENCHANT_INCOMPATIBLE(			"&cErreur : L'enchantement &6{enchantment} &cest incompatible avec l'objet &b[{item}&b]&c."),
		ENCHANT_ITEM_COLOR(				"&b"),
		ENCHANT_SUCCESSFULL(			"&7L'enchantement &6{enchantment} &7a été appliqué sur l'objet &b[{item}&b]&7."),
			
		ENDERCHEST_DESCRIPTION(			"Ouvre le coffre de l'End d'un joueur"),
		ENDERCHEST_TITLE(				"&8Coffre de l'Ender de {player}"),
		
		EXP_DESCRIPTION(				"Modifie l'expérience d'un joueur."),
		EXP_GIVE_LEVEL(					"&7Vous vous êtes ajouté &6{level} &7niveau(x)."),
		EXP_GIVE_EXP(					"&7Vous vous êtes ajouté &6{experience} &7point(s) d'expérience."),
		EXP_SET_LEVEL(					"&7Vous avez défini votre niveau à &6{level}&7."),
		EXP_SET_EXP(					"&7Vous avez défini votre expérience à &6{experience}&7."),
		EXP_OTHERS_PLAYER_GIVE_LEVEL(	"&7Vous avez reçu &6{level} &7niveau(x) par &6{staff}&7."),
		EXP_OTHERS_STAFF_GIVE_LEVEL(	"&7Vous avez ajouté &6{level} &7niveau(x) à &6{player}&7."),
		EXP_OTHERS_PLAYER_GIVE_EXP(		"&7Vous avez reçu &6{experience} &7point(s) d'expérience par &6{staff}&7."),
		EXP_OTHERS_STAFF_GIVE_EXP(		"&7Vous avez ajouté &6{experience} &7point(s) d'expérience à &6{player}&7."),
		EXP_OTHERS_PLAYER_SET_LEVEL(	"&7Votre niveau a été modifié à &6{level} &7par &6{staff}&7."),
		EXP_OTHERS_STAFF_SET_LEVEL(		"&7Vous avez modifié le niveau de &6{player} &7à &6{level}&7."),
		EXP_OTHERS_PLAYER_SET_EXP(		"&7Votre expérience a été modifié à &6{experience} &7par &6{staff}&7."),
		EXP_OTHERS_STAFF_SET_EXP(		"&7Vous avez modifié l'expérience de &6{player} &7à &6{experience}&7."),
		
		EXT_DESCRIPTION(				"Retire le feu sur un joueur."),
		EXT_PLAYER(						"&7Vous n'êtes plus en feu."),
		EXT_PLAYER_ERROR(				"&7Vous n'êtes pas en feu."),
		EXT_OTHERS_PLAYER(				"&7Vous n'êtes plus en feu grâce à &6{staff}&7."),
		EXT_OTHERS_STAFF(				"&7Vous avez retiré le feu sur &6{player}&7."),
		EXT_OTHERS_ERROR(				"&6{player} &7n'est pas en feu."),
		EXT_ALL_STAFF(					"&7Vous avez retiré le feu sur tous les joueurs."),
		
		FEED_DESCRIPTION(				"Satisfait la faim d'un joueur."),
		FEED_PLAYER(					"&7Vous vous êtes rassasié."),
		FEED_OTHERS_STAFF(				"&7Vous avez rassasié &6{player}."),
		FEED_OTHERS_PLAYER(				"&7Vous avez été rassasié par &6{staff}&7."),
		FEED_ALL_STAFF(					"&7Vous avez rassasié tous les joueurs."),
		
		FORMAT_DESCRIPTION(				"Affiche les différents formats dans Minecraft."),
		FORMAT_LIST_TITLE(				"&l&4Liste des formats"), 
		FORMAT_LIST_MESSAGE(			"{format}Stone &0: {id}-{name}"),
		FORMAT_OBFUSCATED(				"Obfusqué"),
		FORMAT_BOLD(					"Gras"),
		FORMAT_STRIKETHROUGH(			"Barré"),
		FORMAT_UNDERLINE(				"Souligné"),
		FORMAT_ITALIC(					"Italique"),
		FORMAT_RESET(					"Réinitialisation"),
		
		FREEZE_DESCRIPTION(				"Gère la paralysie sur un joueur"),
		
		FREEZE_ON_DESCRIPTION(			"Paralyse un joueur"),
		FREEZE_ON_PLAYER( 				"&7Vous êtes désormais paralysé."),
		FREEZE_ON_PLAYER_ERROR(			"&cErreur : Vous êtes déjà paralysé."),
		FREEZE_ON_PLAYER_CANCEL(		"&7Vous venez d'être paralysé par &6{staff}&7."),
		FREEZE_ON_OTHERS_PLAYER(		"&7Vous venez de paralyser &6{player}&7."),
		FREEZE_ON_OTHERS_STAFF(			"&cErreur : {player} &7est déjà paralysé."),
		FREEZE_ON_OTHERS_ERROR(			"&cErreur : &6{player} &cest déjà paralysé."),
		FREEZE_ON_OTHERS_CANCEL(		"&cImpossible de paralyser &6{player}&c."),
		
		FREEZE_OFF_DESCRIPTION(			"Libère un joueur paralysé"),
		FREEZE_OFF_PLAYER(				"&7Vous êtes désormais libre."),
		FREEZE_OFF_PLAYER_ERROR(		"&cErreur : Vous êtes déjà libre."),
		FREEZE_OFF_PLAYER_CANCEL(		"&7Vous êtes libre grâce à &6{staff}&7."),
		FREEZE_OFF_OTHERS_PLAYER(		"&7Vous venez de libérer &6{player}&7."),
		FREEZE_OFF_OTHERS_STAFF(		"&cErreur : {player} &7est déjà libre."),
		FREEZE_OFF_OTHERS_ERROR(		"&cErreur : &6{player} &cest déjà paralysé."),
		FREEZE_OFF_OTHERS_CANCEL(		"&cImpossible de paralyser &6{player}&c."),
		
		FREEZE_STATUS_DESCRIPTION(		"Affiche si le joueur est paralysé où libre"),
		FREEZE_STATUS_PLAYER_ON(		"&7Vous êtes paralysé."),
		FREEZE_STATUS_PLAYER_OFF(		"&7Vous êtes libre."),
		FREEZE_STATUS_OTHERS_ON(		"&6{player} &7est paralysé."),
		FREEZE_STATUS_OTHERS_OFF(		"&6{player} &7est libre."),
		
		FREEZE_NO_COMMAND(				"&7Vous ne pouvez pas exécuter de commande en étant paralysé."),

		FLY_DESCRIPTION(				"Permet de vous envoler"),
		FLY_ON_DESCRIPTION(				"Permet d'accorder le droit de s'envoler à un joueur"),

		FLY_ON_PLAYER(					"&7Vous pouvez désormais vous envoler."),
		FLY_ON_PLAYER_ERROR(			"&cVous possèdez déjà le droit de vous envoler."),
		FLY_ON_PLAYER_CANCEL(			"&cImpossible de vous accorder le droit de vous envoler."),
		FLY_ON_OTHERS_PLAYER(			"&7Vous pouvez désormais vous envoler grâce à &6{staff}&7."),
		FLY_ON_OTHERS_STAFF(			"&7Vous venez d'accorder le droit de s'envoler à &6{player}&7."),
		FLY_ON_OTHERS_ERROR(			"&6{player} &7possède déjà le droit de s'envoler."),
		FLY_ON_OTHERS_CANCEL(			"&cImpossible d'accorder le droit de s'envoler à &6{player}&c."),

		FLY_OFF_DESCRIPTION(			"Permet de retirer le droit de s'envoler à un joueur"),

		FLY_OFF_PLAYER(					"&7Vous ne pouvez plus vous envoler."),
		FLY_OFF_PLAYER_ERROR(			"&7Vous ne pouvez pas vous envoler."),
		FLY_OFF_PLAYER_CREATIVE(		"&7Vous ne pouvez pas vous enlever le droit de vous envoler quand vous êtes en mode créative."),
		FLY_OFF_PLAYER_CANCEL(			"&cImpossible de vous retirer le droit de vous envoler."),
		FLY_OFF_OTHERS_PLAYER(			"&7Vous ne pouvez plus vous envoler à cause de &6{staff}&7."),
		FLY_OFF_OTHERS_STAFF(			"&7Vous venez de retirer le droit de s'envoler à &6{player}&7."),
		FLY_OFF_OTHERS_ERROR(			"&6{player} &7ne possède pas le droit de s'envoler."),
		FLY_OFF_OTHERS_CREATIVE(		"&cVous ne pouvez pas enlever le droit de s'envoler à &6{player} &ccar il est en mode créative."),
		FLY_OFF_OTHERS_CANCEL(			"&cImpossible de retirer le droit de s'envoler à &6{player}&c."),
		
		FLY_STATUS_DESCRIPTION(			"Permet de savoir si un joueur peut s'envoler"),
		FLY_STATUS_PLAYER_ON(			"&7Vous pouvez vous envoler."),
		FLY_STATUS_PLAYER_OFF(			"&7Vous ne pouvez pas vous envoler."),
		FLY_STATUS_OTHERS_ON(			"&6{player} &7peut s'envoler."),
		FLY_STATUS_OTHERS_OFF(			"&6{player} &7ne peut pas s'envoler."),
		
		GAMEMODE_DESCRIPTION(			"Change le mode de jeu d'un joueur"),
		GAMEMODE_PLAYER_CHANGE(			"&7Vous êtes désormais en mode de jeu &6{gamemode}&7."),
		GAMEMODE_PLAYER_EQUAL(			"&7Vous êtes déjà en mode de jeu &6{gamemode}&7."),
		GAMEMODE_OTHERS_STAFF_CHANGE(	"&7Mode de jeu &6{gamemode} &7pour &6{player}&7."),
		GAMEMODE_OTHERS_PLAYER_CHANGE(	"&7Votre mode de jeu a été changé en &6{gamemode} &7par &6{staff}&7."),
		GAMEMODE_OTHERS_EQUAL(			"&6{player} &7possède déjà le mode de jeu &6{gamemode}&7."),
		GAMEMODE_ERROR_NAME(			"&cMode de jeu inconnu."),
		
		GAMERULE_DESCRIPTION(			"Gère les différentes règles d'un monde"),
		GAMERULE_ADD_DESCRIPTION(		"Ajoute une règle personnalisée sur un monde"),
		GAMERULE_ADD_GAMERULE(			"&7Vous venez d'ajouter la gamerule &6'{gamerule}' &7avec la valeur &6'{valeur}'&7."),
		GAMERULE_ADD_ERROR(				"&cErreur : Cette gamerule existe déjà."),
		GAMERULE_REMOVE_DESCRIPTION(	"Supprime une règle personnalisée sur un monde"),
		GAMERULE_SET_DESCRIPTION(		"Modifie une règle sur un monde"),
		
		GAMERULE_LIST_DESCRIPTION(		"Affiche la liste des règles du serveur"),
		GAMERULE_LIST_TITLE(			"&aListe des règles du monde &6{world}"),
		GAMERULE_LIST_LINE(				"    &6&l➤  {gamerule} &7: {statut}"),
		
		GENERATE_DESCRIPTION(			"Initialise tous les chunks d'un monde"),
		GENERATE_WARNING(				"&cAttention : &7Générer tous les chunks d'un monde peut prendre plusieurs heures et causer des latences."
									  + "[RT] Le nombre total de chunks a générer est de &6{chunk}&7."
									  + "[RT] Souhaitez-vous vraiment générer tous les chuncks du monde &6{world} ? {confirmation}"),
		GENERATE_WARNING_VALID(			"&a[Confirmer]"),
		GENERATE_WARNING_VALID_HOVER(	"&cCliquez ici pour lancer la génération des chunks dans le monde &6{world}&7."),
		GENERATE_LAUNCH(				"&7Génération du monde &6{world} &7lancée avec succès."),
				
		GETPOS_DESCRIPTION(				"Affiche les coordonnées d'un joueur"),
		GETPOS_MESSAGE(					"&7Voici votre &6{position}&7."),
		GETPOS_MESSAGE_OTHERS(			"&7Voici la {position} &7de &6{player}&7."),
		GETPOS_POTISITON_NAME(			"&6&lposition"),
		GETPOS_POSITION_HOVER(			"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		
		GOD_DESCRIPTION(				"Gère l'invulnérabilité d'un joueur"),
		
		GOD_ON_DESCRIPTION(				"Rend le joueur invulnérable"),
		GOD_ON_PLAYER(					"&7Vous êtes désormais invulnérable."),
		GOD_ON_PLAYER_ERROR(			"&cErreur : Vous êtes déjà invulnérable."),
		GOD_ON_PLAYER_CANCEL(			"&cImpossible de vous rendre invulnérable."),
		GOD_ON_OTHERS_PLAYER(			"&7Vous êtes désormais invulnérable grâce à &6{staff}&7."),
		GOD_ON_OTHERS_STAFF(			"&7Vous venez de rendre invulnérable &6{player}&7."),
		GOD_ON_OTHERS_ERROR(			"&cErreur : &6{player} &cest déjà invulnérable."),
		GOD_ON_OTHERS_CANCEL(			"&cImpossible de rendre &6{player} &cinvulnérable."),
		
		GOD_OFF_DESCRIPTION(			"Rend le joueur vulnérable"),
		GOD_OFF_PLAYER(					"&7Vous êtes désormais vulnérable."),
		GOD_OFF_PLAYER_ERROR(			"&cErreur : Vous êtes déjà vulnérable."),
		GOD_OFF_PLAYER_CANCEL(			"&cImpossible de vous rendre vulnérable."),
		GOD_OFF_OTHERS_PLAYER(			"&7Vous n'êtes plus invulnérable à cause de &6{staff}&7."),
		GOD_OFF_OTHERS_STAFF(			"&7Vous venez de rendre vulnérable &6{player}&7."),
		GOD_OFF_OTHERS_ERROR(			"&cErreur : &6{player} &cest déjà vulnérable."),
		GOD_OFF_OTHERS_CANCEL(			"&cImpossible de rendre &6{player} &cvulnérable."),
		
		GOD_STATUS_DESCRIPTION(			"Affiche si le joueur est vulnérable où pas"),
		GOD_STATUS_PLAYER_ON(			"&7Vous êtes invulnérable."),
		GOD_STATUS_PLAYER_OFF(			"&7Vous êtes vulnérable."),
		GOD_STATUS_OTHERS_ON(			"&6{player} &7est invulnérable."),
		GOD_STATUS_OTHERS_OFF(			"&6{player} &7est vulnérable."),
		
		GOD_TELEPORT(					"&7Vous avez été téléporté car vous étiez en train de tomber dans le vide."),
		
		HAT_DESCRIPTION(				"Place l'objet dans votre main sur votre tête"),
		HAT_ITEM_COLOR(					"&6"),
		HAT_NO_EMPTY(					"&7Vous ne pouvez pas mettre un objet sur votre tête quand vous avez un {item}&7."),
		HAT_IS_HAT(						"&7Votre nouveau chapeau : &6{item}&7."),
		HAT_REMOVE(						"&7Vous avez enlevé l'objet sur votre chapeau."),
		HAT_REMOVE_EMPTY(				"&cVous n'avez actuellement aucun chapeau."),
		
		HEAL_DESCRIPTION(				"Soigne un joueur."),
		HEAL_PLAYER(					"&7Vous vous êtes soigné."),
		HEAL_OTHERS_PLAYER(				"&7Vous avez été soigné par &6{staff}&7."),
		HEAL_OTHERS_STAFF(				"&7Vous avez soigné &6{player}&7."),
		HEAL_OTHERS_DEAD_STAFF(			"&6{player}&7 est déjà mort."),
		HEAL_ALL_STAFF(					"&7Vous avez soigné tous les joueurs."),
		
		HELP_DESCRIPTION(				"Affiche les informations sur les commandes disponibles du serveur."),
		HELP_TITLE(						"&aListe des commandes"),
		HELP_SEARCH_TITLE(				"&aListe des commandes contenant '{command}'"),
		
		HOME_NAME(						"&6&l{name}"),
		HOME_NAME_HOVER(				"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		
		HOME_DESCRIPTION(				"Téléporte le joueur à une résidence."),
		HOME_LIST_TITLE(				"&aListe des résidences"),
		HOME_LIST_LINE(					"    &6&l➤  {home} &7: {teleport} {delete}"),
		HOME_LIST_LINE_ERROR_WORLD(		"    &6&l➤  {home} &7: {delete}"),
		HOME_LIST_TELEPORT(				"&a[Téléporter]"),
		HOME_LIST_TELEPORT_HOVER(		"&cCliquez ici pour vous téléporter à la résidence &6{home}&c."),
		HOME_LIST_DELETE(				"&c[Supprimer]"),
		HOME_LIST_DELETE_HOVER(			"&cCliquez ici pour supprimer la résidence &6{home}&c."),
		HOME_EMPTY(						"&cVous n'avez aucune résidence."),
		HOME_INCONNU(					"&cVous n'avez pas de résidence qui s'appelle &6{home}&c."),
		HOME_TELEPORT(					"&7Vous avez été téléporté à la résidence &6{home}&7."),

		DELHOME_DESCRIPTION(			"Supprime une résidence"),
		DELHOME_CONFIRMATION(			"&7Souhaitez-vous vraiment supprimer la résidence &6{home} &7: {confirmation}"),
		DELHOME_CONFIRMATION_VALID(		"&a[Confirmer]"),
		DELHOME_CONFIRMATION_VALID_HOVER("&cCliquez ici pour supprimer la résidence &6{home}&c."),
		DELHOME_DELETE(					"&7Vous avez supprimé la résidence &6{home}&7."),
		DELHOME_INCONNU(				"&cVous n'avez pas de résidence qui s'appelle &6{home}&c."),
		
		HOMEOTHERS_DESCRIPTION(			"Gère les résidences d'un joueur"),
		HOMEOTHERS_LIST_TITLE(			"&aListe des résidences de {player}"),
		HOMEOTHERS_LIST_LINE(			"    &6&l➤  {home} &7: {teleport} {delete}"),
		HOMEOTHERS_LIST_TELEPORT(		"&a[Téléporter]"),
		HOMEOTHERS_LIST_TELEPORT_HOVER(	"&cCliquez ici pour vous téléporter à la résidence &6{home} &cde &6{player}&c."),
		HOMEOTHERS_LIST_DELETE(			"&c[Supprimer]"),
		HOMEOTHERS_LIST_DELETE_HOVER(	"&cCliquez ici pour supprimer la résidence &6{home} &cde &6{player}&c."),
		HOMEOTHERS_EMPTY(				"&6{player} &cn'a aucune résidence."),
		HOMEOTHERS_INCONNU(				"&6{player} &cn'a pas de résidence qui s'appelle &6{home}&c."),
		HOMEOTHERS_TELEPORT(			"&7Vous avez été téléporté à la résidence &6{home} &7de &6{player}&7."),
		HOMEOTHERS_TELEPORT_ERROR(		"&cImpossible de vous téléporter à la résidence &6{home} &cde &6{player}&c."),
		HOMEOTHERS_DELETE_CONFIRMATION(	"&7Souhaitez-vous vraiment supprimer la résidence &6{home} &7de &6{player} &7: {confirmation}"),
		HOMEOTHERS_DELETEE_CONFIRMATION_VALID(			"&a[Confirmer]"),
		HOMEOTHERS_DELETE_CONFIRMATION_VALID_HOVER(		"&cCliquez ici pour supprimer la résidence &6{home} &cde &6{player}&c."),
		HOMEOTHERS_DELETE(								"&7Vous avez supprimé la résidence &6{home} &7de &6{player}&7."),
		
		SETHOME_DESCRIPTION(			"Défini une résidence"),
		SETHOME_SET(					"&7Vous avez défini votre résidence."),
		SETHOME_SET_CANCEL(				"&cImpossible de définir votre résidence."),
		SETHOME_MOVE(					"&7Vous avez redéfini votre résidence."),
		SETHOME_MOVE_CANCEL(			"&cImpossible de redéfinir votre résidence."),
		SETHOME_MULTIPLE_SET(			"&7Vous avez défini la résidence &6{home}&7."),
		SETHOME_MULTIPLE_SET_CANCEL(	"&cImpossible de définir la résidence &6{home}&7."),
		SETHOME_MULTIPLE_MOVE(			"&7Vous avez redéfini la résidence &6{home}&7."),
		SETHOME_MULTIPLE_MOVE_CANCEL(	"&cImpossible de redéfinir la résidence &6{home}&7."),
		SETHOME_MULTIPLE_ERROR_MAX(		"&cVous ne pouvez pas créer plus de {nombre} résidence(s)."),
		SETHOME_MULTIPLE_NO_PERMISSION(	"&cVous n'avez pas la permission d'avoir plusieurs résidences."),
		
		// Ignore
		IGNORE_DESCRIPTION(				"Gère la whitelist"),
		
		IGNORE_ADD_DESCRIPTION(			"Permet d'ignorer un joueur"),
		IGNORE_ADD_PLAYER(				"&7Vous ignorez désormais &6{player}&7."),
		IGNORE_ADD_ERROR(				"&cErreur : Vous ignorez déjà &6{player}&c."),
		IGNORE_ADD_CANCEL(				"&cImpossible d'ignoré &6{player} &cpour le moment."),
		IGNORE_ADD_BYPASS(				"&cImpossible d'ignoré &6{player} &ccar il fait partie des membres du staff."),
		
		IGNORE_REMOVE_DESCRIPTION(		"Permet de plus ignorer un joueur"),
		IGNORE_REMOVE_PLAYER(			"&7Vous n'ignorez plus &6{player}&7."),
		IGNORE_REMOVE_ERROR(			"&cErreur : Vous n'ignorez pas &6{player}&c."),
		IGNORE_REMOVE_CANCEL(			"&cImpossible d'arrêté d'ignoré &6{player} &cpour le moment."),
		
		IGNORE_LIST_DESCRIPTION(		"Affiche la liste des joueurs ignorer"),
		IGNORE_LIST_PLAYER_TITLE(		"&aListe des joueurs ignorés"),
		IGNORE_LIST_OTHERS_TITLE(		"&aListe des joueurs ignorés par &6{player}"),
		IGNORE_LIST_LINE_DELETE(		"    &6&l➤  {player} &7: {delete}"),
		IGNORE_LIST_LINE(				"    &6&l➤  {player}"),
		IGNORE_LIST_REMOVE(				"&a[Supprimer]"),
		IGNORE_LIST_REMOVE_HOVER(		"&cCliquez ici pour retirer &6{player} &cde la liste des joueurs ignorés."),
		IGNORE_LIST_EMPTY(				"&7Aucun joueur."),
		
		// Info
		INFO_DESCRIPTION(				"Indique le type d'un objet"),
		INFO_PLAYER(					"&7Le type de l'objet {item} &7est &6{type}&7."),
		INFO_ITEM_COLOR(				"&6"),
		
		ITEM_DESCRIPTION(				"Donne un item spécifique"),
		ITEM_ERROR_ITEM_NOT_FOUND(		"&cErreur : L'objet {item} n'existe pas."),
		ITEM_ERROR_ITEM_BLACKLIST(		"&cErreur : Vous ne pouvez pas vous donner cet objet car il se trouve dans la liste noire."),
		ITEM_ERROR_QUANTITY(			"&cErreur : La quantité doit être compris entre &60 &7et &6{amount} &7objet(s)."),
		ITEM_ERROR_DATA(				"&cErreur : Le type de l'objet est incorrect."),
		ITEM_GIVE(						"&7Vous avez reçu {item}"),
		ITEM_GIVE_COLOR(				"&6"),
		
		ITEM_LORE_DESCRIPTION(			"Modifie la description d'un objet"),
		ITEM_LORE_ADD_DESCRIPTION(		"Ajoute une ligne à la description d'un objet"),
		ITEM_LORE_ADD_LORE(				"&7Description ajoutée à l'objet &b[{item}&b]&7."),
		ITEM_LORE_ADD_COLOR(			"&b"),
		ITEM_LORE_CLEAR_DESCRIPTION(	"Supprime la description d'un objet"),
		ITEM_LORE_CLEAR_NAME(			"&7La description de votre objet &b[{item}&b] &7a été supprimé."),
		ITEM_LORE_CLEAR_ERROR(			"&cErreur : Votre objet &b[{item}&b] &cne possède pas de description."),
		ITEM_LORE_CLEAR_COLOR(			"&b"),
		ITEM_LORE_SET_DESCRIPTION(		"Défini une ligne à la description d'un objet"),
		ITEM_LORE_SET_LORE(				"&7La ligne &6{line} &7a été ajoutée de l'objet &b[{item}&b]&7."),
		ITEM_LORE_SET_COLOR(			"&b"),
		ITEM_LORE_REMOVE_DESCRIPTION(	"Supprime une ligne à la description d'un objet"),
		ITEM_LORE_REMOVE_LORE(			"&7La ligne &6{line} &7a été supprimée de l'objet &b[{item}&b]&7."),
		ITEM_LORE_REMOVE_ERROR(			"&cErreur : La ligne doit être comprise entre &61 &cet &6{max}&c."),
		ITEM_LORE_REMOVE_COLOR(			"&b"),
			
		ITEM_NAME_DESCRIPTION(			"Modifie le nom d'un objet"),
		ITEM_NAME_SET_DESCRIPTION(		"Défini le nom d'un objet"),
		ITEM_NAME_SET_NAME(				"&7Vous avez renommé &b[{item-before}&b] &7en &b[{item-after}&b]&7."),
		ITEM_NAME_SET_COLOR(			"&b"),
		ITEM_NAME_SET_ERROR(			"&cErreur : le nom d'un objet ne doit pas dépasser {amount} caractères."),
		ITEM_NAME_CLEAR_DESCRIPTION(	"Supprime le nom d'un objet"),
		ITEM_NAME_CLEAR_NAME(			"&7Votre nom de l'objet &b[{item}&b] &7a été supprimé."),
		ITEM_NAME_CLEAR_ERROR(			"&cErreur : Votre objet &b[{item}&b] &cne possède pas de nom."),
		ITEM_NAME_CLEAR_COLOR(			"&b"),
		
		JUMP_DESCRIPTION(				"Vous téléporte à l'endroit de votre choix"),
		JUMP_TELEPORT(					"&7Vous avez été téléporté à l'endroit de votre choix."),
		JUMP_TELEPORT_ERROR(			"&7Impossible de trouver une position pour vous téléporter."),
		
		KICK_DESCRIPTION(				"Expulse un joueur du serveur"),
		KICK_DEFAULT_REASON(			"&7Veuillez respecter les règles du serveur."),
		KICK_MESSAGE(					"&c&lExpulsion du serveur[RT][RT]&cRaison : &7{reason}[RT]"),
		KICK_BYPASS(					"&cErreur : {player} ne peut pas être expulsé."),
		
		KICKALL_DESCRIPTION(			"Expulse tous les joueurs du serveur"),
		KICKALL_MESSAGE(				"&c&lExpulsion du serveur[RT][RT]&cRaison : &7{reason}[RT]"),
		KICKALL_ERROR(					"&cErreur : Il n'y a aucun joueur à expulser du serveur."),
		
		KILL_DESCRIPTION(				"Tue un joueur"),
		KILL_PLAYER(					"&7Vous avez été tué par &6{staff}&7."),
		KILL_PLAYER_DEATH_MESSAGE(		"&f<" + EReplacesPlayer.DISPLAYNAME.getName() + "&f> s'est tué par {staff}&f."),
		KILL_PLAYER_CANCEL(				"&cImpossible de tuer &6{player}&c."),
		KILL_STAFF(						"&7Vous avez tué &6{player}&7."),
		KILL_EQUALS(					"&7Vous vous êtes suicidé."),
		
		KILL_EQUALS_DEATH_MESSAGE(		"&f<" + EReplacesPlayer.DISPLAYNAME.getName() + "> s'est suicidé&f."),
		KILL_EQUALS_CANCEL(				"&cImpossible de vous suicider."),
		
		LAG_DESCRIPTION(				"Connaître l'état du serveur"),
		LAG_TITLE(						"&aInformations sur le serveur"),
		LAG_TIME(						"    &6&l➤  &6Durée de fonctionnement : &c{time}"),
		LAG_TPS(						"    &6&l➤  &6TPS actuel : &c{tps}"),
		LAG_HISTORY_TPS(				"    &6&l➤  &6Historique TPS : {tps}"),
		LAG_HISTORY_TPS_HOVER(			"&6Minute : &c{num}[RT]&6TPS : &c{tps}"),
		LAG_MEMORY(						"    &6&l➤  &6RAM : &c{usage}&6/&c{total} &6Mo"),
		LAG_WORLDS(						"    &6&l➤  &6Liste des mondes : [RT]{worlds}"),
		LAG_WORLDS_SEPARATOR(			"[RT]"),
		LAG_WORLDS_WORLD(				"        &6&l●  &a{world}"),
		LAG_WORLDS_WORLD_HOVER(			"&6Chunks : &c{chunks}[RT]&6Entités : &c{entities}[RT]&6Tiles : &c{tiles}"),
		
		LIST_DESCRIPTION(				"Affiche la liste des joueurs connecté"),
		LIST_TITLE(						"&aListe des joueurs connectés : &6" + EReplacesServer.ONLINE_PLAYERS.getName() + " &a/ &6" + EReplacesServer.MAX_PLAYERS.getName() + ""),
		LIST_TITLE_VANISH(				"&aListe des joueurs connectés : &6" + EReplacesServer.ONLINE_PLAYERS.getName() + " &a(+&6{vanish}&a) / &6" + EReplacesServer.MAX_PLAYERS.getName()),
		LIST_GROUP(						"&6{group}&f : {players}"),
		LIST_SEPARATOR(					", "),
		LIST_PLAYER(					"{afk}&r{vanish}&r" + EReplacesPlayer.DISPLAYNAME.getName()),
		LIST_TAG_AFK(					"&7[AFK] "),
		LIST_TAG_VANISH(				"&7[VANISH] "),
		LIST_EMPTY(						"&7Aucun joueur"),
		
		MAIL_DESCRIPTION(				"Gestion de vos messages"),
		
		MAIL_READ_DESCRIPTION(			"Lis les messages"),
		MAIL_READ_TITLE(				"&aLa liste des messages"),
		MAIL_READ_LINE_READ(			"  &a&l➤&7 De &6{player}&7 le &6{date} &7à &6{time} : {read} {delete}"),
		MAIL_READ_LINE_NO_READ(			"  &6&l➤&7 De &6{player}&7 le &6{date} &7à &6{time} : {read} {delete}"),
		MAIL_READ_EMPTY(				"&7Vous n'avez aucun message"),
		MAIL_READ_CANCEL(				"&cImpossible de lire le {mail}."),
		MAIL_READ_MAIL(					"&6message"),
		MAIL_READ_MAIL_HOVER(			"&7De &6{player}[RT]&7Le &6{date}"),
		MAIL_READ_ERROR(				"&cVous n'avez pas de message qui correspond."),
		
		MAIL_DELETE_DESCRIPTION(		"Supprime le message séléctionné"),
		MAIL_DELETE_MESSAGE(			"&7Voulez-vous vraiment supprimer le {mail} de &6{player}&7 le &6{date} &7à &6{time} : {confirmation}."),
		MAIL_DELETE_VALID(	 			"&a[Confirmer]"),
		MAIL_DELETE_VALID_HOVER(		"&cCliquez ici pour supprimer le message."),
		MAIL_DELETE_CONFIRMATION(		"&7Le {mail} &7a bien été supprimé."),
		MAIL_DELETE_CANCEL(				"&7Le {mail} &7n'a pas pu être supprimé."),
		MAIL_DELETE_MAIL(				"&6message"),
		MAIL_DELETE_MAIL_HOVER(			"&7De &6{player}[RT]&7Le &6{date}"),
		MAIL_DELETE_ERROR(				"&cVous n'avez pas de message qui correspond."),
		
		MAIL_CLEAR_DESCRIPTION(			"Supprime tous vos messages"),
		MAIL_CLEAR_MESSAGE(				"&7Vous avez supprimé tous vos messages."),
		MAIL_CLEAR_CANCEL(				"&7Impossible de supprimé tous vos messages."),
		MAIL_CLEAR_ERROR(				"&cVous n'avez pas de message à supprimer."),
		
		MAIL_SEND_DESCRIPTION(			"Envoie un message à un ou plusieurs joueurs"),
		MAIL_SEND_MESSAGE(				"&7Votre message a été envoyé à &6{player}&7."),
		MAIL_SEND_CANCEL(				"&cImpossible d'envoyé le message à &6{player}&7."),
		MAIL_SEND_EQUALS(				"&7Votre message a été envoyé."),
		MAIL_SEND_ALL(					"&7Votre message a été envoyé à tous les joueurs."),
		MAIL_SEND_IGNORE_PLAYER(		"&cImpossible d'envoyer un mail à &6{player}&c car vous l'ignorez."),
		MAIL_SEND_IGNORE_RECEIVE(		"&6{player}&c ne recevera pas votre mail car il vous ignore."),
		
		MAIL_BUTTON_READ(				"&a[Lire]"),
		MAIL_BUTTON_READ_HOVER(			"&cCliquez ici pour lire le message."),
		MAIL_BUTTON_DELETE(				"&c[Supprimer]"),
		MAIL_BUTTON_DELETE_HOVER(		"&cCliquez ici pour supprimer le message."),
		
		MAIL_NEW_MESSAGE(				"&7Vous avez un nouveau message. {message}"),
		MAIL_BUTTON_NEW_MESSAGE(		"&a[Cliquez ici]"),
		MAIL_BUTTON_NEW_MESSAGE_HOVER(	"&7Cliquez ici pour afficher la liste des messages."),
		
		ME_DESCRIPTION(					"Envoie un texte d'action dans le tchat"),
		ME_PLAYER(						"&f* " + EReplacesPlayer.NAME.getName() + " &r{message}"),
		
		MOJANG_DESCRIPTION(				"Affiche les informations sur les serveurs de mojang"),
		MOJANG_TITLE(					"&aLes serveurs de Mojang"),
		MOJANG_LINE(					"    &6&l➤  &6{server} : {color}"),
		MOJANG_SERVER_ACCOUNT(			"Account"),
		MOJANG_SERVER_API(				"API"),
		MOJANG_SERVER_MOJANG(			"Mojang"),
		MOJANG_SERVER_AUTH(				"Auth"),
		MOJANG_SERVER_AUTHSERVER(		"AuthServer"),
		MOJANG_SERVER_MINECRAFT_NET(	"MinecraftNet"),
		MOJANG_SERVER_SESSION(			"Session"),
		MOJANG_SERVER_SESSIONSERVER(	"SessionServer"),
		MOJANG_SERVER_SKINS(			"Skins"),
		MOJANG_SERVER_TEXTURES(			"Textures"),
		MOJANG_STATUS_ONLINE(			"&aEn ligne"),
		MOJANG_STATUS_WARN(				"&6Problème de connexion"),
		MOJANG_STATUS_OFFLINE(			"&4Hors ligne"),
		
		MORE_DESCRIPTION(				"Donne la quantité maximum d'un objet"),
		MORE_PLAYER(					"&7Vous avez maintenant &6{quantity} &6{item}&7."),
		MORE_ITEM_COLOR(				"&6"),
		MORE_MAX_QUANTITY(				"&7Vous avez déjà la quantité maximum de cette objet."),
		
		MOTD_DESCRIPTION(				"Affiche le message du jour."),
		
		NAMES_DESCRIPTION(				"Affiche l'historique des noms d'un joueur"),
		NAMES_PLAYER_TITLE(				"&aVotre historique de nom"),
		NAMES_PLAYER_LINE_ORIGINAL(		"    &6&l➤  &6{name} &7: &cAchat du compte"),
		NAMES_PLAYER_LINE_OTHERS(		"    &6&l➤  &6{name} &7: &c{datetime}"),
		NAMES_PLAYER_EMPTY(				"&7Vous n'avez aucun historique de pseudo"),
		NAMES_OTHERS_TITLE(				"&aHistorique de &6{player}"),
		NAMES_OTHERS_LINE_ORIGINAL(		"    &6&l➤  &6{name} &7: &cAchat du compte"),
		NAMES_OTHERS_LINE_OTHERS(		"    &6&l➤  &6{name} &7: &c{datetime}"),
		NAMES_OTHERS_EMPTY(				"&6{player} &7n'a aucun historique de pseudo"),
		
		NEAR_DESCRIPTION(				"Donne la liste des joueurs dans les environs"),
		NEAR_LIST_LINE(					"    &6&l➤  &6{player} &7: &6{distance} bloc(s)"),
		NEAR_LIST_TITLE(				"&aListe des joueurs dans les environs"),
		NEAR_NOPLAYER(					"&cAucun joueur dans les environs."),
		
		PING_DESCRIPTION(				"Connaître la latence d'un joueur"),
		PING_PLAYER(					"&7Votre ping : &6{ping} &7ms."),
		PING_OTHERS(					"&7Le ping de &6{player} &7: &6{ping} &7ms."),
		
		PLAYED_DESCRIPTION(				"Connaître le temps de jeu d'un joueur"),
		PLAYED_PLAYER(					"&7Votre temps de jeu : &6{time}&7."),
		PLAYED_OTHERS(					"&7Le temps de jeu de &6{player} &7: &6{time}&7."),
		
		INVSEE_DESCRIPTION(				"Regarde l'inventaire d'un autre joueur"),
		
		RELOAD_ALL_DESCRIPTION(			"Recharge tous les plugins"),
		RELOAD_ALL_START(				"&cAttention : Rechargement de tous les plugins, risque de latence"),
		RELOAD_ALL_END(					"&aRechargement terminé"),
		
		REPAIR_DESCRIPTION(				"Répare les objets"),
		
		REPAIR_HAND_DESCRIPTION(		"Répare l'objet dans votre main"),
		REPAIR_HAND_ITEM_COLOR(			"&b"),
		REPAIR_HAND_PLAYER(				"&7Vous venez de réparer l'objet &b[{item}&b]&7."),
		REPAIR_HAND_ERROR(				"&7Vous ne pouvez pas réparer {item}&7."),
		REPAIR_HAND_MAX_DURABILITY(		"&6{item} &7est déjà réparé."),
		
		REPAIR_HOTBAR_DESCRIPTION(		"Répare les objets dans votre barre d'action"),
		REPAIR_HOTBAR_PLAYER(			"&7Vous venez de réparer tous les objets de votre barre d'action."),
		
		REPAIR_ALL_DESCRIPTION(			"Répare tous vos objets"),
		REPAIR_ALL_PLAYER(				"&7Vous venez de réparer tous les objets de votre inventaire."),
		
		MSG_DESCRIPTION(				"Envoie un message privé à un autre joueur"),
		MSG_PLAYER_SEND(				"&dEnvoyer à &f{DISPLAYNAME} &d: &7{message}"),
		MSG_PLAYER_SEND_HOVER(			"&cCliquez ici pour répondre à {DISPLAYNAME}"),
		MSG_PLAYER_RECEIVE(				"&dReçu de &f{DISPLAYNAME} &d: &7{message}"),
		MSG_PLAYER_RECEIVE_HOVER(		"&cCliquez ici pour répondre à {DISPLAYNAME}"),
		MSG_PLAYER_SEND_IS_AFK(			"&7{player} &7est absent."),
		MSG_CONSOLE_SEND(				"&dEnvoyer à la &6console &d: &7{message}"),
		MSG_CONSOLE_SEND_HOVER(			"&cCliquez ici pour répondre à la console"),
		MSG_CONSOLE_RECEIVE(			"&dReçu de la &6console &d: &7{message}"),
		MSG_CONSOLE_RECEIVE_HOVER(		"&cCliquez ici pour répondre à la console"),
		MSG_CONSOLE_ERROR(				"&cImpossible de vous envoyez un message à vous même."),
		MSG_COMMANDBLOCK_RECEIVE(		"&dVous avez reçu un message &d: &7{message}"),
		MSG_IGNORE_PLAYER(				"&cImpossible d'envoyer un message à &6{player}&c car vous l'ignorez."),
		MSG_IGNORE_RECEIVE(				"&6{player}&c ne recevera pas votre message car il vous ignore."),
		
		REPLY_DESCRIPTION(				"Répond à un message privé d'un autre joueur"),
		REPLY_PLAYER_SEND(				"&dEnvoyer à &f{DISPLAYNAME} &d: &7{message}"),
		REPLY_PLAYER_SEND_HOVER(		"&cCliquez ici pour répondre à {DISPLAYNAME}"),
		REPLY_PLAYER_RECEIVE(			"&dReçu de &f{DISPLAYNAME} &d: &7{message}"),
		REPLY_PLAYER_RECEIVE_HOVER(		"&cCliquez ici pour répondre à {DISPLAYNAME}"),
		REPLY_CONSOLE_SEND(				"&dEnvoyer à la &6console &d: &7{message}"),
		REPLY_CONSOLE_SEND_HOVER(		"&cCliquez ici pour répondre à la console"),
		REPLY_CONSOLE_RECEIVE(			"&dReçu de la &6console &d: &7{message}"),
		REPLY_CONSOLE_RECEIVE_HOVER(	"&cCliquez ici pour répondre à la console"),
		REPLY_EMPTY(					"&cVous n'êtes en conversation avec aucune personne, pour en démarrez une fait &6/msg {joueur} {message}"),
		REPLY_IGNORE_PLAYER(			"&cImpossible de répondre un message à &6{player}&c car vous l'ignorez."),
		REPLY_IGNORE_RECEIVE(			"&6{player}&c ne recevera pas votre message car il vous ignore."),
		
		RULES_DESCRIPTION(				"Affiche les règles du serveur."),
		
		SAY_DESCRIPTION(				"Envoie un message à tous les joueurs."),
		SAY_PLAYER(						"&7[&6{player}&7] {message}"),
		SAY_CONSOLE(					"&7[&6Console&7] {message}"),
		SAY_COMMANDBLOCK(				"&7[&6CommandBlock&7] {message}"),
		
		// SEED
		SEED_DESCRIPTION(				"Affiche le seed d'un monde"),
		SEED_MESSAGE(					"&7Le seed du monde &6{world} &7est &6{seed}&7."),
		SEED_NAME(						"&6&l{seed}"),
		
		SEEN_DESCRIPTION(				"Affiche la dernière IP de connexion d'un joueur"),
		SEEN_IP(						"&7Votre IP est &6{ip}&7."),
		SEEN_IP_OTHERS(					"&7L'adresse IP de &6{player} &7est &6{ip}&7."),
		SEEN_IP_OTHERS_NO_IP(			"&6{player} &7n'a pas d'adresse IP."),
		SEEN_IP_STYLE(					"&6{ip}"),
		SEEN_PLAYER_STYLE(				"&6{player}"),
		SEEN_IP_TITLE(					"&aInformations : &c{ip}"),
		SEEN_IP_MESSAGE(				"&7L'adresse IP &6{ip} &7correspond à :"),
		SEEN_IP_LIST(					"    &6&l➤  &6{player}"),
		SEEN_IP_NO_PLAYER(				"&7Aucun joueur"),
		
		// SKULL
		SKULL_DESCRIPTION(				"Donne la tête d'un joueur"),
		SKULL_MY_HEAD(					"&7Vous avez reçu votre tête."),
		SKULL_OTHERS(					"&7Vous avez reçu la tête de &6{player}&7."),
		
		// SPAWN
		SPAWN_DESCRIPTION(				"Permet de téléporter au spawn"),
		SPAWN_PLAYER(					"&7Vous avez été téléporté au &6{spawn}&7."),
		SPAWN_DELAY(					"&7Votre téléportation commencera dans &6{delay}&7. Ne bougez pas."),
		SPAWN_NAME(						"&6spawn"),
		SPAWN_NAME_HOVER(				"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		SPAWN_ERROR_GROUP(				"&cIl y a aucun groupe qui porte le nom &6{name}."),
		SPAWN_ERROR_SET(				"&cIl y a aucun spawn défini pour &6{name}."),
		SPAWN_ERROR_TELEPORT(			"&cImpossible de vous téléporter au {spawn}&7."),
		
		SPAWNS_DESCRIPTION(				"Affiche la liste des spawns"),
		SPAWNS_EMPTY(					"&7Aucun spawn."),
		SPAWNS_TITLE(					"&aListe des spawns"),
		SPAWNS_LINE_DELETE(				"    &6&l➤  &6{spawn} &7: {teleport} {delete}"),
		SPAWNS_LINE_DELETE_ERROR_WORLD(	"    &6&l➤  &6{spawn} &7: {delete}"),
		SPAWNS_LINE(					"    &6&l➤  &6{spawn} &7: {teleport}"),
		SPAWNS_TELEPORT(				"&a[Téléporter]"),
		SPAWNS_TELEPORT_HOVER(			"&cCliquez ici pour vous téléporter au spawn &6{name}&c."),
		SPAWNS_DELETE(					"&c[Supprimer]"),
		SPAWNS_DELETE_HOVER(			"&cCliquez ici pour supprimer le spawn &6{name}&c."),
		SPAWNS_NAME(					"&6{name}"),
		SPAWNS_NAME_HOVER(				"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		SPAWNS_PLAYER(					"&7Vous avez été téléporté au spawn &6{spawn}&7."),
		SPAWNS_DELAY(					"&7Votre téléportation commencera dans &6{delay}&7. Ne bougez pas."),
		SPAWNS_ERROR_TELEPORT(			"&cImpossible de vous téléporter au spawn {spawn}&7."),

		DELSPAWN_DESCRIPTION(			"Supprime un spawn"),
		DELSPAWN_INCONNU(				"&cIl n'y pas de spawn qui s'appelle &6{spawn}&c."),
		DELSPAWN_NAME(					"&6{name}"),
		DELSPAWN_NAME_HOVER(			"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		DELSPAWN_CONFIRMATION(			"&7Souhaitez-vous vraiment supprimer le spawn &6{spawn} &7: {confirmation}"),
		DELSPAWN_CONFIRMATION_VALID(	"&a[Confirmer]"),
		DELSPAWN_CONFIRMATION_VALID_HOVER("&cCliquez ici pour supprimer le spawn &6{name}&c."),
		DELSPAWN_DELETE(				"&7Vous avez supprimé le spawn &6{spawn}&7."),

		SETSPAWN_DESCRIPTION(			"Permet de définir un spawn"),
		SETSPAWN_ERROR_GROUP(			"&cIl y a aucun groupe qui porte le nom &6{name}."),
		SETSPAWN_NAME(					"&6{name}"),
		SETSPAWN_NAME_HOVER(			"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		SETSPAWN_REPLACE(				"&7Vous avez redéfini le spawn &6{name}&7."),
		SETSPAWN_NEW(					"&7Vous avez défini le spawn &6{name}&7."),
		
		// SPAWNER
		SPAWNER_DESCRIPTION(			"Permet de modifier le type d'un mob spawner"),
		
		// SPAWNMOB
		SPAWNMOB_DESCRIPTION(			"Fait apparaître une entité"),
		SPAWNMOB_MOB(					"&7Vous venez d'invoquer &6{amount} {entity}(s)&7."),
		SPAWNMOB_ERROR_MOB(				"&cErreur : nom invalide."),
		
		// SPEED
		SPEED_DESCRIPTION(				"Change la vitesse de déplacement"),
		SPEED_INFO_WALK(				"&7Votre vitesse de &6marche &7est de &6{speed}&7."),
		SPEED_INFO_FLY(					"&7Votre vitesse de &6vol &7est de &6{speed}&7."),
		SPEED_PLAYER_WALK(				"&7Vous avez défini votre vitesse de &6marche &7à &6{speed}&7."),
		SPEED_PLAYER_FLY(				"&7Vous avez défini votre vitesse de &6vol &7à &6{speed}&7."),
		SPEED_OTHERS_PLAYER_WALK(		"&7Votre vitesse de marche a été défini à &6{speed} &7par &6{staff}&7."),
		SPEED_OTHERS_STAFF_WALK(		"&7Vous avez défini la vitesse de &6marche &7de &6{player} &7à &6{speed}&7."),
		SPEED_OTHERS_PLAYER_FLY(		"&7Votre vitesse de vol a été défini à &6{speed} &7par &6{staff}&7."),
		SPEED_OTHERS_STAFF_FLY(			"&7Vous avez défini la vitesse de &6vol &7de &6{player} &7à &6{speed}&7."),
		
		// STOP
		STOP_DESCRIPTION(				"Arrête le serveur"),
		STOP_MESSAGE(					"&cArrêt du serveur par &6{staff}"),
		STOP_MESSAGE_REASON(			"&c{reason}"),
		STOP_CONSOLE_MESSAGE(			"&cArrêt du serveur"),
		STOP_CONSOLE_MESSAGE_REASON(	"&c{reason}"),
		
		// SUDO
		SUDO_DESCRIPTION(				"Fait exécuter une commande par un autre joueur"),
		SUDO_COMMAND(					"&6commande"),
		SUDO_COMMAND_HOVER(				"&c{command}"),
		SUDO_PLAYER(					"&7Votre {command} &7a bien été éxecutée par &6{player}&7."),
		SUDO_BYPASS(					"&cVous ne pouvez pas faire exécuter de commande à &6{player}&7."),
		SUDO_CONSOLE(					"&7Votre {command} &7à bien été éxecutée par la &6console&7."),
		
		// SUICIDE
		SUICIDE_DESCRIPTION(			"Permet de vous suicider"),
		SUICIDE_PLAYER(					"&7Vous vous êtes suicidé."),
		
		SUICIDE_DEATH_MESSAGE(			"&f<" + EReplacesPlayer.DISPLAYNAME.getName() + "> s'est suicidé."),
		SUICIDE_CANCEL(					"&cImpossible de vous suicider."),
		
		// TP
		TP_DESCRIPTION(					"Téléporte le joueur vers un autre joueur"),
		TP_DESTINATION(					"&6&l{player}"),
		TP_DESTINATION_HOVER(			"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		TP_PLAYER(						"&7Vous avez été téléporté vers &6{destination}&7."),
		TP_PLAYER_EQUALS(				"&7Vous avez été repositionné."),
		TP_OTHERS_PLAYER(				"&6{staff} &7vous a téléporté vers &6{destination}."),
		TP_OTHERS_STAFF(				"&6{player} &7a été téléporté vers &6{destination}&7."),
		TP_OTHERS_PLAYER_REPOSITION(	"&6{staff} &7vient de vous repositionner."),
		TP_OTHERS_STAFF_REPOSITION(		"&7Vous venez de repositionner &6{player}&7."),
		TP_OTHERS_STAFF_EQUALS_DESTINATION_PLAYER(	"&6{destination} &7vous a téléporté."),
		TP_OTHERS_STAFF_EQUALS_DESTINATION_STAFF(	"&7Vous venez de téléporter &6{player}&7."),
		TP_ERROR_LOCATION(				"&cImpossible de trouver une position pour réaliser une téléportation."),
		
		// TPALL
		TPALL_DESCRIPTION(				"Téléporte tous les joueurs vers un autre joueur"),
		TPALL_DESTINATION(				"&6&l{player}"),
		TPALL_DESTINATION_HOVER(		"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		TPALL_PLAYER(					"&6{destination} &7vous a téléporté."),
		TPALL_STAFF(					"&7Vous venez de téléporter tous les joueurs."),
		TPALL_ERROR(					"&cImpossible de trouver une position pour téléporter les joueurs."),
		TPALL_OTHERS_PLAYER(			"&6{staff} &7vous a téléporté vers &6{destination}."),
		TPALL_OTHERS_STAFF(				"&7Tous les joueurs ont été téléportés vers &6{destination}&7."),
		
		// TPHERE
		TPHERE_DESCRIPTION(				"Téléporte le joueur vers vous"),
		TPHERE_DESTINATION(				"&6&l{player}"),
		TPHERE_DESTINATION_HOVER(		"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		TPHERE_PLAYER(					"&6{destination} &7vous a téléporté."),
		TPHERE_STAFF(					"&7Vous venez de téléporter &6{player}&7."),
		TPHERE_EQUALS(					"&7Vous avez été repositionné."),
		TPHERE_ERROR(					"&cImpossible de trouver une position pour téléporter le joueur."),
		
		// TPPOS
		TPPOS_DESCRIPTION(				"Téléporte le joueur aux coordonnées choisis"),
		TPPOS_POSITION(					"&6&lposition"),
		TPPOS_POSITION_HOVER(			"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		TPPOS_PLAYER(					"&7Vous avez été téléporté à cette {position}&7."),
		TPPOS_PLAYER_ERROR(				"&7Impossible de vous téléporter à cette {position}&7."),
		TPPOS_OTHERS_PLAYER(			"&7Vous avez été téléporté à cette {position} &7par &6{staff}&7."),
		TPPOS_OTHERS_STAFF(				"&7Vous téléportez &6{player} &7à cette {position}&7."),
		TPPOS_OTHERS_ERROR(				"&7Impossible de téléporter &6{player} &7à cette {position}&7."),
		
		TELEPORT_ERROR_DELAY(			"&cVous avez bougé donc votre demande de téléportation a été annulé."),
		
		// TPA
		TPA_DESCRIPTION(				"Envoie une demande de téléportation à un joueur"),
		
		TPA_DESTINATION(				"&6&l{player}"),
		TPA_DESTINATION_HOVER(			"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		TPA_STAFF_QUESTION(				"&7Votre demande a été envoyée à &6{player}&7."),
		TPA_STAFF_ACCEPT(				"&7Votre demande de téléportation a été acceptée par &6{player}&7."),
		TPA_STAFF_DENY(					"&7Votre demande de téléportation a été refusée par &6{player}&7."),
		TPA_STAFF_EXPIRE(				"&7Votre demande de téléportation à &6{player} &7vient d'expirer."),
		TPA_STAFF_TELEPORT(				"&7Vous avez été téléporté vers &6{destination}&7."),
		TPA_PLAYER_QUESTION(			"&6{player} &7souhaite se téléporter vers vous : [RT]  {accept} {deny}[RT]"
									  + "&7Cette demande de téléportation expira dans &6{delay}&7."),
		TPA_PLAYER_QUESTION_ACCEPT(		"&a[Accepter]"),
		TPA_PLAYER_QUESTION_ACCEPT_HOVER("&cCliquez ici pour accepter la téléportation de &6{player}&c."),
		TPA_PLAYER_QUESTION_DENY(		"&c[Refuser]"),
		TPA_PLAYER_QUESTION_DENY_HOVER(	"&cCliquez ici pour refuser la téléportation de &6{player}&7."),
		TPA_PLAYER_DENY(				"&7Vous avez refusé la demande de téléportation de &6{player}&7."),
		TPA_PLAYER_ACCEPT(				"&6{player} &7sera téléporté dans &6{delay}&7."),
		TPA_PLAYER_EXPIRE(				"&cLa demande de téléportation de &6{player} &ca expiré."),
		TPA_PLAYER_TELEPORT(			"&6{player} &7vient d'être téléporté."),
		TPA_ERROR_EQUALS(				"&cImpossible de vous envoyer une demande à vous même."),
		TPA_ERROR_DELAY(				"&cIl y a déjà une demande de téléportation en cours."),
		TPA_ERROR_LOCATION(				"&cImpossible de trouver une position pour réaliser une téléportation."),
		TPA_IGNORE_PLAYER(				"&cImpossible d'envoyer une requete de téléportaion à &6{player}&c car vous l'ignorez."),
		TPA_IGNORE_DESTINATION(			"&6{player}&c ne recevera pas votre demande de téléportation car il vous ignore."),
		
		// TPHERE
		TPAHERE_DESCRIPTION(			"Envoie une demande de téléportation à un joueur"),
		
		TPAHERE_DESTINATION(			"&6&l{player}"),
		TPAHERE_DESTINATION_HOVER(		"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		TPAHERE_STAFF_QUESTION(			"&7Votre demande a été envoyée à &6{player}&7."),
		TPAHERE_STAFF_ACCEPT(			"&7Votre demande de téléportation a été acceptée par &6{player}&7."),
		TPAHERE_STAFF_DENY(				"&7Votre demande de téléportation a été refusée par &6{player}&7."),
		TPAHERE_STAFF_EXPIRE(			"&7Votre demande de téléportation à &6{player} &7vient d'expirée."),
		TPAHERE_STAFF_TELEPORT(			"&6{player}&7 a été téléporté à vous."),
		TPAHERE_PLAYER_QUESTION(		"&6{player} &7souhaite que vous vous téléportiez à lui/elle : {accept} {deny}[RT]"
																				  + "&7Cette demande de téléportation expira dans &6{delay}&7."),
		TPAHERE_PLAYER_QUESTION_ACCEPT(	"&a[Se téléporter]"),
		TPAHERE_PLAYER_QUESTION_ACCEPT_HOVER("&cCliquez ici pour vous téléporter à &6{player}&c."),
		TPAHERE_PLAYER_QUESTION_DENY(	"&c[Refuser]"),
		TPAHERE_PLAYER_QUESTION_DENY_HOVER("&cCliquez ici pour refuser la téléportation de &6{player}&7."),
		TPAHERE_PLAYER_EXPIRE(			"&cLa demande de téléportation de &6{player} &ca expirée."),
		TPAHERE_PLAYER_DENY(			"&7La demande de &6{player} &7 a bien été refusé."),
		TPAHERE_PLAYER_ACCEPT(			"&7Votre téléportation commencera dans &6{delay}&7. Ne bougez pas."),
		TPAHERE_PLAYER_TELEPORT(		"&7Vous avez été téléporté vers &6{destination}&7."),
		TPAHERE_ERROR_EQUALS(			"&cImpossible de vous envoyer une demande à vous même."),
		TPAHERE_ERROR_DELAY(			"&cIl y a déjà une demande de téléportation en cours."),
		TPAHERE_ERROR_LOCATION(			"&cImpossible de trouver une position pour réaliser une téléportation."),
		TPAHERE_IGNORE_STAFF(			"&cImpossible d'envoyer une requete de téléportaion à &6{player}&c car vous l'ignorez."),
		TPAHERE_IGNORE_PLAYER(			"&6{player}&c ne recevera pas votre demande de téléportation car il vous ignore."),
		
		// TPA list
		TPA_PLAYER_LIST_TITLE(			"&aListe des demandes de téléportation"),
		TPA_PLAYER_LIST_LINE(			"    &6&l➤  &6{player} &7: {accept} {deny}"),
		TPA_PLAYER_LIST_EMPTY(			"&7Aucune demande"),
		TPA_PLAYER_EMPTY(				"&cVous n'avez aucune demande de téléportation de &6{player}&c."),
		
		// TPAALL
		TPAALL_DESCRIPTION(				"Envoie une demande de téléportation à tous les joueurs"),
		TPAALL_PLAYER(					"&7Votre demande de téléportation a bien été envoyée à tous les joueurs."),
		TPAALL_OTHERS_STAFF(			"&7Votre demande pour téléporter tous les joueurs à &6{player} &7a bien été envoyée."),
		TPAALL_OTHERS_PLAYER(			"&6{staff} &7a envoyé une demande de téléportation vers vous à tous les joueurs."),
		TPAALL_ERROR_EMPTY(				"&cErreur : Aucun joueur à téléporter."),
		TPAALL_ERROR_PLAYER_LOCATION(	"&cErreur : Impossible de trouver une position pour téléporter les joueurs."),
		TPAALL_ERROR_OTHERS_LOCATION(	"&cErreur : Impossible de trouver une position pour téléporter les joueurs sur &6{player}&c."),
		
		// TPACCEPT
		TPACCEPT_DESCRIPTION(			"Permet d'accepter une demande de téléportation"),
		
		// TPDENY
		TPDENY_DESCRIPTION(				"Permet de refuser une demande de téléportation"),
		
		// TIME
		TIME_DESCRIPTION(				"Gère l'heure sur un monde"),
		TIME_FORMAT(					"&6{hours}h{minutes}"),
		TIME_INFORMATION(				"&7Il est actuellement &6{hours} &7dans le monde &6{world}&7."),
		TIME_SET_WORLD(					"&7Il est désormais &6{hours} &7dans le monde &6{world}&7."),
		TIME_SET_ALL_WORLD(				"&7Il est désormais &6{hours} &7dans les mondes&7."),
		TIME_ERROR(						"&cErreur : Horaire incorrect."),
		
		TIME_DAY_DESCRIPTION(			"Mettre le jour dans votre monde"),
		TIME_NIGHT_DESCRIPTION(			"Mettre la nuit dans votre monde"),
		
		// TOGGLE
		TOGGLE_DESCRIPTION(				"Permet de gérer les demandes de téléportation"),
		
		TOGGLE_ON_DESCRIPTION(			"Active les demandes de téléportation"),
		TOGGLE_ON_PLAYER(				"&7Vous acceptez désormais les demandes de téléportation."),
		TOGGLE_ON_PLAYER_ERROR(			"&cVous acceptez déjà les demandes de téléportation."),
		TOGGLE_ON_PLAYER_CANCEL(		"&cImpossible d'accepter les demandes de téléportation."),
		TOGGLE_ON_OTHERS_PLAYER(		"&7Vous acceptez désormais les demandes de téléportation grâce à &6{staff}&7."),
		TOGGLE_ON_OTHERS_STAFF(			"&6{player} &7accepte désormais les demandes de téléportation."),
		TOGGLE_ON_OTHERS_ERROR(			"&6{player} &caccepte déjà les demandes de téléportation."),
		TOGGLE_ON_OTHERS_CANCEL(		"&cImpossible que &6{player} &caccepte les demandes de téléportation."),

		TOGGLE_OFF_DESCRIPTION(			"Désactive les demandes de téléportation"),
		TOGGLE_OFF_PLAYER(				"&7Vous refusez désormais les demandes de téléportation."),
		TOGGLE_OFF_PLAYER_ERROR(		"&cVous refusez déjà les demandes de téléportation."),
		TOGGLE_OFF_PLAYER_CANCEL(		"&cImpossible de refuser les demandes de téléportation."),
		TOGGLE_OFF_OTHERS_PLAYER(		"&7Vous refusez désormais les demandes de téléportation grâce à &6{staff}&7."),
		TOGGLE_OFF_OTHERS_STAFF(		"&6{player} &7refuse désormais les demandes de téléportation."),
		TOGGLE_OFF_OTHERS_ERROR(		"&6{player} &crefuse déjà les demandes de téléportation."),
		TOGGLE_OFF_OTHERS_CANCEL(		"&cImpossible que &6{player} &crefuse les demandes de téléportation."),
		
		TOGGLE_STATUS_DESCRIPTION(		"Gère les demandes de téléportation"),
		TOGGLE_STATUS_PLAYER_ON(		"&7Vous acceptez les demandes de téléportation."),
		TOGGLE_STATUS_PLAYER_OFF(		"&7Vous n'acceptez pas les demandes de téléportation."),
		TOGGLE_STATUS_OTHERS_ON(		"&6{player} &7accepte les demandes de téléportation."),
		TOGGLE_STATUS_OTHERS_OFF(		"&6{player} &7n'accepte pas les demandes de téléportation."),
		
		TOGGLE_DISABLED(				"&6{player} &7n’accepte pas les demandes de téléportation."),
		
		// TOP
		TOP_DESCRIPTION(				"Téléporte le joueur à la position la plus élevée"),
		TOP_POSITION(					"&6&lposition"),
		TOP_POSITION_HOVER(				"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		TOP_DELAY(						"&7Votre téléportation commencera dans &6{delay}&7. Ne bougez pas."),
		TOP_TELEPORT(					"&7Vous avez été téléporté à la {position} &7la plus élevée."),
		TOP_TELEPORT_ERROR(				"&cImpossible de trouver une position où vous téléporter."),
		
		// TREE
		TREE_DESCRIPTION(				"Place un arbre"),
		TREE_INCONNU(					"&cType d'arbre inconnu : &6{type}"),
		TREE_NO_CAN_DIRT(				"&cErreur : Impossible de placer un arbre à cette endroit. Regarder plutôt un bloc d'herbe ou de terre."),
		TREE_NO_CAN_SAND(				"&cErreur : Impossible de placer un arbre à cette endroit. Regarder plutôt un bloc de sable."),
		
		// UUID
		UUID_DESCRIPTION(				"Affiche l'identifiant unique du joueur."),
		UUID_NAME(						"&6&l{name}"),
		UUID_UUID(						"&6&l{uuid}"),
		UUID_PLAYER_UUID(				"&7Votre UUID est &6&l{uuid}&7."),
		UUID_PLAYER_NAME(				"&7Votre nom est &6&l{name}&7."),
		UUID_OTHERS_PLAYER_UUID(		"&7L'UUID du joueur &6{player} &7est &6&l{uuid}&7."),
		UUID_OTHERS_PLAYER_NAME(		"&7L'UUID &6{uuid} &7correspond au pseudo &6&l{name}"),
		
		// VANISH
		VANISH_DESCRIPTION(				"Permet de vous rendre invisible."),
		
		VANISH_ON_DESCRIPTION(			"Rend le joueur invisible"),
		VANISH_ON_PLAYER(				"&7Vous êtes désormais invisible."),
		VANISH_ON_PLAYER_ERROR(			"&cErreur : Vous êtes déjà invisible."),
		VANISH_ON_PLAYER_CANCEL(		"&cImpossible de vous rendre invisible."),
		VANISH_ON_OTHERS_PLAYER(		"&7Vous êtes désormais invisible grâce à &6{staff}&7."),
		VANISH_ON_OTHERS_STAFF(			"&7Vous venez de rendre invisible &6{player}&7."),
		VANISH_ON_OTHERS_ERROR(			"&cErreur : &6{player} &cest déjà invisible."),
		VANISH_ON_OTHERS_CANCEL(		"&cImpossible de rendre &6{player} &cinvisible."),
		
		VANISH_OFF_DESCRIPTION(			"Rend le joueur visible"),
		VANISH_OFF_PLAYER(				"&7Vous êtes désormais visible."),
		VANISH_OFF_PLAYER_ERROR(		"&cErreur : Vous êtes déjà visible."),
		VANISH_OFF_PLAYER_CANCEL(		"&cImpossible de vous rendre visible."),
		VANISH_OFF_OTHERS_PLAYER(		"&7Vous désormais visible à cause de &6{staff}&7."),
		VANISH_OFF_OTHERS_STAFF(		"&7Vous venez de rendre &6{player} &7visible."),
		VANISH_OFF_OTHERS_ERROR(		"&cErreur : &6{player} &cest déjà visible."),
		VANISH_OFF_OTHERS_CANCEL(		"&cImpossible de rendre &6{player} &cvible."),
	
		VANISH_STATUS_DESCRIPTION(		"Affiche si le joueur est visible où pas"),
		VANISH_STATUS_PLAYER_ON(		"&7Vous êtes invisible."),
		VANISH_STATUS_PLAYER_OFF(		"&7Vous êtes visible."),
		VANISH_STATUS_OTHERS_ON(		"&6{player} &7est invisible."),
		VANISH_STATUS_OTHERS_OFF(		"&6{player} &7est visible."),
		
		// WARP
		WARP_NAME(						"&6&l{name}"),
		WARP_NAME_HOVER(				"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		WARP_INCONNU(					"&cIl n'y a pas de warp qui s'appelle &6{warp}&c."),
		WARP_NO_PERMISSION(				"&cVous n'avez pas la permission pour vous téléporter au warp &6{warp}&c."),
		
		WARP_DESCRIPTION(				"Se téléporte à un warp"),
		WARP_EMPTY(						"&7Aucun warp"),
		WARP_LIST_TITLE(				"&aListe des warps"),
		WARP_LIST_LINE_DELETE(			"    &6&l➤  &6{warp} &7: {teleport} {delete}"),
		WARP_LIST_LINE_DELETE_ERROR_WORLD("    &6&l➤  &6{warp} &7: {delete}"),
		WARP_LIST_LINE(					"    &6&l➤  &6{warp} &7: {teleport}"),
		WARP_LIST_TELEPORT(				"&a[Téléporter]"),
		WARP_LIST_TELEPORT_HOVER(		"&cCliquez ici pour vous téléporter à le warp &6{warp}&c."),
		WARP_LIST_DELETE(				"&c[Supprimer]"),
		WARP_LIST_DELETE_HOVER(			"&cCliquez ici pour supprimer le warp &6{warp}&c."),
		WARP_TELEPORT_PLAYER(			"&7Vous avez été téléporté au warp &6{warp}&7."),
		WARP_TELEPORT_PLAYER_ERROR(		"&cImpossible de vous téléporter au warp &6{warp}&c."),
		WARP_TELEPORT_OTHERS_PLAYER(	"&7Vous avez été téléporté au warp &6{warp} &7par &6{staff}&7."),
		WARP_TELEPORT_OTHERS_STAFF(		"&7Vous avez téléporté &6{player} &7au warp &6{warp}&7."),
		WARP_TELEPORT_OTHERS_ERROR(		"&cImpossible de téléporter &6{player} &7au warp &6{warp}&c."),
		
		DELWARP_DESCRIPTION(			"Supprime un warp"),
		DELWARP_INCONNU(				"&cIl n'y pas de warp qui s'appelle &6{warp}&c."),
		DELWARP_NAME(					"&6&l{name}"),
		DELWARP_NAME_HOVER(				"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		DELWARP_CONFIRMATION(			"&7Souhaitez-vous vraiment supprimer le warp &6{warp} &7: {confirmation}"),
		DELWARP_CONFIRMATION_VALID(		"&a[Confirmer]"),
		DELWARP_CONFIRMATION_VALID_HOVER("&cCliquez ici pour supprimer le warp &6{warp}&c."),
		DELWARP_DELETE(					"&7Vous avez supprimé le warp &6{warp}&7."),
		DELWARP_CANCEL(					"&cImpossible de supprimé le &6{warp} &cpour le moment."),
		
		SETWARP_DESCRIPTION(			"Crée un warp"),
		SETWARP_NAME(					"&6&l{name}"),
		SETWARP_NAME_HOVER(				"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		SETWARP_REPLACE(				"&7Vous avez redéfini le warp &6{warp}&7."),
		SETWARP_REPLACE_CANCEL(			"&cImpossible de redéfinir le warp &6{warp} &4pour le moment."),
		SETWARP_NEW(					"&7Vous avez défini le warp &6{warp}&7."),
		SETWARP_NEW_CANCEL(				"&cImpossible de définir le warp &6{warp} &4pour le moment."),
		
		WEATHER_DESCRIPTION(			"Change la météo d'un monde"),
		WEATHER_ERROR(					"&cVous ne pouvez pas changer la météo dans ce type de monde."),
		WEATHER_SUN(					"&7Vous avez mis &6le beau temps &7dans le monde &6{world}&7."),
		WEATHER_RAIN(					"&7Vous avez mis &6la pluie &7dans le monde &6{world}&7."),
		WEATHER_STORM(					"&7Vous avez mis &6la tempête &7dans le monde &6{world}&7."),
		WEATHER_SUN_DURATION(			"&7Vous avez mis &6le beau temps &7dans le monde &6{world}&7 pendant &6{duration}&7 minute(s)."),
		WEATHER_RAIN_DURATION(			"&7Vous avez mis &6la pluie &7dans le monde &6{world}&7 pendant &6{duration}&7 minute(s)."),
		WEATHER_STORM_DURATION(			"&7Vous avez mis &6la tempête &7dans le monde &6{world}&7 pendant &6{duration}&7 minute(s)."),
		
		WEATHER_RAIN_DESCRIPTION(		"Met la pluie dans votre monde"),
		WEATHER_STORM_DESCRIPTION(		"Met la tempête dans votre monde"),
		WEATHER_SUN_DESCRIPTION(		"Met le beau dans temps dans votre monde"),
		
		// WhiteList
		WHITELIST_DESCRIPTION(			"Gère la whitelist"),
		
		WHITELIST_ON_DESCRIPTION(		"Active la whitelist"),
		WHITELIST_ON_ACTIVATED(			"&7La whitelist est désormais &aactivée&7."),
		WHITELIST_ON_ALREADY_ACTIVATED(	"&cErreur : La whitelist est déjà activée."),
		
		WHITELIST_OFF_DESCRIPTION(		"Désactive la whitelist"),
		WHITELIST_OFF_DISABLED(			"&7La whitelist est désormais &cdésactivée&7."),
		WHITELIST_OFF_ALREADY_DISABLED(	"&cErreur : &7La whitelist est déjà &cdésactivée&7."),
		
		WHITELIST_STATUS_DESCRIPTION(	"Gère la liste d'acces des joueurs"),
		WHITELIST_STATUS_ACTIVATED(		"&7La whitelist est &aactivée&7."),
		WHITELIST_STATUS_DISABLED(		"&7La whitelist est &cdésactivée&7."),
		
		WHITELIST_ADD_DESCRIPTION(		"Ajoute un joueur dans la whitelist"),
		WHITELIST_ADD_PLAYER(			"&7Le joueur &6{player} &7a été ajouté dans la whitelist."),
		WHITELIST_ADD_ERROR(			"&cErreur : Le joueur {player} est déjà dans la whitelist."),
		
		WHITELIST_REMOVE_DESCRIPTION(	"Supprime un joueur dans la whitelist"),
		WHITELIST_REMOVE_PLAYER(		"&7Le joueur &6{player} &7a été supprimé dans la whitelist."),
		WHITELIST_REMOVE_ERROR(			"&cErreur : Le joueur {player} n'est pas dans la whitelist."),
		WHITELIST_LIST_DESCRIPTION(		"Affiche la whitelist"),
		
		WHITELIST_LIST_TITLE(			"&aWhitelist"),
		WHITELIST_LIST_LINE_DELETE(		"    &6&l➤  {player} &7: {delete}"),
		WHITELIST_LIST_LINE(			"    &6&l➤  {player}"),
		WHITELIST_LIST_REMOVE(			"&c[Supprimer]"),
		WHITELIST_LIST_REMOVE_HOVER(	"&cCliquez ici pour retirer &6{player} &cde la whitelist."),
		WHITELIST_LIST_NO_PLAYER(		"&7Aucun joueur"),
		
		// Whois
		WHOIS_DESCRIPTION(				"Affiche les informations d'un joueur"),
		WHOIS_TITLE_OTHERS(				"&aInformations : &c{player}"),
		WHOIS_TITLE_EQUALS(				"&aVos informations"),
		WHOIS_UUID(						"    &6&l➤  &6UUID : {uuid}"),
		WHOIS_UUID_STYLE(				"&c{uuid}"),
		WHOIS_IP(						"    &6&l➤  &6IP : {ip}"),
		WHOIS_LAST_IP(					"    &6&l➤  &6Dernière IP : {ip}"),
		WHOIS_IP_STYLE(					"&c{ip}"),
		WHOIS_PING(						"    &6&l➤  &6Ping : &c{ping} &6ms"),
		WHOIS_HEAL(						"    &6&l➤  &6Santé : &a{heal}&6/&c{max_heal}"),
		WHOIS_FOOD(						"    &6&l➤  &6Faim : &a{food}&6/&c{max_food}"),
		WHOIS_FOOD_SATURATION(			"    &6&l➤  &6Faim : &a{food}&6/&c{max_food} &6(+&a{saturation} &6saturation)"),
		WHOIS_EXP(						"    &6&l➤  &6Expérience :"),
		WHOIS_EXP_LEVEL(				"        &6&l●  &a{level} &6niveau(x)"),
		WHOIS_EXP_POINT(				"        &6&l●  &a{point} &6point(s)"),
		WHOIS_SPEED(					"    &6&l➤  &6Vitesse :"),
		WHOIS_SPEED_FLY(				"        &6&l●  &6En volant : &a{speed}"),
		WHOIS_SPEED_WALK(				"        &6&l●  &6En marchant : &a{speed}"),
		WHOIS_LOCATION(					"    &6&l➤  &6Position : {position}"),
		WHOIS_LOCATION_POSITION(		"&6(&c{x}&6, &c{y}&6, &c{z}&6, &c{world}&6)"),
		WHOIS_LOCATION_POSITION_HOVER(	"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		WHOIS_BALANCE(					"    &6&l➤  &6Solde : &c{money}"),
		WHOIS_GAMEMODE(					"    &6&l➤  &6Mode de jeu : &c{gamemode}"),
		WHOIS_GOD_ENABLE(				"    &6&l➤  &6Mode Dieu : &aActivé"),
		WHOIS_GOD_DISABLE(				"    &6&l➤  &6Mode Dieu : &cDésactivé"),
		WHOIS_FLY_ENABLE_FLY(			"    &6&l➤  &6Fly Mode : &aActivé &6(&avol&6)"),
		WHOIS_FLY_ENABLE_WALK(			"    &6&l➤  &6Fly Mode : &aActivé &6(&cmarche&6)"),
		WHOIS_FLY_DISABLE(				"    &6&l➤  &6Fly Mode : &cDésactivé"),
		WHOIS_MUTE_ENABLE(				"    &6&l➤  &6Muet : &aActivé"),
		WHOIS_MUTE_DISABLE(				"    &6&l➤  &6Muet : &cDésactivé"),
		WHOIS_VANISH_ENABLE(			"    &6&l➤  &6Vanish : &aActivé"),
		WHOIS_VANISH_DISABLE(			"    &6&l➤  &6Vanish : &cDésactivé"),
		WHOIS_FREEZE_ENABLE(			"    &6&l➤  &6Freeze : &aActivé"),
		WHOIS_FREEZE_DISABLE(			"    &6&l➤  &6Freeze : &cDésactivé"),
		WHOIS_AFK_ENABLE(				"    &6&l➤  &6AFK : &aActivé"),
		WHOIS_AFK_DISABLE(				"    &6&l➤  &6AFK : &cDésactivé"),
		WHOIS_FIRST_DATE_PLAYED(		"    &6&l➤  &6Première connexion : &a{time}"),
		WHOIS_LAST_DATE_PLAYED_ONLINE(	"    &6&l➤  &6Connecté depuis : &a{time}"),
		WHOIS_LAST_DATE_PLAYED_OFFLINE(	"    &6&l➤  &6Dernière connexion : &a{time}"),
		WHOIS_CHAT_FULL(				"    &6&l➤  &6Chat : &aVisible"),
		WHOIS_CHAT_SYSTEM(				"    &6&l➤  &6Chat : &aCommandes seulement"),
		WHOIS_CHAT_HIDDEN(				"    &6&l➤  &6Chat : &aMasqué"),
		WHOIS_VIEW_DISTANCE(			"    &6&l➤  &6Distance d'affichage : &a{amount}"),
		WHOIS_CHATCOLOR_ON(				"    &6&l➤  &6Couleur dans le chat : &aActivé"),
		WHOIS_CHATCOLOR_OFF(			"    &6&l➤  &6Couleur dans le chat : &cDésactivé"),
		WHOIS_LANGUAGE(					"    &6&l➤  &6Langage : &a{langue}"),
		WHOIS_TOGGLE_ENABLE(			"    &6&l➤  &6Requêtes de téléportation : &aActivé"),
		WHOIS_TOGGLE_DISABLE(			"    &6&l➤  &6Requêtes de téléportation : &cDésactivé"),
		WHOIS_TOTAL_TIME_PLAYED(		"    &6&l➤  &6Temps de jeu : &a{time}"),
		
		WORLDBORDER_DESCRIPTION(				"Gère la bordure des mondes"),
		WORLDBORDER_INFO_DESCRIPTION(			"Affiche les informations sur la bordure d'un monde"),
		WORLDBORDER_INFO_TITLE(					"&6Monde : {world}"),
		WORLDBORDER_INFO_LOCATION(				"    &6&l➤  &6Centre : {position}"),
		WORLDBORDER_INFO_LOCATION_POSITION(		"&6(&c{x}&6, &c{z}&6, &c{world}&6)"),
		WORLDBORDER_INFO_LOCATION_POSITION_HOVER("&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cZ : &6{z}"),
		WORLDBORDER_INFO_BORDER(				"    &6&l➤  &6La bordure est de &a{amount} &6bloc(s)"),
		WORLDBORDER_INFO_BUFFER(				"    &6&l➤  &6Zone de tolérance : &a{amount} &6bloc(s)"),
		WORLDBORDER_INFO_DAMAGE(				"    &6&l➤  &6Dégat(s) : &a{amount} &6coeur(s)"),
		WORLDBORDER_INFO_WARNING_TIME(			"    &6&l➤  &6Avertissement du rétrecissement de la bordure : &a{amount} &6seconde(s)"),
		WORLDBORDER_INFO_WARNING_DISTANCE(		"    &6&l➤  &6Avertissement de la bordure : &a{admount} &6bloc(s)"),
		WORLDBORDER_SET_DESCRIPTION(			"Défini la bordure d'un monde"),
		WORLDBORDER_SET_BORDER(					"&7La taille de la bordure du monde &6{world} &7a été défini à &6{amount} &7bloc(s) de large."),
		WORLDBORDER_SET_BORDER_INCREASE(		"&7Agrandissement de la bordure du monde &6{world} &7à {amount} bloc(s) de large en &6{time} &7seconde(s)."),
		WORLDBORDER_SET_BORDER_DECREASE(		"&7Rétrécissement de la bordure du monde &6{world} &7à {amount} bloc(s) de large en &6{time} &7seconde(s)."),
		WORLDBORDER_CENTER_DESCRIPTION(			"Défini le centre de la bordure d'un monde"),
		WORLDBORDER_CENTER_MESSAGE(				"&7Le centre de la bordure du monde &6{world} &7a été défini en &6X: {x} Z: {z}&7."),
		WORLDBORDER_ADD_DESCRIPTION(			"Augmente ou diminue la taille de la bordure d'un monde"),
		WORLDBORDER_ADD_BORDER_INCREASE(		"&7Agrandissement de la bordure du monde &6{world} &7à {amount} bloc(s)."),
		WORLDBORDER_ADD_BORDER_DECREASE(		"&7Rétrécissement de la bordure du monde &6{world} &7à {amount} bloc(s)."),
		WORLDBORDER_ADD_BORDER_TIME_INCREASE(	"&7Agrandissement de la bordure du monde &6{world} &7à {amount} bloc(s) de large en &6{time} &7seconde(s)."),
		WORLDBORDER_ADD_BORDER_TIME_DECREASE(	"&7Rétrécissement de la bordure du monde &6{world} &7à {amount} bloc(s) de large en &6{time} &7seconde(s)."),
		WORLDBORDER_DAMAGE_DESCRIPTION(			"Configure les dégats infligés aux entités en dehors de la bordure d'un monde"),
		WORLDBORDER_DAMAGE_AMOUNT(				"&7La quantité de dégâts causés par la bordure dans le monde &6{world} &7a été défini à &6{amount}&7."),
		WORLDBORDER_DAMAGE_BUFFER(				"&7La zone de tolérance de la bordure du monde &6{world} &7a été défini à &6{amount}&7."),
		WORLDBORDER_WARNING_DESCRIPTION(		"Configure l'écran d'avertissement pour les joueurs qui s'approche de la bordure d'un monde"),
		WORLDBORDER_WARNING_TIME(				"&7L'avertissement de la bordure du monde &6{world} &7a été défini à &6{amount} &7seconde(s)."),
		WORLDBORDER_WARNING_DISTANCE(			"&7L'avertissement de la bordure du monde &6{world} &7a été défini à &6{amount} &7bloc(s) de distance."),
			
		WORLDS_DESCRIPTION(				"Téléporte un joueur dans le monde de votre choix"),
		WORLDS_END_DESCRIPTION(			"Téléporte un joueur dans le monde du néant"),
		WORLDS_NETHER_DESCRIPTION(		"Téléporte un joueur dans le monde de l'enfer"),
		WORLDS_LIST_TITLE(				"&aListe des mondes"),
		WORLDS_LIST_LINE(				"    &6&l➤  &6{world} &7: {teleport}"),
		WORLDS_LIST_TELEPORT(			"&a[Téléporter]"),
		WORLDS_LIST_TELEPORT_HOVER(		"&cCliquez ici pour vous téléporter dans le monde &6{world}&c."),
		WORLDS_TELEPORT_WORLD(			"&6&l{world}"),
		WORLDS_TELEPORT_WORLD_HOVER(	"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		WORLDS_TELEPORT_PLAYER(			"&7Vous avez été téléporté dans le monde &6{world}&7."),
		WORLDS_TELEPORT_PLAYER_ERROR(		"&7Impossible de vous téléporter dans le monde {world}&7."),
		WORLDS_TELEPORT_OTHERS_PLAYER(	"&7Vous avez été téléporté dans le monde {world} &7par &6{staff}&7."),
		WORLDS_TELEPORT_OTHERS_STAFF(	"&7Vous téléportez &6{player} &7dans le monde {world}&7."),
		WORLDS_TELEPORT_OTHERS_ERROR(	"&7Impossible de trouver une position pour téléporter &6{player} &7dans le monde &6{world}&7."),
		
		PERMISSIONS_COMMANDS_EXECUTE(""),
		PERMISSIONS_COMMANDS_HELP(""),
		PERMISSIONS_COMMANDS_RELOAD(""),
		PERMISSIONS_COMMANDS_AFK_EXECUTE(""),
		PERMISSIONS_COMMANDS_AFK_OTHERS(""),
		PERMISSIONS_COMMANDS_AFK_BYPASS_AUTO(""),
		PERMISSIONS_COMMANDS_AFK_BYPASS_KICK(""),
		PERMISSIONS_COMMANDS_BACK_EXECUTE(""),
		PERMISSIONS_COMMANDS_BED_EXECUTE(""),
		PERMISSIONS_COMMANDS_BED_OTHERS(""),
		PERMISSIONS_COMMANDS_BOOK_EXECUTE(""),
		PERMISSIONS_COMMANDS_BROADCAST_EXECUTE(""),
		PERMISSIONS_COMMANDS_BUTCHER_EXECUTE(""),
		PERMISSIONS_COMMANDS_BUTCHER_ANIMAL(""),
		PERMISSIONS_COMMANDS_BUTCHER_MONSTER(""),
		PERMISSIONS_COMMANDS_BUTCHER_TYPE(""),
		PERMISSIONS_COMMANDS_BUTCHER_ALL(""),
		PERMISSIONS_COMMANDS_BUTCHER_WORLD(""),
		PERMISSIONS_COMMANDS_CLEAREFFECT_EXECUTE(""),
		PERMISSIONS_COMMANDS_CLEAREFFECT_OTHERS(""),
		PERMISSIONS_COMMANDS_CLEARINVENTORY_EXECUTE(""),
		PERMISSIONS_COMMANDS_CLEARINVENTORY_OTHERS(""),
		PERMISSIONS_COMMANDS_COLOR_EXECUTE(""),
		PERMISSIONS_COMMANDS_EFFECT_EXECUTE(""),
		PERMISSIONS_COMMANDS_ENCHANT_EXECUTE(""),
		PERMISSIONS_COMMANDS_ENDERCHEST_EXECUTE(""),
		PERMISSIONS_COMMANDS_ENDERCHEST_OTHERS(""),
		PERMISSIONS_COMMANDS_EXP_EXECUTE(""),
		PERMISSIONS_COMMANDS_EXP_OTHERS(""),
		PERMISSIONS_COMMANDS_EXT_EXECUTE(""),
		PERMISSIONS_COMMANDS_EXT_OTHERS(""),
		PERMISSIONS_COMMANDS_FEED_EXECUTE(""),
		PERMISSIONS_COMMANDS_FEED_OTHERS(""),
		PERMISSIONS_COMMANDS_FORMAT_EXECUTE(""),
		PERMISSIONS_COMMANDS_FREEZE_EXECUTE(""),
		PERMISSIONS_COMMANDS_FREEZE_OTHERS(""),
		PERMISSIONS_COMMANDS_FLY_EXECUTE(""),
		PERMISSIONS_COMMANDS_FLY_OTHERS(""),
		PERMISSIONS_COMMANDS_GAMEMODE_EXECUTE(""),
		PERMISSIONS_COMMANDS_GAMEMODE_OTHERS(""),
		PERMISSIONS_COMMANDS_GENERATE_EXECUTE(""),
		PERMISSIONS_COMMANDS_GETPOS_EXECUTE(""),
		PERMISSIONS_COMMANDS_GETPOS_OTHERS(""),
		PERMISSIONS_COMMANDS_GOD_EXECUTE(""),
		PERMISSIONS_COMMANDS_GOD_OTHERS(""),
		PERMISSIONS_COMMANDS_HAT_EXECUTE(""),
		PERMISSIONS_COMMANDS_HEAL_EXECUTE(""),
		PERMISSIONS_COMMANDS_HEAL_OTHERS(""),
		PERMISSIONS_COMMANDS_HOME_EXECUTE(""),
		PERMISSIONS_COMMANDS_DELHOME_EXECUTE(""),
		PERMISSIONS_COMMANDS_HOMEOTHERS_EXECUTE(""),
		PERMISSIONS_COMMANDS_HOMEOTHERS_DELETE(""),
		PERMISSIONS_COMMANDS_SETHOME_EXECUTE(""),
		PERMISSIONS_COMMANDS_SETHOME_MULTIPLE_EXECUTE(""),
		PERMISSIONS_COMMANDS_SETHOME_MULTIPLES(""),
		PERMISSIONS_COMMANDS_SETHOME_MULTIPLE_UNLIMITED(""),
		PERMISSIONS_COMMANDS_IGNORE_EXECUTE(""),
		PERMISSIONS_COMMANDS_IGNORE_OTHERS(""),
		PERMISSIONS_COMMANDS_IGNORE_BYPASS(""),
		PERMISSIONS_COMMANDS_INVSEE_EXECUTE(""),
		PERMISSIONS_COMMANDS_INVSEE_MODIFY(""),
		PERMISSIONS_COMMANDS_INFO_EXECUTE(""),
		PERMISSIONS_COMMANDS_ITEM_EXECUTE(""),
		PERMISSIONS_COMMANDS_ITEM_BYPASS(""),
		PERMISSIONS_COMMANDS_ITEMNAME_EXECUTE(""),
		PERMISSIONS_COMMANDS_ITEMLORE_EXECUTE(""),
		PERMISSIONS_COMMANDS_JUMP_EXECUTE(""),
		PERMISSIONS_COMMANDS_KICK_EXECUTE(""),
		PERMISSIONS_COMMANDS_KICK_BYPASS(""),
		PERMISSIONS_COMMANDS_KICKALL_EXECUTE(""),
		PERMISSIONS_COMMANDS_KILL_EXECUTE(""),
		PERMISSIONS_COMMANDS_LAG_EXECUTE(""),
		PERMISSIONS_COMMANDS_LIST_EXECUTE(""),
		PERMISSIONS_COMMANDS_REPAIR_EXECUTE(""),
		PERMISSIONS_COMMANDS_REPAIR_HAND(""),
		PERMISSIONS_COMMANDS_REPAIR_HOTBAR(""),
		PERMISSIONS_COMMANDS_REPAIR_ALL(""),
		PERMISSIONS_COMMANDS_REPLY_EXECUTE(""),
		PERMISSIONS_COMMANDS_MAIL_EXECUTE(""),
		PERMISSIONS_COMMANDS_MAIL_SEND(""),
		PERMISSIONS_COMMANDS_MAIL_SENDALL(""),
		PERMISSIONS_COMMANDS_ME_EXECUTE(""),
		PERMISSIONS_COMMANDS_MSG_EXECUTE(""),
		PERMISSIONS_COMMANDS_MSG_COLOR(""),
		PERMISSIONS_COMMANDS_MSG_FORMAT(""),
		PERMISSIONS_COMMANDS_MSG_MAGIC(""),
		PERMISSIONS_COMMANDS_MSG_CHARACTER(""),
		PERMISSIONS_COMMANDS_MSG_COMMAND(""),
		PERMISSIONS_COMMANDS_MSG_URL(""),
		PERMISSIONS_COMMANDS_MSG_ICONS(""),
		PERMISSIONS_COMMANDS_MOJANG_EXECUTE(""),
		PERMISSIONS_COMMANDS_MORE_EXECUTE(""),
		PERMISSIONS_COMMANDS_MORE_UNLIMITED(""),
		PERMISSIONS_COMMANDS_MOTD_EXECUTE(""),
		PERMISSIONS_COMMANDS_NAMES_EXECUTE(""),
		PERMISSIONS_COMMANDS_NAMES_OTHERS(""),
		PERMISSIONS_COMMANDS_NEAR_EXECUTE(""),
		PERMISSIONS_COMMANDS_NEARS(""),
		PERMISSIONS_COMMANDS_PING_EXECUTE(""),
		PERMISSIONS_COMMANDS_PING_OTHERS(""),
		PERMISSIONS_COMMANDS_PLAYED_EXECUTE(""),
		PERMISSIONS_COMMANDS_PLAYED_OTHERS(""),
		PERMISSIONS_COMMANDS_RULES_EXECUTE(""),
		PERMISSIONS_COMMANDS_SAY_EXECUTE(""),
		PERMISSIONS_COMMANDS_SEEN_EXECUTE(""),
		PERMISSIONS_COMMANDS_SKULL_EXECUTE(""),
		PERMISSIONS_COMMANDS_SKULL_OTHERS(""),
		PERMISSIONS_COMMANDS_SPAWN_EXECUTE(""),
		PERMISSIONS_COMMANDS_SPAWNS_EXECUTE(""),
		PERMISSIONS_COMMANDS_SETSPAWN_EXECUTE(""),
		PERMISSIONS_COMMANDS_DELSPAWN_EXECUTE(""),
		PERMISSIONS_COMMANDS_SPAWNER_EXECUTE(""),
		PERMISSIONS_COMMANDS_SPAWNMOB_EXECUTE(""),
		PERMISSIONS_COMMANDS_SPEED_EXECUTE(""),
		PERMISSIONS_COMMANDS_SPEED_FLY(""),
		PERMISSIONS_COMMANDS_SPEED_WALK(""),
		PERMISSIONS_COMMANDS_SPEED_OTHERS(""),
		PERMISSIONS_COMMANDS_STOP_EXECUTE(""),
		PERMISSIONS_COMMANDS_SUDO_EXECUTE(""),
		PERMISSIONS_COMMANDS_SUDO_CONSOLE(""),
		PERMISSIONS_COMMANDS_SUDO_BYPASS(""),
		PERMISSIONS_COMMANDS_SUICIDE_EXECUTE(""),
		PERMISSIONS_COMMANDS_TP_EXECUTE(""),
		PERMISSIONS_COMMANDS_TP_OTHERS(""),
		PERMISSIONS_COMMANDS_TPACCEPT_EXECUTE(""),
		PERMISSIONS_COMMANDS_TPADENY_EXECUTE(""),
		PERMISSIONS_COMMANDS_TPA_EXECUTE(""),
		PERMISSIONS_COMMANDS_TPAHERE_EXECUTE(""),
		PERMISSIONS_COMMANDS_TPAALL_EXECUTE(""),
		PERMISSIONS_COMMANDS_TPAALL_OTHERS(""),
		PERMISSIONS_COMMANDS_TPALL_EXECUTE(""),
		PERMISSIONS_COMMANDS_TPALL_OTHERS(""),
		PERMISSIONS_COMMANDS_TPPOS_EXECUTE(""),
		PERMISSIONS_COMMANDS_TPPOS_OTHERS(""),
		PERMISSIONS_COMMANDS_TIME_EXECUTE(""),
		PERMISSIONS_TELEPORT_BYPASS_TIME(""),
		PERMISSIONS_TELEPORT_BYPASS_MOVE(""),
		PERMISSIONS_COMMANDS_TOGGLE_EXECUTE(""),
		PERMISSIONS_COMMANDS_TOOGLE_OTHERS(""),
		PERMISSIONS_COMMANDS_TOP_EXECUTE(""),
		PERMISSIONS_COMMANDS_TPHERE_EXECUTE(""),
		PERMISSIONS_COMMANDS_TREE_EXECUTE(""),
		PERMISSIONS_COMMANDS_UUID_EXECUTE(""),
		PERMISSIONS_COMMANDS_UUID_OTHERS(""),
		PERMISSIONS_COMMANDS_VANISH_EXECUTE(""),
		PERMISSIONS_COMMANDS_VANISH_OTHERS(""),
		PERMISSIONS_COMMANDS_VANISH_SEE(""),
		PERMISSIONS_COMMANDS_VANISH_PVP(""),
		PERMISSIONS_COMMANDS_VANISH_INTERACT(""),
		PERMISSIONS_COMMANDS_WARP_EXECUTE(""),
		PERMISSIONS_COMMANDS_WARPS(""),
		PERMISSIONS_COMMANDS_WARP_OTHERS(""),
		PERMISSIONS_COMMANDS_DELWARP_EXECUTE(""),
		PERMISSIONS_COMMANDS_SETWARP_EXECUTE(""),
		PERMISSIONS_COMMANDS_WEATHER_EXECUTE(""),
		PERMISSIONS_COMMANDS_WHITELIST_EXECUTE(""),
		PERMISSIONS_COMMANDS_WHITELIST_MANAGE(""),
		PERMISSIONS_COMMANDS_WORLDBORDER_EXECUTE(""),
		PERMISSIONS_COMMANDS_WORLDS_EXECUTE(""),
		PERMISSIONS_COMMANDS_WORLDS_OTHERS(""),
		PERMISSIONS_COMMANDS_WHOIS_EXECUTE(""),
		PERMISSIONS_COMMANDS_WHOIS_OTHERS("");
		
		private final String path;
	    private final EMessageBuilder french;
	    private final EMessageBuilder english;
	    private EMessageFormat message;
	    private EMessageBuilder builder;
	    
	    private EEMessages(final String french) {   	
	    	this(EMessageFormat.builder().chat(new EFormatString(french), true));
	    }
	    
	    private EEMessages(final String french, final String english) {   	
	    	this(EMessageFormat.builder().chat(new EFormatString(french), true), 
	    		EMessageFormat.builder().chat(new EFormatString(english), true));
	    }
	    
	    private EEMessages(final EMessageBuilder french) {   	
	    	this(french, french);
	    }
	    
	    private EEMessages(final EMessageBuilder french, final EMessageBuilder english) {
	    	Preconditions.checkNotNull(french, "Le message '" + this.name() + "' n'est pas définit");
	    	
	    	this.path = this.resolvePath();	    	
	    	this.french = french;
	    	this.english = english;
	    	this.message = french.build();
	    }

	    public String getName() {
			return this.name();
		}
	    
		public String getPath() {
			return this.path;
		}

		public EMessageBuilder getFrench() {
			return this.french;
		}

		public EMessageBuilder getEnglish() {
			return this.english;
		}
		
		public EMessageFormat getMessage() {
			return this.message;
		}
		
		public EMessageBuilder getBuilder() {
			return this.builder;
		}
		
		public void set(EMessageBuilder message) {
			this.message = message.build();
			this.builder = message;
		}
	}
}
