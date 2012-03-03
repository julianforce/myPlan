/*
 * This file is part of myPlan.
 *
 * Plan is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Plan is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with myPlan.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.conzebit.myplan.ext.es.masmovil.particulares;

import java.util.ArrayList;
import com.conzebit.myplan.core.Chargeable;
import com.conzebit.myplan.core.call.Call;
import com.conzebit.myplan.core.message.ChargeableMessage;
import com.conzebit.myplan.core.msisdn.MsisdnType;
import com.conzebit.myplan.core.plan.PlanChargeable;
import com.conzebit.myplan.core.plan.PlanSummary;
import com.conzebit.myplan.core.plan.PlanChargeable.Type;
import com.conzebit.myplan.core.sms.Sms;
import com.conzebit.myplan.ext.es.masmovil.ESMasMovil;


/**
 * Masmovil Tarifa Light + BONO 1 GB
 * @author DarkEagle82 aka Sergi (sergi at nucl3ar dot net)
 */
public class ESMasMovilTarifaLight1GB extends ESMasMovil {
    
	private double monthFee = 14.9;
	private double initialPrice = 0.15;
	private double pricePerSecond = 0.024 / 60;
	private double smsPrice = 0.08;
    
	public String getPlanName() {
		return "Tarifa @Light + BONO 1GB";
	}
	
	public String getPlanURL() {
		return "http://promociones.masmovil.es/moviltoday-light";
	}
	
	public Double getMonthFee() {
		return monthFee;
	}
	
	public PlanSummary process(ArrayList<Chargeable> data) {
		PlanSummary ret = new PlanSummary(this);
		ret.addPlanCall(new PlanChargeable(new ChargeableMessage(ChargeableMessage.MESSAGE_MONTH_FEE), monthFee, this.getCurrency(), Type.MONTH_FEE));
		for (Chargeable chargeable : data) {
			if (chargeable.getChargeableType() == Chargeable.CHARGEABLE_TYPE_CALL) {
				Call call = (Call) chargeable;

				if (call.getType() != Call.CALL_TYPE_SENT) {
					continue;
				}
	
				double callPrice = 0;
				PlanChargeable.Type type = null;

				if (call.getContact().getMsisdnType() == MsisdnType.ES_SPECIAL_ZER0) {
					callPrice = 0;
					type = Type.ZERO;
				} else {
					callPrice = initialPrice + (call.getDuration() * pricePerSecond);
					type = Type.INSIDE_PLAN;
				}
				ret.addPlanCall(new PlanChargeable(call, callPrice, this.getCurrency(), type));
			} else if (chargeable.getChargeableType() == Chargeable.CHARGEABLE_TYPE_SMS) {
				Sms sms = (Sms) chargeable;
				if (sms.getType() == Sms.SMS_TYPE_RECEIVED) {
					continue;
				}
				ret.addPlanCall(new PlanChargeable(chargeable, smsPrice, this.getCurrency(), Type.INSIDE_PLAN));
			}
		}
		return ret;
	}
}