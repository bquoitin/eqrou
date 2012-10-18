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

package be.ac.umons.info.routing.predicates.operators;

import be.ac.umons.info.routing.automata.PredicateAutomaton;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import be.ac.umons.info.routing.predicates.IPredicate;

/**
 * Conjunction (AND) of two predicates.
 */
public class PredicateAnd implements IPredicate {

	private final IPredicate leftPredicate;
	private final IPredicate rightPredicate;
	
	/**
	 * Constructor.
	 */
	public PredicateAnd(final IPredicate leftPredicate, 
			final IPredicate rightPredicate) {
		this.leftPredicate = leftPredicate;
		this.rightPredicate = rightPredicate;
	}
	
	/**
	 * Automaton recognizing trees verifying this predicate.
	 */
	@Override
	public PredicateAutomaton automaton(final RouteAlphabet routeAlphabet) {
		return leftPredicate.automaton(routeAlphabet)
			.intersection(rightPredicate.automaton(routeAlphabet));
	}
	
	/**
	 * Returns the set of symbols to be included in the alphabet. More 
	 * precisely, the RouteAlphabet is the set of constant values in predicates
	 * (except for the pref branch) and actions, while the Set<IntegerLabel> is
	 * the set of values in this predicate and related to the pref-branch.
	 */
	@Override
	public RouteAlphabet filterAlphabet() {
		
		return this.leftPredicate().filterAlphabet().union(
				this.rightPredicate().filterAlphabet());
	}
	
	/**
	 * Returns the left predicate of this conjunction.
	 */
	public IPredicate leftPredicate() {
		return this.leftPredicate;
	}
	
	/**
	 * Returns the right predicate of this conjunction.
	 */
	public IPredicate rightPredicate() {
		return this.rightPredicate;
	}

	/**
	 * Number of atomic predicates contained in this predicate.
	 */
	@Override
	public int size() {
		return leftPredicate.size() + rightPredicate.size();
	}
	
	@Override
	public String toString() {
		return "(" + this.leftPredicate().toString() + " AND " 
			+ this.rightPredicate().toString() + ")";
	}
}
