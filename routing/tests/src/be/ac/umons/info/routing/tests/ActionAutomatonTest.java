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
import be.ac.umons.info.routing.actions.atomic.ClearCommunities;
import be.ac.umons.info.routing.actions.atomic.ComAdd;
import be.ac.umons.info.routing.actions.atomic.ComRemove;
import be.ac.umons.info.routing.actions.atomic.PathPrepend;
import be.ac.umons.info.routing.actions.atomic.PrefAdd;
import be.ac.umons.info.routing.actions.atomic.PrefSet;
import be.ac.umons.info.routing.actions.atomic.PrefSub;
import be.ac.umons.info.routing.actions.atomic.Reject;
import be.ac.umons.info.routing.automata.ActionAlphabet;
import be.ac.umons.info.routing.automata.FilterAutomaton;
import be.ac.umons.info.routing.automata.LabelPair;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import traul.ranked.terms.ITerm;

public class ActionAutomatonTest extends TestCase{
	
	public void testPrefSetAccept() {

		final int newPref = 120;
		PrefSet prefChange = new PrefSet(newPref);
		
		RouteAlphabet alphabet = alphabetForAction(prefChange);
		FilterAutomaton automaton = 
			prefChange.automaton(alphabet, new ActionAlphabet(alphabet));
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
				Filter.DEFAULT_LOCAL_PREF, newPref);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
			destList, pathList, prefVal, comList, acceptVal,
			new ActionAlphabet(alphabet));
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));
	}
	
	public void testPrefSetDeny() {	

		final int newPref = 120;
		PrefSet prefChange = new PrefSet(newPref);
		
		RouteAlphabet alphabet = alphabetForAction(prefChange);
		FilterAutomaton automaton = prefChange.automaton(alphabet,
				new ActionAlphabet(alphabet));

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
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;

		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));

		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree));
	}
	
	public void testPrefIncrementAccept() {

		final int increment = 20;
		PrefAdd prefIncrement = new PrefAdd(increment);

		RouteAlphabet alphabet = alphabetForAction(prefIncrement);
		FilterAutomaton automaton = prefIncrement.automaton(alphabet,
				new ActionAlphabet(alphabet));
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
				Filter.DEFAULT_LOCAL_PREF, 
				Filter.DEFAULT_LOCAL_PREF + increment);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));
	}
	
	public void testPrefAddDeny() {
		
		final int increment = 20;
		PrefAdd prefIncrement = new PrefAdd(increment);
		
		RouteAlphabet alphabet = alphabetForAction(prefIncrement);
		FilterAutomaton automaton = prefIncrement.automaton(alphabet,
				new ActionAlphabet(alphabet));
		
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
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree));
	}
	
	public void testPrefAddUpperBound() {

		IAction filter1action = new PrefAdd(20);
		IFilterRule fRule1 = new FilterRule(null, 
				Collections.singletonList(filter1action)); 
		List<IFilterRule> filter1rules = Collections.singletonList(fRule1);
		Filter filter1 = new Filter(filter1rules, Integer.MAX_VALUE-10);

		IAction filter2action = new PrefAdd(30);
		IFilterRule fRule2 = new FilterRule(null, 
				Collections.singletonList(filter2action));
		List<IFilterRule> filter2rules = Collections.singletonList(fRule2);
		Filter filter2 = new Filter(filter2rules, Integer.MAX_VALUE-10);
		
		assertTrue("The filters should be equivalent.",
			filter1.equivalent(filter2));

		IAction filter3action = new PrefAdd(5);
		IFilterRule fRule3 = new FilterRule(null, 
				Collections.singletonList(filter3action));
		List<IFilterRule> filter3rules = Collections.singletonList(fRule3);
		Filter filter3 = new Filter(filter3rules, Integer.MAX_VALUE-10);
		
		assertFalse("The filters should not be equivalent.",
			filter2.equivalent(filter3));		
	}
	
	public void testPrefSubAccept() {
		
		final int decrement = 20;
		PrefSub prefDecrement = new PrefSub(decrement);

		RouteAlphabet alphabet = alphabetForAction(prefDecrement);
		FilterAutomaton automaton = prefDecrement.automaton(alphabet,
				new ActionAlphabet(alphabet));

		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
				Filter.DEFAULT_LOCAL_PREF, 
				Filter.DEFAULT_LOCAL_PREF - decrement);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));
	}
	
	public void testPrefSubAccept2() {	
		
		final int decrement = 20;
		PrefSub prefDecrement = new PrefSub(decrement);
		
		RouteAlphabet alphabet = alphabetForAction(prefDecrement);
		FilterAutomaton automaton = prefDecrement.automaton(alphabet,
				new ActionAlphabet(alphabet));
		
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
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.ACCACC;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));
	}
	
	public void testPrefSubDeny() {	
		
		final int decrement = 20;
		PrefSub prefDecrement = new PrefSub(decrement);
		
		RouteAlphabet alphabet = alphabetForAction(prefDecrement);
		FilterAutomaton automaton = prefDecrement.automaton(alphabet,
				new ActionAlphabet(alphabet));
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
				Filter.DEFAULT_LOCAL_PREF, 
				Filter.DEFAULT_LOCAL_PREF - decrement);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.ACCACC;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertFalse("The automaton should not accept the routing tree because"
				+" it is accepted and modified.", automaton.accepts(tree));
	}
	
	public void testPathPrependAccept() {
		
		final int asValue = 120;	
		PathPrepend asAdd = new PathPrepend(asValue);

		RouteAlphabet alphabet = alphabetForAction(asAdd);
		FilterAutomaton automaton = asAdd.automaton(alphabet,
				new ActionAlphabet(alphabet));		
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.integerinteger(asValue, asValue));
		pathList.add(ActionAlphabet.integerinteger(asValue, asValue));
		pathList.add(ActionAlphabet.pathinteger(asValue));
		pathList.add(ActionAlphabet.DIAMONDPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
				Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));
	}
	
	public void testPathPrependDeny() {
		
		final int asValue = 120;
		PathPrepend asAdd = new PathPrepend(asValue);

		RouteAlphabet alphabet = alphabetForAction(asAdd);
		FilterAutomaton automaton = asAdd.automaton(alphabet,
				new ActionAlphabet(alphabet));
				
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.integerinteger(asValue, asValue));
		pathList.add(ActionAlphabet.integerinteger(asValue, asValue));
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
				Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;
		
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree));
	}
	
	/**
	 * Tests the addition of a greater element: at the end of the list
	 */
	public void testComAddAccept1() {
		
		final int comValue = 120;
		ComAdd comAdd = new ComAdd(comValue);

		RouteAlphabet alphabet = alphabetForAction(comAdd);
		FilterAutomaton automaton = comAdd.automaton(alphabet,
				new ActionAlphabet(alphabet));
		
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
		comList.add(ActionAlphabet.cominteger(comValue));
		comList.add(ActionAlphabet.DIAMONDCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));
	}
	
	/**
	 * Tests the insertion of an element already in.
	 */
	public void testComAddAccept2() {
		
		final int comValue = 40;
		ComAdd comAdd = new ComAdd(comValue);

		RouteAlphabet alphabet = alphabetForAction(comAdd);
		FilterAutomaton automaton = comAdd.automaton(alphabet,
				new ActionAlphabet(alphabet));
				
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
		comList.add(ActionAlphabet.integerinteger(comValue, comValue));
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));
	}
	
	/**
	 * Tests the removal at the end of the list.
	 */
	public void testComRemoveAccept1() {
		
		final int comValue1 = 70;
		final int comValue2 = 80;
		IAction comAdd = new ComAdd(comValue1);
		IAction comRemove = new ComRemove(comValue2);

		Filter filter = new Filter(comAdd, comRemove);
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
		comList.add(ActionAlphabet.integerinteger(comValue1,comValue1));
		comList.add(ActionAlphabet.integercom(comValue2));
		comList.add(ActionAlphabet.COMDIAMOND);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));
	}
	
	/**
	 * Tests the removal at the end of the list.
	 */
	public void testComRemoveAccept2() {
		
		final int comValue = 30;
		final int comValue2 = 40;
		IAction comAdd1 = new ComAdd(comValue);
		IAction comAdd2 = new ComAdd(comValue2);
		ComRemove comRemove = new ComRemove(comValue2);

		Filter filter = new Filter(comAdd1, comAdd2, comRemove);
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
		comList.add(ActionAlphabet.integerinteger(comValue, comValue));
		comList.add(ActionAlphabet.integercom(comValue2));
		comList.add(ActionAlphabet.COMDIAMOND);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));
	}
	
	/**
	 * Tests the removal of an element not in the list.
	 */
	public void testComRemoveAccept3() {
		
		final int comValue1 = 30;
		final int comValue2 = 40;
		IAction comAdd = new ComAdd(comValue1);
		IAction comRemove = new ComRemove(comValue2);
		
		Filter filter = new Filter(comAdd, comRemove);
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
		comList.add(ActionAlphabet.integerinteger(comValue1, comValue1));
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;

		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));
	}
	
	public void testAccept() {

		Accept accept = new Accept();

		RouteAlphabet alphabet = alphabetForAction(accept);
		FilterAutomaton automaton = accept.automaton(alphabet,
				new ActionAlphabet(alphabet));
		
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
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODACC;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));
	}

	public void testReject() {	

		Reject reject = new Reject();

		RouteAlphabet alphabet = alphabetForAction(reject);
		FilterAutomaton automaton = reject.automaton(alphabet,
				new ActionAlphabet(alphabet));
		
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
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODREJ;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));
	}
	
	public void testClearCommunities() {

		// adding some values to alphabet, in order to perform tests
		final int comValue1 = 20;
		final int comValue2 = 40;
		final int comValue3 = 70;
		final IAction comVal1 = new ComAdd(comValue1);
		final IAction comVal2 = new ComAdd(comValue2);
		final IAction comVal3 = new ComAdd(comValue3);
		RouteAlphabet alphabet = 
			new Filter(comVal1, comVal2, comVal3).filterAlphabet();
		
		final IAction clearCom = new ClearCommunities();
		final FilterAutomaton automaton = 
			clearCom.automaton(alphabet, new ActionAlphabet(alphabet));
				
		//destlist
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
		comList.add(ActionAlphabet.integercom(comValue1));
		comList.add(ActionAlphabet.integerdiamond(comValue2));
		comList.add(ActionAlphabet.integerdiamond(comValue3));
		comList.add(ActionAlphabet.COMDIAMOND);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));

		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));

		//comList2
		List<LabelPair> comList2 = new ArrayList<LabelPair>();
		comList2.add(ActionAlphabet.integerinteger(comValue1, comValue1));
		comList2.add(ActionAlphabet.integercom(comValue2));
		comList2.add(ActionAlphabet.integerdiamond(comValue3));
		comList2.add(ActionAlphabet.COMDIAMOND);
		
		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList2, acceptVal,
				new ActionAlphabet(alphabet));

		assertFalse("The automaton should reject the routing tree.",
				automaton.accepts(tree2));

		//comList3
		List<LabelPair> comList3 = new ArrayList<LabelPair>();
		comList3.add(ActionAlphabet.COMCOM);
		
		ITerm<LabelPair> tree3 = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList3, acceptVal,
				new ActionAlphabet(alphabet));

		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree3));

		//comList4
		List<LabelPair> comList4 = new ArrayList<LabelPair>();
		comList4.add(ActionAlphabet.integercom(comValue2));
		comList4.add(ActionAlphabet.COMDIAMOND);
		
		ITerm<LabelPair> tree4 = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList4, acceptVal,
				new ActionAlphabet(alphabet));

		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree4));
	}
	
	/**
	 * Small method to use the method in Filter that builds the alphabet, and
	 * apply it to a single action.
	 */
	private RouteAlphabet alphabetForAction(final IAction action) {
		final IFilterRule rule = 
			new FilterRule(null, Collections.singletonList(action));
		final Filter filter = new Filter(Collections.singletonList(rule));
		return filter.filterAlphabet();
	}
}
