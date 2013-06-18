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

package be.ac.umons.info.routing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import traul.ranked.nta.rules.BasicRule;
import traul.ranked.nta.rules.IRule;
import be.ac.umons.info.routing.actions.IAction;
import be.ac.umons.info.routing.automata.ActionAlphabet;
import be.ac.umons.info.routing.automata.FilterAutomaton;
import be.ac.umons.info.routing.automata.FilterState;
import be.ac.umons.info.routing.automata.LabelPair;
import be.ac.umons.info.routing.automata.QuasiRoutes;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import be.ac.umons.info.routing.predicates.IPredicate;

/**
 * This filter rule is a special one. It changes all "acc" values to "mod",
 * and does not change other parts of the routes.
 * 
 * It is intended to be used when chaining filters, to allow accepted routes
 * to be modified again.
 */
public class ModFilterRule implements IFilterRule {

	/**
	 * Returns the predicate of this rule.
	 * Here, this does not make sense, so we return null.
	 */
	public IPredicate getPredicate() {
		return null;
	}

	/**
	 * Returns the actions of this rule.
	 * Here, this does not make sense, so we return null.
	 */
	public List<IAction> getActions() {
		return null;
	}

	@Override
	public FilterAutomaton automaton(final RouteAlphabet routeAlphabet) {
		
		// states
		Set<FilterState> states = new HashSet<FilterState>();
		final FilterState q0 = new FilterState("q0");
		final FilterState q1 = new FilterState("q1");
		final FilterState qOK = new FilterState("qOK");
		final FilterState sink = new FilterState("sink");
		states.add(q0);
		states.add(q1);
		states.add(qOK);
		states.add(sink);		
		// final states
		Set<FilterState> finalStates = new HashSet<FilterState>();
		finalStates.add(qOK);

		// pairs (t,t) of quasi-routes on all branches except MOD
		final Set<IRule<LabelPair,FilterState>> rulesDest =
			QuasiRoutes.destPairBranch(routeAlphabet, q0);
		final Set<IRule<LabelPair,FilterState>> rulesPath =
			QuasiRoutes.pathPairBranch(routeAlphabet, q0);
		final Set<IRule<LabelPair,FilterState>> rulesPref =
			QuasiRoutes.prefPairBranch(routeAlphabet, q0);
		final Set<IRule<LabelPair,FilterState>> rulesCom =
			QuasiRoutes.comPairBranch(routeAlphabet, q0);
		
		// rules in MOD branch
		Set<IRule<LabelPair,FilterState>> rulesMod =
			new HashSet<IRule<LabelPair,FilterState>>();
		rulesMod.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.ACCMOD, q1));
		rulesMod.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.MODMOD, q1));
		rulesMod.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.REJREJ, q1));
		
		// rules at the root
		Set<IRule<LabelPair,FilterState>> rulesRoot =
			new HashSet<IRule<LabelPair,FilterState>>();
		rulesRoot.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.RR, qOK, q0, q0, q0, q0, q1));
		
		// automaton
		final ActionAlphabet actionAlphabet = 
				new ActionAlphabet(routeAlphabet, true);
		return new FilterAutomaton(
			actionAlphabet, states, finalStates, sink,
			rulesDest, rulesPath, rulesPref, rulesCom, rulesMod, rulesRoot);
	}

	@Override
	public RouteAlphabet filterAlphabet(RouteAlphabet previousAlphabet) {
		return previousAlphabet;
	}
}
