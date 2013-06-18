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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import traul.ranked.nta.labels.ILabel;
import traul.ranked.nta.rules.BasicRule;
import traul.ranked.nta.rules.IRule;
import traul.ranked.nta.states.IState;

/**
 * This class provides the automata rules recognizing quasi-routes (resp. pairs
 * (t,t) of quasi-routes), grouped by branch.
 */
public class QuasiRoutes {

	/**
	 * Rules for the DEST branch of quasi-routes.
	 */
	public static Set<IRule<ILabel, IState>> destBranch(
		final RouteAlphabet alphabet, final IState state) {
		Set<IRule<ILabel,IState>> rules = new HashSet<IRule<ILabel,IState>>();
		rules.add(new BasicRule<ILabel,IState>(RouteAlphabet.DEST, state));
		for (IntegerLabel intLabel : alphabet.destAlphabet()) {
			final int i = intLabel.intValue();
			rules.add(new BasicRule<ILabel,IState>(
				RouteAlphabet.integer(i), state, state));
		}
		return rules;
	}

	/**
	 * Rules for the DEST branch of pairs (t,t) of quasi-routes t.
	 */
	public static Set<IRule<LabelPair, FilterState>> destPairBranch(
		final RouteAlphabet alphabet, final FilterState state) {
		Set<IRule<LabelPair,FilterState>> rules = 
			new HashSet<IRule<LabelPair,FilterState>>();
		rules.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.DESTDEST, state));
		for (IntegerLabel intLabel : alphabet.destAlphabet()) {
			final int i = intLabel.intValue();
			rules.add(new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.integerinteger(i, i), state, state));
		}
		return rules;
	}

	/**
	 * Rules for the AS-PATH branch of quasi-routes.
	 */
	public static Set<IRule<ILabel, IState>> pathBranch(
		final RouteAlphabet alphabet, final IState state) {
		Set<IRule<ILabel,IState>> rules = new HashSet<IRule<ILabel,IState>>();
		rules.add(new BasicRule<ILabel,IState>(RouteAlphabet.PATH, state));
		for (IntegerLabel intLabel : alphabet.asPathAlphabet()) {
			final int i = intLabel.intValue();
			rules.add(new BasicRule<ILabel,IState>(
				RouteAlphabet.integer(i), state, state));
		}
		return rules;
	}

	/**
	 * Rules for the AS-PATH branch of pairs (t,t) of quasi-routes t.
	 */
	public static Set<IRule<LabelPair, FilterState>> pathPairBranch(
		final RouteAlphabet alphabet, final FilterState state) {
		Set<IRule<LabelPair,FilterState>> rules = 
			new HashSet<IRule<LabelPair,FilterState>>();
		rules.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.PATHPATH, state));
		for (IntegerLabel intLabel : alphabet.asPathAlphabet()) {
			final int i = intLabel.intValue();
			rules.add(new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.integerinteger(i, i), state, state));
		}
		return rules;
	}

	/**
	 * Rules for the PREF branch of quasi-routes.
	 */
	public static Set<IRule<ILabel, IState>> prefBranch(
		final RouteAlphabet alphabet, final IState state) {
		Set<IRule<ILabel,IState>> rules = new HashSet<IRule<ILabel,IState>>();
		rules.add(new BasicRule<ILabel,IState>(RouteAlphabet.PREF, state));
		for (IntegerLabel intLabel : alphabet.prefAlphabet()) {
			final int i = intLabel.intValue();
			rules.add(new BasicRule<ILabel,IState>(
				RouteAlphabet.integer(i), state, state));
		}
		return rules;
	}

	/**
	 * Rules for the PREF branch of pairs (t,t) of quasi-routes t.
	 */
	public static Set<IRule<LabelPair, FilterState>> prefPairBranch(
		final RouteAlphabet alphabet, final FilterState state) {
		Set<IRule<LabelPair,FilterState>> rules = 
			new HashSet<IRule<LabelPair,FilterState>>();
		rules.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.PREFPREF, state));
		for (IntegerLabel intLabel : alphabet.prefAlphabet()) {
			final int i = intLabel.intValue();
			rules.add(new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.integerinteger(i, i), state, state));
		}
		return rules;
	}

	/**
	 * Rules for the COM branch of quasi-routes.
	 */
	public static Set<IRule<ILabel, IState>> comBranch(
		final RouteAlphabet alphabet, final IState state) {
		Set<IRule<ILabel,IState>> rules = new HashSet<IRule<ILabel,IState>>();
		rules.add(new BasicRule<ILabel,IState>(RouteAlphabet.COM, state));
		for (IntegerLabel intLabel : alphabet.comAlphabet()) {
			final int i = intLabel.intValue();
			rules.add(new BasicRule<ILabel,IState>(
				RouteAlphabet.integer(i), state, state));
		}
		return rules;
	}

	/**
	 * Rules for the COM branch of pairs (t,t) of quasi-routes t.
	 */
	public static Set<IRule<LabelPair, FilterState>> comPairBranch(
		final RouteAlphabet alphabet, final FilterState state) {
		Set<IRule<LabelPair,FilterState>> rules = 
			new HashSet<IRule<LabelPair,FilterState>>();
		rules.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.COMCOM, state));
		for (IntegerLabel intLabel : alphabet.comAlphabet()) {
			final int i = intLabel.intValue();
			rules.add(new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.integerinteger(i, i), state, state));
		}
		return rules;
	}

	/**
	 * Rules for the MOD branch of quasi-routes.
	 */
	public static Set<IRule<ILabel, IState>> modBranch(
		final RouteAlphabet alphabet, final IState state) {
		Set<IRule<ILabel,IState>> rules = new HashSet<IRule<ILabel,IState>>();
		rules.add(new BasicRule<ILabel,IState>(RouteAlphabet.MODIFIED, state));
		rules.add(new BasicRule<ILabel,IState>(RouteAlphabet.ACCEPTED, state));
		rules.add(new BasicRule<ILabel,IState>(RouteAlphabet.REJECTED, state));
		return rules;
	}

	/**
	 * Rules for the MOD branch of pairs (t,t) of quasi-routes t.
	 */
	public static Set<IRule<LabelPair, FilterState>> modPairBranch(
		final RouteAlphabet alphabet, 
		final FilterState qMod, final FilterState qFix) {
		Set<IRule<LabelPair,FilterState>> rules = 
			new HashSet<IRule<LabelPair,FilterState>>();
		rules.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.MODMOD, qMod));
		rules.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.ACCACC, qFix));
		rules.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.REJREJ, qFix));
		return rules;
	}

	/**
	 * Returns an automaton recognizing pairs (t,t) of valid routes.
	 */
	public static FilterAutomaton quasiRoutePairsAutomaton(
			final RouteAlphabet routeAlphabet,
			final ActionAlphabet actionAlphabet) {
		Set<FilterState> states = new HashSet<FilterState>();
		final FilterState state = new FilterState("q_id");
		final FilterState rootState = new FilterState("q_id_root");
		states.add(state);
		states.add(rootState);
		Set<IRule<LabelPair,FilterState>> rootRules =
			new HashSet<IRule<LabelPair,FilterState>>();
		rootRules.add(new BasicRule<LabelPair,FilterState>(ActionAlphabet.RR,
				rootState, state, state, state, state, state));
		return new FilterAutomaton(actionAlphabet, 
			states, Collections.singleton(rootState),
			new FilterState("sink"), 
			QuasiRoutes.destPairBranch(routeAlphabet, state), 
			QuasiRoutes.pathPairBranch(routeAlphabet, state), 
			QuasiRoutes.prefPairBranch(routeAlphabet, state),
			QuasiRoutes.comPairBranch(routeAlphabet, state),
			QuasiRoutes.modPairBranch(routeAlphabet, state, state),
			rootRules);
	}
}
