import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
export class Contract {
    agId = 0;
    accId = 0;

    agName: string;
    accName: string;

    accomodationRepr: number;
    agencyRepr: number;

    startDate: number;
    endDate: number;

    hotels = new Array<number>();
    hotelNames = new Array<string>();

    prices = new Array<number>(4);
    someContrains = new Array<number>(6);
    roomsInfo: number[];

    periods = new Array<number>(2);
    clause = false;

    advancePayment: number;
    commision: number;
    finePerBed: number;

    courtName = '';
    courtLocation = '';

    id: number;

    agreementDate: number;
    balance: number;
    restricted: boolean;
    availablebalance: number;
}
