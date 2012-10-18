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

package be.ac.umons.info.routing.automata;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import traul.ranked.nta.rules.BasicRule;
import traul.ranked.nta.rules.IRule;

/**
 * This class implements an automaton recognizing all pairs (t,t) where t is a 
 * valid route. By valid we mean: branches as usual, and ordered in COM.
 */
public class ValidRoutePairs {

	/**
	 * An automaton recognizing only (overlapping of) pairs (t,t) of valid
	 * routes. 
	 * @param routeAlphabet the alphabet of routing trees
	 * @return an automaton recognizing only pairs of valid routes
	 */
	public static FilterAutomaton routePairs(final RouteAlphabet routeAlphabet) {

		Set<FilterState> states = new HashSet<FilterState>();
		final FilterState sink = new FilterState("sink");
		states.add(sink);

		// dest branch
		final FilterState qDest = new FilterState("qDest");
		states.add(qDest);
		Set<IRule<LabelPair, FilterState>> rulesDest = 
			new HashSet<IRule<LabelPair, FilterState>>();
		rulesDest.add(new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.DESTDEST, qDest));
		if (routeAlphabet.destAlphabet().size()>0) {
			for (IntegerLabel intLabel : routeAlphabet.destAlphabet()) {
				final int i = intLabel.intValue(); 
				rulesDest.add(new BasicRule<LabelPair,FilterState>(
					ActionAlphabet.integerinteger(i, i), qDest, qDest));
			}
		}
		// AS-path branch
		final FilterState qPath = new FilterState("qPath");
		states.add(qPath);
		Set<IRule<LabelPair, FilterState>> rulesPath = 
			new HashSet<IRule<LabelPair, FilterState>>();
		rulesPath.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.PATHPATH, qPath));
		for (IntegerLabel intLabel : routeAlphabet.asPathAlphabet()) {
			final int i = intLabel.intValue();
			rulesPath.add(new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.integerinteger(i, i), qPath, qPath));
		}
		// Pref branch
		final FilterState qPrefLeaf = new FilterState("qPrefLeaf");
		final FilterState qPref = new FilterState("qPref");
		states.add(qPrefLeaf);
		states.add(qPref);
		Set<IRule<LabelPair, FilterState>> rulesPref = 
			new HashSet<IRule<LabelPair, FilterState>>();
		rulesPref.add(new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.PREFPREF, qPrefLeaf));
		for (IntegerLabel intLabel : routeAlphabet.prefAlphabet()) {
			final int i = intLabel.intValue();
			rulesPref.add(new BasicRule<LabelPair,FilterState>(
					ActionAlphabet.integerinteger(i,i), qPref, qPrefLeaf));
		}
		// Com branch
		final FilterState qCom = new FilterState("qCom");
		states.add(qCom);
		Set<IRule<LabelPair, FilterState>> rulesCom = 
			new HashSet<IRule<LabelPair, FilterState>>();
		Map<Integer,FilterState> intStatesCom = new HashMap<Integer,FilterState>();
		for (IntegerLabel intLabel : routeAlphabet.comAlphabet()) {
			final int i = intLabel.intValue();
			intStatesCom.put(i, new FilterState("qCom_"+i));
		}
		states.addAll(intStatesCom.values());
		rulesCom.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.COMCOM, qCom));
		for (IntegerLabel intLabeli : routeAlphabet.comAlphabet()) {
			final int i = intLabeli.intValue();
			for (IntegerLabel intLabelj : routeAlphabet.comAlphabet()) {
				final int j = intLabelj.intValue();
				if (j>i) {
					rulesCom.add(new BasicRule<LabelPair,FilterState>(
						ActionAlphabet.integerinteger(i, i), 
						intStatesCom.get(i), intStatesCom.get(j)));
				}
				rulesCom.add(new BasicRule<LabelPair,FilterState>(
					ActionAlphabet.integerinteger(i, i), 
					intStatesCom.get(i), qCom));
			}
		}		
		// mod branch
		final FilterState qMod = new FilterState("qMod");
		states.add(qMod);
		Set<IRule<LabelPair, FilterState>> rulesMod = 
			new HashSet<IRule<LabelPair, FilterState>>();
		rulesMod.add(new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.ACCACC, qMod));
		rulesMod.add(new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.REJREJ, qMod));
		rulesMod.add(new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.MODMOD, qMod));
		// root
		final FilterState qRoot = new FilterState("qRoot");
		states.add(qRoot);
		Set<IRule<LabelPair, FilterState>> rulesRoot = 
			new HashSet<IRule<LabelPair, FilterState>>();
		rulesRoot.add(new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.RR, qRoot, qDest, qPath, qPref, qCom, qMod));
		for (IntegerLabel intLabel : routeAlphabet.comAlphabet()) {
			final int i = intLabel.intValue();
			rulesRoot.add(new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.RR, qRoot, qDest, qPath, qPref, 
				intStatesCom.get(i), qMod));
		}

		return new FilterAutomaton(new ActionAlphabet(routeAlphabet), states, 
				Collections.singleton(qRoot), sink,
				rulesDest, rulesPath, rulesPref, rulesCom, rulesMod, rulesRoot);
	}
}
