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

package be.ac.umons.info.routing.actions.atomic;

import java.util.HashSet;
import java.util.Set;

import traul.ranked.nta.rules.BasicRule;
import traul.ranked.nta.rules.IRule;
import be.ac.umons.info.routing.actions.IAction;
import be.ac.umons.info.routing.automata.ActionAlphabet;
import be.ac.umons.info.routing.automata.FilterAutomaton;
import be.ac.umons.info.routing.automata.FilterState;
import be.ac.umons.info.routing.automata.IntegerLabel;
import be.ac.umons.info.routing.automata.LabelPair;
import be.ac.umons.info.routing.automata.QuasiRoutes;
import be.ac.umons.info.routing.automata.RouteAlphabet;

public class ClearCommunities implements IAction {

	@Override
	public FilterAutomaton automaton(RouteAlphabet routeAlphabet,
			ActionAlphabet actionAlphabet) {
		// states
		Set<FilterState> states = new HashSet<FilterState>();
		final FilterState q0 = new FilterState("q0");
		final FilterState q1 = new FilterState("q1");
		final FilterState qMod = new FilterState("qMod");
		final FilterState qFix = new FilterState("qFix");		
		final FilterState sink = new FilterState("sink");
		states.add(q0);
		states.add(q1);
		states.add(qMod);
		states.add(qFix);
		states.add(sink);
		// final states
		Set<FilterState> finalStates = new HashSet<FilterState>();
		finalStates.add(q1);

		// pairs (t,t) of quasi-routes on all branches except COM
		final Set<IRule<LabelPair,FilterState>> rulesDest =
			QuasiRoutes.destPairBranch(routeAlphabet, q0);
		final Set<IRule<LabelPair,FilterState>> rulesPath =
			QuasiRoutes.pathPairBranch(routeAlphabet, q0);
		final Set<IRule<LabelPair,FilterState>> rulesPref =
			QuasiRoutes.prefPairBranch(routeAlphabet, q0);
		final Set<IRule<LabelPair,FilterState>> rulesMod =
			QuasiRoutes.modPairBranch(routeAlphabet, qMod, qFix);
		
		// rules in COM branch
		Set<IRule<LabelPair,FilterState>> rulesCom =
			new HashSet<IRule<LabelPair,FilterState>>();
		rulesCom.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.COMDIAMOND, q0));
		rulesCom.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.COMCOM, q1));
		for (int i : routeAlphabet.comAlphabetInt()) {
			rulesCom.add(new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.integerdiamond(i), q0, q0));
			rulesCom.add(new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.integercom(i), q1, q0));
		}
		
		// rules at the root
		Set<IRule<LabelPair,FilterState>> rulesRoot =
			new HashSet<IRule<LabelPair,FilterState>>();
		rulesRoot.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.RR, q1, q0, q0, q0, q1, qMod));
		rulesRoot.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.RR, q1, q0, q0, q0, q0, qFix));

		// automaton
		return new FilterAutomaton(
			actionAlphabet, states, finalStates, sink,
			rulesDest, rulesPath, rulesPref, rulesCom, rulesMod, rulesRoot);
	}

	/**
	 * Returns the set of labels to be included in the alphabet.
	 */
	@Override
	public RouteAlphabet filterAlphabet() {
		return new RouteAlphabet(new HashSet<IntegerLabel>(), 
				new HashSet<IntegerLabel>(), new HashSet<IntegerLabel>(), 
				new HashSet<IntegerLabel>());
	}

	/**
	 * Returns the set of labels used by the action. This may differ from 
	 * {@link IAction.filterAlphabet} because we may use values during the
	 * composition, that won't be used outside the action.
	 * @param alphabet alphabet already inferred before this action
	 */
	@Override
	public RouteAlphabet internalAlphabet(RouteAlphabet alphabet) {
		return alphabet;
	}

	/**
	 * Returns the output local pref value, for a given input local pref value.
	 */
	@Override
	public int localPrefImage(int inputLocalPref) {
		return inputLocalPref;
	}

	@Override
	public String toString() {
		return "Clear communities";
	}
}
