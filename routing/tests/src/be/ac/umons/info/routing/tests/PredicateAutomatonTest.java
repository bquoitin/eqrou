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
import traul.ranked.nta.IDTA;
import traul.ranked.nta.INTA;
import traul.ranked.nta.labels.ILabel;
import traul.ranked.nta.states.IState;
import be.ac.umons.info.routing.Filter;
import be.ac.umons.info.routing.FilterRule;
import be.ac.umons.info.routing.IFilterRule;
import be.ac.umons.info.routing.actions.IAction;
import be.ac.umons.info.routing.actions.atomic.ComAdd;
import be.ac.umons.info.routing.actions.atomic.PathPrepend;
import be.ac.umons.info.routing.automata.IntegerLabel;
import be.ac.umons.info.routing.automata.PredicateAutomaton;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import be.ac.umons.info.routing.predicates.IPredicate;
import be.ac.umons.info.routing.predicates.atomic.CommIn;
import be.ac.umons.info.routing.predicates.atomic.DstIn;
import be.ac.umons.info.routing.predicates.atomic.DstIs;
import be.ac.umons.info.routing.predicates.atomic.PathIn;
import be.ac.umons.info.routing.predicates.atomic.PathNei;
import be.ac.umons.info.routing.predicates.atomic.PathOri;
import be.ac.umons.info.routing.predicates.atomic.PathSub;
import be.ac.umons.info.routing.predicates.operators.PredicateAnd;
import be.ac.umons.info.routing.predicates.operators.PredicateNot;
import be.ac.umons.info.routing.predicates.operators.PredicateOr;
import traul.ranked.terms.ITerm;

public class PredicateAutomatonTest extends TestCase{
	
