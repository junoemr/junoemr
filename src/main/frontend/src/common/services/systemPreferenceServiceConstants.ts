/*
* Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
* This software was written for
* CloudPractice Inc.
* Victoria, British Columbia
* Canada
*/

/**
 * keys for the instance properties file
 */
export enum SystemProperties
{
    InstanceType = "instance_type",
    BillingType = "billing_type",
    DefaultTicklerProvider = "default_tickler_provider",
    Multisites = "multisites",
    MHATelehealthEnabled = "myhealthaccess_telehealth_enabled",
}

/**
 * keys for the property table, associated with the system provider.
 */
export enum SystemPreferences
{
    // GENERAL PREFERENCES
    DefaultPhonePrefix = "phone_prefix",
    RosteringModule = "enable_family_doctor_and_rostering",
    ExtraAddressField = "enable_additional_address",

    // RX PREFERENCES
    AytoStampRxSignatures = "rx_preset_signatures",

    // BILLING PREFERENCES
    ServiceLocationCode = "service_location_code",

    // AQS
    AqsOrganizationId = "aqs_organization_id",
    AqsOrganizationSecret = "aqs_api_secret_key",

    // UI
    UiCustomNavIcon = "ui_custom_nav",
    UiLockToJunoUi = "ui_lock",

    // FAXING
    ActiveFaxAccount = "system_active_fax_account_id",

    // misc integrations
    AQSEnabled = "aqs_enabled",
    CareConnectEnabled = "integration.CareConnect.enabled",
    IcefallEnabled = "icefall_enabled",
    IMDHealthEnabled = "integration.imdHealth.enabled",
    K2AEnabled = "integration.know2Act.enabled",
    NetcareEnabled = "integration.netcare.enabled",
    OceanToolbarEnabled = "integration.OceanToolBar.enabled",
}

export enum SystemPreferences
{
    OlisIntegrationEnabled = "integration.olis.enabled",
    OlisEmrId = "integration.olis.emr_id",
    OlisPollingEnabled = "olis_polling_enabled",

		HrmEnabled = "integration.hrm.enabled",
		HrmPollingEnabled = "omd.hrm.polling_enabled",
		HrmPollingInterval = "omd.hrm.polling_interval_sec",
		HrmUser = "omd.hrm.user",
		HrmMailBoxAddress = "omd.hrm.address",
		HrmRemotePath = "omd.hrm.remote_path",
		HrmPort = "omd.hrm.port",
}

export enum INSTANCE_TYPE
{
    BC = "BC",
    ON = "ON",
    AB = "AB",
    SK = "SK",
    PEI = "PE",
}

export enum BILLING_TYPE {
    BC = "BC",
    ON = "ON",
    CLINICAID = "CLINICAID"
}
