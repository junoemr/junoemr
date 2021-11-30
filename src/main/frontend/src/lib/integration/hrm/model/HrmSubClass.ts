import {HrmSubClassModel, HRMSubClassTransferInbound} from "../../../../../generated";
import HrmCategory from "./HRMCategory";

export enum HrmReportClass
{
  MEDICAL_RECORDS = "Medical Records Report",
  CARDIO_RESPIRATORY = "Cardio Respiratory Report",
  DIAGNOSTIC_IMAGING = "Diagnostic Imaging Report",
}

export default class HrmSubClass
{
  private _id: number;
  private _hrmCategoryId: number;
  private _facilityNumber: string;
  private _reportClass: HrmReportClass;
  private _subClassName: string;
  private _accompanyingSubClassName: string;

  public static fromTransfer(transfer: HrmSubClassModel): HrmSubClass
  {
    if (transfer == null)
    {
      return null;
    }

    const hrmSubClass = new HrmSubClass();
    hrmSubClass._hrmCategoryId = transfer.hrmCategoryId;
    hrmSubClass._facilityNumber = transfer.facilityNumber;
    hrmSubClass._reportClass = transfer.className as HrmReportClass;
    hrmSubClass._subClassName = transfer.subClassName;
    hrmSubClass._accompanyingSubClassName = transfer.accompanyingSubClassName;

    return hrmSubClass;
  }

  public static fromTransferList(transferList: HrmSubClassModel[]): HrmSubClass[]
  {
    if (!transferList)
    {
      return null;
    }

    return transferList.map(HrmSubClass.fromTransfer)
  }


  public static toTransferList(subClasses: HrmSubClass[]): HRMSubClassTransferInbound[]
  {
    if (subClasses === null)
    {
      return null;
    }

    return subClasses.map(subClass => subClass.toTransfer());
  }

  public toTransfer(): HRMSubClassTransferInbound
  {
    return {
      facilityNumber: this.facilityNumber,
      className: this.reportClass,
      subClassName: this.subClassName,
      accompanyingSubClassName: this.accompanyingSubClassName,
    }
  }

  public constructor()
  {
    this._id = null;
    this._hrmCategoryId = null;
    this._hrmCategoryId = null;
    this._facilityNumber = null;
    this._reportClass = null;
    this._subClassName = null;
    this._accompanyingSubClassName = null;
  }

  get id(): number
  {
    return this._id;
  }

  get hrmCategoryId(): number {
    return this._hrmCategoryId;
  }

  set hrmCategoryId(value: number) {
    this._hrmCategoryId = value;
  }

  get facilityNumber(): string {
    return this._facilityNumber;
  }

  set facilityNumber(value: string) {
    this._facilityNumber = value;
  }

  get reportClass(): HrmReportClass {
    return this._reportClass;
  }

  set reportClass(value: HrmReportClass) {
    this._reportClass = value;
  }

  get subClassName(): string {
    return this._subClassName;
  }

  set subClassName(value: string) {
    this._subClassName = value;
  }

  get accompanyingSubClassName(): string {
    return this._accompanyingSubClassName;
  }

  set accompanyingSubClassName(value: string) {
    this._accompanyingSubClassName = value;
  }
}