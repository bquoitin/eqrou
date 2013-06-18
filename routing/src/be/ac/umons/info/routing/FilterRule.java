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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import traul.ranked.nta.labels.ILabel;
import traul.ranked.nta.rules.BasicRule;
import traul.ranked.nta.rules.IRule;
import traul.ranked.nta.states.IState;
import be.ac.umons.info.routing.actions.IAction;
import be.ac.umons.info.routing.actions.atomic.Accept;
import be.ac.umons.info.routing.actions.atomic.PrefAdd;
import be.ac.umons.info.routing.actions.atomic.PrefSet;
import be.ac.umons.info.routing.actions.atomic.PrefSub;
import be.ac.umons.info.routing.actions.atomic.Reject;
import be.ac.umons.info.routing.automata.ActionAlphabet;
import be.ac.umons.info.routing.automata.FilterAutomaton;
import be.ac.umons.info.routing.automata.FilterState;
import be.ac.umons.info.routing.automata.IntegerLabel;
import be.ac.umons.info.routing.automata.LabelPair;
import be.ac.umons.info.routing.automata.PredicateAutomaton;
import be.ac.umons.info.routing.automata.QuasiRoutes;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import be.ac.umons.info.routing.predicates.IPredicate;

/**
 * A rule of a filter.
 */
public class FilterRule implements IFilterRule {
	
	private final List<IAction> actions;
	private final IPredicate predicate;
	
	/**
	 * Constructor
	 */
	public FilterRule(IPredicate predicate, List<IAction> actions) {
		this.actions = actionsPreprocessing(actions);
		this.predicate = predicate;
	}

	/**
	 * Returns the predicate of this rule.
	 */
	@Override
	public IPredicate getPredicate() {
		return this.predicate;
	}

	/**
	 * When an Accept or Reject action is reached in a series of actions, the
	 * remaining part can be forgotten. Moreover, when a prefChange is 
	 * performed, all previous actions related to pref can be removed.
	 */
	private List<IAction> actionsPreprocessing(final List<IAction> actions) {
		List<IAction> newActions = new ArrayList<IAction>();
		int i = 0;
		int lastPrefChange = -1;
		boolean stop = false;
		// stop when action is accept or reject
		while (actions!=null && i<actions.size() && !stop) {
			final IAction action = actions.get(i);
			newActions.add(action);
			stop |= (action instanceof Accept);
			stop |= (action instanceof Reject);
			if (action instanceof PrefSet) {
				lastPrefChange = i;
			}
			i++;
		}
		// if there's a PrefChange action, remove previous pref-related actions
		if (lastPrefChange!=-1) {
			newActions = new ArrayList<IAction>();
			for (i=0; i<actions.size(); i++) {
				final IAction action = actions.get(i);
				if (i >= lastPrefChange ||
					(!(action instanceof PrefSet) &&
					!(action instanceof PrefAdd) &&
					!(action instanceof PrefSub))) {
						newActions.add(action);
				}
			}
		}
		return newActions;
	}
	
	/**
	 * Builds the automaton recognizing this rule's language. It is obtained
	 * by computing automata for its predicate and action, and then combine
	 * them in the right way.
	 */
	public FilterAutomaton automaton(final RouteAlphabet filterRouteAlphabet) {
		
		final FilterAutomaton actionAutomaton = 
				convertToFilterAlphabet(
					transformActionAutomaton(
						this.actionAutomaton(filterRouteAlphabet), 
						filterRouteAlphabet), 
					new ActionAlphabet(filterRouteAlphabet));
			
		FilterAutomaton result = actionAutomaton;

		if (this.predicate != null) {

			final FilterAutomaton predicateAutomaton =
				transformPredicateAutomaton(filterRouteAlphabet);
			result = actionAutomaton.compose(predicateAutomaton);
		}		
		return result;
	}

	/**
	 * Returns the automaton for the action of this rule.
	 */
	private FilterAutomaton actionAutomaton(
			final RouteAlphabet filterRouteAlphabet) {
		// compute the alphabet: filter alphabet + internal alphabet
		final RouteAlphabet internalAlphabet = 
			internalAlphabet(filterRouteAlphabet);
		final ActionAlphabet actionAlphabet = 
			new ActionAlphabet(internalAlphabet);
		// compute the automaton
		FilterAutomaton actionAutomaton = 
			actions.get(0).automaton(internalAlphabet, actionAlphabet);
		for (int i=1; i<actions.size(); i++) {
			actionAutomaton = 
				actions.get(i).automaton(internalAlphabet, actionAlphabet)
				.compose(actionAutomaton);
		}
		return actionAutomaton.cleanInaccessibleStates();
	}
	
