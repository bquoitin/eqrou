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
import java.util.List;
import java.util.StringTokenizer;

/**
 * An IP prefix, given as an IP address with a mask length, such as:
 * 192.168.128.0/17. 
 */
public class IPPrefix {

	/**
	 * The list of integers (0 or 1) for this IP prefix. The length of the list
	 * is between 0 and 32.
	 */
	private final List<Integer> prefix;
	
	/**
	 * A constructor, from a String like "192.168.128.0/17".
	 */
	public IPPrefix(final String ipWithMask) {
		this.prefix = parseIpWithMask(ipWithMask);
	}
	
	public List<Integer> intList() {
		return this.prefix;
	}
	
	/**
	 * Converts an IP/mask-length to the corresponding sequence of bits, of
	 * length mask-length.
	 */
	private List<Integer> parseIpWithMask(String prefixString) {
		StringTokenizer tokenizer = new StringTokenizer(prefixString, "/");
		if (tokenizer.countTokens() != 2) {
			throw new IllegalStateException("IP prefix should be written as "
					+"192.168.128.0/17, i.e. IP/mask length");
		}
		// IP
		String ip = tokenizer.nextToken();
		StringTokenizer ipTokenizer = new StringTokenizer(ip, ".");
		if (ipTokenizer.countTokens() != 4) {
			throw new IllegalStateException("IP prefix should be given by 4" +
					"integers separated by .");
		}
		List<Integer> fullIP = new ArrayList<Integer>();
		while (ipTokenizer.hasMoreElements()) {
			Integer octet = new Integer(ipTokenizer.nextToken());
			if (octet<0 || octet>255) {
				throw new IllegalStateException("Each octet should be between "
					+ "0 and 255, instead of " + octet);
			}
			fullIP.addAll(convertToIntList(octet));
		}
		// Mask
		Integer maskLength = new Integer(tokenizer.nextToken());
		if (maskLength<1 || maskLength>32) {
			throw new IllegalStateException("Mask length should be between "
					+ "1 and 32, instead of "+maskLength);
		}		
		return fullIP.subList(0, maskLength);
	}
	
	/**
	 * Converts an octet to its series of bits.
	 */
	private List<Integer> convertToIntList(final Integer octet) {
		String binary = Integer.toBinaryString(octet);
		int binLength = binary.length();
		List<Integer> bitList = new ArrayList<Integer>();
		for (int i=0; i<8-binLength; i++) {
			bitList.add(0);
		}
		for (int i=0; i<binLength; i++) {
			bitList.add(binary.charAt(i)=='0'?0:1);
		}
		return bitList;
	}
}
