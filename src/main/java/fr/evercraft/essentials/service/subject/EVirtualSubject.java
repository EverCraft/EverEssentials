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
package fr.evercraft.essentials.service.subject;

import java.util.Optional;

import fr.evercraft.everapi.services.essentials.SubjectVirtualEssentials;

public class EVirtualSubject implements SubjectVirtualEssentials {
	
	private Optional<String> replyTo;

	public EVirtualSubject() {
		this.replyTo = Optional.empty();
	}
	
	/*
	 * ReplyTo
	 */
	
	@Override
	public boolean setReplyTo(String identifier) {
		this.replyTo = Optional.ofNullable(identifier);
		return true;
	}

	@Override
	public Optional<String> getReplyTo() {
		return this.replyTo;
	}
}
