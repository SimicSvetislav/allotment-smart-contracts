import { RoomsInfoKids } from './RoomsInfo';

export class Reservation {
    startDate: number;
    endDate: number;
    priceType = 0;
    roomsInfo: Array<RoomsInfoKids> = new Array<RoomsInfoKids>();
}

export class SingleReservation {
    from: number;
    to: number;
    priceType = 0;
    noRooms: number;
    beds: number;
    kids: number;
}

export class ReservationDTO {
    id: number;
    from: number;
    to: number;
    price: number;
    priceType: number;
    provision: boolean;
    mainSeason: false;
    kids: number;

    beds: number;
    rooms: number
}