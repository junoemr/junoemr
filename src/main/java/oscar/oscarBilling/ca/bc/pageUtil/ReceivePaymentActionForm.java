/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package oscar.oscarBilling.ca.bc.pageUtil;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.oscarehr.util.MiscUtils;

import oscar.util.StringUtils;

public class ReceivePaymentActionForm
    extends ActionForm {
  private String amountReceived;
  private String payment;
  private String paymentMethod;
  private List paymentMethodList;
  private String billingmasterNo;
  private String billNo;
  private boolean paymentReceived;
  private String paymentDate;
  private Timestamp paymentTimestamp;
  private String isRefund;
  private String payeeProviderNo;
  public String getAmountReceived() {
    return amountReceived;
  }

  public void setAmountReceived(String amountReceived) {
    this.amountReceived = amountReceived;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public void setPayment(String payment) {
    this.payment = payment;
  }

  public void setPaymentMethodList(List paymentMethodList) {
    this.paymentMethodList = paymentMethodList;
  }

  public void setBillingmasterNo(String billingmasterNo) {
    this.billingmasterNo = billingmasterNo;
  }

  public void setBillNo(String billNo) {
    this.billNo = billNo;
  }

  public void setPaymentDate(String paymentDate) {
    this.paymentDate = paymentDate;
  }

  public void setPaymentReceived(boolean paymentReceived) {
    this.paymentReceived = paymentReceived;
  }

  public void setIsRefund(String isRefund) {

    this.isRefund = isRefund;
  }

  public void setPayeeProviderNo(String payeeProviderNo) {
    this.payeeProviderNo = payeeProviderNo;
  }

  public String getPayment() {
    return payment;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public List getPaymentMethodList() {
    return paymentMethodList;
  }

  public String getBillingmasterNo() {
    return billingmasterNo;
  }

  public String getBillNo() {
    return billNo;
  }
  
  public String getPaymentDate() {
	return paymentDate;
  }

  public boolean isPaymentReceived() {
    return paymentReceived;
  }

  public String getIsRefund() {
    return isRefund;
  }

  public String getPayeeProviderNo() {
    return payeeProviderNo;
  }

  public Timestamp getPaymentTimestamp() {
	
	if( this.paymentDate.trim().length() == 0 ){
		return null;
	}
	
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	Date paymentdate = null;
	paymentTimestamp = null;
	
    try {
        paymentdate = dateFormat.parse(this.paymentDate);
        paymentTimestamp = new Timestamp(paymentdate.getTime());
    } catch (ParseException e) {
    	MiscUtils.getLogger().error("Parsing Error", e);
    } 
    return paymentTimestamp;
  }

  public void setPaymentTimestamp(Timestamp paymentTimestamp) {
    this.paymentTimestamp = paymentTimestamp;
  }

public ActionErrors validate(ActionMapping actionMapping,
                               HttpServletRequest httpServletRequest) {
    ActionErrors errors = new ActionErrors();
    if (!StringUtils.isNumeric(this.getAmountReceived())) {
      errors.add("",
                 new ActionMessage(
                     "oscar.billing.CA.BC.billingBC.receivePayment.error.amtReceived"));
    }
    return errors;
  }
}
