import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
export class JwtResponse {
    accessToken: string;
    tokenType: string;
    displayName: string;
    authorities: string[];
    networkAddress: string;
}

export class Token {
    accessToken: string;
    tokenType: string;
 }

export class User {
    username: string;
    email: string;
    password: string;
    fullName: string;
    address: string;
    telephone: string;
}

export class AuthLoginInfo {
    username: string;
    password: string;

    constructor(username: string, password: string) {
        this.username = username;
        this.password = password;
    }
}

export class Query {
    startDate: NgbDateStruct;
    endDate: NgbDateStruct;
    location: string;
}

export class QueryDTO {
    startDate: number;
    endDate: number;
    location: string;
}
