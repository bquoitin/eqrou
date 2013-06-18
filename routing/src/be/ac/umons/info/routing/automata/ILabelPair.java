/*
routing-equiv: testing the equivalence of routing policies
Copyright (C) 2013 routing-equiv team

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package be.ac.umons.info.routing.automata;

import traul.ranked.nta.labels.ILabel;

/**
 * A label composed by a pair of labels.
 */
public interface ILabelPair extends ILabel {

	/**
	 * Returns the left label.
	 * @return the first component of this label pair
	 */
	public ILabel left();
	
	/**
	 * Returns the right label.
	 * @return the second component of this label pair
	 */
	public ILabel right();	
}
