import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
export class Contract {
    agencyId: number;
    acoomodationId: number;

    startDate: number;
    endDate: number;

    hotels = new Array<number>();

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
}