	private static FilterAutomaton convertToFilterAlphabet(
			final FilterAutomaton actionAutomaton,
			final ActionAlphabet actionAlphabet) {
		return new FilterAutomaton(actionAlphabet, 
			actionAutomaton.getStates(), actionAutomaton.getFinalStates(), 
			actionAutomaton.sinkState(), 
			removeInternalRules(actionAutomaton.getRulesDest(), actionAlphabet),
			removeInternalRules(actionAutomaton.getRulesPath(), actionAlphabet),
			removeInternalRules(actionAutomaton.getRulesPref(), actionAlphabet),
			removeInternalRules(actionAutomaton.getRulesCom(), actionAlphabet),
			removeInternalRules(actionAutomaton.getRulesMod(), actionAlphabet),
			removeInternalRules(actionAutomaton.getRulesRoot(), actionAlphabet));
	}
	
	
	private static Set<IRule<LabelPair, FilterState>> removeInternalRules(
			final Set<IRule<LabelPair, FilterState>> oldRules, 
			final ActionAlphabet actionAlphabet) {
		Set<IRule<LabelPair, FilterState>> rules = 
			new HashSet<IRule<LabelPair,FilterState>>();
		final Set<LabelPair> filterLabels = 
			actionAlphabet.getSymbols().keySet();
		for (IRule<LabelPair, FilterState> oldRule : oldRules) {
			if (filterLabels.contains(oldRule.label())) {
				rules.add(oldRule);
			}
		}
		return rules;
	}
	
	private RouteAlphabet internalAlphabet(final RouteAlphabet actionAlphabet){
		RouteAlphabet internalAlphabet = 
			actions.get(0).internalAlphabet(actionAlphabet);
		for (int i=1; i<actions.size(); i++) {
			internalAlphabet = 
				actions.get(i).internalAlphabet(internalAlphabet);
		}
		return internalAlphabet;
	}
	
	/**
	 * Returns the list of actions of this filter.
	 */
	@Override
	public List<IAction> getActions() {
		return Collections.unmodifiableList(actions);
	}
	
	/**
	 * Returns the set of labels used in the filter.
	 * @param previousAlphabet the alphabet for previous rules of this filter
	 * @return the set of labels used in the filter.
	 */
	public RouteAlphabet filterAlphabet(final RouteAlphabet previousAlphabet) {

		RouteAlphabet alphabet = new RouteAlphabet(
			previousAlphabet.destAlphabet(), previousAlphabet.asPathAlphabet(),
			previousAlphabet.prefAlphabet(), previousAlphabet.comAlphabet());
		// predicates
		if (this.predicate != null) {
			alphabet = alphabet.union(this.predicate.filterAlphabet());
		}
		// actions
		for (IAction action : getActions()) {
			alphabet = alphabet.union(action.filterAlphabet());
		}
		// local preference values
		Set<IntegerLabel> localPrefPossibleValues = 
			new HashSet<IntegerLabel>();
		for (int inputLP : previousAlphabet.prefAlphabetInt()) {
			localPrefPossibleValues.add(
				new IntegerLabel(this.localPrefImage(inputLP)));
		}
		localPrefPossibleValues.addAll(previousAlphabet.prefAlphabet());
		return new RouteAlphabet(
			alphabet.destAlphabet(), alphabet.asPathAlphabet(),
			localPrefPossibleValues, alphabet.comAlphabet());
	}
	
	/**
	 * Returns the output local pref value, for a given input local pref value.
	 */
	public int localPrefImage(final int inputLocalPref) {
		int lpValue  = inputLocalPref;
		for (IAction action : this.actions) {
			lpValue = action.localPrefImage(lpValue);
		}
		return lpValue;
	}

	/**
	 * Changes the predicate automaton, such that it recognizes pairs (t,t) if
	 * route t satisfies the predicate, and (t,t-bar) otherwise.
	 */
	private FilterAutomaton transformPredicateAutomaton(
			final RouteAlphabet routeAlphabet){
		
		final ActionAlphabet actionAlphabet = 
			new ActionAlphabet(routeAlphabet);
		final PredicateAutomaton predicateAutomaton = 
			this.predicate.automaton(routeAlphabet);
		// states
		Set<FilterState> newStates = new HashSet<FilterState>();
		for (IState state : predicateAutomaton.getStates()) {
			newStates.add(new FilterState(state));
		}
		// final states
		Set<FilterState> finalStates = new HashSet<FilterState>();
		for (IState state : predicateAutomaton.getFinalStates()) {
			finalStates.add(new FilterState(state));
		}
		final FilterState finalState = finalStates.iterator().next();
		// root rules
		Set<IRule<LabelPair,FilterState>> rootRules = 
			new HashSet<IRule<LabelPair,FilterState>>();
		for (IRule<ILabel,IState> rule : predicateAutomaton.getRulesRoot()) {
			final FilterState rightState = new FilterState(rule.rightState());
			if (finalStates.contains(rightState)) {
				rootRules.add(new BasicRule<LabelPair,FilterState>(
					convertToFilterStates(rule.leftStates()),
					new LabelPair(rule.label(), rule.label()),
					rightState));
			} else {
				rootRules.add(new BasicRule<LabelPair,FilterState>(
					convertToFilterStates(rule.leftStates()),
					new LabelPair(rule.label(), RouteAlphabet.R_BAR),
					new FilterState(finalState)));
			}
		}
		return new FilterAutomaton(actionAlphabet, newStates, finalStates, 
			new FilterState(predicateAutomaton.sinkState()),
			duplicateLabelsForBranch(predicateAutomaton.getRulesDest()),
			duplicateLabelsForBranch(predicateAutomaton.getRulesPath()),
			duplicateLabelsForBranch(predicateAutomaton.getRulesPref()),
			duplicateLabelsForBranch(predicateAutomaton.getRulesCom()),
			duplicateLabelsForBranch(predicateAutomaton.getRulesMod()),
			rootRules);
	}

