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

package be.ac.umons.info.routing.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import be.ac.umons.info.routing.Filter;
import be.ac.umons.info.routing.FilterRule;
import be.ac.umons.info.routing.IFilterRule;
import be.ac.umons.info.routing.actions.IAction;
import be.ac.umons.info.routing.actions.atomic.Accept;
import be.ac.umons.info.routing.actions.atomic.ComAdd;
import be.ac.umons.info.routing.actions.atomic.ComRemove;
import be.ac.umons.info.routing.actions.atomic.PathPrepend;
import be.ac.umons.info.routing.automata.ActionAlphabet;
import be.ac.umons.info.routing.automata.FilterAutomaton;
import be.ac.umons.info.routing.automata.LabelPair;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import traul.ranked.terms.ITerm;

public class FilterTest extends TestCase {
	
	/**
	 * Tests a filter with a single rule, made of a series of actions ended by
	 * accept.
	 */
	public void testFilterListActions1() {
		
		List<IAction> actions = new ArrayList<IAction>();
		actions.add(new PathPrepend(30));
		actions.add(new ComAdd(90));
		actions.add(new ComAdd(80));
		actions.add(new PathPrepend(40));
		actions.add(new PathPrepend(50));
		actions.add(new ComAdd(30));
		actions.add(new ComRemove(50));
		actions.add(new Accept());
		
		IFilterRule filterRule = new FilterRule(null, actions);
		List<IFilterRule> filterRules = Collections.singletonList(filterRule);
		Filter filter = new Filter(filterRules);
		
		// alphabet extended by additional actions
		List<IAction> extendedActions = new ArrayList<IAction>();
		extendedActions.add(new PathPrepend(20));
		extendedActions.addAll(actions);
		RouteAlphabet alphabet = 
			FilterProvider.alphabetForActions(extendedActions);

		FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.integerinteger(50, 50));
		pathList.add(ActionAlphabet.integerinteger(20, 20));
		pathList.add(ActionAlphabet.integerinteger(30, 30));
		pathList.add(ActionAlphabet.pathinteger(30));
		pathList.add(ActionAlphabet.diamondinteger(40));
		pathList.add(ActionAlphabet.diamondinteger(50));
		pathList.add(ActionAlphabet.DIAMONDPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.integerinteger(50, 30));
		comList.add(ActionAlphabet.cominteger(80));
		comList.add(ActionAlphabet.diamondinteger(90));
		comList.add(ActionAlphabet.DIAMONDCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODACC;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
			destList, pathList, prefVal, comList, acceptVal,
			new ActionAlphabet(alphabet));
		
		assertTrue("The filter automaton should accept the routing tree.",
				automaton.accepts(tree));