	public void testFirstElemAS() {

		final int firstElemValue = 50;
		final int otherElem1 = 20;
		final int otherElem2 = 70;
		
		final PathNei firstElemAS = new PathNei(firstElemValue);
		
		final IAction pathAction1 = new PathPrepend(otherElem1);
		final IAction pathAction2 = new PathPrepend(otherElem2);
		RouteAlphabet alphabet = alphabetForPredicate(firstElemAS);
		alphabet = alphabet.union(new Filter(pathAction1, pathAction2)
			.filterAlphabet());
		final IDTA<ILabel, IState> automaton = firstElemAS.automaton(alphabet);
		
		//destList
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.add(RouteAlphabet.DEST);

		//pathList1
		List<ILabel> pathList1 = new ArrayList<ILabel>();
		pathList1.add(RouteAlphabet.integer(otherElem1));
		pathList1.add(RouteAlphabet.integer(firstElemValue));
		pathList1.add(RouteAlphabet.PATH);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree1 = PredicateTree.getPredicateTree(
			destList, pathList1, prefVal, comList, acceptVal, alphabet);
				
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree1));

		//pathList2
		List<ILabel> pathList2 = new ArrayList<ILabel>();
		pathList2.add(RouteAlphabet.integer(firstElemValue));
		pathList2.add(RouteAlphabet.integer(otherElem1));
		pathList2.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
				destList, pathList2, prefVal, comList, acceptVal, alphabet);

		assertFalse("The automaton should not accept the routing tree.",
					automaton.accepts(tree2));

		//pathList3
		List<ILabel> pathList3 = new ArrayList<ILabel>();
		pathList3.add(RouteAlphabet.integer(otherElem1));
		pathList3.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
				destList, pathList3, prefVal, comList, acceptVal, alphabet);

		assertFalse("The automaton should not accept the routing tree.",
					automaton.accepts(tree3));

		//pathList4
		List<ILabel> pathList4 = new ArrayList<ILabel>();
		pathList4.add(RouteAlphabet.integer(firstElemValue));
		pathList4.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree4 = PredicateTree.getPredicateTree(
				destList, pathList4, prefVal, comList, acceptVal, alphabet);

		assertTrue("The automaton should accept the routing tree.",
					automaton.accepts(tree4));

}

	public void testLastElemAS() {

		final int lastElemValue = 10;
		final int otherElem1 = 20;
		final int otherElem2 = 70;

		final PathOri lastElemAS = new PathOri(lastElemValue);
		final IAction pathAction1 = new PathPrepend(otherElem1);
		final IAction pathAction2 = new PathPrepend(otherElem2);
		
		RouteAlphabet alphabet = alphabetForPredicate(lastElemAS);
		alphabet = alphabet.union(
			new Filter(pathAction1, pathAction2).filterAlphabet());

		final IDTA<ILabel, IState> automaton = lastElemAS.automaton(alphabet);
		
		//destlist
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.add(RouteAlphabet.DEST);

		//pathList
		List<ILabel> pathList = new ArrayList<ILabel>();
		pathList.add(RouteAlphabet.integer(lastElemValue));
		pathList.add(RouteAlphabet.integer(otherElem1));
		pathList.add(RouteAlphabet.integer(otherElem2));
		pathList.add(RouteAlphabet.PATH);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList, acceptVal, alphabet);
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));

		//pathList2
		List<ILabel> pathList2 = new ArrayList<ILabel>();
		pathList2.add(RouteAlphabet.integer(lastElemValue));
		pathList2.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
			destList, pathList2, prefVal, comList, acceptVal, alphabet);
			
		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree2));

		//pathList3
		List<ILabel> pathList3 = new ArrayList<ILabel>();
		pathList3.add(RouteAlphabet.integer(otherElem1));
		pathList3.add(RouteAlphabet.integer(lastElemValue));
		pathList3.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
			destList, pathList3, prefVal, comList, acceptVal, alphabet);
			
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree3));

		//pathList4
		List<ILabel> pathList4 = new ArrayList<ILabel>();
		pathList4.add(RouteAlphabet.integer(otherElem1));
		pathList4.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree4 = PredicateTree.getPredicateTree(
			destList, pathList4, prefVal, comList, acceptVal, alphabet);
			
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree4));
}
	
	public void testInAS() {

		final int inValue = 40;
		final int otherElem1 = 20;
		final int otherElem2 = 70;

		final PathIn inAS = new PathIn(inValue);
		final IAction pathAction1 = new PathPrepend(otherElem1);
		final IAction pathAction2 = new PathPrepend(otherElem2);
		
		RouteAlphabet alphabet = alphabetForPredicate(inAS);
		alphabet = alphabet.union(
			new Filter(pathAction1, pathAction2).filterAlphabet());
		final IDTA<ILabel, IState> automaton = inAS.automaton(alphabet);
				
		//destlist
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.add(RouteAlphabet.DEST);

		//pathList
		List<ILabel> pathList = new ArrayList<ILabel>();
		pathList.add(RouteAlphabet.integer(otherElem1));
		pathList.add(RouteAlphabet.integer(otherElem1));
		pathList.add(RouteAlphabet.integer(inValue));
		pathList.add(RouteAlphabet.integer(otherElem1));
		pathList.add(RouteAlphabet.PATH);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList, acceptVal, alphabet);
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));

		//pathList2
		List<ILabel> pathList2 = new ArrayList<ILabel>();
		pathList2.add(RouteAlphabet.integer(inValue));
		pathList2.add(RouteAlphabet.integer(inValue));
		pathList2.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
				destList, pathList2, prefVal, comList, acceptVal, alphabet);
			
			assertTrue("The automaton should accept the routing tree.",
					automaton.accepts(tree2));

		//pathList3
		List<ILabel> pathList3 = new ArrayList<ILabel>();
		pathList3.add(RouteAlphabet.integer(otherElem1));
		pathList3.add(RouteAlphabet.integer(otherElem2));
		pathList3.add(RouteAlphabet.PATH);
			
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
			destList, pathList3, prefVal, comList, acceptVal, alphabet);
				
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree3));

		//pathList4
		List<ILabel> pathList4 = new ArrayList<ILabel>();
		pathList4.add(RouteAlphabet.PATH);
			
		ITerm<ILabel> tree4 = PredicateTree.getPredicateTree(
			destList, pathList4, prefVal, comList, acceptVal, alphabet);
				
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree4));
	}
	
	public void testInCom() {

		final int inValue = 40;
		final int otherElem1 = 20;
		final int otherElem2 = 70;

		final CommIn inCom = new CommIn(inValue);
		final IAction comAction1 = new ComAdd(otherElem1);
		final IAction comAction2 = new ComAdd(otherElem2);
		
		RouteAlphabet alphabet = alphabetForPredicate(inCom);
		alphabet = alphabet.union(
			new Filter(comAction1, comAction2).filterAlphabet());
		final IDTA<ILabel, IState> automaton = inCom.automaton(alphabet);
				
		//destlist
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.add(RouteAlphabet.DEST);

		//pathList
		List<ILabel> pathList = new ArrayList<ILabel>();
		pathList.add(RouteAlphabet.PATH);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.integer(otherElem1));
		comList.add(RouteAlphabet.integer(inValue));
		comList.add(RouteAlphabet.integer(otherElem2));
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList, acceptVal, alphabet);

		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));

		//comList2
		List<ILabel> comList2 = new ArrayList<ILabel>();
		comList2.add(RouteAlphabet.integer(inValue));
		comList2.add(RouteAlphabet.integer(inValue));
		comList2.add(RouteAlphabet.COM);
		
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList2, acceptVal, alphabet);
			
		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree2));

		//comList3
		List<ILabel> comList3 = new ArrayList<ILabel>();
		comList3.add(RouteAlphabet.integer(otherElem1));
		comList3.add(RouteAlphabet.integer(otherElem2));
		comList3.add(RouteAlphabet.COM);
		
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList3, acceptVal, alphabet);
			
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree3));

		//comList4
		List<ILabel> comList4 = new ArrayList<ILabel>();
		comList4.add(RouteAlphabet.COM);
		
		ITerm<ILabel> tree4 = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList4, acceptVal, alphabet);
			
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree4));
}
	
	public void testFirstAndPathOri() {

		final int firstElemValue = 50;
		final int lastElemValue = 10;
		final int otherElem1 = 20;
		final int otherElem2 = 70;

		final PathOri lastElemAS = new PathOri(lastElemValue);
		final PathNei firstElemAS = new PathNei(firstElemValue);
		final IPredicate predicate = new PredicateAnd(lastElemAS, firstElemAS);
		final IAction pathAction1 = new PathPrepend(otherElem1);
		final IAction pathAction2 = new PathPrepend(otherElem2);
		
		RouteAlphabet alphabet = alphabetForPredicate(predicate);
		alphabet = alphabet.union(
			new Filter(pathAction1, pathAction2).filterAlphabet());
		final IDTA<ILabel, IState> automaton = predicate.automaton(alphabet);
		
		//destlist
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.add(RouteAlphabet.DEST);

		//pathList
		List<ILabel> pathList = new ArrayList<ILabel>();
		pathList.add(RouteAlphabet.integer(lastElemValue));
		pathList.add(RouteAlphabet.integer(otherElem1));
		pathList.add(RouteAlphabet.integer(lastElemValue));
		pathList.add(RouteAlphabet.integer(firstElemValue));
		pathList.add(RouteAlphabet.PATH);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList, acceptVal, alphabet);
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));

		//pathList2
		List<ILabel> pathList2 = new ArrayList<ILabel>();
		pathList2.add(RouteAlphabet.integer(lastElemValue));
		pathList2.add(RouteAlphabet.integer(firstElemValue));
		pathList2.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
			destList, pathList2, prefVal, comList, acceptVal, alphabet);
			
		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree2));

		//pathList3
		List<ILabel> pathList3 = new ArrayList<ILabel>();
		pathList3.add(RouteAlphabet.integer(otherElem1));
		pathList3.add(RouteAlphabet.integer(firstElemValue));
		pathList3.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
			destList, pathList3, prefVal, comList, acceptVal, alphabet);
			
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree3));

		//pathList4
		List<ILabel> pathList4 = new ArrayList<ILabel>();
		pathList4.add(RouteAlphabet.integer(otherElem1));
		pathList4.add(RouteAlphabet.integer(otherElem2));
		pathList4.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree4 = PredicateTree.getPredicateTree(
			destList, pathList4, prefVal, comList, acceptVal, alphabet);
			
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree4));

		//pathList5
		List<ILabel> pathList5 = new ArrayList<ILabel>();
		pathList5.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree5 = PredicateTree.getPredicateTree(
			destList, pathList5, prefVal, comList, acceptVal, alphabet);
			
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree5));
	}
	
	public void testFirstOrPathOri() {

		int firstElemValue = 50;
		int lastElemValue = 10;
		final int otherElem1 = 20;
		final int otherElem2 = 70;
		
		final PathOri lastElemAS = new PathOri(lastElemValue);
		final PathNei firstElemAS = new PathNei(firstElemValue);
		final IPredicate predicate = new PredicateOr(lastElemAS, firstElemAS);
		final IAction pathAction1 = new PathPrepend(otherElem1);
		final IAction pathAction2 = new PathPrepend(otherElem2);
		
		RouteAlphabet alphabet = alphabetForPredicate(predicate);
		alphabet = alphabet.union(
			new Filter(pathAction1, pathAction2).filterAlphabet());
		final IDTA<ILabel, IState> automaton = predicate.automaton(alphabet);

		//destlist
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.add(RouteAlphabet.DEST);

		//pathList
		List<ILabel> pathList = new ArrayList<ILabel>();
		pathList.add(RouteAlphabet.integer(lastElemValue));
		pathList.add(RouteAlphabet.integer(firstElemValue));
		pathList.add(RouteAlphabet.integer(otherElem1));
		pathList.add(RouteAlphabet.integer(otherElem2));
		pathList.add(RouteAlphabet.PATH);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList, acceptVal, alphabet);
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));

		//pathList2
		List<ILabel> pathList2 = new ArrayList<ILabel>();
		pathList2.add(RouteAlphabet.integer(firstElemValue));
		pathList2.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
			destList, pathList2, prefVal, comList, acceptVal, alphabet);
			
		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree2));

		//pathList3
		List<ILabel> pathList3 = new ArrayList<ILabel>();
		pathList3.add(RouteAlphabet.integer(otherElem1));
		pathList3.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
			destList, pathList3, prefVal, comList, acceptVal, alphabet);
			
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree3));
}
	
	public void testComplementCommIn() {
		
		final int notinValue = 40;
		final int otherElem1 = 20;
		final int otherElem2 = 70;
				
		final CommIn inCom = new CommIn(notinValue);
		final IPredicate predicate = new PredicateNot(inCom);
		final IAction comAction1 = new ComAdd(otherElem1);
		final IAction comAction2 = new ComAdd(otherElem2);
		
		RouteAlphabet alphabet = alphabetForPredicate(predicate);
		alphabet = alphabet.union(
			new Filter(comAction1, comAction2).filterAlphabet());
		final INTA<ILabel, IState> automaton = 
			predicate.automaton(alphabet);
		
		//destlist
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.add(RouteAlphabet.DEST);

		//pathList
		List<ILabel> pathList = new ArrayList<ILabel>();
		pathList.add(RouteAlphabet.PATH);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.integer(otherElem1));
		comList.add(RouteAlphabet.integer(otherElem2));
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList, acceptVal, alphabet);

		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree));
		
		//comList2
		List<ILabel> comList2 = new ArrayList<ILabel>();
		comList2.add(RouteAlphabet.integer(notinValue));
		comList2.add(RouteAlphabet.integer(otherElem2));
		comList2.add(RouteAlphabet.COM);
		
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList2, acceptVal, alphabet);
		
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree2));
		
		//comList3
		List<ILabel> comList3 = new ArrayList<ILabel>();
		comList3.add(RouteAlphabet.COM);
		
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList3, acceptVal, alphabet);
		
		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree3));
	}
	
	
	/**
	 *  test (firstElemeAS = 50 OR lastElemAS = 10) AND (30 in AS)
	 */
	public void testFirstOrPathOriANDPathIn() {
		
		final int firstElemValue = 50;
		final int lastElemValue = 10;
		final int inValue = 30;
		final int otherElem1 = 20;
		final int otherElem2 = 70;
				
		final PathOri lastElemAS = new PathOri(lastElemValue);
		final PathNei firstElemAS = new PathNei(firstElemValue);
		final PathIn inAS = new PathIn(inValue);
		final IPredicate predicate = new PredicateAnd(
			new PredicateOr(firstElemAS, lastElemAS), inAS);
		final IAction pathAction1 = new PathPrepend(otherElem1);
		final IAction pathAction2 = new PathPrepend(otherElem2);
		
		RouteAlphabet alphabet = alphabetForPredicate(predicate);
		alphabet = alphabet.union(
			new Filter(pathAction1, pathAction2).filterAlphabet());
		final IDTA<ILabel, IState> automaton = 
			predicate.automaton(alphabet);
		
		//destlist
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.add(RouteAlphabet.DEST);

		//pathList
		List<ILabel> pathList = new ArrayList<ILabel>();
		pathList.add(RouteAlphabet.integer(otherElem1));
		pathList.add(RouteAlphabet.integer(otherElem2));
		pathList.add(RouteAlphabet.integer(inValue));
		pathList.add(RouteAlphabet.integer(firstElemValue));
		pathList.add(RouteAlphabet.PATH);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList, acceptVal, alphabet);
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));

		//pathList2
		List<ILabel> pathList2 = new ArrayList<ILabel>();
		pathList2.add(RouteAlphabet.integer(lastElemValue));
		pathList2.add(RouteAlphabet.integer(otherElem1));
		pathList2.add(RouteAlphabet.integer(otherElem2));
		pathList2.add(RouteAlphabet.integer(inValue));
		pathList2.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
			destList, pathList2, prefVal, comList, acceptVal, alphabet);
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree2));

		//pathList3
		List<ILabel> pathList3 = new ArrayList<ILabel>();
		pathList3.add(RouteAlphabet.integer(otherElem1));
		pathList3.add(RouteAlphabet.integer(otherElem2));
		pathList3.add(RouteAlphabet.integer(inValue));
		pathList3.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
			destList, pathList3, prefVal, comList, acceptVal, alphabet);
		
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree3));

		//pathList4
		List<ILabel> pathList4 = new ArrayList<ILabel>();
		pathList4.add(RouteAlphabet.integer(lastElemValue));
		pathList4.add(RouteAlphabet.integer(otherElem2));
		pathList4.add(RouteAlphabet.PATH);
		
		ITerm<ILabel> tree4 = PredicateTree.getPredicateTree(
			destList, pathList4, prefVal, comList, acceptVal, alphabet);
		
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree4));
	}
	
	/** 
	 * test (firstElemeAS = 50 OR lastElemAS = 10) AND (30 in AS) AND 
	 * (40 not in COM)
	 */
	public void testFirstOrPathOriANDPathInANDNotCommIn() {

		final int firstElemValue = 50;
		final int lastElemValue = 10;
		final int inValue = 30;
		final int notinValue = 40;
		final int otherElem1 = 20;
		final int otherElem2 = 70;
		
		final PathOri lastElemAS = new PathOri(lastElemValue);
		final PathNei firstElemAS = new PathNei(firstElemValue);
		final PathIn inAS = new PathIn(inValue);
		final CommIn inCom = new CommIn(notinValue);
		final IAction pathAction1 = new PathPrepend(otherElem1);
		final IAction pathAction2 = new PathPrepend(otherElem2);
		
		final IPredicate predUnion = new PredicateOr(lastElemAS, firstElemAS);
		final IPredicate predInter = new PredicateAnd(inAS, predUnion);
		
		final IPredicate predComplCommIn = new PredicateNot(inCom);
		final IPredicate predicate = 
			new PredicateAnd(predComplCommIn, predInter);
		
		RouteAlphabet alphabet = alphabetForPredicate(predicate);
		alphabet = alphabet.union(
			new Filter(pathAction1, pathAction2).filterAlphabet());
		final IDTA<ILabel, IState> automaton = 
			predicate.automaton(alphabet);

		//destlist
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.add(RouteAlphabet.DEST);

		//pathList
		List<ILabel> pathList = new ArrayList<ILabel>();
		pathList.add(RouteAlphabet.integer(otherElem1));
		pathList.add(RouteAlphabet.integer(lastElemValue));
		pathList.add(RouteAlphabet.integer(inValue));
		pathList.add(RouteAlphabet.integer(firstElemValue));
		pathList.add(RouteAlphabet.PATH);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList, acceptVal, alphabet);
		
		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree));

		//pathList2
		List<ILabel> pathList2 = new ArrayList<ILabel>();
		pathList2.add(RouteAlphabet.integer(lastElemValue));
		pathList2.add(RouteAlphabet.integer(otherElem1));
		pathList2.add(RouteAlphabet.integer(inValue));
		pathList2.add(RouteAlphabet.PATH);
		
		//comList2
		List<ILabel> comList2 = new ArrayList<ILabel>();
		comList2.add(RouteAlphabet.COM);
		
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
			destList, pathList2, prefVal, comList2, acceptVal, alphabet);
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree2));

		//pathList3
		List<ILabel> pathList3 = new ArrayList<ILabel>();
		pathList3.add(RouteAlphabet.integer(lastElemValue));
		pathList3.add(RouteAlphabet.integer(otherElem1));
		pathList3.add(RouteAlphabet.integer(inValue));
		pathList3.add(RouteAlphabet.PATH);
		
		//comList3
		List<ILabel> comList3 = new ArrayList<ILabel>();
		comList3.add(RouteAlphabet.integer(notinValue));
		comList3.add(RouteAlphabet.COM);
		
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
			destList, pathList3, prefVal, comList3, acceptVal, alphabet);
		
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree3));
}
	
	public void testDstIs1() {

		final IPredicate dstIs = new DstIs("192.168.12.128/17");
		final RouteAlphabet alphabet = alphabetForPredicate(dstIs);
		final PredicateAutomaton automaton = dstIs.automaton(alphabet);

		//destlist
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.addAll(revertBitSequence("11000000101010000"));
		destList.add(RouteAlphabet.DEST);

		//pathList
		List<ILabel> pathList = new ArrayList<ILabel>();
		pathList.add(RouteAlphabet.PATH);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree1 = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList, acceptVal, alphabet);
		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree1));

		// destList 2 (added a 0)
		List<ILabel> destList2 = new ArrayList<ILabel>();
		destList2.addAll(revertBitSequence("110000001010100000"));
		destList2.add(RouteAlphabet.DEST);
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
				destList2, pathList, prefVal, comList, acceptVal, alphabet);
			assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree2));

		// destList 3 (removed a 0)
		List<ILabel> destList3 = new ArrayList<ILabel>();
		destList3.addAll(revertBitSequence("1100000010101000"));
		destList3.add(RouteAlphabet.DEST);
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
			destList3, pathList, prefVal, comList, acceptVal, alphabet);
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree3));
	}
	
	public void testDstIs2() {

		final IPredicate dstIs = new DstIs("192.168.12.128/1");
		final RouteAlphabet alphabet = alphabetForPredicate(dstIs);
		final PredicateAutomaton automaton = dstIs.automaton(alphabet);

		//destlist
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.addAll(revertBitSequence("1"));
		destList.add(RouteAlphabet.DEST);

		//pathList
		List<ILabel> pathList = new ArrayList<ILabel>();
		pathList.add(RouteAlphabet.PATH);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree1 = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList, acceptVal, alphabet);
		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree1));

		// destList 2 (added a 0)
		List<ILabel> destList2 = new ArrayList<ILabel>();
		destList2.addAll(revertBitSequence("10"));
		destList2.add(RouteAlphabet.DEST);
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
				destList2, pathList, prefVal, comList, acceptVal, alphabet);
			assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree2));

		// destList 3 (removed a 1)
		List<ILabel> destList3 = new ArrayList<ILabel>();
		destList3.addAll(revertBitSequence(""));
		destList3.add(RouteAlphabet.DEST);
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
			destList3, pathList, prefVal, comList, acceptVal, alphabet);
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree3));
	}
	
	public void testDstIs3() {

		final IPredicate dstIs = new DstIs("192.168.12.128/32");
		final RouteAlphabet alphabet = alphabetForPredicate(dstIs);
		final PredicateAutomaton automaton = dstIs.automaton(alphabet);

		//destlist
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.addAll(revertBitSequence(
			"11000000101010000000110010000000"));
		destList.add(RouteAlphabet.DEST);

		//pathList
		List<ILabel> pathList = new ArrayList<ILabel>();
		pathList.add(RouteAlphabet.PATH);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree1 = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList, acceptVal, alphabet);
		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree1));

		// destList 2 (added a 0)
		List<ILabel> destList2 = new ArrayList<ILabel>();
		destList2.addAll(revertBitSequence(
			"110000001010100000001100100000000"));
		destList2.add(RouteAlphabet.DEST);
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
				destList2, pathList, prefVal, comList, acceptVal, alphabet);
			assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree2));

		// destList 3 (removed a 0)
		List<ILabel> destList3 = new ArrayList<ILabel>();
		destList3.addAll(revertBitSequence(
			"1100000010101000000011001000000"));
		destList3.add(RouteAlphabet.DEST);
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
			destList3, pathList, prefVal, comList, acceptVal, alphabet);
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree3));
	}
	
	public void testDstIn1() {

		final IPredicate dstIn = new DstIn("192.168.12.128/17");
		final RouteAlphabet alphabet = alphabetForPredicate(dstIn);
		final PredicateAutomaton automaton = dstIn.automaton(alphabet);

		//destlist
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.addAll(revertBitSequence("11000000101010000"));
		destList.add(RouteAlphabet.DEST);

		//pathList
		List<ILabel> pathList = new ArrayList<ILabel>();
		pathList.add(RouteAlphabet.PATH);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree1 = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList, acceptVal, alphabet);
		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree1));

		// destList 2 (added a 0)
		List<ILabel> destList2 = new ArrayList<ILabel>();
		destList2.addAll(revertBitSequence("110000001010100000"));
		destList2.add(RouteAlphabet.DEST);
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
				destList2, pathList, prefVal, comList, acceptVal, alphabet);
			assertTrue("The automaton should not accept the routing tree.",
				automaton.accepts(tree2));

		// destList 3 (removed a 0)
		List<ILabel> destList3 = new ArrayList<ILabel>();
		destList3.addAll(revertBitSequence("1100000010101000"));
		destList3.add(RouteAlphabet.DEST);
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
			destList3, pathList, prefVal, comList, acceptVal, alphabet);
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree3));

		// destList 4 (added 10)
		List<ILabel> destList4 = new ArrayList<ILabel>();
		destList4.addAll(revertBitSequence("1100000010101000010"));
		destList4.add(RouteAlphabet.DEST);
		ITerm<ILabel> tree4 = PredicateTree.getPredicateTree(
			destList4, pathList, prefVal, comList, acceptVal, alphabet);
		assertTrue("The automaton should not accept the routing tree.",
			automaton.accepts(tree4));
	}
	
	public void testDstIn2() {

		final IPredicate dstIn = new DstIn("192.168.12.128/1");
		final RouteAlphabet alphabet = alphabetForPredicate(dstIn);
		final PredicateAutomaton automaton = dstIn.automaton(alphabet);

		//destlist
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.addAll(revertBitSequence("1"));
		destList.add(RouteAlphabet.DEST);

		//pathList
		List<ILabel> pathList = new ArrayList<ILabel>();
		pathList.add(RouteAlphabet.PATH);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree1 = PredicateTree.getPredicateTree(
			destList, pathList, prefVal, comList, acceptVal, alphabet);
		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree1));

		// destList 2 (added a 0)
		List<ILabel> destList2 = new ArrayList<ILabel>();
		destList2.addAll(revertBitSequence("10"));
		destList2.add(RouteAlphabet.DEST);
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
			destList2, pathList, prefVal, comList, acceptVal, alphabet);
		assertTrue("The automaton should not accept the routing tree.",
			automaton.accepts(tree2));

		// destList 3 (removed 1)
		List<ILabel> destList3 = new ArrayList<ILabel>();
		destList3.addAll(revertBitSequence(""));
		destList3.add(RouteAlphabet.DEST);
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
			destList3, pathList, prefVal, comList, acceptVal, alphabet);
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree3));

		// destList 4 (added a lot...)
		List<ILabel> destList4 = new ArrayList<ILabel>();
		destList4.addAll(revertBitSequence("1100000010101000010"));
		destList4.add(RouteAlphabet.DEST);
		ITerm<ILabel> tree4 = PredicateTree.getPredicateTree(
			destList4, pathList, prefVal, comList, acceptVal, alphabet);
		assertTrue("The automaton should not accept the routing tree.",
			automaton.accepts(tree4));
	}

	public void testPathSubBorderLength() {
		int[] seq = {3,2,1,3,1};
		final PathSub pathSub = new PathSub(seq);
		int[] pattern1 = {3,2,1,3,3};
		assertEquals("Largest suffix length",
			1, pathSub.borderLength(pattern1, seq));
		int[] pattern2 = {3,2,1,3,2};
		assertEquals("Largest suffix length",
			2, pathSub.borderLength(pattern2, seq));
		int[] pattern3 = {3,2,1,3,1};
		assertEquals("Largest suffix length",
			5, pathSub.borderLength(pattern3, seq));
		int[] pattern4 = {3,1,2};
		assertEquals("Largest suffix length",
			0, pathSub.borderLength(pattern4, seq));
		int[] pattern5 = {};
		assertEquals("Largest suffix length",
			0, pathSub.borderLength(pattern5, seq));
		int[] pattern6 = {3,2,1,3,1,3,3,2,3};
		assertEquals("Largest suffix length",
			1, pathSub.borderLength(pattern6, seq));

		int[] seq2 = {};
		final PathSub pathSub2 = new PathSub(seq2);
		int[] pattern27 = {3,2,1,3,1};
		assertEquals("Largest suffix length",
			0, pathSub2.borderLength(pattern27, seq2));
}
	
	public void testPathSub() {

		final int seqValue1 = 40;
		final int seqValue2 = 20;
		final int seqValue3 = 30;
		final int otherElem1 = 10;
		final int otherElem2 = 25;

		int[] seq1 = {seqValue1, seqValue2, seqValue3};
				
		final PathSub pathSub = new PathSub(seq1);
		final IAction pathAction1 = new PathPrepend(otherElem1);
		final IAction pathAction2 = new PathPrepend(otherElem2);
		
		RouteAlphabet alphabet = alphabetForPredicate(pathSub);
		alphabet = alphabet.union(
			new Filter(pathAction1, pathAction2).filterAlphabet());
		final IDTA<ILabel, IState> automaton = pathSub.automaton(alphabet);
				
		//destlist
		List<ILabel> destList = new ArrayList<ILabel>();
		destList.add(RouteAlphabet.DEST);

		//pathList1
		int[] path1 = {otherElem1, otherElem2, seqValue1, otherElem1};
		List<ILabel> pathList1 = pathFromInts(path1);
		
		//prefval
		ILabel prefVal = RouteAlphabet.integer(Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<ILabel> comList = new ArrayList<ILabel>();
		comList.add(RouteAlphabet.COM);
		
		//accept val
		ILabel acceptVal = RouteAlphabet.MODIFIED;
		
		ITerm<ILabel> tree1 = PredicateTree.getPredicateTree(
			destList, pathList1, prefVal, comList, acceptVal, alphabet);
		
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree1));

		//pathList2
		int[] path2 = {otherElem1, seqValue3, seqValue2, seqValue1, otherElem1};
		List<ILabel> pathList2 = pathFromInts(path2);
		ITerm<ILabel> tree2 = PredicateTree.getPredicateTree(
			destList, pathList2, prefVal, comList, acceptVal, alphabet);
		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree2));

		//pathList3
		int[] path3 = {seqValue3, seqValue2, seqValue1};
		List<ILabel> pathList3 = pathFromInts(path3);
		ITerm<ILabel> tree3 = PredicateTree.getPredicateTree(
			destList, pathList3, prefVal, comList, acceptVal, alphabet);
		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree3));

		//pathList4
		int[] path4 = {seqValue1, seqValue2, otherElem1, seqValue3};
		List<ILabel> pathList4 = pathFromInts(path4);
		ITerm<ILabel> tree4 = PredicateTree.getPredicateTree(
			destList, pathList4, prefVal, comList, acceptVal, alphabet);
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree4));
	}

	/**
	 * Method to transform an array of integer to an AS-path.
	 */
	private List<ILabel> pathFromInts(int[] intSeq) {
		List<ILabel> pathList = new ArrayList<ILabel>();
		for (int i=0; i<intSeq.length; i++) {
			pathList.add(RouteAlphabet.integer(intSeq[i]));
		}
		pathList.add(RouteAlphabet.PATH);
		return pathList;
	}
	
	/**
	 * Small method to use the method in Filter that builds the alphabet, and
	 * apply it to a single predicate.
	 */
	private RouteAlphabet alphabetForPredicate(IPredicate predicate) {
		final IFilterRule rule = new FilterRule(predicate, null);
		final Filter filter = new Filter(Collections.singletonList(rule));
		return filter.filterAlphabet();
	}
	
	/**
	 * Converts a String of 0 and 1 to the corresponding IntegerLabel sequence,
	 * and reverses it.
	 */
	private List<IntegerLabel> revertBitSequence(String bits) {
		List<IntegerLabel> intList = new ArrayList<IntegerLabel>();
		for (int i=bits.length()-1; i>=0; i--) {
			int bit = bits.charAt(i);
			if (bit!='0' && bit!='1') {
				throw new IllegalStateException(
					"Bits should be 0 or 1, instead of " + bit);
			}
			intList.add(new IntegerLabel(bit=='0'?0:1));
		}
		return intList;
	}
}
