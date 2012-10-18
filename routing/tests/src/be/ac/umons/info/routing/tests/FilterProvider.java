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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import be.ac.umons.info.routing.Filter;
import be.ac.umons.info.routing.FilterRule;
import be.ac.umons.info.routing.IFilterRule;
import be.ac.umons.info.routing.actions.IAction;
import be.ac.umons.info.routing.actions.atomic.Accept;
import be.ac.umons.info.routing.actions.atomic.ComAdd;
import be.ac.umons.info.routing.actions.atomic.ComRemove;
import be.ac.umons.info.routing.actions.atomic.PrefAdd;
import be.ac.umons.info.routing.actions.atomic.PrefSet;
import be.ac.umons.info.routing.actions.atomic.PrefSub;
import be.ac.umons.info.routing.actions.atomic.Reject;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import be.ac.umons.info.routing.predicates.IPredicate;
import be.ac.umons.info.routing.predicates.atomic.CommIn;
import be.ac.umons.info.routing.predicates.atomic.PathIn;
import be.ac.umons.info.routing.predicates.atomic.PathNei;
import be.ac.umons.info.routing.predicates.operators.PredicateAnd;
import be.ac.umons.info.routing.predicates.operators.PredicateNot;

/**
 * This class provides some filters used in tests.
 */
public class FilterProvider {

	public static Filter complexComAddAccept(final int asValue, final int comValue) {
		
		final IPredicate inPath = new PathIn(asValue);
		final List<IAction> actions1 = new ArrayList<IAction>();
		actions1.add(new ComAdd(comValue));
		actions1.add(new Accept());
		actions1.add(new ComRemove(comValue));
		final FilterRule rule1 = new FilterRule(inPath, actions1);
		
		final IPredicate notInPath = new PredicateNot(inPath);
		final List<IAction> actions2 = new ArrayList<IAction>();
		actions2.add(new ComAdd(comValue));
		actions2.add(new Accept());
		final FilterRule rule2 = new FilterRule(notInPath, actions2);

		return filterFromRules(rule1, rule2);
	}
	
	public static Filter simpleComAddAccept(final int comValue) {
		
		final List<IAction> actions1 = new ArrayList<IAction>();
		actions1.add(new ComAdd(comValue));
		actions1.add(new Accept());
		final FilterRule rule1 = new FilterRule(null, actions1);
		
		return filterFromRules(rule1);
	}

	public static Filter relativePref(final int prefIncr, final int prefDecr) {

		final List<IAction> actions1 =
			Collections.singletonList((IAction)new PrefAdd(prefIncr));
		final IFilterRule rule1 = new FilterRule(null, actions1);
		
		final List<IAction> actions2 =
			Collections.singletonList((IAction)new PrefSub(prefDecr));
		final IFilterRule rule2 = new FilterRule(null, actions2);
		
		return filterFromRules(rule1, rule2);
	}
	
	/**
	 * Returns the automaton from a series of filters.
	 */
	public static Filter filterFromRules(IFilterRule... rules){
		return new Filter(Arrays.asList(rules));
	}
	
	/**
	 * Small method to use the method in Filter that builds the alphabet, and
	 * apply it to a series of actions.
	 */
	public static RouteAlphabet alphabetForActions(final IAction... actions) {
		return FilterProvider.alphabetForActions(Arrays.asList(actions));
	}
	
	/**
	 * Small method to use the method in Filter that builds the alphabet, and
	 * apply it to a series of actions.
	 */
	public static RouteAlphabet alphabetForActions(
			final List<IAction> actions) {
		final IFilterRule rule = new FilterRule(null, actions);
		final Filter filter = new Filter(Collections.singletonList(rule));
		return filter.filterAlphabet();
	}
	
