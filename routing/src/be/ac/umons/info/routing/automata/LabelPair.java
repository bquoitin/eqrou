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
 * A pair of labels, used in actions and rules.
 */
public class LabelPair implements ILabelPair {

	private final ILabel left, right;
	
	public LabelPair(ILabel left, ILabel right) {
		this.left = left;
		this.right = right;
	}
	
	/**
	 * Returns the left label.
	 * @return the first component of this label pair
	 */
	@Override
	public ILabel left() {
		return this.left;
	}

	/**
	 * Returns the right label.
	 * @return the second component of this label pair
	 */
	@Override
	public ILabel right() {
		return this.right;
	}
	
	@Override
	public String toString() {
		return "<"+left.toString()+","+right.toString()+">";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result+ ((left == null) ? 0 : left.hashCode());
        result = prime * result+ ((right == null) ? 0 : right.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LabelPair other = (LabelPair) obj;
		if(left.equals(other.left())&& right.equals(other.right())){
			return true;
		}
		else return false;
	}

}
