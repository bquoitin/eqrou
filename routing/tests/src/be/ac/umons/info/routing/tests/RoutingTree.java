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

import be.ac.umons.info.routing.automata.ActionAlphabet;
import be.ac.umons.info.routing.automata.LabelPair;
import traul.ranked.terms.ITerm;
import traul.ranked.terms.Term;

/**
 * Builds a tree over ActionAlphabet, from its branches.
 */
public class RoutingTree {
	
	/**
	 * Builds a tree corresponding to a pair (t,t') from its branches. 
	 */
	public static ITerm<LabelPair> getRoutingTree(
			final List<LabelPair> destList, final List<LabelPair> pathList, 
			final LabelPair prefVal, final List<LabelPair> comList,
			final LabelPair accept, final ActionAlphabet alphabet) {	

		// Dest branch
		ITerm<LabelPair> nodeDest = listToTerm(destList, alphabet);
		
		// AS-path branch
		ITerm<LabelPair> nodePath = listToTerm(pathList, alphabet);

		// Pref	branch
		ITerm<LabelPair> leafPref = new Term<LabelPair>(alphabet, null,
			ActionAlphabet.PREFPREF);
		ITerm<LabelPair> nodePref = new Term<LabelPair>(
			alphabet, Collections.singletonList(leafPref), prefVal);
		
		// Com branch
		ITerm<LabelPair> nodeCom = listToTerm(comList, alphabet);
		
		// Mod branch
		ITerm<LabelPair> nodeMod = new Term<LabelPair>(alphabet, null, accept);
		
		List<ITerm<LabelPair>> childrenRoot = 
			new ArrayList<ITerm<LabelPair>>();
		childrenRoot.add(nodeDest);
		childrenRoot.add(nodePath);
		childrenRoot.add(nodePref);
		childrenRoot.add(nodeCom);		
		childrenRoot.add(nodeMod);
		
		return new Term<LabelPair>(
				alphabet, childrenRoot, ActionAlphabet.RR);
	}

	private static ITerm<LabelPair> listToTerm(final List<LabelPair> list,
			final ActionAlphabet alphabet) {
		final ITerm<LabelPair> leaf = 
			new Term<LabelPair>(alphabet, null, list.get(list.size()-1));
		List<ITerm<LabelPair>> children = Collections.singletonList(leaf);
		for (int i=list.size()-2; i>=0; i--) {
			final ITerm<LabelPair> node = 
				new Term<LabelPair>(alphabet, children, list.get(i));
			children = Collections.singletonList(node);
		}
		return children.get(0);
	}
}
