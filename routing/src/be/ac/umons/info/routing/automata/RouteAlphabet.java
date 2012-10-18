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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import traul.ranked.nta.DTA;
import traul.ranked.nta.IDTA;
import traul.ranked.nta.labels.Alphabet;
import traul.ranked.nta.labels.ILabel;
import traul.ranked.nta.labels.StringLabel;
import traul.ranked.nta.rules.BasicRule;
import traul.ranked.nta.rules.IRule;

public class RouteAlphabet extends Alphabet<ILabel> {

	public static final StringLabel R = new StringLabel("R");
	public static final StringLabel R_BAR = new StringLabel("R_BAR");
	public static final StringLabel DEST = new StringLabel("DEST");
	public static final StringLabel PATH = new StringLabel("PATH");
	public static final StringLabel PREF = new StringLabel("PREF");
	public static final StringLabel COM  = new StringLabel("COM");
	public static final StringLabel ACCEPTED = new StringLabel("ACCEPTED");
	public static final StringLabel REJECTED = new StringLabel("REJECTED");
	public static final StringLabel MODIFIED = new StringLabel("MODIFIED");
	public static final ILabel PATH1 = new UnaryVersionLabel(PATH);
	public static final ILabel COM1  = new UnaryVersionLabel(COM);
	public static final StringLabel DIAMOND  = new StringLabel("DIAMOND");
	public static final ILabel DIAMOND1 = new UnaryVersionLabel(DIAMOND);

	private final Set<IntegerLabel> destAlphabet;
	private final Set<IntegerLabel> asPathAlphabet;
	private final Set<IntegerLabel> prefAlphabet;
	private final Set<IntegerLabel> comAlphabet;
	
	/**
	 * Constructor.
	 */
	public RouteAlphabet(
		final Set<IntegerLabel> destAlphabet,
		final Set<IntegerLabel> asPathAlphabet,
		final Set<IntegerLabel> prefAlphabet,
		final Set<IntegerLabel> comAlphabet) {
		super(RouteAlphabet.routeSymbols(
				destAlphabet, asPathAlphabet, prefAlphabet, comAlphabet));
		this.destAlphabet = destAlphabet;
		this.asPathAlphabet = asPathAlphabet;
		this.prefAlphabet = prefAlphabet;
		this.comAlphabet = comAlphabet;
	}

	/**
	 * All symbols used in a route.
	 */
	public static Map<ILabel,Integer> routeSymbols(
			final Set<IntegerLabel> destAlphabet,
			final Set<IntegerLabel> asPathAlphabet,
			final Set<IntegerLabel> prefAlphabet,
			final Set<IntegerLabel> comAlphabet) {
		Map<ILabel,Integer> routeSymbols = new HashMap<ILabel,Integer>();		
		// root
		routeSymbols.put(R, 5);
		routeSymbols.put(R_BAR, 5);
		// unary symbols
		Set<ILabel> unaryLabels = new HashSet<ILabel>();
		unaryLabels.addAll(destAlphabet);
		unaryLabels.addAll(asPathAlphabet);
		unaryLabels.addAll(prefAlphabet);
		unaryLabels.addAll(comAlphabet);
		unaryLabels.add(PATH1);
		unaryLabels.add(COM1);
		unaryLabels.add(DIAMOND1);
		for (ILabel unaryLabel : unaryLabels) {
			routeSymbols.put(unaryLabel, 1);
		}
		// leaves
		routeSymbols.put(DEST, 0);
		routeSymbols.put(PATH, 0);
		routeSymbols.put(PREF, 0);
		routeSymbols.put(COM, 0);
		routeSymbols.put(ACCEPTED, 0);
		routeSymbols.put(REJECTED, 0);
		routeSymbols.put(MODIFIED, 0);
		routeSymbols.put(DIAMOND, 0);
		return routeSymbols;
	}
	
	/**
	 * Builds a new RouteAlphabet containing this one plus labels from another
	 * one.
	 */
	public RouteAlphabet union(final RouteAlphabet otherAlphabet) {
		// DEST branch
		Set<IntegerLabel> newDestAlphabet = new HashSet<IntegerLabel>();
		newDestAlphabet.addAll(this.destAlphabet());
		newDestAlphabet.addAll(otherAlphabet.destAlphabet());
		// PATH branch
		Set<IntegerLabel> newAsPathAlphabet = new HashSet<IntegerLabel>();
		newAsPathAlphabet.addAll(this.asPathAlphabet());
		newAsPathAlphabet.addAll(otherAlphabet.asPathAlphabet());
		// PREF branch
		Set<IntegerLabel> newPrefAlphabet = new HashSet<IntegerLabel>();
		newPrefAlphabet.addAll(this.prefAlphabet());
		newPrefAlphabet.addAll(otherAlphabet.prefAlphabet());
		// COM branch
		Set<IntegerLabel> newComAlphabet = new HashSet<IntegerLabel>();
		newComAlphabet.addAll(this.comAlphabet());
		newComAlphabet.addAll(otherAlphabet.comAlphabet());
		return new RouteAlphabet(newDestAlphabet, newAsPathAlphabet, 
				newPrefAlphabet, newComAlphabet);
	}
	
