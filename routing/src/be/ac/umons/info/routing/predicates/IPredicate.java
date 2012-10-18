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

package be.ac.umons.info.routing.predicates;

import be.ac.umons.info.routing.automata.PredicateAutomaton;
import be.ac.umons.info.routing.automata.RouteAlphabet;

/**
 * Interface for predicates.
 */
public interface IPredicate {
	
	/**
	 * Returns the automaton recognizing the language of this predicate.
	 */
	public PredicateAutomaton automaton(RouteAlphabet routeAlphabet);
	
	/**
	 * Returns the set of symbols to be included in the alphabet. More 
	 * precisely, the RouteAlphabet is the set of constant values in predicates
	 * (except for the pref branch) and actions, while the Set<IntegerLabel> is
	 * the set of values in this predicate and related to the pref-branch.
	 */
	public RouteAlphabet filterAlphabet();

	/**
	 * Number of atomic predicates contained in this predicate.
	 */
	public int size();

}
