/**
 * Copyright (C) 2016, Laboratorio di Valutazione delle Prestazioni - Politecnico di Milano

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package jmt.jteach.animation;

import jmt.jteach.ConstantsJTch;

/**
 * Enum for the type of Routing Policy.
 * The behaviour of the policy is then implemented in the Router
 *
 * @author Lorenzo Torri
 * Date: 02-apr-2024
 * Time: 12.19
 */
public enum RoutingPolicy {
	RR{
		@Override
		public String toString() {
			return "Round Robin";
		}

		@Override
		public String getDescription() {
			return ConstantsJTch.RR_DESCRIPTION;
		}
	}, 
	PROBABILISTIC{
		@Override
		public String toString() {
			return "Probabilistic";
		}

		@Override
		public String getDescription() {
			return ConstantsJTch.PROB_DESCRIPTION;
		}
	},
	JSQ{
		@Override
		public String toString() {
			return "Join Shortest Job Queue";
		}

		@Override
		public String getDescription() {
			return ConstantsJTch.JSQ_DESCRIPTION;
		}
	};

	public abstract String toString();
	public abstract String getDescription();
}
