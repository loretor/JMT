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
 * Enum for the type of Queue Policy.
 * Those types of queue are then implemented with one of the classes that extends the CustomCollection interface
 *
 * @author Lorenzo Torri
 * Date: 25-mar-2024
 * Time: 12.19
 */
public enum QueuePolicyNonPreemptive {
	FIFO{
		@Override
		public String toString() {
			return "FCFS";
		}

		@Override
		public String getDescription() {
			return ConstantsJTch.FIFO_DESCRIPTION;
		}
		
	}, 
	LIFO{
		@Override
		public String toString() {
			return "LCFS";
		}

		@Override
		public String getDescription() {
			return ConstantsJTch.LIFO_DESCRIPTION;
		}
	}, 
	PRIO{
		@Override
		public String toString() {
			return "Priority";
		}

		@Override
		public String getDescription() {
			return ConstantsJTch.PRIO_DESCRIPTION;
		}
	},
	SJF{
		@Override
		public String toString() {
			return "SJF";
		}

		@Override
		public String getDescription() {
			return ConstantsJTch.SJF_DESCRIPTION;
		}
	},
	LJF{
		@Override
		public String toString() {
			return "LJF";
		}

		@Override
		public String getDescription() {
			return ConstantsJTch.LJF_DESCRIPTION;
		}
	};
	
	public abstract String toString(); //to covert each enum to its policy name
	public abstract String getDescription(); //to get for each enum a description on how the policy works
}
