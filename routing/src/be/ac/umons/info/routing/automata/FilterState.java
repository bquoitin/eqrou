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

import traul.ranked.nta.states.BasicState;
import traul.ranked.nta.states.IState;

/**
 * A FilterState is a state used in FilterAutomaton. It usually encapsulate
 * one or two BasicState.
 */
public class FilterState implements IState {

	private final IState leftState, rightState;
	
	/**
	 * Constructor
	 */
	public FilterState(IState leftState, IState rightState) {
		this.leftState = leftState;
		this.rightState = rightState;
	}
	
	/**
	 * Constructor
	 */
	public FilterState(IState state) {
		this.leftState = null;
		this.rightState = state;
	}
	
	/**
	 * Constructor, building an inner BasicState
	 */
	public FilterState(final String name) {
		this.leftState = null;
		this.rightState = new BasicState(name);
	}
	
	/**
	 * Returns the rightmost state
	 */
	public IState getRightState() {
		return rightState;
	}

	/**
	 * Returns the leftmost state
	 */
	public IState getLeftState() {
		return leftState;
	}
	
	@Override
	public String toString() {
		if(leftState!=null)
			return "<"+leftState.toString()+", "+ rightState.toString()+">";
		else return rightState.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		FilterState other = (FilterState) obj;
		if (leftState!=null) {
			if (!this.leftState.equals(other.getLeftState())) {
				return false;
			}
		} else {
			if (other.getLeftState()!=null) {
				return false;
			}
		}
		if (rightState!=null) {
			if (!this.rightState.equals(other.getRightState())) {
				return false;
			}
		} else {
			if (other.getRightState()!=null) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((leftState == null) ? 0 : leftState.hashCode());
		result = prime * result + ((rightState == null) ? 0 : rightState.hashCode());
		return result;
	}

}