	/**
	 * Returns the label encoding a given integer value i.
	 * @param i
	 * @return the label for i
	 */
	public static IntegerLabel integer(final int i) {
		return new IntegerLabel(i);
	}

	/**
	 * An automaton recognizing exactly all correct routes, without 
	 * (diamond,diamond). This automaton is not complete: it is blocking on 
	 * trees that are not routes.
	 * 
	 * Method {@link ValidRoutePairs.routePairs} is assumed to return a 
	 * deterministic automaton.
	 * @return a DTA recognizing all correct routes
	 */
	public IDTA<ILabel, FilterState> allRoutesDTA() {
		
		final FilterAutomaton routePairs = ValidRoutePairs.routePairs(this);
		Set<IRule<ILabel,FilterState>> rules = 
			new HashSet<IRule<ILabel,FilterState>>();
		for (IRule<LabelPair,FilterState> rule : routePairs.getRules(false)) {
			rules.add(new BasicRule<ILabel, FilterState>(
					rule.leftStates(), rule.label().left(), rule.rightState()));
		}
		return new DTA<ILabel,FilterState>(this, routePairs.getStates(), 
				routePairs.getFinalStates(), rules, routePairs.sinkState());
	}
	
	public Set<IntegerLabel> destAlphabet()   { return this.destAlphabet; }
	public Set<IntegerLabel> asPathAlphabet() { return this.asPathAlphabet; }
	public Set<IntegerLabel> prefAlphabet()   { return this.prefAlphabet; }
	public Set<IntegerLabel> comAlphabet()    { return this.comAlphabet; }

	public Set<Integer> destAlphabetInt() { 
		return toIntegerSet(this.destAlphabet); 
	}
	
	public Set<Integer> asPathAlphabetInt() { 
		return toIntegerSet(this.asPathAlphabet); 
	}
	
	public Set<Integer> prefAlphabetInt() { 
		return toIntegerSet(this.prefAlphabet);
	}
	
	public Set<Integer> comAlphabetInt() { 
		return toIntegerSet(this.comAlphabet); 
	}

	private Set<Integer> toIntegerSet(final Set<IntegerLabel> intLabelSet) {
		Set<Integer> ints = new HashSet<Integer>();
		for (IntegerLabel intLabel : intLabelSet) {
			ints.add(intLabel.intValue());
		}
		return ints;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\ndestAlphabet:\n").append(
				branchLabels(this.destAlphabet()));
		sb.append("\npathAlphabet:\n").append(
				branchLabels(this.asPathAlphabet()));
		sb.append("\nprefAlphabet:\n").append(
				branchLabels(this.prefAlphabet()));
		sb.append("\ncomAlphabet:\n").append(
				branchLabels(this.comAlphabet()));
		sb.append("\noverall:\n").append(super.toString());
		return sb.toString();
	}
	
	private StringBuffer branchLabels(Set<IntegerLabel> branchLabels) {
		StringBuffer sb = new StringBuffer();
		for (IntegerLabel i : branchLabels) {
			sb.append(i+"("+this.arity(i)+"),");
		}
		return sb;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RouteAlphabet other = (RouteAlphabet) obj;
		if (asPathAlphabet == null) {
			if (other.asPathAlphabet != null)
				return false;
		} else if (!asPathAlphabet.equals(other.asPathAlphabet))
			return false;
		if (comAlphabet == null) {
			if (other.comAlphabet != null)
				return false;
		} else if (!comAlphabet.equals(other.comAlphabet))
			return false;
		if (destAlphabet == null) {
			if (other.destAlphabet != null)
				return false;
		} else if (!destAlphabet.equals(other.destAlphabet))
			return false;
		if (prefAlphabet == null) {
			if (other.prefAlphabet != null)
				return false;
		} else if (!prefAlphabet.equals(other.prefAlphabet))
			return false;
		return true;
	}	
}
