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

/**
 * Decrements the local preference, with a given step.
 */
public final class PrefSub extends PrefAdd {
	
	/**
	 * Constructor
	 */
	public PrefSub(final int decrement) {
		super(decrement);
		if (decrement < 0) {
			throw new IllegalStateException(
				"Decrement value should be positive, instead of "+decrement);
		}
	}
	
	/**
	 * Returns the output local pref value, for a given input local pref value.
	 */
	@Override
	public int localPrefImage(final int inputLocalPref) {
		int image = 0;
		if (this.increment < inputLocalPref) {
			image = inputLocalPref - this.increment;
		}
		return image;
	}

	@Override
	public String toString() {
		return "Decrement Local Preference value of " + this.increment;
	}
}
