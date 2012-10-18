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

package be.ac.umons.info.routing;

import java.util.List;

import be.ac.umons.info.routing.actions.IAction;
import be.ac.umons.info.routing.automata.FilterAutomaton;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import be.ac.umons.info.routing.predicates.IPredicate;

/**
 * Interface for the rule of a filter.
 */
public interface IFilterRule {

	/**
	 * Returns the predicate of this rule.
	 */
	public IPredicate getPredicate();

	/**
	 * Returns the actions of this rule.
	 */
	public List<IAction> getActions();

	/**
	 * Builds the automaton recognizing this rule's language. It is obtained
	 * by computing automata for its predicate and action, and then combine
	 * them in the right way.
	 */
	public FilterAutomaton automaton(RouteAlphabet filterRouteAlphabet);

	/**
	 * Returns the set of labels used in the filter.
	 * @param previousAlphabet the alphabet for previous rules of this filter
	 * @return the set of labels used in the filter.
	 */
	public RouteAlphabet filterAlphabet(RouteAlphabet previousAlphabet);
}
