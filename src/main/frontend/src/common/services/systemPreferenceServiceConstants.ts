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

export enum SYSTEM_PROPERTIES
{
    INSTANCE_TYPE = "instance_type",
    BILLING_TYPE = "billing_type",

    // GENERAL PREFERENCES
    DEFAULT_PHONE_PREFIX = "phone_prefix",
    ROSTERING_MODULE = "enable_family_doctor_and_rostering",
    EXTRA_ADDRESS_FIELD = "enable_additional_address",

    // RX PREFERENCES
    AUTO_STAMP_RX_SIGNATURES = "rx_preset_signatures",

    // BILLING PREFERENCES
    SERVICE_LOCATION_CODE = "service_location_code",

    // AQS
    AQS_ORGANIZATION_ID = "aqs_organization_id",
    AQS_ORGANIZATION_SECRET = "aqs_api_secret_key",

    // UI
    UI_CUSTOM_NAV_ICON = "ui_custom_nav",
    UI_LOCK_TO_JUNO_UI = "ui_lock",

    // FAXING
    ACTIVE_FAX_ACCOUNT = "system_active_fax_account_id",
}

export enum SystemPreferences
{
    // misc integrations
    AQSEnabled = "aqs_enabled",
    CareConnectEnabled = "integration.CareConnect.enabled",
    IcefallEnabled = "icefall_enabled",
    IMDHealthEnabled = "integration.imdHealth.enabled",
    K2AEnabled = "integration.know2Act.enabled",
    NetcareEnabled = "integration.netcare.enabled",
    OceanToolbarEnabled = "integration.OceanToolBar.enabled",
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