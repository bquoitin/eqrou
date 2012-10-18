/*
routing-equiv: testing the equivalence of routing policies
Copyright (C) 2011 routing-equiv team

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

package be.ac.umons.info.routing.actions;

import be.ac.umons.info.routing.automata.ActionAlphabet;
import be.ac.umons.info.routing.automata.FilterAutomaton;
import be.ac.umons.info.routing.automata.RouteAlphabet;

/**
 * Interface for actions. An action can be either atomic, or a composition of
 * actions.
 */
public interface IAction {

	/**
	 * Returns the automaton recognizing the language of this action.
	 */
	public FilterAutomaton automaton(RouteAlphabet routeAlphabet,
			ActionAlphabet actionAlphabet);
	
	/**
	 * Returns the set of labels to be included in the filter's alphabet.
	 */
	public RouteAlphabet filterAlphabet();
	
	/**
	 * Returns the set of labels used by the action. This may differ from 
	 * {@link IAction.filterAlphabet} because we may use values during the
	 * composition, that won't be used outside the action.
	 * @param alphabet alphabet already inferred before this action
	 */
	public RouteAlphabet internalAlphabet(RouteAlphabet alphabet);
	
	/**
	 * Returns the output local pref value, for a given input local pref value.
	 */
	public int localPrefImage(int inputLocalPref);
}
