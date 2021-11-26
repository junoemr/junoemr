import {HrmSubClassModel} from "../../../../../generated";

export default class HrmSubClass
{
  private _id: number
  private _facilityNumber: string;
  private _className: string;
  private _subClassName: string;
  private _accompanyingSubClassName: string;

  public static fromTransfer(transfer: HrmSubClassModel): HrmSubClass
  {
    const hrmSubClass = new HrmSubClass();
    hrmSubClass._facilityNumber = transfer.facilityNumber;
    hrmSubClass._className = transfer.className;
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

  public constructor()
  {
    this._id = null;
    this._facilityNumber = null;
    this._className = null;
    this._subClassName = null;
    this._accompanyingSubClassName = null;
  }

  get id(): number
  {
    return this._id;
  }

  get className(): string {
    return this._className;
  }

  set className(value: string) {
    this._className = value;
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