	/**
	 * Returns the same set of rules, except that each label l is replaced by a
	 * pair (l,l) and states are converted to FilterState.
	 */
	private Set<IRule<LabelPair,FilterState>> duplicateLabelsForBranch(
			final Set<IRule<ILabel,IState>> rules) {
		Set<IRule<LabelPair,FilterState>> newRules =
			new HashSet<IRule<LabelPair,FilterState>>();
		for (IRule<ILabel,IState> rule : rules) {
			newRules.add(new BasicRule<LabelPair,FilterState>(
				convertToFilterStates(rule.leftStates()), 
				new LabelPair(rule.label(), rule.label()),
				new FilterState(rule.rightState())));
		}
		return newRules;
	}
	
	/**
	 * Encapsulates each state of a list inside a FilterState.
	 */
	private List<FilterState> convertToFilterStates(final List<IState> states){
		List<FilterState> filterStates = new ArrayList<FilterState>();
		for (IState state : states) {
			filterStates.add(new FilterState(state));
		}
		return filterStates;
	}
	
	/**
	 * Transforms the action automaton, such that it recognizes all pairs
	 * (t,t') where t' is the image of t by the action, plus all pairs 
	 * (t-bar,t).
	 */
	private FilterAutomaton transformActionAutomaton(
			final FilterAutomaton actionAut,
			final RouteAlphabet routeAlphabet) {

		final ActionAlphabet actionAlphabet = actionAut.getActionAlphabet();
		final FilterAutomaton tbart = tbart(routeAlphabet, actionAlphabet);
		// compute the union of these two automata
		Set<FilterState> states = new HashSet<FilterState>();
		states.addAll(actionAut.getStates());
		states.addAll(tbart.getStates());
		Set<FilterState> finalStates = new HashSet<FilterState>();
		finalStates.addAll(actionAut.getFinalStates());
		finalStates.addAll(tbart.getFinalStates());
		return new FilterAutomaton(
			actionAlphabet, states, finalStates, actionAut.sinkState(), 
			unionOfRules(actionAut.getRulesDest(), tbart.getRulesDest()),
			unionOfRules(actionAut.getRulesPath(), tbart.getRulesPath()),
			unionOfRules(actionAut.getRulesPref(), tbart.getRulesPref()),
			unionOfRules(actionAut.getRulesCom(), tbart.getRulesCom()),
			unionOfRules(actionAut.getRulesMod(), tbart.getRulesMod()),
			unionOfRules(actionAut.getRulesRoot(), tbart.getRulesRoot()));
	}
	
	/**
	 * Returns the set of all overlays of pairs (t-bar,t) where t is a quasi
	 * route.
	 */
	private FilterAutomaton tbart(final RouteAlphabet routeAlphabet,
			final ActionAlphabet actionAlphabet) {
		final FilterAutomaton quasiRoutesPairs = 
			QuasiRoutes.quasiRoutePairsAutomaton(
				routeAlphabet, actionAlphabet);
		Set<IRule<LabelPair,FilterState>> newRootRules =
			new HashSet<IRule<LabelPair,FilterState>>();
		for (IRule<LabelPair,FilterState> rule : 
			quasiRoutesPairs.getRulesWithLabel(ActionAlphabet.RR, false)) {
			newRootRules.add(new BasicRule<LabelPair,FilterState>(
				rule.leftStates(), ActionAlphabet.RBAR_R, rule.rightState()));
		}
		return new FilterAutomaton(actionAlphabet, quasiRoutesPairs.getStates(),
			quasiRoutesPairs.getFinalStates(), quasiRoutesPairs.sinkState(), 
			quasiRoutesPairs.getRulesDest(), quasiRoutesPairs.getRulesPath(), 
			quasiRoutesPairs.getRulesPref(), quasiRoutesPairs.getRulesCom(), 
			quasiRoutesPairs.getRulesMod(), newRootRules);
	}
	
	/**
	 * Returns the union of two sets of rules.
	 */
	private Set<IRule<LabelPair,FilterState>> unionOfRules(
			final Set<IRule<LabelPair,FilterState>> rules1,
			final Set<IRule<LabelPair,FilterState>> rules2) {
		Set<IRule<LabelPair,FilterState>> rules =
			new HashSet<IRule<LabelPair,FilterState>>();
		rules.addAll(rules1);
		rules.addAll(rules2);
		return rules;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" Predicate: ");
		if (this.predicate==null) {
			sb.append("(no predicate)");
		} else {
			sb.append(this.predicate.toString());
		}
		sb.append("\n Action:\n");
		for (IAction action : this.actions) {
			sb.append("  ").append(action.toString()).append("\n");
		}
		return sb.toString();
	}
}
