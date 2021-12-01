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

  get hrmCategoryId(): number
  {
    return this._hrmCategoryId;
  }

  set hrmCategoryId(value: number)
  {
    this._hrmCategoryId = value;
  }

  get facilityNumber(): string
  {
    return this._facilityNumber;
  }

  set facilityNumber(value: string)
  {
    this._facilityNumber = value;
  }

  get reportClass(): HrmReportClass
  {
    return this._reportClass;
  }

  set reportClass(value: HrmReportClass)
  {
    this._reportClass = value;
  }

  get subClassName(): string
  {
    return this._subClassName;
  }

  set subClassName(value: string)
  {
    this._subClassName = value;
  }

  get accompanyingSubClassName(): string
  {
    return this._accompanyingSubClassName;
  }

  set accompanyingSubClassName(value: string)
  {
    this._accompanyingSubClassName = value;
  }

  // Facility number is filled out and either subclass or accompanying subclass depending on the reportClass
  public isCompleteMapping(): boolean {
    if (!this.reportClass || !this.facilityNumber)
    {
      return false;
    }

    if (this.reportClass === HrmReportClass.MEDICAL_RECORDS)
    {
      return !!this.subClassName;
    }
    else
    {
      return !!this.accompanyingSubClassName;
    }
  }

  // Two subclasses are equal according to the following conditions:
  // All cases:  facilityNumber and reportClass match, AND
  // -if reportClass is Medical Records -> match subClass
  // -if reportClass is Diagnostic Imaging or CardioRespiratory -> match accompanyingSubClass
  public equals(other: HrmSubClass): boolean
  {
    const facilityNumberMatch = this.facilityNumber == other.facilityNumber;
    const reportClassMatch = this.reportClass === other.reportClass;

    if (!facilityNumberMatch || !reportClassMatch)
    {
      return false;
    }

    if (this.reportClass === HrmReportClass.MEDICAL_RECORDS)
    {
      return this.subClassName === other.subClassName;
    }
    else
    {
      return this.accompanyingSubClassName === other.accompanyingSubClassName;
    }
  }
}