	/**
	 * The filter sent by Laurent on Sept 21st.
	 */
	public static Filter realWorldFilter() {
		List<IFilterRule> rules = new ArrayList<IFilterRule>();
		
		/* rule:
		 * if destination_prefix in 57.0.0.0/8 (not implemented)
		 *    AND path_neighbor is 2647 
		 *    AND community-list contains 5511:70
		 * then
		 *    set local-preference to 70 
		 *    AND add communities 5511:540 5511:542 5511:600 5511:999 to community-list 
		 *    AND accept route
		 */
		final IPredicate rule1Predicate = 
			new PredicateAnd(new PathNei(2647), new CommIn(70));
		List<IAction> rule1Actions = new ArrayList<IAction>();
		rule1Actions.add(new PrefSet(70));
		rule1Actions.add(new ComAdd(540));
		rule1Actions.add(new ComAdd(542));
		rule1Actions.add(new ComAdd(600));
		rule1Actions.add(new ComAdd(999));
		rule1Actions.add(new Accept());
		rules.add(new FilterRule(rule1Predicate, rule1Actions));

		/* rule:
		 * if destination_prefix in 57.0.0.0/8 (not implemented) 
		 *    AND path_neighbor is 2647 
		 *    AND community-list contains 5511:80
		 * then
		 *    set local-preference to 80 
		 *    AND add communities 5511:540 5511:542 5511:600 5511:999 to community-list 
		 *    AND accept route
		 */
		final IPredicate rule2Predicate = 
			new PredicateAnd(new PathNei(2647), new CommIn(80));
		List<IAction> rule2Actions = new ArrayList<IAction>();
		rule2Actions.add(new PrefSet(80));
		rule2Actions.add(new ComAdd(540));
		rule2Actions.add(new ComAdd(542));
		rule2Actions.add(new ComAdd(600));
		rule2Actions.add(new ComAdd(999));
		rule2Actions.add(new Accept());
		rules.add(new FilterRule(rule2Predicate, rule2Actions));

		/* rule:
		 * if destination_prefix in 57.0.0.0/8 (not implemented) 
		 *    AND path_neighbor is 2647 
		 *    AND community-list contains 5511:90
		 * then
		 *    set local-preference to 90 
		 *    AND add communities 5511:540 5511:542 5511:600 5511:999 to community-list 
		 *    AND accept route
		 */
		final IPredicate rule3Predicate = 
			new PredicateAnd(new PathNei(2647), new CommIn(90));
		List<IAction> rule3Actions = new ArrayList<IAction>();
		rule3Actions.add(new PrefSet(90));
		rule3Actions.add(new ComAdd(540));
		rule3Actions.add(new ComAdd(542));
		rule3Actions.add(new ComAdd(600));
		rule3Actions.add(new ComAdd(999));
		rule3Actions.add(new Accept());
		rules.add(new FilterRule(rule3Predicate, rule3Actions));
	
		/* rule:
		 * if destination_prefix in 57.0.0.0/8 (not implemented)
		 *    AND path_neighbor is 2647
		 * then
		 *    add communities 5511:540 5511:542 5511:600 5511:999 to community-list
		 *    AND accept route
		 */
		final IPredicate rule4Predicate = new PathNei(2647);
		List<IAction> rule4Actions = new ArrayList<IAction>();
		rule4Actions.add(new ComAdd(540));
		rule4Actions.add(new ComAdd(542));
		rule4Actions.add(new ComAdd(600));
		rule4Actions.add(new ComAdd(999));
		rule4Actions.add(new Accept());
		rules.add(new FilterRule(rule4Predicate, rule4Actions));
		
		/* rule:
		 * if path_neighbor is 2647 
		 *    AND community-list contains 5511:70
		 * then
		 *    set local-preference to 70 
		 *    AND add communities 5511:540 5511:542 5511:999 to community-list 
		 *    AND accept route
		 */
		final IPredicate rule5Predicate = 
			new PredicateAnd(new PathNei(2647), new CommIn(70));
		List<IAction> rule5Actions = new ArrayList<IAction>();
		rule5Actions.add(new PrefSet(70));
		rule5Actions.add(new ComAdd(540));
		rule5Actions.add(new ComAdd(542));
		rule5Actions.add(new ComAdd(999));
		rule5Actions.add(new Accept());
		rules.add(new FilterRule(rule5Predicate, rule5Actions));

		/* rule:
		 * if path_neighbor is 2647 
		 *    AND community-list contains 5511:80
		 * then
		 *    set local-preference to 80 
		 *    AND add communities 5511:540 5511:542 5511:999 to community-list 
		 *    AND accept route
		 */
		final IPredicate rule6Predicate = 
			new PredicateAnd(new PathNei(2647), new CommIn(80));
		List<IAction> rule6Actions = new ArrayList<IAction>();
		rule6Actions.add(new PrefSet(80));
		rule6Actions.add(new ComAdd(540));
		rule6Actions.add(new ComAdd(542));
		rule6Actions.add(new ComAdd(999));
		rule6Actions.add(new Accept());
		rules.add(new FilterRule(rule6Predicate, rule6Actions));
		
		/* rule:
		 * if path_neighbor is 2647 
		 *    AND community-list contains 5511:90
		 * then
		 *    set local-preference to 90 
		 *    AND add communities 5511:540 5511:542 5511:999 to community-list 
		 *    AND accept route
		 */
		final IPredicate rule7Predicate = 
			new PredicateAnd(new PathNei(2647), new CommIn(90));
		List<IAction> rule7Actions = new ArrayList<IAction>();
		rule7Actions.add(new PrefSet(90));
		rule7Actions.add(new ComAdd(540));
		rule7Actions.add(new ComAdd(542));
		rule7Actions.add(new ComAdd(999));
		rule7Actions.add(new Accept());
		rules.add(new FilterRule(rule7Predicate, rule7Actions));

		/* rule:
		 * if path_neighbor is 2647 
		 *    AND community-list contains 5511:101
		 * then
		 *    set local-preference to 101 
		 *    AND add communities 5511:540 5511:542 5511:999 to community-list 
		 *    AND accept route
		 */
		final IPredicate rule8Predicate = 
			new PredicateAnd(new PathNei(2647), new CommIn(101));
		List<IAction> rule8Actions = new ArrayList<IAction>();
		rule8Actions.add(new PrefSet(101));
		rule8Actions.add(new ComAdd(540));
		rule8Actions.add(new ComAdd(542));
		rule8Actions.add(new ComAdd(999));
		rule8Actions.add(new Accept());
		rules.add(new FilterRule(rule8Predicate, rule8Actions));

		/* rule:
		 * if path_neighbor is 2647
		 * then
		 *    set local-preference to 100 
		 *    AND add communities 5511:540 5511:542 5511:999 to community-list 
		 *    AND accept route
		 */
		final IPredicate rule9Predicate = new PathNei(2647);
		List<IAction> rule9Actions = new ArrayList<IAction>();
		rule9Actions.add(new PrefSet(100));
		rule9Actions.add(new ComAdd(540));
		rule9Actions.add(new ComAdd(542));
		rule9Actions.add(new ComAdd(999));
		rule9Actions.add(new Accept());
		rules.add(new FilterRule(rule9Predicate, rule9Actions));

		/* rule:
		 * if true
		 * then
		 * 	  reject route
		 */
		List<IAction> rule10Actions = new ArrayList<IAction>();
		rule10Actions.add(new Reject());
		rules.add(new FilterRule(null, rule10Actions));
		
		return new Filter(rules);
	}
}
