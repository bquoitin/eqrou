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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import traul.ranked.nta.BinaryOperators;
import traul.ranked.nta.IBinaryOperators;
import traul.ranked.nta.INTA;
import be.ac.umons.info.routing.actions.IAction;
import be.ac.umons.info.routing.automata.FilterAutomaton;
import be.ac.umons.info.routing.automata.FilterState;
import be.ac.umons.info.routing.automata.IntegerLabel;
import be.ac.umons.info.routing.automata.LabelPair;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import traul.ranked.terms.ITerm;

/**
 * A routing filter.
 */
public class Filter {
	
	private final List<IFilterRule> filterRules;
	private final int initialLocalPref;
	public static final int DEFAULT_LOCAL_PREF = 100;
	
	/**
	 * Filter constructor.
	 * @param filterRules the list of filter rules of this filter
	 * @param initialLocalPref the local preference set to incoming routes 
	 * (default is 100)
	 */
	public Filter(final List<IFilterRule> filterRules, 
			final int initialLocalPref) {
		this.filterRules = filterRules;
		this.initialLocalPref = initialLocalPref;
	}
	
	/**
	 * Filter constructor, with input local preference set to 100.
	 * @param filterRules the list of filter rules of this filter
	 */
	public Filter(final List<IFilterRule> filterRules){
		this.filterRules = filterRules;
		this.initialLocalPref = DEFAULT_LOCAL_PREF;
	}
	
	/**
	 * Filter constructor, when then filter is made of only one filter rule,
	 * with no predicate, and a series of actions. Input local preference is
	 * set to 100.
	 * @param actions the list of actions of this filter
	 */
	public Filter(final IAction... actions) {
		List<IFilterRule> fRules = new ArrayList<IFilterRule>();
		fRules.add(new FilterRule(null, Arrays.asList(actions)));
		this.filterRules = Collections.unmodifiableList(fRules);
		this.initialLocalPref = DEFAULT_LOCAL_PREF;
	}

	/**
	 * Returns the filter rules of this filter.
	 */
	public List<IFilterRule> getRules() {
		return this.filterRules;
	}
	
	/**
	 * Builds the automaton recognizing this filter's language. This is just
	 * obtained by composing the automata of its rules, and then restrict to
	 * valid routes.
	 */
	public FilterAutomaton automaton(final RouteAlphabet routeAlphabet) {

		FilterAutomaton automaton = null;
		for (IFilterRule filterRule : this.filterRules){
			final FilterAutomaton ruleAutomaton = 
				filterRule.automaton(routeAlphabet);
			if (automaton==null) {
				automaton = ruleAutomaton;
			} else {
				automaton = ruleAutomaton.compose(automaton);
			}
		}
		return automaton.automatonForValidRoutes(routeAlphabet);
	}
	
	/**
	 * Checks the equivalence of two filters (main goal of this project).
	 * @param otherFilter the other filter to be compared with
	 * @return true iff both filters are equivalent
	 */
	public boolean equivalent(final Filter otherFilter) {
		final RouteAlphabet commonRouteAlphabet = 
			this.filterAlphabet().union(otherFilter.filterAlphabet());
		return this.automaton(commonRouteAlphabet).equivalent(
			otherFilter.automaton(commonRouteAlphabet));
	}
	
	/**
	 * Checks the equivalence of two filters (main goal of this project).
	 * Here we use the usual Boolean operations on automata in order to test
	 * their equivalence, ie we do not use the optimization specific to
	 * functional relations.
	 * @param otherFilter the other filter to be compared with
	 * @return true iff both filters are equivalent
	 */
	public boolean equivalentUsingBooleanOperations(final Filter otherFilter) {
		final RouteAlphabet alphabet = 
			this.filterAlphabet().union(otherFilter.filterAlphabet());
		final INTA<LabelPair,FilterState> auto1 = 
			this.automaton(alphabet).automatonForValidRoutes(alphabet);
		final INTA<LabelPair,FilterState> auto2 = 
			otherFilter.automaton(alphabet).automatonForValidRoutes(alphabet);
		IBinaryOperators<LabelPair, FilterState, FilterState> binOps1 =
			new BinaryOperators<LabelPair, FilterState, FilterState>();
		return binOps1.equivalent(auto1, auto2);
	}
	
	/**
	 * Returns a route which is treated differently by the two filters, or null
	 * when these two filters are equivalent. More precisely, the computed
	 * route is given with its transformation by "this" filter, and we are
	 * guaranteed that its transformation by the "otherFilter" differs.
	 */
	public ITerm<LabelPair> separatingRoute(final Filter otherFilter) {
		final RouteAlphabet commonRouteAlphabet = 
			this.filterAlphabet().union(otherFilter.filterAlphabet());
		return this.automaton(commonRouteAlphabet)
			.separationRoute(otherFilter.automaton(commonRouteAlphabet));
	}

	/**
	 * Computes the alphabet of this filter.
	 */
	public RouteAlphabet filterAlphabet() {
		final Set<IntegerLabel> initPrefAlphabet =
			Collections.singleton(new IntegerLabel(this.initialLocalPref));
		RouteAlphabet alphabet = new RouteAlphabet(
			new HashSet<IntegerLabel>(), new HashSet<IntegerLabel>(),
			initPrefAlphabet, new HashSet<IntegerLabel>());
		for (final IFilterRule rule : this.filterRules) {
			alphabet = alphabet.union(rule.filterAlphabet(alphabet));
		}
		return alphabet;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i<this.filterRules.size(); i++) {
			sb.append("Rule ").append(i).append(":\n");
			sb.append(this.filterRules.get(i).toString());
		}
		return sb.toString();
	}
}