		//pathList2
		List<LabelPair> pathList2 = new ArrayList<LabelPair>();
		pathList2.add(ActionAlphabet.integerinteger(50, 50));
		pathList2.add(ActionAlphabet.integerinteger(20, 20));
		pathList2.add(ActionAlphabet.integerinteger(30, 30));
		pathList2.add(ActionAlphabet.pathinteger(30));
		pathList2.add(ActionAlphabet.diamondinteger(50));
		pathList2.add(ActionAlphabet.DIAMONDPATH);
		
		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
			destList, pathList2, prefVal, comList, acceptVal,
			new ActionAlphabet(alphabet));
		
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree2));
	}
	
	/**
	 * Tests a filter with a single rule, made of a series of actions with
	 * accept in the middle.
	 */
	public void testFilterListActions2() {

		List<IAction> actions = new ArrayList<IAction>();
		actions.add(new PathPrepend(30));
		actions.add(new ComAdd(90));
		actions.add(new ComAdd(80));
		actions.add(new PathPrepend(40));
		actions.add(new Accept());
		actions.add(new PathPrepend(50));
		actions.add(new ComAdd(30));
		actions.add(new ComRemove(50));		
		
		IFilterRule filterRule = new FilterRule(null, actions);
		List<IFilterRule> filterRules = new ArrayList<IFilterRule>();
		filterRules.add(filterRule);
		Filter filter = new Filter(filterRules);

		// alphabet extended by additional actions
		List<IAction> extendedActions = new ArrayList<IAction>();
		extendedActions.add(new PathPrepend(20));
		extendedActions.add(new PathPrepend(50));
		extendedActions.add(new ComAdd(50));		
		extendedActions.addAll(actions);
		RouteAlphabet alphabet = 
			FilterProvider.alphabetForActions(extendedActions);

		FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.integerinteger(50, 50));
		pathList.add(ActionAlphabet.integerinteger(20, 20));
		pathList.add(ActionAlphabet.integerinteger(30, 30));
		pathList.add(ActionAlphabet.pathinteger(30));
		pathList.add(ActionAlphabet.diamondinteger(40));
		pathList.add(ActionAlphabet.DIAMONDPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.integerinteger(50, 50));
		comList.add(ActionAlphabet.cominteger(80));
		comList.add(ActionAlphabet.diamondinteger(90));
		comList.add(ActionAlphabet.DIAMONDCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODACC;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The filter automaton should accept the routing tree.",
				automaton.accepts(tree));

		//pathList2
		List<LabelPair> pathList2 = new ArrayList<LabelPair>();
		pathList2.add(ActionAlphabet.integerinteger(50, 50));
		pathList2.add(ActionAlphabet.integerinteger(20, 20));
		pathList2.add(ActionAlphabet.integerinteger(30, 30));
		pathList2.add(ActionAlphabet.pathinteger(30));
		pathList2.add(ActionAlphabet.DIAMONDPATH);
		
		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
				destList, pathList2, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree2));
}
	
	/**
	 * Tests a filter with two rules, made of a series of actions with
	 * accept at the end of the second rule.
	 */
	public void testFilterListActions3() {
		
		List<IAction> actions1 = new ArrayList<IAction>();
		actions1.add(new PathPrepend(30));
		actions1.add(new ComAdd(90));
		actions1.add(new ComAdd(80));
		actions1.add(new PathPrepend(40));
		
		List<IAction> actions2 = new ArrayList<IAction>();
		actions2.add(new PathPrepend(50));
		actions2.add(new ComAdd(30));
		actions2.add(new ComRemove(50));
		actions2.add(new Accept());
		
		IFilterRule filterRule1 = new FilterRule(null, actions1);
		IFilterRule filterRule2 = new FilterRule(null, actions2);
		
		List<IFilterRule> filterRules = new ArrayList<IFilterRule>();
		filterRules.add(filterRule1);
		filterRules.add(filterRule2);
		Filter filter = new Filter(filterRules);

		// alphabet extended by additional actions
		List<IAction> extendedActions = new ArrayList<IAction>();
		extendedActions.add(new PathPrepend(20));
		extendedActions.addAll(actions1);
		extendedActions.addAll(actions2);
		RouteAlphabet alphabet = 
			FilterProvider.alphabetForActions(extendedActions);

		FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.integerinteger(50, 50));
		pathList.add(ActionAlphabet.integerinteger(20, 20));
		pathList.add(ActionAlphabet.integerinteger(30, 30));
		pathList.add(ActionAlphabet.pathinteger(30));
		pathList.add(ActionAlphabet.diamondinteger(40));
		pathList.add(ActionAlphabet.diamondinteger(50));
		pathList.add(ActionAlphabet.DIAMONDPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.integerinteger(50, 30));
		comList.add(ActionAlphabet.cominteger(80));
		comList.add(ActionAlphabet.diamondinteger(90));
		comList.add(ActionAlphabet.DIAMONDCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODACC;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The filter automaton should accept the routing tree.",
				automaton.accepts(tree));
		
		//comList2
		List<LabelPair> comList2 = new ArrayList<LabelPair>();
		comList2.add(ActionAlphabet.integerinteger(50, 30));
		comList2.add(ActionAlphabet.cominteger(50));
		comList2.add(ActionAlphabet.cominteger(80));
		comList2.add(ActionAlphabet.diamondinteger(90));
		comList2.add(ActionAlphabet.DIAMONDCOM);
		
		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList2, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree2));
	}
	
	/**
	 * Tests a filter with two rules, made of a series of actions with
	 * accept at the end of the first rule.
	 */
	public void testFilterListActions4() {

		List<IAction> actions1 = new ArrayList<IAction>();
		actions1.add(new PathPrepend(30));
		actions1.add(new ComAdd(90));
		actions1.add(new ComAdd(80));
		actions1.add(new PathPrepend(40));
		actions1.add(new Accept());
		
		List<IAction> actions2 = new ArrayList<IAction>();
		actions2.add(new PathPrepend(50));
		actions2.add(new ComAdd(30));
		actions2.add(new ComRemove(50));		
		
		IFilterRule filterRule1 = new FilterRule(null, actions1);
		IFilterRule filterRule2 = new FilterRule(null, actions2);
		
		List<IFilterRule> filterRules = new ArrayList<IFilterRule>();
		filterRules.add(filterRule1);
		filterRules.add(filterRule2);
		Filter filter = new Filter(filterRules);
		
		// alphabet extended by additional actions
		List<IAction> extendedActions = new ArrayList<IAction>();
		extendedActions.add(new PathPrepend(20));
		extendedActions.add(new PathPrepend(50));
		extendedActions.add(new ComAdd(30));
		extendedActions.add(new ComAdd(50));
		extendedActions.addAll(actions1);
		extendedActions.addAll(actions2);
		RouteAlphabet alphabet = 
			FilterProvider.alphabetForActions(extendedActions);

		FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.integerinteger(50, 50));
		pathList.add(ActionAlphabet.integerinteger(20, 20));
		pathList.add(ActionAlphabet.integerinteger(30, 30));
		pathList.add(ActionAlphabet.pathinteger(30));
		pathList.add(ActionAlphabet.diamondinteger(40));
		pathList.add(ActionAlphabet.DIAMONDPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.integerinteger(50, 50));
		comList.add(ActionAlphabet.cominteger(80));
		comList.add(ActionAlphabet.diamondinteger(90));
		comList.add(ActionAlphabet.DIAMONDCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODACC;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The filter automaton should accept the routing tree.",
				automaton.accepts(tree));

		//pathList2
		List<LabelPair> pathList2 = new ArrayList<LabelPair>();
		pathList2.add(ActionAlphabet.integerinteger(20, 20));
		pathList2.add(ActionAlphabet.integerinteger(30, 30));
		pathList2.add(ActionAlphabet.pathinteger(30));
		pathList2.add(ActionAlphabet.diamondinteger(40));
		pathList2.add(ActionAlphabet.diamondinteger(40));
		pathList2.add(ActionAlphabet.DIAMONDPATH);
		
		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
				destList, pathList2, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree2));
	}
	
	public void testFilterListActionsDeny() {
		
		List<IAction> actions1 = new ArrayList<IAction>();
		actions1.add(new PathPrepend(30));
		actions1.add(new ComAdd(90));
		actions1.add(new ComAdd(80));
		actions1.add(new PathPrepend(40));
		actions1.add(new Accept());
		
		List<IAction> actions2 = new ArrayList<IAction>();
		actions2.add(new PathPrepend(50));
		actions2.add(new ComAdd(30));
		actions2.add(new ComRemove(50));		
		
		IFilterRule filterRule1 = new FilterRule(null, actions1);
		IFilterRule filterRule2 = new FilterRule(null, actions2);
		
		List<IFilterRule> filterRules = new ArrayList<IFilterRule>();
		filterRules.add(filterRule1);
		filterRules.add(filterRule2);
		Filter filter = new Filter(filterRules);
		
		// alphabet extended by additional actions
		List<IAction> extendedActions = new ArrayList<IAction>();
		extendedActions.add(new PathPrepend(20));
		extendedActions.add(new PathPrepend(50));
		extendedActions.add(new ComAdd(30));
		extendedActions.add(new ComAdd(50));
		extendedActions.addAll(actions1);
		extendedActions.addAll(actions2);
		RouteAlphabet alphabet = 
			FilterProvider.alphabetForActions(extendedActions);

		FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.integerinteger(50, 50));
		pathList.add(ActionAlphabet.integerinteger(20, 20));
		pathList.add(ActionAlphabet.integerinteger(30, 30));
		pathList.add(ActionAlphabet.pathinteger(30));
		pathList.add(ActionAlphabet.diamondinteger(40));
		pathList.add(ActionAlphabet.diamondinteger(50));
		pathList.add(ActionAlphabet.DIAMONDPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(100, 100);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.integerinteger(50, 50));
		comList.add(ActionAlphabet.cominteger(80));
		comList.add(ActionAlphabet.diamondinteger(90));
		comList.add(ActionAlphabet.DIAMONDCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODACC;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertFalse("The filter automaton should not accept the routing tree.",
				automaton.accepts(tree));
	}

	/**
	 * Tests the composition of comRemove after comAdd: add an element already
	 * in and remove it (on a tree to be accepted).
	 */
	public void testFilterComAddComRemove() {

		final int comValue = 30;

		List<IAction> actions1 = new ArrayList<IAction>();
		actions1.add(new ComAdd(comValue));
		actions1.add(new ComRemove(comValue));
		
		IFilterRule filterRule1 = new FilterRule(null,actions1);
		
		Filter filter = new Filter(Collections.singletonList(filterRule1));
		
		RouteAlphabet alphabet = filter.filterAlphabet();
		FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.integercom(30));
		comList.add(ActionAlphabet.COMDIAMOND);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The filter automaton should accept the routing tree.",
				automaton.accepts(tree));
		
		//comList2
		List<LabelPair> comList2 = new ArrayList<LabelPair>();
		comList2.add(ActionAlphabet.integerinteger(30, 30));
		comList2.add(ActionAlphabet.COMDIAMOND);
	
		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList2, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree2));
	}
}
